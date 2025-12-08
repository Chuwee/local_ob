import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import { SeasonTicketsService, SeasonTicketStatus } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CustomerFieldsRestrictions, CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { CountriesService, TicketsBaseService } from '@admin-clients/shared/common/data-access';
import {
    ContextNotificationComponent, EphemeralMessageService, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatSlideToggle, TranslatePipe, MatProgressSpinner, AsyncPipe, MatTooltip,
        ContextNotificationComponent, MatIcon, MatError, MatFormField, MatInput, MatLabel, MatOption, MatSelect, ObFormFieldLabelDirective,
        SelectSearchComponent, FormControlErrorsComponent
    ],
    selector: 'app-customer-season-tickets-auto-renewal',
    templateUrl: './customer-season-ticket-auto-renewal.component.html',
    styleUrl: './customer-season-ticket-auto-renewal.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerSeasonTicketAutoRenewalComponent implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #customersSrv = inject(CustomersService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #ticketSrv = inject(TicketsBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly $ticket = toSignal(this.#ticketSrv.ticketDetail.get$().pipe(filter(Boolean)));
    readonly $isSeasonTicketSetUp = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$()
        .pipe(
            filter(Boolean),
            map(seasonTicketStatus => seasonTicketStatus.status === SeasonTicketStatus.setUp)
        ));

    readonly #$customer = toSignal(this.#customersSrv.customer.get$().pipe(first(Boolean)));
    readonly $isCustomerManaged = computed(() => this.#$customer()?.is_managed);
    readonly $customerManagers = toSignal(this.#customersSrv.customerFriendOfList.getManagersLinksList$());
    readonly $hasManagedFriends = toSignal(this.#customersSrv.customerFriendsList.getData$()
        .pipe(
            filter(Boolean),
            map(friends => {
                if (!friends) return false;
                return friends.some(friend => friend.relation === 'MANAGER');
            }))
    );

    readonly $sepaFields = toSignal(this.#seasonTicketSrv.seasonTicketForms.get$()
        .pipe(filter(Boolean), map(form => form?.flat().filter(field => field.visible))));

    readonly $sepaFieldNames = computed(() => this.$sepaFields()?.map(field => field.key) || []);

    readonly $hasMissingMandatoryFields = computed(() => {
        if (this.$seasonTicket()?.settings.operative.renewal?.renewal_type !== 'XML_SEPA' || this.$savedConfig()) {
            return false;
        }
        const sepaFields = this.$sepaFields();
        const ticket = this.$ticket();
        const renewalDetails = ticket?.renewal_details || { auto_renewal: false, field: {} };

        const mandatoryFields = sepaFields?.filter(field => field.mandatory) || [];
        return mandatoryFields.some(field => {
            const value = renewalDetails.field?.[field.key];
            return !value || value.trim() === '';
        });
    });

    readonly countries$ = this.#countriesSrv.getCountries$().pipe(filter(Boolean));
    readonly $importTooltip = computed(() => this.$hasMissingMandatoryFields() ?
        'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL.STATUS.TICKET_DATA_NOT_COMPLETED_TOOLTIP' : (this.$isSeasonTicketSetUp() ? '' :
            'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL.STATUS.SEASON_TICKET_NOT_SET_UP_TOOLTIP'));

    readonly $savedConfig = signal(false);

    readonly form = this.#fb.group({
        enabled: [false],
        sepaForm: this.#fb.group({
            address: this.#fb.control({
                value: '',
                disabled: true
            }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
            city: this.#fb.control({
                value: '',
                disabled: true
            }, Validators.maxLength(CustomerFieldsRestrictions.customerCityMaxLength)),
            country: this.#fb.control({ value: '', disabled: true }),
            postal_code: this.#fb.control({
                value: '',
                disabled: true
            }, Validators.maxLength(CustomerFieldsRestrictions.customerPostalCodeMaxLength)),
            name: this.#fb.control({
                value: '',
                disabled: true
            }, [Validators.maxLength(CustomerFieldsRestrictions.customerNameMaxLength)]),
            iban: this.#fb.control({
                value: '',
                disabled: true
            }),
            bic: this.#fb.control({
                value: '',
                disabled: true
            })
        })
    });

    readonly loading$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketForms.inProgress$(),
        this.#customersSrv.customer.loading$(),
        this.#customersSrv.customerFriendOfList.loading$(),
        this.#customersSrv.customerFriendsList.loading$(),
        this.#countriesSrv.isCountriesLoading$(),
        this.#ticketSrv.ticketDetail.loading$(),
        this.#ticketSrv.ticketDetail.renewalDetails.loading$()
    ]);

    constructor() {
        effect(() => {
            const seasonTicket = this.$seasonTicket();
            if (!seasonTicket) return;

            const renewalConfig = seasonTicket.settings.operative.renewal;
            if (!renewalConfig?.automatic) {
                this.#router.navigate(['../general-data'], {
                    relativeTo: this.#route,
                    queryParams: this.#route.snapshot.queryParams
                });
                return;
            }

            if (renewalConfig?.renewal_type === 'XML_SEPA') {
                this.#seasonTicketSrv.seasonTicketForms.load(seasonTicket.id.toString(), 'sepa');
            } else {
                this.#seasonTicketSrv.seasonTicketForms.clear();
            }
        });

        effect(() => {
            if (!this.$isSeasonTicketSetUp() || this.$hasMissingMandatoryFields()) {
                this.form.controls.enabled.disable({ emitEvent: false });
            } else {
                this.form.controls.enabled.enable({ emitEvent: false });
            }
        });

        effect(() => {
            const seasonTicket = this.$seasonTicket();
            const sepaFields = this.$sepaFields();
            const ticket = this.$ticket();
            const renewalDetails = ticket?.renewal_details || { auto_renewal: false, field: {} };

            this.form.controls.enabled.reset(renewalDetails.auto_renewal || false, { emitEvent: false });
            this.$savedConfig.set(false);

            if (seasonTicket?.settings.operative.renewal?.renewal_type === 'XML_SEPA' && sepaFields?.length > 0) {
                this.#configureSepaForm(renewalDetails.field || {}, sepaFields);
            }
        });
    }

    ngOnInit(): void {
        if (this.$isCustomerManaged()) {
            this.#customersSrv.customerFriendOfList.load(this.#$customer().id);
        } else {
            this.#customersSrv.customerFriendsList.load(this.#$customer().id);
        }
    }

    handleStatusChange(): void {
        const ticket = this.$ticket();
        const automatic = this.form.controls.enabled.value;
        this.#ticketSrv.ticketDetail.renewalDetails.update(ticket?.order.code, ticket?.id.toString(), { auto_renewal: automatic })
            .subscribe({
                next: () => {
                    if (automatic) {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL.STATUS.ENABLED_SUCCESS' });
                    } else {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL.STATUS.DISABLED_SUCCESS' });
                    }
                }
            });
    }

    handleManagerClick(event: MouseEvent): void {
        const target = event.target as HTMLElement;
        if (target.tagName.toLowerCase() === 'a') {
            event.preventDefault();
            this.goToCustomer(target.classList[1]);
        }
    }

    goToCustomer(friendId: string): void {
        this.#router.navigate(['/customers', friendId], { queryParams: { entityId: this.#$customer().entity.id } });
    }

    cancel(): void {
        const ticket = this.$ticket();
        this.#ticketSrv.ticketDetail.load(ticket?.order.code, ticket?.id.toString());
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    save(): void {
        if (this.form.controls.sepaForm.valid) {
            const ticket = this.$ticket();
            const field = Object.fromEntries(
                Object.entries(this.form.controls.sepaForm.value).filter(([_, v]) => !!v)
            ) as Record<string, string>;
            this.#ticketSrv.ticketDetail.renewalDetails.update(ticket?.order.code, ticket?.id.toString(), { field })
                .subscribe({
                    next: () => {
                        this.form.controls.sepaForm.markAsPristine();
                        this.$savedConfig.set(true);
                        this.#ephemeralSrv.showSuccess({ msgKey: 'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL.UPDATED_SUCCESSFULLY' });
                    }
                });
        } else {
            this.form.controls.sepaForm.markAllAsTouched();
            this.form.controls.sepaForm.patchValue(this.form.controls.sepaForm.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, undefined, undefined, 'start');
        }
    }

    #configureSepaForm(sepaData: Record<string, string>, fields: FormsField[]): void {
        const fieldNames = fields.map(field => field.key);
        const requiredFields = fields.filter(field => field.mandatory).map(field => field.key);

        if (fieldNames.length === 0) return;

        if (fieldNames.includes('country')) {
            this.#countriesSrv.loadCountries();
        }

        this.#fieldsHandler(fields, fieldNames, this.form.controls.sepaForm.controls, requiredFields);
        this.#updateSepaFormData(sepaData, fieldNames);
    }

    #fieldsHandler(fields: FormsField[], fieldNames: string[], ctrls: Record<string, any>, requiredFields: string[] = []): void {
        const names = [...fieldNames, ...requiredFields];
        Object.entries(ctrls).forEach(([key, control]) => {
            if (control.controls) {
                this.#fieldsHandler(fields, fieldNames, control.controls, requiredFields);
            } else {
                const fieldIndex = names.findIndex(name => name === key);
                this.#handleControl(fields, names, fieldIndex, control, requiredFields);
            }
        });
    }

    #handleControl(fields: FormsField[], fieldNames: string[], index: number, ctrl: FormControl, requiredFields: string[] = []): void {
        const controlKey = fieldNames[index];
        const field = fields.find(f => f.key === controlKey);

        if (index >= 0 && field?.visible) {
            ctrl.enable({ emitEvent: false });
        } else {
            ctrl.disable({ emitEvent: false });
        }

        if (field?.mandatory || requiredFields.includes(controlKey)) {
            ctrl.addValidators(Validators.required);
        } else {
            ctrl.removeValidators(Validators.required);
        }

        if (field?.applied_rules) {
            const regexRule = field.applied_rules.find(formRule => formRule.rule === 'REGEX');
            if (!!regexRule) {
                ctrl.addValidators(Validators.pattern(regexRule?.value));
            }
        }
    }

    #updateSepaFormData(sepaData: Record<string, string>, fieldNames: string[]): void {
        if (sepaData) {
            this.form.controls.sepaForm.reset(sepaData);
            this.form.controls.sepaForm.markAsPristine();
        } else {
            const emptyFields = fieldNames.reduce<Record<string, string>>((acc, key) => {
                acc[key] = null;
                return acc;
            }, {});
            this.form.controls.sepaForm.reset(emptyFields);
        }
    }
}

import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { FormsComponent } from '@admin-clients/cpanel/common/feature/forms';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import { EntitiesService, entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    SeasonTicketGenerationStatus, SeasonTicketsService, type SeasonTicket, type PutSeasonTicket
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';

export const renewalTypes = {
    xml_sepa: 'XML_SEPA',
    csv_import: 'CSV_IMPORT'
} as const;
export type RenewalType = typeof renewalTypes[keyof typeof renewalTypes];

@Component({
    selector: 'app-season-ticket-renewals-config',
    templateUrl: './season-ticket-renewals-config.component.html',
    styleUrls: ['./season-ticket-renewals-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, TranslatePipe, ReactiveFormsModule, MatProgressSpinner, MatExpansionPanel, MatOption, FormsComponent,
        MatExpansionPanelHeader, MatExpansionPanelTitle, MatIcon, MatLabel, MatCheckbox, MatButton, MatFormField, MatSelect, MatAccordion,
        MatDivider, MatRadioGroup, MatRadioButton, FormControlErrorsComponent, MatError, ErrorMessage$Pipe, AsyncPipe
    ],
    providers: [entitiesProviders]
})
export class SeasonTicketRenewalsConfigComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #entitySrv = inject(EntitiesService);
    readonly #entityBaseSrv = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly renewalTypes = Object.values(renewalTypes);
    readonly $bankAccounts = toSignal(this.#entitySrv.entityBankAccountList.get$().pipe(filter(Boolean)));
    readonly $isLoadingOrSaving = toSignal(booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketForms.inProgress$()
    ]));

    readonly $isFriendsAndFamilyEnabled = toSignal(this.#entityBaseSrv.getEntity$().pipe(
        first(Boolean),
        map(entity => entity.settings?.allow_friends)
    ));

    readonly form = this.#fb.group({
        allow_renewal: null as boolean,
        renewal_type: [null as RenewalType | null],
        automatic_mandatory: [null as boolean],
        automaticRenewalsForm: this.#fb.group({}),
        bank_account_id: [null as number],
        group_by_reference: null as boolean
    });

    readonly seasonTicketForms$ = this.#seasonTicketSrv.seasonTicket.get$().pipe(
        filter(Boolean),
        switchMap(seasonTicket => {
            this.#seasonTicketSrv.seasonTicketForms.load(seasonTicket.id.toString(), 'sepa');
            return this.#seasonTicketSrv.seasonTicketForms.get$();
        }),
        shareReplay(1)
    );

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(
        filter(Boolean),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    ));

    get isActivateButtonDisabled(): boolean {
        const controls = this.form.controls;
        const type = controls.renewal_type.value;
        if (!type) {
            return true;
        }
        const isAutomaticMandatoryValid = controls.automatic_mandatory.valid;
        if (type === renewalTypes.xml_sepa) {
            return !controls.automaticRenewalsForm.valid || !controls.bank_account_id.valid || !isAutomaticMandatoryValid;
        }
        return !isAutomaticMandatoryValid;
    }

    constructor() {
        this.#handleRenewalTypeChanges();
    }

    ngOnInit(): void {
        this.#entitySrv.entityBankAccountList.load(this.$seasonTicket()?.entity.id);

        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(() => {
                const renewalData = this.$seasonTicket()?.settings?.operative?.renewal;
                this.form.reset({
                    allow_renewal: this.$seasonTicket()?.settings?.operative?.allow_renewal,
                    renewal_type: renewalData?.renewal_type ?? null,
                    bank_account_id: renewalData?.bank_account_id,
                    automatic_mandatory: renewalData?.automatic_mandatory,
                    group_by_reference: renewalData?.group_by_reference
                });
                this.form.markAsPristine();

                const isRenewalAutomaticActive = this.$seasonTicket()?.settings?.operative?.renewal?.automatic;
                if (isRenewalAutomaticActive) {
                    this.form.controls.renewal_type.disable();
                } else {
                    this.form.controls.renewal_type.enable();
                }

                const renewalType = renewalData?.renewal_type;
                if (this.renewalTypes.includes(renewalType)) {
                    this.form.controls.automatic_mandatory.addValidators(Validators.required);
                } else {
                    this.form.controls.automatic_mandatory.clearValidators();
                }
                this.form.controls.automatic_mandatory.updateValueAndValidity({ emitEvent: false });
            });

        this.#seasonTicketSrv.seasonTicketStatus.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(status => {
                if (status.generation_status !== SeasonTicketGenerationStatus.ready ||
                    this.$seasonTicket()?.settings?.operative?.renewal?.in_process) {
                    this.form.controls.allow_renewal.disable({ emitEvent: false });
                } else if (status.generation_status === SeasonTicketGenerationStatus.ready) {
                    this.form.controls.allow_renewal.enable({ emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this.#seasonTicketSrv.seasonTicketForms.clear();
    }

    cancel(): void {
        this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString());
        this.#seasonTicketSrv.seasonTicketForms.load(this.$seasonTicket().id.toString(), 'sepa');
        this.#entitySrv.entityBankAccountList.load(this.$seasonTicket()?.entity.id);
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const obs$: Observable<void>[] = [];
            const formValue = this.form.value;
            const renewal = {
                ...(formValue.renewal_type && { renewal_type: formValue.renewal_type }),
                ...(formValue.bank_account_id && { bank_account_id: formValue.bank_account_id }),
                ...(formValue.group_by_reference != null && { group_by_reference: formValue.group_by_reference }),
                ...(formValue.automatic_mandatory != null && { automatic_mandatory: formValue.automatic_mandatory })
            };
            const body: PutSeasonTicket = {
                settings: {
                    operative: {
                        allow_renewal: this.form.value.allow_renewal,
                        ...(Object.keys(renewal).length > 0 && { renewal })
                    }
                }
            };
            obs$.push(this.#seasonTicketSrv.seasonTicket.save(this.$seasonTicket().id.toString(), body));

            if (this.form.controls.automaticRenewalsForm.dirty) {
                obs$.push(this.#saveForms$(this.$seasonTicket()));
            }

            return forkJoin(obs$).pipe(
                tap(() => {
                    this.#ephemeralSrv.showSaveSuccess();
                    this.form.markAsPristine();
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString()));
    }

    enableAutomaticRenewal(): void {
        if (this.form.dirty) {
            this.#msgDialogSrv.showWarn({
                size: DialogSize.MEDIUM,
                title: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.UNSAVED_CHANGES.TITLE',
                message: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.UNSAVED_CHANGES.DESCRIPTION',
                actionLabel: 'FORMS.ACTIONS.UPDATE',
                showCancelButton: true
            }).pipe(
                switchMap(saveAccepted => {
                    if (saveAccepted) {
                        return this.save$();
                    } else {
                        return of(false);
                    }
                }),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();
            return;
        }
        this.#msgDialogSrv.showWarn({
            size: DialogSize.MEDIUM,
            title: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.ENABLE_DIALOG_TITLE',
            message: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.ENABLE_DIALOG_MESSAGE',
            actionLabel: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.ENABLE_DIALOG_BUTTON',
            showCancelButton: true
        }).pipe(
            filter(Boolean),
            switchMap(() => this.#seasonTicketSrv.seasonTicket.save(this.$seasonTicket().id.toString(), {
                settings: {
                    operative: {
                        renewal: { automatic: true }
                    }
                }
            })),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(() => {
            this.#ephemeralSrv.showSuccess({ msgKey: 'SEASON_TICKET.GENERAL_DATA.AUTOMATIC_RENEWAL.ENABLE_SUCCESS' });
            this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString());
        });
    }

    #handleRenewalTypeChanges(): void {
        this.form.controls.renewal_type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                if (type === renewalTypes.xml_sepa) {
                    this.form.controls.group_by_reference.disable({ emitEvent: false });
                    this.form.controls.bank_account_id.enable({ emitEvent: false });
                    this.form.controls.bank_account_id.addValidators(Validators.required);
                    this.form.controls.automatic_mandatory.addValidators(Validators.required);
                } else if (type === renewalTypes.csv_import) {
                    this.form.controls.group_by_reference.enable({ emitEvent: false });
                    this.form.controls.bank_account_id.disable({ emitEvent: false });
                    this.form.controls.bank_account_id.clearValidators();
                    this.form.controls.automatic_mandatory.addValidators(Validators.required);
                } else {
                    this.form.controls.group_by_reference.disable();
                    this.form.controls.bank_account_id.disable();
                    this.form.controls.bank_account_id.clearValidators();
                    this.form.controls.automatic_mandatory.clearValidators();
                }
                this.form.controls.bank_account_id.updateValueAndValidity({ emitEvent: false });
                this.form.controls.automatic_mandatory.updateValueAndValidity({ emitEvent: false });
            });
    }

    #getFormValues(form: FormsField[][], formsValue: FormsField[]): FormsField[][] {
        return form?.map(formField => formField.map(field =>
            formsValue.find(formField => formField.key === field.key)));
    }

    #saveForms$(seasonTicket: SeasonTicket): Observable<void> {
        return this.seasonTicketForms$.pipe(
            first(),
            switchMap(forms => {
                const value = this.form.controls.automaticRenewalsForm.getRawValue()[0];
                const formsData = this.#getFormValues(forms, value);
                return this.#seasonTicketSrv.seasonTicketForms.update(seasonTicket.id.toString(), 'sepa', formsData);
            })
        );
    }
}

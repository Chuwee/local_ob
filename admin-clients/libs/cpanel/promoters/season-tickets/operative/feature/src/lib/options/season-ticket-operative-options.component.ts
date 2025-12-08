import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PutSeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-season-ticket-operative-options',
    templateUrl: './season-ticket-operative-options.component.html',
    styleUrls: ['./season-ticket-operative-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, FormContainerComponent, ReactiveFormsModule, MatInput, MatCheckbox, MatProgressSpinner,
        MatFormFieldModule, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
    ]
})
export class SeasonTicketOperativeOptionsComponent implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]));

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly form = this.#fb.group({
        max_buying_limit: this.#fb.group({
            override: false,
            value: [null as number, Validators.required]
        }),
        purchasePermissionsForm: this.#fb.group({
            register_mandatory: false,
            enable_customer_max_seats: false,
            customer_max_seats: [null as number, [Validators.required, Validators.min(1)]]
        })
    });

    ngOnInit(): void {
        this.#refreshFormDataHandler();
        this.#initFormChangesHandlers();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValue = this.form.getRawValue();
            const maxSeats = formValue.purchasePermissionsForm.enable_customer_max_seats
                ? formValue.purchasePermissionsForm.customer_max_seats : 0;

            const objectToSend: PutSeasonTicket = {
                settings: {
                    operative: {
                        max_buying_limit: formValue.max_buying_limit,
                        register_mandatory: formValue.purchasePermissionsForm.register_mandatory,
                        customer_max_seats: maxSeats
                    }
                }
            };

            return this.#seasonTicketSrv.seasonTicket.save(this.$seasonTicket().id.toString(), objectToSend)
                .pipe(tap(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#reloadSeasonTicket();
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#reloadSeasonTicket();
    }

    #reloadSeasonTicket(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first(Boolean))
            .subscribe(st => {
                this.#seasonTicketSrv.seasonTicket.load(st.id.toString());
                this.form.markAsPristine();
                this.#initControlsState();
            });
    }

    #refreshFormDataHandler(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                filter(seasonTicket => !!(seasonTicket.settings?.operative)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(seasonTicket => {
                const operative = seasonTicket.settings.operative;
                this.form.patchValue({
                    max_buying_limit: {
                        override: !!operative.max_buying_limit,
                        value: operative.max_buying_limit?.value || 1
                    },
                    purchasePermissionsForm: {
                        register_mandatory: operative.register_mandatory,
                        enable_customer_max_seats: operative.customer_max_seats > 0,
                        customer_max_seats: operative.customer_max_seats || 1
                    }
                }, { emitEvent: false });
                this.#initControlsState();
            });
    }

    #initFormChangesHandlers(): void {
        this.form.controls.max_buying_limit.controls.override.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                const valueControl = this.form.controls.max_buying_limit.controls.value;
                this.#manageControlState(isEnabled, valueControl);
            });

        this.form.controls.purchasePermissionsForm.controls.register_mandatory.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                const enableMaxSeatsControl = this.form.controls.purchasePermissionsForm.controls.enable_customer_max_seats;
                this.#manageControlState(isEnabled, enableMaxSeatsControl);
            });

        this.form.controls.purchasePermissionsForm.controls.enable_customer_max_seats.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                const customerMaxSeatsControl = this.form.controls.purchasePermissionsForm.controls.customer_max_seats;
                this.#manageControlState(isEnabled, customerMaxSeatsControl, [Validators.min(1)]);
                if (isEnabled && (!customerMaxSeatsControl.value || customerMaxSeatsControl.value < 1)) {
                    customerMaxSeatsControl.setValue(1);
                }
            });
    }

    #initControlsState(): void {
        const isValueEnabled = this.form.controls.max_buying_limit.controls.override.value;
        const valueControl = this.form.controls.max_buying_limit.controls.value;
        this.#manageControlState(isValueEnabled, valueControl);

        const isRegisterMandatoryEnabled = this.form.controls.purchasePermissionsForm.controls.register_mandatory.value;
        const enableCustomerMaxSeatsControl = this.form.controls.purchasePermissionsForm.controls.enable_customer_max_seats;
        this.#manageControlState(isRegisterMandatoryEnabled, enableCustomerMaxSeatsControl);

        const isMaxSeatsEnabled = this.form.controls.purchasePermissionsForm.controls.enable_customer_max_seats.value;
        const maxSeatsControl = this.form.controls.purchasePermissionsForm.controls.customer_max_seats;
        this.#manageControlState(isMaxSeatsEnabled, maxSeatsControl, [Validators.min(1)]);
    }

    #manageControlState(enableControl: boolean, valueControl: AbstractControl, validators: ValidatorFn[] = []): void {
        const allValidators = [...validators, Validators.required];
        if (enableControl) {
            valueControl.enable({ emitEvent: false });
            valueControl.setValidators(allValidators);
        } else {
            valueControl.disable({ emitEvent: false });
            valueControl.clearValidators();
        }
        valueControl.updateValueAndValidity({ emitEvent: false });
    }
}
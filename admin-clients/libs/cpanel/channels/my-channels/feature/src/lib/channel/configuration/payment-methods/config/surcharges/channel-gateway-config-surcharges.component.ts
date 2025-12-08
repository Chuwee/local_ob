import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelGatewayConfig, PaymentMethodSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { CurrencyInputComponent, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnDestroy, OnInit
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
    AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import {
    MatCell, MatHeaderRow, MatRow, MatRowDef, MatHeaderRowDef, MatHeaderCellDef,
    MatHeaderCell, MatCellDef, MatTable, MatColumnDef
} from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map } from 'rxjs';
import { ChannelGatewayCtrlType } from '../channel-gateway-config-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, MatRadioModule, MatDialogContent, MatTable, MatColumnDef,
        MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, CurrencyInputComponent, LocalCurrencyPipe, AsyncPipe,
        MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatFormField, MatIcon, ErrorMessage$Pipe, ErrorIconDirective, MatLabel,
        CurrencyInputComponent, LocalNumberPipe, PercentageInputComponent, MatSelect, MatOption, MatError, FormControlErrorsComponent,
        NgTemplateOutlet
    ],
    selector: 'app-channel-gateway-config-surcharges',
    templateUrl: './channel-gateway-config-surcharges.component.html'
})
export class ChannelGatewayConfigSurchargesComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesService = inject(EntitiesBaseService);

    readonly $channelGatewayConfig = input.required<ChannelGatewayConfig>({ alias: 'channelGatewayConfig' });
    readonly $gatewayRequestCtrl = input.required<FormControl<ChannelGatewayCtrlType>>({ alias: 'gatewayRequestCtrl' });
    readonly $form = input.required<FormGroup>({ alias: 'form' });
    readonly $requestCurrencies = input.required<string[]>({ alias: 'requestCurrencies' });
    readonly $currencies = computed(() => this.$requestCurrencies() || this.$channelGatewayConfig()?.currencies?.map(c => c.code));

    readonly $surcharges = computed(() => this.$channelGatewayConfig()?.surcharges || []);

    readonly surchargesForm = this.#fb.group({
        surchargeType: ['NONE' as PaymentMethodSurchargeType],
        surchargesFixedValues: this.#fb.nonNullable.record<number>({}),
        surchargesPercentValues: this.#fb.nonNullable.record<number>({}),
        surchargesPercentMinValues: this.#fb.record<number>({}),
        surchargesPercentMaxValues: this.#fb.record<number>({}),
        surchargesTaxesFixed: this.#fb.control<number[]>([], this.#taxesValidator()),
        surchargesTaxesPercent: this.#fb.control<number[]>([], this.#taxesValidator())
    }, { validators: this.#surchargeFormValidator() });

    readonly fixedSurchargesDisplayedColumns = ['currency', 'surcharge'];
    readonly percentSurchargesDisplayedColumns = [...this.fixedSurchargesDisplayedColumns, 'min', 'max'];

    readonly #entityId$ = this.#entitiesService.getEntity$().pipe(filter(Boolean), map(e => e.id));
    readonly $taxes = toSignal(this.#entitiesService.getEntityTaxes$().pipe(filter(Boolean)));

    ngOnInit(): void {
        this.#loadTaxesOptions();
        this.$form().addControl('surcharges', this.surchargesForm, { emitEvent: false });
        this.#initialConfig();
        this.#mapToGatewayRequest();
    }

    ngOnDestroy(): void {
        this.$form().removeControl('surcharges', { emitEvent: false });
    }

    #initialConfig(): void {
        const { request } = this.$gatewayRequestCtrl().value;
        const surchargeType = this.$surcharges()?.[0]?.type || null;
        this.#initCtrl(this.surchargesForm.controls.surchargeType, request.surcharges?.[0].type, surchargeType);

        const recordFixedValues = this.#fb.nonNullable.record<number>(this.$currencies()?.reduce((acc, currency) => {
            const requestValue = (request.surcharges?.[0]?.type === 'FIXED' ?
                request.surcharges.find(surcharge => surcharge.currency === currency)?.value
                : null as number);
            const serverValue = (this.$surcharges()?.[0]?.type === 'FIXED' ?
                this.$surcharges().find(surcharge => surcharge.currency === currency)?.value
                : null as number);
            acc[currency] = [requestValue ?? serverValue, [Validators.required, Validators.min(0)]];
            return acc;
        }, {}));

        this.surchargesForm.setControl('surchargesFixedValues', recordFixedValues);

        const requestTaxes = request.taxes || [];
        const serverTaxes = this.$channelGatewayConfig()?.taxes || [];

        const transformTaxes = (taxes: (number | { id: number; name?: string })[]): number[] =>
            taxes.map(tax => typeof tax === 'object' && tax.id ? tax.id : tax as number);

        const taxesToUse = requestTaxes.length > 0 ? requestTaxes : serverTaxes;
        const currentSurchargeType = this.surchargesForm.controls.surchargeType.value;
        if (currentSurchargeType === 'FIXED') {
            this.surchargesForm.controls.surchargesTaxesFixed.setValue(transformTaxes(taxesToUse));
        } else if (currentSurchargeType === 'PERCENT') {
            this.surchargesForm.controls.surchargesTaxesPercent.setValue(transformTaxes(taxesToUse));
        }

        const recordPercentValues = this.#fb.nonNullable.record<number>(this.$currencies().reduce((acc, currency) => {
            const requestValue = (request.surcharges?.[0]?.type === 'PERCENT' ?
                request.surcharges.find(surcharge => surcharge.currency === currency)?.value
                : null as number);
            const serverValue = (this.$surcharges()?.[0]?.type === 'PERCENT' ?
                this.$surcharges().find(surcharge => surcharge.currency === currency)?.value
                : null as number);
            acc[currency] = [requestValue ?? serverValue, [Validators.required, Validators.min(0)]];
            return acc;
        }, {}));

        this.surchargesForm.setControl('surchargesPercentValues', recordPercentValues);

        const recordPercentMinValues = this.#fb.record<number>(this.$currencies().reduce((acc, currency) => {
            const requestValue = (request.surcharges?.[0]?.type === 'PERCENT' ?
                request.surcharges.find(surcharge => surcharge.currency === currency)?.min_value
                : null as number);
            const serverValue = (this.$surcharges()?.[0]?.type === 'PERCENT' ?
                this.$surcharges().find(surcharge => surcharge.currency === currency)?.min_value
                : null as number);
            acc[currency] = [
                requestValue ?? serverValue, [Validators.min(0), this.#minMaxValueValidator(currency, 'min')]
            ];
            return acc;
        }, {}));

        this.surchargesForm.setControl('surchargesPercentMinValues', recordPercentMinValues);

        const recordPercentMaxValues = this.#fb.record<number>(this.$currencies().reduce((acc, currency) => {
            const requestValue = (request.surcharges?.[0]?.type === 'PERCENT' ?
                request.surcharges.find(surcharge => surcharge.currency === currency)?.max_value
                : null as number);
            const serverValue = (this.$surcharges()?.[0]?.type === 'PERCENT' ?
                this.$surcharges().find(surcharge => surcharge.currency === currency)?.max_value
                : null as number);
            acc[currency] = [
                requestValue ?? serverValue, [Validators.min(0), this.#minMaxValueValidator(currency, 'max')]
            ];
            return acc;
        }, {}));

        this.surchargesForm.setControl('surchargesPercentMaxValues', recordPercentMaxValues);

        this.#updateSurchargeControlsState(this.surchargesForm.controls.surchargeType.value);

        this.surchargesForm.controls.surchargeType.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                this.#updateSurchargeControlsState(value);
                this.#restoreFormRecords();
            });
    }

    #initCtrl(ctrl: AbstractControl, previousValue: unknown, serverValue: unknown): void {
        ctrl.reset(previousValue || serverValue || 'NONE' as PaymentMethodSurchargeType, { emitEvent: false });
        if (previousValue) {
            ctrl.markAsDirty();
        }
    }

    #minMaxValueValidator(currency: string, type: 'min' | 'max'): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) {
                return null;
            }

            const oppositeControl = this.surchargesForm?.get(
                type === 'min' ? 'surchargesPercentMaxValues' : 'surchargesPercentMinValues'
            )?.get(currency);

            if (
                oppositeControl?.value && (type === 'min' ?
                    (control.value >= oppositeControl.value) :
                    (control.value <= oppositeControl.value))
            ) {
                return { maxLessThanMin: true };
            }

            if (control.touched && oppositeControl?.hasError('maxLessThanMin')) {
                oppositeControl.reset(oppositeControl.value, { emitEvent: false });
            }

            return null;
        };
    }

    #updateSurchargeControlsState(value: PaymentMethodSurchargeType): void {
        const allControls = [
            'surchargesFixedValues',
            'surchargesPercentValues',
            'surchargesPercentMinValues',
            'surchargesPercentMaxValues',
            'surchargesTaxesFixed',
            'surchargesTaxesPercent'
        ] as const;

        let enabledControls: typeof allControls[number][] = [];

        if (value === 'PERCENT') {
            enabledControls = [
                'surchargesPercentMinValues',
                'surchargesPercentMaxValues',
                'surchargesPercentValues',
                'surchargesTaxesPercent'
            ];
        } else if (value === 'FIXED') {
            enabledControls = ['surchargesFixedValues', 'surchargesTaxesFixed'];
        }

        allControls.forEach(controlName => {
            const control = this.surchargesForm.controls[controlName];
            if (enabledControls.includes(controlName)) {
                control.enable();
            } else {
                control.disable();
                control.markAsUntouched();
            }
        });
    }

    #restoreFormRecords(): void {
        const formRecords = [
            'surchargesFixedValues',
            'surchargesPercentValues',
            'surchargesPercentMinValues',
            'surchargesPercentMaxValues'
        ] as const;

        formRecords.forEach(recordName => {
            const nullValues = Object.keys(this.surchargesForm.controls[recordName].controls)
                .reduce((acc, key) => ({ ...acc, [key]: null }), {});
            this.surchargesForm.controls[recordName].patchValue(nullValues);
        });

        this.surchargesForm.controls.surchargesTaxesFixed.setValue([]);
        this.surchargesForm.controls.surchargesTaxesPercent.setValue([]);
    }

    #mapToGatewayRequest(): void {
        this.$gatewayRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(({ request }) => {
                if (this.$form().invalid) {
                    return;
                }

                const {
                    surchargeType,
                    surchargesFixedValues,
                    surchargesPercentValues,
                    surchargesPercentMinValues,
                    surchargesPercentMaxValues,
                    surchargesTaxesPercent,
                    surchargesTaxesFixed
                } = this.surchargesForm.controls;

                if (this.surchargesForm.dirty) {
                    const taxesControl = surchargeType.value === 'FIXED' ? surchargesTaxesFixed : surchargesTaxesPercent;
                    const taxesFormatted = Array.isArray(taxesControl.value)
                        ? taxesControl.value.map(taxId => ({ id: taxId }))
                        : [];

                    request.surcharges = this.$currencies().map(
                        currency => ({
                            type: surchargeType.value,
                            currency,
                            value:
                                surchargeType.value === 'FIXED' ?
                                    surchargesFixedValues.value[currency] :
                                    surchargesPercentValues.value[currency],
                            ...(surchargeType.value === 'PERCENT' ? {
                                min_value: surchargesPercentMinValues.value[currency],
                                max_value: surchargesPercentMaxValues.value[currency]
                            } : {})
                        })
                    );
                    request.taxes = taxesFormatted;
                }

                this.$gatewayRequestCtrl().setValue({ request }, { emitEvent: false });
            });
    }

    #taxesValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!this.surchargesForm) {
                return null;
            }

            const surchargeType = this.surchargesForm.controls.surchargeType?.value as PaymentMethodSurchargeType;

            if ((surchargeType === 'PERCENT' || surchargeType === 'FIXED') &&
                (!control.value || !Array.isArray(control.value) || control.value.length === 0)) {
                return { required: true };
            }

            return null;
        };
    }

    #surchargeFormValidator(): ValidatorFn {
        return (form: AbstractControl): ValidationErrors | null => {
            const type = form.get('surchargeType')?.value as PaymentMethodSurchargeType;
            const fixed = form.get('surchargesFixedValues');
            const percent = form.get('surchargesPercentValues');
            const percentMin = form.get('surchargesPercentMinValues');
            const percentMax = form.get('surchargesPercentMaxValues');
            const taxesFixed = form.get('surchargesTaxesFixed');
            const taxesPercent = form.get('surchargesTaxesPercent');

            if (
                type === 'NONE' ||
                (type === 'FIXED' && fixed?.valid && taxesFixed?.valid) ||
                (type === 'PERCENT' && percent?.valid && percentMin?.valid && percentMax?.valid && taxesPercent?.valid)
            ) {
                return null;
            }

            return { surchargeInvalid: true };
        };
    }

    #loadTaxesOptions(): void {
        this.#entityId$.pipe(first(Boolean)).subscribe(id => this.#entitiesService.loadEntityTaxes(id));
    }
}

import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { forceDatesTimezone, forceToDefaultTimezone } from '@admin-clients/cpanel/common/utils';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionZoneDynamicPriceConditionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    CurrencyInputComponent, DateTimePickerComponent, DialogSize, EphemeralMessageService, ObDialog, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import {
    atLeastOneRequiredInFormGroup, dateIsSameOrAfter, dateTimeValidator, unique, maxDecimalLength, FormControlHandler
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { VmSessionZoneDynamicPrice } from '../vm-session-zone-dynamic-prices.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogTitle, TranslatePipe, MatIcon, MatIconButton, MatDialogContent, MatError, MatFormField, MatLabel,
        ObFormFieldLabelDirective, ReactiveFormsModule, MatInput, MatButton, MatDialogActions, FormControlErrorsComponent,
        MatTable, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, CurrencyInputComponent, LocalCurrencyPipe,
        DateTimePickerComponent, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, AsyncPipe, ErrorMessage$Pipe, MatPrefix,
        ErrorIconDirective, MatCheckbox, TabsMenuComponent, TabDirective
    ],
    selector: 'app-session-zone-dynamic-prices-dialog',
    templateUrl: './session-zone-dynamic-prices-dialog.component.html',
    styleUrl: './session-zone-dynamic-prices-dialog.component.scss'
})
export class SessionZoneDynamicPricesDialogComponent extends ObDialog<SessionZoneDynamicPricesDialogComponent,
    {
        vmZoneDynamicPrices: VmSessionZoneDynamicPrice[]; vmZoneDynamicPriceToEdit?: VmSessionZoneDynamicPrice; activeEditable: boolean;
        capacity: number;
    }, VmSessionZoneDynamicPrice> {
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #translate = inject(TranslateService);
    readonly #fb = inject(FormBuilder);

    private readonly _$translationTabs = viewChild(TabsMenuComponent);

    readonly #$session = toSignal(this.#eventSessionsSrv.session.get$());
    readonly $rates = toSignal(this.#eventSessionsSrv.session.get$().pipe(map(session => session.settings.rates)), { requireSync: true });
    readonly $event = toSignal(this.#eventsSrv.event.get$(), { requireSync: true });
    readonly $languages = computed(() => this.$event()?.settings.languages.selected);
    readonly $defaultLanguageIndex = computed(() =>
        this.$event()?.settings.languages.selected.findIndex(language => language === this.$event()?.settings.languages.default));

    readonly form = this.#fb.nonNullable.group({
        name: [this.data.vmZoneDynamicPriceToEdit?.name ?? null as string, [Validators.required,
        unique(this.data.vmZoneDynamicPrices, (vmZoneDynamicPrice, currentValue) => {
            if (this.data.vmZoneDynamicPriceToEdit?.order === vmZoneDynamicPrice.order) return false;
            return vmZoneDynamicPrice.name === (currentValue as string)?.trim();
        })]],
        translations: this.#fb.nonNullable.group({
            namesCtrl: this.#fb.nonNullable.record<string>({})
        }),
        conditions: this.#fb.nonNullable.group({
            dateEnabled: !!this.data.vmZoneDynamicPriceToEdit?.condition_types.includes('DATE'),
            capacityEnabled: !!this.data.vmZoneDynamicPriceToEdit?.condition_types.includes('CAPACITY')
        }, { validators: [atLeastOneRequiredInFormGroup() as ValidatorFn] }),
        date: this.#fb.group({
            absoluteDate: [
                {
                    value: this.data.vmZoneDynamicPriceToEdit?.valid_date ?
                        forceToDefaultTimezone(this.data.vmZoneDynamicPriceToEdit?.valid_date) : null as string,
                    disabled: !this.data.vmZoneDynamicPriceToEdit?.condition_types.includes('DATE')
                },
                [
                    Validators.required,
                    dateTimeValidator(
                        dateIsSameOrAfter, 'zoneDynamicPriceDateBeforeVenueTemplateNow',
                        forceToDefaultTimezone(moment().tz(this.#$session().venue_template.venue.timezone).format()),
                        this.#translate.instant('EVENTS.SESSION.VENUE_TEMPLATE_DATE_TZ').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsSameOrAfter, 'zoneDynamicPriceDateBeforeSalesStartDate',
                        forceToDefaultTimezone(this.#$session().settings.sale.start_date),
                        this.#translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
                    )
                ]
            ]
        }),
        capacity: this.#fb.nonNullable.group({
            absoluteCapacity: [{
                value: this.data.vmZoneDynamicPriceToEdit?.capacity ?? null as number,
                disabled: !this.data.vmZoneDynamicPriceToEdit?.condition_types.includes('CAPACITY')
            },
            [Validators.required, Validators.min(0),
            Validators.max(this.data.capacity),
            maxDecimalLength(0)]]
        }),
        ratesPrices: this.#fb.nonNullable.record<number>({})
    });

    readonly displayedColumns = ['name', 'price'];

    constructor() {
        super(DialogSize.MEDIUM, true);

        if (!this.data.activeEditable && this.data.vmZoneDynamicPrices.length) {
            const activeVmZoneDynamicPrice = this.data.vmZoneDynamicPrices
                .find(vmZoneDynamicPrice => vmZoneDynamicPrice.status_dynamic_price === 'ACTIVE');

            if (activeVmZoneDynamicPrice && !activeVmZoneDynamicPrice.condition_types.includes('CAPACITY')) {
                this.form.controls.date.controls.absoluteDate.addValidators(dateTimeValidator(
                    dateIsSameOrAfter, 'zoneDynamicPriceDateBeforeActiveZoneDynamicPriceDate',
                    forceToDefaultTimezone(activeVmZoneDynamicPrice.valid_date),
                    this.#translate.instant('EVENTS.SESSION.DYNAMIC_PRICES.FORMS.LABELS.ACTIVE_ZONE_DYNAMIC_PRICE').toLowerCase()
                ));
            }

            if (activeVmZoneDynamicPrice && !activeVmZoneDynamicPrice.condition_types.includes('DATE')) {
                this.form.controls.capacity.controls.absoluteCapacity.addValidators(
                    Validators.min(activeVmZoneDynamicPrice.capacity + 1)
                );
            }
        }

        if (this.data.vmZoneDynamicPriceToEdit) {
            const dynamicPriceLanguages = this.data.vmZoneDynamicPriceToEdit.translations;
            const newTranslationNamesRecord = this.#fb.nonNullable.record<string>(
                this.$languages().reduce((acc, language) => {
                    const foundLanguage = dynamicPriceLanguages
                        .find(dynamicPriceLanguage => dynamicPriceLanguage.language === language);
                    acc[language] = foundLanguage?.value ?? '';
                    return acc;
                }, {}));
            this.form.controls.translations.setControl('namesCtrl', newTranslationNamesRecord);

            const dynamicRatesPrice = this.data.vmZoneDynamicPriceToEdit.dynamic_rates_price;
            const newDynamicRatesPriceRecord = this.#fb.nonNullable.record<number>(
                this.$rates().reduce((acc, rate) => {
                    const foundDynamicRatePrice = dynamicRatesPrice
                        .find(dynamicRatePrice => dynamicRatePrice.id === rate.id);
                    acc[rate.id] = [foundDynamicRatePrice?.price ?? null as number, [Validators.required, Validators.min(0)]];
                    return acc;
                }, {}));
            this.form.setControl('ratesPrices', newDynamicRatesPriceRecord);
        } else {
            const newTranslationNamesRecord = this.#fb.nonNullable.record<string>(
                this.$languages().reduce((acc, language) => {
                    acc[language] = '';
                    return acc;
                }, {}));
            this.form.controls.translations.setControl('namesCtrl', newTranslationNamesRecord);
            this.$languages()?.forEach(language => {
                FormControlHandler.reflectControlValue(
                    this.form.controls.name,
                    this.form.controls.translations.controls.namesCtrl.controls[language]
                ).pipe(takeUntilDestroyed()).subscribe();
            });

            const newDynamicRatesPriceRecord = this.#fb.nonNullable.record<number>(
                this.$rates().reduce((acc, rate) => {
                    acc[rate.id] = [null as number, [Validators.required, Validators.min(0)]];
                    return acc;
                }, {}));
            this.form.setControl('ratesPrices', newDynamicRatesPriceRecord);

            this.form.controls.capacity.disable({ emitEvent: false });
            this.form.controls.date.disable({ emitEvent: false });
        }

        this.form.controls.conditions.controls.dateEnabled.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(dateEnabled => {
                //Emit changes show error messages in mat-error in case date is disabled and afterward enabled
                if (dateEnabled) {
                    this.form.controls.date.enable();
                } else {
                    this.form.controls.date.disable();
                }
            });

        this.form.controls.conditions.controls.capacityEnabled.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(capacityEnabled => {
                //Emit changes show error messages in mat-error in case capacity is disabled and afterward enabled
                if (capacityEnabled) {
                    this.form.controls.capacity.enable();
                } else {
                    this.form.controls.capacity.disable();
                }
            });
    }

    save(): void {
        if (this.form.valid) {
            const conditionTypes: SessionZoneDynamicPriceConditionType[] = [];
            if (this.form.value.conditions.dateEnabled) {
                conditionTypes.push('DATE');
            }
            if (this.form.value.conditions.capacityEnabled) {
                conditionTypes.push('CAPACITY');
            }

            const vmSessionZoneDynamicPrice: VmSessionZoneDynamicPrice = {
                name: this.form.value.name,
                condition_types: conditionTypes,
                order: this.data.vmZoneDynamicPriceToEdit?.order ?? 0,
                dynamic_rates_price: Object.entries(this.form.value.ratesPrices)
                    .map(([key, value]) => {
                        const foundDynamicRatePrice = this.$rates()
                            .find(rate => Number(key) === rate.id);
                        const rate: VmSessionZoneDynamicPrice['dynamic_rates_price'][number] = {
                            name: foundDynamicRatePrice.name,
                            id: foundDynamicRatePrice.id,
                            price: value
                        };
                        return rate;
                    }),
                status_dynamic_price: 'PENDING',
                translations: Object.entries(this.form.value.translations.namesCtrl)
                    .filter(([_, value]) => !!value?.trim())
                    .map(([key, value]) => ({ language: key, value }))
            };

            if (this.form.value.conditions.dateEnabled) {
                vmSessionZoneDynamicPrice.valid_date = this.form.value.date.absoluteDate;
                forceDatesTimezone(vmSessionZoneDynamicPrice, this.#$session().venue_template.venue.timezone, 'valid_date');
            }
            if (this.form.value.conditions.capacityEnabled) {
                vmSessionZoneDynamicPrice.capacity = this.form.value.capacity.absoluteCapacity;
            }

            if (this.data.vmZoneDynamicPriceToEdit) {
                this.#ephemeralMessageSrv.showSuccess(
                    { msgKey: 'EVENTS.SESSION.DYNAMIC_PRICES.FORMS.FEEDBACK.ZONE_DYNAMIC_PRICE_MODIFIED' });
            } else {
                this.#ephemeralMessageSrv.showSuccess(
                    { msgKey: 'EVENTS.SESSION.DYNAMIC_PRICES.FORMS.FEEDBACK.ZONE_DYNAMIC_PRICE_ADDED' });
            }
            this.dialogRef.close(vmSessionZoneDynamicPrice);
        } else {
            this._$translationTabs()?.goToInvalidCtrlTab();
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            throwError(() => new Error('Invalid form'));
        }
    }
}

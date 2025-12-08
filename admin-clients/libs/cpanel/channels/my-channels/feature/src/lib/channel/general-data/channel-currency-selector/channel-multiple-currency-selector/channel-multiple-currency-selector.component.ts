import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { PutChannel } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { LocalCurrencyFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredTrueInFormRecord } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatError } from '@angular/material/form-field';
import { CurrencySelector } from '../channel-currency-selector.model';

@Component({
    selector: 'app-channel-multiple-currency-selector',
    templateUrl: './channel-multiple-currency-selector.component.html',
    styleUrls: ['./channel-multiple-currency-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, FormControlErrorsComponent, LocalCurrencyFullTranslationPipe, MatCheckboxModule, MatError]
})
export class ChannelMultipleCurrencySelectorComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #formGroup = inject(FormGroupDirective, { optional: true });

    available: Currency[];

    readonly currenciesRecord = this.#fb.record<boolean>({}, { validators: atLeastOneRequiredTrueInFormRecord() });
    readonly $putChannelCtrl = input<FormControl<PutChannel>>(null, { alias: 'putChannelCtrl' });
    readonly $description = input<string>(null, { alias: 'description' });

    #selectedCurrencies = [] as Currency[];

    @Input()
    set data(data: CurrencySelector) {
        if (data) {
            this.available = data.available ? data.available : [];
            this.#selectedCurrencies = data.selected ? data.selected : [];
            if (!Object.keys(this.currenciesRecord.controls).length) {
                this.available.forEach(currency =>
                    this.currenciesRecord.addControl(currency.code, this.#fb.control(false), { emitEvent: false })
                );
            }
            this.#selectedCurrencies.forEach(currency =>
                this.currenciesRecord.controls[currency.code]?.setValue(true, { emitEvent: false }));
        }
    }

    ngOnInit(): void {
        this.#formGroup.control.setControl('currencies', this.currenciesRecord, { emitEvent: false });
        this.$putChannelCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannel => {
                if (!this.#formGroup.valid) return;
                if (this.currenciesRecord.dirty) {
                    const selectedCurrencies = Object.keys(this.currenciesRecord.value).filter(currency =>
                        this.currenciesRecord.value[currency]);
                    if (selectedCurrencies.length) {
                        putChannel.currency_codes = selectedCurrencies;
                        this.$putChannelCtrl().setValue(putChannel, { emitEvent: false });
                    }
                }
            });

        this.currenciesRecord.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(currencyControls => {
                Object.keys(currencyControls).forEach(currencyCode => {
                    if (
                        !this.currenciesRecord.value[currencyCode] &&
                        this.#selectedCurrencies.find(c => c.code === currencyCode) &&
                        !this.$putChannelCtrl().value
                    ) {
                        this.#messageDialogService
                            .showWarn({
                                size: DialogSize.SMALL,
                                showCancelButton: true,
                                title: 'CHANNELS.DISABLE_CURRENCY_TITLE',
                                message: 'CHANNELS.DISABLE_CURRENCY_DESCRIPTION',
                                actionLabel: 'FORMS.ACTIONS.DEACTIVATE'
                            })
                            .subscribe(result => {
                                if (!result) {
                                    this.currenciesRecord.controls[currencyCode].setValue(true, { emitEvent: false });
                                } else {
                                    this.#selectedCurrencies = this.#selectedCurrencies.filter(c => c.code !== currencyCode);
                                }
                            });
                    }
                });
            });
    }
}

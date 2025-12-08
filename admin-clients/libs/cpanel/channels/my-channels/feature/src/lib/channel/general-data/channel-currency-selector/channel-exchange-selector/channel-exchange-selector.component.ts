import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Channel, PutChannel } from '@admin-clients/cpanel/channels/data-access';
import { CurrenciesService } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { LocalCurrencyFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-channel-exchange-selector',
    imports: [
        TranslatePipe, MatCheckboxModule, ReactiveFormsModule, MatFormFieldModule,
        LocalCurrencyFullTranslationPipe, MatSelectModule, FormControlErrorsComponent
    ],
    templateUrl: './channel-exchange-selector.component.html',
    styleUrls: ['./channel-exchange-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelExchangeSelectorComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #currenciesService = inject(CurrenciesService);
    readonly #formGroup = inject(FormGroupDirective, { optional: true });

    readonly $currencies = toSignal(this.#currenciesService.currencies.get$());

    readonly currencyExchangeCtrl = this.#fb.group({
        useCurrencyExchange: false,
        currencySelected: [{ value: null as string, disabled: true }, Validators.required]
    });

    readonly $putChannelCtrl = input<FormControl<PutChannel>>(null, { alias: 'putChannelCtrl' });

    #previousDefaultCurrency = null;

    @Input() set data(data: Channel) {
        if (data) {
            this.#previousDefaultCurrency = data?.settings?.currency_default_exchange;
            this.currencyExchangeCtrl.reset({
                useCurrencyExchange: data.settings.use_currency_exchange,
                currencySelected: data.settings.currency_default_exchange
            }, { emitEvent: false });
        }
    }

    ngOnInit(): void {
        this.#currenciesService.currencies.load();
        this.#formGroup.control.setControl('exchange', this.currencyExchangeCtrl, { emitEvent: false });
        if (this.currencyExchangeCtrl.value.useCurrencyExchange) {
            this.currencyExchangeCtrl.controls.currencySelected.enable({ emitEvent: false });
        } else {
            this.currencyExchangeCtrl.controls.currencySelected.disable({ emitEvent: false });
        }
        this.$putChannelCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannel => {
                if (this.#formGroup.invalid) return;
                if (this.currencyExchangeCtrl.dirty) {
                    putChannel.settings = putChannel.settings ?? {};
                    if (this.currencyExchangeCtrl.controls.currencySelected.dirty) {
                        putChannel.settings.currency_default_exchange = this.currencyExchangeCtrl.value.currencySelected;
                    }
                    if (this.currencyExchangeCtrl.controls.useCurrencyExchange.dirty) {
                        putChannel.settings.use_currency_exchange = this.currencyExchangeCtrl.value.useCurrencyExchange;
                    }
                    this.$putChannelCtrl().setValue(putChannel, { emitEvent: false });
                }
            });

        this.currencyExchangeCtrl.controls.useCurrencyExchange.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(checked => {
                if (this.$putChannelCtrl().value) return;
                if (this.#previousDefaultCurrency && !checked) {
                    this.#messageDialogService
                        .showWarn({
                            size: DialogSize.SMALL,
                            showCancelButton: true,
                            title: 'CHANNELS.DISABLE_CURRENCY_EXCHANGE_TITLE',
                            message: 'CHANNELS.DISABLE_CURRENCY_EXCHANGE_DESCRIPTION',
                            actionLabel: 'FORMS.ACTIONS.DEACTIVATE'
                        })
                        .subscribe(result => {
                            if (!result) {
                                this.currencyExchangeCtrl.controls.useCurrencyExchange.setValue(true, { emitEvent: false });
                                this.currencyExchangeCtrl.controls.currencySelected.setValue(
                                    this.#previousDefaultCurrency, { emitEvent: false }
                                );
                                this.currencyExchangeCtrl.controls.currencySelected.markAsDirty({ emitEvent: false });
                            } else {
                                this.currencyExchangeCtrl.controls.currencySelected.disable({ emitEvent: false });
                            }
                        });
                } else {
                    this.currencyExchangeCtrl.controls.currencySelected.enable({ emitEvent: false });
                }

            });
    }
}

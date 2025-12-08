import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import type { ChannelPurchaseConfig, ChannelWhitelabelSettings, CheckoutFlowModes, InvoiceConfig, Threshold, ThresholdFormGroup } from '@admin-clients/cpanel/channels/data-access';
import { ChannelsExtendedService, ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { BadgeComponent, EphemeralMessageService, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import type { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import type { Observable } from 'rxjs';
import { combineLatest, filter, first, forkJoin, map, tap, throwError } from 'rxjs';
import { PaymentConfigThresholdComponent } from './payment-config-threshold/payment-config-threshold.component';

const DEFAULT_INVOICE_CONFIG = {
    enabled: false,
    mandatory_thresholds: []
};

@Component({
    selector: 'ob-channel-payment-config',
    templateUrl: './payment-config.component.html',
    styleUrl: './payment-config.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, AsyncPipe, ReactiveFormsModule, TabsMenuComponent, TabDirective,
        TranslatePipe, LocalCurrencyPartialTranslationPipe, PaymentConfigThresholdComponent, MatRadioGroup,
        MatRadioButton, MatExpansionModule, MatSpinner, MatCheckbox, BadgeComponent
    ]
})
export class ChannelPaymentConfigComponent implements OnInit, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #authSrv = inject(AuthenticationService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelsService = inject(ChannelsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly #amountValidators = [Validators.required, Validators.min(0)];
    readonly form = this.#fb.group({
        mode: null as CheckoutFlowModes,
        invoice: this.#fb.group({
            enableInvoicing: false,
            // uncomment when back is ready
            // invoiceGenerationMode: ['MANUAL' as InvoiceConfig['invoice_generation_mode'], [Validators.required]],
            thresholds: this.#fb.array<ThresholdFormGroup>([]),
            requestType: ['MANDATORY ' as InvoiceConfig['invoice_request_type'], [Validators.required]]
        })
    });

    readonly isEnabledInvoicing$ = this.form.controls.invoice.controls.enableInvoicing.valueChanges.pipe(
        tap(isChecked => isChecked ? this.thresholdsCtrl.enable() : this.thresholdsCtrl.disable())
    );

    readonly $channelId: WritableSignal<number> = signal(null);
    readonly $isB2cChannel = signal(false);

    readonly inProgress$ = booleanOrMerge([
        this.#channelsExtSrv.isPurchaseConfigLoading$(),
        this.#channelsExtSrv.isPurchaseConfigSaving$()
    ]);

    readonly #currencies$ = combineLatest([
        this.#authSrv.getLoggedUser$(),
        this.#channelsService.getChannel$()
    ]).pipe(
        filter(resp => resp.every(Boolean)),
        tap(([_, channel]) => {
            this.$channelId.set(channel.id);
            this.$isB2cChannel.set(channel.type === ChannelType.web);
            this.#channelsExtSrv.loadPurchaseConfig(channel.id);
            this.#channelsService.channelWhitelabelSettings.load(channel.id);
        }),
        map(([user, channel]) => {
            if (user.operator.currencies) {
                return channel.currencies.map(c => c.code);
            }
            return [user.currency];
        })
    );

    readonly #invoiceConfig$ = this.#channelsExtSrv.getPurchaseConfig$().pipe(
        filter(Boolean),
        map(purchaseConfig => purchaseConfig.invoice ?? DEFAULT_INVOICE_CONFIG)
    );

    ngOnInit(): void {
        this.#channelsExtSrv.clearPurchaseConfig();
        this.#handleRequestTypeChanges();
        combineLatest([this.#invoiceConfig$, this.#currencies$, this.#channelsService.channelWhitelabelSettings.get$()])
            .pipe(filter(resp => resp.every(Boolean)), takeUntilDestroyed(this.#destroyRef))
            .subscribe(([invoiceConfig, channelCurrencies, whitelabelSettings]) => this.#updateFormValues(invoiceConfig, channelCurrencies, whitelabelSettings));
    }

    cancel(): void {
        this.#channelsExtSrv.loadPurchaseConfig(this.$channelId());
        this.form.markAsPristine();
    }

    save$(): Observable<void[]> {
        this.form.markAllAsTouched();
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            if (this.form.controls.invoice.dirty) {
                const { invoice: { enableInvoicing, thresholds, requestType } } = this.form.getRawValue();
                const validatedThresholds = thresholds?.filter(threshold => this.#isValidAmount(threshold.amount)) ?? [];
                const config: ChannelPurchaseConfig = {
                    invoice: {
                        enabled: enableInvoicing,
                        mandatory_thresholds: requestType === 'BY_AMOUNT' ? validatedThresholds : [],
                        invoice_request_type: requestType
                        // uncomment when back is ready
                        // invoice_generation_mode: invoiceGenerationMode
                    }
                };
                obs$.push(this.#channelsExtSrv.updatePurchaseConfig(this.$channelId(), config));
            }
            if (this.form.controls.mode.dirty) {
                const checkoutFlow = this.form.controls.mode.value;
                const whitelabelSettingsUpdate: Partial<ChannelWhitelabelSettings> = {
                    checkout: { checkout_flow: checkoutFlow }
                };
                obs$.push(this.#channelsService.channelWhitelabelSettings.update(this.$channelId(), whitelabelSettingsUpdate));
            }

            return forkJoin(obs$);
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
            if (this.form.controls.invoice.dirty) {
                this.#channelsExtSrv.loadPurchaseConfig(this.$channelId());
            }
            if (this.form.controls.mode.dirty) {
                this.#channelsService.channelWhitelabelSettings.load(this.$channelId());
            }
            this.form.markAsPristine();
        });
    }

    #updateFormValues(config: InvoiceConfig, currencies: string[], whitelabelSettings: ChannelWhitelabelSettings): void {
        const mandatoryThresholds = config?.mandatory_thresholds?.filter(t => this.#isValidAmount(t.amount)) ?? [];
        const hasMandatoryThresholds = mandatoryThresholds.length > 0;
        const checkoutFlow = whitelabelSettings?.checkout?.checkout_flow ?? 'ONE_STEP';

        this.form.patchValue({
            mode: checkoutFlow,
            invoice: {
                enableInvoicing: config.enabled,
                // uncomment when back is ready
                // invoiceGenerationMode: config.invoice_generation_mode || 'MANUAL',
                requestType: config.invoice_request_type || 'MANDATORY'
            }
        });

        this.thresholdsCtrl.clear();

        currencies.forEach(currencyTab => {
            const existingMandatoryThreshold = mandatoryThresholds.find(threshold => threshold.currency === currencyTab);
            const defaultAmount = hasMandatoryThresholds ? null : 0;
            const { currency, amount } = existingMandatoryThreshold ?? { currency: currencyTab, amount: defaultAmount } as Threshold;
            const formGroup = this.#fb.group({ currency, amount });
            const amountCtrl = formGroup.controls.amount;

            if (hasMandatoryThresholds && amount === null) {
                amountCtrl.valueChanges.pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef))
                    .subscribe(() => amountCtrl.addValidators(this.#amountValidators));
            } else {
                amountCtrl.addValidators(this.#amountValidators);
            }

            this.thresholdsCtrl.push(formGroup);
        });

        if (!hasMandatoryThresholds && config.enabled && config.invoice_request_type === 'BY_AMOUNT') {
            this.form.markAsDirty();
        }
    }

    #handleRequestTypeChanges(): void {
        this.form.controls.invoice.controls.requestType.valueChanges.subscribe(requestType => {
            if (requestType === 'BY_AMOUNT') {
                this.form.controls.invoice.controls.thresholds.enable();
            } else {
                this.form.controls.invoice.controls.thresholds.disable();
            }
        });
    }

    #isValidAmount(value: unknown): boolean {
        return typeof value === 'number' && value >= 0;
    }

    get thresholdsCtrl(): FormArray<ThresholdFormGroup> {
        return this.form.controls.invoice.controls.thresholds as FormArray;
    }
}

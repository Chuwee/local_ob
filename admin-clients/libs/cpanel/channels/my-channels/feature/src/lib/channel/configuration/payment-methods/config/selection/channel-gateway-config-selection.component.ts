import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelGatewayConfig, ChannelsExtendedService, ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService, EntityGateway, Gateway, GatewaysService } from '@admin-clients/shared/common/data-access';
import { JoinedLocalCurrenciesFullTranslationPipe, LocalCurrencyFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, KeyValue, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatDialogContent } from '@angular/material/dialog';
import { MatHint, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, firstValueFrom, Observable } from 'rxjs';
import { filter, first, map, shareReplay, withLatestFrom } from 'rxjs/operators';
import { ChannelGatewayCtrlType } from '../channel-gateway-config-dialog.component';
import { HasCommonElementsPipe } from './has-common-elements-array.pipe';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatLabel, MatOption, MatHint, MatTooltipModule, MatSelectModule, AsyncPipe, MatIconModule, MatRadioButton, MatProgressSpinner,
        KeyValuePipe, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent, LocalCurrencyFullTranslationPipe, MatCheckboxModule,
        JoinedLocalCurrenciesFullTranslationPipe, MatInputModule, HasCommonElementsPipe, MatDialogContent, MatRadioGroup
    ],
    selector: 'app-channel-gateway-config-selection',
    templateUrl: './channel-gateway-config-selection.component.html',
    styleUrls: ['./channel-gateway-config-selection.component.scss']
})
export class ChannelGatewayConfigSelectionComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #cdr = inject(ChangeDetectorRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #gatewaysSrv = inject(GatewaysService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);

    readonly PRIVATE_KEYS = [
        'STRIPE_APIKEY',
        'INSTANT_CREDIT_SECRET_KEY',
        'PAYPAL_CLIENT_SECRET',
        'REDSYS_PASSWORD',
        'API_KEY',
        'ACCESS_TOKEN',
        'PASSWORD',
        'WORLDPAY_PASSWORD',
        'DPAY_PASSWORD',
        'SECRET_KEY',
        'ADDON_PAYMENTS_REBATE_PASSWORD',
        'CREDOMATIC_PASSWORD',
        'SANTANDER_PASSWORD',
        'UNIVERSAL_PAY_PASSWORD',
        'CPAY_API_KEY'
    ];

    #maxRetriesValidatorFn = Validators.max(0);

    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly gatewayCtrl = this.#fb.nonNullable.control(null as EntityGateway, [Validators.required]);
    readonly gatewayCurrenciesCtrl = this.#fb.nonNullable.control({ value: null as string[], disabled: true }, Validators.required);
    readonly gatewayGroup = this.#fb.nonNullable.group({
        attemptsCtrl: [{ value: null as number, disabled: true },
        [Validators.required, Validators.min(1), this.#maxRetriesValidatorFn]],
        fieldValuesRecord: this.#fb.nonNullable.record<unknown>({}),
        refundCtrl: [{ value: null as boolean, disabled: true }],
        allowBenefitsCtrl: [{ value: null as boolean, disabled: true }],
        showBillingFormCtrl: [{ value: null as boolean, disabled: true }],
        saveCardByDefaultCtrl: [{ value: null as boolean, disabled: true }],
        forceRiskEvaluationCtrl: [{ value: null as boolean, disabled: true }],
        sendAdditionalDataCtrl: [{ value: null as boolean, disabled: true }],
        priceRangeEnabledCtrl: [{ value: null as boolean, disabled: true }],
        priceRangeGroup: this.#fb.nonNullable.group({
            minCtrl: null as number,
            maxCtrl: null as number
        }),
        liveCtrl: [{ value: false, disabled: true }]
    });

    readonly baseForm = this.#fb.nonNullable.group({
        gatewayCtrl: this.gatewayCtrl,
        gatewayGroup: this.gatewayGroup,
        gatewayCurrenciesCtrl: this.gatewayCurrenciesCtrl
    });

    readonly paymentMethods$ = this.#channelsExtSrv.getChannelPaymentMethods$()
        .pipe(
            filter(Boolean),
            map(paymentMethods => paymentMethods.map(pm => ({
                configuration_sid: pm.configuration_sid,
                gateway_sid: pm.gateway_sid,
                name: pm.name
            }))),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $paymentMethodGatewaySids = toSignal(this.#channelsExtSrv.getChannelPaymentMethods$()
        .pipe(
            filter(Boolean),
            map(paymentMethods => paymentMethods.map(pm => pm.gateway_sid)),
            shareReplay({ refCount: true, bufferSize: 1 })
        ));

    readonly entityGateways$: Observable<EntityGateway[]> = this.#entitiesSrv.entityGateways.get$()
        .pipe(
            first(Boolean),
            withLatestFrom(this.#channelsSrv.getChannel$()),
            map(([entityGateways, channel]) => {
                const excludedGateways = {
                    [ChannelType.webB2B]: ['cash']
                };
                return entityGateways.filter((entityGateway =>
                    !excludedGateways[channel.type]?.includes(entityGateway.gateway_sid)));
            })
        );

    readonly walletAssociationGatewayMethodsSids$ = combineLatest([
        this.entityGateways$,
        this.#gatewaysSrv.gateway.get$()
    ]).pipe(
        map(([entityGateways, entityGateway]) =>
            entityGateways?.filter(
                gateway => entityGateways.find(
                    g => g.gateway_sid === entityGateway?.sid
                )?.available_gateway_asociation.includes(gateway.gateway_sid)
            ).map(g => g.gateway_sid)
        )
    );

    readonly walletAsociationPaymentMethods$ = combineLatest([
        this.paymentMethods$,
        this.walletAssociationGatewayMethodsSids$
    ]).pipe(
        map(([paymentMethods, walletAssociationGatewayMethodsSids]) =>
            paymentMethods.filter(
                pm => walletAssociationGatewayMethodsSids.includes(pm.gateway_sid)
            ))
    );

    readonly channelCurrencies$ = this.#channelsSrv.getChannel$()
        .pipe(first(), map(channel => channel.currencies));

    readonly areMultipleChannelCurrencies$ = this.#channelsSrv.getChannel$()
        .pipe(first(), map(channel => channel.currencies?.length > 1));

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#entitiesSrv.entityGateways.loading$(),
        this.#gatewaysSrv.gateway.loading$()
    ]));

    $channelGatewayConfig = input<ChannelGatewayConfig>(null, { alias: 'channelGatewayConfig' });
    $gatewayRequestCtrl = input<FormControl<ChannelGatewayCtrlType>>(null, { alias: 'gatewayRequestCtrl' });
    $form = input<FormGroup>(null, { alias: 'form' });

    async ngOnInit(): Promise<void> {
        this.$form().addControl('base', this.baseForm, { emitEvent: false });
        await this.#init();
        this.#listenToCtrlChanges();
        this.#mapToGatewayRequest();
    }

    ngOnDestroy(): void {
        this.$form().removeControl('base', { emitEvent: false });
    }

    compareEntityGatewayWith(option: EntityGateway, option2: EntityGateway): boolean {
        return option?.gateway_sid === option2?.gateway_sid;
    }

    customOrder(a: KeyValue<string, unknown>, b: KeyValue<string, unknown>): number {
        if (a.key === 'GATEWAY_ASSOCIATION_CONFIG_SID' && b.key !== 'GATEWAY_ASSOCIATION_CONFIG_SID') {
            return 1;
        }

        if (b.key === 'GATEWAY_ASSOCIATION_CONFIG_SID' && a.key !== 'GATEWAY_ASSOCIATION_CONFIG_SID') {
            return -1;
        }

        return a.key.localeCompare(b.key);
    }

    async #init(): Promise<void> {
        await this.#initGatewayCurrencies();

        this.gatewayGroup.controls.fieldValuesRecord.disable({ emitEvent: false });
        this.gatewayGroup.controls.priceRangeGroup.disable({ emitEvent: false });

        if (this.$gatewayRequestCtrl().value.entityGateway) {
            this.#initGateway(this.$gatewayRequestCtrl().value.entityGateway);
        } else if (this.$channelGatewayConfig()) {
            this.entityGateways$
                .pipe(first(Boolean))
                .subscribe(entityGateways => {
                    const entityGateway = entityGateways.find(entityGateway =>
                        entityGateway.gateway_sid === this.$channelGatewayConfig().gateway_sid);
                    this.#initGateway(entityGateway);
                });
        }
    }

    async #initGatewayCurrencies(): Promise<void> {
        //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
        const isMultiCurrency = await firstValueFrom(this.isMultiCurrency$);
        if (isMultiCurrency) {
            //Config
            if (!this.$channelGatewayConfig()) {
                this.gatewayCurrenciesCtrl.enable({ emitEvent: false });

                const { request } = this.$gatewayRequestCtrl().value;
                const areMultipleChannelCurrencies = await firstValueFrom(this.areMultipleChannelCurrencies$);
                if (request.currency_codes) {
                    this.gatewayCurrenciesCtrl.reset(request.currency_codes, { emitEvent: false });
                    this.gatewayCurrenciesCtrl.markAsDirty();
                } else if (!areMultipleChannelCurrencies) {
                    const [channelCurrency] = await firstValueFrom(this.channelCurrencies$);
                    this.gatewayCurrenciesCtrl.reset([channelCurrency.code], { emitEvent: false });
                    this.gatewayCurrenciesCtrl.markAsDirty();
                }
            } else {
                const { currencies } = this.$channelGatewayConfig() ?? {};
                const serverCurrencyCodes = currencies?.map(currency => currency.code);
                if (serverCurrencyCodes) {
                    this.gatewayCurrenciesCtrl.reset(serverCurrencyCodes, { emitEvent: false });
                }
            }
        }
    }

    #initGateway(entityGateway: EntityGateway): void {
        this.gatewayCtrl.setValue(entityGateway, { emitEvent: false });
        this.#gatewaysSrv.gateway.get$()
            .pipe(first(Boolean))
            .subscribe(gateway => {
                this.#configGatewayGroup(gateway, this.gatewayCtrl.value);
                this.#initGatewayGroupValues();
            });
    }

    #configGatewayGroup(gateway: Gateway, entityGateway: EntityGateway): void {
        const {
            attemptsCtrl,
            liveCtrl,
            priceRangeEnabledCtrl,
            refundCtrl,
            allowBenefitsCtrl,
            showBillingFormCtrl,
            saveCardByDefaultCtrl,
            forceRiskEvaluationCtrl,
            sendAdditionalDataCtrl,
            fieldValuesRecord
        } = this.gatewayGroup.controls;

        this.#enableCtrl(attemptsCtrl, gateway.retry);
        this.#enableCtrl(refundCtrl, gateway.refund);
        this.#enableCtrl(allowBenefitsCtrl, gateway.allow_benefits);
        this.#enableCtrl(showBillingFormCtrl, gateway.show_billing_form);
        this.#enableCtrl(saveCardByDefaultCtrl, gateway.save_card_by_default);
        this.#enableCtrl(forceRiskEvaluationCtrl, gateway.force_risk_evaluation);
        this.#enableCtrl(sendAdditionalDataCtrl, gateway.send_additional_data);
        this.#enableCtrl(priceRangeEnabledCtrl, gateway.price_range_enabled);
        this.#enableCtrl(liveCtrl, gateway.live);

        if (entityGateway?.retry) {
            attemptsCtrl.removeValidators(this.#maxRetriesValidatorFn);
            this.#maxRetriesValidatorFn = Validators.max(entityGateway.retries);
            attemptsCtrl.addValidators(this.#maxRetriesValidatorFn);
            attemptsCtrl.updateValueAndValidity({ emitEvent: false });
        }

        if (gateway.fields.length) {
            const newFieldValuesRecord = this.#fb.nonNullable.record(gateway.fields.reduce((acc, field) => {
                acc[field] = [null, Validators.required];
                return acc;
            }, {}));
            this.gatewayGroup.setControl('fieldValuesRecord', newFieldValuesRecord, { emitEvent: false });
            newFieldValuesRecord.enable({ emitEvent: false });
        } else {
            fieldValuesRecord.disable({ emitEvent: false });
        }

        this.#cdr.detectChanges();
    }

    #enableCtrl(ctrl: AbstractControl, value: unknown): void {
        if (value) ctrl.enable({ emitEvent: false });
    }

    #initGatewayGroupValues(): void {
        const {
            attemptsCtrl,
            liveCtrl,
            priceRangeEnabledCtrl,
            priceRangeGroup,
            refundCtrl,
            allowBenefitsCtrl,
            showBillingFormCtrl,
            saveCardByDefaultCtrl,
            forceRiskEvaluationCtrl,
            sendAdditionalDataCtrl,
            fieldValuesRecord
        } = this.gatewayGroup.controls;

        const {
            attempts,
            refund,
            show_billing_form: showBillingForm,
            save_card_by_default: saveCardByDefault,
            allow_benefits: allowBenefits,
            force_risk_evaluation: forceRiskEvaluation,
            send_additional_data: sendAdditionalData,
            price_range_enabled: priceRangeEnabled,
            price_range: priceRange,
            live,
            field_values: fieldValues
        } = this.$channelGatewayConfig() ?? {};
        const { request } = this.$gatewayRequestCtrl().value;

        this.#initCtrl(attemptsCtrl, request.attempts, attempts);
        this.#initCtrl(refundCtrl, request.refund, refund);
        this.#initCtrl(allowBenefitsCtrl, request.allow_benefits, allowBenefits);
        this.#initCtrl(showBillingFormCtrl, request.show_billing_form, showBillingForm);
        this.#initCtrl(saveCardByDefaultCtrl, request.save_card_by_default, saveCardByDefault);
        this.#initCtrl(forceRiskEvaluationCtrl, request.force_risk_evaluation, forceRiskEvaluation);
        this.#initCtrl(sendAdditionalDataCtrl, request.send_additional_data, sendAdditionalData);
        if (priceRangeEnabledCtrl.enabled && (request.price_range_enabled || priceRangeEnabled)) {
            priceRangeEnabledCtrl.reset(request.price_range_enabled ?? priceRangeEnabled, { emitEvent: false });
            priceRangeGroup.enable({ emitEvent: false });
            priceRangeGroup.controls.minCtrl.reset(request.price_range?.min ?? priceRange.min, { emitEvent: false });
            priceRangeGroup.controls.maxCtrl.reset(request.price_range?.max ?? priceRange.max, { emitEvent: false });
            if (Number.isInteger(request.price_range?.min) || Number.isInteger(request.price_range?.max)) {
                priceRangeGroup.markAsDirty();
                priceRangeEnabledCtrl.markAsDirty();
            }
        }
        this.#initCtrl(liveCtrl, request.live, live);
        this.#initCtrl(fieldValuesRecord, request.field_values, fieldValues);
        this.#cdr.detectChanges();
    }

    #initCtrl(ctrl: AbstractControl, previousValue: unknown, serverValue: unknown): void {
        if (ctrl.enabled) {
            ctrl.reset(previousValue ?? serverValue, { emitEvent: false });
            if (previousValue) {
                ctrl.markAsDirty();
            }
        }
    }

    #listenToCtrlChanges(): void {
        let previousEntityGatewayGatewaySid = this.gatewayCtrl.value?.gateway_sid;
        this.gatewayCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entityGateway => {
                if (!entityGateway || previousEntityGatewayGatewaySid === entityGateway.gateway_sid) return;
                previousEntityGatewayGatewaySid = entityGateway.gateway_sid;

                this.gatewayGroup.reset({ priceRangeGroup: {} }, { emitEvent: false });
                this.gatewayGroup.disable({ emitEvent: false });

                this.#gatewaysSrv.gateway.clear();
                this.#gatewaysSrv.gateway.load(entityGateway.gateway_sid);
                this.#gatewaysSrv.gateway.get$()
                    .pipe(first(Boolean))
                    .subscribe(gateway => {
                        this.#configGatewayGroup(gateway, entityGateway);
                    });
            });

        this.gatewayGroup.controls.priceRangeEnabledCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(priceRangeEnabled => {
                if (priceRangeEnabled) {
                    this.gatewayGroup.controls.priceRangeGroup.enable({ emitEvent: false });
                } else {
                    this.gatewayGroup.controls.priceRangeGroup.reset({}, { emitEvent: false });
                    this.gatewayGroup.controls.priceRangeGroup.disable({ emitEvent: false });
                }
            });
    }

    #mapToGatewayRequest(): void {
        this.$gatewayRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(({ request }) => {
                if (this.$form().invalid) return;

                const {
                    attemptsCtrl,
                    liveCtrl,
                    priceRangeEnabledCtrl,
                    priceRangeGroup,
                    priceRangeGroup: { controls: { minCtrl, maxCtrl } },
                    refundCtrl,
                    allowBenefitsCtrl,
                    showBillingFormCtrl,
                    saveCardByDefaultCtrl,
                    forceRiskEvaluationCtrl,
                    sendAdditionalDataCtrl,
                    fieldValuesRecord
                } = this.gatewayGroup.controls;

                this.#setRequestProperty(attemptsCtrl, request, 'attempts');
                this.#setRequestProperty(liveCtrl, request, 'live');
                this.#setRequestProperty(refundCtrl, request, 'refund');
                this.#setRequestProperty(allowBenefitsCtrl, request, 'allow_benefits');
                this.#setRequestProperty(showBillingFormCtrl, request, 'show_billing_form');
                this.#setRequestProperty(saveCardByDefaultCtrl, request, 'save_card_by_default');
                this.#setRequestProperty(forceRiskEvaluationCtrl, request, 'force_risk_evaluation');
                this.#setRequestProperty(sendAdditionalDataCtrl, request, 'send_additional_data');
                if (priceRangeGroup.enabled && priceRangeGroup.dirty) {
                    request.price_range_enabled = priceRangeEnabledCtrl.value;
                    request.price_range = request.price_range ?? {};
                    request.price_range.max = maxCtrl.value;
                    request.price_range.min = minCtrl.value;
                }
                this.#setRequestProperty(fieldValuesRecord, request, 'field_values');
                this.#setRequestProperty(this.gatewayCurrenciesCtrl, request, 'currency_codes');

                this.$gatewayRequestCtrl().setValue({ request, entityGateway: this.gatewayCtrl.value }, { emitEvent: false });
            });
    }

    #setRequestProperty<T, K extends keyof T>(ctrl: AbstractControl<T[K]>, request: T, key: K): void {
        if (ctrl.enabled && ctrl.dirty) {
            request[key] = ctrl.value;
        }
    }
}

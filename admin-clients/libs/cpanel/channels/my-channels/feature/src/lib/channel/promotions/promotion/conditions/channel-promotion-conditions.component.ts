import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    CollectiveStatus, CollectiveValidationMethod, CollectivesService
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles, isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { ChannelPromotionsService, ChannelPromotionType, ChannelPromotion } from '@admin-clients/cpanel-channels-promotions-data-access';
import { PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, dateIsBefore, dateTimeGroupValidator } from '@admin-clients/shared/utility/utils';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnInit, QueryList, ViewChildren, inject, signal
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import {
    BehaviorSubject, combineLatest, filter, first, firstValueFrom, map, Observable, shareReplay, tap, throwError
} from 'rxjs';

@Component({
    selector: 'app-channel-promotion-conditions',
    templateUrl: './channel-promotion-conditions.component.html',
    styleUrls: ['./channel-promotion-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionConditionsComponent implements OnInit {
    readonly #channelPromotionsService = inject(ChannelPromotionsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #channelService = inject(ChannelsService);
    readonly #collectivesService = inject(CollectivesService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #changeRef = inject(ChangeDetectorRef);
    readonly #onDestroy = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    #originalFormValue: Record<string, unknown>;
    #isMultiCurrency: boolean;
    private readonly _isMultiCurrency$ = isMultiCurrency$().pipe(shareReplay({ bufferSize: 1, refCount: true }));
    readonly promotionTypes = ChannelPromotionType;
    readonly validityPeriodTypes = PromotionValidityPeriodType;
    readonly isOperator$ = this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]);

    readonly promotion$ = this.#channelPromotionsService.getPromotion$()
        .pipe(filter(Boolean));

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());

    readonly collectives$ = this.#collectivesService.getCollectivesListData$();
    readonly reqInProgress$ = booleanOrMerge([
        this.#channelPromotionsService.isPromotionInProgress$(),
        this.#channelPromotionsService.isPromotionsListInProgress$()
    ]);

    readonly hasMoreThanOneCurrencyBS = new BehaviorSubject<boolean>(false);
    readonly currencies$ = this.#channelService.getChannel$()
        .pipe(
            first(),
            tap(channel => this.hasMoreThanOneCurrencyBS.next(channel.currencies?.length > 1)),
            map(channel => channel.currencies?.map(curr => curr.code) ?? []),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $isSecondaryMarketActive = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean), map(entity => entity.settings.allow_secondary_market)));

    readonly $dataSource = signal([{}]);

    form: UntypedFormGroup;
    channelId: number;
    promotionId: number;
    channelOneCurrency = '';

    ngOnInit(): void {
        this.initForm();
        this.initFormChangesHandlers();

        combineLatest([
            this.promotion$,
            this.#channelService.getChannel$(),
            this._isMultiCurrency$,
            this.hasMoreThanOneCurrencyBS
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([promotion, channel, isMultiCurrency, hasMoreThanOneCurrency]) => {
                this.channelId = channel.id;
                this.promotionId = promotion.id;
                this.enableFieldsByPromoType(promotion.type);
                this.updateFormValues(promotion);

                // TODO MULTICURRENCY: Delete _isMulticurrency
                this.#isMultiCurrency = isMultiCurrency;
                if (isMultiCurrency) {
                    const amounts = promotion.usage_limits.amount_min.values ?? [];
                    const amountsFormArray = this.form.get('usage_limits.amount_min.values') as UntypedFormArray;
                    amountsFormArray.clear();
                    if (hasMoreThanOneCurrency) {
                        channel.currencies.forEach(currency => {
                            const control = this.#fb.group({
                                amount: [amounts?.find(amount => amount.currency_code === currency.code)?.amount ?? 0, [Validators.required, Validators.min(0.01)]],
                                currency_code: currency.code
                            });
                            amountsFormArray.push(control);
                        });
                    } else {
                        this.channelOneCurrency = channel.currencies[0].code;
                        this.form.get('usage_limits.amount_min.amount').setValue(amounts[0]?.amount);
                    }
                } else {
                    this.#authService.getLoggedUser$()
                        .pipe(first(Boolean))
                        .subscribe(user => this.channelOneCurrency = user.currency);
                }
            });

        this.#channelService.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                if (this.$entity()?.id !== channel?.entity?.id) {
                    this.#entitiesSrv.loadEntity(channel.entity.id);
                }
            });
    }

    cancel(): void {
        this.loadPromotion();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        // TODO MULTICURRENCY: Delete if
        if (this.#isMultiCurrency) { // operator multicurrency
            if (!this.hasMoreThanOneCurrencyBS.value) { // channel has only one currency
                const valuesForm = this.form.get('usage_limits.amount_min.values') as UntypedFormArray;
                valuesForm.enable();
                valuesForm.push(this.#fb.group({
                    amount: this.form.get('usage_limits.amount_min.amount').value,
                    currency_code: this.channelOneCurrency
                }));
            } else { // channel has more than one currency
                this.form.get('usage_limits.amount_min.amount').disable({ emitEvent: false });
                if (!this.form.get('usage_limits.amount_min.enabled').value) {
                    this.form.get('usage_limits.amount_min.values').disable({ emitEvent: false });
                } else {
                    this.form.get('usage_limits.amount_min.values').enable({ emitEvent: false });
                }
            }
        }
        if (this.form.valid) {
            const data = this.form.value;
            const promotion: ChannelPromotion = {
                ...data,
                ...{ block_secondary_market_sale: this.$isSecondaryMarketActive() ? data.block_secondary_market_sale : undefined },
                combinable: typeof data.not_combinable === 'boolean' ? !data.not_combinable : undefined
            };

            return this.#channelPromotionsService.updatePromotion(this.channelId, this.promotionId, promotion)
                .pipe(
                    tap(() => {
                        this.#ephemeralMessageService.showSaveSuccess();
                        this.loadPromotion();
                        if (this.form.get('validity_period').touched) {
                            this.#channelPromotionsService.loadPromotionsList(this.channelId, {
                                limit: 999, offset: 0, sort: 'name:asc'
                            });
                        }
                        if (this.#isMultiCurrency && !this.hasMoreThanOneCurrencyBS.value) {
                            this.form.get('usage_limits.amount_min.amount').enable();
                        }
                        this.form.get('usage_limits.amount_min.values').enable();
                    }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            this.form.get('usage_limits.amount_min.values').enable();
            return throwError(() => new Error('Invalid form'));
        }
    }

    async loadCollectives(q: string = null): Promise<void> {
        const channel = await firstValueFrom(this.#channelService.getChannel$());
        const isOperator = await firstValueFrom(this.isOperator$);

        const validationMethods = [
            CollectiveValidationMethod.promotionalCode,
            CollectiveValidationMethod.user,
            CollectiveValidationMethod.userPassword
        ];

        this.#collectivesService.loadCollectivesList({
            entity_id: isOperator ? channel.entity.id : undefined,
            status: CollectiveStatus.active,
            limit: 50,
            sort: 'name:asc',
            q,
            validation_method: validationMethods
        });
    }

    private updateFormValues(promotion: ChannelPromotion): void {
        this.form.reset({
            ...promotion,
            not_combinable: !promotion.combinable,
            collective: promotion.collective?.id && promotion.collective

        });
        this.#originalFormValue = this.form.value;
        this.form.markAsPristine();
    }

    private loadPromotion(): void {
        this.#channelPromotionsService.loadPromotion(this.channelId, this.promotionId);
    }

    private enableFieldsByPromoType(type: ChannelPromotionType): void {
        if (type === ChannelPromotionType.automatic) {
            this.form.get('not_combinable').enable();
            this.form.get('collective').disable();
        } else {
            this.form.get('not_combinable').disable();
            this.form.get('collective').enable();
        }
    }

    private initForm(): void {
        this.form = this.#fb.group({
            not_combinable: { value: false, disabled: true }, // only auto
            alternative_surcharges: this.#fb.group({
                use_alternative_surcharges: [{ value: false }],
                use_alternative_promoter_surcharges: [{ value: false }]
            }),
            validity_period: this.#fb.group({
                type: [null, Validators.required],
                start_date: [{ value: null, disabled: true }, Validators.required],
                end_date: [{ value: null, disabled: true }, Validators.required]
            }, {
                validators: [dateTimeGroupValidator(dateIsBefore, 'startDateAfterEndDate', 'start_date', 'end_date')]
            }),
            collective: [{ value: null, disabled: true }, Validators.required],
            usage_limits: this.#fb.group({
                purchase_min: this.#fb.group({
                    enabled: false,
                    limit: [
                        { value: null, disabled: true },
                        [Validators.required, Validators.min(1)]
                    ]
                }),
                promotion_max: this.#fb.group({
                    enabled: false,
                    limit: [
                        { value: null, disabled: true },
                        [Validators.required, Validators.min(1)]
                    ]
                }),
                amount_min: this.#fb.group({
                    enabled: false,
                    amount: [
                        { value: null, disabled: true },
                        [Validators.required, Validators.min(0.01)]
                    ],
                    values: this.#fb.array(
                        [{ value: null, disabled: true }],
                        [Validators.required, Validators.min(0.01)]
                    )
                })
            }),
            block_secondary_market_sale: false
        });
    }

    private initFormChangesHandlers(): void {
        this.form.get('validity_period.type').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(validityPeriodType => {
                const startDate = this.form.get('validity_period.start_date');
                const endDate = this.form.get('validity_period.end_date');
                if (validityPeriodType === PromotionValidityPeriodType.channel) {
                    startDate.disable();
                    endDate.disable();
                } else {
                    startDate.enable();
                    endDate.enable();
                }
            });

        this.form.get('usage_limits').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(limits => {
                for (const prop of Object.keys(limits)) {
                    const ctrl = this.form.get(`usage_limits.${prop}.limit`) || this.form.get(`usage_limits.${prop}.amount`) ||
                        this.form.get(`usage_limits.${prop}.values`);
                    if (limits[prop].enabled && ctrl.disabled) {
                        ctrl.enable({ emitEvent: false });
                        ctrl.markAsUntouched();
                    } else if (!limits[prop].enabled && ctrl.enabled) {
                        ctrl.disable({ emitEvent: false });
                        ctrl.markAsUntouched();
                    }
                }
            });
    }
}

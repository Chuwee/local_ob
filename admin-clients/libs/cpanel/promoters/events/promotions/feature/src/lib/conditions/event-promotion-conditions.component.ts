import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    CollectivesService, CollectiveStatus, CollectiveType, CollectiveValidationMethod
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { GetPacksRequest, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import {
    BasePromotion, PromotionChannelsScope, PromotionCollectiveScope,
    PromotionRatesScope,
    PutPromotionChannels
} from '@admin-clients/cpanel/promoters/data-access';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventPromotion, EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    PromotionByRateTicketsGroupsComponent, PromotionCustomerTypesComponent,
    PromotionLimitsComponent
} from '@admin-clients/cpanel-common-promotions-feature';
import { PromotionType, PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import {
    DateTimeModule, DialogSize, EphemeralMessageService, HelpButtonComponent,
    MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    booleanOrMerge, dateIsBefore,
    dateTimeGroupValidator, FormControlHandler
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, viewChildren, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, ValidationErrors, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatExpansionPanel } from '@angular/material/expansion';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    bufferCount, catchError, combineLatest, combineLatestWith, concat, debounceTime, filter,
    map, Observable, of, shareReplay, startWith, switchMap, tap, throwError, withLatestFrom
} from 'rxjs';
import { EventPromotionChannelsComponent } from '../channels/event-promotion-channels.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        EventPromotionChannelsComponent, FormContainerComponent, ReactiveFormsModule, MaterialModule,
        TranslatePipe, EllipsifyDirective, HelpButtonComponent, DateTimeModule, FormControlErrorsComponent,
        PromotionLimitsComponent, ArchivedEventMgrComponent, RouterLink, AsyncPipe, PromotionCustomerTypesComponent,
        PromotionByRateTicketsGroupsComponent
    ],
    selector: 'app-event-promotion-conditions',
    templateUrl: './event-promotion-conditions.component.html',
    styleUrls: ['./event-promotion-conditions.component.scss']
})
export class EventPromotionConditionsComponent implements OnInit, OnDestroy {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #eventsService = inject(EventsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #collectivesService = inject(CollectivesService);
    readonly #authService = inject(AuthenticationService);
    readonly #packsSrv = inject(PacksService);
    readonly #msgService = inject(MessageDialogService);
    readonly #onDestroy = inject(DestroyRef);

    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly pageSize = 10;
    readonly promotionTypes = PromotionType;
    readonly promotionCollectiveScope = PromotionCollectiveScope;
    readonly validityPeriodTypes = PromotionValidityPeriodType;
    readonly promotion$ = this.#eventPromotionsService.promotion.get$().pipe(filter(Boolean), shareReplay(1));
    readonly $promotion = toSignal(this.promotion$);
    readonly collectives$ = this.#collectivesService.getCollectivesListData$().pipe(filter(Boolean));
    readonly $isSecondaryMarketActive = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean), map(entity => entity.settings.allow_secondary_market)));

    readonly $rates = toSignal(this.#eventsService.eventRates.get$());
    readonly $promotionRates = toSignal(this.#eventPromotionsService.promotionRates.get$());
    readonly $loading = toSignal(booleanOrMerge([
        this.#eventPromotionsService.promotion.loading$(),
        this.#eventPromotionsService.promotionChannels.loading$(),
        this.#collectivesService.isCollectiveListLoading$(),
        this.#eventPromotionsService.promotionRates.loading$()
    ]));

    readonly form = this.#fb.group({
        notCombinable: { value: false, disabled: true }, // only auto
        enableAlternativeChannelSurcharges: false,
        enableAlternativePromoterSurcharges: false,
        showDiscountNameInTicket: { value: false, disabled: true }, // only auto
        showTicketPriceWithoutDiscount: false,
        accessControlRestricted: false,
        blockSecondaryMarketSale: false,
        validityPeriod: this.#fb.group({
            type: [null, Validators.required],
            dates: this.#fb.group({
                start: [{ value: null, disabled: true }, Validators.required],
                end: [{ value: null, disabled: true }, Validators.required]
            }, {
                validators: [dateTimeGroupValidator(dateIsBefore, 'startDateAfterEndDate', 'start', 'end')]
            })
        }),
        collective: this.#fb.group({ // only non auto
            type: [{ value: null, disabled: true }, Validators.required],
            collectiveId: [{ value: null, disabled: true }, Validators.required],
            restrictiveSale: { value: false, disabled: true },
            presaleSale: { value: false, disabled: true },
            notBoxOfficeValidation: { value: false, disabled: true },
            selfManaged: { value: false, disabled: true }
        }),
        channels: this.#fb.group({
            type: [null, Validators.required],
            selected: [{ value: [] as IdName[], disabled: true }, Validators.required]
        }),
        // Commented till back in channels done
        // customerTypes: this.#fb.group({
        //     type: [null, [Validators.required]],
        //     selected: [[] as number[], [atLeastOneRequiredInArray()]]
        // }),
        rateTicketsGroups: this.#fb.group({
            enabled: [false],
            rate_ticket_groups: this.#fb.array([], { validators: Validators.required })
        })
    });

    readonly fixedChannelTypeFilter$ = this.form.get('collective').valueChanges.pipe(
        startWith(this.form.get('collective').value),
        debounceTime(200),
        map(collective => collective?.presaleSale ? ChannelType.web : null),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly isPresale$ = this.form.get('collective.presaleSale').valueChanges.pipe(
        startWith(this.form.get('collective.presaleSale').value),
        debounceTime(200),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    eventId: number;
    promotionId: number;
    isAvet = false;
    errors: ValidationErrors;
    $packList = toSignal(this.#packsSrv.packsList.getData$());
    promotionType: PromotionType = null;
    promotionRateTicketsGroups: BasePromotion['applicable_conditions']['rates_relations_condition']['rates'] = [];

    ngOnInit(): void {
        this.promotion$
            .pipe(
                withLatestFrom(
                    this.#eventsService.event.get$(),
                    this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS])),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([promotion, event, isOperator]) => {
                if (promotion.type !== PromotionType.automatic) {
                    const req = { entity_id: undefined, status: CollectiveStatus.active, limit: 50 };
                    if (isOperator) {
                        req.entity_id = event.entity.id;
                    }
                    this.#collectivesService.fetchCollectives(req);
                }
                this.eventId = event.id;
                this.promotionId = promotion.id;
                this.promotionType = promotion.type;
                this.promotionRateTicketsGroups = promotion.applicable_conditions?.rates_relations_condition?.rates || [];
                this.isAvet = event.type === EventType.avet;
                this.errors = {};
                this.#enableFieldsByPromoType(promotion.type);
                this.#initFormChangesHandlers();
                this.#updateFormValues(promotion, event);
                if (this.promotionType === this.promotionTypes.automatic) this.#loadEventRates();
            });

        const filters: GetPacksRequest = {
            eventId: this.eventId,
            limit: 10,
            offset: 0
        };

        this.#packsSrv.packsList.load(filters);
    }

    ngOnDestroy(): void {
        this.#packsSrv.packsList.clear();
    }

    cancel(): void {
        this.#loadPromotionAndChannels();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const data = this.form.value;
            const req: EventPromotion = {
                combinable: typeof data.notCombinable === 'boolean' ? !data.notCombinable : undefined,
                surcharges: {
                    channel_fees: data.enableAlternativeChannelSurcharges,
                    promoter: data.enableAlternativePromoterSurcharges
                },
                ...(this.$isSecondaryMarketActive() && { block_secondary_market_sale: data.blockSecondaryMarketSale }),
                show_discount_name_ticket: data.showDiscountNameInTicket,
                show_ticket_price_without_discount: data.showTicketPriceWithoutDiscount,
                access_control_restricted: data.accessControlRestricted,
                validity_period: {
                    type: data.validityPeriod?.type,
                    start_date: data.validityPeriod?.dates?.start,
                    end_date: data.validityPeriod?.dates?.end
                },
                collective: data.collective && {
                    type: data.collective.type,
                    id: data.collective.collectiveId || undefined,
                    restrictive_sale: data.collective.restrictiveSale,
                    box_office_validation: (data.collective.collectiveId && !data.collective.notBoxOfficeValidation) ?? undefined,
                    self_managed: data.collective.selfManaged
                },
                presale: (data.collective?.restrictiveSale && data.collective?.presaleSale) ?? false,
                usage_limits: {
                    ticket_group_min: data.limits?.packsMin && { // non Auto
                        enabled: data.limits.packsMin.enabled,
                        limit: data.limits.packsMin.limit
                    },
                    purchase_min: data.limits?.purchaseMin && { // non Auto
                        enabled: data.limits.purchaseMin.enabled,
                        limit: data.limits.purchaseMin.limit
                    },
                    purchase_max: data.limits?.purchaseMax && { // non Auto
                        enabled: data.limits.purchaseMax.enabled,
                        limit: data.limits.purchaseMax.limit
                    },
                    event_user_collective_max: data.limits?.eventUserMax && { // non Auto
                        enabled: data.limits.eventUserMax.enabled,
                        limit: data.limits.eventUserMax.limit
                    },
                    session_user_collective_max: data.limits?.sessionUserMax && { // non Auto
                        enabled: data.limits.sessionUserMax.enabled,
                        limit: data.limits.sessionUserMax.limit
                    },
                    promotion_max: {
                        enabled: data.limits?.promotionMax?.enabled,
                        limit: data.limits?.promotionMax?.limit
                    },
                    session_max: {
                        enabled: data.limits?.sessionMax?.enabled,
                        limit: data.limits?.sessionMax?.limit
                    }
                },
                ...(this.promotionType === PromotionType.automatic && {
                    applicable_conditions: {
                        rates_relations_condition: {
                            enabled: data.rateTicketsGroups?.enabled,
                            rates: data.rateTicketsGroups?.rate_ticket_groups?.map(group => ({
                                limit: group.limit,
                                rate: group.rate
                            })) || []
                        }
                        // Commented till back in channels done
                        // ...(this.form.get('customerTypes').enabled && {
                        //     customer_types_condition: {
                        //         type: data.customerTypes?.type,
                        //         customer_types: data.customerTypes?.selected
                        //     }
                        // })
                    }
                })
            };
            const updateObs: Observable<void>[] = [];
            updateObs.push(this.#eventPromotionsService.promotion.update(this.eventId, this.promotionId, req));

            const channelsForm = this.form.get('channels');
            if (channelsForm.dirty) {
                this.errors['savePromotionChannels'] = false;
                const request: PutPromotionChannels = {
                    type: (channelsForm.value.selected && PromotionChannelsScope.restricted) || PromotionChannelsScope.all,
                    channels: channelsForm.value.selected?.map(channel => channel.id) || undefined
                };
                updateObs.push(
                    this.#eventPromotionsService.promotionChannels.update(this.eventId, this.promotionId, request)
                        .pipe(
                            catchError(error => {
                                this.errors['savePromotionChannels'] = true;
                                this.#ref.detectChanges();
                                return throwError(() => error);
                            })
                        )
                );
            }

            let warningObs$: Observable<boolean>;
            if (this.promotionType === PromotionType.automatic && data.rateTicketsGroups?.enabled
                && !this.#areRatesGroupsAlignedWithAssignationRates()) {
                warningObs$ = this.#msgService.showWarn({
                    title: 'EVENTS.PROMOTIONS.BY_RATE_TICKETS_GROUPS.WARNING_TITLE',
                    message: 'EVENTS.PROMOTIONS.BY_RATE_TICKETS_GROUPS.WARNING_MESSAGE',
                    actionLabel: 'FORMS.ACTIONS.SAVE',
                    size: DialogSize.MEDIUM
                });
                const selectedRatesIds = data.rateTicketsGroups?.rate_ticket_groups?.map(group => group.rate) || [];
                updateObs.push(this.#eventPromotionsService.promotionRates.update(this.eventId, this.promotionId,
                    { type: PromotionRatesScope.restricted, rates: selectedRatesIds }));
            } else {
                warningObs$ = of(true);
            }

            return warningObs$.pipe(filter(Boolean), switchMap(() => concat(...updateObs).pipe(
                bufferCount(updateObs.length),
                tap(() => {
                    this.#handleSaveSuccess();
                })
            )));
        } else {
            this.form.markAllAsTouched();
            this.form.get('channels').updateValueAndValidity();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
            return throwError(() => 'invalid fields');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    emptyChannels(e: MatCheckboxChange): void {
        if (e.checked) {
            this.form.get('channels.selected').setValue([]);
            this.form.get('channels').markAsDirty();
        }
    }

    #enableFieldsByPromoType(type: PromotionType): void {
        if (type === PromotionType.automatic) {
            this.form.get('notCombinable').enable({ emitEvent: false });
            this.form.get('collective.type').disable({ emitEvent: false });
            this.form.get('showDiscountNameInTicket').disable({ emitEvent: false });
            // Commented till back in channels done
            // this.form.get('customerTypes').enable({ emitEvent: false });
        } else {
            this.form.get('showDiscountNameInTicket').enable({ emitEvent: false });
            this.form.get('notCombinable').disable({ emitEvent: false });
            this.form.get('collective.type').enable({ emitEvent: false });
            // Commented till back in channels done
            // this.form.get('customerTypes').disable({ emitEvent: false });
        }
    }

    #initFormChangesHandlers(): void {
        this.form.get('validityPeriod.type').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(validityPeriodType => {
                const startDate = this.form.get('validityPeriod.dates.start');
                const endDate = this.form.get('validityPeriod.dates.end');
                if (validityPeriodType === PromotionValidityPeriodType.event) {
                    startDate.disable({ emitEvent: false });
                    endDate.disable({ emitEvent: false });
                } else {
                    startDate.enable({ emitEvent: false });
                    endDate.enable({ emitEvent: false });
                }
            });

        this.form.get('collective.type').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe((scope: PromotionCollectiveScope) => {
                const collectiveId = this.form.get('collective.collectiveId') as UntypedFormControl;
                const restrictiveSale = this.form.get('collective.restrictiveSale') as UntypedFormControl;
                const presaleSale = this.form.get('collective.presaleSale') as UntypedFormControl;
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                const selfManaged = this.form.get('collective.selfManaged') as UntypedFormControl;
                if (!scope || scope === PromotionCollectiveScope.none) {
                    collectiveId.disable();
                    restrictiveSale.disable();
                    restrictiveSale.value && restrictiveSale.setValue(null);
                    notBoxOfficeValidation.disable({ emitEvent: false });
                    selfManaged.disable({ emitEvent: false });
                    presaleSale.disable();
                } else {
                    collectiveId.enable();
                    restrictiveSale.enable();
                }
            });

        if (!this.isAvet) {
            this.form.get('collective.restrictiveSale').valueChanges
                .pipe(takeUntilDestroyed(this.#onDestroy))
                .subscribe(restrictiveSale => {
                    const presaleSaleCtrl = this.form.get('collective.presaleSale');
                    if (!restrictiveSale) {
                        presaleSaleCtrl.disable({ emitEvent: false });
                        presaleSaleCtrl.value && presaleSaleCtrl.setValue(null);
                    } else {
                        presaleSaleCtrl.enable({ emitEvent: false });
                    }
                });
        }

        this.form.get('collective.presaleSale').valueChanges.pipe(
            combineLatestWith(this.promotion$, this.#eventPromotionsService.promotionChannels.get$()),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([isPresale, promo, channels]) => {
            const validityCtrl = this.form.get('validityPeriod.type');
            const channelsTypeCtrl = this.form.get('channels.type');
            if (isPresale) {
                validityCtrl.disable({ emitEvent: false });
                validityCtrl.setValue(PromotionValidityPeriodType.event, { emitEvent: false });
                channelsTypeCtrl.setValue(PromotionChannelsScope.restricted);
                channelsTypeCtrl.disable({ emitEvent: false });
            } else {
                validityCtrl.enable({ emitEvent: false });
                validityCtrl.reset(promo?.validity_period?.type, { emitEvent: false });
                channelsTypeCtrl.setValue(channels?.type);
                channelsTypeCtrl.enable({ emitEvent: false });
            }
        });

        combineLatest([
            this.form.get('collective.collectiveId').valueChanges,
            this.collectives$
        ])
            .pipe(
                map(([id, collectives]) => collectives.find(el => el.id === id)),
                filter(collective => !!collective),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(collective => {
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                if (collective.validation_method === CollectiveValidationMethod.promotionalCode
                    || collective.validation_method === CollectiveValidationMethod.user) {
                    notBoxOfficeValidation.enable({ emitEvent: false });
                } else {
                    notBoxOfficeValidation.reset({ emitEvent: false });
                    notBoxOfficeValidation.disable({ emitEvent: false });
                }

                const collectiveSelfMngCtrl = this.form.get('collective.selfManaged');
                if (!(collective.type === CollectiveType.external && this.isAvet
                    && collective.validation_method === CollectiveValidationMethod.userPassword)) {
                    collectiveSelfMngCtrl.disable({ emitEvent: false });
                } else {
                    collectiveSelfMngCtrl.enable({ emitEvent: false });
                }

            });

        combineLatest([
            this.promotion$,
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([promo]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('notCombinable'),
                    !promo.combinable
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('enableAlternativeChannelSurcharges'),
                    promo.surcharges?.channel_fees || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('enableAlternativePromoterSurcharges'),
                    promo.surcharges?.promoter || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('showDiscountNameInTicket'),
                    promo.show_discount_name_ticket || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('showTicketPriceWithoutDiscount'),
                    promo.show_ticket_price_without_discount || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('accessControlRestricted'),
                    promo.access_control_restricted || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('validityPeriod.type'),
                    promo.validity_period?.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('validityPeriod.dates.start'),
                    promo.validity_period?.start_date || null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('validityPeriod.dates.end'),
                    promo.validity_period?.end_date || null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('collective.type'),
                    promo.collective?.type || null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('collective.collectiveId'),
                    promo.collective?.id || null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('collective.restrictiveSale'),
                    promo.collective?.restrictive_sale || null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('collective.notBoxOfficeValidation'),
                    typeof promo.collective?.box_office_validation === 'boolean'
                        ? !promo.collective?.box_office_validation : null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('collective.selfManaged'),
                    promo.collective?.self_managed || null
                );
            });
    }

    #updateFormValues(promotion: EventPromotion, event: Event): void {
        this.form.patchValue({
            notCombinable: !promotion.combinable,
            enableAlternativeChannelSurcharges: promotion.surcharges?.channel_fees || false,
            enableAlternativePromoterSurcharges: promotion.surcharges?.promoter || false,
            showDiscountNameInTicket: promotion.show_discount_name_ticket || false,
            showTicketPriceWithoutDiscount: promotion.show_ticket_price_without_discount || false,
            accessControlRestricted: promotion.access_control_restricted || false,
            blockSecondaryMarketSale: promotion.block_secondary_market_sale || false,
            validityPeriod: {
                type: promotion.validity_period?.type,
                dates: {
                    start: promotion.validity_period?.start_date || event.start_date,
                    end: promotion.validity_period?.end_date || event.end_date
                }
            },
            collective: {
                type: promotion.collective?.type || null,
                collectiveId: promotion.collective?.id || null,
                restrictiveSale: promotion.collective?.restrictive_sale || null,
                notBoxOfficeValidation: !promotion.collective?.box_office_validation || null,
                selfManaged: promotion.collective?.self_managed || null,
                presaleSale: promotion?.presale || null
            },
            rateTicketsGroups: {
                enabled: promotion.applicable_conditions?.rates_relations_condition?.enabled || false
            }
        });
        if (!promotion.applicable_conditions?.rates_relations_condition?.enabled) {
            this.form.get('rateTicketsGroups.rate_ticket_groups').disable();
        } else {
            this.form.get('rateTicketsGroups.rate_ticket_groups').enable();
        }
        this.form.markAsPristine();
    }

    #loadPromotionAndChannels(): void {
        this.#eventPromotionsService.promotion.load(this.eventId, this.promotionId);
        this.#eventPromotionsService.promotionChannels.load(this.eventId, this.promotionId);
    }

    #loadEventRates(): void {
        this.#eventsService.eventRates.load(this.eventId.toString());
        this.#eventPromotionsService.promotionRates.load(this.eventId, this.promotionId);
    }

    #areRatesGroupsAlignedWithAssignationRates(): boolean {
        const selectedRates = this.form.value.rateTicketsGroups?.rate_ticket_groups?.map(group => group.rate) || [];
        return this.$promotionRates()?.type === 'ALL' ||
            (selectedRates.every(rateId => this.$promotionRates()?.rates?.map(rate => rate.id).includes(rateId))
                && this.$promotionRates()?.rates?.every(rate => selectedRates.includes(rate.id)));
    }

    #handleSaveSuccess(): void {
        this.#ephemeralMsg.showSaveSuccess();
        if (this.form.get('validityPeriod.type').touched ||
            this.form.get('validityPeriod.dates.start').touched ||
            this.form.get('validityPeriod.dates.end').touched ||
            this.form.get('collective.restrictiveSale').touched ||
            this.form.get('collective.presaleSale').touched) {
            this.#eventPromotionsService.promotionsList.load(this.eventId, {
                limit: 999, offset: 0, sort: 'name:asc'
            });
        }
        this.#loadPromotionAndChannels();
        if (this.form.get('collective.presaleSale')?.value && this.form.get('collective.presaleSale')?.dirty) {
            this.#eventPromotionsService.promotionPriceTypes.load(this.eventId, this.promotionId);
            this.#eventPromotionsService.promotionRates.load(this.eventId, this.promotionId);
        }
    }
}

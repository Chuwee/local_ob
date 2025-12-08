import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    CollectivesService, CollectiveStatus, CollectiveType, CollectiveValidationMethod
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    BasePromotion, PromotionCollectiveScope, PromotionRatesScope,
    PutPromotionChannels
} from '@admin-clients/cpanel/promoters/data-access';
import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotion, SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import {
    PromotionByRateTicketsGroupsComponent,
    PromotionCustomerTypesComponent, PromotionLimitsComponent
} from '@admin-clients/cpanel-common-promotions-feature';
import { PromotionType, PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DateTimeModule, DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    booleanOrMerge, FormControlHandler, dateIsBefore,
    dateTimeGroupValidator
} from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, viewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { bufferCount, combineLatest, concat, Observable, of, tap, throwError } from 'rxjs';
import { catchError, filter, map, shareReplay, switchMap, withLatestFrom } from 'rxjs/operators';
import { SeasonTicketPromotionChannelsComponent } from '../channels/season-ticket-promotion-channels.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, FormContainerComponent, ReactiveFormsModule, MaterialModule, TranslatePipe,
        EllipsifyDirective, DateTimeModule, FormControlErrorsComponent, PromotionCustomerTypesComponent,
        SeasonTicketPromotionChannelsComponent, PromotionLimitsComponent, RouterLink, PromotionByRateTicketsGroupsComponent
    ],
    selector: 'app-season-ticket-promotion-conditions',
    templateUrl: './season-ticket-promotion-conditions.component.html',
    styleUrls: ['./season-ticket-promotion-conditions.component.scss']
})
export class SeasonTicketPromotionConditionsComponent implements OnInit {
    readonly #stPromotionsSrv = inject(SeasonTicketPromotionsService);
    readonly #seasonTicketService = inject(SeasonTicketsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #collectivesService = inject(CollectivesService);
    readonly #authService = inject(AuthenticationService);
    readonly #msgService = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);

    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly promotionTypes = PromotionType;
    readonly promotionCollectiveScope = PromotionCollectiveScope;
    readonly validityPeriodTypes = PromotionValidityPeriodType;

    readonly promotion$ = this.#stPromotionsSrv.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly $promotion = toSignal(this.promotion$);
    readonly collectives$ = this.#collectivesService.getCollectivesListData$()
        .pipe(
            filter(collective => !!collective),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly $rates = toSignal(this.#seasonTicketService.getSeasonTicketRates$());
    readonly $promotionRates = toSignal(this.#stPromotionsSrv.promotionRates.get$());

    readonly $loading = toSignal(booleanOrMerge([
        this.#stPromotionsSrv.promotion.loading$(),
        this.#stPromotionsSrv.promotionChannels.loading$(),
        this.#collectivesService.isCollectiveListLoading$(),
        this.#stPromotionsSrv.promotionRates.loading$()
    ]));

    readonly form = this.#fb.group({
        notCombinable: { value: false, disabled: true }, // only auto
        enableAlternativeChannelSurcharges: false,
        enableAlternativePromoterSurcharges: false,
        showDiscountNameInTicket: { value: false, disabled: true }, // only auto
        showTicketPriceWithoutDiscount: false,
        accessControlRestricted: false,
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
            notBoxOfficeValidation: { value: false, disabled: true },
            selfManaged: { value: false, disabled: true }
        }),
        channels: this.#fb.group({
            type: [null, Validators.required],
            selected: [{ value: [], disabled: true }, Validators.required]
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

    seasonTicketId: number;
    promotionId: number;
    promotionType: PromotionType = null;
    promotionRateTicketsGroups: BasePromotion['applicable_conditions']['rates_relations_condition']['rates'] = [];
    errors = { savePromotionChannels: false };

    ngOnInit(): void {
        this.promotion$
            .pipe(
                withLatestFrom(
                    this.#seasonTicketService.seasonTicket.get$(),
                    this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS])),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([promotion, seasonTicket, isOperator]) => {
                if (promotion.type !== PromotionType.automatic) {
                    const req = { entity_id: undefined, status: CollectiveStatus.active, limit: 50 };
                    if (isOperator) {
                        req.entity_id = seasonTicket.entity.id;
                    }
                    this.#collectivesService.fetchCollectives(req);
                }
                this.seasonTicketId = seasonTicket.id;
                this.promotionId = promotion.id;
                this.promotionType = promotion.type;
                this.promotionRateTicketsGroups = promotion.applicable_conditions?.rates_relations_condition?.rates || [];
                this.#enableFieldsByPromoType(promotion.type);
                this.#initFormChangesHandlers();
                this.#updateFormValues(promotion, seasonTicket);
                if (this.promotionType === PromotionType.automatic) this.#loadSTRates();
            });

    }

    cancel(): void {
        this.#loadPromotionAndChannels();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const data = this.form.value;
            const req: SeasonTicketPromotion = {
                combinable: typeof data.notCombinable === 'boolean' ? !data.notCombinable : undefined,
                surcharges: {
                    channel_fees: data.enableAlternativeChannelSurcharges,
                    promoter: data.enableAlternativePromoterSurcharges
                },
                show_discount_name_ticket: data.showDiscountNameInTicket,
                show_ticket_price_without_discount: data.showTicketPriceWithoutDiscount,
                access_control_restricted: data.accessControlRestricted,
                validity_period: {
                    type: data.validityPeriod?.type,
                    start_date: data.validityPeriod?.dates?.start,
                    end_date: data.validityPeriod?.dates?.end
                },
                collective: data.collective && {
                    type: data.collective.type || null,
                    id: data.collective.collectiveId || null,
                    restrictive_sale: data.collective.restrictiveSale,
                    box_office_validation: typeof data.collective.notBoxOfficeValidation === 'boolean'
                        ? !data.collective.notBoxOfficeValidation : undefined,
                    self_managed: data.collective.selfManaged
                },
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
            const updateObs = [this.#stPromotionsSrv.promotion.update(this.seasonTicketId, this.promotionId, req)];
            const channelsForm = this.form.get('channels');
            if (channelsForm.dirty) {
                this.errors.savePromotionChannels = false;
                const request: PutPromotionChannels = {
                    type: channelsForm.value.type,
                    channels: channelsForm.value.selected?.map(channel => channel.id) || []
                };
                updateObs.push(
                    this.#stPromotionsSrv.promotionChannels.update(this.seasonTicketId, this.promotionId, request)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionChannels = true;
                                this.#ref.detectChanges();
                                throw error;
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
                updateObs.push(this.#stPromotionsSrv.promotionRates.update(this.seasonTicketId, this.promotionId,
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
            this.form.get('channels.type').updateValueAndValidity();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
            return throwError(() => 'invalid fields');
        }
    }

    #enableFieldsByPromoType(type: PromotionType): void {
        if (type === PromotionType.automatic) {
            this.form.get('notCombinable').enable();
            this.form.get('collective.type').disable();
            this.form.get('showDiscountNameInTicket').disable();
            // Commented till back in channels done
            // this.form.get('customerTypes').enable();
        } else {
            this.form.get('showDiscountNameInTicket').enable();
            this.form.get('notCombinable').disable();
            this.form.get('collective.type').enable();
            // Commented till back in channels done
            // this.form.get('customerTypes').disable();
        }
    }

    #initFormChangesHandlers(): void {
        this.form.get('validityPeriod.type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(validityPeriodType => {
                const startDate = this.form.get('validityPeriod.dates.start');
                const endDate = this.form.get('validityPeriod.dates.end');
                if (validityPeriodType === PromotionValidityPeriodType.event) {
                    startDate.disable();
                    endDate.disable();
                } else {
                    startDate.enable();
                    endDate.enable();
                }
            });

        this.form.get('collective.type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((scope: PromotionCollectiveScope) => {
                const collectiveId = this.form.get('collective.collectiveId') as UntypedFormControl;
                const restrictiveSale = this.form.get('collective.restrictiveSale') as UntypedFormControl;
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                const selfManaged = this.form.get('collective.selfManaged') as UntypedFormControl;
                if (!scope || scope === PromotionCollectiveScope.none) {
                    collectiveId.disable();
                    restrictiveSale.disable();
                    notBoxOfficeValidation.disable();
                    selfManaged.disable();
                } else {
                    collectiveId.enable();
                    restrictiveSale.enable();
                }
            });

        combineLatest([
            this.form.get('collective.collectiveId').valueChanges,
            this.collectives$
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef),
                map(([id, collectives]) => collectives.find(el => el.id === id)),
                filter(collective => !!collective)
            ).subscribe(collective => {
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                const selfManaged = this.form.get('collective.selfManaged') as UntypedFormControl;
                if (collective.validation_method === CollectiveValidationMethod.promotionalCode
                    || collective.validation_method === CollectiveValidationMethod.user) {
                    notBoxOfficeValidation.enable();
                } else {
                    notBoxOfficeValidation.reset();
                    notBoxOfficeValidation.disable();
                }

                if (collective.type === CollectiveType.external
                    && collective.validation_method === CollectiveValidationMethod.userPassword) {
                    selfManaged.enable();
                } else {
                    selfManaged.disable();
                }
            });

        combineLatest([
            this.promotion$,
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
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

    #updateFormValues(promotion: SeasonTicketPromotion, seasonTicket: SeasonTicket): void {
        this.form.patchValue({
            notCombinable: !promotion.combinable,
            enableAlternativeChannelSurcharges: promotion.surcharges?.channel_fees || false,
            enableAlternativePromoterSurcharges: promotion.surcharges?.promoter || false,
            showDiscountNameInTicket: promotion.show_discount_name_ticket || false,
            showTicketPriceWithoutDiscount: promotion.show_ticket_price_without_discount || false,
            accessControlRestricted: promotion.access_control_restricted || false,
            validityPeriod: {
                type: promotion.validity_period?.type,
                dates: {
                    start: promotion.validity_period?.start_date || seasonTicket.start_date,
                    end: promotion.validity_period?.end_date || seasonTicket.end_date
                }
            },
            collective: {
                type: promotion.collective?.type || null,
                collectiveId: promotion.collective?.id || null,
                restrictiveSale: promotion.collective?.restrictive_sale || null,
                notBoxOfficeValidation: typeof promotion.collective?.box_office_validation === 'boolean'
                    ? !promotion.collective?.box_office_validation : null,
                selfManaged: promotion.collective?.self_managed || null
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
        this.#stPromotionsSrv.promotion.load(this.seasonTicketId, this.promotionId);
        this.#stPromotionsSrv.promotionChannels.load(this.seasonTicketId, this.promotionId);
    }

    #loadSTRates(): void {
        this.#seasonTicketService.loadSeasonTicketRates(this.seasonTicketId.toString());
        this.#stPromotionsSrv.promotionRates.load(this.seasonTicketId, this.promotionId);
    }

    #areRatesGroupsAlignedWithAssignationRates(): boolean {
        const selectedRates = this.form.value.rateTicketsGroups?.rate_ticket_groups?.map(group => group.rate) || [];
        return this.$promotionRates()?.type === 'ALL' ||
            (selectedRates.every(rateId => this.$promotionRates()?.rates?.map(rate => rate.id).includes(rateId))
                && this.$promotionRates()?.rates?.every(rate => selectedRates.includes(rate.id)));
    }

    #handleSaveSuccess(): void {
        this.#ephemeralSrv.showSaveSuccess();
        if (this.form.get('validityPeriod.type').touched ||
            this.form.get('validityPeriod.dates.start').touched ||
            this.form.get('validityPeriod.dates.end').touched) {
            this.#stPromotionsSrv.promotionsList.load(this.seasonTicketId, {
                limit: 999, offset: 0, sort: 'name:asc'
            });
        }
        this.#loadPromotionAndChannels();
        if (this.form.get('collective.presaleSale')?.value && this.form.get('collective.presaleSale')?.dirty) {
            this.#stPromotionsSrv.promotionPriceTypes.load(this.seasonTicketId, this.promotionId);
            this.#stPromotionsSrv.promotionRates.load(this.seasonTicketId, this.promotionId);
        }
    }
}

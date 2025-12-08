import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    CollectivesService, CollectiveStatus, CollectiveValidationMethod, Collective
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { PromotionCollectiveScope, PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionLimitsComponent } from '@admin-clients/cpanel-common-promotions-feature';
import { PromotionType, PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DateTimePickerComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    booleanOrMerge, FormControlHandler, dateIsAfter, dateIsBefore, dateTimeValidator
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, viewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, throwError } from 'rxjs';
import { catchError, filter, map, shareReplay, tap, withLatestFrom } from 'rxjs/operators';
import { PromotionTplChannelsComponent } from '../channels/promotion-tpl-channels.component';

@Component({
    selector: 'app-promotion-tpl-conditions',
    templateUrl: './promotion-tpl-conditions.component.html',
    styleUrls: ['./promotion-tpl-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, FormContainerComponent, ReactiveFormsModule, AsyncPipe,
        DateTimePickerComponent, PromotionTplChannelsComponent, PromotionLimitsComponent, RouterLink,
        EllipsifyDirective
    ]
})
export class PromotionTplConditionsComponent implements OnInit {
    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #collectivesService = inject(CollectivesService);
    readonly #channelsService = inject(ChannelsService);
    readonly #authService = inject(AuthenticationService);
    readonly #translate = inject(TranslateService);
    readonly #onDestroy = inject(DestroyRef);
    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly promotionTypes = PromotionType;
    readonly promotionCollectiveScope = PromotionCollectiveScope;
    readonly validityPeriodTypes = PromotionValidityPeriodType;

    errors = { savePromotionChannels: false };

    promotion$: Observable<PromotionTpl>;
    collectives$: Observable<Collective[]>;
    reqInProgress$: Observable<boolean>;
    form: UntypedFormGroup;
    promotionId: number;

    ngOnInit(): void {
        this.initForm();
        this.reqInProgress$ = booleanOrMerge([
            this.#promotionTplsSrv.isPromotionTemplateLoading$(),
            this.#promotionTplsSrv.isPromotionTemplateSaving$(),
            this.#promotionTplsSrv.isPromotionTplChannelsLoading$(),
            this.#promotionTplsSrv.isPromotionTplChannelsSaving$(),
            this.#collectivesService.isCollectiveListLoading$(),
            this.#channelsService.isChannelsListLoading$()
        ]);

        this.collectives$ = this.#collectivesService.getCollectivesListData$()
            .pipe(
                filter(collective => !!collective),
                shareReplay(1)
            );

        this.promotion$ = this.#promotionTplsSrv.getPromotionTemplate$()
            .pipe(
                filter(promotion => !!promotion),
                shareReplay(1)
            );

        this.promotion$
            .pipe(
                withLatestFrom(this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS])),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([promotion, isOperator]) => {
                if (promotion.type !== PromotionType.automatic) {
                    const req = { entity_id: undefined, status: CollectiveStatus.active, limit: 50 };
                    if (isOperator) {
                        req.entity_id = promotion.entity.id;
                    }
                    this.#collectivesService.fetchCollectives(req);
                }
                this.promotionId = promotion.id;
                this.enableFieldsByPromoType(promotion.type);
                this.enableFieldsByPresaleValue(promotion.presale);
                this.initFormChangesHandlers();
                this.updateFormValues(promotion);
            });

    }

    cancel(): void {
        this.loadPromotionAndChannels();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const data = this.form.value;
            const req: PromotionTpl = {
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
                        ? !data.collective.notBoxOfficeValidation : undefined
                },
                presale: data?.collective?.presaleSale,
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
                }
            };
            const updateObs = [this.#promotionTplsSrv.savePromotionTemplate(this.promotionId, req)];
            const channelsForm = this.form.get('channels');
            if (channelsForm.dirty) {
                this.errors.savePromotionChannels = false;
                const request: PutPromotionChannels = {
                    type: channelsForm.value.type,
                    channels: channelsForm.value.selected?.map(idName => idName.id) || []
                };
                updateObs.push(
                    this.#promotionTplsSrv.savePromotionTplChannels(this.promotionId, request)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionChannels = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            return forkJoin(updateObs)
                .pipe(tap(() => {
                    this.#ephemeralMsg.showSaveSuccess();
                    this.loadPromotionAndChannels();
                }));

        } else {
            this.form.markAllAsTouched();
            this.form.get('channels.selected').updateValueAndValidity();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
            return throwError(() => 'invalid fields');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    private initForm(): void {
        this.form = this.#fb.group({
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
                })
            }),
            collective: this.#fb.group({ // only non auto
                type: [{ value: null, disabled: true }, Validators.required],
                collectiveId: [{ value: null, disabled: true }, Validators.required],
                restrictiveSale: { value: false, disabled: true },
                notBoxOfficeValidation: { value: false, disabled: true },
                presaleSale: { value: false, disabled: true }
            }),
            channels: this.#fb.group({
                type: [null, Validators.required],
                selected: [{ value: [], disabled: true }, Validators.required]
            })
        });

        const startDate = this.form.get('validityPeriod.dates.start');
        const endDate = this.form.get('validityPeriod.dates.end');

        startDate.addValidators(
            dateTimeValidator(
                dateIsBefore, 'startAfterEnd', endDate,
                this.#translate.instant('DATES.END_DATE').toLowerCase()
            )
        );

        endDate.addValidators(
            dateTimeValidator(
                dateIsAfter, 'startAfterEnd', startDate,
                this.#translate.instant('DATES.END_DATE').toLowerCase()
            )
        );
    }

    private enableFieldsByPromoType(type: PromotionType): void {
        if (type === PromotionType.automatic) {
            this.form.get('notCombinable').enable();
            this.form.get('collective.type').disable();
            this.form.get('showDiscountNameInTicket').disable();
        } else {
            this.form.get('showDiscountNameInTicket').enable();
            this.form.get('notCombinable').disable();
            this.form.get('collective.type').enable();
        }
    }

    private enableFieldsByPresaleValue(isPresale: boolean): void {
        const validityPeriod = this.form.get('validityPeriod.type') as UntypedFormControl;
        if (isPresale) {
            validityPeriod.disable();
            validityPeriod.setValue(PromotionValidityPeriodType.event);
        } else {
            validityPeriod.enable();
        }
    }

    private initFormChangesHandlers(): void {
        this.form.get('validityPeriod.type').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
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
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe((scope: PromotionCollectiveScope) => {
                const collectiveId = this.form.get('collective.collectiveId') as UntypedFormControl;
                const restrictiveSale = this.form.get('collective.restrictiveSale') as UntypedFormControl;
                const presaleSale = this.form.get('collective.presaleSale') as UntypedFormControl;
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                if (!scope || scope === PromotionCollectiveScope.none) {
                    collectiveId.disable();
                    restrictiveSale.disable();
                    notBoxOfficeValidation.disable();
                    presaleSale.disable();
                } else {
                    collectiveId.enable();
                    restrictiveSale.enable();
                }
            });
        this.form.get('collective.restrictiveSale').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                const presaleSale = this.form.get('collective.presaleSale') as UntypedFormControl;
                if (value) {
                    presaleSale.enable();
                } else {
                    presaleSale.disable();
                    presaleSale.setValue(null);
                }
            });

        this.form.get('collective.presaleSale').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(isPresale => this.enableFieldsByPresaleValue(isPresale));

        combineLatest([
            this.form.get('collective.collectiveId').valueChanges,
            this.collectives$
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy),
                map(([id, collectives]) => collectives.find(el => el.id === id)),
                filter(collective => !!collective)
            ).subscribe(collective => {
                const notBoxOfficeValidation = this.form.get('collective.notBoxOfficeValidation') as UntypedFormControl;
                if (collective.validation_method === CollectiveValidationMethod.promotionalCode
                    || collective.validation_method === CollectiveValidationMethod.user) {
                    notBoxOfficeValidation.enable();
                } else {
                    notBoxOfficeValidation.reset();
                    notBoxOfficeValidation.disable();
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
            });
    }

    private updateFormValues(promotion: PromotionTpl): void {
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
                    start: promotion.validity_period?.start_date || null,
                    end: promotion.validity_period?.end_date || null
                }
            },
            collective: {
                type: promotion.collective?.type || null,
                collectiveId: promotion.collective?.id || null,
                restrictiveSale: promotion.collective?.restrictive_sale || null,
                notBoxOfficeValidation: typeof promotion.collective?.box_office_validation === 'boolean'
                    ? !promotion.collective?.box_office_validation : null,
                presaleSale: promotion?.presale || null
            }
        });
        this.form.markAsPristine();
    }

    private loadPromotionAndChannels(): void {
        this.#promotionTplsSrv.loadPromotionTemplate(this.promotionId);
        this.#promotionTplsSrv.loadPromotionTplChannels(this.promotionId);
    }
}

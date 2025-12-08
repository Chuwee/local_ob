import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventPromotion, EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { PromotionDiscountType, PromotionStatus, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, NavTabMenuElement, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, of } from 'rxjs';
import { delay, filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

const menuElements: NavTabMenuElement[] = [
    {
        label: 'EVENTS.PROMOTIONS.GENERAL_DATA',
        param: 'general-data'
    },
    {
        label: 'EVENTS.PROMOTIONS.LIMITS_AND_CONDITIONS',
        param: 'conditions'
    },
    {
        label: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE',
        param: 'discount-type'
    },
    {
        label: 'EVENTS.PROMOTIONS.ZONES',
        param: 'zones'
    }
];

@Component({
    selector: 'app-event-promotion-details',
    templateUrl: './event-promotion-details.component.html',
    styleUrls: ['./event-promotion-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterOutlet, MaterialModule, NavTabsMenuComponent, ReactiveFormsModule,
        TranslatePipe, NgClass, NgIf, AsyncPipe
    ]
})
export class EventPromotionDetailsComponent implements OnInit, OnDestroy {
    private readonly _eventsSrv = inject(EventsService);
    private readonly _eventSessionsSrv = inject(EventSessionsService);
    private readonly _eventPromoSrv = inject(EventPromotionsService);
    private readonly _venueTemplatesSrv = inject(VenueTemplatesService);
    private readonly _ephemeralMdgSrv = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _fb = inject(FormBuilder);
    private readonly _destroyRef = inject(DestroyRef);

    private _eventId: number;
    private _promotionId: number;
    private _childComponent: WritingComponent;

    readonly elements = menuElements;
    readonly promotion$ = this._eventPromoSrv.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            withLatestFrom(this._eventsSrv.event.get$()),
            tap(([promotion, event]) => {
                this._eventId = event.id;
                this._promotionId = promotion.id;
            }),
            map(([promo]) => mapPromoVirtualStatus()(promo)),
            takeUntilDestroyed(),
            shareReplay(1)
        );

    readonly statusCtrl = this._fb.control({ value: null, disabled: true });
    readonly $disabledDueToStatus = toSignal(this._eventsSrv.event.get$().pipe(
        first(Boolean),
        map(event => event.additional_config?.inventory_provider === ExternalInventoryProviders.italianCompliance &&
            event.status !== EventStatus.inProgramming
        )
    ));

    ngOnInit(): void {
        combineLatest([
            this._eventPromoSrv.promotion.get$(),
            combineLatest([
                this._eventPromoSrv.promotionChannels.get$(),
                this._eventPromoSrv.promotionSessions.get$(),
                this._eventPromoSrv.promotionPriceTypes.get$(),
                this._eventPromoSrv.promotionRates.get$()
            ]),
            this._eventPromoSrv.promotionChannelTextContents.get$(),
            this._eventsSrv.event.get$().pipe(first(Boolean))
        ]).pipe(
            filter(([promo, submodels, contents, event]) =>
                [promo, ...submodels, contents, event.settings?.languages?.selected].every(model => !!model)),
            takeUntilDestroyed(this._destroyRef)
        ).subscribe(([promo, submodels, contents, event]) => {
            const langs = event.settings?.languages?.selected;
            // Más adelante será un flag más genérico y el backend se encargará de hacer estas comprobaciones
            const isItalianCompliance = event.additional_config?.inventory_provider === ExternalInventoryProviders.italianCompliance;
            const isInProgramming = event.status === EventStatus.inProgramming;
            const isActive = promo.status !== PromotionStatus.inactive;
            this.statusCtrl.patchValue(isActive);
            const submodelsToCheck: { type: string }[] = [promo.discount, promo.validity_period, ...submodels];

            const discount = promo.discount;

            if (promo.type !== PromotionType.automatic) {
                submodelsToCheck.push(promo.collective);
            }
            if (
                submodelsToCheck.some(model => !model?.type) ||
                contents.length < langs.length ||
                (discount.type !== PromotionDiscountType.noDiscount &&
                    (discount.value === 0 && promo.type === PromotionType.automatic)
                ) ||
                (discount.type === PromotionDiscountType.basePrice &&
                    (!discount.ranges?.length ||
                        discount.ranges.some(range => range.value < 0 || range.value === null))
                ) ||
                (isItalianCompliance && !isInProgramming)
            ) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this._eventPromoSrv.promotion.clear();
        this._eventPromoSrv.clearNestedPromotionData();
        this._venueTemplatesSrv.clearVenueTemplateList();
        this._venueTemplatesSrv.clearGroupedVenueTemplatePriceTypes();
        this._eventsSrv.eventRates.clear();
        this._eventSessionsSrv.sessionList.clear();
    }

    handleStatusChange(isActive: boolean): void {
        if (this._childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() =>
                    this._msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted =>
                    saveAccepted ? this._childComponent.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.saveStatus(isActive);
        }
    }

    childComponentChange(component: WritingComponent): void {
        this._childComponent = component;
    }

    private saveStatus(isActive: boolean): void {
        const status = isActive ? PromotionStatus.active : PromotionStatus.inactive;
        const promotion: EventPromotion = { status };
        this._eventPromoSrv.promotion.update(this._eventId, this._promotionId, promotion)
            .subscribe({
                complete: () => {
                    this._eventPromoSrv.promotion.load(this._eventId, this._promotionId);
                    this._eventPromoSrv.promotionsList.load(this._eventId, { limit: 999, offset: 0, sort: 'name:asc' });
                    this._eventPromoSrv.promotionChannels.load(this._eventId, this._promotionId);
                    this._ephemeralMdgSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }
}

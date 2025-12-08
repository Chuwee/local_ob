import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotion
    , SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { PromotionDiscountType, PromotionStatus, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import {
    DialogSize,
    EphemeralMessageService, MessageDialogConfig, MessageDialogService,
    NavTabMenuElement,
    NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
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
        label: 'SEASON_TICKET.PROMOTIONS.GENERAL_DATA',
        param: 'general-data'
    },
    {
        label: 'SEASON_TICKET.PROMOTIONS.LIMITS_AND_CONDITIONS',
        param: 'conditions'
    },
    {
        label: 'SEASON_TICKET.PROMOTIONS.DISCOUNT_TYPE',
        param: 'discount-type'
    },
    {
        label: 'SEASON_TICKET.PROMOTIONS.ZONES',
        param: 'zones'
    }
];

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NavTabsMenuComponent,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        RouterOutlet,
        CommonModule
    ],
    selector: 'app-season-ticket-promotion-details',
    templateUrl: './season-ticket-promotion-details.component.html',
    styleUrls: ['./season-ticket-promotion-details.component.scss']
})
export class SeasonTicketPromotionDetailsComponent implements OnInit, OnDestroy {
    private readonly _stSrv = inject(SeasonTicketsService);
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _venueTemplatesSrv = inject(VenueTemplatesService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _fb = inject(FormBuilder);
    private readonly _destroyRef = inject(DestroyRef);

    private _stId: number;
    private _promotionId: number;
    private _childComponent: WritingComponent;

    readonly elements = menuElements;
    readonly promotion$ = this._stPromotionsSrv.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            withLatestFrom(this._stSrv.seasonTicket.get$()),
            tap(([promotion, seasonTicket]) => {
                this._stId = seasonTicket.id;
                this._promotionId = promotion.id;
            }),
            map(([promo]) => mapPromoVirtualStatus()(promo)),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

    readonly statusCtrl = this._fb.control({ value: null, disabled: true });

    ngOnInit(): void {
        // status ctrl activation logic
        combineLatest([
            this._stPromotionsSrv.promotion.get$(),
            combineLatest([
                this._stPromotionsSrv.promotionChannels.get$(),
                this._stPromotionsSrv.promotionPriceTypes.get$(),
                this._stPromotionsSrv.promotionRates.get$()
            ]),
            this._stPromotionsSrv.promotionChannelTextContents.get$(),
            this._stSrv.seasonTicket.get$().pipe(first(Boolean), map(st => st.settings.languages.selected))
        ]).pipe(
            filter(([promo, submodels, contents, langs]) =>
                [promo, ...submodels, contents, langs].every(model => !!model)),
            takeUntilDestroyed(this._destroyRef)
        ).subscribe(([promo, submodels, contents, langs]) => {
            const isActivePromo = promo.status !== PromotionStatus.inactive;
            this.statusCtrl.patchValue(isActivePromo);
            const submodelsToCheck: { type: string }[] = [promo.discount, promo.validity_period, ...submodels];
            if (promo.type !== PromotionType.automatic) {
                submodelsToCheck.push(promo.collective);
            }
            if (
                submodelsToCheck.some(model => !model?.type) ||
                contents.length < langs.length ||
                promo.discount.value === 0 ||
                (promo.discount.type === PromotionDiscountType.basePrice &&
                    promo.discount.ranges.every(range => range.value === 0))
            ) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this._stPromotionsSrv.promotion.clear();
        this._stPromotionsSrv.clearNestedPromotionData();
        this._venueTemplatesSrv.clearVenueTemplateList();
        this._venueTemplatesSrv.clearGroupedVenueTemplatePriceTypes();
        this._stSrv.clearSeasonTicketRates();
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

    private saveStatus(isActivePromo: boolean): void {
        const status = isActivePromo ? PromotionStatus.active : PromotionStatus.inactive;
        const promotion: SeasonTicketPromotion = { status };
        this._stPromotionsSrv.promotion.update(this._stId, this._promotionId, promotion)
            .subscribe({
                complete: () => {
                    this._stPromotionsSrv.promotion.load(this._stId, this._promotionId);
                    this._stPromotionsSrv.promotionsList.load(this._stId, {
                        limit: 999, offset: 0, sort: 'name:asc'
                    });
                    this._stPromotionsSrv.promotionChannels.load(this._stId, this._promotionId);
                    this._ephemeralMessageSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActivePromo)
            });
    }
}

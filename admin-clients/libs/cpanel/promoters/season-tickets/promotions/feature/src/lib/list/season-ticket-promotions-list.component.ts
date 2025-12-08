import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import { MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    inject,
    OnDestroy,
    OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import {
    debounceTime, filter, first, map, switchMap, take
} from 'rxjs/operators';
import { NewPromotionDialogComponent } from '../create/new-promotion-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        EllipsifyDirective,
        DateTimePipe,
        CommonModule
    ],
    selector: 'app-season-ticket-promotions-list',
    templateUrl: './season-ticket-promotions-list.component.html',
    styleUrls: ['./season-ticket-promotions-list.component.scss']
})
export class SeasonTicketPromotionsListComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketsSrv = inject(SeasonTicketsService);
    private readonly _stPromotionSrv = inject(SeasonTicketPromotionsService);
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);
    private readonly _matDialog = inject(MatDialog);
    private readonly _msgDialog = inject(MessageDialogService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _destroyRef = inject(DestroyRef);

    private _entityId: number;
    private _seasonTicketId: number;

    // route promotion id (could be undefined if not present)
    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0]?.params['promotionId'];
    }

    // gets the inner path (tab route) if found
    private get _innerPath(): string {
        return this._route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    private get _currentPath(): string {
        return this._innerPath ?
            this.selectedStPromotion.toString() + '/' + this._innerPath :
            this.selectedStPromotion.toString();
    }

    readonly loadingList$ = this._stPromotionSrv.promotionsList.loading$();
    readonly promotionList$ = this._stPromotionSrv.promotionsList.getData$()
        .pipe(
            filter(Boolean),
            map(promotions => promotions.map(mapPromoVirtualStatus()))
        );

    readonly totalPromotions$ = this._stPromotionSrv.promotionsList.getMetadata$()
        .pipe(map(md => md ? md.total : 0));

    readonly seasonTicketPromotion$ = this._stPromotionSrv.promotion.get$()
        .pipe(filter(Boolean));

    readonly canCreate$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]);
    readonly dateTimeFormats = DateTimeFormats;

    selectedStPromotion: number;

    ngOnInit(): void {
        this.loadSeasonTicketPromotionList();
        combineLatest([
            this._stPromotionSrv.promotion.get$(),
            this._stPromotionSrv.promotion.error$()
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promotion, error]) => {
                this.selectedStPromotion = error || !promotion ? null : promotion.id;
            });

        // scoll to the selected element
        this._stPromotionSrv.promotionsList.getData$()
            .pipe(
                filter(promotionList => !!promotionList?.length),
                debounceTime(500),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(() => {
                const promo = this.selectedStPromotion;
                const element = document.getElementById('promotion-list-option-' + promo);
                return element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });

        this._stPromotionSrv.promotionsList.getData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(promos => {
                if (!promos.length) {
                    this._stPromotionSrv.promotion.clear();
                    this._router.navigate(['.'], { relativeTo: this._route });
                } else if (!this._idPath) {
                    this._router.navigate([promos.at(0).id], { relativeTo: this._route });
                }
            });
    }

    ngOnDestroy(): void {
        this._stPromotionSrv.cancelRequests();
        this._stPromotionSrv.promotion.clear();
        this._stPromotionSrv.promotionsList.clear();
    }

    selectionChangeHandler(promotionId: number): void {
        this.selectedStPromotion = promotionId;
        this._router.navigate([this._currentPath], { relativeTo: this._route });
    }

    openNewPromotionDialog(): void {
        this._matDialog.open(NewPromotionDialogComponent, new ObMatDialogConfig({
            entityId: this._entityId,
            seasonTicketId: this._seasonTicketId
        }))
            .beforeClosed()
            .pipe(filter(id => !!id))
            .subscribe((promotionId: number) => {
                this._ephemeralMessageSrv.showSuccess({ msgKey: 'SEASON_TICKET.PROMOTIONS.CREATE_SUCCESS' });
                this.loadSeasonTicketPromotionList();
                this.selectionChangeHandler(promotionId);
            });
    }

    openDeletePromotionDialog(): void {
        this._stPromotionSrv.promotion.get$().pipe(
            take(1),
            filter(promotion => !!promotion),
            switchMap(promotion =>
                this._msgDialog.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_PROMOTION',
                    message: 'SEASON_TICKET.DELETE_PROMOTION_WARNING',
                    messageParams: { name: promotion.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })
                    .pipe(map(accepted => ({ accepted, promotion })))
            ),
            filter(({ accepted }) => !!accepted),
            switchMap(({ promotion }) =>
                this._stPromotionSrv.promotion.delete(this._seasonTicketId, promotion.id)
                    .pipe(map(_ => promotion))
            )
        )
            .subscribe(promotion => {
                this._ephemeralMessageSrv.showSuccess({
                    msgKey: 'SEASON_TICKET.PROMOTIONS.DELETE_SUCCESS',
                    msgParams: promotion
                });
                this._router.navigate(['.'], { relativeTo: this._route });
                this.loadSeasonTicketPromotionList();
            });
    }

    openClonePromotionDialog(promotionId: number): void {
        this._msgDialog.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CLONE_PROMOTION',
            message: 'SEASON_TICKET.CLONE_PROMOTION_WARNING',
            messageParams: {},
            actionLabel: 'FORMS.ACTIONS.YES',
            showCancelButton: true
        })
            .pipe(
                filter(success => !!success),
                switchMap(() => this._stPromotionSrv.promotion.clone(this._seasonTicketId, promotionId))
            )
            .subscribe(id => {
                this.loadSeasonTicketPromotionList();
                this.selectionChangeHandler(id);
            });
    }

    private loadSeasonTicketPromotionList(): void {
        this._seasonTicketsSrv.seasonTicket.get$()
            .pipe(first(Boolean))
            .subscribe(seasonTicket => {
                this._entityId = seasonTicket.entity.id;
                this._seasonTicketId = seasonTicket.id;
                this._stPromotionSrv.promotionsList.load(seasonTicket.id, {
                    limit: 999, offset: 0, sort: 'name:asc'
                });
            });
    }
}

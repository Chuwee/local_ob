import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventPromotionListElement,
    EventPromotionsService
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import { MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import {
    debounceTime, filter, first, map, switchMap
} from 'rxjs/operators';
import { NewEventPromotionDialogComponent } from '../create/new-event-promotion-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        DateTimePipe,
        EllipsifyDirective,
        CommonModule
    ],
    selector: 'app-event-promotions-list',
    templateUrl: './event-promotions-list.component.html',
    styleUrls: ['./event-promotions-list.component.scss']
})
export class EventPromotionsListComponent implements OnInit, OnDestroy {
    private readonly _eventsSrv = inject(EventsService);
    private readonly _eventPromotionSrv = inject(EventPromotionsService);
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);
    private readonly _matDialog = inject(MatDialog);
    private readonly _msgDialog = inject(MessageDialogService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _destroyRef = inject(DestroyRef);

    private _entityId: number;
    private _eventId: number;

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
            this.selectedEventPromotion.toString() + '/' + this._innerPath :
            this.selectedEventPromotion.toString();
    }

    readonly loadingList$ = this._eventPromotionSrv.promotionsList.loading$();
    readonly promotionList$: Observable<EventPromotionListElement[]> = this._eventPromotionSrv.promotionsList.getData$()
        .pipe(
            filter(Boolean),
            map(promotions => promotions.map(mapPromoVirtualStatus()))
        );

    readonly totalPromotions$ = this._eventPromotionSrv.promotionsList.getMetadata$()
        .pipe(map(md => md ? md.total : 0));

    readonly eventPromotion$ = this._eventPromotionSrv.promotion.get$()
        .pipe(filter(Boolean));

    readonly canCreate$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]);
    readonly dateTimeFormats = DateTimeFormats;

    selectedEventPromotion: number;

    ngOnInit(): void {
        this.loadEventPromotionList();
        combineLatest([
            this._eventPromotionSrv.promotion.get$(),
            this._eventPromotionSrv.promotion.error$()
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promotion, error]) => {
                this.selectedEventPromotion = error || !promotion ? null : promotion.id;
            });

        // scroll to the selected element
        this._eventPromotionSrv.promotionsList.getData$()
            .pipe(
                filter(promotionList => !!promotionList?.length),
                debounceTime(500),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(() => {
                const promo = this.selectedEventPromotion;
                const element = document.getElementById('promotion-list-option-' + promo);
                return element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });

        this._eventPromotionSrv.promotionsList.getData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(promos => {
                if (!promos.length) {
                    this._eventPromotionSrv.promotion.clear();
                    this._router.navigate(['.'], { relativeTo: this._route });
                } else if (!this._idPath) {
                    this._router.navigate([promos.at(0).id], { relativeTo: this._route });
                }
            });

    }

    ngOnDestroy(): void {
        this._eventPromotionSrv.cancelRequests();
        this._eventPromotionSrv.promotion.clear();
        this._eventPromotionSrv.promotionsList.clear();
    }

    selectionChangeHandler(promotionId: number): void {
        this.selectedEventPromotion = promotionId;
        this._router.navigate([this._currentPath], { relativeTo: this._route });
    }

    openNewPromotionDialog(): void {
        this._matDialog.open(NewEventPromotionDialogComponent, new ObMatDialogConfig({
            entityId: this._entityId,
            eventId: this._eventId
        }))
            .beforeClosed()
            .pipe(filter(id => !!id))
            .subscribe((promotionId: number) => {
                this._ephemeralMessageSrv.showSuccess({ msgKey: 'EVENTS.PROMOTIONS.CREATE_SUCCESS' });
                this.loadEventPromotionList();
                this.selectionChangeHandler(promotionId);
            });
    }

    openDeletePromotionDialog(): void {
        this._eventPromotionSrv.promotion.get$()
            .pipe(
                first(),
                switchMap(promotion =>
                    this._msgDialog.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_PROMOTION',
                        message: 'EVENTS.DELETE_PROMOTION_WARNING',
                        messageParams: { name: promotion.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                        .pipe(map(accepted => ({ accepted, promotion })))
                ),
                filter(({ accepted }) => !!accepted),
                switchMap(({ promotion }) =>
                    this._eventPromotionSrv.promotion.delete(this._eventId, promotion.id)
                        .pipe(map(_ => promotion))
                )
            )
            .subscribe(promotion => {
                this._ephemeralMessageSrv.showSuccess({
                    msgKey: 'EVENTS.PROMOTIONS.DELETE_SUCCESS',
                    msgParams: promotion
                });
                this._router.navigate(['.'], { relativeTo: this._route });
                this.loadEventPromotionList();
            });
    }

    openClonePromotionDialog(promotionId: number): void {
        this._msgDialog.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CLONE_PROMOTION',
            message: 'EVENTS.CLONE_PROMOTION_WARNING',
            messageParams: {},
            actionLabel: 'FORMS.ACTIONS.YES',
            showCancelButton: true
        })
            .pipe(
                filter(success => !!success),
                switchMap(() => this._eventPromotionSrv.promotion.clone(this._eventId, promotionId))
            )
            .subscribe(id => {
                this.loadEventPromotionList();
                this.selectionChangeHandler(id);
            });
    }

    private loadEventPromotionList(): void {
        this._eventsSrv.event.get$()
            .pipe(first(Boolean))
            .subscribe(event => {
                this._entityId = event.entity.id;
                this._eventId = event.id;
                this._eventPromotionSrv.promotionsList.load(event.id, {
                    limit: 999, offset: 0, sort: 'name:asc'
                });
            });
    }
}

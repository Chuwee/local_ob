import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    ChannelPromotionsService
} from '@admin-clients/cpanel-channels-promotions-data-access';
import { PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import { MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import {
    debounceTime, filter, first, map, shareReplay, startWith, switchMap, take, tap
} from 'rxjs/operators';
import { NewPromotionDialogComponent } from '../create/new-promotion-dialog.component';

@Component({
    selector: 'app-channel-promotions-list',
    templateUrl: './channel-promotions-list.component.html',
    styleUrls: ['./channel-promotions-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionsListComponent implements OnInit, OnDestroy {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelPromotionSrv = inject(ChannelPromotionsService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialog = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #createRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];
    #entityId: number;
    #channelId: number;

    // route promotion id (could be undefined if not present)
    get #idPath(): string | undefined {
        return this.#route.snapshot.children[0]?.params['promotionId'];
    }

    // gets the inner path (tab route) if found
    get #innerPath(): string {
        return this.#route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    readonly loadingList$ = this.#channelPromotionSrv.isPromotionsListInProgress$();
    readonly promotionList$ = this.#channelPromotionSrv.getPromotionsListData$()
        .pipe(
            filter(Boolean),
            map(promotions => promotions.map(mapPromoVirtualStatus())),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

    readonly totalPromotions$ = this.#channelPromotionSrv.getPromotionsListMetaData$()
        .pipe(map(md => md ? md.total : 0));

    readonly channelPromotion$ = this.#channelPromotionSrv.getPromotion$()
        .pipe(
            filter(Boolean),
            shareReplay(1)
        );

    readonly canCreate$ = this.#authService.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, this.#createRoles))
        );

    readonly dateTimeFormats = DateTimeFormats;
    readonly validityPeriodType = PromotionValidityPeriodType;

    selectedPromotionId: number;
    selectedPromotionType: string;

    ngOnInit(): void {
        this.loadChannelPromotionList();

        combineLatest([
            this.#channelPromotionSrv.getPromotion$(),
            this.#channelPromotionSrv.getPromotionError$()
        ]).pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([promotion, error]) => {
                this.selectedPromotionId = error || !promotion ? null : promotion.id;
                this.selectedPromotionType = this.selectedPromotionId ? promotion.subtype : null;
                if (this.selectedPromotionId && this.#idPath !== this.selectedPromotionId.toString()) {
                    const path = this.currentPath();
                    this.#router.navigate([path], { relativeTo: this.#route });
                }
            });

        // scoll to the selected element
        this.promotionList$
            .pipe(
                filter(promotionList => !!promotionList.length),
                debounceTime(500), takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(() => {
                const promo = this.selectedPromotionId;
                const element = document.getElementById('promotion-list-option-' + promo);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });

        // if there is no promotion loaded and there is at least one promo in the list, navigate to the first one
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd)
        ).pipe(
            switchMap(() => this.promotionList$),
            filter(promotionList => !this.#idPath && !!promotionList.length),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([firstPromotion]) => {
            this.#router.navigate([firstPromotion.id], { relativeTo: this.#route });
        });

        this.promotionList$.pipe(
            tap(promotionList => {
                if (!promotionList.length) {
                    this.#channelPromotionSrv.clearPromotion();
                    setTimeout(() =>
                        this.#router.navigate(['.'], { relativeTo: this.#route })
                    );
                }
            }),
            filter(promotionList =>
                !!promotionList.length &&
                this.#idPath &&
                !promotionList.find(element => element.id.toString() === this.#idPath)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([firstPromotion]) => {
            this.#router.navigate([firstPromotion.id], { relativeTo: this.#route });
        });
    }

    ngOnDestroy(): void {
        this.#channelPromotionSrv.cancelRequests();
        this.#channelPromotionSrv.clearPromotion();
        this.#channelPromotionSrv.clearPromotionsList();
    }

    selectionChangeHandler(promotionId: number): void {
        if (!!promotionId && this.selectedPromotionId !== promotionId) {
            this.selectedPromotionId = promotionId;
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#route });
        }
    }

    openNewPromotionDialog(): void {
        this.#matDialog.open(NewPromotionDialogComponent, new ObMatDialogConfig({
            entityId: this.#entityId,
            channelId: this.#channelId
        })).beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((promotionId: number) => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'CHANNELS.PROMOTIONS.CREATE_SUCCESS'
                });
                this.loadChannelPromotionList();
                this.selectionChangeHandler(promotionId);
            });
    }

    openDeletePromotionDialog(): void {
        this.#channelPromotionSrv.getPromotion$().pipe(
            take(1),
            filter(Boolean),
            switchMap(promotion =>
                this.#msgDialog.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_PROMOTION',
                    message: 'CHANNELS.PROMOTIONS.DELETE_PROMOTION_WARNING',
                    messageParams: { name: promotion.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })
                    .pipe(map(accepted => ({ accepted, promotion })))
            ),
            filter(({ accepted }) => !!accepted),
            switchMap(({ promotion }) =>
                this.#channelPromotionSrv.deletePromotion(this.#channelId, promotion.id)
                    .pipe(map(_ => promotion))
            )
        ).subscribe(promotion => {
            this.#ephemeralMessageService.showSuccess({
                msgKey: 'CHANNELS.PROMOTIONS.DELETE_SUCCESS',
                msgParams: promotion
            });
            this.loadChannelPromotionList();
        });
    }

    openClonePromotionDialog(promotionId: number): void {
        this.#msgDialog.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CLONE_PROMOTION',
            message: 'CHANNELS.PROMOTIONS.CLONE_PROMOTION_WARNING',
            messageParams: {},
            actionLabel: 'FORMS.ACTIONS.YES',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#channelPromotionSrv.clonePromotion(this.#channelId, promotionId))
            ).subscribe(id => {
                this.loadChannelPromotionList();
                this.selectionChangeHandler(id);
            });
    }

    private currentPath(): string {
        return this.#innerPath ?
            this.selectedPromotionId.toString() + '/' + this.#innerPath :
            this.selectedPromotionId.toString();
    }

    private loadChannelPromotionList(): void {
        this.#channelsService.getChannel$()
            .pipe(first(channel => channel !== null))
            .subscribe(channel => {
                this.#entityId = channel.entity.id;
                this.#channelId = channel.id;
                this.#channelPromotionSrv.loadPromotionsList(channel.id, {
                    limit: 999, offset: 0, sort: 'name:asc'
                });
            });
    }
}

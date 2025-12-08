/* eslint-disable @typescript-eslint/dot-notation */
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, debounceTime, filter, first, map, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { AddProductChannelDialogComponent } from '../add/add-product-channel-dialog.component';
import { getSaleStatusIndicator } from '../product-channel-status-mapping-function';

@Component({
    selector: 'app-product-channels-list',
    imports: [
        CommonModule, TranslatePipe, MaterialModule,
        LastPathGuardListenerDirective, EllipsifyDirective
    ],
    templateUrl: './product-channels-list.component.html',
    styleUrls: ['./product-channels-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelsListComponent implements OnInit {
    readonly #productsSrv = inject(ProductsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #cdRef = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);

    get #idPath(): string | undefined {
        return this.#activatedRoute.snapshot.children[0]?.params['channelId'];
    }

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isLoading$ = this.#productsSrv.product.channelsList.inProgress$();
    readonly $productStatus = toSignal(this.#productsSrv.product.get$().pipe(filter(Boolean), map(product => product.product_state)));

    readonly productChannels$ = this.#productsSrv.product.channelsList.get$()
        .pipe(
            filter(Boolean),
            map(productChannel => productChannel.map(productChannel => ({
                ...productChannel,
                /* In the future, if products continue to grow, we will need a status object (productChannel.status) as
                in event-channel and we will have to rethink literal keys */
                saleStatusIndicator: getSaleStatusIndicator(productChannel.sale_request_status, this.$productStatus())
            }))),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $productId = toSignal(this.#productsSrv.product.get$().pipe(map(product => product.product_id)));

    selectedProductChannelId: number;

    ngOnInit(): void {
        this.handleSelectedChannelChanges();
        this.handleRouterChannelsChanges();
        this.handleProductChannelsChangesForScroll();
        this.handleProductChannelsChangesToNavigate();
    }

    openAddChannelsDialog(): void {
        this.#matDialog.open<AddProductChannelDialogComponent>(
            AddProductChannelDialogComponent, new ObMatDialogConfig({ productId: this.$productId() })
        )
            .beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'PRODUCT.CHANNELS.ADD_CHANNELS_SUCCESS' });
                    this.reloadChannels();
                }
            });
    }

    openDeleteChannelDialog(): void {
        this.#productsSrv.product.channel.get$()
            .pipe(
                first(Boolean),
                switchMap(productChannel => this.#msgDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_CHANNEL',
                    message: 'PRODUCT.CHANNELS.DELETE_CHANNEL_WARNING',
                    messageParams: { channelName: productChannel.channel.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })),
                filter(Boolean),
                switchMap(() => {
                    const productId = this.$productId();
                    return this.#productsSrv.product.channelsList.delete(productId, Number(this.#idPath));
                })
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'PRODUCT.EVENTS.DELETE_PRODUCT_EVENT_SUCCESS'
                });
                this.reloadChannels();
            });
    }

    selectionChangeHandler(productChannelId: number): void {
        if (!!productChannelId && this.selectedProductChannelId !== productChannelId) {
            this.selectedProductChannelId = productChannelId;
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#activatedRoute });
        }
    }

    private reloadChannels(): void {
        this.#productsSrv.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productsSrv.product.channelsList.load(product.product_id);
        });
    }

    private currentPath(): string {
        return this._innerPath ?
            this.selectedProductChannelId.toString() + '/' + this._innerPath :
            this.selectedProductChannelId.toString();
    }

    private handleSelectedChannelChanges(): void {
        combineLatest([
            this.#productsSrv.product.channel.get$(),
            this.#productsSrv.product.channel.error$()
        ])
            .pipe(
                filter(([productChannel, error]) => !!productChannel || !!error),
                takeUntilDestroyed(this.#destroyRef))
            .subscribe(([productChannel, error]) => {
                this.selectedProductChannelId = error || !productChannel ? null : productChannel.channel.id;
                this.#cdRef.markForCheck();
            });
    }

    private handleProductChannelsChangesForScroll(): void {
        this.productChannels$
            .pipe(
                filter(productChannels => !!productChannels.length),
                debounceTime(500), takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(() => {
                const channel = this.selectedProductChannelId;
                const element = document.getElementById('promotion-list-option-' + channel);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
    }

    private handleRouterChannelsChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.productChannels$),
            filter(channelsList => !this.#idPath && !!channelsList.length),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([firstChannel]) => {
            this.#router.navigate([firstChannel.channel.id], { relativeTo: this.#activatedRoute });
        });
    }

    private handleProductChannelsChangesToNavigate(): void {
        this.productChannels$
            .pipe(
                tap(channelsList => {
                    if (!channelsList.length) {
                        setTimeout(() =>
                            this.#router.navigate(['.'], { relativeTo: this.#activatedRoute })
                        );
                    }
                }),
                filter(channelsList =>
                    !!channelsList.length &&
                    this.#idPath &&
                    !channelsList.find(element => element.channel.id.toString() === this.#idPath)),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(([firstChannel]) => {
                this.#router.navigate([firstChannel.channel.id], { relativeTo: this.#activatedRoute });
            });
    }

    private get _innerPath(): string {
        return this.#activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

}

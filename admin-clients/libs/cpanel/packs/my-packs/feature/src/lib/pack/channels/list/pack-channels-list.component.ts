import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { getPackSaleStatusIndicator, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, debounceTime, filter, first, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { AddPackChannelDialogComponent } from '../add/add-pack-channel-dialog.component';

@Component({
    selector: 'app-pack-channels-list',
    imports: [
        CommonModule, TranslatePipe, MaterialModule, FlexModule, FlexLayoutModule,
        LastPathGuardListenerDirective, EllipsifyDirective
    ],
    templateUrl: './pack-channels-list.component.html',
    styleUrls: ['./pack-channels-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackChannelsListComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #cdRef = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly getSaleStatusIndicator = getPackSaleStatusIndicator;

    get #idPath(): string | undefined {
        return this.#activatedRoute.snapshot.children[0]?.params['channelId'];
    }

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isLoading$ = this.#packsSrv.pack.channels.loading$();

    readonly packChannels$ = this.#packsSrv.pack.channels.getData$()
        .pipe(
            filter(Boolean),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $pack = toSignal(this.#packsSrv.pack.get$());

    readonly $selectedPackChannelId = signal(Number(this.#idPath));

    ngOnInit(): void {
        this.handleSelectedChannelChanges();
        this.handleRouterChannelsChanges();
        this.handlePackChannelsChangesForScroll();
        this.handlePackChannelsChangesToNavigate();
    }

    openAddChannelsDialog(): void {
        this.#matDialog.open<AddPackChannelDialogComponent>(
            AddPackChannelDialogComponent, new ObMatDialogConfig({ packId: this.$pack().id })
        ).beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'PACK.CHANNELS.ADD_CHANNELS_SUCCESS' });
                    this.loadChannels();
                }
            });
    }

    openDeleteChannelDialog(): void {
        this.#channelsSrv.getChannel$()
            .pipe(
                first(Boolean),
                switchMap(channel => this.#msgDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_CHANNEL',
                    message: 'PACK.CHANNELS.DELETE_CHANNEL_WARNING',
                    messageParams: { channelName: channel.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })),
                filter(Boolean),
                switchMap(() => {
                    const packId = this.$pack().id;
                    return this.#packsSrv.pack.channel.delete(packId, Number(this.#idPath));
                })
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'PACK.CHANNELS.DELETE_CHANNEL_SUCCESS'
                });
                this.loadChannels();
            });
    }

    selectionChangeHandler(packChannelId: number): void {
        if (!!packChannelId && this.$selectedPackChannelId() !== packChannelId) {
            this.$selectedPackChannelId.set(packChannelId);
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#activatedRoute });
        }
    }

    private loadChannels(): void {
        this.#packsSrv.pack.channels.load(this.$pack().id);
    }

    private currentPath(): string {
        return this._innerPath ?
            this.$selectedPackChannelId().toString() + '/' + this._innerPath :
            this.$selectedPackChannelId().toString();
    }

    private handleSelectedChannelChanges(): void {
        combineLatest([
            this.#channelsSrv.getChannel$(),
            this.#channelsSrv.getChannelError$()
        ]).pipe(
            filter(res => res.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef))
            .subscribe(([channel, error]) => {
                this.$selectedPackChannelId.set(error || !channel ? null : channel.id);
                this.#cdRef.markForCheck();
            });
    }

    private handlePackChannelsChangesForScroll(): void {
        this.packChannels$.pipe(
            filter(packChannels => !!packChannels.length),
            debounceTime(500), takeUntilDestroyed(this.#destroyRef)
        ).subscribe(() => {
            const channel = this.$selectedPackChannelId();
            const element = document.getElementById('channels-list-option-' + channel);
            element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
    }

    private handleRouterChannelsChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.packChannels$),
            filter(channelsList => !this.#idPath && !!channelsList.length),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([firstChannel]) => {
            this.#router.navigate([firstChannel.channel.id], { relativeTo: this.#activatedRoute });
            this.$selectedPackChannelId.set(firstChannel.channel.id);
        });
    }

    private handlePackChannelsChangesToNavigate(): void {
        this.packChannels$.pipe(
            tap(channelsList => {
                if (!channelsList.length) {
                    setTimeout(() =>
                        this.#router.navigate(['.'], { relativeTo: this.#activatedRoute })
                    );
                }
            }),
            filter(channelsList => !!channelsList.length && this.#idPath &&
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

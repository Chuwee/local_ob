import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { Pack, PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, debounceTime, filter, first, map, shareReplay, startWith, switchMap, switchMapTo, take, tap } from 'rxjs';
import { CreatePackDialogComponent } from '../create/create-pack-dialog.component';
import { UpdatePackDialogComponent } from '../update/update-pack-dialog.component';

@Component({
    selector: 'app-packs-list',
    templateUrl: './packs-list.component.html',
    imports: [
        NgIf, NgFor, NgClass, FlexLayoutModule, MaterialModule, TranslatePipe, LocalDateTimePipe, AsyncPipe,
        LastPathGuardListenerDirective, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PacksListComponent implements OnInit, OnDestroy {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #onDestroy = inject(DestroyRef);
    #channelId: number;
    #entityId: number;

    get #idPath(): string | undefined {
        return this.#route.snapshot.children[0].params['packId'];
    }

    get #innerPath(): string {
        return this.#route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    readonly dateTimeFormats = DateTimeFormats;
    readonly isLoadingList$ = this.#packsSrv.packList.loading$();
    readonly totalPacks$ = this.#packsSrv.packList.get$().pipe(filter(Boolean), map(pl => pl.length || 0));
    readonly packsList$ = this.#packsSrv.packList.get$().pipe(filter(Boolean));
    readonly pack$ = this.#packsSrv.pack.get$().pipe(filter(Boolean), shareReplay(1));
    selectedPackId: number;

    ngOnInit(): void {
        this.loadChannelPackList();

        combineLatest([
            this.#packsSrv.pack.get$(),
            this.#packsSrv.pack.getError$()
        ]).pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([pack, error]) => {
                this.selectedPackId = error || !pack ? null : pack.id;
                if (this.selectedPackId && this.#idPath !== this.selectedPackId.toString()) {
                    const path = this.currentPath();
                    this.#router.navigate([path], { relativeTo: this.#route });
                }
            });

        // scoll to the selected element
        this.packsList$.pipe(
            filter(packList => !!packList.length),
            debounceTime(500), takeUntilDestroyed(this.#onDestroy)
        ).subscribe(() => {
            const pack = this.selectedPackId;
            const element = document.getElementById('pack-list-option-' + pack);
            element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });

        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd)
        ).pipe(
            switchMapTo(this.packsList$),
            filter(packsList => !this.#idPath && !!packsList.length),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([firstPack]) => {
            this.selectedPackId = firstPack.id;
            this.#router.navigate([firstPack.id], { relativeTo: this.#route });
        });

        this.packsList$
            .pipe(
                tap(packList => {
                    if (!packList.length) {
                        this.#packsSrv.pack.clear();
                        setTimeout(() =>
                            this.#router.navigate(['.'], { relativeTo: this.#route })
                        );
                    }
                }),
                filter(packList => !!packList.length && this.#idPath && !packList.find(element => element.id.toString() === this.#idPath)),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(([firstPack]) => {
                this.#router.navigate([firstPack.id], { relativeTo: this.#route });
            });

    }

    ngOnDestroy(): void {
        this.#packsSrv.pack.clear();
        this.#packsSrv.packList.clear();
    }

    openNewPackDialog(): void {
        this.#matDialog.open(CreatePackDialogComponent, new ObMatDialogConfig({
            channelId: this.#channelId, entityId: this.#entityId
        })).beforeClosed()
            .subscribe((packId: number) => {
                if (packId) {
                    this.#ephemeralMsg.showSuccess({
                        msgKey: 'CHANNELS.PACKS.CREATE_SUCCESS'
                    });
                    this.loadChannelPackList();
                    this.selectionChangeHandler(packId);
                }
            });
    }

    openEditPackDialog(): void {
        this.#matDialog.open(UpdatePackDialogComponent,
            new ObMatDialogConfig({ channelId: this.#channelId, packId: this.selectedPackId })
        ).beforeClosed().subscribe((packId: number) => {
            if (packId) {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'CHANNELS.PACKS.EDIT_SUCCESS'
                });
                this.#packsSrv.packList.load(this.#channelId);
            }
        });
    }

    openDeletePackDialog(): void {
        this.#packsSrv.pack.get$().pipe(
            take(1),
            filter(Boolean),
            switchMap((pack: Pack) => {
                let typeMessage;

                if (pack.has_sales) {
                    return this.#msgDialogSrv.showAlert({
                        title: 'CHANNELS.PACKS.DELETE_HAS_SALES_ERROR_TITLE',
                        message: 'CHANNELS.PACKS.DELETE_HAS_SALES_ERROR_DESCRIPTION'
                    })
                        .pipe(map(() => ({ accepted: false, pack })));
                }

                if (pack.type === 'AUTOMATIC') {
                    typeMessage = 'CHANNELS.PACKS.DELETE_AUTOMATIC';
                } else if (pack.type === 'MANUAL') {
                    typeMessage = 'CHANNELS.PACKS.DELETE_MANUAL';
                }

                return this.#msgDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'CHANNELS.PACKS.DELETE_TITLE',
                    message: typeMessage,
                    messageParams: { name: pack.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })
                    .pipe(map(accepted => ({ accepted, pack })));
            }),
            filter(({ accepted }) => !!accepted),
            switchMap(({ pack }) =>
                this.#packsSrv.pack.delete(this.#channelId, pack.id)
            )
        ).subscribe(() => {
            this.#ephemeralMsg.showSuccess({
                msgKey: 'CHANNELS.PACKS.DELETE_SUCCESS'
            });
            this.loadChannelPackList();
        });
    }

    selectionChangeHandler(packId: number): void {
        if (!!packId && this.selectedPackId !== packId) {
            this.selectedPackId = packId;
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#route });
        }
    }

    private loadChannelPackList(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                this.#channelId = channel.id;
                this.#entityId = channel.entity.id;
                this.#packsSrv.packList.load(channel.id);
            });
    }

    private currentPath(): string {
        return this.#innerPath ?
            this.selectedPackId.toString() + '/' + this.#innerPath : this.selectedPackId.toString();
    }
}

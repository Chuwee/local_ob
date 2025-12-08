import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    SeasonTicketChannelsService, SeasonTicketChannelsLoadCase, SeasonTicketChannel
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import {
    SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import {
    distinctUntilChanged,
    filter,
    first,
    map,
    shareReplay,
    take,
    withLatestFrom
} from 'rxjs/operators';
import { NewSeasonTicketChannelDialogComponent } from '../create/new-season-ticket-channel-dialog.component';
import { getReleaseStatusIndicator, getSaleStatusIndicator } from '../models/season-sale-status-mapping-functions';
import { SeasonTicketChannelsStateMachine } from '../season-ticket-channel-state-machine';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        AsyncPipe,
        MaterialModule,
        LastPathGuardListenerDirective,
        EllipsifyDirective,
        LocalDateTimePipe
    ],
    selector: 'app-season-ticket-channels-list',
    templateUrl: './season-ticket-channels-list.component.html',
    styleUrls: ['./season-ticket-channels-list.component.scss']
})
export class SeasonTicketChannelsListComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketsSrv = inject(SeasonTicketsService);
    private readonly _seasonTicketChannelService = inject(SeasonTicketChannelsService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _activatedRoute = inject(ActivatedRoute);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _seasonTicketChannelSM = inject(SeasonTicketChannelsStateMachine);

    private readonly _destroyRef = inject(DestroyRef);

    readonly channelList$ = this._seasonTicketChannelService.seasonTicketChannelList.getData$()
        .pipe(
            filter(channelList => channelList !== null),
            shareReplay(1)
        );

    selectedSeasonTicketChannel: SeasonTicketChannel;
    readonly totalChannels$ = this._seasonTicketChannelService.seasonTicketChannelList.getMetadata$()
        .pipe(
            map(md => md ? md.total : 0),
            distinctUntilChanged(),
            shareReplay(1)
        );

    readonly isGenerationStatusReady$ = this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$();
    readonly seasonTicketChannel$ = this._seasonTicketChannelService.getSeasonTicketChannel$()
        .pipe(filter(Boolean), shareReplay(1));

    readonly getReleaseStatusIndicator = getReleaseStatusIndicator;
    readonly getSaleStatusIndicator = getSaleStatusIndicator;
    readonly dateTimeFormats = DateTimeFormats;
    readonly isLoading$ = booleanOrMerge([
        this._seasonTicketChannelService.seasonTicketChannelList.loading$(),
        this._seasonTicketsSrv.seasonTicketStatus.inProgress$()
    ]);

    ngOnInit(): void {
        this._seasonTicketChannelService.getSeasonTicketChannel$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(seasonTicketChannel => {
                if (this.selectedSeasonTicketChannel?.channel.id !== seasonTicketChannel?.channel.id) {
                    this.selectedSeasonTicketChannel = seasonTicketChannel;
                }
            });

        this._seasonTicketChannelSM.getListDetailState$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(state => {
                if (state === SeasonTicketChannelsLoadCase.none) {
                    this._seasonTicketChannelSM.setCurrentState({
                        state: SeasonTicketChannelsLoadCase.loadSeasonTicketChannel,
                        idPath: this._idPath
                    });
                }
            });

        this._seasonTicketChannelService.getSeasonTicketChannel$()
            .pipe(
                filter(Boolean),
                withLatestFrom(this._seasonTicketChannelSM.getListDetailState$()),
                takeUntilDestroyed(this._destroyRef)
            ).subscribe(([eventChannel, state]) => {
                if (state === SeasonTicketChannelsLoadCase.loadSeasonTicketChannel) {
                    this.scrollToSelectedEventChannel(eventChannel.channel.id);
                }
            });
    }

    ngOnDestroy(): void {
        this._seasonTicketChannelService.seasonTicketChannelList.clear();
    }

    async openNewChannelDialog(): Promise<void> {
        const seasonTicket = await firstValueFrom((this._seasonTicketsSrv.seasonTicket.get$()));
        const data = { seasonTicketId: seasonTicket.id };
        this._matDialog.open(NewSeasonTicketChannelDialogComponent, new ObMatDialogConfig(data))
            .afterClosed()
            .subscribe(isNewChannels => {
                if (!isNewChannels) return;

                this._ephemeralMessageService.showSuccess({
                    msgKey: 'SEASON_TICKET.ADD_CHANNEL_SUCCESS'
                });
                this._seasonTicketChannelSM.setCurrentState({
                    state: SeasonTicketChannelsLoadCase.loadSeasonTicketChannel
                });
                this._seasonTicketsSrv.seasonTicket.get$().pipe(take(1))
                    .subscribe(seasonTicket => this._seasonTicketsSrv.seasonTicket.load(seasonTicket.id.toString()));
            });
    }

    openDeleteChannelDialog(seasonTicketChannel: SeasonTicketChannel): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'SEASON_TICKET.DELETE_SEASON_TICKET_CHANNEL',
            message: 'SEASON_TICKET.DELETE_SEASON_TICKET_CHANNEL_WARNING',
            messageParams: { channelName: seasonTicketChannel.channel.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (!success) return;

                this._seasonTicketChannelService.seasonTicketChannelList
                    .delete(seasonTicketChannel.season_ticket.id, seasonTicketChannel.channel.id)
                    .subscribe(() => {
                        this._ephemeralMessageService.showDeleteSuccess();
                        this._seasonTicketChannelSM.setCurrentState({
                            state: SeasonTicketChannelsLoadCase.loadSeasonTicketChannel
                        });
                    });
            });
    }

    selectionChangeHandler(seasonTicketChannel: SeasonTicketChannel): void {
        this._seasonTicketChannelService.getSeasonTicketChannel$()
            .pipe(first())
            .subscribe(selectedSeasonTicketChannel => {
                if (!!seasonTicketChannel.channel.id && selectedSeasonTicketChannel?.channel.id !== seasonTicketChannel.channel.id) {
                    this._seasonTicketChannelSM.setCurrentState({
                        state: SeasonTicketChannelsLoadCase.selectSeasonTicketChannel,
                        idPath: seasonTicketChannel.channel.id
                    });
                }
            });
    }

    private get _idPath(): number | undefined {
        // eslint-disable-next-line @typescript-eslint/dot-notation
        return parseInt(this._activatedRoute.snapshot.children[0].params['channelId'], 10);
    }

    private scrollToSelectedEventChannel(channelId: number): void {
        setTimeout(() => {
            const element = document.getElementById('channel-list-option-' + channelId);
            return element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 500);
    }
}

import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EventChannel, EventChannelsLoadCase, EventChannelsService, EventChannelToFavoriteRequest, getReleaseStatusIndicator,
    getSaleStatusIndicator
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { debounceTime, filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { NewEventChannelDialogComponent } from '../create/new-event-channel-dialog.component';
import { EventChannelsStateMachine } from '../event-channels-state-machine';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        CommonModule,
        LastPathGuardListenerDirective,
        EllipsifyDirective,
        LocalDateTimePipe
    ],
    selector: 'app-event-channels-list',
    templateUrl: './event-channels-list.component.html',
    styleUrls: ['./event-channels-list.component.scss']
})
export class EventChannelsListComponent implements OnInit, OnDestroy {
    private _event$: Observable<Event>;
    private _onDestroy = new Subject<void>();
    private _favoriteChannelSavingId: number = null;

    channelList$: Observable<EventChannel[]>;
    loading$: Observable<boolean>;
    totalChannels$: Observable<number>;
    eventChannel$: Observable<EventChannel>;
    favoriteChannelSavingId$: Observable<number>;
    isOperatorUser$: Observable<boolean>;
    getReleaseStatusIndicator = getReleaseStatusIndicator;
    getSaleStatusIndicator = getSaleStatusIndicator;
    readonly dateTimeFormats = DateTimeFormats;

    constructor(
        private _eventsService: EventsService,
        private _eventChannelService: EventChannelsService,
        private _eventChannelSM: EventChannelsStateMachine,
        private _msgDialogSrv: MessageDialogService,
        private _matDialog: MatDialog,
        private _route: ActivatedRoute,
        private _messageDialogService: MessageDialogService,
        private _ephemeralSrv: EphemeralMessageService,
        private _authSrv: AuthenticationService,
        private _channelService: ChannelsService
    ) { }

    ngOnInit(): void {
        this.model();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventChannelService.eventChannel.clear();
        this._eventChannelService.eventChannelsList.clear();
    }

    openNewChannelDialog(): void {
        this._event$
            .pipe(
                take(1),
                switchMap(event => this._matDialog.open(NewEventChannelDialogComponent, new ObMatDialogConfig(
                    { eventId: event.id, eventChannelService: this._eventChannelService }))
                    .afterClosed().pipe(
                        filter(isNewChannels => isNewChannels),
                        tap(() => {
                            this._ephemeralSrv.showSuccess({ msgKey: 'EVENTS.CHANNEL.ADD_CHANNEL_SUCCESS' });
                            this._eventChannelSM.setCurrentState({
                                state: EventChannelsLoadCase.loadEventChannel
                            });
                            this._eventsService.event.get$().pipe(take(1))
                                .subscribe(event => this._eventsService.event.load(event.id.toString()));
                        })
                    ))
            ).subscribe();
    }

    openDeleteChannelDialog(): void {
        this.eventChannel$
            .pipe(
                take(1),
                tap(eventChannel => {
                    this._msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'EVENTS.CHANNEL.DELETE_EVENT_CHANNEL',
                        message: 'EVENTS.CHANNEL.DELETE_EVENT_CHANNEL_WARNING',
                        messageParams: { channelName: eventChannel.channel.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                        .pipe(
                            filter(success => !!success),
                            switchMap(() => this._eventChannelService.deleteEventChannel(eventChannel.event.id, eventChannel.channel.id)
                                .pipe(
                                    tap(() => {
                                        this._ephemeralSrv.showSuccess({
                                            msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS',
                                            msgParams: { channelName: eventChannel.channel.name }
                                        });
                                        this._eventChannelSM.setCurrentState({
                                            state: EventChannelsLoadCase.loadEventChannel
                                        });
                                    })))
                        )
                        .subscribe();
                })
            ).subscribe();
    }

    selectionChangeHandler(eventChannel: EventChannel): void {
        if (eventChannel.channel.id) {
            this._eventChannelSM.setCurrentState({
                state: EventChannelsLoadCase.selectEventChannel,
                idPath: eventChannel.channel.id
            });
        }
    }

    updateFavoriteChannel(eventChannel: EventChannel, event: MouseEvent): void {
        event.stopPropagation();
        const eventId = eventChannel.event.id;
        const channelId = eventChannel.channel.id;
        const changeToFavorite: EventChannelToFavoriteRequest = {
            favorite: !eventChannel.channel.favorite
        };
        let finishedUpdatingFavoriteChannel$: Observable<void>;
        //Add favorite
        if (changeToFavorite.favorite) {
            this._favoriteChannelSavingId = channelId;
            finishedUpdatingFavoriteChannel$ = this._eventChannelService.updateFavoriteChannel(eventId, channelId, changeToFavorite);
        } else {
            //Remove favorite
            finishedUpdatingFavoriteChannel$ = this._messageDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.WARNING',
                message: 'EVENTS.CHANNEL.REMOVE_FROM_FAVORITES_WARNING',
                actionLabel: 'FORMS.ACTIONS.OK'
            })
                .pipe(
                    switchMap(success => {
                        if (success) {
                            this._favoriteChannelSavingId = channelId;
                            return this._eventChannelService.updateFavoriteChannel(eventId, channelId, changeToFavorite);
                        } else {
                            return throwError(() => null);
                        }
                    })
                );
        }
        finishedUpdatingFavoriteChannel$
            .pipe(
                switchMap(() => this.eventChannel$),
                first()
            )
            .subscribe(selectedEvChan => {
                //Only load again EventChannel if it's selected
                if (eventChannel.channel.id === selectedEvChan.channel.id) {
                    this._eventChannelService.eventChannel.load(eventId, channelId);
                }
                this._eventChannelService.eventChannelsList.load(eventId, {
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc'
                });
                this._ephemeralSrv.show({
                    type: changeToFavorite.favorite ? MessageType.info : MessageType.success,
                    msgKey: changeToFavorite.favorite ?
                        'EVENTS.CHANNEL.ADD_TO_FAVORITE_INFO' : 'EVENTS.CHANNEL.REMOVE_FROM_FAVORITES_SUCCESS'
                });
            });
    }

    private model(): void {
        this._event$ = this._eventsService.event.get$()
            .pipe(
                filter(event => !!event),
                shareReplay(1)
            );
        this.eventChannel$ = this._eventChannelService.eventChannel.get$()
            .pipe(
                filter(Boolean),
                shareReplay({ refCount: true, bufferSize: 1 })
            );
        this.loading$ = this._eventChannelService.eventChannelsList.inProgress$();
        this.totalChannels$ = this._eventChannelService.eventChannelsList.getMetaData$()
            .pipe(map(md => md ? md.total : 0));
        this.channelList$ = this._eventChannelService.eventChannelsList.getData$()
            .pipe(filter(Boolean));
        this.isOperatorUser$ = this._authSrv.getLoggedUser$()
            .pipe(
                first(user => !!user),
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
            );
        //If isSaving, returns the id of the channel being saved
        this.favoriteChannelSavingId$ = this._eventChannelService.isChannelToFavoriteSaving()
            .pipe(
                map(isSaving => {
                    const savingChannelId = this._favoriteChannelSavingId;
                    this._favoriteChannelSavingId = null;
                    return (isSaving && savingChannelId) || null;
                }),
                shareReplay(1)
            );
    }

    private loadDataHandler(): void {
        // carga inicial del listado de canales (+ el detalle del canal seleccionado si lo hubiera)
        this._eventChannelSM.getListDetailState$()
            .pipe(
                tap(state => {
                    if (state === EventChannelsLoadCase.none) {
                        this._eventChannelSM.setCurrentState({
                            state: EventChannelsLoadCase.loadEventChannel,
                            // eslint-disable-next-line @typescript-eslint/dot-notation
                            idPath: parseInt(this._route.snapshot.children[0].params['channelId'], 10)
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();
        this.eventChannel$
            .pipe(
                withLatestFrom(this._eventChannelSM.getListDetailState$()),
                filter(([_, state]) => state === EventChannelsLoadCase.loadEventChannel),
                map(([eventChannel]) => eventChannel),
                debounceTime(0),
                takeUntil(this._onDestroy)
            )
            .subscribe(eventChannel =>
                document.getElementById('channel-list-option-' + eventChannel.channel.id)
                    ?.scrollIntoView({ behavior: 'smooth', block: 'center' })
            );
    }
}

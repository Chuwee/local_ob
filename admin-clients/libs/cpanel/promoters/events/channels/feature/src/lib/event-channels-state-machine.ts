import {
    EventChannel, EventChannelsLoadCase, EventChannelsService, EventChannelsState
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, GuardsCheckEnd, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { filter, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';

export type EventChannelsStateParams = {
    state: EventChannelsLoadCase;
    idPath?: number;
};

@Injectable()
export class EventChannelsStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: number;
    private _eventId: number;
    private _channelId: number;

    constructor(
        private _eventsService: EventsService,
        private _eventChannelService: EventChannelsService,
        private _eventChannelsState: EventChannelsState,
        private _route: ActivatedRoute,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                filter(state => state !== null),
                takeUntil(this._onDestroy)
            )
            .subscribe(state => {
                switch (state) {
                    case EventChannelsLoadCase.loadEventChannel:
                        this.loadEventChannelWithRefreshList();
                        break;
                    case EventChannelsLoadCase.justLoadEventChannel:
                        this.justLoadEventChannel();
                        break;
                    case EventChannelsLoadCase.selectEventChannel:
                        this.selectEventChannel();
                        break;
                    case EventChannelsLoadCase.none:
                    default:
                        break;
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath }: EventChannelsStateParams): void {
        this._idPath = idPath;
        this._eventChannelsState.listDetailState.setValue(state);
    }

    getListDetailState$(): Observable<EventChannelsLoadCase> {
        return this._eventChannelsState.listDetailState.getValue$();
    }

    private loadEventChannelWithRefreshList(): void {
        this.loadEventChannelsList();
        this.getEventChannelsList()
            .subscribe(eventChannelsList => {
                if (eventChannelsList.length) {
                    this.setEventChannelIdOfChannelList(eventChannelsList);
                    this.loadEventChannel();
                    this.navigateToEventChannel();
                }
            });
    }

    private justLoadEventChannel(): void {
        this.getEventChannelsList()
            .subscribe(eventChannelsList => {
                if (eventChannelsList.length) {
                    this.setEventChannelIdOfChannelList(eventChannelsList);
                    this.loadEventChannel();
                    this._eventChannelService.eventChannel.get$()
                        .pipe(first(Boolean))
                        .subscribe(eventChannel => this._eventChannelService.eventChannelsList.updateEventChannelStatus(eventChannel));
                }
            });
    }

    private selectEventChannel(): void {
        this._eventChannelsState.eventChannel.triggerCancellation();
        this.getEventChannelsList().subscribe(eventChannelsList => {
            if (eventChannelsList.length) {
                this.setEventChannelId(this._idPath);
                this.navigateToEventChannel();
            }
        });

        this._router.events
            .pipe(
                first((event): event is GuardsCheckEnd => event instanceof GuardsCheckEnd),
                filter((event: GuardsCheckEnd) => event.shouldActivate),
                tap(() => this.loadEventChannel()),
                switchMap(() => this._eventChannelService.eventChannel.get$()),
                first(Boolean)
            )
            .subscribe(eventChannel => this._eventChannelService.eventChannelsList.updateEventChannelStatus(eventChannel));
    }

    private setEventChannelId(channelId: number): void {
        this._channelId = channelId;
    }

    private setEventChannelIdOfChannelList(eventChannelsList: EventChannel[]): void {
        if (this._idPath && !!eventChannelsList.length &&
            eventChannelsList.some(eventChannelFromList => eventChannelFromList.channel.id === this._idPath)) {
            this.setEventChannelId(this._idPath);
        } else {
            this.setEventChannelId(eventChannelsList[0].channel.id);
        }
    }

    private navigateToEventChannel(): void {
        const path = this.currentPath();
        this._router.navigate([path], { relativeTo: this._route });
        this._eventChannelService.eventChannel.get$()
            .pipe(first(eventChannel => eventChannel?.channel.id === this._channelId || !this._channelId))
            .subscribe(eventChannel => {
                // eslint-disable-next-line @typescript-eslint/dot-notation
                const allowedChannelTypes: string[] = this._innerActivatedRouteSnapshot?.routeConfig.data?.['allowedChannelTypes'];
                if (allowedChannelTypes && !allowedChannelTypes.includes(eventChannel?.channel.type)) {
                    this._router.navigate(['..'], { relativeTo: this._innerActivatedRoute });
                }
            });
    }

    private loadEventChannel(): void {
        this._eventChannelService.eventChannel.clear();
        this._eventChannelService.eventChannel.load(this._eventId, this._channelId);
    }

    private loadEventChannelsList(): void {
        this._eventsService.event.get$()
            .pipe(first(event => !!event))
            .subscribe(event => {
                this._eventChannelService.eventChannelsList.clear();
                this._eventId = event.id;
                this._eventChannelService.eventChannelsList.load(event.id, {
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc'
                });
            });
    }

    private getEventChannelsList(): Observable<EventChannel[]> {
        return this._eventChannelService.eventChannelsList.getData$()
            .pipe(
                first(eventChannelsList => !!eventChannelsList),
                switchMap(eventChannelsList => {
                    if (!eventChannelsList.length) {
                        return this._eventsService.event.get$()
                            .pipe(
                                tap(event => this._router.navigate(['/events', event.id, 'channels'])),
                                mapTo(eventChannelsList)
                            );
                    } else {
                        return of(eventChannelsList);
                    }
                }),
                take(1)
            );
    }

    private currentPath(): string {
        return this._innerPath ? this._channelId + '/' + this._innerPath : this._channelId.toString();
    }

    // gets the inner path (tab route) if found
    private get _innerPath(): string {
        return this._innerActivatedRouteSnapshot?.routeConfig.path;
    }

    private get _innerActivatedRouteSnapshot(): ActivatedRouteSnapshot {
        return this._route.snapshot.children[0]?.children[0];
    }

    private get _innerActivatedRoute(): ActivatedRoute {
        return this._route.children[0]?.children[0];
    }
}


import {
    SeasonTicketChannelsService, SeasonTicketChannelsLoadCase, SeasonTicketChannel, SeasonTicketChannelsState
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute, GuardsCheckEnd, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { filter, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';

export type SeasonTicketsChannelsStateParams = {
    state: SeasonTicketChannelsLoadCase;
    idPath?: number;
};

@Injectable()
export class SeasonTicketChannelsStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: number;
    private _seasonTicketId: number;
    private _channelId: number;

    constructor(
        private _seasonTicketsService: SeasonTicketsService,
        private _seasonTicketChannelService: SeasonTicketChannelsService,
        private _seasonTicketChannelsState: SeasonTicketChannelsState,
        private _activatedRoute: ActivatedRoute,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                filter(state => state !== null),
                tap(state => {
                    switch (state) {
                        case SeasonTicketChannelsLoadCase.loadSeasonTicketChannel:
                            this.loadSeasonTicketChannelWithRefreshList();
                            break;
                        case SeasonTicketChannelsLoadCase.justLoadSeasonTicketChannel:
                            this.justLoadSeasonTicketChannel();
                            break;
                        case SeasonTicketChannelsLoadCase.selectSeasonTicketChannel:
                            this.selectSeasonTicketChannel();
                            break;
                        case SeasonTicketChannelsLoadCase.none:
                        default:
                            break;
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath }: SeasonTicketsChannelsStateParams): void {
        this._idPath = idPath;
        this._seasonTicketChannelsState.setListDetailState(state);
    }

    getListDetailState$(): Observable<SeasonTicketChannelsLoadCase> {
        return this._seasonTicketChannelsState.getListDetailState$();
    }

    private loadSeasonTicketChannelWithRefreshList(): void {
        this.loadSeasonTicketChannelsList();
        this.getSeasonTicketChannelsList()
            .pipe(
                tap(seasonTicketChannelsList => {
                    if (seasonTicketChannelsList.length) {
                        this.setSeasonTicketChannelIdOfChannelList(seasonTicketChannelsList);
                        this.loadSeasonTicketChannel();
                        this.navigateToSeasonTicketChannel();
                    }
                })
            ).subscribe();
    }

    private justLoadSeasonTicketChannel(): void {
        this.getSeasonTicketChannelsList()
            .pipe(
                tap(seasonTicketChannelsList => {
                    if (seasonTicketChannelsList.length) {
                        this.setSeasonTicketChannelIdOfChannelList(seasonTicketChannelsList);
                        this.loadSeasonTicketChannel();
                        this._seasonTicketChannelService.getSeasonTicketChannel$()
                            .pipe(
                                filter(seasonTicketChannel => !!seasonTicketChannel),
                                take(1),
                                tap(seasonTicketChannel => {
                                    this._seasonTicketChannelService.updateSeasonTicketChannelListSelectedStatus(seasonTicketChannel);
                                })
                            ).subscribe();
                    }
                })
            ).subscribe();
    }

    private selectSeasonTicketChannel(): void {
        this.getSeasonTicketChannelsList()
            .pipe(
                tap(seasonTicketChannelsList => {
                    if (seasonTicketChannelsList.length) {
                        this.setEventChannelId(this._idPath);
                        this.navigateToSeasonTicketChannel();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first((event): event is GuardsCheckEnd => event instanceof GuardsCheckEnd),
            tap((event: GuardsCheckEnd) => {
                if (event.shouldActivate) {
                    this.loadSeasonTicketChannel();
                }
            })
        ).subscribe();
    }

    private setEventChannelId(channelId: number): void {
        this._channelId = channelId;
    }

    private setSeasonTicketChannelIdOfChannelList(seasonTicketChannelsList: SeasonTicketChannel[]): void {
        if (this._idPath && !!seasonTicketChannelsList.length &&
            seasonTicketChannelsList.some(seasonTicketChannelFromList => seasonTicketChannelFromList.channel.id === this._idPath)) {
            this.setEventChannelId(this._idPath);
        } else {
            this.setEventChannelId(seasonTicketChannelsList[0].channel.id);
        }
    }

    private navigateToSeasonTicketChannel(): void {
        this._seasonTicketsService.seasonTicket.get$()
            .pipe(
                take(1),
                tap(() => {
                    const path = this.currentPath();
                    this._router.navigate([path], { relativeTo: this._activatedRoute });
                })
            ).subscribe();
    }

    private loadSeasonTicketChannel(): void {
        this._seasonTicketChannelService.clearSeasonTicketChannel();
        this._seasonTicketChannelService.loadSeasonTicketChannel(this._seasonTicketId, this._channelId);
    }

    private loadSeasonTicketChannelsList(): void {
        this._seasonTicketsService.seasonTicket.get$()
            .pipe(
                first(seasonTicket => !!seasonTicket),
                tap(seasonTicket => {
                    this._seasonTicketChannelService.seasonTicketChannelList.clear();
                    this._seasonTicketId = seasonTicket.id;
                    this._seasonTicketChannelService.seasonTicketChannelList.load(seasonTicket.id, {
                        limit: 999,
                        offset: 0,
                        sort: 'name:asc'
                    });
                }
                )
            ).subscribe();
    }

    private getSeasonTicketChannelsList(): Observable<SeasonTicketChannel[]> {
        return this._seasonTicketChannelService.seasonTicketChannelList.getData$()
            .pipe(
                first(seasonTicketChannelsList => !!seasonTicketChannelsList),
                switchMap(seasonTicketChannelsList => {
                    if (!seasonTicketChannelsList.length) {
                        return this._seasonTicketsService.seasonTicket.get$()
                            .pipe(
                                tap(event => this._router.navigate(['/season-tickets', event.id, 'channels'])),
                                mapTo(seasonTicketChannelsList)
                            );
                    } else {
                        return of(seasonTicketChannelsList);
                    }
                }),
                take(1)
            );
    }

    private currentPath(): string {
        return this._innerPath ?
            this._channelId.toString() + '/' + this._innerPath : this._channelId.toString();
    }

    // gets the inner path (tab route) if found
    private get _innerPath(): string {
        return this._activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }
}


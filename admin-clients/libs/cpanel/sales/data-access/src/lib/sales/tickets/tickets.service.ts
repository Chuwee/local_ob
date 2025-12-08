import { StateManager } from '@OneboxTM/utils-state';
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { distinctByField } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { Observable, switchMap, tap, withLatestFrom } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { GetFilterRequest } from '../models/get-filter-request.model';
import { GetFilterResponse } from '../models/get-filter-response.model';
import { SalesFilterField } from '../models/sales-filter-field.enum';
import { TicketsApi } from './api/tickets.api';
import { GetFilterSessionDataRequest } from './models/get-filter-session-data-request.model';
import { GetFilterSessionDataResponse } from './models/get-filter-session-data-response.model';
import { TicketsState } from './state/tickets.state';

@Injectable()
export class TicketsService extends TicketsBaseService {

    readonly filterCurrencyList = Object.freeze({
        load: (request: GetFilterRequest) => StateManager.load(
            this._ticketsState.filterCurrencyList,
            this._ticketsApi.getFilterOptions$(SalesFilterField.currencies, request)
                .pipe(tap(response => this._ticketsState.currencyCache.cacheItems(response.data)))
        ),
        getNames$: (ids: string[], request: GetFilterRequest = {}) => this._ticketsState.currencyCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.currencies, id, request)),
        loading$: () => this._ticketsState.filterCurrencyList.isInProgress$(),
        clear: () => this._ticketsState.filterCurrencyList.setValue(null),
        getData$: () => this._ticketsState.filterCurrencyList.getValue$().pipe(map(response => response?.data))
    });

    readonly filterChannels = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                SalesFilterField.channels,
                request,
                this._ticketsState.filterChannelList.getValue$(),
                nextPage
            ).subscribe(result => {
                this._ticketsState.channelCache.cacheItems(result.data);
                this._ticketsState.filterChannelList.setValue(result);
            });
        },
        get$: () => this._ticketsState.filterChannelList.getValue$(),
        getData$: () => this._ticketsState.filterChannelList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[], request: GetFilterRequest = {}) => this._ticketsState.channelCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.channels, id, request)),
        error$: () => this._ticketsState.filterChannelList.getError$(),
        loading$: () => this._ticketsState.filterChannelList.isInProgress$(),
        clear: () => this._ticketsState.filterChannelList.setValue(null)
    });

    readonly filterChannelsEntities = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                SalesFilterField.channelEntity,
                request,
                this._ticketsState.filterChannelEntityList.getValue$(),
                nextPage
            )
                .subscribe(result => {
                    this._ticketsState.channelEntityCache.cacheItems(result.data);
                    this._ticketsState.filterChannelEntityList.setValue(result);
                });
        },
        get$: () => this._ticketsState.filterChannelEntityList.getValue$(),
        getData$: () => this._ticketsState.filterChannelEntityList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[], request: GetFilterRequest = {}) => this._ticketsState.channelEntityCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.channelEntity, id, request)),
        error$: () => this._ticketsState.filterChannelEntityList.getError$(),
        loading$: () => this._ticketsState.filterChannelEntityList.isInProgress$(),
        clear: () => this._ticketsState.filterChannelEntityList.setValue(null)
    });

    readonly filterEvents = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                SalesFilterField.events,
                request,
                this._ticketsState.filterEventList.getValue$(),
                nextPage
            )
                .subscribe(result => {
                    this._ticketsState.eventCache.cacheItems(result.data);
                    this._ticketsState.filterEventList.setValue(result);
                });
        },
        get$: () => this._ticketsState.filterEventList.getValue$(),
        getData$: () => this._ticketsState.filterEventList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[]) => this._ticketsState.eventCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.events, id)),
        error$: () => this._ticketsState.filterEventList.getError$(),
        loading$: () => this._ticketsState.filterEventList.isInProgress$(),
        clear: () => this._ticketsState.filterEventList.setValue(null)
    });

    readonly filterClients = Object.freeze({
        load: (request: GetFilterRequest, nextPage: boolean = false) => {
            this.loadSalesFilterList(
                SalesFilterField.clients,
                request,
                this._ticketsState.filterClientList.getValue$(),
                nextPage
            )
                .subscribe(result => {
                    this._ticketsState.clientCache.cacheItems(result.data);
                    this._ticketsState.filterClientList.setValue(result);
                });
        },
        get$: () => this._ticketsState.filterClientList.getValue$(),
        getData$: () => this._ticketsState.filterClientList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[]) => this._ticketsState.clientCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.clients, id)),
        error$: () => this._ticketsState.filterClientList.getError$(),
        loading$: () => this._ticketsState.filterClientList.isInProgress$(),
        clear: () => this._ticketsState.filterClientList.setValue(null)
    });

    readonly filterEventsEntities = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                SalesFilterField.eventEntity,
                request,
                this._ticketsState.filterEventEntityList.getValue$(),
                nextPage
            ).subscribe(result => {
                this._ticketsState.eventEntityCache.cacheItems(result.data);
                this._ticketsState.filterEventEntityList.setValue(result);
            });
        },
        get$: () => this._ticketsState.filterEventEntityList.getValue$(),
        getData$: () => this._ticketsState.filterEventEntityList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[], request: GetFilterRequest = {}) => this._ticketsState.eventEntityCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.eventEntity, id, request)),
        error$: () => this._ticketsState.filterEventEntityList.getError$(),
        loading$: () => this._ticketsState.filterEventEntityList.isInProgress$(),
        clear: () => this._ticketsState.filterEventEntityList.setValue(null)
    });

    readonly filterSessions = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                SalesFilterField.sessions,
                request,
                this._ticketsState.filterSessionList.getValue$(),
                nextPage
            ).subscribe(result => {
                this._ticketsState.sessionCache.cacheItems(result.data);
                this._ticketsState.filterSessionList.setValue(result);
            });
        },
        get$: () => this._ticketsState.filterSessionList.getValue$(),
        getData$: () => this._ticketsState.filterSessionList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (ids: string[]) => this._ticketsState.sessionCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.sessions, id)),
        error$: () => this._ticketsState.filterSessionList.getError$(),
        loading$: () => this._ticketsState.filterSessionList.isInProgress$(),
        clear: () => this._ticketsState.filterSessionList.setValue(null)
    });

    readonly filterSectors = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSessionDataList(
                this._ticketsState.filterSectorList.getValue$(),
                r => this._ticketsApi.getFilterSectors(r),
                request,
                nextPage
            ).subscribe(result => {
                this._ticketsState.sectorCache.cacheItems(result.data);
                this._ticketsState.filterSectorList.setValue(result);
            });
        },
        get$: () => this._ticketsState.filterSectorList.getValue$(),
        getNames$: (sessionIds: number[], ids: string[]) => this._ticketsState.sectorCache.getItems$(ids, id =>
            this._ticketsApi.getFilterSectors({ session_id: sessionIds, q: id, limit: 1 }).pipe(map(result => result?.data?.[0]))
        ),
        error$: () => this._ticketsState.filterSectorList.getError$(),
        loading$: () => this._ticketsState.filterSectorList.isInProgress$(),
        clear: () => this._ticketsState.filterSectorList.setValue(null)
    });

    readonly filterPriceTypes = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSessionDataList(
                this._ticketsState.filterPriceTypeList.getValue$(),
                r => this._ticketsApi.getFilterPriceTypes(r),
                request,
                nextPage
            ).subscribe(result => {
                this._ticketsState.priceTypeCache.cacheItems(result.data);
                this._ticketsState.filterPriceTypeList.setValue(result);
            });
        },
        get$: () => this._ticketsState.filterPriceTypeList.getValue$(),
        getData$: () => this._ticketsState.filterPriceTypeList.getValue$().pipe(map(resp => resp?.data)),
        getNames$: (sessionIds: number[], ids: string[]) => this._ticketsState.priceTypeCache.getItems$(ids, id =>
            this._ticketsApi.getFilterPriceTypes({ session_id: sessionIds, q: id, limit: 1 }).pipe(map(result => result?.data?.[0]))
        ),
        error$: () => this._ticketsState.filterPriceTypeList.getError$(),
        loading$: () => this._ticketsState.filterPriceTypeList.isInProgress$(),
        clear: () => this._ticketsState.filterPriceTypeList.setValue(null)
    });

    readonly ticketRelocations = Object.freeze({
        load: (orderCode: string, ticketId: number) =>
            StateManager.load(this._ticketsState.ticketRelocations, this._ticketsApi.getTicketRelocations(orderCode, ticketId)),
        get$: () => this._ticketsState.ticketRelocations.getValue$(),
        loading$: () => this._ticketsState.ticketRelocations.isInProgress$(),
        clear: () => this._ticketsState.ticketRelocations.setValue(null)
    })

    constructor(
        private _ticketsApi: TicketsApi,
        private _ticketsState: TicketsState
    ) {
        super(_ticketsApi, _ticketsState);
    }

    //CLEAR FILTER DATA
    clearFilterListsData(): void {
        this._ticketsState.filterChannelEntityList.setValue(null);
        this._ticketsState.filterEventEntityList.setValue(null);
        this._ticketsState.filterChannelList.setValue(null);
        this._ticketsState.filterEventList.setValue(null);
        this._ticketsState.filterSessionList.setValue(null);
        this._ticketsState.filterSectorList.setValue(null);
        this._ticketsState.filterPriceTypeList.setValue(null);
        this._ticketsState.filterClientList.setValue(null);
        this._ticketsState.filterCurrencyList.setValue(null);
    }

    clearFilterListsCache(): void {
        this._ticketsState.channelEntityCache.clear();
        this._ticketsState.eventEntityCache.clear();
        this._ticketsState.channelCache.clear();
        this._ticketsState.eventCache.clear();
        this._ticketsState.sessionCache.clear();
        this._ticketsState.sectorCache.clear();
        this._ticketsState.priceTypeCache.clear();
        this._ticketsState.clientCache.clear();
        this._ticketsState.currencyCache.clear();
    }

    private loadFilterOption(field: SalesFilterField, id: string, request: GetFilterRequest = {}): Observable<FilterOption> {
        request = {
            ...request, q: id, limit: 1
        };
        return this._ticketsApi.getFilterOptions$(field, request)
            .pipe(map(result => result?.data?.[0]));
    }

    private loadSalesFilterList(
        field: string, request: GetFilterRequest, currentObservable$: Observable<GetFilterResponse>, nextPage: boolean
    ): Observable<GetFilterResponse> {
        let result: Observable<GetFilterResponse>;
        if (!nextPage) {
            result = this._ticketsApi.getFilterOptions$(field, request);
        } else {
            result = currentObservable$
                .pipe(
                    take(1),
                    switchMap(currentData => {
                        request.cursor = currentData.metadata.next_cursor;
                        return this._ticketsApi.getFilterOptions$(field, request).pipe(
                            // gets the current list again, to prevent any change during request, this result will patch the current,
                            // if the current is a new one, it would be lost without recheck
                            withLatestFrom(currentObservable$.pipe(take(1))),
                            filter(([nextElements, currentElements]) =>
                                nextElements && currentElements.metadata.next_cursor === request.cursor),
                            tap(([nextElements, currentElements]) => nextElements.data = currentElements.data.concat(nextElements.data)),
                            map(([nextElements, _]) => nextElements)
                        );
                    })
                );
        }
        return result.pipe(tap(response => response.data = distinctByField(response.data, item => item.id, true)));
    }

    private loadSessionDataList(
        currentDataObs$: Observable<GetFilterSessionDataResponse>,
        apiCall: (r: GetFilterSessionDataRequest) => Observable<GetFilterSessionDataResponse>,
        request: GetFilterSessionDataRequest,
        nextPage: boolean
    ): Observable<GetFilterSessionDataResponse> {
        if (!nextPage) {
            request.offset = 0;
            return apiCall(request);
        } else {
            return currentDataObs$.pipe(
                take(1),
                switchMap(currentData => {
                    request.offset = currentData.metadata.offset + currentData.metadata.limit;
                    return apiCall(request)
                        .pipe(
                            // gets the current list again, to prevent any change during reques,
                            // this result will patch the current, if the current is a new one, it would be lost without recheck
                            withLatestFrom(currentDataObs$),
                            filter(([_, currentDataUpd]) => currentData === currentDataUpd),
                            tap(([nextEls, currentDataUpd]) => nextEls.data = currentDataUpd.data.concat(nextEls.data)),
                            map(([nextEls]) => nextEls)
                        );
                })
            );
        }
    }
}

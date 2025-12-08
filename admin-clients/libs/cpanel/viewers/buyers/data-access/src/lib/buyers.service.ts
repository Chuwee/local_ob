import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import {
    AggregatedData, ExportRequest, ExportResponse, Id, IdName
} from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, switchMap, take } from 'rxjs/operators';
import { BuyersApi } from './api/buyers.api';
import { BuyerOrderItemType, GetBuyerOrderItemsRequest } from './models/_index';
import { BuyersFilterContentData } from './models/buyer-filter-content.model';
import { Buyer } from './models/buyer.model';
import { aggDataBuyers } from './models/buyers-aggregated-data';
import { BuyersFilterContent } from './models/buyers-filter-content.model';
import { BuyersFilterField } from './models/buyers-filter-field.enum';
import { BuyersQueryDef } from './models/buyers-query-def.model';
import { BuyersQueryWrapper } from './models/buyers-query-wrapper.model';
import { BuyersQuery } from './models/buyers-query.model';
import { BuyersState } from './state/buyers.state';

@Injectable({
    providedIn: 'root'
})
export class BuyersService {
    readonly #api = inject(BuyersApi);
    readonly #state = inject(BuyersState);

    // BUYER ORDER ITEMS
    orderItems = Object.freeze({
        load: (buyerId: string, filter: GetBuyerOrderItemsRequest) => {
            filter.product_type = filter.product_type || BuyerOrderItemType.seat;

            const stateProp = filter.product_type === BuyerOrderItemType.seat
                ? this.#state.orderItemsSeat
                : this.#state.orderItemsProduct;

            return StateManager.load(
                stateProp,
                this.#api.getBuyerOrderItems(buyerId, filter).pipe(mapMetadata())
            );
        },
        getSeatMetadata$: () => this.#state.orderItemsSeat.getValue$().pipe(getMetadata()),
        getProductMetadata$: () => this.#state.orderItemsProduct.getValue$().pipe(getMetadata()),
        getSeatData$: () => this.#state.orderItemsSeat.getValue$().pipe(getListData()),
        getProductData$: () => this.#state.orderItemsProduct.getValue$().pipe(getListData()),
        loadingSeat$: () => this.#state.orderItemsSeat.isInProgress$(),
        loadingProduct$: () => this.#state.orderItemsProduct.isInProgress$(),
        clearSeat: () => this.#state.orderItemsSeat.setValue(null),
        clearProduct: () => this.#state.orderItemsProduct.setValue(null),
        errorSeat$: () => this.#state.orderItemsSeat.getError$(),
        errorProduct$: () => this.#state.orderItemsProduct.getError$()
    });

    constructor(
        private _buyersApi: BuyersApi,
        private _buyersState: BuyersState
    ) { }

    // BUYERS LIST

    loadBuyers(request: BuyersQuery): void {
        this._buyersState.setBuyersListLoading(true);
        this._buyersApi.getBuyers(request)
            .pipe(
                mapMetadata(),
                finalize(() => this._buyersState.setBuyersListLoading(false))
            )
            .subscribe(buyers => this._buyersState.setBuyersList(buyers));
    }

    getBuyersData$(): Observable<Buyer[]> {
        return this._buyersState.getBuyersList$().pipe(getListData());
    }

    getBuyersMetadata$(): Observable<Metadata> {
        return this._buyersState.getBuyersList$().pipe(getMetadata());
    }

    getBuyersAggregatedData$(): Observable<AggregatedData> {
        return this._buyersState.getBuyersList$()
            .pipe(map(buyers => buyers?.aggregated_data && new AggregatedData(buyers.aggregated_data, aggDataBuyers)));
    }

    isBuyersListLoading$(): Observable<boolean> {
        return this._buyersState.isBuyersListLoading$();
    }

    clearBuyersList(): void {
        this._buyersState.setBuyersList(null);
    }

    // INDIVIDUAL BUYER
    loadBuyer(id: string): void {
        this._buyersState.setBuyerError(null);
        this._buyersState.setBuyerLoading(true);
        this._buyersApi.getBuyer(id)
            .pipe(
                catchError(error => {
                    this._buyersState.setBuyerError(error);
                    return of(null);
                }),
                finalize(() => this._buyersState.setBuyerLoading(false))
            )
            .subscribe(buyer => this._buyersState.setBuyer(buyer));
    }

    getBuyer$(): Observable<Buyer> {
        return this._buyersState.getBuyer$();
    }

    getBuyerError$(): Observable<HttpErrorResponse> {
        return this._buyersState.getBuyerError$();
    }

    isBuyerLoading$(): Observable<boolean> {
        return this._buyersState.isBuyerLoading$();
    }

    clearBuyer(): void {
        this._buyersState.setBuyer(null);
    }

    createBuyer(buyer: Buyer): Observable<{ id: string }> {
        this._buyersState.setBuyerUpdating(true);
        return this._buyersApi.postBuyer(buyer)
            .pipe(finalize(() => this._buyersState.setBuyerUpdating(false)));
    }

    updateBuyer(buyer: Buyer): Observable<void> {
        this._buyersState.setBuyerUpdating(true);
        return this._buyersApi.putBuyer(buyer)
            .pipe(finalize(() => this._buyersState.setBuyerUpdating(false)));
    }

    isUpdatingBuyer$(): Observable<boolean> {
        return this._buyersState.isBuyerUpdating$();
    }

    deleteBuyer(id: string): Observable<void> {
        this._buyersState.setBuyerDeleting(true);
        return this._buyersApi.deleteBuyer(id)
            .pipe(finalize(() => this._buyersState.setBuyerDeleting(false)));
    }

    isBuyerDeleting$(): Observable<boolean> {
        return this._buyersState.isBuyerDeleting$();
    }

    // BUYERS FILTER CONTENTS

    loadFilterCollection(entityId: number, filterField: BuyersFilterField, parentId: number = null): void {
        switch (filterField) {
            case BuyersFilterField.subscriptionLists:
                this.loadFilterSubscriptionLists(entityId);
                break;
            case BuyersFilterField.channels:
                this.loadFilterChannels(entityId);
                break;
            case BuyersFilterField.collectives:
                this.loadFilterCollectives(entityId);
                break;
            case BuyersFilterField.events:
                this.loadFilterEvents(entityId);
                break;
            case BuyersFilterField.sessions:
                this.loadFilterSessions(entityId, parentId);
                break;
        }
    }

    // CHANNELS
    loadFilterChannels(entityId: number = null): void {
        this._buyersState.setFilterChannelsLoading(true);
        this.loadFilterContent(BuyersFilterField.channels, entityId)
            .pipe(finalize(() => this._buyersState.setFilterChannelsLoading(false)))
            .subscribe(channels => this._buyersState.setFilterChannels(channels));
    }

    getFilterChannels$(): Observable<IdName[]> {
        return this.mapFilterContent(this._buyersState.getFilterChannels$());
    }

    isFilterChannelsLoading$(): Observable<boolean> {
        return this._buyersState.isFilterChannelsLoading$();
    }

    clearFilterChannels(): void {
        this._buyersState.setFilterChannels(null);
    }

    // EVENTS
    loadFilterEvents(entityId: number): void {
        this._buyersState.setFilterEventsLoading(true);
        this.loadFilterContent(BuyersFilterField.events, entityId)
            .pipe(finalize(() => this._buyersState.setFilterEventsLoading(false)))
            .subscribe(events => this._buyersState.setFilterEvents(events));
    }

    getFilterEvents$(): Observable<IdName[]> {
        return this.mapFilterContent(this._buyersState.getFilterEvents$());
    }

    isFilterEventsLoading$(): Observable<boolean> {
        return this._buyersState.isFilterEventsLoading$();
    }

    // SESSIONS
    loadFilterSessions(entityId: number, event: number): void {
        this._buyersState.setFilterSessions(null);
        this._buyersState.setFilterSessionsLoading(true);
        this.loadFilterContent(BuyersFilterField.sessions, entityId, event)
            .pipe(finalize(() => this._buyersState.setFilterSessionsLoading(false)))
            .subscribe(sessions => this._buyersState.setFilterSessions(sessions));
    }

    getFilterSessions(): Observable<BuyersFilterContentData[]> {
        return this.mapFilterContent(this._buyersState.getFilterSessions$());
    }

    isFilterSessionsLoading$(): Observable<boolean> {
        return this._buyersState.isFilterSessionsLoading$();
    }

    clearFilterSessions(): void {
        this._buyersState.setFilterSessions(null);
    }

    // COLLECTIVES
    loadFilterCollectives(entityId: number): void {
        this._buyersState.setFilterCollectivesLoading(true);
        this.loadFilterContent(BuyersFilterField.collectives, entityId)
            .pipe(finalize(() => this._buyersState.setFilterCollectivesLoading(false)))
            .subscribe(collectives => this._buyersState.setFilterCollectives(collectives));
    }

    getFilterCollectives$(): Observable<IdName[]> {
        return this.mapFilterContent(this._buyersState.getFilterCollectives$());
    }

    isFilterCollectivesLoading$(): Observable<boolean> {
        return this._buyersState.isFilterCollectivesLoading$();
    }

    // SUBSCRIPTION LISTS

    loadFilterSubscriptionLists(entityId: number): void {
        this._buyersState.setFilterSubscriptionListsLoading(true);
        this.loadFilterContent(BuyersFilterField.subscriptionLists, entityId)
            .pipe(finalize(() => this._buyersState.setFilterSubscriptionListsLoading(false)))
            .subscribe(subscriptions => this._buyersState.setFilterSubscriptionLists(subscriptions));
    }

    getFilterSubscriptionLists$(): Observable<IdName[]> {
        return this.mapFilterContent(this._buyersState.getFilterSubscriptionLists$());
    }

    isFilterSubscriptionListsLoading$(): Observable<boolean> {
        return this._buyersState.isFilterSubscriptionListsLoading$();
    }

    // BUYERS QUERIES

    loadQueries(): void {
        this._buyersState.setQueriesLoading(true);
        this._buyersApi.getBuyersQueries()
            .pipe(
                mapMetadata(),
                finalize(() => this._buyersState.setQueriesLoading(false))
            )
            .subscribe(queries => this._buyersState.setQueries(queries));
    }

    getQueries$(): Observable<BuyersQueryDef[]> {
        return this._buyersState.getQueries$().pipe(getListData());
    }

    getQueriesMetadata$(): Observable<Metadata> {
        return this._buyersState.getQueries$().pipe(getMetadata());
    }

    isQueriesLoading$(): Observable<boolean> {
        return this._buyersState.isQueriesLoading$();
    }

    // Single query

    loadQuery(id: number): void {
        this._buyersState.setQueryLoading(true);
        this._buyersApi.getBuyersQuery(id)
            .pipe(finalize(() => this._buyersState.setQueryLoading(false)))
            .subscribe(query => this._buyersState.setQuery(query));
    }

    clearQuery(): void {
        this._buyersState.setQuery(null);
    }

    getQuery$(): Observable<BuyersQueryWrapper> {
        return this._buyersState.getQuery$();
    }

    setBlankQuery(): void {
        this._buyersState.setQuery({
            query: {
                limit: undefined,
                offset: undefined
            }
        });
    }

    updateQueryInMemory(buyersQuery: BuyersQuery): void {
        this._buyersState.getQuery$()
            .pipe(take(1))
            .subscribe(queryInState => {
                queryInState.query = buyersQuery;
            });
    }

    isQueryLoading$(): Observable<boolean> {
        return this._buyersState.isQueryLoading$();
    }

    createQuery(query: BuyersQueryWrapper): Observable<Id> {
        this._buyersState.setQuerySaving(true);
        return this._buyersApi.postBuyersQuery(query)
            .pipe(finalize(() => this._buyersState.setQuerySaving(false)));
    }

    updateQuery(id: number, query: BuyersQueryDef | BuyersQueryWrapper): Observable<void> {
        this._buyersState.setQuerySaving(true);
        return this._buyersApi.putBuyersQuery(id, query)
            .pipe(finalize(() => this._buyersState.setQuerySaving(false)));
    }

    cloneQuery(id: number, query: BuyersQueryDef): Observable<Id> {
        return this._buyersApi.getBuyersQuery(id)
            .pipe(
                switchMap(queryToClone => {
                    queryToClone.id = undefined;
                    queryToClone.query_name = query.query_name;
                    queryToClone.query_description = query.query_description;
                    return this._buyersApi.postBuyersQuery(queryToClone);
                })
            );
    }

    isQuerySaving$(): Observable<boolean> {
        return this._buyersState.isQuerySaving$();
    }

    deleteQuery(id: number): Observable<void> {
        this._buyersState.setQueryDeleting(true);
        return this._buyersApi.deleteBuyersQuery(id)
            .pipe(finalize(() => this._buyersState.setQueryDeleting(false)));
    }

    isDeletingQuery(): Observable<boolean> {
        return this._buyersState.isQueryDeleting$();
    }

    // EXPORT BUYERS

    exportBuyers(filter: BuyersQuery, exportRequest: ExportRequest): Observable<ExportResponse> {
        this._buyersState.setExportBuyersLoading(true);
        return this._buyersApi.exportBuyers(filter, exportRequest.format, exportRequest.fields, exportRequest.delivery)
            .pipe(finalize(() => this._buyersState.setExportBuyersLoading(false)));
    }

    // BUYERS FILTER CONTENTS

    private loadFilterContent(
        filterField: BuyersFilterField, entityId: number, event = undefined as number
    ): Observable<BuyersFilterContent> {
        return this._buyersApi.getBuyerFilters({ filterField, entityId, limit: 500, event });
    }

    private mapFilterContent(source: Observable<BuyersFilterContent>): Observable<IdName[]> {
        return source.pipe(map(val => val?.data));
    }
}

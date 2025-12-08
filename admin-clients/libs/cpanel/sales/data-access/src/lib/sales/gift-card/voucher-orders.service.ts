import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import {
    AggregatedData, ExportRequest, ExportResponse, ScrolledMetadata, FilterOption, combineAggregatedData
} from '@admin-clients/shared/data-access/models';
import { distinctByField } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { combineLatest, Observable, of, switchMap, tap, withLatestFrom } from 'rxjs';
import { catchError, filter, finalize, map, take } from 'rxjs/operators';
import { GetFilterRequest } from '../models/get-filter-request.model';
import { GetFilterResponse } from '../models/get-filter-response.model';
import { OrderFilterFields } from '../orders/models/order-filter-fields.enum';
import { VoucherOrdersApi } from './api/voucher-orders.api';
import { GetVoucherOrdersRequest } from './models/get-voucher-orders-request.model';
import { ResendVoucherOrderType } from './models/resend-voucher-order-type.enum';
import { VoucherOrderDetail } from './models/voucher-order-detail.model';
import { VoucherOrderFilterField } from './models/voucher-order-filter-field.enum';
import { VoucherOrder } from './models/voucher-order.model';
import { aggDataVoucherOrder } from './models/voucher-orders-aggregated-data';
import { VoucherOrdersState } from './state/voucher-orders.state';

@Injectable({
    providedIn: 'root'
})
export class VoucherOrdersService {

    readonly filterCurrencyList = Object.freeze({
        load: (request: GetFilterRequest) => StateManager.load(
            this._state.filterCurrencyList,
            this._api.getFilterOptions$(VoucherOrderFilterField.currencies, request)
                .pipe(tap(response => this._state.currencyCache.cacheItems(response.data)))
        ),
        getNames$: (ids: string[]) => this._state.currencyCache
            .getItems$(ids, id => this._api.getFilterOptions$(OrderFilterFields.currencies, { q: id })
                .pipe(map(result => result.data?.[0]))),
        loading$: () => this._state.filterCurrencyList.isInProgress$(),
        clear: () => this._state.filterCurrencyList.setValue(null),
        getData$: () => this._state.filterCurrencyList.getValue$().pipe(map(response => response?.data))
    });

    readonly currencyAggregatedData = Object.freeze({
        load: (request: GetVoucherOrdersRequest) => {
            request.entity_id = request.channel_id?.length ? undefined : request.entity_id;
            StateManager.load(this._state.aggregations, this._api.getAggregations(request));
        },
        loading$: () => this._state.aggregations.isInProgress$(),
        clear: () => this._state.aggregations.setValue(null),
        getCombined$: () => combineLatest([
            this._state.aggregations.getValue$().pipe(filter(Boolean)),
            this._state.getVoucherOrdersList$().pipe(
                map(orders => orders?.aggregated_data),
                filter(Boolean)
            )
        ]).pipe(
            map(([currencyAggregatedData, aggregatedData]) =>
                combineAggregatedData(currencyAggregatedData, aggregatedData, aggDataVoucherOrder))
        )
    });

    readonly filterMerchantList = Object.freeze({
        load: (request: GetFilterRequest, nextPage = false) => {
            this.loadSalesFilterList(
                VoucherOrderFilterField.merchants,
                request,
                this._state.filterMerchantList.getValue$(),
                nextPage
            ).subscribe(merchants => {
                this._state.merchantsCache.cacheItems(merchants?.data);
                this._state.filterMerchantList.setValue(merchants);
            });
        },
        getData$: () => this._state.filterMerchantList.getValue$().pipe(map(merchants => merchants?.data)),
        getMetaData$: () => this._state.filterMerchantList.getValue$().pipe(map(merchants => merchants?.metadata)),
        getNames$: (ids: string[]) => this._state.merchantsCache
            .getItems$(ids, id => this.getFilterItem(id, VoucherOrderFilterField.merchants))
    });

    constructor(
        private _api: VoucherOrdersApi,
        private _state: VoucherOrdersState
    ) {
    }

    loadVoucherOrdersList(request: GetVoucherOrdersRequest, relevance = false): void {
        this._state.setVoucherOrdersListLoading(true);
        request.entity_id = request.channel_id?.length ? undefined : request.entity_id;
        if (relevance && request.q) {
            request.sort = null; // ordenación default por relevancia en búsqueda por parámetro q
        }
        this._api.getVoucherOrders(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._state.setVoucherOrdersListLoading(false))
            ).subscribe(orders =>
                this._state.setVoucherOrdersList(orders)
            );
    }

    getVoucherOrdersListData$(): Observable<VoucherOrder[]> {
        return this._state.getVoucherOrdersList$().pipe(getListData());
    }

    getVoucherOrdersListMetadata$(): Observable<Metadata> {
        return this._state.getVoucherOrdersList$().pipe(getMetadata());
    }

    getVoucherOrdersListAggregatedData$(aggregatedMetric = aggDataVoucherOrder): Observable<AggregatedData> {
        return this._state.getVoucherOrdersList$()
            .pipe(map(orders => orders?.aggregated_data && new AggregatedData(orders.aggregated_data, aggregatedMetric)));
    }

    isVoucherOrdersListLoading$(): Observable<boolean> {
        return this._state.isVoucherOrdersListLoading$();
    }

    exportVoucherOrders(request: GetVoucherOrdersRequest, data: ExportRequest): Observable<ExportResponse> {
        this._state.setExportVoucherOrdersLoading(true);
        return this._api.exportVoucherOrders(request, data)
            .pipe(finalize(() => this._state.setExportVoucherOrdersLoading(false)));
    }

    // FILTER CHANNELS

    loadFilterChannelList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            VoucherOrderFilterField.channels,
            request,
            this._state.getFilterChannelList$(),
            nextPage
        )
            .subscribe(channels => {
                this._state.getChannelCache().cacheItems(channels?.data);
                this._state.setFilterChannelList(channels);
            });
    }

    getFilterChannelListData$(): Observable<FilterOption[]> {
        return this._state.getFilterChannelList$().pipe(map(channels => channels?.data));
    }

    getFilterChannelListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.getFilterChannelList$().pipe(map(channels => channels?.metadata));
    }

    getFilterChannelNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.getChannelCache().getItems$(ids, id =>
            this._api
                .getFilterOptions$(VoucherOrderFilterField.channels, { q: id, limit: 1 })
                .pipe(map(result => result?.data?.[0]))
        );
    }

    // CHANNEL ENTITIES

    loadFilterChannelEntitiesList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            VoucherOrderFilterField.channelEntity,
            request,
            this._state.getFilterChannelEntityList$(),
            nextPage
        )
            .subscribe(channelEntities => {
                this._state.getChannelEntityCache().cacheItems(channelEntities?.data);
                this._state.setFilterChannelEntityList(channelEntities);
            });
    }

    getFilterChannelEntitiesListData$(): Observable<FilterOption[]> {
        return this._state.getFilterChannelEntityList$().pipe(map(channels => channels?.data));
    }

    getFilterChannelEntitiesListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.getFilterChannelEntityList$().pipe(map(channels => channels?.metadata));
    }

    getFilterChannelEntitiesNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.getChannelEntityCache().getItems$(ids, id =>
            this._api.getFilterOptions$(VoucherOrderFilterField.channelEntity, { q: id, limit: 1 })
                .pipe(map(result => result?.data?.[0]))
        );
    }

    clearFilterListsData(): void {
        this._state.setFilterChannelEntityList(null);
        this._state.setFilterChannelList(null);
        this._state.filterCurrencyList.setValue(null);
        this._state.filterMerchantList.setValue(null);
    }

    clearFilterListsCache(): void {
        this._state.getChannelCache().clear();
        this._state.getChannelEntityCache().clear();
        this._state.currencyCache.clear();
    }

    loadVoucherOrderDetail(orderCode: string): void {
        this._state.setVoucherOrderDetailLoading(true);
        this._state.setVoucherOrderDetailError(null);
        this._api.getVoucherOrder(orderCode)
            .pipe(
                catchError(error => {
                    this._state.setVoucherOrderDetailError(error);
                    return of(null);
                }),
                finalize(() => this._state.setVoucherOrderDetailLoading(false))
            )
            .subscribe(orderDetail =>
                this._state.setVoucherOrderDetail(orderDetail)
            );
    }

    clearVoucherOrderDetail(): void {
        this._state.setVoucherOrderDetail(null);
    }

    getVoucherOrderDetail$(): Observable<VoucherOrderDetail> {
        return this._state.getVoucherOrderDetail$();
    }

    getVoucherOrderDetailError$(): Observable<HttpErrorResponse> {
        return this._state.getVoucherOrderDetailError$();
    }

    isVoucherOrderDetailLoading$(): Observable<boolean> {
        return this._state.isVoucherOrderDetailLoading$();
    }

    isExportVoucherOrdersLoading$(): Observable<boolean> {
        return this._state.isExportVoucherOrdersLoading$();
    }

    resend(voucherOrderCode: string, types: ResendVoucherOrderType[], email: string): Observable<void> {
        this._state.setResendLoading(true);
        return this._api.resend(voucherOrderCode, types, email).pipe(
            finalize(() => this._state.setResendLoading(false))
        );
    }

    isResendLoading$(): Observable<boolean> {
        return this._state.isResendLoading$();
    }

    private getFilterItem(id: string, field: VoucherOrderFilterField): Observable<FilterOption> {
        return this._api.getFilterOptions$(field, { q: id }).pipe(map(result => result.data?.[0]));
    }

    private loadFilterOption(field: VoucherOrderFilterField, id: string): Observable<FilterOption> {
        return this._api.getFilterOptions$(field, { q: id, limit: 1 })
            .pipe(map(result => result?.data?.[0]));
    }

    private loadSalesFilterList(
        field: string,
        request: GetFilterRequest,
        currentObservable$: Observable<GetFilterResponse>,
        nextPage: boolean
    ): Observable<GetFilterResponse> {
        let result: Observable<GetFilterResponse>;
        if (!nextPage) {
            result = this._api.getFilterOptions$(field, request);
        } else {
            return currentObservable$
                .pipe(
                    take(1),
                    switchMap(currentData => {
                        request.cursor = currentData.metadata.next_cursor;
                        return this._api.getFilterOptions$(field, request).pipe(
                            // gets the current list again, to prevent any change during request, this result will patch the current,
                            // if the current is a new one, it would be lost without recheck
                            withLatestFrom(currentObservable$.pipe(take(1))),
                            filter(([nextElements, currentElements]) =>
                                nextElements && currentElements.metadata.next_cursor === request.cursor),
                            tap(([nextElements, currentElements]) =>
                                nextElements.data = currentElements.data.concat(nextElements.data)),
                            map(([nextElements, _]) => nextElements)
                        );
                    })
                );
        }
        return result.pipe(tap(result => result.data = distinctByField(result.data, v => v.id, true)));
    }
}

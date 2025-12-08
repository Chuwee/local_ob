import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest, ExportResponse, AggregatedData, FilterOption } from '@admin-clients/shared/data-access/models';
import { distinctByField } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, switchMap, tap, withLatestFrom } from 'rxjs';
import { catchError, filter, finalize, map, take } from 'rxjs/operators';
import { GetFilterRequest } from '../models/get-filter-request.model';
import { GetFilterResponse } from '../models/get-filter-response.model';
import { SalesFilterField } from '../models/sales-filter-field.enum';
import { aggDataOrder } from '../orders/models/orders-aggregated-data';
import { MemberOrdersApi } from './api/member-orders.api';
import { GetMemberOrdersRequest } from './models/get-member-orders-request.model';
import { MemberOrderDetail } from './models/member-order-detail.model';
import { MemberOrderFilterField } from './models/member-order-filter-field.enum';
import { MemberOrder } from './models/member-order.model';
import { MemberOrdersState } from './state/member-orders.state';

@Injectable({
    providedIn: 'root'
})
export class MemberOrdersService {
    private _api = inject(MemberOrdersApi);
    private _state = inject(MemberOrdersState);

    readonly filterCurrencyList = Object.freeze({
        load: (request: GetFilterRequest) => StateManager.load(
            this._state.filterCurrencyList,
            this._api.getFilterOptions$(MemberOrderFilterField.currencies, request)
                .pipe(tap(response => this._state.currencyCache.cacheItems(response.data)))
        ),
        getNames$: (ids: string[]) => this._state.currencyCache
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.currencies, id)),
        loading$: () => this._state.filterCurrencyList.isInProgress$(),
        clear: () => this._state.filterCurrencyList.setValue(null),
        getData$: () => this._state.filterCurrencyList.getValue$().pipe(map(response => response?.data))
    });

    loadMemberOrdersList(request: GetMemberOrdersRequest, relevance = false): void {
        this._state.setMemberOrdersListLoading(true);
        request.sort = relevance && request.q ? null : request.sort; // with q parameter, default sorting by relevance
        this._api.getMemberOrders(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._state.setMemberOrdersListLoading(false))
            )
            .subscribe(orders => this._state.setMemberOrdersList(orders));
    }

    getMemberOrdersListData$(): Observable<MemberOrder[]> {
        return this._state.getMemberOrdersList$().pipe(getListData());
    }

    getMemberOrdersListMetadata$(): Observable<Metadata> {
        return this._state.getMemberOrdersList$().pipe(getMetadata());
    }

    getMemberOrdersListAggregatedData$(): Observable<AggregatedData> {
        return this._state.getMemberOrdersList$()
            .pipe(map(orders => orders?.aggregated_data && new AggregatedData(orders.aggregated_data, aggDataOrder)));
    }

    isMemberOrdersListLoading$(): Observable<boolean> {
        return this._state.isMemberOrdersListLoading$();
    }

    exportMemberOrders(request: GetMemberOrdersRequest, byMember: boolean, data: ExportRequest): Observable<ExportResponse> {
        this._state.setExportMemberOrdersLoading(true);
        return this._api.exportMemberOrders(request, byMember, data)
            .pipe(finalize(() => this._state.setExportMemberOrdersLoading(false)));
    }

    //CHANNEL ENTITIES
    loadFilterChannelEntityList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            MemberOrderFilterField.channelEntity,
            request,
            this._state.getFilterChannelEntityList$(),
            nextPage
        )
            .subscribe(result => {
                this._state.getChannelEntityCache().cacheItems(result.data);
                this._state.setFilterChannelEntityList(result);
            });
    }

    getFilterChannelEntityList$(): Observable<GetFilterResponse> {
        return this._state.getFilterChannelEntityList$();
    }

    getFilterChannelEntityListData$(): Observable<FilterOption[]> {
        return this.getFilterChannelEntityList$().pipe(map(resp => resp?.data));
    }

    getFilterChannelEntityNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.getChannelEntityCache()
            .getItems$(ids, id => this.loadFilterOption(SalesFilterField.channelEntity, id));
    }

    clearFilterListsData(): void {
        this._state.setFilterChannelEntityList(null);
        this._state.filterCurrencyList.setValue(null);
    }

    clearFilterListsCache(): void {
        this._state.getChannelEntityCache().clear();
        this._state.currencyCache.clear();
    }

    loadMemberOrderDetail(orderCode: string): void {
        this._state.setMemberOrderDetailLoading(true);
        this._state.setMemberOrderDetailError(null);
        this._api.getMemberOrder(orderCode)
            .pipe(
                catchError(error => {
                    this._state.setMemberOrderDetailError(error);
                    return of(null);
                }),
                finalize(() => this._state.setMemberOrderDetailLoading(false))
            )
            .subscribe(orderDetail =>
                this._state.setMemberOrderDetail(orderDetail)
            );
    }

    clearMemberOrderDetail(): void {
        this._state.setMemberOrderDetail(null);
    }

    getMemberOrderDetail$(): Observable<MemberOrderDetail> {
        return this._state.getMemberOrderDetail$();
    }

    getMemberOrderDetailError$(): Observable<HttpErrorResponse> {
        return this._state.getMemberOrderDetailError$();
    }

    isMemberOrderDetailLoading$(): Observable<boolean> {
        return this._state.isMemberOrderDetailLoading$();
    }

    isExportMemberOrdersLoading$(): Observable<boolean> {
        return this._state.isExportMemberOrdersLoading$();
    }

    isResendLoading$(): Observable<boolean> {
        return this._state.isResendLoading$();
    }

    private loadFilterOption(field: SalesFilterField, id: string): Observable<FilterOption> {
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

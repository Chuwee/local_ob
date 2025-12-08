import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { OrderItem, PriceTotalTaxes, TicketPrintType } from '@admin-clients/shared/common/data-access';
import {
    AggregatedData, AggregationMetrics, combineAggregatedData, ExportRequest, ExportResponse, FilterOption, ScrolledMetadata
} from '@admin-clients/shared/data-access/models';
import { distinctByField, runWithRetriesIfNull$ } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import moment from 'moment';
import { combineLatest, Observable, concat, of, switchMap, tap, withLatestFrom } from 'rxjs';
import { catchError, filter, finalize, map, mergeMap, take, toArray } from 'rxjs/operators';
import { GetFilterRequest } from '../models/get-filter-request.model';
import { GetFilterResponse } from '../models/get-filter-response.model';
import { OrdersApi } from './api/orders.api';
import { ExternalPermissionsResendRequest } from './models/external-permissions-resend-request.model';
import { GetOrdersRequest } from './models/get-orders-request.model';
import { GetOrdersWithFieldsRequestBody } from './models/get-orders-with-fields-request-body.model';
import { GetOrdersWithFieldsRequest } from './models/get-orders-with-fields-request.model';
import { OrderDetail } from './models/order-detail.model';
import { OrderFilterFields } from './models/order-filter-fields.enum';
import { OrderWithFields } from './models/order-with-fields.model';
import { Order } from './models/order.model';
import { aggDataOrder } from './models/orders-aggregated-data';
import {
    PostMassiveRefundOrdersRequest, PostMassiveRefundOrdersResponse, PostMassiveRefundOrdersSummaryRequest,
    PostMassiveRefundOrdersSummaryResponse
} from './models/post-massive-refund-orders.model';
import { RefundRequest } from './models/refund-request.model';
import { ResendData } from './models/resend-data.model';
import { RetryReimbursementRequestModel } from './models/retry-reimbursement-request.model';
import { OrdersState } from './state/orders.state';

@Injectable({
    providedIn: 'root'
})
export class OrdersService {

    readonly invoice = Object.freeze({
        resend: (orderCode: string, email: string) => StateManager.inProgress(
            this._state.invoice,
            this._api.resendInvoice(orderCode, email)
        ),
        loading$: () => this._state.invoice.isInProgress$()
    });

    readonly currencyAggregatedData = Object.freeze({
        load: (request: GetOrdersWithFieldsRequest) => {
            request.channel_entity_id = request.channel_id?.length ? undefined : request.channel_entity_id;
            request.event_entity_id = request.event_id?.length ? undefined : request.event_entity_id;
            StateManager.load(this._state.aggregations, this._api.getAggregations(request));
        },
        loading$: () => this._state.aggregations.isInProgress$(),
        clear: () => this._state.aggregations.setValue(null),
        getCombined$: () => combineLatest([
            this._state.aggregations.getValue$().pipe(filter(Boolean)),
            this._state.ordersWithFieldList.getValue$().pipe(
                map(orders => orders?.aggregated_data),
                filter(Boolean)
            )
        ]).pipe(
            map(([currencyAggregatedData, aggregatedData]) =>
                combineAggregatedData(currencyAggregatedData, aggregatedData, aggDataOrder))
        )
    });

    readonly weeklyCurrencyAggregatedData = Object.freeze({
        load: (request: GetOrdersWithFieldsRequest, lastDay: moment.Moment) => {
            StateManager.load(
                this._state.weeklyAggregations,
                concat(
                    ...Array(7).fill(null).map((_, daysAgo) => {
                        request.purchase_date_from = lastDay.clone().subtract(daysAgo, 'days').startOf('day').toJSON();
                        request.purchase_date_to = lastDay.clone().subtract(daysAgo, 'days').endOf('day').toJSON();
                        return this._api.getAggregations(request);
                    })
                ).pipe(
                    mergeMap((response, index) =>
                        of({
                            aggData: new AggregatedData(response, aggDataOrder),
                            weekDay: lastDay.clone().subtract(index, 'days').isoWeekday()
                        })
                    ),
                    toArray()
                )
            );
        },
        get$: () => this._state.weeklyAggregations.getValue$(),
        clear: () => {
            this._state.weeklyAggregations.setValue(null);
        },
        loading$: () => this._state.weeklyAggregations.isInProgress$()
    });

    readonly filterCurrencyList = Object.freeze({
        load: (request: GetFilterRequest) => StateManager.load(
            this._state.filterCurrencyList,
            this._api.getFilterOptions$(OrderFilterFields.currencies, request)
                .pipe(tap(response => this._state.currencyCache.cacheItems(response.data)))
        ),
        getNames$: (ids: string[]) => this._state.currencyCache
            .getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.currencies)),
        loading$: () => this._state.filterCurrencyList.isInProgress$(),
        clear: () => this._state.filterCurrencyList.setValue(null),
        getData$: () => this._state.filterCurrencyList.getValue$().pipe(map(response => response?.data))
    });

    readonly changeSeat = Object.freeze({
        setEnabled$:
            (code: string, enabled: boolean) => StateManager.inProgress(
                this._state.changeSeatOrder,
                this._api.setOrderChangeSeatEnabled(code, enabled)
            )
        ,
        load: (code: string) => StateManager.load(
            this._state.changeSeatOrder,
            this._api.getOrderChangeSeat(code)
        ),
        generatePromoterUrl$: (code: string, eventId: number) => StateManager.inProgress(
            this._state.changeSeatOrder,
            this._api.generateChangeSeatPromoterUrl(code, eventId)
        ),
        getData$: () => this._state.changeSeatOrder.getValue$(),
        clear: () => this._state.changeSeatOrder.setValue(null),
        loading$: () => this._state.changeSeatOrder.isInProgress$()
    });

    constructor(
        private _api: OrdersApi,
        private _state: OrdersState
    ) {
    }

    loadOrdersList(request: GetOrdersRequest): void {
        this._state.ordersList.setInProgress(true);
        request.channel_entity_id = request.channel_id?.length ? undefined : request.channel_entity_id;
        request.event_entity_id = request.event_id?.length ? undefined : request.event_entity_id;
        request.sort = request.q ? null : request.sort; // ordenación default por relevancia en búsqueda por parámetro q
        this._api.getOrders(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._state.ordersList.setInProgress(false))
            )
            .subscribe(orders => this._state.ordersList.setValue(orders));
    }

    getOrdersListData$(): Observable<Order[]> {
        return this._state.ordersList.getValue$().pipe(getListData());
    }

    getOrdersListMetadata$(): Observable<Metadata> {
        return this._state.ordersList.getValue$().pipe(getMetadata());
    }

    getOrdersListAggregatedData$(): Observable<AggregatedData> {
        return this._state.ordersList.getValue$()
            .pipe(map(orders => orders?.aggregated_data && new AggregatedData(orders.aggregated_data, aggDataOrder)));
    }

    isOrdersListLoading$(): Observable<boolean> {
        return this._state.ordersList.isInProgress$();
    }

    clearOrdersList(): void {
        this._state.ordersList.setValue(null);
    }

    //Orders with fields list

    loadOrdersWithFieldsList(request: GetOrdersWithFieldsRequest, body: GetOrdersWithFieldsRequestBody, relevance = false): void {
        this._state.ordersWithFieldList.setInProgress(true);
        request.channel_entity_id = request.channel_id?.length ? undefined : request.channel_entity_id;
        request.event_entity_id = request.event_id?.length ? undefined : request.event_entity_id;
        if (relevance && request.q) {
            request.sort = null; // ordenación default por relevancia en búsqueda por parámetro q
        }
        this._api.postOrdersWithFields(request, body)
            .pipe(
                mapMetadata(),
                finalize(() => this._state.ordersWithFieldList.setInProgress(false))
            )
            .subscribe(orders => this._state.ordersWithFieldList.setValue(orders));
    }

    getOrdersWithFieldsListData$(): Observable<OrderWithFields[]> {
        return this._state.ordersWithFieldList.getValue$().pipe(getListData());
    }

    getOrdersWithFieldsListMetadata$(): Observable<Metadata> {
        return this._state.ordersWithFieldList.getValue$().pipe(getMetadata());
    }

    getOrdersWithFieldsListAggregatedData$(aggregatedMetric: AggregationMetrics): Observable<AggregatedData> {
        return this._state.ordersWithFieldList.getValue$()
            .pipe(map(orders => orders?.aggregated_data && new AggregatedData(orders.aggregated_data, aggregatedMetric)));
    }

    isOrdersWithFieldsListLoading$(): Observable<boolean> {
        return this._state.ordersWithFieldList.isInProgress$();
    }

    isExportOrdersLoading$(): Observable<boolean> {
        return this._state.exportOrders.isInProgress$();
    }

    exportOrders(request: GetOrdersWithFieldsRequest, data: ExportRequest): Observable<ExportResponse> {
        this._state.exportOrders.setInProgress(true);
        return this._api.exportOrders(request, data)
            .pipe(finalize(() => this._state.exportOrders.setInProgress(false)));
    }

    clearOrdersWithFields(): void {
        this._state.ordersWithFieldList.setValue(null);
    }

    // CHANNELS ENTITIES

    loadFilterChannelEntityList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.channelEntity,
            request,
            this._state.filterChannelEntityList.getValue$(),
            nextPage
        )
            .subscribe(entities => {
                this._state.channelEntitiesCache.cacheItems(entities?.data);
                this._state.filterChannelEntityList.setValue(entities);
            });
    }

    getFilterChannelEntityListData$(): Observable<FilterOption[]> {
        return this._state.filterChannelEntityList.getValue$().pipe(map(entities => entities?.data));
    }

    getFilterChannelEntityListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterChannelEntityList.getValue$().pipe(map(entities => entities?.metadata));
    }

    getFilterChannelEntityNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.channelEntitiesCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.channelEntity));
    }

    // CHANNELS

    loadFilterChannelList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.channels,
            request,
            this._state.filterChannelList.getValue$(),
            nextPage
        )
            .subscribe(channels => {
                this._state.channelsCache.cacheItems(channels?.data);
                this._state.filterChannelList.setValue(channels);
            });
    }

    getFilterChannelListData$(): Observable<FilterOption[]> {
        return this._state.filterChannelList.getValue$().pipe(map(channels => channels?.data));
    }

    getFilterChannelListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterChannelList.getValue$().pipe(map(channels => channels?.metadata));
    }

    getFilterChannelNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.channelsCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.channels));
    }

    // EVENT ENTITIES

    loadFilterEventEntityList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.eventEntity,
            request,
            this._state.filterEventEntityList.getValue$(),
            nextPage
        )
            .subscribe(entities => {
                this._state.eventEntitiesCache.cacheItems(entities?.data);
                this._state.filterEventEntityList.setValue(entities);
            });
    }

    getFilterEventEntityListData$(): Observable<FilterOption[]> {
        return this._state.filterEventEntityList.getValue$().pipe(map(entity => entity?.data));
    }

    getFilterEventEntityListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterEventEntityList.getValue$().pipe(map(entity => entity?.metadata));
    }

    getFilterEventEntityNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.eventEntitiesCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.eventEntity));
    }

    // EVENTS

    loadFilterEventList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.events,
            request,
            this._state.filterEventList.getValue$(),
            nextPage
        )
            .subscribe(events => {
                this._state.eventsCache.cacheItems(events?.data);
                this._state.filterEventList.setValue(events);
            });
    }

    getFilterEventListData$(): Observable<FilterOption[]> {
        return this._state.filterEventList.getValue$().pipe(map(events => events?.data));
    }

    getFilterEventListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterEventList.getValue$().pipe(map(events => events?.metadata));
    }

    getFilterEventNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.eventsCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.events));
    }

    // SESSIONS

    loadFilterSessionList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.sessions,
            request,
            this._state.filterSessionList.getValue$(),
            nextPage
        )
            .subscribe(sessions => {
                this._state.sessionsCache.cacheItems(sessions?.data);
                this._state.filterSessionList.setValue(sessions);
            });
    }

    getFilterSessionListData$(): Observable<FilterOption[]> {
        return this._state.filterSessionList.getValue$().pipe(map(sessions => sessions?.data));
    }

    getFilterSessionListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterSessionList.getValue$().pipe(map(sessions => sessions?.metadata));
    }

    getFilterSessionNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.sessionsCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.sessions));
    }

    // MERCHANTS

    loadFilterMerchantList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.merchants,
            request,
            this._state.filterMerchantList.getValue$(),
            nextPage
        )
            .subscribe(merchants => {
                this._state.merchantsCache.cacheItems(merchants?.data);
                this._state.filterMerchantList.setValue(merchants);
            });
    }

    getFilterMerchantListData$(): Observable<FilterOption[]> {
        return this._state.filterMerchantList.getValue$().pipe(map(merchants => merchants?.data));
    }

    getFilterMerchantListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterMerchantList.getValue$().pipe(map(merchants => merchants?.metadata));
    }

    getFilterMerchantNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.merchantsCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.merchants));
    }

    // USERS

    loadFilterUserList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.users,
            request,
            this._state.filterUserList.getValue$(),
            nextPage
        )
            .subscribe(users => {
                this._state.usersCache.cacheItems(users?.data);
                this._state.filterUserList.setValue(users);
            });
    }

    loadFilterClientList(request: GetFilterRequest, nextPage = false): void {
        this.loadSalesFilterList(
            OrderFilterFields.clients,
            request,
            this._state.filterClientList.getValue$(),
            nextPage
        )
            .subscribe(users => {
                this._state.clientsCache.cacheItems(users?.data);
                this._state.filterClientList.setValue(users);
            });
    }

    getFilterUserListData$(): Observable<FilterOption[]> {
        return this._state.filterUserList.getValue$().pipe(map(users => users?.data));
    }

    getFilterUserListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterUserList.getValue$().pipe(map(users => users?.metadata));
    }

    getFilterUserNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.usersCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.users));
    }

    getFilterClientListData$(): Observable<FilterOption[]> {
        return this._state.filterClientList.getValue$().pipe(map(users => users?.data));
    }

    getFilterClientListMetadata$(): Observable<ScrolledMetadata> {
        return this._state.filterClientList.getValue$().pipe(map(users => users?.metadata));
    }

    getFilterClientNames$(ids: string[]): Observable<FilterOption[]> {
        return this._state.clientsCache.getItems$(ids, id => this.getFilterItem(id, OrderFilterFields.clients));
    }

    clearFilterListsData(): void {
        this._state.filterChannelEntityList.setValue(null);
        this._state.filterChannelList.setValue(null);
        this._state.filterEventEntityList.setValue(null);
        this._state.filterEventList.setValue(null);
        this._state.filterSessionList.setValue(null);
        this._state.filterMerchantList.setValue(null);
        this._state.filterUserList.setValue(null);
        this._state.filterClientList.setValue(null);
        this._state.filterCurrencyList.setValue(null);
    }

    clearFilterChannelList(): void {
        this._state.filterChannelList.setValue(null);
    }

    clearFilterEventList(): void {
        this._state.filterEventList.setValue(null);
    }

    clearFilterSessionList(): void {
        this._state.filterSessionList.setValue(null);
    }

    clearFilterListsCache(): void {
        this._state.channelEntitiesCache.clear();
        this._state.eventEntitiesCache.clear();
        this._state.channelsCache.clear();
        this._state.eventsCache.clear();
        this._state.sessionsCache.clear();
        this._state.merchantsCache.clear();
        this._state.usersCache.clear();
        this._state.clientsCache.clear();
        this._state.currencyCache.clear();
    }

    loadOrderDetail(orderCode: string): void {
        this._state.orderDetail.setInProgress(true);
        this._state.orderDetail.setError(null);
        this._api.getOrder(orderCode)
            .pipe(
                catchError(error => {
                    this._state.orderDetail.setError(error);
                    return of(null);
                }),
                finalize(() => this._state.orderDetail.setInProgress(false))
            )
            .subscribe(orderDetail =>
                this._state.orderDetail.setValue(orderDetail)
            );
    }

    clearOrderDetail(): void {
        this._state.orderDetail.setValue(null);
    }

    getOrderDetail$(): Observable<OrderDetail> {
        return this._state.orderDetail.getValue$();
    }

    getOrderDetailError$(): Observable<HttpErrorResponse> {
        return this._state.orderDetail.getError$();
    }

    resendOrder(orderCode: string, resendData: ResendData): Observable<void> {
        this._state.resendOrder.setInProgress(true);
        return this._api.resendOrder(orderCode, resendData)
            .pipe(finalize(() => this._state.resendOrder.setInProgress(false)));
    }

    regenerateOrder(orderCode: string): Observable<void> {
        this._state.regenerateOrder.setInProgress(true);
        return this._api.regenerateOrder(orderCode, [TicketPrintType.pdf, TicketPrintType.passbook]).pipe(
            finalize(() => this._state.regenerateOrder.setInProgress(false))
        );
    }

    cancelOrder(orderCode: string): Observable<void> {
        this._state.cancelOrder.setInProgress(true);
        return this._api.deleteOrder(orderCode).pipe(
            finalize(() => this._state.cancelOrder.setInProgress(false))
        );
    }

    refundOrder(orderCode: string, refundRequest: RefundRequest): Observable<HttpResponse<unknown>> {
        this._state.refundOrder.setInProgress(true);
        return this._api.refundOrder(orderCode, refundRequest).pipe(
            finalize(() => this._state.refundOrder.setInProgress(false))
        );
    }

    reimburseOrder(orderCode: string, transactionId: string, params: RetryReimbursementRequestModel): Observable<HttpResponse<unknown>> {
        this._state.reimburseOrder.setInProgress(true);
        return this._api.reimburseOrder(orderCode, transactionId, params).pipe(
            finalize(() => this._state.reimburseOrder.setInProgress(false))
        );
    }

    massiveRefundOrders(request: PostMassiveRefundOrdersRequest): Observable<PostMassiveRefundOrdersResponse> {
        this._state.massiveRefund.setInProgress(true);
        return this._api.postMassiveRefundOrders(request)
            .pipe(finalize(() => this._state.massiveRefund.setInProgress(false)));
    }

    isMassiveRefundLoading$(): Observable<boolean> {
        return this._state.massiveRefund.isInProgress$();
    }

    loadMassiveRefundSummary(request: PostMassiveRefundOrdersSummaryRequest): void {
        this._state.massiveRefundSummary.setInProgress(true);
        this._state.massiveRefundSummary.setError(null);
        this._api.postMassiveRefundOrdersSummary(request)
            .pipe(
                catchError(error => {
                    this._state.massiveRefundSummary.setError(error);
                    return of(null);
                }),
                finalize(() => this._state.massiveRefundSummary.setInProgress(false))
            )
            .subscribe(summary =>
                this._state.massiveRefundSummary.setValue(summary)
            );
    }

    clearMassiveRefundSummary(): void {
        this._state.massiveRefundSummary.setValue(null);
    }

    getMassiveRefundSummary$(): Observable<PostMassiveRefundOrdersSummaryResponse> {
        return this._state.massiveRefundSummary.getValue$();
    }

    getMassiveRefundSummaryError$(): Observable<HttpErrorResponse> {
        return this._state.massiveRefundSummary.getError$();
    }

    isMassiveRefundSummaryLoading$(): Observable<boolean> {
        return this._state.massiveRefundSummary.isInProgress$();
    }

    isReimburseOrderLoading$(): Observable<boolean> {
        return this._state.reimburseOrder.isInProgress$();
    }

    isOrderDetailLoading$(): Observable<boolean> {
        return this._state.orderDetail.isInProgress$();
    }

    isResendOrderLoading$(): Observable<boolean> {
        return this._state.resendOrder.isInProgress$();
    }

    isRefundOrderLoading$(): Observable<boolean> {
        return this._state.refundOrder.isInProgress$();
    }

    isTicketsLinkLoading$(): Observable<boolean> {
        return this._state.ticketsLink.isInProgress$();
    }

    isOrderReloading$(): Observable<boolean> {
        return this._state.orderReloading.isInProgress$();
    }

    isCancelOrderLoading$(): Observable<boolean> {
        return this._state.cancelOrder.isInProgress$();
    }

    isRegenerateOrderLoading$(): Observable<boolean> {
        return this._state.regenerateOrder.isInProgress$();
    }

    setOrderReloading(isReloading: boolean): void {
        this._state.orderReloading.setInProgress(isReloading);
    }

    getTicketsLink$(orderCode: string): Observable<string> {
        this._state.ticketsLink.setInProgress(true);
        return this.tryToGetTicketsLink$(orderCode).pipe(
            finalize(() => this._state.ticketsLink.setInProgress(false))
        );
    }

    refreshExternalPermissions(orderCode: string): Observable<void> {
        this._state.refreshExternalPermissions.setInProgress(true);
        return this._api.refreshExternalPermissions(orderCode).pipe(
            finalize(() => this._state.refreshExternalPermissions.setInProgress(false))
        );
    }

    resendExternalPermissions(request: ExternalPermissionsResendRequest): Observable<void> {
        this._state.resendExternalPermissions.setInProgress(true);
        return this._api.postExternalPermissions(request).pipe(
            finalize(() => this._state.resendExternalPermissions.setInProgress(false))
        );
    }

    isResendExternalPermissionsInProgress$(): Observable<boolean> {
        return this._state.resendExternalPermissions.isInProgress$();
    }

    calculateTaxesForItems(items: OrderItem[], order: OrderDetail): PriceTotalTaxes {
        const filteredItems = this.taxFilteredItems(items, order);
        if (!filteredItems || filteredItems.length === 0) return { items: -1, charges: -1 };
        const totalTaxesItem = filteredItems.reduce((total, item) => total + Math.abs((item.price?.taxes?.item?.total ?? 0)), 0);
        const totalTaxesCharges = filteredItems.reduce((total, item) => total + Math.abs((item.price?.taxes?.charges?.total ?? 0)), 0);
        return { items: totalTaxesItem, charges: totalTaxesCharges };
    }

    //Filters items based on order type for tax calculation:
    //   - Excludes All items other than refund and price less than zero
    //   - BOOKING: Excludes SOLD and REFUNDED items
    //   - ISSUE: Excludes SOLD items
    //   - FEFUND: Excludes all items if previous type is booking
    taxFilteredItems(items: OrderItem[], order: OrderDetail): OrderItem[] {
        const orderType = order.type;
        const previousOrderType = order?.previous_order?.type;
        const finalPrice = order?.price?.final;

        if (orderType !== 'REFUND' && (finalPrice != null && finalPrice < 0)) {
            return [];
        }

        switch (orderType) {
            case 'BOOKING':
                return items.filter(item =>
                    item?.related_product_state &&
                    item.related_product_state !== 'SOLD' &&
                    item.related_product_state !== 'REFUNDED'
                );
            case 'ISSUE':
                return items.filter(item =>
                    item?.related_product_state &&
                    item.related_product_state !== 'SOLD'
                );
            case 'REFUND':
                return ((previousOrderType && previousOrderType === 'BOOKING')) ? [] : items;
            default:
                return items;
        }
    }

    private getFilterItem(id: string, field: OrderFilterFields): Observable<FilterOption> {
        return this._api.getFilterOptions$(field, { q: id }).pipe(map(result => result.data?.[0]));
    }

    private tryToGetTicketsLink$(code: string): Observable<string> {
        return runWithRetriesIfNull$(() => this._api.getOrderTicketsPdf(code), 1, 10)
            .pipe(map(response => {
                if (response) {
                    return response.merged_download_link;
                }
                return null;
            }));
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
            result = currentObservable$
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
        return result.pipe(tap(response => response.data = distinctByField(response.data, item => item.id, true)));
    }
}

import { StateProperty } from '@OneboxTM/utils-state';
import { AggregatedData, FilterOption, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetOrdersResponse } from '../models/get-orders-response.model';
import { GetOrdersWithFieldsResponse } from '../models/get-orders-with-fields-response.model';
import { OrderChangeSeat } from '../models/order-change-seat.model';
import { OrderDetail } from '../models/order-detail.model';
import { PostMassiveRefundOrdersSummaryResponse } from '../models/post-massive-refund-orders.model';

@Injectable({
    providedIn: 'root'
})
export class OrdersState {
    readonly channelEntitiesCache = new ItemCache<FilterOption>();
    readonly eventEntitiesCache = new ItemCache<FilterOption>();
    readonly channelsCache = new ItemCache<FilterOption>();
    readonly eventsCache = new ItemCache<FilterOption>();
    readonly sessionsCache = new ItemCache<FilterOption>();
    readonly merchantsCache = new ItemCache<FilterOption>();
    readonly usersCache = new ItemCache<FilterOption>();
    readonly clientsCache = new ItemCache<FilterOption>();
    readonly currencyCache = new ItemCache<FilterOption>();

    readonly invoice = new StateProperty<void>();
    readonly aggregations = new StateProperty<ResponseAggregatedData>();
    readonly weeklyAggregations = new StateProperty<{ aggData: AggregatedData; weekDay: number }[]>();
    readonly ordersList = new StateProperty<GetOrdersResponse>();
    readonly ordersWithFieldList = new StateProperty<GetOrdersWithFieldsResponse>();
    readonly exportOrders = new StateProperty<void>();
    readonly filterCurrencyList = new StateProperty<GetFilterResponse>();
    readonly filterChannelList = new StateProperty<GetFilterResponse>();
    readonly filterChannelEntityList = new StateProperty<GetFilterResponse>();
    readonly filterEventList = new StateProperty<GetFilterResponse>();
    readonly filterEventEntityList = new StateProperty<GetFilterResponse>();
    readonly filterSessionList = new StateProperty<GetFilterResponse>();
    readonly filterMerchantList = new StateProperty<GetFilterResponse>();
    readonly filterUserList = new StateProperty<GetFilterResponse>();
    readonly filterClientList = new StateProperty<GetFilterResponse>();

    readonly orderDetail = new StateProperty<OrderDetail>();
    readonly resendOrder = new StateProperty<void>();
    readonly regenerateOrder = new StateProperty<void>();
    readonly cancelOrder = new StateProperty<void>();
    readonly refundOrder = new StateProperty<void>();
    readonly changeSeatOrder = new StateProperty<OrderChangeSeat>();
    readonly reimburseOrder = new StateProperty<void>();
    readonly massiveRefund = new StateProperty<void>();
    readonly massiveRefundSummary = new StateProperty<PostMassiveRefundOrdersSummaryResponse>();
    readonly ticketsLink = new StateProperty<void>();
    readonly orderReloading = new StateProperty<void>();
    readonly refreshExternalPermissions = new StateProperty<void>();
    readonly resendExternalPermissions = new StateProperty<void>();
    readonly weekAggregates = new StateProperty<{ aggData: AggregatedData; weekDay: number }[]>();
}

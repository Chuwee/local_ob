import { StateProperty } from '@OneboxTM/utils-state';
import { FilterOption, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetVoucherOrdersResponse } from '../models/get-voucher-orders-response.model';
import { VoucherOrderDetail } from '../models/voucher-order-detail.model';

@Injectable({
    providedIn: 'root'
})
export class VoucherOrdersState {
    readonly #channelCache = new ItemCache<FilterOption>();
    readonly #channelEntityCache = new ItemCache<FilterOption>();
    private _voucherOrdersList = new BaseStateProp<GetVoucherOrdersResponse>();
    readonly getVoucherOrdersList$ = this._voucherOrdersList.getValueFunction();
    readonly setVoucherOrdersList = this._voucherOrdersList.setValueFunction();
    readonly isVoucherOrdersListLoading$ = this._voucherOrdersList.getInProgressFunction();
    readonly setVoucherOrdersListLoading = this._voucherOrdersList.setInProgressFunction();

    private _exportVoucherOrders = new BaseStateProp<void>();
    readonly isExportVoucherOrdersLoading$ = this._exportVoucherOrders.getInProgressFunction();
    readonly setExportVoucherOrdersLoading = this._exportVoucherOrders.setInProgressFunction();

    private _filterChannelList = new BaseStateProp<GetFilterResponse>();
    readonly getFilterChannelList$ = this._filterChannelList.getValueFunction();
    readonly setFilterChannelList = this._filterChannelList.setValueFunction();

    private _filterChannelEntityList = new BaseStateProp<GetFilterResponse>();
    readonly getFilterChannelEntityList$ = this._filterChannelEntityList.getValueFunction();
    readonly setFilterChannelEntityList = this._filterChannelEntityList.setValueFunction();

    private _voucherOrderDetail = new BaseStateProp<VoucherOrderDetail>();
    readonly getVoucherOrderDetail$ = this._voucherOrderDetail.getValueFunction();
    readonly setVoucherOrderDetail = this._voucherOrderDetail.setValueFunction();
    readonly getVoucherOrderDetailError$ = this._voucherOrderDetail.getErrorFunction();
    readonly setVoucherOrderDetailError = this._voucherOrderDetail.setErrorFunction();
    readonly isVoucherOrderDetailLoading$ = this._voucherOrderDetail.getInProgressFunction();
    readonly setVoucherOrderDetailLoading = this._voucherOrderDetail.setInProgressFunction();

    private _resend = new BaseStateProp<void>();
    readonly isResendLoading$ = this._resend.getInProgressFunction();
    readonly setResendLoading = this._resend.setInProgressFunction();

    readonly aggregations = new StateProperty<ResponseAggregatedData>();
    readonly filterCurrencyList = new StateProperty<GetFilterResponse>();
    readonly currencyCache = new ItemCache<FilterOption>();

    readonly merchantsCache = new ItemCache<FilterOption>();
    readonly filterMerchantList = new StateProperty<GetFilterResponse>();

    getChannelCache(): ItemCache<FilterOption> {
        return this.#channelCache;
    }

    getChannelEntityCache(): ItemCache<FilterOption> {
        return this.#channelEntityCache;
    }
}

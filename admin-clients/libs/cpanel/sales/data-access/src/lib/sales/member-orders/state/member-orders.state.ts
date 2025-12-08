import { StateProperty } from '@OneboxTM/utils-state';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetMemberOrdersResponse } from '../models/get-member-orders-response.model';
import { MemberOrderDetail } from '../models/member-order-detail.model';

@Injectable({
    providedIn: 'root'
})
export class MemberOrdersState {
    readonly #channelEntityCache = new ItemCache<FilterOption>();

    private _memberOrdersList = new BaseStateProp<GetMemberOrdersResponse>();
    readonly getMemberOrdersList$ = this._memberOrdersList.getValueFunction();
    readonly setMemberOrdersList = this._memberOrdersList.setValueFunction();
    readonly isMemberOrdersListLoading$ = this._memberOrdersList.getInProgressFunction();
    readonly setMemberOrdersListLoading = this._memberOrdersList.setInProgressFunction();

    private _exportMemberOrders = new BaseStateProp<void>();
    readonly isExportMemberOrdersLoading$ = this._exportMemberOrders.getInProgressFunction();
    readonly setExportMemberOrdersLoading = this._exportMemberOrders.setInProgressFunction();

    private readonly _filterChannelEntityList = new BaseStateProp<GetFilterResponse>();
    readonly setFilterChannelEntityList = this._filterChannelEntityList.setValueFunction();
    readonly getFilterChannelEntityList$ = this._filterChannelEntityList.getValueFunction();

    private _memberOrderDetail = new BaseStateProp<MemberOrderDetail>();
    readonly getMemberOrderDetail$ = this._memberOrderDetail.getValueFunction();
    readonly setMemberOrderDetail = this._memberOrderDetail.setValueFunction();
    readonly getMemberOrderDetailError$ = this._memberOrderDetail.getErrorFunction();
    readonly setMemberOrderDetailError = this._memberOrderDetail.setErrorFunction();
    readonly isMemberOrderDetailLoading$ = this._memberOrderDetail.getInProgressFunction();
    readonly setMemberOrderDetailLoading = this._memberOrderDetail.setInProgressFunction();

    private _resend = new BaseStateProp<void>();
    readonly isResendLoading$ = this._resend.getInProgressFunction();
    readonly setResendLoading = this._resend.setInProgressFunction();

    readonly filterCurrencyList = new StateProperty<GetFilterResponse>();
    readonly currencyCache = new ItemCache<FilterOption>();

    getChannelEntityCache(): ItemCache<FilterOption> {
        return this.#channelEntityCache;
    }
}

import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterRequest } from '../../models/get-filter-request.model';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetMemberOrdersRequest } from '../models/get-member-orders-request.model';
import { GetMemberOrdersResponse } from '../models/get-member-orders-response.model';
import { MemberOrderDetail } from '../models/member-order-detail.model';

@Injectable({
    providedIn: 'root'
})
export class MemberOrdersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly MEMBER_ORDER_ITEMS_BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/member-order-items`;
    private readonly MEMBER_ORDERS_BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/member-orders`;

    private readonly _http = inject(HttpClient);

    getMemberOrders(request: GetMemberOrdersRequest): Observable<GetMemberOrdersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetMemberOrdersResponse>(this.MEMBER_ORDERS_BASE_URL, { params });
    }

    exportMemberOrders(request: GetMemberOrdersRequest, byMember: boolean, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(
            `${byMember ? this.MEMBER_ORDER_ITEMS_BASE_URL : this.MEMBER_ORDERS_BASE_URL}/exports`, body, { params });
    }

    getFilterOptions$(filterName: string, request: GetFilterRequest): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetFilterResponse>(this.MEMBER_ORDERS_BASE_URL + '/filters/' + filterName, { params });
    }

    getMemberOrder(code: string): Observable<MemberOrderDetail> {
        return this._http.get<MemberOrderDetail>(`${this.MEMBER_ORDERS_BASE_URL}/${code}`);
    }
}

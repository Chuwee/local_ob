import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterRequest } from '../../models/get-filter-request.model';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetVoucherOrdersRequest } from '../models/get-voucher-orders-request.model';
import { GetVoucherOrdersResponse } from '../models/get-voucher-orders-response.model';
import { ResendVoucherOrderType } from '../models/resend-voucher-order-type.enum';
import { VoucherOrderDetail } from '../models/voucher-order-detail.model';

@Injectable({
    providedIn: 'root'
})
export class VoucherOrdersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/voucher-orders`;

    private readonly _http = inject(HttpClient);

    getVoucherOrders(request: GetVoucherOrdersRequest): Observable<GetVoucherOrdersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetVoucherOrdersResponse>(this.BASE_URL, {
            params
        });
    }

    exportVoucherOrders(request: GetVoucherOrdersRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(this.BASE_URL + '/exports', body, {
            params
        });
    }

    getFilterOptions$(filterName: string, request: GetFilterRequest): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetFilterResponse>(this.BASE_URL + '/filters/' + filterName, { params });
    }

    getVoucherOrder(code: string): Observable<VoucherOrderDetail> {
        return this._http.get<VoucherOrderDetail>(`${this.BASE_URL}/${code}`);
    }

    resend(code: string, types: ResendVoucherOrderType[], email?: string): Observable<void> {
        const resendConfig = { types, email_address: email || null };
        return this._http.post<void>(`${this.BASE_URL}/${code}/resend`, resendConfig);
    }

    getAggregations(request: GetVoucherOrdersRequest): Observable<ResponseAggregatedData> {
        const params = buildHttpParams(request);
        return this._http.get<ResponseAggregatedData>(`${this.BASE_URL}/aggregations`, { params });
    }
}

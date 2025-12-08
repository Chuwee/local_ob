import { buildHttpParams } from '@OneboxTM/utils-http';
import { TicketPrintType } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterRequest } from '../../models/get-filter-request.model';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { ExternalPermissionsResendRequest } from '../models/external-permissions-resend-request.model';
import { GetOrderTicketsPdfResponse } from '../models/get-order-tickets-pdf.model';
import { GetOrdersRequest } from '../models/get-orders-request.model';
import { GetOrdersResponse } from '../models/get-orders-response.model';
import { GetOrdersWithFieldsRequestBody } from '../models/get-orders-with-fields-request-body.model';
import { GetOrdersWithFieldsRequest } from '../models/get-orders-with-fields-request.model';
import { GetOrdersWithFieldsResponse } from '../models/get-orders-with-fields-response.model';
import { OrderChangeSeat } from '../models/order-change-seat.model';
import { OrderDetail } from '../models/order-detail.model';
import {
    PostMassiveRefundOrdersRequest,
    PostMassiveRefundOrdersResponse,
    PostMassiveRefundOrdersSummaryRequest,
    PostMassiveRefundOrdersSummaryResponse
} from '../models/post-massive-refund-orders.model';
import { RefundRequest } from '../models/refund-request.model';
import { ResendData } from '../models/resend-data.model';
import { RetryReimbursementRequestModel } from '../models/retry-reimbursement-request.model';

@Injectable({
    providedIn: 'root'
})
export class OrdersApi {
    private readonly _http = inject(HttpClient);

    private readonly BASE_API = inject(APP_BASE_API);

    private readonly BASE_ORDERS_URL = `${this.BASE_API}/orders-mgmt-api/v1/orders`;
    private readonly BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1`;

    getOrders(request: GetOrdersRequest): Observable<GetOrdersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetOrdersResponse>(this.BASE_ORDERS_URL, { params });
    }

    //Get orders returning the specific fields on the request
    postOrdersWithFields(
        request: GetOrdersWithFieldsRequest, body: GetOrdersWithFieldsRequestBody
    ): Observable<GetOrdersWithFieldsResponse> {
        const params = buildHttpParams(request);
        return this._http.post<GetOrdersWithFieldsResponse>(this.BASE_ORDERS_URL, body, { params });
    }

    getAggregations(request: GetOrdersWithFieldsRequest): Observable<ResponseAggregatedData> {
        const params = buildHttpParams(request);
        return this._http.get<ResponseAggregatedData>(`${this.BASE_ORDERS_URL}/aggregations`, { params });
    }

    exportOrders(request: GetOrdersWithFieldsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(this.BASE_ORDERS_URL + '/exports', body, { params });
    }

    getOrder(orderCode: string): Observable<OrderDetail> {
        return this._http.get<OrderDetail>(`${this.BASE_ORDERS_URL}/${orderCode}`);
    }

    deleteOrder(code: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_ORDERS_URL}/${code}`, {});
    }

    regenerateOrder(code: string, ticketPrintTypes: TicketPrintType[]): Observable<void> {
        return this._http.post<void>(`${this.BASE_ORDERS_URL}/${code}/regenerate`, {
            ticket_type: ticketPrintTypes,
            full_regeneration: true
        });
    }

    getFilterOptions$(filterName: string, request: GetFilterRequest): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetFilterResponse>(this.BASE_ORDERS_URL + '/filters/' + filterName, { params });
    }

    resendOrder(code: string, resendData: ResendData): Observable<void> {
        return this._http.post<void>(`${this.BASE_ORDERS_URL}/${code}/resend`, resendData);
    }

    refundOrder(code: string, refundRequest: RefundRequest): Observable<HttpResponse<unknown>> {
        return this._http.post<HttpResponse<unknown>>(
            `${this.BASE_ORDERS_URL}/${code}/refund`, refundRequest, { observe: 'response' });
    }

    resendInvoice(code: string, email: string): Observable<void> {
        return this._http.post<void>(`${this.BASE_ORDERS_URL}/${code}/invoices/resend`, { email });
    }

    getOrderChangeSeat(code: string): Observable<OrderChangeSeat> {
        return this._http.get<OrderChangeSeat>(`${this.BASE_ORDERS_URL}/${code}/change-seat`);
    }

    generateChangeSeatPromoterUrl(code: string, eventId: number): Observable<string> {
        return this._http.post<string>(`${this.BASE_ORDERS_URL}/${code}/change-seat/regenerate`, { event_id: eventId });
    }

    setOrderChangeSeatEnabled(code: string, enabled: boolean): Observable<void> {
        return this._http.post<void>(`${this.BASE_ORDERS_URL}/${code}/change-seat`, { enabled });
    }

    reimburseOrder(code: string, transactionId: string, params: RetryReimbursementRequestModel): Observable<HttpResponse<unknown>> {
        return this._http.put<HttpResponse<unknown>>(
            `${this.BASE_ORDERS_URL}/${code}/reimburse/${transactionId}`, params, { observe: 'response' });
    }

    getOrderTicketsPdf(code: string): Observable<GetOrderTicketsPdfResponse> {
        return this._http.get<GetOrderTicketsPdfResponse>(this.BASE_ORDERS_URL + '/' + code + '/print', {
            params: { merged: 'true', full_regeneration: true }
        });
    }

    refreshExternalPermissions(code: string): Observable<void> {
        return this._http.put<void>(`${this.BASE_ORDERS_URL}/${code}/external-permissions`, {});
    }

    // Resend external permissions
    postExternalPermissions(request: ExternalPermissionsResendRequest): Observable<void> {
        return this._http.post<void>(`${this.BASE_URL}/external-permissions/resend`, request);
    }

    postMassiveRefundOrdersSummary(request: PostMassiveRefundOrdersSummaryRequest): Observable<PostMassiveRefundOrdersSummaryResponse> {
        return this._http.post<PostMassiveRefundOrdersSummaryResponse>(`${this.BASE_ORDERS_URL}/summary-massive-refunds`, request);
    }

    postMassiveRefundOrders(request: PostMassiveRefundOrdersRequest): Observable<PostMassiveRefundOrdersResponse> {
        return this._http.post<PostMassiveRefundOrdersResponse>(`${this.BASE_ORDERS_URL}/massive-refunds`, request);
    }
}

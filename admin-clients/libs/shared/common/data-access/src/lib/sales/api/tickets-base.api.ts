import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetStateHistoryResponse } from '../models/get-state-history-response.model';
import { GetTicketPdfResponse } from '../models/get-ticket-pdf.model';
import { GetTicketsRequest } from '../models/get-tickets-request.model';
import { GetTicketsResponse } from '../models/get-tickets-response.model';
import { TicketAttendeeHistory } from '../models/ticket-detail-attendees-history.model';
import { TicketDetail } from '../models/ticket-detail.model';
import {
    DeleteTicketTransferRequest,
    PostTicketReleaseRequest,
    PostTicketTransferRequest
} from '../models/transfer-ticket-request.model';
import { RenewalDetails } from '../models/ticket-seat-management-data.model';

@Injectable()
export class TicketsBaseApi {
    protected readonly BASE_API = inject(APP_BASE_API);
    protected readonly TICKETS_BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/order-items`;
    protected readonly ORDERS_BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/orders`;
    protected readonly http = inject(HttpClient);

    getTickets(request: GetTicketsRequest): Observable<GetTicketsResponse> {
        const params = buildHttpParams(request);
        return this.http.get<GetTicketsResponse>(this.TICKETS_BASE_URL, { params });
    }

    getAggregations(request: GetTicketsRequest): Observable<ResponseAggregatedData> {
        const params = buildHttpParams(request);
        return this.http.get<ResponseAggregatedData>(`${this.TICKETS_BASE_URL}/aggregations`, { params });
    }

    getTicket(orderCode: string, itemId: string): Observable<TicketDetail> {
        return this.http.get<TicketDetail>(`${this.ORDERS_BASE_URL}/${orderCode}/items/${itemId}`);
    }

    getTicketStateHistory(orderCode: string, itemId: string): Observable<GetStateHistoryResponse> {
        return this.http.get<GetStateHistoryResponse>(`${this.ORDERS_BASE_URL}/${orderCode}/order-items/${itemId}/state-history`);
    }

    getTicketAttendeeHistory$(orderCode: string, itemId: number): Observable<TicketAttendeeHistory[]> {
        return this.http.get<TicketAttendeeHistory[]>(`${this.ORDERS_BASE_URL}/${orderCode}/items/${itemId}/attendant-history`);
    }

    getTicketAttendeeFields$(orderCode: string, itemId: number): Observable<Record<string, string>> {
        return this.http.get<Record<string, string>>(`${this.ORDERS_BASE_URL}/${orderCode}/items/${itemId}/attendant`);
    }

    postTicketAttendant(orderCode: string, itemId: string, attendantData: Record<string, string>): Observable<void> {
        const params = buildHttpParams({ force_regenerate: true });
        return this.http.post<void>(`${this.ORDERS_BASE_URL}/${orderCode}/items/${itemId}/attendant`, attendantData, { params });
    }

    putRenewalDetails(orderCode: string, itemId: string, renewalDetails: RenewalDetails): Observable<void> {
        return this.http.put<void>(`${this.ORDERS_BASE_URL}/${orderCode}/items/${itemId}/renewal-details`, renewalDetails);
    }

    exportTickets(request: GetTicketsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this.http.post<ExportResponse>(this.TICKETS_BASE_URL + '/exports', body, { params });
    }

    exportTicketActions(request: GetTicketsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this.http.post<ExportResponse>(this.TICKETS_BASE_URL + '/actions-history/exports', body, { params });
    }

    transfer(request: PostTicketTransferRequest): Observable<void> {
        const { code, itemId, ...body } = request;
        return this.http.post<void>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/transfer`, body);
    }

    deleteTransfer(request: DeleteTicketTransferRequest): Observable<void> {
        const { code, itemId, session_id: sessionId } = request;
        return this.http.delete<void>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/transfer/${sessionId}`);
    }

    getTransferPdf(code: string, itemId: number, sessionId: number): Observable<GetTicketPdfResponse> {
        return this.http.get<GetTicketPdfResponse>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/transfer/${sessionId}/print`);
    }

    resendTransferEmail(code: string, itemId: number, sessionId: number): Observable<void> {
        return this.http.post<void>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/transfer/${sessionId}/resend`, null);
    }

    getTicketPdf(code: string, itemId: number): Observable<GetTicketPdfResponse> {
        return this.http.get<GetTicketPdfResponse>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/print`, {
            params: { merged: 'true' }
        });
    }

    release(request: PostTicketReleaseRequest): Observable<void> {
        const { code, itemId, ...body } = request;
        return this.http.post<void>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/release`, body);
    }

    deleteRelease(request: DeleteTicketTransferRequest): Observable<void> {
        const { code, itemId, session_id: sessionId } = request;
        return this.http.delete<void>(`${this.ORDERS_BASE_URL}/${code}/items/${itemId}/release/${sessionId}`);
    }
}

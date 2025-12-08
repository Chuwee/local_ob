import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { GetTicketPassbookRequest } from '../models/get-ticket-passbook-request.model';
import { GetTicketPassbookResponse } from '../models/get-ticket-passbook-response.model';
import { PostTicketPassbok } from '../models/post-ticket-passbook.model';
import { TicketPassbookAvailableFields } from '../models/ticket-passbook-available-fields.model';
import { TicketPassbookLiterals } from '../models/ticket-passbook-literals.model';
import { TicketPassbookType } from '../models/ticket-passbook-type.enum';
import { PutTicketPassbook, TicketPassbook } from '../models/ticket-passbook.model';

@Injectable({
    providedIn: 'root'
})
export class TicketsPassbookApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly TICKET_PASSBOOK_API = `${this.BASE_API}/mgmt-api/v1/passbook-templates`;

    private readonly _http = inject(HttpClient);

    getTicketsPassbook(request: GetTicketPassbookRequest): Observable<GetTicketPassbookResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            entity_id: request.entity_id,
            type: request.type,
            create_date: getRangeParam(request.create_start_date, request.create_end_date)
        });
        return this._http.get<GetTicketPassbookResponse>(this.TICKET_PASSBOOK_API, { params });
    }

    deleteTicketPassbook(ticketPassbookCode: string, entityId: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.delete<void>(`${this.TICKET_PASSBOOK_API}/${ticketPassbookCode}`, { params });
    }

    postTicketPassbook(ticketPassbook: PostTicketPassbok): Observable<{ code: number }> {
        return this._http.post<{ code: number }>(this.TICKET_PASSBOOK_API, ticketPassbook);
    }

    getTicketPassbook(id: string, entityId: string): Observable<TicketPassbook> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<TicketPassbook>(`${this.TICKET_PASSBOOK_API}/${id}`, { params });
    }

    putTicketPassbook(ticketPassbook: PutTicketPassbook, entityId: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.put<void>(`${this.TICKET_PASSBOOK_API}/${ticketPassbook.code}`, ticketPassbook, { params });
    }

    getTicketPassbookTemplateLiterals(ticketPassbookCode: string, langCode: string, entityId: string): Observable<TicketPassbookLiterals> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<TicketPassbookLiterals>(`${this.TICKET_PASSBOOK_API}/${ticketPassbookCode}/literals/${langCode}`, { params });
    }

    putTicketPassbookTemplateLiterals(
        ticketPassbookTemplateLiterals: TicketPassbookLiterals[],
        ticketPassbookCode: string,
        langCode: string,
        entityId: string
    ): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.put<void>(
            `${this.TICKET_PASSBOOK_API}/${ticketPassbookCode}/literals/${langCode}`,
            ticketPassbookTemplateLiterals, { params });
    }

    getDownloadUrlTicketPassbookPreview$(passbookCode: string, entityId: string, language?: string): Observable<{ download_url: string }> {
        const params = buildHttpParams({
            entity_id: entityId,
            language
        });
        return this._http.get<{ download_url: string }>(`${this.TICKET_PASSBOOK_API}/${passbookCode}/preview`, { params });
    }

    getTicketPassbookAvailableFields(type: TicketPassbookType): Observable<TicketPassbookAvailableFields[]> {
        const params = buildHttpParams({ type });
        return this._http.get<TicketPassbookAvailableFields[]>(`${this.TICKET_PASSBOOK_API}/available-fields`, { params });
    }

    getTicketPassbookCustomTemplateLiterals(): Observable<string[]> {
        return this._http.get<string[]>(`${this.TICKET_PASSBOOK_API}/available-literals`);
    }

    getTicketPassbookCustomTemplatePlaceholders(type: TicketPassbookType): Observable<string[]> {
        const params = buildHttpParams({ type });
        return this._http.get<string[]>(`${this.TICKET_PASSBOOK_API}/available-data-placeholders`, { params });
    }
}

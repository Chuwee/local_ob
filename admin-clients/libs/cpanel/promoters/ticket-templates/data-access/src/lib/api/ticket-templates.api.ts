import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetTicketTemplatesRequest } from '../models/get-ticket-templates-request.model';
import { GetTicketTemplatesResponse } from '../models/get-ticket-templates-response.model';
import { PostTicketTemplate } from '../models/post-ticket-template.model';
import { PutTicketTemplate } from '../models/put-ticket-template.model';
import { TicketTemplateFormat } from '../models/ticket-template-format.enum';
import { TicketTemplateImageType } from '../models/ticket-template-image-type.enum';
import { GetTicketTemplateImage, PostTicketTemplateImage } from '../models/ticket-template-image.model';
import { TicketTemplateText } from '../models/ticket-template-text.model';
import { TicketTemplate, TicketTemplateDesign } from '../models/ticket-template.model';

@Injectable({
    providedIn: 'root'
})
export class TicketTemplatesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly TICKET_TEMPLATE_API = `${this.BASE_API}/mgmt-api/v1/ticket-templates`;

    private readonly _http = inject(HttpClient);

    getTicketTemplates(request: GetTicketTemplatesRequest): Observable<GetTicketTemplatesResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetTicketTemplatesResponse>(this.TICKET_TEMPLATE_API, {
            params
        });
    }

    getTicketTemplate(id: string): Observable<TicketTemplate> {
        return this._http.get<TicketTemplate>(`${this.TICKET_TEMPLATE_API}/${id}`);
    }

    postTicketTemplate(ticketTemplate: PostTicketTemplate): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(this.TICKET_TEMPLATE_API, ticketTemplate);
    }

    putTicketTemplate(id: number, ticketTemplate: PutTicketTemplate): Observable<void> {
        return this._http.put<void>(`${this.TICKET_TEMPLATE_API}/${id}`, ticketTemplate);
    }

    deleteTicketTemplate(id: string): Observable<void> {
        return this._http.delete<void>(`${this.TICKET_TEMPLATE_API}/${id}`);
    }

    cloneTicketTemplate(id: number, name: string, entityId: number): Observable<{ id: number }> {
        const ticketTpl: { name: string; entity_id?: number } = { name };
        if (entityId) {
            ticketTpl.entity_id = entityId;
        }
        return this._http.post<{ id: number }>(`${this.TICKET_TEMPLATE_API}/${id}/clone`, ticketTpl);
    }

    getDesigns(): Observable<TicketTemplateDesign[]> {
        return this._http.get<TicketTemplateDesign[]>(`${this.TICKET_TEMPLATE_API}/designs`);
    }

    getPrinters(): Observable<string[]> {
        return this._http.get<string[]>(`${this.TICKET_TEMPLATE_API}/printers`);
    }

    getPaperTypes(): Observable<string[]> {
        return this._http.get<string[]>(`${this.TICKET_TEMPLATE_API}/paper-types`);
    }

    getTicketTemplateTexts(id: number, language?: string, type?: string): Observable<TicketTemplateText[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<TicketTemplateText[]>(`${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/texts`, { params });
    }

    postTicketTemplateTexts(texts: TicketTemplateText[], id: number): Observable<TicketTemplateText[]> {
        return this._http.post<TicketTemplateText[]>(`${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/texts`, texts);
    }

    getTicketTemplateLiterals(id: number, language?: string, type?: string): Observable<TicketTemplateText[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<TicketTemplateText[]>(`${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/literals`, { params });
    }

    postTicketTemplateLiterals(literals: TicketTemplateText[], id: number): Observable<TicketTemplateText[]> {
        return this._http.post<TicketTemplateText[]>(`${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/literals`, literals);
    }

    getTicketTemplateImages$(
        id: number,
        format: TicketTemplateFormat,
        language: string,
        type: TicketTemplateImageType
    ): Observable<GetTicketTemplateImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<GetTicketTemplateImage[]>(
            `${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/${format}/images`, { params }
        );
    }

    postTicketTemplateImages$(
        id: number,
        format: TicketTemplateFormat,
        images: PostTicketTemplateImage[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/${format}/images`, images
        );
    }

    deleteTicketTemplateImage$(
        id: number,
        format: TicketTemplateFormat,
        language: string,
        type: TicketTemplateImageType
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.TICKET_TEMPLATE_API}/${id}/ticket-contents/${format}/images/languages/${language}/types/${type}`);
    }

    downloadTicketPdfPreview$(ticketId: number, language: string): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this._http.get<{ url: string }>(`${this.TICKET_TEMPLATE_API}/${ticketId}/preview`, { params });
    }

}

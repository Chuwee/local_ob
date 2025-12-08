import { buildHttpParams } from '@OneboxTM/utils-http';
import {
    TicketContentImageType, TicketContentImage, TicketContentImageRequest, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Code, Id } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventChannelContentImageType } from '../models/event-channel-content-image-type.enum';
import { EventChannelContentImage } from '../models/event-channel-content-image.model';
import { EventChannelContentImageRequest } from '../models/event-channel-content-image.request.model';
import { EventChannelContentText } from '../models/event-channel-content-text.model';
import { EventTicketTemplateType } from '../models/event-ticket-template-type.enum';
import { EventTicketTemplate } from '../models/event-ticket-template.model';
import { TicketContentFormat } from '../models/ticket-content-format.enum';

@Injectable({
    providedIn: 'root'
})
export class EventCommunicationApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;

    private readonly _http = inject(HttpClient);

    getEventChannelContentTexts(eventId: number): Observable<EventChannelContentText[]> {
        return this._http.get<EventChannelContentText[]>(`${this.EVENTS_API}/${eventId}/channel-contents/texts`);
    }

    postEventChannelContentTexts(eventId: number, contents: EventChannelContentText[]): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/channel-contents/texts`, contents);
    }

    getEventChannelContentImages(
        eventId: number, language: string, type: EventChannelContentImageType
    ): Observable<EventChannelContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<EventChannelContentImage[]>(`${this.EVENTS_API}/${eventId}/channel-contents/images`, { params });
    }

    postEventChannelContentImages(eventId: number, contents: EventChannelContentImageRequest[]): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/channel-contents/images`, contents);
    }

    deleteEventChannelContentImage(
        eventId: number, language: string, type: EventChannelContentImageType, position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}/channel-contents/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    getEventTicketTemplates(eventId: number): Observable<EventTicketTemplate[]> {
        return this._http.get<EventTicketTemplate[]>(`${this.EVENTS_API}/${eventId}/ticket-templates`);
    }

    postEventTicketTemplate(
        eventId: number, templateId: Id | Code, type: EventTicketTemplateType, format: TicketContentFormat
    ): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/ticket-templates/${type}/${format}`, templateId);
    }

    getEventTicketContentTexts(ticketType: TicketType, eventId: number, format: TicketContentFormat): Observable<TicketContentText[]> {
        return this._http.get<TicketContentText[]>(`${this.getEventTicketContentUrl(ticketType, eventId, format)}/texts`);
    }

    postEventTicketContentTexts(
        ticketType: TicketType, eventId: number, format: TicketContentFormat, contents: TicketContentText[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.getEventTicketContentUrl(ticketType, eventId, format)}/texts`,
            contents
        );
    }

    getEventTicketContentImages(
        ticketType: TicketType, eventId: number, format: TicketContentFormat, language: string, type: TicketContentImageType
    ): Observable<TicketContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<TicketContentImage[]>(
            `${this.getEventTicketContentUrl(ticketType, eventId, format)}/images`,
            { params }
        );
    }

    postEventTicketContentImages(
        ticketType: TicketType, eventId: number, format: TicketContentFormat, contents: TicketContentImageRequest[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.getEventTicketContentUrl(ticketType, eventId, format)}/images`,
            contents
        );
    }

    deleteEventTicketContentImage(
        ticketType: TicketType, eventId: number, format: TicketContentFormat, language: string, type: TicketContentImageType
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.getEventTicketContentUrl(ticketType, eventId, format)}/images/languages/${language}/types/${type}`
        );
    }

    downloadTicketPdfPreview$(eventId: number, type: EventTicketTemplateType, language: string): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this._http.get<{ url: string }>(`${this.EVENTS_API}/${eventId}/ticket-templates/${type}/preview`, { params });
    }

    getDownloadUrlPassbookPreview$(eventId: number): Observable<{ download_url: string }> {
        return this._http.get<{ download_url: string }>(`${this.EVENTS_API}/${eventId}/ticket-templates/passbook/preview`);
    }

    private getEventTicketContentUrl(ticketType: TicketType, eventId: number, format: TicketContentFormat): string {
        return `${this.EVENTS_API}/${eventId}/ticket${ticketType === TicketType.invitation ? '-invitation' : ''}-contents/${format}`;
    }
}

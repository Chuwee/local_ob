import { buildHttpParams } from '@OneboxTM/utils-http';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { SessionChannelImageRequest } from '../models/session-channel-image-request.model';
import { SessionChannelImage } from '../models/session-channel-image.model';
import { SessionChannelText } from '../models/session-channel-text.model';
import { SessionTicketContentFormat } from '../models/session-ticket-content-format.enum';
import { SessionTicketImageRequest } from '../models/session-ticket-image-request.model';
import { SessionTicketImage } from '../models/session-ticket-image.model';
import { SessionTicketText } from '../models/session-ticket-text.model';

@Injectable({
    providedIn: 'root'
})
export class SessionCommunicationApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;

    constructor(private _http: HttpClient) { }

    getChannelTexts(eventId: number, sessionId: number): Observable<SessionChannelText[]> {
        return this._http.get<SessionChannelText[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/channel-contents/texts`);
    }

    postChannelTexts(eventId: number, sessionsIds: number[], texts: SessionChannelText[]): Observable<void> {
        if (sessionsIds.length === 1) {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionsIds[0]}/channel-contents/texts`, texts);
        } else {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/channel-contents/texts`, {
                ids: sessionsIds, values: texts
            });
        }
    }

    getTicketTexts(format: SessionTicketContentFormat, eventId: number, sessionId: number): Observable<SessionTicketText[]> {
        return this._http.get<SessionTicketText[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/ticket-contents/${format}/texts`);
    }

    postTicketTexts(
        format: SessionTicketContentFormat, eventId: number, sessionIds: number[], texts: SessionTicketText[]
    ): Observable<void> {
        if (sessionIds.length === 1) {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionIds[0]}/ticket-contents/${format}/texts`, texts);
        } else {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/ticket-contents/${format}/texts`, {
                ids: sessionIds, values: texts
            });
        }
    }

    getChannelImages(eventId: number, sessionId: number): Observable<SessionChannelImage[]> {
        return this._http.get<SessionChannelImage[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/channel-contents/images`);
    }

    postChannelImages(eventId: number, sessionIds: number[], image: SessionChannelImageRequest[]): Observable<void> {
        if (sessionIds.length === 1) {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionIds[0]}/channel-contents/images`, image);
        } else {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/channel-contents/images`, {
                ids: sessionIds, values: image
            });
        }
    }

    deleteChannelImage(eventId: number, sessionIds: number[], img: SessionChannelImageRequest): Observable<void> {
        if (sessionIds.length === 1) {
            const id = sessionIds[0];
            const params = buildHttpParams({ position: img.position });
            return this._http.delete<void>(
                `${this.EVENTS_API}/${eventId}/sessions/${id}/channel-contents/images/languages/${img.language}/types/${img.type}`,
                { params }
            );
        } else {
            const params = buildHttpParams({
                position: img.position,
                session_id: sessionIds
            });
            return this._http.delete<void>(
                `${this.EVENTS_API}/${eventId}/sessions/channel-contents/images/languages/${img.language}/types/${img.type}`,
                { params }
            );
        }
    }

    deleteAllChannelImage(eventId: number, sessionIds: number[], language: string): Observable<void> {
        const params = buildHttpParams({ session_id: sessionIds });
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/channel-contents/images/languages/${language}`, { params });
    }

    getTicketImages(format: SessionTicketContentFormat, eventId: number, sessionId: number): Observable<SessionTicketImage[]> {
        return this._http.get<SessionTicketImage[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/ticket-contents/${format}/images`);
    }

    postTicketImages(
        format: SessionTicketContentFormat, eventId: number, sessionIds: number[], image: SessionTicketImageRequest[]
    ): Observable<void> {
        if (sessionIds.length === 1) {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionIds[0]}/ticket-contents/${format}/images`, image);
        } else {
            return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/ticket-contents/${format}/images`, {
                ids: sessionIds, values: image
            });
        }
    }

    deleteTicketImage(
        format: SessionTicketContentFormat, eventId: number, sessionIds: number[], image: SessionTicketImageRequest
    ): Observable<void> {
        if (sessionIds.length === 1) {
            return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionIds[0]}` +
                `/ticket-contents/${format}/images/languages/${image.language}/types/${image.type}`);
        } else {
            const params = buildHttpParams({ session_id: sessionIds });
            return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions` +
                `/ticket-contents/${format}/images/languages/${image.language}/types/${image.type}`, { params });
        }
    }

    deleteAllTicketImage(format: SessionTicketContentFormat, eventId: number, sessionIds: number[], language: string): Observable<void> {
        const params = buildHttpParams({ session_id: sessionIds });
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/ticket-contents/${format}/images/languages/${language}`, {
            params
        });
    }

    getDownloadUrlPassbookPreview$(eventId: number, sessionId: number): Observable<{ download_url: string }> {
        return this._http.get<{ download_url: string }>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/ticket-templates/passbook/preview`
        );
    }
}

import { buildHttpParams } from '@OneboxTM/utils-http';
import { TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Code, Id } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SeasonTicketChannelContentImageType } from '../models/season-ticket-channel-content-image-type.enum';
import { SeasonTicketChannelContentImage } from '../models/season-ticket-channel-content-image.model';
import { SeasonTicketChannelContentImageRequest } from '../models/season-ticket-channel-content-image.request.model';
import { SeasonTicketChannelContentText } from '../models/season-ticket-channel-content-text.model';
import { SeasonTicketTemplateType } from '../models/season-ticket-template-type.enum';
import { SeasonTicketTicketContentFormat } from '../models/season-ticket-ticket-content-format.enum';
import { SeasonTicketTicketContentImageType } from '../models/season-ticket-ticket-content-image-type.enum';
import {
    SeasonTicketTicketContentImage
} from '../models/season-ticket-ticket-content-image.model';
import { SeasonTicketTicketContentImageRequest } from '../models/season-ticket-ticket-content-image.request.model';
import { SeasonTicketTicketContentText } from '../models/season-ticket-ticket-content-text.model';
import { SeasonTicketTicketTemplate } from '../models/season-ticket-ticket-template.model';

@Injectable()
export class SeasonTicketCommunicationApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SEASON_TICKET_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;

    private readonly _http = inject(HttpClient);

    getSeasonTicketChannelContentTexts(seasonTicketId: number): Observable<SeasonTicketChannelContentText[]> {
        return this._http.get<SeasonTicketChannelContentText[]>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/channel-contents/texts`);
    }

    postSeasonTicketChannelContentTexts(seasonTicketId: number, contents: SeasonTicketChannelContentText[]): Observable<void> {
        return this._http.post<void>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/channel-contents/texts`,
            contents
        );
    }

    getSeasonTicketTicketContentTexts(ticketType: TicketType, eventId: number,
        format: SeasonTicketTicketContentFormat): Observable<SeasonTicketTicketContentText[]> {
        return this._http.get<SeasonTicketTicketContentText[]>(
            `${this.getSeasonTicketTicketContentUrl(ticketType, eventId, format)}/texts`
        );
    }

    postSeasonTicketTicketContentTexts(ticketType: TicketType, eventId: number, format: SeasonTicketTicketContentFormat,
        contents: SeasonTicketTicketContentText[]): Observable<void> {
        return this._http.post<void>(`${this.getSeasonTicketTicketContentUrl(ticketType, eventId, format)}/texts`, contents);
    }

    getSeasonTicketChannelContentImages$(
        seasonTicketId: number,
        language: string,
        type: SeasonTicketChannelContentImageType
    ): Observable<SeasonTicketChannelContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<SeasonTicketChannelContentImage[]>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/channel-contents/images`,
            { params }
        );

    }

    postSeasonTicketChannelContentImages(
        seasonTicketId: number,
        contents: SeasonTicketChannelContentImageRequest[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/channel-contents/images`,
            contents
        );
    }

    deleteSeasonTicketChannelContentImage$(
        seasonTicketId: number,
        language: string,
        type: SeasonTicketChannelContentImageType,
        position: number): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/channel-contents/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    getSeasonTicketTicketTemplates(seasonTicketId: number): Observable<SeasonTicketTicketTemplate[]> {
        return this._http.get<SeasonTicketTicketTemplate[]>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/ticket-templates`);
    }

    postSeasonTicketTicketTemplate$(
        seasonTicketId: number,
        templateId: Id | Code,
        format: TicketTemplateFormat,
        type: SeasonTicketTemplateType
    ): Observable<void> {
        return this._http.put<void>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/ticket-templates/${type}/${format}`,
            templateId
        );
    }

    getSeasonTicketTicketContentTexts$(
        ticketType: TicketType,
        seasonTicketId: number,
        format: SeasonTicketTicketContentFormat
    ): Observable<SeasonTicketTicketContentText[]> {
        return this._http.get<SeasonTicketTicketContentText[]>(
            `${this.getSeasonTicketTicketContentUrl(ticketType, seasonTicketId, format)}/texts`);
    }

    postSeasonTicketTicketContentTexts$(
        ticketType: TicketType,
        eventId: number,
        format: SeasonTicketTicketContentFormat,
        contents: SeasonTicketTicketContentText[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.getSeasonTicketTicketContentUrl(ticketType, eventId, format)}/texts`,
            contents
        );
    }

    getSeasonTicketTicketContentImages$(
        ticketType: TicketType,
        seasonTicketId: number,
        format: SeasonTicketTicketContentFormat,
        language: string,
        type: SeasonTicketTicketContentImageType
    ): Observable<SeasonTicketTicketContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<SeasonTicketTicketContentImage[]>(
            `${this.getSeasonTicketTicketContentUrl(ticketType, seasonTicketId, format)}/images`,
            { params }
        );
    }

    postSeasonTicketTicketContentImages$(
        ticketType: TicketType,
        seasonTicketId: number,
        format: SeasonTicketTicketContentFormat,
        contents: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.getSeasonTicketTicketContentUrl(ticketType, seasonTicketId, format)}/images`,
            contents
        );
    }

    deleteSeasonTicketTicketContentImage$(
        ticketType: TicketType,
        seasonTicketId: number,
        format: SeasonTicketTicketContentFormat,
        language: string,
        type: SeasonTicketTicketContentImageType
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.getSeasonTicketTicketContentUrl(
                ticketType,
                seasonTicketId,
                format
            )}/images/languages/${language}/types/${type}`);
    }

    downloadTicketPdfPreview$(
        seasonTicketId: number,
        type: SeasonTicketTemplateType,
        language: string
    ): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this._http.get<{ url: string }>(
            `${this.SEASON_TICKET_API}/${seasonTicketId}/ticket-templates/${type}/preview`, { params }
        );
    }

    getDownloadUrlPassbookPreview$(seasonTicketId: number): Observable<{ download_url: string }> {
        return this._http.get<{ download_url: string }>(`${this.SEASON_TICKET_API}/${seasonTicketId}/ticket-templates/passbook/preview`);
    }

    private getSeasonTicketTicketContentUrl(
        ticketType: TicketType,
        seasonTicketId: number,
        format: SeasonTicketTicketContentFormat
    ): string {
        return `${this.SEASON_TICKET_API}/${seasonTicketId}/ticket${ticketType === TicketType.invitation ?
            '-invitation' :
            ''}-contents/${format}`;
    }
}

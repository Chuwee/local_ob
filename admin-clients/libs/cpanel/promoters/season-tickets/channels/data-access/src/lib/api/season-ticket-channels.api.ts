import { buildHttpParams } from '@OneboxTM/utils-http';
import { ChannelCommission, ChannelPriceSimulation, ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { ChannelB2bAssignations } from '@admin-clients/cpanel/promoters/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Id, PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSeasonTicketChannelsResponse } from '../models/get-season-ticket-channels-response.model';
import { SeasonTicketChannelAttendantsLink } from '../models/season-ticket-channel-attendants-link.model';
import { SeasonTicketChannelLink } from '../models/season-ticket-channel-content.model';
import { SeasonTicketChannel } from '../models/season-ticket-channel.model';
import { UpdateSeasonTicketChannelsRequest } from '../models/update-season-ticket-channels-request.model';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketChannelsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SEASON_TICKETS_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;
    private readonly CHANNELS_SEGMENT = '/channels';

    private readonly _http = inject(HttpClient);

    getSeasonTicketChannelsList(seasonTicketId: number, request: PageableFilter): Observable<GetSeasonTicketChannelsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetSeasonTicketChannelsResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}`, { params }
        );
    }

    getSeasonTicketChannel(seasonTicketChannelId: number, channelId: number): Observable<SeasonTicketChannel> {
        return this._http.get<SeasonTicketChannel>(
            `${this.SEASON_TICKETS_API}/${seasonTicketChannelId}${this.CHANNELS_SEGMENT}/${channelId}`
        );
    }

    getSeasonTicketChannelCommissions(seasonTicketId: number, channelId: number): Observable<ChannelCommission[]> {
        return this._http.get<ChannelCommission[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/channel-commissions`
        );
    }

    postSeasonTicketChannel(seasonTicketId: number, channelId: number): Observable<void> {
        return this._http.post<void>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}`, { channel_id: channelId }
        );
    }

    postRequestSeasonTicketChannel(seasonTicketId: number, channelId: number): Observable<void> {
        return this._http.post<void>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/request-approval`, {}
        );
    }

    deleteSeasonTicketChannel(seasonTicketId: number, channelId: number): Observable<void> {
        return this._http.delete<void>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}`
        );
    }

    updateSeasonTicketChannel(seasonTicketId: number, channelId: number, request: UpdateSeasonTicketChannelsRequest): Observable<void> {
        return this._http.put<void>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}`, request
        );
    }

    getSeasonTicketChannelPriceSimulation(seasonTicketId: number, channelId: number): Observable<ChannelPriceSimulation[]> {
        return this._http.get<ChannelPriceSimulation[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/price-simulation`
        );
    }

    getSeasonTicketChannelChannelSurcharges(seasonTicketId: number, channelId: number): Observable<ChannelSurcharge[]> {
        return this._http.get<ChannelSurcharge[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/channel-surcharges`
        );
    }

    getSeasonTicketChannelSurcharges(seasonTicketId: number, channelId: number): Observable<ChannelSurcharge[]> {
        return this._http.get<ChannelSurcharge[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/surcharges`
        );
    }

    postSeasonTicketChannelSurcharges(seasonTicketId: number, channelId: number, surcharges: ChannelSurcharge[]): Observable<any> {
        return this._http.post<any>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/surcharges`, surcharges
        );
    }

    getSeasonTicketChannelLink(seasonTicketId: number, channelId: number): Observable<SeasonTicketChannelLink> {
        return this._http.get<SeasonTicketChannelLink>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/funnel-urls`
        );
    }

    downloadTicketPdfPreview(seasonTicketId: number, channelId: number, language: string): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this._http.get<{ url: string }>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/ticket-template/preview`, { params }
        );
    }

    getSeasonTicketChannelAttendantsLinks(seasonTicketId: number, channelId: number): Observable<SeasonTicketChannelAttendantsLink[]> {
        return this._http.get<SeasonTicketChannelAttendantsLink[]>(`
            ${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/edit-attendants-urls`);
    }

    getB2bAssignations(seasonTicketId: number, channelId: number): Observable<ChannelB2bAssignations> {
        return this._http.get<ChannelB2bAssignations>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/assignations`
        );
    }

    putB2bAssignations(seasonTicketId: number, channelId: number, request: ChannelB2bAssignations<Id>): Observable<void> {
        return this._http.put<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/assignations`, request);
    }
}

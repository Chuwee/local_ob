import { buildHttpParams } from '@OneboxTM/utils-http';
import { ChannelCommission, ChannelPriceSimulation, ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import {
    EventChannelContentImageRequest, EventChannelContentImageRequestConfig, EventChannelContentImageType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ContentLinkRequest, ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { B2BPublishConfigurationRequest, B2BPublishConfigurationResponse } from '../models/b2b-publish-configuration.model';
import { EventChannelAttendantsLink } from '../models/event-channel-attendants-link.model';
import { EventChannelB2bAssignations, PutEventChannelB2bAssignations } from '../models/event-channel-b2b-assignations.model';
import { EventChannelTicketContentFormat } from '../models/event-channel-ticket-content-format.enum';
import { EventChannelTicketTemplateType } from '../models/event-channel-ticket-template-type.enum';
import { EventChannelTicketTemplate } from '../models/event-channel-ticket-templates.model';
import { EventChannelToFavoriteRequest } from '../models/event-channel-to-favorite-request.model';
import { EventChannel } from '../models/event-channel.model';
import { GetEventChannelsCandidatesRequest, GetEventChannelsCandidatesResponse } from '../models/event-channels-candidates.model';
import { EventChannelsRequest } from '../models/event-channels-request.model';
import { GetEventChannelSessionLinksRequest } from '../models/get-event-channel-session-links-request';
import { GetEventChannelsResponse } from '../models/get-event-channels-response.model';
import { UpdateEventChannelsRequest } from '../models/update-event-channels-request.model';

@Injectable()
export class EventChannelsApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;
    private readonly CHANNELS_SEGMENT = '/channels';
    private readonly _http = inject(HttpClient);

    getEventChannelsList(eventId: number, request: EventChannelsRequest): Observable<GetEventChannelsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetEventChannelsResponse>(`${this.EVENTS_API}/${eventId}/channels`, { params });
    }

    getEventChannel(eventId: number, channelId: number): Observable<EventChannel> {
        return this._http.get<EventChannel>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}`);
    }

    postEventChannel(eventId: number, channelId: number): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}`, { channel_id: channelId });
    }

    postRequestEventChannel(eventId: number, channelId: number): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/request-approval`, {});
    }

    deleteEventChannel(eventId: number, channelId: number): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}`);
    }

    putEventChannel(eventId: number, channelId: number, request: UpdateEventChannelsRequest): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}`, request);
    }

    getEventChannelSurcharges(eventId: number, channelId: number): Observable<ChannelSurcharge[]> {
        return this._http.get<ChannelSurcharge[]>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/surcharges`);
    }

    postEventChannelSurcharges(eventId: number, channelId: number, surcharges: ChannelSurcharge[]): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/surcharges`, surcharges);
    }

    getEventChannelChannelSurcharges(eventId: number, channelId: number): Observable<ChannelSurcharge[]> {
        return this._http.get<ChannelSurcharge[]>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-surcharges`);
    }

    getEventChannelPriceSimulation(eventId: number, channelId: number): Observable<ChannelPriceSimulation[]> {
        return this._http.get<ChannelPriceSimulation[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/price-simulation`
        );
    }

    getEventChannelCommissions(eventId: number, channelId: number): Observable<ChannelCommission[]> {
        return this._http.get<ChannelCommission[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-commissions`
        );
    }

    getContentLinkRequest(eventId: number, channelId: number): Observable<ContentLinkRequest[]> {
        return this._http.get<ContentLinkRequest[]>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/funnel-urls`);
    }

    getEventChannelSessionLinks(request: GetEventChannelSessionLinksRequest): Observable<ContentLinkResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            q: request.q,
            offset: request.offset,
            sort: request.sort,
            session_status: request.session_status
        });
        return this._http.get<ContentLinkResponse>(
            `${this.EVENTS_API}/${request.eventId}${this.CHANNELS_SEGMENT}/${request.channelId}/language/${request.language}/session-links`,
            { params }
        );
    }

    getEventChannelAttendantsLinks(eventId: number, channelId: number): Observable<EventChannelAttendantsLink[]> {
        return this._http.get<EventChannelAttendantsLink[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/edit-attendants-urls`
        );
    }

    downloadTicketPdfPreview(eventId: number, channelId: number, language: string): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this._http.get<{ url: string }>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/ticket-template/preview`, { params }
        );
    }

    putFavoriteChannel(eventId: number, channelId: number, request: EventChannelToFavoriteRequest): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/favorite`, request);
    }

    getB2bAssignations(eventId: number, channelId: number): Observable<EventChannelB2bAssignations> {
        return this._http.get<EventChannelB2bAssignations>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/assignations`
        );
    }

    putB2bAssignations(eventId: number, channelId: number, request: PutEventChannelB2bAssignations): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/assignations`, request);
    }

    getB2bPublishConfiguration(
        eventId: number, channelId: number, venueTemplateId: number
    ): Observable<B2BPublishConfigurationResponse> {
        return this._http.get<B2BPublishConfigurationResponse>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/venue-templates/${venueTemplateId}/publishing-config`
        );
    }

    putB2bPublishConfiguration(
        eventId: number, channelId: number, venueTemplateId: number, request: B2BPublishConfigurationRequest
    ): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/b2b/venue-templates/${venueTemplateId}/publishing-config`,
            request
        );
    }

    // TICKET TEMPLATES
    getEventChannelTicketTemplates(eventId: number, channelId: number): Observable<EventChannelTicketTemplate[]> {
        return this._http.get<EventChannelTicketTemplate[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/ticket-templates`);
    }

    postEventChannelTicketTemplates(
        eventId: number, channelId: number, templateId: number, type: EventChannelTicketTemplateType, format: EventChannelTicketContentFormat
    ): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/ticket-templates/${type}/${format}`, { id: templateId });
    }

    // Get candidates channels for sales requests
    getEventChannelsCandidatesList(eventId: number, request: GetEventChannelsCandidatesRequest):
        Observable<GetEventChannelsCandidatesResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            name: request.name,
            status: request.status,
            type: request.type,
            include_third_party_channels: request.includeThirdPartyChannels
        });
        return this._http.get<GetEventChannelsCandidatesResponse>(
            `${this.EVENTS_API}/${eventId}/sale-requests/channels-candidates`, { params }
        );
    }

    getEventChannelContentSquareImages(eventId: number, channelId: number): Observable<EventChannelContentImageRequest[]> {
        return this._http.get<EventChannelContentImageRequest[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images`
        );
    }

    postEventChannelContentSquareImages(eventId: number, channelId: number, images: EventChannelContentImageRequest[]): Observable<void> {
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images`, images
        );
    }

    deleteEventChannelContentSquareImage(
        eventId: number, channelId: number, language: string, type: EventChannelContentImageType, position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    getEventSessionContentSquareImages(
        eventId: number, channelId: number, sessionId: number
    ): Observable<EventChannelContentImageRequest[]> {
        return this._http.get<EventChannelContentImageRequest[]>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images`
        );
    }

    deleteEventSessionContentSquareImage(
        eventId: number, channelId: number, sessionId: number, language: string, type: EventChannelContentImageType, position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    deleteAllEventSessionContentSquareImages(eventId: number, channelId: number, sessionId: number): Observable<void> {
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images`
        );
    }

    postEventSessionContentSquareImages(
        eventId: number, channelId: number, sessionId: number, images: EventChannelContentImageRequest[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images`, images
        );
    }

    getEventSessionContentSquareImagesConfig(eventId: number, channelId: number): Observable<EventChannelContentImageRequestConfig[]> {
        return this._http.get<EventChannelContentImageRequestConfig[]>(
            `${this.EVENTS_API}/${eventId}${this.CHANNELS_SEGMENT}/${channelId}/channel-contents/images-config`
        );
    }
}
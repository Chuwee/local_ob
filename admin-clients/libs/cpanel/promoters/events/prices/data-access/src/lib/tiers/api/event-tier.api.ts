import { buildHttpParams } from '@OneboxTM/utils-http';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { EventTiersChannelContent } from '../models/event-tiers-channel-content.model';
import { EventTiers } from '../models/event-tiers.model';
import { GetEventTierRequest } from '../models/get-event-tiers-request.model';
import { GetEventTiersResponse } from '../models/get-event-tiers-response.model';
import { PostEventTierQuota } from '../models/post-event-tier-quota.model';
import { PostEventTier } from '../models/post-event-tier.model';
import { PutEventTierQuota } from '../models/put-event-tier-quota.model';
import { PutEventTier } from '../models/put-event-tier.model';

@Injectable({
    providedIn: 'root'
})
export class EventTierApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;

    constructor(private _http: HttpClient) { }

    getEventTiers(eventId: string, request: GetEventTierRequest): Observable<GetEventTiersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetEventTiersResponse>(`${this.EVENTS_API}/${eventId}/tiers`, { params });
    }

    putEventTier(eventId: string, tierId: string, tiers: PutEventTier): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}`, tiers);
    }

    postEventTier(eventId: string, eventTier: PostEventTier): Observable<number> {
        return this._http.post<number>(`${this.EVENTS_API}/${eventId}/tiers`, eventTier);
    }

    deleteEventTier(eventId: string, tierId: string): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}`);
    }

    getEventTier(eventId: string, tierId: string): Observable<EventTiers> {
        return this._http.get<EventTiers>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}`);
    }

    postEventTierQuota(eventId: string, tierId: string, quota: PostEventTierQuota): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/quotas`, quota);
    }

    putEventTierQuota(eventId: string, tierId: string, quotaId: string, quota: PutEventTierQuota): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/quotas/${quotaId}`, quota);
    }

    deleteEventTierQuota(eventId: string, tierId: string, quotaId: string): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/quotas/${quotaId}`);
    }

    deleteTierLimit(eventId: string, tierId: string): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/limit`);
    }

    getTiersChannelContents(eventId: string, tierId: string): Observable<EventTiersChannelContent[]> {
        return this._http.get<EventTiersChannelContent[]>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/channel-contents`);
    }

    postTiersTypeChannelContent(eventId: string, tierId: string, content: EventTiersChannelContent[]): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/tiers/${tierId}/channel-contents`, content);
    }

}

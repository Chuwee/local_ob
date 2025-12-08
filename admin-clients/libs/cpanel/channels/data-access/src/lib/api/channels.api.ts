import { buildHttpParams } from '@OneboxTM/utils-http';
import { LoginAuthConfig } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelCustomResources, CustomAssetElement, GetCustomAssetsResponse } from '../models/channel-custom-resources.model';
import { ChannelFaqs, FaqsListFilters } from '../models/channel-faqs.model';
import {
    GetReviewConfigElementResponse, PutReviewConfigElement, ReviewConfig, ReviewConfigElementFilter, ReviewCriteria, ReviewScope
} from '../models/channel-review-config.model';
import { ChannelWhitelabelSettings, PutChannelWhitelabelSettings } from '../models/channel-whitelabel-settings.model';
import { Channel } from '../models/channel.model';
import { GetChannelsRequest } from '../models/get-channels-request.model';
import { GetChannelsResponse } from '../models/get-channels-response.model';
import { PostChannelRequest } from '../models/post-channel-request.model';
import { PutChannel } from '../models/put-channel.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelsApi {

    private readonly BASE_API = inject(APP_BASE_API);

    protected readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    protected readonly WHITELABEL_SETTINGS = '/whitelabel-settings';
    protected readonly FAQS = '/faqs';
    protected readonly CUSTOM_RESOURCES = '/custom-resources';
    protected readonly CUSTOM_ASSETS = '/assets';

    protected readonly http = inject(HttpClient);

    getChannels(request: GetChannelsRequest): Observable<GetChannelsResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            entity_id: request.entityId,
            name: request.name,
            status: request.status,
            type: request.type,
            operator_id: request.operatorId,
            include_third_party_channels: request.includeThirdPartyChannels
        });
        return this.http.get<GetChannelsResponse>(this.CHANNELS_API, { params });
    }

    getChannel(channelId: string): Observable<Channel> {
        return this.http.get<Channel>(`${this.CHANNELS_API}/${channelId}`);
    }

    postChannel(channel: PostChannelRequest): Observable<{ id: number }> {
        return this.http.post<{ id: number }>(this.CHANNELS_API, {
            entity_id: channel.entity.id,
            name: channel.name,
            type: channel.type,
            url: channel.url,
            collective_id: channel.collective?.id
        });
    }

    putChannel(id: number, channel: PutChannel): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${id}`, channel);
    }

    deleteChannel(channelId: string): Observable<void> {
        return this.http.delete<void>(`${this.CHANNELS_API}/${channelId}`);
    }

    getChannelWhitelabelSettings(channelId: number): Observable<ChannelWhitelabelSettings> {
        return this.http.get<ChannelWhitelabelSettings>(`${this.CHANNELS_API}/${channelId}${this.WHITELABEL_SETTINGS}`);
    }

    putChannelWhitelabelSettings(channelId: number, settings: PutChannelWhitelabelSettings): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.WHITELABEL_SETTINGS}`, settings);
    }

    //FAQs
    getFaqs(channelId: number, filter?: FaqsListFilters): Observable<ChannelFaqs[]> {
        const params = filter ? buildHttpParams(filter) : {};
        return this.http.get<ChannelFaqs[]>(`${this.CHANNELS_API}/${channelId}${this.FAQS}`, { params });
    }

    putFaqs(channelId: number, faqs: ChannelFaqs[]): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.FAQS}`, faqs);
    }

    //FAQ
    getFaq(channelId: number, key: number): Observable<ChannelFaqs> {
        return this.http.get<ChannelFaqs>(`${this.CHANNELS_API}/${channelId}${this.FAQS}/${key}`);
    }

    postFaq(channelId: number, faq: ChannelFaqs): Observable<void> {
        return this.http.post<void>(`${this.CHANNELS_API}/${channelId}${this.FAQS}`, faq);
    }

    putFaq(channelId: number, key: number, faq: ChannelFaqs): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.FAQS}/${key}`, faq);
    }

    deleteFaq(channelId: number, key: number): Observable<void> {
        return this.http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.FAQS}/${key}`);
    }

    //Custom resources
    getCustomResources(channelId: number): Observable<ChannelCustomResources> {
        return this.http.get<ChannelCustomResources>(`${this.CHANNELS_API}/${channelId}${this.CUSTOM_RESOURCES}`);
    }

    putCustomResources(channelId: number, resources: ChannelCustomResources): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.CUSTOM_RESOURCES}`, resources);
    }

    getCustomAssets(channelId: number, filter?: PageableFilter): Observable<GetCustomAssetsResponse> {
        const params = filter ? buildHttpParams(filter) : {};
        return this.http.get<GetCustomAssetsResponse>(
            `${this.CHANNELS_API}/${channelId}${this.CUSTOM_RESOURCES}${this.CUSTOM_ASSETS}`, { params }
        );
    }

    postCustomAsset(channelId: number, asset: CustomAssetElement[]): Observable<void> {
        return this.http.post<void>(`${this.CHANNELS_API}/${channelId}${this.CUSTOM_RESOURCES}${this.CUSTOM_ASSETS}`, asset);
    }

    deleteCustomAsset(channelId: number, fileName: string): Observable<void> {
        return this.http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.CUSTOM_RESOURCES}${this.CUSTOM_ASSETS}/${fileName}`);
    }

    getChannelAuthConfig(channelId: number): Observable<LoginAuthConfig> {
        return this.http.get<LoginAuthConfig>(`${this.CHANNELS_API}/${channelId}/auth-config`);
    }

    putChannelAuthConfig(channelId: number, config: Partial<LoginAuthConfig>): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}/auth-config`, config);
    }

    getChannelReviewConfig(channelId: number): Observable<ReviewConfig> {
        return this.http.get<ReviewConfig>(`${this.CHANNELS_API}/${channelId}/reviews`);
    }

    putChannelReviewConfig(channelId: number, config: ReviewConfig): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}/reviews`, config);
    }

    getChannelReviewConfigElements(channelId: number, filters?: ReviewConfigElementFilter): Observable<GetReviewConfigElementResponse> {
        const params = filters ? buildHttpParams(filters) : {};
        return this.http.get<GetReviewConfigElementResponse>(`${this.CHANNELS_API}/${channelId}/reviews/config`, { params });
    }

    putChannelReviewConfigElement(channelId: number, scope: ReviewScope, config: PutReviewConfigElement): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}/reviews/config/${scope}`, config);
    }

    updateChannelReviewConfigElement(channelId: number, scope: ReviewScope, scopeId: number, criteria: ReviewCriteria): Observable<void> {
        const params = { send_criteria: criteria };
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}/reviews/config/${scope}/${scopeId}`, params);
    }

    deletetChannelReviewConfigElement(channelId: number, scope: ReviewScope, scopeId: number): Observable<void> {
        return this.http.delete<void>(`${this.CHANNELS_API}/${channelId}/reviews/config/${scope}/${scopeId}`);
    }
}

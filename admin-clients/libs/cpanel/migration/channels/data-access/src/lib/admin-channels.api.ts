import { buildHttpParams } from '@OneboxTM/utils-http';
import { GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import { DomainConfiguration, DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import type {
    CorsSettings, GetAdminChannelsResponse, PostChannelMigrateRequest, PutChannelWhitelabelType, PutReceiptMigrateRequest
} from './models/admin-channels.model';

export class AdminChannelsApi {
    readonly #http = inject(HttpClient);
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #PLATFORM_CHANNELS_API = `${this.#BASE_API}/mgmt-api/v1/platform-admin-channels`;
    readonly #CHANNELS_API = `${this.#BASE_API}/mgmt-api/v1/channels`;
    readonly #DOMAINS_CONFIG_API = `${this.#BASE_API}/mgmt-api/v1/domains-config`;

    getAdminChannels(request: GetChannelsRequest): Observable<GetAdminChannelsResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            entity_id: request.entityId,
            entity_admin_id: request.entityAdminId,
            name: request.name,
            status: request.status,
            type: request.type,
            operator_id: request.operatorId,
            include_third_party_channels: request.includeThirdPartyChannels
        });
        return this.#http.get<GetAdminChannelsResponse>(`${this.#PLATFORM_CHANNELS_API}/`, { params });
    }

    postMigrateChannel(channelId: number, request: PostChannelMigrateRequest): Observable<void> {
        const params = buildHttpParams({
            migrate_to_channels: request.migrate_to_channels,
            stripe_hook_checked: request.stripe_hook_checked
        });
        return this.#http.post<void>(`${this.#PLATFORM_CHANNELS_API}/${channelId}/migration`, params);
    }

    putMigrateReceipt(channelId: number, request: PutReceiptMigrateRequest): Observable<void> {
        const params = buildHttpParams({
            migrate_receipt_template: request.migrate_receipt_template
        });
        return this.#http.put<void>(`${this.#PLATFORM_CHANNELS_API}/${channelId}/migrate-receipt`, null, { params });
    }

    putChannelWhitelabelType(channelId: number, request: PutChannelWhitelabelType): Observable<void> {
        const params = buildHttpParams({
            whitelabel_type: request.whitelabel_type
        });
        return this.#http.put<void>(`${this.#PLATFORM_CHANNELS_API}/${channelId}/whitelabel-type`, null, { params });
    }

    getChannelCorsSettings(channelId: number): Observable<CorsSettings> {
        return this.#http.get<CorsSettings>(`${this.#CHANNELS_API}/${channelId}/cors-settings`);
    }

    postChannelCorsSettings(channelId: number, request: CorsSettings): Observable<void> {
        return this.#http.post<void>(`${this.#CHANNELS_API}/${channelId}/cors-settings`, request);
    }

    getChannelSubdomainSettings(channelId: number): Observable<DomainSettings> {
        return this.#http.get<DomainSettings>(`${this.#CHANNELS_API}/${channelId}/domain-settings`);
    }

    postChannelSubdomainSettings(channelId: number, request: Partial<DomainSettings>): Observable<void> {
        return this.#http.post<void>(`${this.#CHANNELS_API}/${channelId}/domain-settings`, request);
    }

    getDomainConfiguration(domain: string): Observable<DomainConfiguration> {
        return this.#http.get<DomainConfiguration>(`${this.#DOMAINS_CONFIG_API}/${domain}`);
    }

    putDomainConfiguration(domain: string, request: Partial<DomainConfiguration>): Observable<void> {
        return this.#http.put<void>(`${this.#DOMAINS_CONFIG_API}/${domain}`, request);
    }
}

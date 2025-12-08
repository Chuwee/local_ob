import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ChannelsApi, GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import type { DomainConfiguration, DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { catchError, of } from 'rxjs';
import { AdminChannelsApi } from './admin-channels.api';
import { AdminChannelsState } from './admin-channels.state';
import type {
    CorsSettings, PostChannelMigrateRequest, PutChannelWhitelabelType, PutReceiptMigrateRequest
} from './models/admin-channels.model';

const DEFAULT_CORS_SETTINGS: CorsSettings = {
    enabled: false,
    allowed_origins: []
};

@Injectable()
export class AdminChannelsService {
    readonly #api = new AdminChannelsApi();
    readonly #state = new AdminChannelsState();
    readonly #channelsApi = new ChannelsApi();

    readonly channelsList = Object.freeze({
        load: (request: GetChannelsRequest) => StateManager.load(
            this.#state.adminChannelsList, this.#api.getAdminChannels(request).pipe(mapMetadata())
        ),
        loadMore: (request: GetChannelsRequest) => StateManager.loadMore(
            request, this.#state.adminChannelsList, r => this.#api.getAdminChannels(r)
        ),
        getList$: () => this.#state.adminChannelsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.adminChannelsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.adminChannelsList.isInProgress$(),
        clear: () => this.#state.adminChannelsList.setValue(null)
    });

    readonly channel = Object.freeze({
        load: (channelId: string) => StateManager.load(
            this.#state.channel, this.#channelsApi.getChannel(channelId)
        ),
        get$: () => this.#state.channel.getValue$(),
        loading$: () => this.#state.channel.isInProgress$(),
        error$: () => this.#state.channel.getError$(),
        clear: () => this.#state.channel.setValue(null),
        updateWhitelabelType: (channelId: number, request: PutChannelWhitelabelType) => StateManager.inProgress(
            this.#state.channel,
            this.#api.putChannelWhitelabelType(channelId, request)
        ),
        migrate: (channelId: number, request: PostChannelMigrateRequest) => StateManager.inProgress(
            this.#state.channel,
            this.#api.postMigrateChannel(channelId, request)
        ),
        migrateReceipt: (channelId: number, request: PutReceiptMigrateRequest) => StateManager.inProgress(
            this.#state.channel,
            this.#api.putMigrateReceipt(channelId, request)
        )
    });

    readonly channelCorsSettings = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this.#state.channelCorsSettings,
            this.#api.getChannelCorsSettings(channelId).pipe(catchError(() => of(DEFAULT_CORS_SETTINGS)))
        ),
        get$: () => this.#state.channelCorsSettings.getValue$(),
        upsert$: (channelId: number, request: CorsSettings) => StateManager.inProgress(
            this.#state.channelCorsSettings,
            this.#api.postChannelCorsSettings(channelId, request)
        ),
        loading$: () => this.#state.channelCorsSettings.isInProgress$(),
        error$: () => this.#state.channelCorsSettings.getError$(),
        clear: () => this.#state.channelCorsSettings.setValue(null)
    });

    readonly channelSubdomainSettings = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this.#state.channelSubdomainSettings,
            this.#api.getChannelSubdomainSettings(channelId)
        ),
        get$: () => this.#state.channelSubdomainSettings.getValue$(),
        upsert$: (channelId: number, request: Partial<DomainSettings>) => StateManager.inProgress(
            this.#state.channelSubdomainSettings,
            this.#api.postChannelSubdomainSettings(channelId, request)
        ),
        loading$: () => this.#state.channelSubdomainSettings.isInProgress$(),
        error$: () => this.#state.channelSubdomainSettings.getError$(),
        clear: () => this.#state.channelSubdomainSettings.setValue(null)
    });

    readonly channelDomainConfiguration = Object.freeze({
        load: (domain: string) => StateManager.load(
            this.#state.domainConfiguration,
            this.#api.getDomainConfiguration(domain)
        ),
        get$: () => this.#state.domainConfiguration.getValue$(),
        update$: (domain: string, request: DomainConfiguration) => StateManager.inProgress(
            this.#state.domainConfiguration,
            this.#api.putDomainConfiguration(domain, request)
        ),
        loading$: () => this.#state.domainConfiguration.isInProgress$(),
        error$: () => this.#state.domainConfiguration.getError$(),
        clear: () => this.#state.domainConfiguration.setValue(null)
    });
}

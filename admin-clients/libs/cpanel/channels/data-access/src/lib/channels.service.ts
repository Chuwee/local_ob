import { StateManager, getListData, getMetadata, mapMetadata } from '@OneboxTM/utils-state';
import { LoginAuthConfig } from '@admin-clients/shared/common/data-access';
import { IdName, PageableFilter } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, finalize, map, of, tap } from 'rxjs';
import { ChannelsApi } from './api/channels.api';
import { ChannelCustomResources, CustomAssetElement } from './models/channel-custom-resources.model';
import { ChannelFaqs, FaqsListFilters } from './models/channel-faqs.model';
import type {
    ReviewConfigElementFilter, PutReviewConfigElement, ReviewConfig, ReviewConfigElement, ReviewScope
} from './models/channel-review-config.model';
import { PutChannelWhitelabelSettings } from './models/channel-whitelabel-settings.model';
import { Channel } from './models/channel.model';
import { GetChannelsRequest } from './models/get-channels-request.model';
import { PostChannelRequest } from './models/post-channel-request.model';
import { PutChannel } from './models/put-channel.model';
import { ChannelsState } from './state/channels.state';

@Injectable({ providedIn: 'root' })
export class ChannelsService {
    private readonly _channelsBaseApi = inject(ChannelsApi);
    private readonly _channelsBaseState = inject(ChannelsState);

    readonly channelsList = Object.freeze({
        load: (request: GetChannelsRequest) => StateManager.load(
            this._channelsBaseState.channelsList, this._channelsBaseApi.getChannels(request).pipe(mapMetadata())
        ),
        loadMore: (request: GetChannelsRequest) => StateManager.loadMore(
            request, this._channelsBaseState.channelsList, r => this._channelsBaseApi.getChannels(r)
        ),
        getList$: () => this._channelsBaseState.channelsList.getValue$().pipe(getListData()),
        getMetadata$: () => this._channelsBaseState.channelsList.getValue$().pipe(getMetadata()),
        loading$: () => this._channelsBaseState.channelsList.isInProgress$(),
        clear: () => this._channelsBaseState.channelsList.setValue(null)
    });

    readonly channelWhitelabelSettings = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._channelsBaseState.channelWhitelabelSettings, this._channelsBaseApi.getChannelWhitelabelSettings(channelId)
        ),
        update: (channelId: number, settings: PutChannelWhitelabelSettings) => StateManager.inProgress(
            this._channelsBaseState.channelWhitelabelSettings,
            this._channelsBaseApi.putChannelWhitelabelSettings(channelId, settings)
        ),
        get$: () => this._channelsBaseState.channelWhitelabelSettings.getValue$(),
        loading$: () => this._channelsBaseState.channelWhitelabelSettings.isInProgress$(),
        clear: () => this._channelsBaseState.channelWhitelabelSettings.setValue(null)
    });

    readonly faqs = Object.freeze({
        list: Object.freeze({
            load: (channelId: number, filters?: FaqsListFilters) => StateManager.load(
                this._channelsBaseState.faqsList,
                this._channelsBaseApi.getFaqs(channelId, filters).pipe(
                    tap(config => this._channelsBaseState.faqsList.setValue(config))
                )
            ),
            get$: () => this._channelsBaseState.faqsList.getValue$(),
            // TODO: Backend missing
            update: (channelId: number, faqs: ChannelFaqs[]) => StateManager.inProgress(
                this._channelsBaseState.faqsList,
                this._channelsBaseApi.putFaqs(channelId, faqs)
            ),
            loading$: () => this._channelsBaseState.faqsList.isInProgress$(),
            clear: () => this._channelsBaseState.faqsList.setValue(null)
        }),
        load: (channelId: number, key: number) => StateManager.load(
            this._channelsBaseState.faq,
            this._channelsBaseApi.getFaq(channelId, key)
        ),
        get$: () => this._channelsBaseState.faq.getValue$(),
        save: (channelId: number, faq: ChannelFaqs) => StateManager.inProgress(
            this._channelsBaseState.faq,
            this._channelsBaseApi.postFaq(channelId, faq)
        ),
        update: (channelId: number, key: number, faq: ChannelFaqs) => StateManager.inProgress(
            this._channelsBaseState.faq,
            this._channelsBaseApi.putFaq(channelId, key, faq)
        ),
        delete: (channelId: number, key: number) => StateManager.inProgress(
            this._channelsBaseState.faq,
            this._channelsBaseApi.deleteFaq(channelId, key)
        ),
        loading$: () => this._channelsBaseState.faq.isInProgress$(),
        clear: () => this._channelsBaseState.faq.setValue(null)
    });

    readonly customResources = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._channelsBaseState.customResources,
            this._channelsBaseApi.getCustomResources(channelId)
        ),
        update: (channelId: number, resources: ChannelCustomResources) => StateManager.inProgress(
            this._channelsBaseState.customResources,
            this._channelsBaseApi.putCustomResources(channelId, resources)
        ),
        get$: () => this._channelsBaseState.customResources.getValue$(),
        loading$: () => this._channelsBaseState.customResources.isInProgress$(),
        clear: () => this._channelsBaseState.customResources.setValue(null)
    });

    readonly customAssets = Object.freeze({
        load: (channelId: number, filters?: PageableFilter) => StateManager.load(
            this._channelsBaseState.customAssets,
            this._channelsBaseApi.getCustomAssets(channelId, filters).pipe(mapMetadata())
        ),
        create: (channelId: number, assets: CustomAssetElement[]) => StateManager.inProgress(
            this._channelsBaseState.customAssets,
            this._channelsBaseApi.postCustomAsset(channelId, assets)
        ),
        getCustomAssetsData$: () => this._channelsBaseState.customAssets.getValue$().pipe(getListData()),
        getCustomAssetsMetadata$: () => this._channelsBaseState.customAssets.getValue$().pipe(getMetadata()),
        delete: (channelId: number, fileName: string) => StateManager.inProgress(
            this._channelsBaseState.customAssets,
            this._channelsBaseApi.deleteCustomAsset(channelId, fileName)
        ),
        loading$: () => this._channelsBaseState.customAssets.isInProgress$(),
        clear: () => this._channelsBaseState.customAssets.setValue(null)
    });

    // Authentication configuration
    readonly authConfig = Object.freeze({
        load: (channelId: number): void => StateManager.load(
            this._channelsBaseState.authConfig,
            this._channelsBaseApi.getChannelAuthConfig(channelId)
        ),
        update: (channelId: number, config: Partial<LoginAuthConfig>): Observable<void> =>
            StateManager.inProgress(
                this._channelsBaseState.authConfig,
                this._channelsBaseApi.putChannelAuthConfig(channelId, config)
            ),
        get$: () => this._channelsBaseState.authConfig.getValue$(),
        inProgress$: () => this._channelsBaseState.authConfig.isInProgress$(),
        clear: () => this._channelsBaseState.authConfig.setValue(null)
    });

    readonly reviewConfig = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._channelsBaseState.reviewConfig,
            this._channelsBaseApi.getChannelReviewConfig(channelId)
        ),
        update: (channelId: number, config: ReviewConfig) => StateManager.inProgress(
            this._channelsBaseState.reviewConfig,
            this._channelsBaseApi.putChannelReviewConfig(channelId, config)
        ),
        get$: () => this._channelsBaseState.reviewConfig.getValue$(),
        loading$: () => this._channelsBaseState.reviewConfig.isInProgress$(),
        clear: () => this._channelsBaseState.reviewConfig.setValue(null)
    });

    readonly reviewConfigElements = Object.freeze({
        load: (channelId: number, filters?: ReviewConfigElementFilter) => StateManager.load(
            this._channelsBaseState.reviewConfigElements,
            this._channelsBaseApi.getChannelReviewConfigElements(channelId, filters)
        ),
        getData$: () => this._channelsBaseState.reviewConfigElements.getValue$().pipe(getListData()),
        getMetadata$: () => this._channelsBaseState.reviewConfigElements.getValue$().pipe(getMetadata()),
        add: (channelId: number, scope: ReviewScope, config: PutReviewConfigElement) => StateManager.inProgress(
            this._channelsBaseState.reviewConfigElements,
            this._channelsBaseApi.putChannelReviewConfigElement(channelId, scope, config)
        ),
        update: (channelId: number, updatedConfig: ReviewConfigElement) => StateManager.inProgress(
            this._channelsBaseState.reviewConfigElements,
            this._channelsBaseApi.updateChannelReviewConfigElement(
                channelId,
                updatedConfig.scope,
                updatedConfig.scope_id,
                updatedConfig.send_criteria
            )
        ),
        loading$: () => this._channelsBaseState.reviewConfigElements.isInProgress$(),
        clear: () => this._channelsBaseState.reviewConfigElements.setValue(null),
        delete: (channelId: number, scope: ReviewScope, scopeId: number) => StateManager.inProgress(
            this._channelsBaseState.reviewConfigElements,
            this._channelsBaseApi.deletetChannelReviewConfigElement(channelId, scope, scopeId)
        )
    });

    isChannelsListLoading$(): Observable<boolean> {
        return this._channelsBaseState.channelsList.isInProgress$();
    }

    isChannelLoading$(): Observable<boolean> {
        return this._channelsBaseState.isChannelLoading$();
    }

    loadChannel(id: string): void {
        this._channelsBaseState.setChannelError(null);
        this._channelsBaseState.setChannelLoading(true);
        this._channelsBaseApi.getChannel(id)
            .pipe(
                catchError(error => {
                    this._channelsBaseState.setChannelError(error);
                    return of(null);
                }),
                finalize(() => this._channelsBaseState.setChannelLoading(false))
            )
            .subscribe(channel => this._channelsBaseState.setChannel(channel));
    }

    clearChannel(): void {
        this._channelsBaseState.setChannel(null);
    }

    getChannel$(): Observable<Channel> {
        return this._channelsBaseState.getChannel$().pipe(
            map(channel => (channel && {
                ...channel,
                languages: {
                    selected: channel.languages?.selected || [],
                    default: channel.languages?.default
                }
            }))
        );
    }

    getChannelError$(): Observable<HttpErrorResponse> {
        return this._channelsBaseState.getChannelError$();
    }

    saveChannel(id: number, channel: PutChannel): Observable<void> {
        this._channelsBaseState.setChannelError(null);
        this._channelsBaseState.setChannelSaving(true);
        return this._channelsBaseApi.putChannel(id, channel)
            .pipe(
                catchError(error => {
                    this._channelsBaseState.setChannelError(error);
                    throw error;
                }),
                finalize(() => this._channelsBaseState.setChannelSaving(false))
            );
    }

    createChannel(channel: PostChannelRequest): Observable<number> {
        this._channelsBaseState.setChannelError(null);
        this._channelsBaseState.setChannelSaving(true);
        return this._channelsBaseApi.postChannel(channel)
            .pipe(
                catchError(error => {
                    this._channelsBaseState.setChannelError(error);
                    return of(null);
                }),
                map((result: { id: number }) => result?.id),
                finalize(() => this._channelsBaseState.setChannelSaving(false))
            );
    }

    isChannelSaving$(): Observable<boolean> {
        return this._channelsBaseState.isChannelSaving$();
    }

    deleteChannel(id: string): Observable<void> {
        return this._channelsBaseApi.deleteChannel(id);
    }

    isChannelInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsBaseState.isChannelLoading$(),
            this._channelsBaseState.isChannelSaving$()
        ]);
    }

    getChannelsNames$(ids: number[]): Observable<IdName[]> {
        return this._channelsBaseState.channelsCache.getItems$(ids,
            id => (this._channelsBaseApi.getChannel(id.toString())) as Observable<IdName>);
    }
}

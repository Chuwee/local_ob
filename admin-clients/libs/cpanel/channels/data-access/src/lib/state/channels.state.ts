import { StateProperty } from '@OneboxTM/utils-state';
import { LoginAuthConfig } from '@admin-clients/shared/common/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { ChannelCustomResources, GetCustomAssetsResponse } from '../models/channel-custom-resources.model';
import { ChannelFaqs } from '../models/channel-faqs.model';
import { GetReviewConfigElementResponse, ReviewConfig } from '../models/channel-review-config.model';
import { ChannelWhitelabelSettings } from '../models/channel-whitelabel-settings.model';
import { Channel } from '../models/channel.model';
import { GetChannelsResponse } from '../models/get-channels-response.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelsState {
    readonly channelsList = new StateProperty<GetChannelsResponse>();
    readonly channelsCache = new ItemCache<IdName>();
    // channel whitelabel settings
    readonly channelWhitelabelSettings = new StateProperty<ChannelWhitelabelSettings>();
    // faqs
    readonly faqsList = new StateProperty<ChannelFaqs[]>();
    // faq
    readonly faq = new StateProperty<ChannelFaqs>();

    // Channel detail
    private readonly _channel = new BaseStateProp<Channel>();
    readonly setChannel = this._channel.setValueFunction();
    readonly getChannel$ = this._channel.getValueFunction();
    readonly setChannelLoading = this._channel.setInProgressFunction();
    readonly isChannelLoading$ = this._channel.getInProgressFunction();
    readonly setChannelError = this._channel.setErrorFunction();
    readonly getChannelError$ = this._channel.getErrorFunction();

    // post channel saving
    private readonly _channelSaving = new BaseStateProp<void>();
    readonly setChannelSaving = this._channelSaving.setInProgressFunction();
    readonly isChannelSaving$ = this._channelSaving.getInProgressFunction();

    // custom resources
    readonly customResources = new StateProperty<ChannelCustomResources>();
    readonly customAssets = new StateProperty<GetCustomAssetsResponse>();

    //auth config
    readonly authConfig = new StateProperty<LoginAuthConfig>();

    // review
    readonly reviewConfig = new StateProperty<ReviewConfig>();
    readonly reviewConfigElements = new StateProperty<GetReviewConfigElementResponse>();
}

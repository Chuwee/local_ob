import { StateProperty } from '@OneboxTM/utils-state';
import { ChannelSurcharge, ChannelPriceSimulation, ChannelCommission } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelContentImageRequest, EventChannelContentImageRequestConfig } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ContentLinkRequest, ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { B2BPublishConfigurationResponse } from '../models/b2b-publish-configuration.model';
import { EventChannelAttendantsLink } from '../models/event-channel-attendants-link.model';
import { EventChannelB2bAssignations } from '../models/event-channel-b2b-assignations.model';
import { EventChannelTicketTemplate } from '../models/event-channel-ticket-templates.model';
import { EventChannel } from '../models/event-channel.model';
import { GetEventChannelsCandidatesResponse } from '../models/event-channels-candidates.model';
import { EventChannelsLoadCase } from '../models/event-channels-load.case';
import { GetEventChannelsResponse } from '../models/get-event-channels-response.model';

@Injectable()
export class EventChannelsState {
    // ListDetailState
    readonly listDetailState = new StateProperty<EventChannelsLoadCase>(EventChannelsLoadCase.none);
    // CHANNEL LIST
    readonly eventChannelList = new StateProperty<GetEventChannelsResponse>();
    // CHANNELS CANDIDATES LIST FOR SALES REQUESTS
    readonly eventChannelsCandidatesList = new StateProperty<GetEventChannelsCandidatesResponse>();
    // CHANNEL
    readonly eventChannel = new StateProperty<EventChannel>();
    // EVENT CHANNEL PROMOTER SURCHARGES
    readonly eventChannelSurcharges = new StateProperty<ChannelSurcharge[]>();
    readonly eventChannelSurchargesSaving = new StateProperty<void>();
    // EVENT CHANNEL CHANNEL SURCHARGES
    readonly eventChannelChannelSurcharges = new StateProperty<ChannelSurcharge[]>();
    // EVENT CHANNEL PriceSimulation
    readonly eventChannelPriceSimulation = new StateProperty<ChannelPriceSimulation[]>();
    // EVENT CHANNEL COMMISSIONS
    readonly eventChannelCommissions = new StateProperty<ChannelCommission[]>();
    // EVENT CHANNEL CONTENTS
    readonly eventChannelLinks = new StateProperty<ContentLinkRequest[]>();
    readonly eventChannelPublishedSessionLinks = new StateProperty<ContentLinkResponse>();
    readonly eventChannelUnpublishedSessionLinks = new StateProperty<ContentLinkResponse>();
    readonly eventChannelAttendantsLinks = new StateProperty<EventChannelAttendantsLink[]>();
    readonly ticketPdfPreviewDownloading = new StateProperty<void>();
    // ADD CHANNEL TO FAVORITE
    readonly channelToFavoriteSaving = new StateProperty<void>();
    // B2B ASSIGNATIONS
    readonly b2bAssignations = new StateProperty<EventChannelB2bAssignations>();
    // B2B PUBLISH CONFIGURATION
    readonly b2bPublishConfiguration = new StateProperty<B2BPublishConfigurationResponse>();
    // TICKET TEMPLATE
    readonly ticketTemplate = new StateProperty<EventChannelTicketTemplate[]>();
    // EVENT CHANNEL SQUARE IMAGES
    readonly eventChannelSquareImages = new StateProperty<EventChannelContentImageRequest[]>();
    // EVENT SESSION SQUARE IMAGES
    readonly eventSessionSquareImages = new StateProperty<EventChannelContentImageRequest[]>();
    // EVENT SESSION SQUARE IMAGES CONFIG
    readonly eventSessionSquareImagesConfig = new StateProperty<EventChannelContentImageRequestConfig[]>();
}

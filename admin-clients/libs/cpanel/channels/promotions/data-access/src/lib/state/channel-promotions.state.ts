
import { StateProperty } from '@OneboxTM/utils-state';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { ChannelPromotionEvents } from '../models/channel-promotion-events.model';
import { ChannelPromotionPriceTypes } from '../models/channel-promotion-price-types.model';
import { ChannelPromotionSessions } from '../models/channel-promotion-sessions.model';
import { ChannelPromotion } from '../models/channel-promotion.model';
import { GetChannelPromotionsResponse } from '../models/get-channel-promotions-response.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelPromotionsState {

    readonly promotionsList = new StateProperty<GetChannelPromotionsResponse>();

    readonly promotion = new StateProperty<ChannelPromotion>();

    readonly promotionSaving = new StateProperty<void>();

    readonly promotionContents = new StateProperty<CommunicationTextContent[]>();

    readonly promotionEvents = new StateProperty<ChannelPromotionEvents>();

    readonly promotionSessions = new StateProperty<ChannelPromotionSessions>();

    readonly promotionPriceTypes = new StateProperty<ChannelPromotionPriceTypes>();

}

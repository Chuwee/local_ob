import { StateProperty } from '@OneboxTM/utils-state';
import { PromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { EventPromotionPacks } from '../models/event-promotion-packs.model';
import { EventPromotionPriceTypes } from '../models/event-promotion-price-types.model';
import { EventPromotionRates } from '../models/event-promotion-rates.model';
import { EventPromotionSessions } from '../models/event-promotion-sessions.model';
import { EventPromotion } from '../models/event-promotion.model';
import { GetEventPromotionsResponse } from '../models/get-event-promotions-response.model';

@Injectable()
export class EventPromotionsState {
    // promotions list
    readonly eventPromotionsList = new StateProperty<GetEventPromotionsResponse>();
    // promotion
    readonly eventPromotion = new StateProperty<EventPromotion>();
    // promotion communication elements
    readonly eventPromotionChannelTextContents = new StateProperty<CommunicationTextContent[]>();
    // promotion channels
    readonly eventPromotionChannels = new StateProperty<PromotionChannels>();
    // promotion sessions
    readonly eventPromotionSessions = new StateProperty<EventPromotionSessions>();
    // promotion price types
    readonly eventPromotionPriceTypes = new StateProperty<EventPromotionPriceTypes>();
    // promotion rates
    readonly eventPromotionRates = new StateProperty<EventPromotionRates>();
    // promotion packs
    readonly eventPromotionPacks = new StateProperty<EventPromotionPacks>();
}

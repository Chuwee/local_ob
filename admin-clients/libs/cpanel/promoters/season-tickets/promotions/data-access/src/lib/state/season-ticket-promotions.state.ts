
import { StateProperty } from '@OneboxTM/utils-state';
import { PromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import {
    GetSeasonTicketPromotionsResponse,
    SeasonTicketPromotion,
    SeasonTicketPromotionPriceTypes, SeasonTicketPromotionRates
} from '../models/season-ticket-promotion.model';

@Injectable({ providedIn: 'root' })
export class SeasonTicketPromotionsState {
    // promotions list
    readonly stPromotionsList = new StateProperty<GetSeasonTicketPromotionsResponse>();
    // promotion
    readonly stPromotion = new StateProperty<SeasonTicketPromotion>();
    // promotion communication elements
    readonly stPromotionChannelTextContents = new StateProperty<CommunicationTextContent[]>();
    // promotion channels
    readonly stPromotionChannels = new StateProperty<PromotionChannels>();
    // promotion price types
    readonly stPromotionPriceTypes = new StateProperty<SeasonTicketPromotionPriceTypes>();
    // promotion rates
    readonly stPromotionRates = new StateProperty<SeasonTicketPromotionRates>();
}

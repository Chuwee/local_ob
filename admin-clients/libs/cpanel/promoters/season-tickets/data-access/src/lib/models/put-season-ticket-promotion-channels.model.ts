import { PromotionChannelsScope } from '@admin-clients/cpanel/promoters/data-access';

export interface PutSeasonTicketPromotionChannels {
    type: PromotionChannelsScope;
    channels: number[];
}

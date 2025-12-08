import { PromotionChannelsScope } from './promotion-scopes.enum';

export interface PutPromotionChannels {
    type: PromotionChannelsScope;
    channels: number[];
}

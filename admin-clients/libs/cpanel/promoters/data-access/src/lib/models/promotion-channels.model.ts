import { PromotionChannelsScope } from './promotion-scopes.enum';

export interface PromotionChannels {
    type: PromotionChannelsScope;
    channels: {
        id: number;
        name: string;
    }[];
}

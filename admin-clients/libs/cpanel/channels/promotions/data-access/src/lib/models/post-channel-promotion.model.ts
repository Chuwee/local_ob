import { ChannelPromotionType } from './channel-promotion-type.enum';

export interface PostChannelPromotion {
    name: string;
    type: ChannelPromotionType;
    from_entity_template_id?: number;
}

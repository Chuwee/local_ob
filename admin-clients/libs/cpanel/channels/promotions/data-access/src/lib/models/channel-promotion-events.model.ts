export enum ChannelPromotionEventScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface ChannelPromotionEvents {
    type?: ChannelPromotionEventScope;
    events: {
        id: number;
        catalog_sale_request_id: number;
        name: string;
        start_date: string;
    }[];
}

export interface PutChannelPromotionEvents {
    type: ChannelPromotionEventScope;
    events: number[];
}

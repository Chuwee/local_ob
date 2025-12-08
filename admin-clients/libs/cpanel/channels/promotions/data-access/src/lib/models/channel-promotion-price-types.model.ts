export enum ChannelPromotionPriceTypesScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface ChannelPromotionPriceTypes {
    type?: ChannelPromotionPriceTypesScope;
    price_types: {
        id: number;
        name: string;
        venue_template: {
            id: number;
            name: string;
        };
        catalog_sale_request_id: number;
    }[];
}

export interface PutChannelPromotionPriceTypes {
    type: ChannelPromotionPriceTypesScope;
    price_types: number[];
}

export enum ChannelPromotionSessionScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface ChannelPromotionSessions {
    type?: ChannelPromotionSessionScope;
    sessions: {
        id: number;
        name: string;
        date?: {
            start?: string;
        };
        catalog_sale_request_id: number;
    }[];
}

export interface PutChannelPromotionSessions {
    type: ChannelPromotionSessionScope;
    sessions: number[];
}

import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface ChannelPromotionSessionsListElement {
    id: number;
    name: string;
    catalog_sale_request_id: number;
    dates?: {
        start?: string;
    };
    type: SessionType;
}

export interface ChannelPromotionSessionsEventSelectionListElement {
    id: number;
    name: string;
}

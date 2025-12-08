import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface EventSessionSelectorListElement {
    id: number;
    name: string;
    catalog_sale_request_id: number;
    event_id: number;
    dates?: {
        start?: string;
    };
    type: SessionType;
}

export interface SessionsEventSelectionListElement {
    id: number;
    saleReqId: number;
    name: string;
}

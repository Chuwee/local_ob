import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface PutEventPrice {
    price_type_id: number;
    rate_id: number;
    value: number;
    ticket_type: ActivityTicketType;
}

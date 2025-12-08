import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface EventPrice {
    ticket_type?: ActivityTicketType;
    price_type: {
        id: number;
        code: string;
        description: string;
    };
    rate: {
        id: number;
        name: string;
        rate_group: {
            id: number;
            name: string;
        };
    };
    value: number;
}

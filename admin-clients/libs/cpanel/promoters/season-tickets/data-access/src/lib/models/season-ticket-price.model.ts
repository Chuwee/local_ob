import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface SeasonTicketPrice {
    ticket_type?: ActivityTicketType;
    price_type: {
        id: number;
        code: string;
        description: string;
    };
    rate: {
        id: number;
        name: string;
    };
    value: number;
}

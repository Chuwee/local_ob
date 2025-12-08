import { IdName } from '@admin-clients/shared/data-access/models';

export interface TicketRelocation {
    id: number;
    date: string;
    sector: IdName;
    row: IdName;
    seat: IdName;
    price_type: IdName;
}

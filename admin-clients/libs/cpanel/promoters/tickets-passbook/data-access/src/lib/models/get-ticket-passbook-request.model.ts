import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { TicketPassbookType } from './ticket-passbook-type.enum';

export interface GetTicketPassbookRequest extends PageableFilter {
    entity_id?: number | null;
    create_start_date?: string;
    create_end_date?: string;
    type?: TicketPassbookType;
}

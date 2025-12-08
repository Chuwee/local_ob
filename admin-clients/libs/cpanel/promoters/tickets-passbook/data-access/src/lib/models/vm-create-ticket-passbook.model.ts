import { TicketPassbookType } from './ticket-passbook-type.enum';

export interface VMCreateTicketPassbok {
    entity_id: number;
    code: string;
    name: string;
    type: TicketPassbookType;
}

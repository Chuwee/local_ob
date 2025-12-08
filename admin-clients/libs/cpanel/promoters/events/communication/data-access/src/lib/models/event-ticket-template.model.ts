import { EventTicketTemplateType } from './event-ticket-template-type.enum';
import { TicketContentFormat } from './ticket-content-format.enum';

export interface EventTicketTemplate {
    id: number;
    type: EventTicketTemplateType;
    format: TicketContentFormat;
}

export interface EventTicketTemplateFields {
    formField: string;
    format: TicketContentFormat;
    type: EventTicketTemplateType;
}

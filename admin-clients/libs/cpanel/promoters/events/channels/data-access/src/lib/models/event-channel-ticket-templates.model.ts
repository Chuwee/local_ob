import { EventChannelTicketContentFormat } from './event-channel-ticket-content-format.enum';
import { EventChannelTicketTemplateType } from './event-channel-ticket-template-type.enum';

export interface EventChannelTicketTemplate {
    id: string;
    type: EventChannelTicketTemplateType;
    format: EventChannelTicketContentFormat;
}

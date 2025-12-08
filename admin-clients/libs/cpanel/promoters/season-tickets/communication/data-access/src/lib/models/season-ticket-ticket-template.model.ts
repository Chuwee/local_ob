import { TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { SeasonTicketTemplateType } from './season-ticket-template-type.enum';

export interface SeasonTicketTicketTemplate {
    id: number;
    type: SeasonTicketTemplateType;
    format: TicketTemplateFormat;
}

export interface SeasonTicketTicketTemplateFields {
    formField: string;
    format: TicketTemplateFormat;
    type: SeasonTicketTemplateType;
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { TicketTemplateDesignTypes } from './ticket-template-design-types.enum';
import { TicketTemplateFilterFields } from './ticket-template-filter-fields.enum';
import { TicketTemplateFormat } from './ticket-template-format.enum';

export interface GetTicketTemplatesRequest extends PageableFilter {
    fields?: TicketTemplateFilterFields[];
    entity_id?: number;
    design_id?: number;
    format?: TicketTemplateFormat;
    printer?: string;
    paper_type?: string;
    design_type?: TicketTemplateDesignTypes;
}

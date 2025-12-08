import { TicketTemplateFormat } from './ticket-template-format.enum';

export interface TicketTemplateDesign {
    id: number;
    name: string;
    format: TicketTemplateFormat;
    printer: string;
    description?: string;
    paper_type: string;
    orientation: string;
}
export interface TicketTemplate {
    id: number;
    name: string;
    entity: {
        id: number;
        name: string;
    };
    design: TicketTemplateDesign;
    default: boolean;
    languages: {
        selected: string[];
        default: string;
    };
}

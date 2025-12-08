import { SaleRequestTicketContentFormat } from './sale-request-ticket-content-format.enum';

export interface SaleRequestTicketContentTemplate {
    name: string;
    design: {
        description: string;
        format: SaleRequestTicketContentFormat;
        id: number;
        name: string;
        orientation: string;
        paper_type: string;
        printer: string;
    };
}


import { SaleRequestTicketContentTextType } from './sale-request-ticket-content-text-type.enum';

export interface SaleRequestTicketContentText {
    language: string;
    type: SaleRequestTicketContentTextType;
    value?: string;
}

export interface SaleRequestTicketContentTextField {
    formField: string;
    type: SaleRequestTicketContentTextType;
}

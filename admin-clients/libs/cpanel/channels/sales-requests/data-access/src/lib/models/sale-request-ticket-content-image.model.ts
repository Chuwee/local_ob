import { SaleRequestTicketContentImageType } from './sale-request-ticket-content-image-type.enum';

export interface SaleRequestTicketContentImage {
    language: string;
    type: SaleRequestTicketContentImageType;
    image_url?: string;
}

export interface SaleRequestTicketContentImageField {
    formField: string;
    type: SaleRequestTicketContentImageType;
}

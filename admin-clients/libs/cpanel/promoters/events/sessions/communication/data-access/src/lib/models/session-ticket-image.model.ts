import { SessionTicketPassbookImageType, SessionTicketPdfImageType, SessionTicketPrinterImageType } from './session-ticket-image-type.enum';

export interface SessionTicketImage {
    language: string;
    type: SessionTicketPdfImageType | SessionTicketPrinterImageType | SessionTicketPassbookImageType;
    image_url: string;
    position?: number;
}

export interface SessionTicketContentImageFields {
    formField: string;
    type: SessionTicketPdfImageType | SessionTicketPrinterImageType | SessionTicketPassbookImageType;
    maxSize: number;
}

import { SessionTicketPdfImageType, SessionTicketPrinterImageType, SessionTicketPassbookImageType } from './session-ticket-image-type.enum';

export interface SessionTicketImageRequest {
    language: string;
    type: SessionTicketPdfImageType | SessionTicketPrinterImageType | SessionTicketPassbookImageType;
    image: string;
}

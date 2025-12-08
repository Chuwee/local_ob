import { SeasonTicketTicketContentImageType } from './season-ticket-ticket-content-image-type.enum';

export interface SeasonTicketTicketContentImage {
    language: string;
    type: SeasonTicketTicketContentImageType;
    image_url?: string;
}

export interface SeasonTicketTicketContentImageFields {
    formField: string;
    type: SeasonTicketTicketContentImageType;
    maxSize: number;
}

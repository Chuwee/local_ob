import { ChannelTicketContentImageType } from './channel-ticket-content-image-type.enum';

export interface ChannelTicketContentImage {
    language: string;
    type: ChannelTicketContentImageType;
    image_url?: string;
}

export interface ChannelTicketContentImageField {
    formField: string;
    type: ChannelTicketContentImageType;
    maxSize: number;
}

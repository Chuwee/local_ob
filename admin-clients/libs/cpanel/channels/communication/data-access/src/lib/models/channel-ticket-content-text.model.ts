import { ChannelContentFieldsRestrictions } from './channel-content-fields-restrictions.enum';
import { ChannelTicketContentTextType } from './channel-ticket-content-text-type.enum';

export interface ChannelTicketContentText {
    language: string;
    type: ChannelTicketContentTextType;
    value?: string;
}

export interface ChannelTicketContentTextField {
    formField: string;
    type: ChannelTicketContentTextType;
    maxLength: ChannelContentFieldsRestrictions.colorPassbookLength;
}

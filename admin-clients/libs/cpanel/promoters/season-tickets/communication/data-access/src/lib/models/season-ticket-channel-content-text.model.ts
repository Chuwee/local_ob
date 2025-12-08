import { SeasonTicketChannelContentTextType } from './season-ticket-channel-content-text-type.enum';
import { SeasonTicketTicketContentFieldsRestrictions } from './season-ticket-ticket-content-fields-restrictions.enum';
import { SeasonTicketTicketContentTextType } from './season-ticket-ticket-content-text-type.enum';

export interface SeasonTicketChannelContentText {
    language: string;
    type: SeasonTicketChannelContentTextType;
    value?: string;
}

export interface SeasonTicketTicketContentTextFields {
    formField: string;
    type: SeasonTicketTicketContentTextType;
    maxLength: SeasonTicketTicketContentFieldsRestrictions;
}

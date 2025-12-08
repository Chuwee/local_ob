import { SeasonTicketTicketContentTextType } from './season-ticket-ticket-content-text-type.enum';

export interface SeasonTicketTicketContentText {
    language: string;
    type: SeasonTicketTicketContentTextType;
    value?: string;
}

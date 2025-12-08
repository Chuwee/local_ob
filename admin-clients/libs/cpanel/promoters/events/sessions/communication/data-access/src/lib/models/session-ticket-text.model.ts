import { SessionTicketTextType } from './session-ticket-text-type.enum';

export interface SessionTicketText {
    language: string;
    type: SessionTicketTextType;
    value: string;
}

export interface SessionTicketContentTextFields {
    formField: string;
    type: SessionTicketTextType;
    maxLength: number;
}
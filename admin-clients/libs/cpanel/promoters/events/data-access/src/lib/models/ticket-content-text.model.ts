import { ValidatorFn } from '@angular/forms';
import { TicketContentTextType } from './ticket-content-text-type.enum';

export interface TicketContentText {
    language: string;
    type: TicketContentTextType;
    value?: string;
}

export interface TicketContentTextFields {
    type: TicketContentTextType;
    validators: ValidatorFn[];
}

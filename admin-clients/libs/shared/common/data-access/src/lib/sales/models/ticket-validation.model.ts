import { TicketValidationStatus } from './ticket-validation-status.enum';

export interface TicketValidation {
    session_id: number;
    user: string;
    date: string;
    status: TicketValidationStatus;
}

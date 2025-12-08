import { TicketState } from './ticket-state.enum';

export interface StateHistory {
    id: number;
    order_code: string;
    date: string;
    state: TicketState;
}

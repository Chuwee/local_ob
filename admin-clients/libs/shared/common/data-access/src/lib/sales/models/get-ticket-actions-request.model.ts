import { GetTicketsRequest } from './get-tickets-request.model';
import { TicketActionTypes } from './ticket-actions-types.enum';

export interface GetTicketActionsRequest extends GetTicketsRequest {
    action_types: TicketActionTypes[];
}

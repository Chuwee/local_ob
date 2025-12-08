import { OrderItemBase } from './order-item-base.model';
import { TicketState } from './ticket-state.enum';

/** Alone item (/order-items), contains a processed state conditioned by its order **/
export interface OrderItemDetails extends OrderItemBase {
    state: TicketState;
}

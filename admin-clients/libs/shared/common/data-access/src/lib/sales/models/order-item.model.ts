import { OrderItemBase } from './order-item-base.model';
import { TicketDetailState } from './ticket-detail-state.enum';

/** item inside an order (/orders), contains the strict state of the item**/
export interface OrderItem extends OrderItemBase {
    user_id?: string;
    state: TicketDetailState;
}

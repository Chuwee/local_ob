import { OrderItem } from './order-item.model';
import { TicketDetailState } from './ticket-detail-state.enum';

export interface OrderSubItem extends OrderItem {
    session: {
        id: number;
        name: string;
        date: string;
    };
    state: TicketDetailState.refunded | TicketDetailState.purchased;
}

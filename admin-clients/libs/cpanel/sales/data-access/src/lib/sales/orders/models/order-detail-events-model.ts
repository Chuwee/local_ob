import { TicketAllocationEvent, TicketAllocationSession, OrderItem, OrderSubItem } from '@admin-clients/shared/common/data-access';

export interface OrderDetailTicket {
    text: string;
    item: OrderItem | OrderSubItem;
    hidden?: boolean;
    expandable?: boolean;
    expanded?: boolean;
    parentId?: number;
    hideRelatedOrders?: boolean;
}

export interface OrderDetailSession {
    session: TicketAllocationSession;
    tickets: OrderDetailTicket[];
    // products?: OrderDetailProduct[];
    refundAllowed: boolean;
    isSkipGeneration: boolean;
    pack?: OrderItem['pack'];
}

export interface OrderDetailEvent {
    event: TicketAllocationEvent;
    sessions: OrderDetailSession[];
}

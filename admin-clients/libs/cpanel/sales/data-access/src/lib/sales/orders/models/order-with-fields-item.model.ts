import { TicketType, EventType, TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { OrderWithFieldsItemState } from './order-with-fields-item-state.enum';
import { OrderWithFieldsItemType } from './order-with-fields-item-type.enum';

export interface OrderWithFieldsItem {
    id?: number;
    type?: OrderWithFieldsItemType;
    state?: OrderWithFieldsItemState;
    ticket?: {
        type?: TicketType;
        allocation?: {
            type?: TicketAllocationType;
            event?: {
                id?: number;
                name?: string;
                type?: EventType;
            };
            session?: {
                id?: number;
                name?: string;
                date?: {
                    start?: string;
                };
            };
            venue?: {
                id?: number;
                name?: string;
            };
        };
    };
}

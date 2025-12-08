import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { ProductEventSessionSelectionType } from './product-event-session-selection-type.model';
import { ProductEventStatus } from './product-event-status.model';

export interface ProductEvent {
    product: {
        id: number;
        name: string;
    };
    event: {
        id: number;
        name: string;
        status: EventStatus;
        start_date: string;
    };
    status: ProductEventStatus;
    sessions_selection_type: ProductEventSessionSelectionType;
}

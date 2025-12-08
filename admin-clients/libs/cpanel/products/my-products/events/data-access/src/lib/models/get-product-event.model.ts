import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { ProductEventStatus } from './product-event-status.model';

export type GetProductEventsRequest = {
    event_status: EventStatus;
    product_event_status: ProductEventStatus;
    start_date: string;
};
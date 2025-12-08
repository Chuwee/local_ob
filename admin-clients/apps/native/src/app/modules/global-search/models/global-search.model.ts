import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { Order } from '@admin-clients/cpanel/sales/feature';
import { OrderItem } from '@admin-clients/shared/common/data-access';

export interface GlobalSearchRequestResponse {
    name: string;
    response: OrderItem[] | Event[] | Order[];
}

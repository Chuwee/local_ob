import { OrderItemType } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetFilterRequest extends Omit<PageableFilter, 'offset' | 'sort' | 'aggs'> {
    event_id?: string;
    channel_entity_id?: string;
    channel_id?: string;
    event_entity_id?: string;
    purchase_date_from?: string;
    purchase_date_to?: string;
    cursor?: string;
    currency_code?: string;
    product_id?: string;
    item_type?: OrderItemType;
}

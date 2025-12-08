import { OrderType } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetOrdersRequest extends PageableFilter {
    code?: string;
    channel_id?: string[];
    channel_entity_id?: string;
    event_id?: string[];
    event_entity_id?: string;
    merchant?: string;
    user_id?: string;
    session_id?: string[];
    type?: OrderType[];
    price_gateway?: string;
    purchase_date_from?: string;
    purchase_date_to?: string;
    order_alive?: boolean;
    session_start_date_from?: string;
    session_start_date_to?: string;
    currency_code?: string;
}

import { OrderType, ClientType } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetOrdersWithFieldsRequest extends PageableFilter {
    code?: string[];
    channel_id?: number[];
    channel_entity_id?: number | number[];
    type?: OrderType[];
    event_id?: number[];
    event_entity_id?: number | number[];
    session_id?: number[];
    session_start_date_from?: string;
    session_start_date_to?: string;
    client_entity_id?: number[];
    purchase_date_from?: string;
    purchase_date_to?: string;
    last_modified?: string;
    client_type?: ClientType;
    buyer_email?: string[];
    user_id?: number | number[];
    gateways?: string[];
    merchant?: string | string[];
    include_updated_refunds?: boolean;
    order_alive?: boolean;
    reallocation_refund?: boolean;
    delivery?: string | string[];
    currency_code?: string;
}

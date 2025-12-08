import { ListResponse } from '@OneboxTM/utils-state';
import { EventType } from '@admin-clients/shared/common/data-access';

export enum CustomerProductSeatType {
    numbered = 'NUMBERED',
    notNumbered = 'NOT_NUMBERED'
}

export interface CustomerExternalProductListItem {
    event?: {
        id: number;
        name: string;
        type: EventType;
    };
    entity?: {
        id: number;
        name: string;
    };
    sector_name: string;
    row_name: string;
    seat_name: string;
    price_zone_name: string;
    rate_name: string;
    purchase_date: string;
    not_numbered_zone_name: string;
    seat_type: CustomerProductSeatType;
}

export interface GetCustomerExternalProductsResponse extends ListResponse<CustomerExternalProductListItem> {
}


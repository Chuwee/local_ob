import { ListResponse } from '@OneboxTM/utils-state';
import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketDetailState, EventType } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export enum CustomerProductStatus {
    active = 'ACTIVE',
    locked = 'LOCKED'
}

export interface CustomerProductListItem {
    id: number;
    state: TicketDetailState;
    status?: TicketDetailState;
    event?: {
        id: number;
        name: string;
        type: EventType;
        status: EventStatus;
    };
    session?: {
        id: number;
        name: string;
    };
    entity?: {
        id: number;
        name: string;
    };
    price: {
        final_price: number;
        currency: string;
    };
    product_status: CustomerProductStatus;
    seat: {
        seat: {
            id: number;
            name: string;
        };
        row: {
            id: number;
            name: string;
        };
        sector: {
            id: number;
            name: string;
        };
        not_numbered_area: {
            id: number;
            name: string;
        };
    };
    product?: {
        product: {
            id: number;
            name: string;
        };
        variant?: {
            id: number;
            name: string;
        };
        entity?: {
            id: number;
            name: string;
        };
    };
}

export interface GetCustomerProductsResponse extends ListResponse<CustomerProductListItem> {
}

export interface CustomerProductsFilters extends PageableFilter {
    product_type?: 'SEAT' | 'PRODUCT' | 'GROUP';
}


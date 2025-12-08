import { ProductType } from '@admin-clients/cpanel/products/my-products/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface ProductSaleRequest {
    id: number;
    status: ProductSaleRequestStatus;
    date: string;
    languages: {
        default: string;
        selected: string[];
    };
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    product: {
        product_id: number;
        product_name: string;
        product_type: ProductType;
        entity: {
            id: number;
            name: string;
        };
        currency_code: string; /* TO DO: Check naming with backend when done */
        contact_person: Partial<{
            name: string;
            surname: string;
            email: string;
            phone: string;
        }>;
    };
}

export interface ProductSaleRequestListElem {
    id: number;
    status: ProductSaleRequestStatus;
    request_date: string;
    channel: {
        id: number;
        name: string;
    };
    product: {
        id: number;
        name: string;
    };
    producer: {
        id: number;
        name: string;
    };
}

export interface GetProductsSaleRequestsReq extends PageableFilter {
    status?: ProductSaleRequestStatus[];
    startDate?: string;
    endDate?: string;
    fields?: string[];
}

export const productSaleRequestStatus = ['PENDING', 'ACCEPTED', 'REJECTED'] as const;
export type ProductSaleRequestStatus = typeof productSaleRequestStatus[number];

import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { ActionType } from './action-type.enum';
import { DeliveryMethod } from './delivery-method.enum';
import { SaleStatus } from './sale-status.enum';

export interface Sale {
    id: number;
    code: string;
    status: SaleStatus;
    listing_id: number;
    supplier: SupplierName;
    supplier_id: string;
    supplier_listing_id: string;
    products: [
        {
            seat_id: string;
            seat: string;
            row: string;
        }
    ];
    price: {
        currency: string;
        supplier_currency: string;
        exchange_rate: number;
        payout_per_product: number;
    };
    delivery_method: DeliveryMethod;
    event: {
        id: number;
        supplier_id: string;
        name: string;
        date: Date;
        country_code: string;
    };
    created: Date;
    section: string;
    last_update: Date;
    last_action: ActionType;
    general_admission: boolean;
    error_description?: string;
    actions: {
        retry_confirm: boolean;
        retry_fulfill: boolean;
    };
}

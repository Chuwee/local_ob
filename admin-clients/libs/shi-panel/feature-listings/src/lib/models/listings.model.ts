import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { ListingSplitOptions } from './listing-split-options.enum';
import { ListingStatus } from './listing-status.enum';
import { ListingStockTypes } from './listing-stock-types.enum';

export interface Listing {
    id: number;
    code: string;
    status: ListingStatus;
    error_description: string;
    event_id: number;
    quantity: number;
    supplier: SupplierName;
    supplier_id: string;
    supplier_event_id: string;
    price: {
        currency: string;
        supplier_currency: string;
        exchange_rate: number;
        payout_per_product: number;
    };
    created: Date;
    last_update: Date;
    general_admission: boolean;
    inhand?: boolean;
    inhand_date: Date;
    section: string;
    products: [];
    comments: string;
    exchange_rate: string;
    split_option: ListingSplitOptions;
    stock_types: ListingStockTypes;
    blacklisted: boolean;
}

export interface ListingToBlacklist {
    code: string;
    event_id: number;
}

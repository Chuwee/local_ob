import { OrderType, ClientType, PriceCharges, PriceSales } from '@admin-clients/shared/common/data-access';
import { OrderWithFieldsItem } from './order-with-fields-item.model';

export interface OrderWithFields {
    code?: string;
    type?: OrderType;
    date?: string;
    channel?: {
        id?: number;
        name?: string;
    };
    buyer_data?: {
        name?: string;
        surname?: string;
        email?: string;
        phone?: string;
    };
    price: {
        base?: number;
        delivery?: number;
        insurance?: number;
        sales?: Partial<PriceSales>;
        charges?: Partial<PriceCharges>;
        b2b?: {
            conditions?: number;
            commission?: number;
        };
        channel_commission?: number;
        final?: number;
        currency?: string;
    };
    client_type?: ClientType;
    last_modified?: string;
    items_count?: number;
    items?: OrderWithFieldsItem[];
}

//Only used for building and displaying the data on event column and tooltip on orders-list
export interface VmOrderWithFields extends OrderWithFields {
    eventColumnData?: string;
    eventColumnExtendedData?: string;
    totalCharges?: number;
    totalPromotions?: number;
}

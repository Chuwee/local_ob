import { IdName } from '@admin-clients/shared/data-access/models';
import { ActionsHistory } from './actions-history.model';
import { BuyerData } from './buyer-data.model';
import { OperativeGrant } from './operative-grant.model';
import { OrderItemType } from './order-item-type.enum';
import { OrderSubItem } from './order-subitem.model';
import { OrderType } from './order-type.enum';
import { Price, PricePartialTaxes } from './price.model';
import { SaleItemProduct } from './product.model';
import { RelatedProductState } from './related-product-state.model';
import { Ticket } from './ticket.model';

export interface OrderItemBase {
    type?: OrderItemType;
    id: number;
    buyer_data?: BuyerData;
    ticket?: Ticket;
    product?: SaleItemProduct;
    price: Partial<Price> & { taxes?: PricePartialTaxes };
    order: {
        code: string;
        date: string;
        type: OrderType;
    };
    subitems?: OrderSubItem[];
    action?: OperativeGrant;
    actions_history?: ActionsHistory[];
    previous_order?: {
        code: string;
        type: OrderType;
    };
    origin_market?: 'PRIMARY' | 'SECONDARY';
    related_product_state?: RelatedProductState;
    related_reallocation_code?: string;
    next_order: {
        code: string;
        type: OrderType;
    };
    pack?: {
        id: number;
        code: number;
        main_item: boolean;
        name?: string;
    };
    channel?: IdName;
    transfer?: {
        status?: 'TRANSFERRED' | 'IN_SEASON';
        receiver?: {
            date?: string;
            email?: string;
            name?: string;
            surname?: string;
            customer_id?: string;
        };
        resends_count?: number;
    };
}

import { BuyerData, ClientType, OrderItem, OrderType, Price, PriceTotalTaxes } from '@admin-clients/shared/common/data-access';
import { OrderChannel } from './order-channel.model';
import { OrderDeliveryType } from './order-delivery-type.enum';
import { OrderOperativeGrant } from './order-operative-grant.model';
import { PaymentData } from './payment-data.model';
import { PaymentDetail } from './payment-detail.model';

export interface OrderDetail {
    code: string;
    type: OrderType;
    date: string;
    delivery: OrderDeliveryType;
    language: string;
    channel: OrderChannel;
    buyer_data: BuyerData;
    price: Price & { taxes?: PriceTotalTaxes };
    client_type: ClientType;
    booking_expires?: string;
    items_count: number;
    tickets_count: number;
    products_count: number;
    items: OrderItem[];
    action: OrderOperativeGrant;
    payment_data: PaymentData[];
    payment_detail: PaymentDetail;
    related_original_reallocation_code?: string;
    terminal?: {
        id: number;
        name: string;
    };
    external_code?: string;
    point_of_sale?: {
        id: number;
        name: string;
    };
    user?: {
        id: number;
        name: string;
        username: string;
    };
    shared_user?: {
        email: string;
        id: number;
        name: string;
        username: string;
    };
    previous_order?: {
        code?: string;
        type?: OrderType;
    };
}


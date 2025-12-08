import { BuyerGender } from './buyer-gender.enum';
import { BuyerType } from './buyer-type.enum';

export interface BuyersQuery {
    entity_id?: number;
    email?: string;
    name?: string;
    surname?: string;
    gender?: BuyerGender;
    type?: BuyerType;
    allow_commercial_mailing?: boolean;
    age?: {
        from?: number;
        to?: number;
    };
    country?: string;
    country_subdivision?: string[];
    phone?: string;
    date_entry?: string;
    date_update?: string;
    channel_id?: number[];
    event_id?: number[];
    session_id?: number[];
    event_promotion_id?: number[];
    collective_id?: number[];
    subscription_list_id?: number[];
    session_dates?: {
        from?: string;
        to?: string;
    };
    order?: {
        code?: string;
        barcode?: string;
        dates?: {
            purchase?: {
                from?: string;
                to?: string;
            };
            first_purchase?: {
                from?: string;
                to?: string;
            };
            without_transactions?: {
                from?: string;
                to?: string;
            };
            presale_days?: {
                from?: number;
                to?: number;
            };
        };
        transactions?: {
            orders_purchased?: {
                from?: number;
                to?: number;
            };
            order_items_purchased?: {
                from?: number;
                to?: number;
            };
            order_items_refunded?: {
                from?: number;
                to?: number;
            };
        };
        prices?: {
            orders_purchased?: {
                from?: number;
                to?: number;
            };
            orders_refunded?: {
                from?: number;
                to?: number;
            };
            order_items_avg?: {
                from?: number;
                to?: number;
            };
            order_items_base_price?: {
                from?: number;
                to?: number;
            };
            order_items_final_price?: {
                from?: number;
                to?: number;
            };
            invitations?: boolean;
        };
    };
    q?: string;
    sort?: string;
    aggs?: boolean;
    limit?: number;
    offset?: number;
}

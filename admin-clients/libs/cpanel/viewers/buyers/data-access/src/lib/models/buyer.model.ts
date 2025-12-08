import { Country, Region } from '@admin-clients/shared/common/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { BuyerGender } from './buyer-gender.enum';

export interface Buyer {
    entity_id?: number;
    id: string;
    type?: string;
    email?: string;
    name?: string;
    surname?: string;
    gender?: BuyerGender;
    date_of_birth?: string;
    language?: string;
    date?: {
        create?: string;
        last_update?: string;
    };
    identity_card?: {
        type?: string;
        id: string;
    };
    location?: {
        country?: Pick<Country, 'code'>;
        country_subdivision?: Pick<Region, 'code'>;
        city?: string;
        address?: string;
        zip_code?: string;
    };
    phone?: {
        fix?: string;
        mobile?: string;
    };
    notes?: string;
    allow_commercial_mailing?: boolean;
    subscription_lists?: IdName[];
    channels?: IdName[];
    collectives?: IdName[];
    total_orders?: number;
    total_order_items?: number;
    sum_price?: number;
    sum_refunded_price?: number;
    avg_price?: number;
    total_refunded_items?: number;
    avg_days_before_date_buyed?: number;
    first_purchase?: string;
    last_purchase?: string;
}

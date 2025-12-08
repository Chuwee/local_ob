import { IdName } from '@admin-clients/shared/data-access/models';

export const payoutStatus = ['UNPAID', 'PAID'] as const;
export type PayoutStatus = typeof payoutStatus[number];

export interface Payout {
    uuid?: string;
    original_order_code?: string;
    location?: {
        id?: number;
        seat_id?: number;
        num_seat?: number;
        row?: IdName;
        sector?: IdName;
        not_numbered_area?: IdName;
        price_zone?: IdName;
        rate?: IdName;
        session?: IdName;
        event?: IdName;
        channel?: IdName;
    };
    customer?: {
        user_id?: string;
        id?: string;
        name?: string;
        surname?: string;
        iban?: string;
        email?: string;
        bacs_sort_code?: string;
        bacs_account_number?: string;
    };
    payout_status?: PayoutStatus;
    pay_to_balance?: boolean;
    purchase_date?: string;
    original_price?: number;
    price?: number;
    currency?: string;
    payout_type?: 'MANUAL' | 'STRIPE' | 'UNKNOWN';
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { PayoutStatus } from './payout.model';

export interface GetPayoutsRequest extends PageableFilter {
    order_code?: string[];
    channel_id?: string[];
    event_id?: string[];
    session_id?: string[];
    payout_status?: PayoutStatus;
    purchase_date_from?: string;
    purchase_date_to?: string;
    session_start_date_from?: string;
    session_start_date_to?: string;
    currency_code?: string;
    payout_type?: 'STRIPE' | 'MANUAL';
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VoucherOrderType } from './voucher-order-type.enum';
import { VoucherOrderEmailStatus } from './voucher-orders-email-status.enum';

export interface GetVoucherOrdersRequest extends PageableFilter {
    code?: string;
    voucher_code?: string;
    channel_id?: string[];
    entity_id?: string[];
    type?: VoucherOrderType[];
    voucher_group_id?: number[];
    purchase_date_from?: string;
    purchase_date_to?: string;
    expiration_date_from?: string;
    expiration_date_to?: string;
    buyer_email?: string[];
    receiver_email?: string[];
    email_status?: VoucherOrderEmailStatus[];
    email_delivery_date_from?: string;
    email_delivery_date_to?: string;
    currency_code?: string;
    merchant?: string;
}

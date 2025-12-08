import { ObTimeUnit } from '@admin-clients/shared/data-access/models';

export interface EventChangeSeatSettings {
    enable: boolean;
    // Refactor this three fields to object when the backend is ready
    change_type: ChangeSeatType;
    event_change_seat_expiry?: ChangeSeatExpiry<ObTimeUnit.hours | ObTimeUnit.days>;

    ticket_selection: ChangeSeatTicketSelection;
    reallocation_channel: ChangeSeatReallocationChannel;
}

export interface ChangeSeatExpiry<T> {
    time_offset_limit_amount: number;
    time_offset_limit_unit: T;
}

export interface ChangeSeatTicketSelection {
    allowed_sessions: ChangeSeatAllowedSessions;
    same_date_only?: boolean;
    price: ChangeSeatPrice;
    tickets: ChangeSeatTicketsType;
}

export interface ChangeSeatPrice {
    type: ChangeSeatTicketsType;
    refund?: ChangeSeatRefund;
}

export interface ChangeSeatRefund {
    type: ChangeSeatRefundType;
    voucher_expiry?: ChangeSeatVoucherExpiry;
}

export interface ChangeSeatVoucherExpiry {
    enabled: boolean;
    expiry_time?: ChangeSeatExpiry<ObTimeUnit.hours | ObTimeUnit.days | ObTimeUnit.months | ObTimeUnit.weeks>;
}

export interface ChangeSeatReallocationChannel {
    id: number;
    apply_to_all_channel_types: boolean;
}

export enum ChangeSeatType {
    all = 'ALL',
    partial = 'PARTIAL'
}

export enum ChangeSeatAllowedSessions {
    same = 'SAME',
    different = 'DIFFERENT',
    any = 'ANY'
}

export enum ChangeSeatTicketsType {
    greaterOrEqual = 'GREATER_OR_EQUAL',
    any = 'ANY'
}

export enum ChangeSeatRefundType {
    none = 'NONE',
    voucher = 'VOUCHER'
}


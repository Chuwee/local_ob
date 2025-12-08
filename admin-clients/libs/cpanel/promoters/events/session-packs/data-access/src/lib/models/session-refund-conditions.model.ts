export interface Rate {
    id: number;
    name?: string;
}

export interface PriceType {
    id: number;
    name?: string;
}

export interface PricePercentageValue {
    price_type: PriceType;
    rate: Rate;
    value: number;
}

export interface SessionPackRefundCondition {
    session_id: number;
    session_name?: string;
    session_start_date?: string;
    price_percentage_values: PricePercentageValue[];
}

export interface SessionRefundConditions {
    refunded_seat_quota?: {
        enabled: boolean;
        id?: number;
    };
    print_refund_price_in_ticket: boolean;
    refunded_seat_status: number;
    refunded_seat_block_reason_id?: number;
    session_pack_automatically_calculate_conditions: boolean;
    session_pack_refund_conditions: SessionPackRefundCondition[];
}

export interface PutSessionRefundConditions {
    print_refund_price_in_ticket?: boolean;
    refunded_seat_status?: number;
    refunded_seat_block_reason_id?: number;
    refunded_seat_quota?: {
        enabled: boolean;
        id?: number;
    };
    session_pack_automatically_calculate_conditions?: boolean;
    session_pack_refund_conditions?: PutSessionPackRefundCondition[];
}

export interface PutSessionPackRefundCondition {
    session_id: number;
    price_percentage_values: {
        price_type_id: number;
        rate_id: number;
        value: number;
    }[];
}

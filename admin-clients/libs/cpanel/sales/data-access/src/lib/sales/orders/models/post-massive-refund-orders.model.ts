import { RefundType } from './refund-type.enum';

export interface PostMassiveRefundOrdersSummaryRequest {
    event_entity_id: number;
    event_id: number;
    session_id: number[];
    channel_id?: number[];
    order_codes?: string[];
}

export interface PostMassiveRefundOrdersRequest extends PostMassiveRefundOrdersSummaryRequest {
    refund_type: RefundType;
    include_surcharges: boolean;
    include_delivery: boolean;
    include_insurance: boolean;
}

export interface PostMassiveRefundOrdersSummaryResponse {
    total_orders: number;
    total_order_items: number;
    total_amount: {
        base: number;
        channel_charges: number;
        promoter_charges: number;
        delivery: number;
        insurance: number;
        sales: {
            automatic: number;
            promotion: number;
            discount: number;
            order_automatic: number;
            order_collective: number;
        };
        final_amount: number;
    };
}

export interface PostMassiveRefundOrdersResponse extends PostMassiveRefundOrdersSummaryResponse {
    event_entity: {
        id: number;
        name: string;
    };
    event: {
        id: number;
        name: string;
    };
    sessions: {
        id: number;
        name: string;
        date: {
            start: string;
        };
    }[];
    channels: {
        id: number;
        name: string;
    }[];
    export_id: string;
}

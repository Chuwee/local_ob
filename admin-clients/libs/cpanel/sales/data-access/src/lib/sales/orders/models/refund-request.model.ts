import { PaymentType } from './payment-type.enum';
import { RefundType } from './refund-type.enum';

export interface RefundRequest {
    items: {
        id: number;
        subitem_ids?: number[];
    }[];
    type: RefundType;
    include_surcharges: boolean;
    include_delivery: boolean;
    include_insurance: boolean;
    include_gateway: boolean;
    payments?: {
        type: PaymentType;
        value: number;
    }[];
}

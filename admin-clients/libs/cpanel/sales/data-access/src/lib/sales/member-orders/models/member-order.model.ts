import { MemberOrderState } from './member-order-state.enum';
import { MemberOrderType } from './member-order-type.enum';

export interface MemberOrder {
    code: string;
    state: MemberOrderState;
    type: MemberOrderType;
    language: string;
    purchase_date: string;
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    price: {
        currency: string;
        discounts: number;
        final: number;
        base: number;
        charges: number;
    };
    items: { id: string }[];
    buyer_data: {
        email: string;
        name: string;
        surname: string;
    };
}

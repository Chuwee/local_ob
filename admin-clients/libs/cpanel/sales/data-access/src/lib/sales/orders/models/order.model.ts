import { OrderType } from '@admin-clients/shared/common/data-access';

export interface Order {
    code: string;
    type: OrderType;
    date: string;
    items: number;
    channel: {
        id: number;
        name: string;
    };
    price: {
        base: number;
        final: number;
        currency: string;
    };
}

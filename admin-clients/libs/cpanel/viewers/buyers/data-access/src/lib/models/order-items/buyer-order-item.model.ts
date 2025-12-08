import { IdName } from '@admin-clients/shared/data-access/models';

export interface BuyerOrderItem {
    id: number;
    state: string;
    order: {
        code: string;
        date: string;
    };
    ticket: {
        type: string;
        allocation: {
            event: IdName;
            session: {
                id: number;
                name: string;
                date: {
                    start: string;
                };
            };
        };
    };
    channel: IdName;
    price: {
        base: number;
        final: number;
        currency: string;
    };
    product?: {
        product_name: string;
        variant_name: string;
    };

}

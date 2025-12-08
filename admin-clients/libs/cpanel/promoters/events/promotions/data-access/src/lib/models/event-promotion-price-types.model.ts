import { PromotionPriceTypesScope } from '@admin-clients/cpanel/promoters/data-access';

export interface EventPromotionPriceTypes {
    type: PromotionPriceTypesScope;
    price_types: {
        id: number;
        name: string;
    }[];
}

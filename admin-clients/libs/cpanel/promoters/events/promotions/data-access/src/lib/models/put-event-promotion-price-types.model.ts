import { PromotionPriceTypesScope } from '@admin-clients/cpanel/promoters/data-access';

export interface PutEventPromotionPriceTypes {
    type: PromotionPriceTypesScope;
    price_types: number[];
}

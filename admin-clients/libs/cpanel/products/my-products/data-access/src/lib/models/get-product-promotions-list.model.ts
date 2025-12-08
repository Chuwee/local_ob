import { Collective } from '@admin-clients/cpanel/collectives/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ProductPromotionDiscountType } from './product-promotion-discount-type.model';
import { ProductPromotionType } from './product-promotion-type.model';

export interface ProductPromotion {
    id?: number;
    name?: string;
    status?: PromotionStatus;
    type?: ProductPromotionType;
    discount?: {
        type: ProductPromotionDiscountType.fixed | ProductPromotionDiscountType.percentage;
        value: number;
    };
    activator?: {
        type: ProductPromotionActivatorType;
        collective?: Pick<Collective, 'id' | 'name' | 'type' | 'validation_method'>;
    };
}

export const productPromotionActivatorTypes = {
    default: 'DEFAULT',
    collective: 'COLLECTIVE'
} as const;
export type ProductPromotionActivatorType = typeof productPromotionActivatorTypes[keyof typeof productPromotionActivatorTypes];

export interface ProductPromotionsListReq extends PageableFilter {
    status?: PromotionStatus;
}

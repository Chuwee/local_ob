import { RangeElement } from '@admin-clients/shared-utility-models';

export const productSurchargeType = {
    generic: 'GENERIC',
    promotion: 'PROMOTION'
} as const;
export type ProductSurchargeType = (typeof productSurchargeType)[keyof typeof productSurchargeType];

export interface ProductSurcharge {
    type: ProductSurchargeType;
    ranges: RangeElement[];
}

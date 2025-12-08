import { RangeElement } from '@admin-clients/shared-utility-models';

export enum EntitySurchargeType {
    generic = 'GENERIC',
    secondaryMarketPromoter = 'SECONDARY_MARKET_PROMOTER'
}
export interface EntitySurcharge {
    type: EntitySurchargeType;
    ranges: RangeElement[];
}

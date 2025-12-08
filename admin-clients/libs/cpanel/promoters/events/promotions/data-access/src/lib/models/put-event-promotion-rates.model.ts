import { PromotionRatesScope } from '@admin-clients/cpanel/promoters/data-access';

export interface PutEventPromotionRates {
    type: PromotionRatesScope;
    rates: number[];
}

import { PromotionRatesScope } from '@admin-clients/cpanel/promoters/data-access';

export interface EventPromotionRates {
    type: PromotionRatesScope;
    rates: {
        id: number;
        name: string;
    }[];
}

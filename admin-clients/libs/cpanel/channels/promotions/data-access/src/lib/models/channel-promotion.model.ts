import { Collective } from '@admin-clients/cpanel/collectives/data-access';
import { PromotionStatus, PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { ChannelPromotionDiscountType } from './channel-promotion-discount-type.enum';
import { ChannelPromotionType } from './channel-promotion-type.enum';

export interface ChannelPromotion {
    id?: number;
    name?: string;
    status?: PromotionStatus;
    type?: ChannelPromotionType;
    subtype?: string;
    validity_period?: {
        type: PromotionValidityPeriodType;
        start_date?: string;
        end_date?: string;
    };
    discount?: {
        type: ChannelPromotionDiscountType;
        // TODO MULTICURRENCY: remove value and remove optionals
        value?: number;
        fixed_values?: CurrencyAmount[];
        percentage_value?: number;
    };
    combinable?: boolean;
    collective?: Pick<Collective, 'name' | 'id' | 'status' | 'type' | 'validation_method'>;
    usage_limits?: {
        promotion_max?: {
            enabled: boolean;
            current?: number;
            limit: number;
        };
        purchase_min?: {
            enabled: boolean;
            limit: number;
        };
        amount_min?: {
            enabled: boolean;
            // TODO MULTICURRENCY: remove amount and remove optional in values
            amount?: number;
            values?: CurrencyAmount[];
        };
    };
    packs?: {
        enabled: boolean;
        events?: number;
        sessions?: number;
    };
    alternative_surcharges?: {
        use_alternative_surcharges?: boolean;
        use_alternative_promoter_surcharges?: boolean;
    };
    block_secondary_market_sale?: boolean;
}

export interface CurrencyAmount {
    amount: number;
    currency_code: string;
}

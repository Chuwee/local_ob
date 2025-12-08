import {
    PromotionDiscountType,
    PromotionType,
    PromotionValidityPeriodType
} from '@admin-clients/cpanel-common-promotions-utility-models';
import { PromotionCollectiveScope } from './promotion-scopes.enum';

export interface BasePromotion {
    id?: number;
    name?: string;
    type?: PromotionType;
    presale?: boolean;
    discount?: {
        currency_code?: string;
        type: PromotionDiscountType;
        value: number;
        ranges?: {
            from: number;
            to: number;
            value: number;
        }[];
    };
    usage_limits?: {
        promotion_max: {
            current?: number;
            enabled: boolean;
            limit?: number;
        };
        session_max?: {
            enabled: boolean;
            limit?: number;
        };
        purchase_max?: { // non Auto
            enabled: boolean;
            limit?: number;
        };
        purchase_min?: { // non Auto
            enabled: boolean;
            limit?: number;
        };
        event_user_collective_max?: {
            enabled: boolean;
            limit?: number;
        };
        session_user_collective_max?: {
            enabled: boolean;
            limit?: number;
        };
        ticket_group_min?: { // non Auto
            enabled: boolean;
            limit?: number;
        };
    };
    applicable_conditions?: { // Only auto
        customer_types_condition?: {
            type: 'ALL' | 'RESTRICTED';
            customer_types?: { id: number; name: string; code: string }[];
        };
        rates_relations_condition?: {
            enabled: boolean;
            rates: { limit: number; rate: number }[];
        };
    };
    validity_period?: {
        type: PromotionValidityPeriodType;
        start_date?: string;
        end_date?: string;
    };
    surcharges?: {
        channel_fees: boolean;
        promoter: boolean;
    };
    show_ticket_price_without_discount?: boolean;
    show_discount_name_ticket?: boolean; // only Auto
    combinable?: boolean; // only Auto
    access_control_restricted?: boolean;
    collective?: BasePromotionCollective;
    block_secondary_market_sale?: boolean;
}

export interface BasePromotionCollective {
    id: number;
    restrictive_sale: boolean;
    box_office_validation: boolean;
    self_managed?: boolean;
    type: PromotionCollectiveScope;
}

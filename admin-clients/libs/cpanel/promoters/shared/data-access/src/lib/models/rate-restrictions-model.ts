
export interface RateRestrictions {
    restrictions: {
        date_restriction_enabled: boolean;
        date_restriction: {
            from: string;
            to: string;
        };
        customer_type_restriction_enabled: boolean;
        customer_type_restriction: number[];
        rate_relations_restriction: {
            locked_tickets_number: number;
            required_tickets_number: number;
            required_rate_ids: number[];
            restricted_price_zone_ids: number[];
            use_all_zone_prices: boolean;
            price_zone_criteria: 'EQUAL' | 'ANY';
            apply_to_b2b: boolean;
        };
        rate_relations_restriction_enabled: boolean;
        period_restriction_enabled: boolean;
        period_restriction: PeriodRestriction[];
        price_type_restriction?: {
            restricted_price_type_ids?: number[];
            apply_to_b2b?: boolean;
        };
        price_type_restriction_enabled?: boolean;
        channel_restriction_enabled?: boolean;
        channel_restriction?: number[];
        max_item_restriction_enabled?: boolean;
        max_item_restriction?: number;
    };
    rate: {
        id: number;
        name: string;
    };
}

export type PeriodRestriction = 'PURCHASE' | 'RENEWAL';

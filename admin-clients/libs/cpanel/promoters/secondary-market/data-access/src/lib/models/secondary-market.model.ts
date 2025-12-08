export type SecondaryMarketPriceType = 'PRICE_WITH_RESTRICTIONS' | 'ORIGINAL_PRICE' | 'UNRESTRICTED' | 'PRORATED';
export type SecondaryMarketConfigType = 'EVENT' | 'SESSION';

interface PriceConfiguration {
    type: SecondaryMarketPriceType;
    restrictions?: {
        min: number;
        max: number;
    };
}

interface CommissionConfiguration {
    percentage: number;
}

export interface SecondaryMarketConfig {
    enabled?: boolean;
    price?: PriceConfiguration;
    commission?: CommissionConfiguration;
    type?: SecondaryMarketConfigType;
    customer_limits_enabled?: boolean;
    customer_limits?: {
        limit: number;
        excluded_customer_types: string[];
    };
    //Exclusive options for Season Tickets
    sale_type?: 'PARTIAL' | 'FULL';
    num_sessions?: number;
    additional_settings?: {
        hide_base_price?: boolean;
        pay_to_balance?: boolean;
    };
}
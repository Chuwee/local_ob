
export interface GetSessionDynamicPricesResponse {
    dynamic_price_zones: GetSessionZoneDynamicPricesResponse[];
}

export interface GetSessionZoneDynamicPricesResponse {
    editable: boolean;
    id_price_zone: number;
    price_zone_name: string;
    active_zone: number;
    available_capacity:	number;
    capacity: number;
    dynamic_prices: {
        name: string;
        capacity?: number;
        valid_date?: string;
        condition_types: SessionZoneDynamicPriceConditionType[];
        status_dynamic_price: 'ACTIVE' | 'PENDING' | 'BLOCKED';
        order: number;
        dynamic_rates_price: {
            id: number;
            name: string;
            price: number;
        }[];
        translations: {
            language: string;
            value: string;
        }[];
    }[];
}

export interface PutSessionDynamicPrices {
    status: boolean;
}

export type SessionZoneDynamicPriceConditionType = 'DATE' | 'CAPACITY';
export type PostSessionZoneDynamicPrices = {
    name: string;
    capacity?: number;
    valid_date?: string;
    condition_types: SessionZoneDynamicPriceConditionType[];
    order: number;
    dynamic_rates_price: {
        id?: number;
        name: string;
        price: number;
    }[];
    translations: {
        language: string;
        value: string;
    }[];
}[];

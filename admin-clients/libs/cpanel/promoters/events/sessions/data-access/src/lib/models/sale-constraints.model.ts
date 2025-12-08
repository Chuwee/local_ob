export type PriceTypeLimit = {
    price_type_id: number;
    price_type_name: string;
    min: number;
    max: number;
};

export type CartLimits = {
    limit: number;
    price_type_limits_enabled: boolean;
    price_type_limits: PriceTypeLimit[];
};

export type CustomerLimitsByPriceZone = {
    price_type_limits: PriceTypeLimit[];
};

export type CustomerLimitsBySession = {
    max: number;
};

export type CustomersLimits = CustomerLimitsByPriceZone | CustomerLimitsBySession;

export type SaleConstraints = {
    cart_limits_enabled?: boolean;
    cart_limits?: CartLimits;
    customers_limits_enabled?: boolean;
    customers_limits?: CustomersLimits;
};

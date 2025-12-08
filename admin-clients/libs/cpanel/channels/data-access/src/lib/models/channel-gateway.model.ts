
export type ChannelGatewayType = 'oneboxAccounting' | 'redsys' | 'cash' | string;

export interface ChannelGateway {
    gateway_sid: ChannelGatewayType;
    configuration_sid: string;
    name: string;
    description?: string;
    active: boolean;
    currencies?: { code: string; name: string }[];
    default: boolean;
    surcharges?: PaymentMethodSurcharge[];
    has_benefits: boolean;
    allow_benefits: boolean;
}

export interface PaymentMethodSurcharge {
    type: PaymentMethodSurchargeType;
    value: number;
    currency: string;
    max_value?: number;
    min_value?: number;
}

export type PaymentMethodSurchargeType = 'NONE' | 'FIXED' | 'PERCENT';

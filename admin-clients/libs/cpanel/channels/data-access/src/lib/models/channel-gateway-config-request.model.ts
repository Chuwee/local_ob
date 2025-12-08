import { PaymentMethodSurcharge } from './channel-gateway.model';

export interface ChannelGatewayConfigRequest {
    name?: string;
    translations?: {
        name?: Record<string, string>;
        subtitle?: Record<string, string>;
    };
    description?: string;
    send_additional_data?: boolean;
    live?: boolean;
    refund?: boolean;
    allow_benefits?: boolean;
    show_billing_form?: boolean;
    save_card_by_default?: boolean;
    force_risk_evaluation?: boolean;
    attempts?: number;
    price_range_enabled?: boolean;
    price_range?: { min?: number; max?: number };
    field_values?: Record<string, unknown>;
    currency_codes?: string[];
    surcharges?: PaymentMethodSurcharge[];
    taxes?: { id: number }[];
}

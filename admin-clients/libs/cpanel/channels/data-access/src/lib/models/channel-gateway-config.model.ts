import { ChannelGateway, PaymentMethodSurcharge } from './channel-gateway.model';

export interface ChannelGatewayConfig extends ChannelGateway {
    translations: {
        name?: Record<string, string>;
        subtitle?: Record<string, string>;
    };
    live: boolean;
    refund: boolean;
    allow_benefits: boolean;
    show_billing_form: boolean;
    save_card_by_default: boolean;
    force_risk_evaluation: boolean;
    attempts: number;
    send_additional_data: boolean;
    field_values?: Record<string, string>;
    price_range_enabled?: boolean;
    price_range?: {
        min: number;
        max: number;
    };
    //TODO(MULTICURRENCY): make it a must when the multicurrency functionality is finished
    currencies?: { code: string; name: string }[];
    surcharges?: PaymentMethodSurcharge[];
    taxes?: { id: number }[];
}

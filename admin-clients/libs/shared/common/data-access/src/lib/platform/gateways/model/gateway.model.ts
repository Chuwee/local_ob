export interface Gateway {
    sid: string;
    name: string;
    retry: boolean;
    max_attempts: number;
    refund: boolean;
    retry_refund: boolean;
    allow_benefits: boolean;
    send_additional_data: boolean;
    price_range_enabled?: boolean;
    show_billing_form?: boolean;
    save_card_by_default?: boolean;
    force_risk_evaluation?: boolean;
    live: boolean;
    fields: string[];
    wallet: boolean;
}

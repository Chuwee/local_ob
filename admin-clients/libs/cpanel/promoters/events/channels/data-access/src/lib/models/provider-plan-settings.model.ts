export interface ProviderPlanSettings {
    // General Sync Settings
    sync_sessions_as_hidden: boolean;
    sync_prices: boolean;
    sync_surcharges: boolean;
    round_prices_up: boolean;
    sync_session_labels: boolean;
    sync_session_pics: boolean;
    sync_session_key_dates: boolean;
    sync_session_type_ordering: boolean;
    sync_hidden_status: boolean;
    sync_session_type_details: boolean;
    sync_billing_terms: boolean;
    sync_allowed_num_tickets: boolean;
    sync_instructions: boolean;

    // Cancellation
    enforce_cancellation: boolean;
    price_modifier: number;
    session_price_comes_with_taxes: boolean;

    // Real-Time & Availability
    enable_real_time: boolean;
    sync_available_tickets: boolean;
    sync_sold_out_status: boolean;

    // Session Types
    sync_session_type: boolean;
    use_real_time_api: boolean;
    channels_to_autopublish_session_types: number[];
}

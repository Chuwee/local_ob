export interface ProviderPlanSettings {
    // General Sync Settings
    sync_sessions_as_hidden: boolean;
    sync_prices: boolean;
    sync_surcharges: boolean;
    round_prices_up: boolean;
    sync_session_labels: boolean;
    sync_session_pics: boolean;
    sync_session_type_ordering: boolean;
    sync_hidden_status: boolean;
    sync_session_type_details: boolean;

    // Main Plan
    sync_main_plan_title: boolean;
    sync_main_plan_description: boolean;
    sync_main_plan_images: boolean;
}

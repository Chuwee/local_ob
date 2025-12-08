import { ProviderPlanSettings } from './provider-plan-settings.model';

export interface UpdateEventChannelsRequest {
    settings: {
        use_event_dates: boolean;
        release: {
            enabled: boolean;
            date: string;
        };
        sale: {
            enabled: boolean;
            start_date: string;
            end_date: string;
        };
    };
    use_all_quotas: boolean;
    quotas: number[];
    provider_plan_settings?: ProviderPlanSettings;
}

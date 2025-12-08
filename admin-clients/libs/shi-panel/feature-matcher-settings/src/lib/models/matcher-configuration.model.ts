export interface MatcherConfiguration {
    supplier: string;
    countries: string[];
    register_matchings: boolean;
    enabled: boolean;
    send_report: boolean;
    excluded_states: string[];
    excluded_taxonomies: string[];
    keywords: string[];
    delivery: MatcherConfigurationDelivery;
    target_emails: string;
}

export interface MatcherConfigurationDelivery {
    enabled: boolean;
    recipients: string[];
}

export interface PutMatcherConfigurationRequest {
    countries?: string[];
    register_matchings?: boolean;
    enabled?: boolean;
    excluded_states?: string[];
    excluded_taxonomies?: string[];
    keywords?: string[];
    delivery?: MatcherConfigurationDelivery;
}

export type RuleType = 'STARTS_WITH' | 'ENDS_WITH' | 'CONTAINS' | 'IS';

export type ChannelsAllowed = 'ALL' | 'RESTRICTED';

export type DomainSettingsMode = 'DEFAULT' | 'REDIRECT';

export interface DomainSettings {
    enabled: boolean;
    mode: DomainSettingsMode;
    domains: DomainSettingsEntry[];
}

export type DomainSettingsEntry = {
    domain: string;
    default: boolean;
};

export interface DomainConfiguration {
    social_login: {
        google_client_id: string;
    };
    domain_fallback: FallbackConfig;
}

export interface FallbackConfig {
    enabled: boolean;
    channels_allowed?: ChannelsAllowed;
    rules?: FallbackRule[];
    default_redirection_url?: string;
}
export interface FallbackRule {
    type: RuleType;
    values: string[];
}


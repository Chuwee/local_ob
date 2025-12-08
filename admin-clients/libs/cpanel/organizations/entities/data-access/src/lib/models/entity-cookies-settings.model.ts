export interface EntityCookiesSettings extends EntityCookiesSettingsReduced {
    history?: EntityCookiesSettingsReduced[];
}

export interface EntityCookiesSettingsReduced {
    enable_custom_integration?: boolean;
    accept_integration_conditions?: boolean;
    channel_enabling_mode?: EntityCookiesScope;
    custom_integration_channels?: number[];
    date?: string;
}

export enum EntityCookiesType {
    onebox = 'ONEBOX',
    customIntegration = 'CUSTOM_INTEGRATION'
}

export enum EntityCookiesScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface EntitySecuritySettings {
    entity_id: number;
    password_config: EntitySecurityPasswordConfig;
}

export interface EntitySecurityPasswordConfig {
    max_retries: number;
    expiration: EntitySecurityPasswordExpiration;
    storage: EntitySecurityPasswordStorage;
}

export interface EntitySecurityPasswordExpiration {
    enabled: boolean;
    type: EntitySecurityPasswordExpirationType;
    amount: number;
}

export interface EntitySecurityPasswordStorage {
    enabled: boolean;
    amount: number;
}

export type EntitySecurityPasswordExpirationType = 'DAYS' | 'MONTHS';


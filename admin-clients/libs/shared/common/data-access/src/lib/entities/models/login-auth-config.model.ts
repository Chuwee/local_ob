export type LoginAuthConfig = {
    enabled: boolean;
    use_entity_config?: boolean; //TODO: Això no hauria d'arribar en entity però sí en canal
    max_members: {
        enabled: boolean;
        limit: number;
    };
    authenticators: LoginAuthMethod[];
    allowed_customer_types?: number[];
    settings?: LoginPortalSettings;
};

export type PhoneValidator = {
    enabled: boolean;
    validator_id: string;
    available_validators?: string[];
};

export type LoginAuthMethod = {
    type: 'DEFAULT' | 'COLLECTIVE' | 'VENDOR';
    id?: string;
    customer_creation?: CustomerCreation;
    properties?: { [key: string]: string };
};

export type LoginPortalSettings = {
    mode: 'REQUIRED' | 'NON_REQUIRED';
    customer_types?: string[];
    triggers_on?: LoginPlacement[];
    user_data_editable?: boolean;
    account_creation: AccountCreation;
    blocked_customer_types_enabled?: boolean;
    blocked_customer_types?: string[];
    counter_mode?: 'DEFAULT' | 'AUTOINCREMENT';
    counter_id?: number;
    phone_validator?: PhoneValidator;
};

export type LoginPlacement = 'IMMEDIATELY' | 'BEFORE_CHECKOUT' | 'BEFORE_SELECT_LOCATION';
export type CustomerCreation = 'ENABLED' | 'DISABLED';
export type AccountCreation = 'ENABLED' | 'DISABLED';
export const socialLogins = ['GOOGLE']; // ir añadiendo
export const vendorsWithProviders = ['FEVER'];

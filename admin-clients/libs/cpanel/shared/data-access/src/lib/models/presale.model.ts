import { CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';

export interface Presale {
    id: string;
    status: string;
    name: string;
    active: boolean;
    session_id?: number;
    presale_period: {
        type: ValidationRangeTypes;
        start_date: string;
        end_date: string;
    };
    channels: {
        id: number;
        name: string;
        selected: boolean;
    }[];
    validator?: {
        id: number;
        name: string;
        validation_method: CollectiveValidationMethod;
    };
    customer_types: {
        id: number;
        name: string;
        selected: boolean;
    }[];
    loyalty_program?: {
        enabled: boolean;
        points?: number;
    };
    validator_type?: ValidatorTypes;
    member_tickets_limit: number;
    member_tickets_limit_enabled: boolean;
    general_tickets_limit: number;
    settings?: {
        multiple_purchase?: boolean;
    };
}

export interface PresalePut {
    name?: string;
    active?: boolean;
    presale_period?: {
        type: ValidationRangeTypes;
        start_date?: string;
        end_date?: string;
    };
    channels?: number[];
    member_tickets_limit?: number;
    member_tickets_limit_enabled?: boolean;
    general_tickets_limit?: number;
    customer_types?: number[];
    loyalty_program?: {
        enabled: boolean;
        points?: number;
    };
    settings?: {
        multiple_purchase?: boolean;
    };
}

export interface PresalePost {
    name: string;
    validator_id?: number;
    validator_type?: ValidatorTypes;
    additional_config?: ExternalProviderPresalesPost;
}

export interface ExternalProviderPresalesPost {
    inventory_provider: ExternalInventoryProviders;
    inventory_id?: string;
    external_presale_id: string;
    entity_id: number;
}

export enum ValidatorTypes {
    collective = 'COLLECTIVE',
    customers = 'CUSTOMERS'
}

export enum ValidationRangeTypes {
    all = 'ALL',
    dateRange = 'DATE_RANGE'
}

export interface SettingsLanguages {
    default: string;
    selected: string[];
}

export type PresalesRedirectionPolicy = {
    mode: RedirectionPolicyMode;
    value?: Record<string, string>;
};

export const redirectionPolicyMode = ['CATALOG', 'CUSTOM'] as const;
export type RedirectionPolicyMode = typeof redirectionPolicyMode[number];

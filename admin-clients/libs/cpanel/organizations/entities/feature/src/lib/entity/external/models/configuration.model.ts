export enum HttpProtocol {
    http = 'HTTP',
    https = 'HTTPS'
}

export enum WSConnectionVersion {
    oneDotX = 'ONE_DOT_X',
    twoDotFour = 'TWO_DOT_FOUR',
    twoDotFourLegacy = 'TWO_DOT_FOUR_LEGACY'
}

export enum Environment {
    pre = 'PRE',
    pro = 'PRO'
}

export enum PartnerValidationType {
    person = 'PERSON',
    partner = 'PARTNER'
}

export enum CapacityNameType {
    shortName = 'SHORT_NAME',
    fullName = 'FULL_NAME'
}

import { ProviderPlanSettings } from './provider-plan-settings.model';

export interface ExternalEntityConfiguration {
    club_code: string;
    entity_id: number;
    avet_ws_environment?: Environment;
    avet_connection_type: 'SOCKET' | 'WEBSERVICES' | 'APIM';
    provider_plan_settings?: ProviderPlanSettings;
    ticketing?: {
        connection?: {
            name?: string;
            username?: string;
            password?: string;
            protocol?: HttpProtocol;
            ip?: string;
            port?: number;
            ping_requests_blocked?: boolean;
            ws_connection_version?: WSConnectionVersion;
        };
        capacity?: {
            capacity_name_type?: CapacityNameType;
            capacities?: number[];
            season?: string;
            members_capacity_id?: number;
        };
        operative?: {
            partner_validation_type?: PartnerValidationType;
            payment_method?: number;
            generate_partner_ticket?: boolean;
            scheduled?: boolean;
            fixed_delay_ms?: number;
            check_partner_grant?: boolean;
            partner_grant_capacities?: number;
            check_partner_pin_regexp?: boolean;
            partner_pin_regexp?: string;
            send_id_number?: boolean;
            id_number_max_length?: number;
            digital_ticket_mode?: string;
        };
    };
    members?: {
        enabled?: boolean;
        connection?: {
            ip?: string;
            port?: 0;
            protocol?: HttpProtocol;
            username?: string;
            password?: string;
            ws_connection_version?: WSConnectionVersion;
        };
    };
    smart_booking?: {
        enabled?: boolean;
        connection?: {
            url?: string;
            credentials?: {
                username?: string;
                password?: string;
            };
        };
    };
    sga?: {
        enabled?: boolean;
        connection?: {
            url?: string;
            auth_url?: string;
            client_id?: string;
            profile?: string;
            scope?: string;
            sales_channel_id?: string;
            credentials?: {
                username?: string;
                password?: string;
            };
        };
    };
    inventory_providers?: [string];
}

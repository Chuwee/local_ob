import { Country, CustomManagementType, EntityType, Region } from '@admin-clients/shared/common/data-access';
import { BasicUser } from '@admin-clients/shared/data-access/models';
import { Currency } from '@admin-clients/shared-utility-models';
import { CustomResources } from './custom-resources.model';
import { MstrUrls } from './reports-config.model';

export interface User extends BasicUser {
    status: UserStatus;
    entity: {
        id: number;
        name: string;
        short_name: string;
        settings: {
            types: EntityType[];
            // eslint-disable-next-line @typescript-eslint/naming-convention
            enable_B2B: boolean;
            // eslint-disable-next-line @typescript-eslint/naming-convention
            allow_B2B_publishing?: boolean;
            allow_avet_integration: boolean;
            allow_digital_season_ticket: boolean;
            allow_members: boolean;
            allow_activity_events: boolean;
            allow_loyalty_points?: boolean;
            languages: {
                default: string;
                available: string[];
            };
            external_integration: {
                custom_managements: {
                    enabled: boolean;
                    type: CustomManagementType;
                }[];
            };
            interactive_venue: {
                enabled: boolean;
            };
            notifications?: {
                email?: {
                    send_limit: number;
                    enabled: boolean;
                };
            };
            customization: CustomResources;
        };
    };
    operator: {
        id: number;
        name: string;
        currencies?: {
            selected: Currency[];
            default_currency: string;
        };
        currency?: Currency;
        settings: {
            customization: CustomResources;
        };
        allow_fever_zone?: boolean;
        allow_gateway_benefits?: boolean;
    };
    job_title: string;
    language: string;
    timezone: string;
    notes: string;
    contact: {
        primary_phone: string;
        secondary_phone: string;
        fax: string;
    };
    location: {
        address: string;
        city: string;
        postal_code: string;
        country: Country;
        country_subdivision: Region;
    };
    roles: {
        code: string;
        permissions: string[];
    }[];
    reports?: MstrUrls;
    use_multicurrency: boolean;
}

export enum UserStatus {
    active = 'ACTIVE',
    pending = 'PENDING',
    blocked = 'BLOCKED',
    temporaryBlocked = 'TEMPORARY_BLOCKED'
}

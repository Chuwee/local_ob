import { Country, Region } from '@admin-clients/shared/common/data-access';
import { EntityUserMfaTypes } from './entity-user-mfa-types.enum';
import { EntityUserStatus } from './entity-user-status.model';

export interface EntityUser {
    id: number;
    apikey: string;
    username: string;
    email: string;
    name: string;
    last_name: string;
    status: EntityUserStatus;
    mfa_type: EntityUserMfaTypes;
    entity: {
        id: number;
        name: string;
    };
    producer?: {
        id: number;
        name: string;
    };
    operator: {
        id: number;
        name: string;
    };
    job_title: string;
    language: string;
    notes: string;
    contact: {
        primary_phone: string;
        secondary_phone: string;
        fax: string;
    };
    location: {
        address?: string;
        city?: string;
        postal_code?: string;
        country?: Country;
        country_subdivision?: Region;
    };
    roles: [
        {
            code: string;
            permissions: [string];
        }
    ];
}

import { Country, Region } from '@admin-clients/shared/common/data-access';
import { EntityUserStatus } from './entity-user-status.model';

export interface PutEntityUser {
    entity_id: number;
    name: string;
    last_name: string;
    job_title?: string;
    status: EntityUserStatus;
    language?: string;
    notes?: string;
    contact?: {
        primary_phone?: string;
        secondary_phone?: string;
        fax?: string;
    };
    location?: {
        address?: string;
        city?: string;
        postal_code?: string;
        country?: Pick<Country, 'code'>;
        country_subdivision?: Pick<Region, 'code'>;
    };
}

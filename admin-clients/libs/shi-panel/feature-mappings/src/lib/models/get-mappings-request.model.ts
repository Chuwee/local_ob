import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { MappingCategories } from './mapping-categories.enum';
import { MappingStatus } from './mapping-status.enum';

export interface GetMappingsRequest extends PageableFilter {
    shi_id?: string;
    supplier_id?: string;
    status?: MappingStatus[];
    supplier?: number[];
    category?: MappingCategories[];
    country_code?: string[];
    taxonomies?: string[];
    event_date_from?: string;
    event_date_to?: string;
    create_date_from?: string;
    create_date_to?: string;
    update_date_from?: string;
    update_date_to?: string;
    favorite?: boolean;
}

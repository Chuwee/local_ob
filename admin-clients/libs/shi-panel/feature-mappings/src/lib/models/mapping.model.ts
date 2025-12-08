import { IdName } from '@admin-clients/shared/data-access/models';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { MappingCategories } from './mapping-categories.enum';
import { MappingStatus } from './mapping-status.enum';

export interface Mapping {
    id?: string; //necessary to make status select work
    code: string;
    shi_id: number;
    supplier_id: string;
    supplier: SupplierName;
    status: MappingStatus;
    name: string;
    created: Date;
    updated: Date;
    country_code?: string;
    category?: MappingCategories;
    taxonomies?: IdName[];
    favorite?: boolean;
}

export interface MappingUpdate {
    code: string;
    favorite: boolean;
}

export interface PutMappingsRequest {
    mappings: MappingUpdate[];
}

export interface MappingToClean {
    shi_id: string;
}

export interface MappingToCreate {
    shi_id: number,
    supplier_id: string,
    supplier: SupplierName,
    favorite: string
}


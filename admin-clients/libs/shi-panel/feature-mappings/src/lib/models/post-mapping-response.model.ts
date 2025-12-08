import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { MappingCategories } from './mapping-categories.enum';
import { MappingStatus } from './mapping-status.enum';

export interface PostMappingResponse {
    code: string;
    shi_id: number;
    supplier_id: string;
    supplier: SupplierName;
    status: MappingStatus;
    name: string;
    created: Date;
    category: MappingCategories;
}

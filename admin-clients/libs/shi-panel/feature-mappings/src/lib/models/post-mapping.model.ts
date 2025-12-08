import { SupplierName } from '@admin-clients/shi-panel/utility-models';

export interface PostMapping {
    shi_id: number;
    supplier: SupplierName;
    supplier_id: number;
    favorite: boolean;
}

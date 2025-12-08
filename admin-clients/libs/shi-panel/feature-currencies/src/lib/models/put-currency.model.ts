import { SupplierName } from '@admin-clients/shi-panel/utility-models';

export interface PutCurrency {
    supplier: SupplierName;
    source: string;
    target: string;
    rate: number;
}

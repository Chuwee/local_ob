import { SupplierName } from '@admin-clients/shi-panel/utility-models';

export interface Currency {
    supplier: SupplierName;
    source: string;
    target: string;
    rate: number;
    last_update: Date;
    modifier: string;
    has_transitions: boolean;
}

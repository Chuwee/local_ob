import { TaxDataType } from './tax-data-type.enum';

export interface TaxesData {
    type: TaxDataType;
    producer_id: number;
    invoice_prefix_id: number;
}

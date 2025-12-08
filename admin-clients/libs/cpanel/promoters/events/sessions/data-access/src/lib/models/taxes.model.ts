import { TaxesData } from './taxes-data.model';

export interface Taxes {
    data?: TaxesData;
    ticket?: {
        id?: number;
        name?: string;
    };
    charges?: {
        id?: number;
        name?: string;
    };
}

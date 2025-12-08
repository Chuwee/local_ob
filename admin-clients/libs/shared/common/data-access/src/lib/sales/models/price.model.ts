import { PriceB2B } from './price-b2b.model';
import { PriceCharges } from './price-charges.model';
import { PriceSales } from './price-sales.model';

export interface Price {
    base: number;
    delivery: number;
    insurance: number;
    donation?: number;
    gateway?: number;
    sales: PriceSales;
    charges: PriceCharges;
    total_charges?: number;
    b2b: PriceB2B;
    channel_commision: number;
    final: number;
    currency: string;
    reallocation_refund?: number;
}
export interface PriceTotalTaxes {
    charges?: number;
    items?: number;
}

export interface PricePartialTaxes {
    charges?: {
        total?: number;
    };
    item?: {
        total?: number;
    };
}

import { ProductVariantStatus } from './product-variant-status.model';

export interface PutProductVariant {
    price?: number;
    sku?: string;
    stock?: number;
    status?: ProductVariantStatus;
}
export interface PutProductVariantPrices {
    price: number;
    variants: number[];
}

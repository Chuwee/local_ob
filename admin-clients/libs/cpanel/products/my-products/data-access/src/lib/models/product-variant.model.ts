import { ProductVariantStatus } from './product-variant-status.model';

export interface ProductVariant {
    id: number;
    product: {
        id: number;
        name: string;
    };
    name: string;
    sku: string;
    stock: number;
    price: number;
    status: ProductVariantStatus;
}

export type ProductVariantsDialogAction = 'CREATE' | 'EDIT';

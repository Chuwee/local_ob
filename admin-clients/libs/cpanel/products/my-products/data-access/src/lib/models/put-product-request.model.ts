import { Category, EntityCategory } from '@admin-clients/shared/common/data-access';
import { ProductStatus } from './product-status.model';
import { ProductTaxesMode } from './product-taxes-mode.model';

export interface PutProductRequest {
    tax_id: number;
    name: string;
    product_state: ProductStatus;
    currency_code: string;
    ui_settings?: {
        hide_delivery_date_time?: boolean;
        hide_delivery_point?: boolean;
    };
    settings?: {
        categories?: {
            base?: {
                id?: number;
                code?: string;
                description?: string;
            };
            custom?: Category | EntityCategory;
        };
        tax_mode: ProductTaxesMode;
    };
}

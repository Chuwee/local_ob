import { ProductStatus } from './product-status.model';
import { ProductStockType } from './product-stock-type.model';
import { ProductTaxesMode } from './product-taxes-mode.model';
import { ProductType } from './product-type.model';

export interface Product {
    product_id: number;
    entity: {
        id: number;
        name: string;
    };
    producer: {
        id: number;
        name: string;
    };
    tax: {
        id: number;
        name: string;
    };
    surcharge_tax: {
        id: number;
        name: string;
    };
    name: string;
    product_type: ProductType;
    stock_type: ProductStockType;
    product_state: ProductStatus;
    currency_code: string;
    create_date: string;
    update_date: string;
    ui_settings?: {
        hide_delivery_date_time?: boolean;
        hide_delivery_point?: boolean;
    };
    settings: {
        categories: {
            base: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
            custom: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
        };
        tax_mode: ProductTaxesMode;
    };
    has_sales: boolean;
}

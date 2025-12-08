import { ProductStockType } from './product-stock-type.model';
import { ProductType } from './product-type.model';

export interface PostProduct {
    name: string;
    entity_id: number;
    producer_id: number;
    stock_type: ProductStockType;
    product_type: ProductType;
    currency_code?: string;
}

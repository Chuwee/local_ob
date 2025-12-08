import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ProductStockType } from './product-stock-type.model';
import { ProductType } from './product-type.model';

export class GetProductsRequest implements PageableFilter {

    limit: number;
    offset?: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    q?: string;  // Wildcard filter
    entityId?: number | null;
    status?: string;
    type?: ProductType;
    stock?: ProductStockType;
    name?: string;
    currency?: string;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
    }
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';

export class GetProductsDeliveryPointsRequest implements PageableFilter {

    limit: number;
    offset: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    q?: string;  // Wildcard filter
    entityId?: number | null;
    country?: string;
    countrySubdivision?: string;
    status: string;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
    }
}

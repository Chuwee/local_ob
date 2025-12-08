import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ProducerStatus } from './producer-status.model';
import { ProducersFilterFields } from './producers-filter-fields.model';

export class GetProducersRequest implements PageableFilter {
    limit: number;
    offset?: number;
    sort?: string;
    q?: string;  // Wildcard filter
    entityId?: number;
    status?: ProducerStatus[];
    fields?: ProducersFilterFields[];

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
        this.entityId = null;
        this.status = null;
        this.fields = null;
    }
}

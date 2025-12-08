import { PageableFilter } from '@admin-clients/shared/data-access/models';

export class GetPacksRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string;
    q?: string;
    entity_id?: number | null;
    status?: 'ACTIVE' | 'INACTIVE';
    eventId?: number;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
    }
}

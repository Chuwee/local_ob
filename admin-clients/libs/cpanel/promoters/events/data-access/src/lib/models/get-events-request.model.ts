import { EventType } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { EventFilterFields } from './event-filter-fields.enum';
import { EventStatus } from './event-status.enum';

export class GetEventsRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    q?: string;  // Wildcard filter
    fields?: EventFilterFields[] | null;
    entityId?: number | null;
    status?: EventStatus[] | null;
    type?: EventType | null;
    startDate?: string;
    endDate?: string;
    producerId?: number | null;
    venueId?: number | null;
    country?: string;
    city?: string;
    includeArchived?: boolean;
    currency?: string;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
        this.status = [];
    }
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SeasonTicketStatus } from './season-ticket-status.model';

export class GetSeasonTicketsRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string;
    q?: string;  // Wildcard filter
    entityId?: number | null;
    producerId?: number | null;
    venueId?: number | null;
    currency?: string;
    status?: SeasonTicketStatus[] | null;

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
        this.entityId = null;
        this.producerId = null;
        this.venueId = null;
        this.status = [];
    }
}

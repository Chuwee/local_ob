import { WebhookStatus } from '@admin-clients/cpanel/shared/feature/webhook';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export class GetWebhooksRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    entityId?: number;
    operatorId?: number;
    status?: WebhookStatus[];

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.entityId = null;
        this.operatorId = null;
        this.status = null;
    }
}

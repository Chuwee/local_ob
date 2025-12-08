import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SalesRequestsEventStatus } from './sales-requests-event-status.model';
import { SalesRequestsStatus } from './sales-requests-status.model';

export interface GetSalesRequestsRequest extends PageableFilter {
    status?: SalesRequestsStatus[] | null;
    event_status?: SalesRequestsEventStatus[];
    channelEntity?: number;
    eventEntity?: number;
    channel?: number;
    startDate?: string;
    endDate?: string;
    include_third_party_entity_events?: boolean;
    fields?: string[];
    currencyCode?: string;
}

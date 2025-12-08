import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetInvoicingEntityEventsRequest extends PageableFilter {
    status?: EventStatus[] | null;
}

import { SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Weekdays } from '@admin-clients/shared-utility-models';

export interface GetSaleRequestSessionsRequest extends PageableFilter {
    saleRequestId?: string;
    status?: SessionStatus[];
    start_date?: {
        from?: string;
        to?: string;
    };
    weekdays?: Weekdays[];
    fields?: string[];
}

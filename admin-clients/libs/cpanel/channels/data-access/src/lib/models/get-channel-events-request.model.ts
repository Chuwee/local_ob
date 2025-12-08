import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetChannelEventsRequest extends PageableFilter {
    on_sale?: boolean;
    published?: boolean;
}

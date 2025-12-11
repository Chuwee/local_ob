import { PageableFilter } from './common-types';

export interface GetChannelEventsRequest extends PageableFilter {
    on_sale?: boolean;
    published?: boolean;
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetBlacklistedMatchingsRequest extends PageableFilter {
    date?: string;
    date_end?: string;
    country_code?: string[];
}

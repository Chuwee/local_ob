import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetChannelBlacklistRequest extends PageableFilter {
    startDate?: string; // Example: 2021-01-01T08:00:00Z
    endDate?: string; // Example: 2021-01-01T08:00:00Z
}

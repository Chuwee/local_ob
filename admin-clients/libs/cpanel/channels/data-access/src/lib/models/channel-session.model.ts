import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Weekdays } from '@admin-clients/shared-utility-models';

export interface ChannelSession {
    id: number;
    name: string;
    startDate: string;
}

export interface ChannelSessionsFilter extends PageableFilter {
    initStartDate?: string;
    finalStartDate?: string;
    weekdays?: Weekdays[];
    timezone?: string;
}

export interface GetChannelSessionsResponse extends ListResponse<ChannelSession> { }


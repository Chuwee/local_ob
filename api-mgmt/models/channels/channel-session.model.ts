import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from './common-types';
import { Weekdays } from './common-types';

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


import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { SessionStatus } from './session-status.enum';
import { SessionType } from './session-type.enum';
import { SessionsFilterFields } from './sessions-filter-fields.enum';

export interface GetSessionsRequest extends PageableFilter {
    filterByIds?: number[];
    status?: SessionStatus[];
    type?: SessionType | SessionType[];
    venueTplId?: number;
    initStartDate?: string;
    finalStartDate?: string;
    initEndDate?: string;
    finalEndDate?: string;
    fields?: SessionsFilterFields[];
    weekdays?: Weekdays[];
    timezone?: string;
    hourRanges?: string[];
}

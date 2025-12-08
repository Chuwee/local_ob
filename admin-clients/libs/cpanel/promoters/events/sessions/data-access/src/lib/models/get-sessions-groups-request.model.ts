import { ListFilter } from '@admin-clients/shared/data-access/models';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { SessionGroupType } from './session-group-type.enum';
import { SessionStatus } from './session-status.enum';
import { SessionType } from './session-type.enum';

export interface GetSessionsGroupsRequest extends ListFilter {
    filterByIds?: number[];
    status?: SessionStatus[];
    type?: SessionType | SessionType[];
    venueTplId?: number;
    initStartDate?: string;
    finalStartDate?: string;
    initEndDate?: string;
    finalEndDate?: string;
    weekdays?: Weekdays[];
    timezone?: string; // Europe/Brussels
    hourRanges?: string[];
    groupType: SessionGroupType;
}

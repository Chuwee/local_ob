import { Weekdays } from '@admin-clients/shared-utility-models';
import { SessionGroupType } from './session-group-type.enum';
import { SessionStatus } from './session-status.enum';

export interface SessionListFilters {
    venueTplId?: number;
    status?: SessionStatus[];
    initStartDate?: string;
    finalStartDate?: string;
    weekdays?: Weekdays[];
    timezone?: string; // needed when applying weekdays filter
    hourRanges?: string[];
    groupType: SessionGroupType;
}

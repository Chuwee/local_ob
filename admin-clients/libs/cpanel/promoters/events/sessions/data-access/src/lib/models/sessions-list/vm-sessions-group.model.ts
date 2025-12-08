import { SessionGroupStatus } from '../session-group-status.enum';
import { SessionWrapper } from '../session-wrapper.model';

export interface VmSessionsGroup {
    startDate: string;
    endDate: string;
    title: string;
    totalSessions: number;
    selectedSessions: number;
    sessions: SessionWrapper[];
    status?: SessionGroupStatus;
}

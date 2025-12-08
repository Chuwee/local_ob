import {
    SeasonTicketAssignableSession,
    SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { ReleaseDataSession, TransferDataSession } from '@admin-clients/shared/common/data-access';

export interface VmCustomerSession {
    status: SeasonTicketSessionStatus;
    session_id: number;
    session_name: string;
    event_id: number;
    event_name: string;
    session_assignable: SeasonTicketAssignableSession;
    session_starting_date: string;
    transferSession?: TransferDataSession;
    releaseSession?: ReleaseDataSession;
}

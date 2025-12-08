import {
    SeasonTicketSession, SeasonTicketUnAssignSessionReason,
    SeasonTicketValidateOrAssignSessionReason
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';

export interface VmSeasonTicketSession extends SeasonTicketSession {
    is_session_valid?: boolean;
    is_session_not_valid?: boolean;
    session_not_valid_reason?: SeasonTicketValidateOrAssignSessionReason;
    sessions_not_unassigned_reason?: SeasonTicketUnAssignSessionReason;
    is_session_validated?: boolean;
    is_process_session_assignment_done?: boolean;
    is_session_assignment_to_be_unassigned?: boolean;
    is_process_session_unassignment_done?: boolean;
    is_selected?: boolean;
    is_session_row_selectable?: boolean;
    is_validation_in_progress?: boolean;
    is_assignment_in_progress?: boolean;
    is_unassignment_in_progress?: boolean;
}

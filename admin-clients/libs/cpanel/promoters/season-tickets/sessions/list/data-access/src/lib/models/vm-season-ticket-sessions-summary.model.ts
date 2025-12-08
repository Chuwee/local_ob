import { SeasonTicketSessionsSummary } from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';

export interface VmSeasonTicketSessionsSummary extends SeasonTicketSessionsSummary{
    valid_sessions?: number;
}

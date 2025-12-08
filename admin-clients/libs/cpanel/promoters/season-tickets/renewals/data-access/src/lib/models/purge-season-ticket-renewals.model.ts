import { SeasonTicketRenewalMappingStatus } from './season-ticket-renewal-mapping-status.enum';
import { SeasonTicketRenewalStatus } from './season-ticket-renewal-status.enum';

export interface PurgeSeasonTicketsRenewalsRequest {
    q?: string;
    endDate?: string;
    mapping_status?: SeasonTicketRenewalMappingStatus;
    renewal_status?: SeasonTicketRenewalStatus;
    startDate?: string;
}

export interface GetDeletableSeasonTicketRenewalsNumberResponse {
    deletable_renewals: number;
}

import { ListResponse } from '@OneboxTM/utils-state';
import { SeasonTicketRenewal } from './season-ticket-renewal.model';
import { SeasonTicketRenewalsSummary } from './season-ticket-renewals-summary.model';

export interface GetSeasonTicketRenewalsResponse extends ListResponse<SeasonTicketRenewal> {
    summary: SeasonTicketRenewalsSummary;
}

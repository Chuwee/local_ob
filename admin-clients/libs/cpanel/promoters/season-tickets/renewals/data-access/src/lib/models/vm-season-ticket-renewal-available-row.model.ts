import { SeasonTicketRenewalCapacityTreeRow } from './season-ticket-renewal-capacity-tree-row.model';

export interface VmSeasonTicketRenewalAvailableRow extends SeasonTicketRenewalCapacityTreeRow {
    availableSeatsCalc?;
    assignedTo?: Set<string>;
    alreadyMappedTo?: Set<string>;
}

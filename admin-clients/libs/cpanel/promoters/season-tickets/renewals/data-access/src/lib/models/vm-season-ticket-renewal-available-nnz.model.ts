import { SeasonTicketRenewalCapacityTreeNnz } from './season-ticket-renewal-capacity-tree-nnz.model';

export interface VmSeasonTicketRenewalAvailableNnz extends SeasonTicketRenewalCapacityTreeNnz {
    availableSeatsCalc?;
    assignedTo?: Set<string>;
    alreadyMappedTo?: Set<string>;
}

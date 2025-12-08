import { SeasonTicketRenewalCapacityTreeSector } from './season-ticket-renewal-capacity-tree-sector.model';

export interface VmSeasonTicketRenewalAvailableSector extends SeasonTicketRenewalCapacityTreeSector {
    availableSeatsCalc?;
    assignedTo?: Set<string>;
    alreadyMappedTo?: Set<string>;
}

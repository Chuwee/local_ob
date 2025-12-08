import { SeasonTicketRenewalCapacityTreeNnz } from './season-ticket-renewal-capacity-tree-nnz.model';
import { SeasonTicketRenewalCapacityTreeRow } from './season-ticket-renewal-capacity-tree-row.model';

export interface SeasonTicketRenewalCapacityTreeSector {
    sector_id: number;
    sector_name: string;
    available_seats: number;
    rows?: SeasonTicketRenewalCapacityTreeRow[];
    not_numbered_zones?: SeasonTicketRenewalCapacityTreeNnz[];
}

import { VmSeasonTicketRenewalAvailableNnz } from './vm-season-ticket-renewal-available-nnz.model';
import { VmSeasonTicketRenewalAvailableRow } from './vm-season-ticket-renewal-available-row.model';
import { VmSeasonTicketRenewalAvailableSeat } from './vm-season-ticket-renewal-available-seat.model';
import { VmSeasonTicketRenewalAvailableSector } from './vm-season-ticket-renewal-available-sector.model';
import { VmSeasonTicketRenewalEdit } from './vm-season-ticket-renewal-edit.model';

export interface MappedInfo {
    sector: VmSeasonTicketRenewalAvailableSector;
    row?: VmSeasonTicketRenewalAvailableRow;
    nnz?: VmSeasonTicketRenewalAvailableNnz;
    renewal: VmSeasonTicketRenewalEdit;
    renewalIndex: number;
    seat: VmSeasonTicketRenewalAvailableSeat;
}

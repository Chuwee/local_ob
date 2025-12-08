import { Observable } from 'rxjs';
import { SeasonTicketRenewal } from './season-ticket-renewal.model';
import { VmSeasonTicketRenewalAvailableNnz } from './vm-season-ticket-renewal-available-nnz.model';
import { VmSeasonTicketRenewalAvailableRow } from './vm-season-ticket-renewal-available-row.model';
import { VmSeasonTicketRenewalAvailableSeat } from './vm-season-ticket-renewal-available-seat.model';
import { VmSeasonTicketRenewalAvailableSector } from './vm-season-ticket-renewal-available-sector.model';

export interface VmSeasonTicketRenewalEdit extends SeasonTicketRenewal {
    assignedRateId?: number;
    assignedRate?: string;
    assignedSectorId?: number;
    assignedSector?: VmSeasonTicketRenewalAvailableSector;
    assignedRowId?: number;
    assignedRow?: VmSeasonTicketRenewalAvailableRow;
    assignedNnzId?: number;
    assignedNnz?: VmSeasonTicketRenewalAvailableNnz;
    assignedSeatId?: number;
    assignedSeat?: VmSeasonTicketRenewalAvailableSeat;
    assignedAutoRenewal?: boolean;
    sectorsToShow$: Observable<VmSeasonTicketRenewalAvailableSector[]>;
    rowsRecordKey: string;
    nnzsRecordKey: string;
    rowNnzGroupsToShow$: Observable<VmRowNnzGroupsToShow>;
    rowSeatsRecordKey: string;
    rowSeatsToShow$: Observable<VmSeasonTicketRenewalAvailableSeat[]>;
    nnzSeatsRecordKey: string;
}

export type VmRowNnzGroupsToShow = (VmSeasonTicketRenewalAvailableRowGroup | VmSeasonTicketRenewalAvailableNnzGroup)[];

export interface VmSeasonTicketRenewalAvailableNnzGroup {
    name: string;
    data: VmSeasonTicketRenewalAvailableNnz[];
}

export interface VmSeasonTicketRenewalAvailableRowGroup {
    name: string;
    data: VmSeasonTicketRenewalAvailableRow[];
}

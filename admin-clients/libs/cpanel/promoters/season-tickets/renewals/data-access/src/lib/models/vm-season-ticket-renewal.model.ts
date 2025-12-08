import { SeasonTicketRenewal } from './season-ticket-renewal.model';

export interface VmSeasonTicketRenewal extends SeasonTicketRenewal {
    isSelected: boolean;
    isSelectable: boolean;
    historicLocation: string;
    actualLocation: string;
}

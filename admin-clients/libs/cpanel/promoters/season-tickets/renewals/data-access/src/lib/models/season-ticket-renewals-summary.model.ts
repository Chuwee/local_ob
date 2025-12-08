import { AutomaticRenewalStatus, RenewalsGenerationStatus } from './renewals-generation-status.enum';

export interface SeasonTicketRenewalsSummary {
    total_imports: number;
    mapped_imports: number;
    not_mapped_imports: number;
    origin_season_ticket_name: string;
    origin_season_ticket_id: number;
    renewal_import_date: string;
    generation_status: RenewalsGenerationStatus;
    automatic_renewal_status?: AutomaticRenewalStatus;
}

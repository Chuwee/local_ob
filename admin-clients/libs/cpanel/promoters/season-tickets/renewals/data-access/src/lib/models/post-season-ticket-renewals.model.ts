import { SeasonTicketRenewalRateMapping } from './season-ticket-renewal-rate-mapping.model';

export interface PostSeasonTicketRenewals {
    renewal_season_ticket?: number;
    renewal_external_event?: number;
    is_external_event?: boolean;
    rates: SeasonTicketRenewalRateMapping[];
    include_all_entities?: boolean;
    include_balance: boolean;
}

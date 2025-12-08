import { SeasonTicketRenewalCandidatesReasons } from './season-ticket-renewal-candidates-reasons.enum';

export interface SeasonTicketRenewalCandidateSearch {
    id: number;
    name: string;
    compatible: boolean;
    reasons?: SeasonTicketRenewalCandidatesReasons[];
}

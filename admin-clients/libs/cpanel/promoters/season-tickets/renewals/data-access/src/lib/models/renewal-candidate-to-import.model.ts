import { RenewalCandidateTypeEnum } from './renewal-candidate-type.enum';
import { SeasonTicketRenewalRateMapping } from './season-ticket-renewal-rate-mapping.model';

export interface RenewalCandidateToImport {
    renewalCandidateId: number;
    renewalRates: SeasonTicketRenewalRateMapping[];
    type: RenewalCandidateTypeEnum;
    includeBalance: boolean;
}

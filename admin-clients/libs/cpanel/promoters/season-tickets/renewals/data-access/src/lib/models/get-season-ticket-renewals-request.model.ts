import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SeasonTicketRenewalMappingStatus } from './season-ticket-renewal-mapping-status.enum';
import { SeasonTicketRenewalStatus } from './season-ticket-renewal-status.enum';

export interface GetSeasonTicketRenewalsRequest extends PageableFilter {
    endDate?: string;
    mapping_status?: SeasonTicketRenewalMappingStatus;
    renewal_status?: SeasonTicketRenewalStatus;
    renewal_substatus?: string;
    auto_renewal?: boolean;
    startDate?: string;
    entityId?: number;
}

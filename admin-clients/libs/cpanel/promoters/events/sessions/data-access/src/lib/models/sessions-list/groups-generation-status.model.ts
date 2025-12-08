import { SessionsGenerationStatusCounters } from '@admin-clients/cpanel/promoters/events/data-access';

export interface GroupsGenerationStatus {
    [date: string]: SessionsGenerationStatusCounters;
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetEventTierRequest extends PageableFilter {
    venue_template_id: string;
    active?: boolean;
}

import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VenuesFilterFields } from './venues-filter-fields.model';

export interface GetVenuesRequest extends PageableFilter {
    entityId?: number;
    countryCode?: string;
    city?: string;
    includeThirdPartyVenues?: boolean;
    onlyInUseVenues?: boolean;
    fields?: VenuesFilterFields[];
    includeOwnTemplateVenues?: boolean;
}

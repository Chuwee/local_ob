import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VenueTemplateScope } from './venue-template-scope.enum';

export interface BaseVenueTemplatesRequest extends PageableFilter {
    eventId?: number;
    scope?: VenueTemplateScope;
}

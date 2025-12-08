import { BaseVenueTemplatesRequest } from './base-venue-templates-request.model';
import { VenueTemplateStatus } from './venue-template-status.enum';
import { VenueTemplateType } from './venue-template-type.enum';

export interface GetVenueTemplatesRequest extends BaseVenueTemplatesRequest {
    filter?: string;
    venueId?: number;
    entityId?: number;
    venueEntityId?: number;
    type?: VenueTemplateType;
    eventId?: number;
    status?: VenueTemplateStatus[];
    publicTpl?: boolean;
    includeThirdPartyTemplates?: boolean;
    has_avet_mapping?: boolean;
    graphic?: boolean;
    city?: string;
    inventory_provider?: string; // TODO: change to enum when backend uses the same enum in all cases
}

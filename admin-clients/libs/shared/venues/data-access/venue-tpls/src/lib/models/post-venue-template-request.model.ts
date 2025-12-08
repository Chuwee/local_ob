import { VenueTemplateScope } from './venue-template-scope.enum';
import { VenueTemplateType } from './venue-template-type.enum';

export interface PostVenueTemplateRequest {
    name: string;
    venue_id: number;
    space_id?: number;
    event_id?: number;
    entity_id?: number;
    scope?: VenueTemplateScope;
    type?: VenueTemplateType;
    graphic?: boolean;
    from_template_id?: number;
    additional_config?: {
        external_capacity_id: number;
        inventory_provider?: string; // TODO: change to enum when backend uses the same enum in all cases
        inventory_id?: string;
    };
}

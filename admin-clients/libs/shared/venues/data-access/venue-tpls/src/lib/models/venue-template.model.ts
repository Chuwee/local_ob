import { IdName, Venue } from '@admin-clients/shared/data-access/models';
import { ActivityGroupsConfig } from './activity/activity-groups-config.model';
import { VenueTemplateScope } from './venue-template-scope.enum';
import { VenueTemplateStatus } from './venue-template-status.enum';
import { VenueTemplateType } from './venue-template-type.enum';

export interface VenueTemplate {
    id: number;
    name: string;
    entity: IdName;
    venue: Venue;
    space: IdName;
    capacity: number;
    available_capacity: number;
    status: VenueTemplateStatus;
    type: VenueTemplateType;
    scope: VenueTemplateScope;
    event_id: number;
    graphic: boolean;
    public: boolean;
    creation_date: string;
    image_url: string;
    groups: ActivityGroupsConfig;
    external_data: Record<string, string>;
    inventory_provider: 'sga';  // TODO: change to enum when backend uses the same enum in all cases
}

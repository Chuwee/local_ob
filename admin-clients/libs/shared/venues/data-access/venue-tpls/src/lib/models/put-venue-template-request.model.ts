import { ActivityGroupsConfig } from './activity/activity-groups-config.model';

export interface PutVenueTemplateRequest {
    name?: string;
    image?: string;
    venue_id?: number;
    space_id?: number;
    groups?: ActivityGroupsConfig;
}

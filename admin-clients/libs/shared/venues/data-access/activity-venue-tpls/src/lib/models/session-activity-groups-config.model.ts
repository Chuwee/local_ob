import { ActivityGroupsConfig } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface SessionActivityGroupsConfig extends ActivityGroupsConfig {
    use_venue_template_group_config: boolean;
    venue_template_name?: string;
}

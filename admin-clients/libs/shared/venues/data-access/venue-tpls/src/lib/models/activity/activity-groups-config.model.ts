import { ActivityGroupMaxType } from './activity-group-max-type.enum';

export interface ActivityGroupsConfig {
    limit: {
        type: ActivityGroupMaxType;
        value: number;
    };
    attendees: {
        min: number;
        max: {
            type: ActivityGroupMaxType;
            value: number;
        };
    };
    companions: {
        min: number;
        max: {
            type: ActivityGroupMaxType;
            value: number;
        };
        occupy_capacity: boolean;
    };
}

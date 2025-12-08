import { ActivityLimitType } from './activity-limit-type.enum';

export interface ActivityCapacityValue {
    type: ActivityLimitType;
    value: number;
}

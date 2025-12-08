import { ActivityCapacityValue } from './activity-capacity-value.model';

export interface ActivityCapacity {
    id: number;
    capacity?: ActivityCapacityValue;
    on_sale?: boolean;
}

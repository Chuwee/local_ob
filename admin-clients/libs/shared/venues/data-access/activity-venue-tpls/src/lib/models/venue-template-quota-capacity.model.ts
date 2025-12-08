import { ActivityCapacityValue } from './activity-capacity-value.model';
import { ActivityCapacity } from './activity-capacity.model';

export interface VenueTemplateQuotaCapacity {
    id: number;
    max_capacity: ActivityCapacityValue;
    price_types: ActivityCapacity[];
}

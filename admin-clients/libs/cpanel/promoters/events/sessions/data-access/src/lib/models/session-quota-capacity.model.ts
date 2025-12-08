import { ActivityCapacity, ActivityCapacityValue } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';

export interface SessionQuotaCapacity {
    id: number;
    max_capacity: ActivityCapacityValue;
    price_types: ActivityCapacity[];
}

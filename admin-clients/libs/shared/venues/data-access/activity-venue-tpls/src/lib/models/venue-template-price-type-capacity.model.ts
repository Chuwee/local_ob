import { ActivityCapacity } from './activity-capacity.model';

export interface VenueTemplatePriceTypeCapacity {
    id: number;
    maxCapacity?: number;
    quotas?: ActivityCapacity[];
}

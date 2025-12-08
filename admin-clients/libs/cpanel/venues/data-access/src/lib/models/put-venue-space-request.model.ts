import { VenueSpaceCapacityType } from './venue-space-capacity-type.enum';

export interface PutVenueSpaceRequest {
    name: string;
    capacity?: {
        type: VenueSpaceCapacityType;
        value?: number;
    };
    default?: boolean;
    notes?: string;
}

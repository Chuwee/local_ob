import { VenueSpaceCapacityType } from './venue-space-capacity-type.enum';

export interface PostVenueSpaceRequest {
    name: string;
    capacity?: {
        type: VenueSpaceCapacityType;
        value?: number;
    };
    notes?: string;
}

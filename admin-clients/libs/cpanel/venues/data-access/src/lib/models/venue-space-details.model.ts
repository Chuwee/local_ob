import { VenueSpaceCapacityType } from './venue-space-capacity-type.enum';

export interface VenueSpaceDetails {
    venue_id: number;
    id: number;
    name: string;
    capacity?: {
        type: VenueSpaceCapacityType;
        value?: number;
    };
    default: boolean;
    notes?: string;
}

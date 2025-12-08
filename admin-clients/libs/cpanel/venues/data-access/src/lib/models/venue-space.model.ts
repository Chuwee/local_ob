import { VenueSpaceCapacityType } from './venue-space-capacity-type.enum';

export interface VenueSpace {
    id: number;
    name: string;
    capacity?: {
        type: VenueSpaceCapacityType;
        value?: number;
    };
    default: boolean;
}

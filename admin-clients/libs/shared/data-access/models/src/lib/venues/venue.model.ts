import { VenueAccessControlSystem } from './venue-access-control-system.model';

export interface Venue {
    id: number;
    name: string;
    entity: {
        id: number;
        name: string;
    };
    city: string;
    country: string;
    timezone: string;
    capacity: number;
    access_control_systems?: VenueAccessControlSystem[];
    external_id: string;
}

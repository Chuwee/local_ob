import { VenueContact } from './venue-contact.model';
import { VenueType } from './venue-type.enum';

export interface PostVenueRequest {
    entity_id?: number;
    name: string;
    type: VenueType;
    capacity: number;
    timezone: string;
    calendar_id?: number;
    country_code: string;
    country_subdivision_code: string;
    city: string;
    postal_code: string;
    address: string;
    coordinates: {
        latitude: number;
        longitude: number;
    };
    manager?: string;
    owner?: string;
    website?: string;
    contact?: VenueContact;
    google_place_id: string;
    external_id?: string;
}

import { Country, Region } from '@admin-clients/shared/common/data-access';
import { Space } from './space.model';
import { VenueContact } from './venue-contact.model';
import { VenueType } from './venue-type.enum';

export interface VenueDetails {
    id: number;
    entity: {
        id: number;
        name: string;
    };
    name: string;
    type?: VenueType;
    capacity?: number;
    public: boolean;
    timezone: string;
    calendar?: {
        id: number;
        name: string;
    };
    country: Country;
    country_subdivision: Region;
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
    image_logo_url?: string;
    google_place_id: string;
    spaces: Space[];
    external_id?: string;
}

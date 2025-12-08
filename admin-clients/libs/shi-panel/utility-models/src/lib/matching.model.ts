import { IdName } from '@admin-clients/shared/data-access/models';
import { MatchingStatus } from './matching.status';
import { SupplierName } from './supplier-name.enum';

export interface Matching {
    id: string;
    status?: MatchingStatus;
    supplier: SupplierName;
    supplier_event: Event;
    shi_event: Event;
}

export interface Event {
    id: string;
    name: string;
    date: string;
    venue: Venue;
    availability?: {
        tickets: number;
        listings: number;
    };
    taxonomies?: IdName[];
}

export interface Venue {
    id?: string;
    name: string;
    latitude: number;
    longitude: number;
    city: string;
    country_code: string;
    state: string;
    postal_code: string;
}

export interface GetFavoritesResponse {
    total: number;
    favorites: number;
    available: boolean;
}

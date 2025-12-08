import { DeliveryPointStatus } from './delivery-point.model';

export interface PutProductDeliveryPoint {
    name?: string;
    location?: {
        country?: string;
        country_subdivision?: string;
        city?: string;
        zip_code?: string;
        address?: string;
        notes?: string;
    };
    venue_id?: string;
    status?: DeliveryPointStatus;
}

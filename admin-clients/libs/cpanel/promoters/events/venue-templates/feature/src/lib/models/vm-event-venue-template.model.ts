import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface VmEventVenueTemplate extends VenueTemplate {
    isActiveFromInProgress?: boolean;
}

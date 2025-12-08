import { NotNumberedZone, Seat } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export interface VenueTemplateSelectionChange {
    select: boolean;
    items: (Seat | NotNumberedZone)[];
}

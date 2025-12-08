import { VenueTemplatePriceType } from '@admin-clients/shared/venues/data-access/venue-tpls';

export interface PriceTypesFilter {
    price_types_origin?: VenueTemplatePriceType[];
    price_types_target?: VenueTemplatePriceType[];
}

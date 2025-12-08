import { VenueTemplatePriceType } from './venue-template-price-type.model';

export interface VenueTemplatePriceTypeGrouped {
    venueTemplateName: string;
    priceTypes: VenueTemplatePriceType[];
}

import { Provider } from '@angular/core';
import { VenueTemplatePriceTypesApi } from './api/venue-template-price-types.api';
import { VenueTemplatePriceTypesState } from './state/venue-template-price-types.state';
import { VenueTemplatePriceTypesService } from './venue-template-price-types.service';

export const venueTemplatePriceTypesProviders: Provider[] = [
    VenueTemplatePriceTypesApi,
    VenueTemplatePriceTypesState,
    VenueTemplatePriceTypesService
];

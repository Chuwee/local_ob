import { Provider } from '@angular/core';
import { VenueTemplatesApi } from './api/venue-templates.api';
import { VenueTemplatesState } from './state/venue-templates.state';
import { VenueTemplatesService } from './venue-templates.service';

export const venueTemplatesProviders: Provider[] = [
    VenueTemplatesState,
    VenueTemplatesApi,
    VenueTemplatesService
];

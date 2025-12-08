import { Provider } from '@angular/core';
import { VenuesApi } from './api/venues.api';
import { VenuesState } from './state/venues.state';
import { VenuesService } from './venues.service';

export const venuesProviders: Provider[] = [
    VenuesApi,
    VenuesState,
    VenuesService
];

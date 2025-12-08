import { StateProperty } from '@OneboxTM/utils-state';
import { IdName, VenueAccessControlSystem } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetVenueCitiesResponse } from '../models/get-venue-cities-response.model';
import { GetVenueCountriesResponse } from '../models/get-venue-countries-response.model';
import { GetVenueSpacesResponse } from '../models/get-venue-spaces-response.model';
import { GetVenuesResponse } from '../models/get-venues-response.model';
import { VenueDetails } from '../models/venue-details.model';
import { VenueSpaceDetails } from '../models/venue-space-details.model';
import { VenueSpacesLoadCase } from '../models/venue-spaces-load.case';

@Injectable()
export class VenuesState {
    // venue list
    readonly venuesList = new StateProperty<GetVenuesResponse>();
    readonly venuesCache = new ItemCache<IdName>();
    // venue
    readonly venue = new StateProperty<VenueDetails>();
    // save venue
    readonly venueSaving = new StateProperty<void>();
    // venue countries
    readonly venueCountriesList = new StateProperty<GetVenueCountriesResponse>();
    // venue cities
    readonly venueCitiesList = new StateProperty<GetVenueCitiesResponse>();
    // venue access control system
    readonly venueAccessControlSystem = new StateProperty<VenueAccessControlSystem>();
    // ListDetailState
    readonly listDetailState = new StateProperty<VenueSpacesLoadCase>(VenueSpacesLoadCase.none);
    // Venue spaces
    readonly venueSpacesList = new StateProperty<GetVenueSpacesResponse>();
    // Venue space
    readonly venueSpace = new StateProperty<VenueSpaceDetails>();
    // Save venue space
    readonly venueSpaceSaving = new StateProperty<void>();
}

import { StateProperty } from '@OneboxTM/utils-state';
import { VenueMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';

@Injectable()
export class MultiSessionCapacityState {
    readonly venueMap = new StateProperty<VenueMap>();
}

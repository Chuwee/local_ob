/* eslint-disable @typescript-eslint/member-ordering*/
import { Injectable } from '@angular/core';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { VenueMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { SeasonTicketLinkableSeats } from '../models/season-ticket-linkable-seats.model';

@Injectable()
export class SeasonTicketCapacityState {
    // venue template map
    private readonly _venueTplMap = new BaseStateProp<VenueMap>();
    readonly getVenueTplMap$ = this._venueTplMap.getValueFunction();
    readonly setVenueTplMap = this._venueTplMap.setValueFunction();
    readonly isVenueTplMapLoading$ = this._venueTplMap.getInProgressFunction();
    readonly setVenueTplMapLoading = this._venueTplMap.setInProgressFunction();
    // update seats
    private readonly _updateSeats = new BaseStateProp<void>();
    readonly isUpdateSeatsInProgress$ = this._updateSeats.getInProgressFunction();
    readonly setUpdateSeatsInProgress = this._updateSeats.setInProgressFunction();
    // linkableSeats
    private readonly _linkableSeats = new BaseStateProp<SeasonTicketLinkableSeats>();
    readonly getLinkableSeats$ = this._linkableSeats.getValueFunction();
    readonly setLinkableSeats = this._linkableSeats.setValueFunction();
}

/* eslint-enable @typescript-eslint/member-ordering*/

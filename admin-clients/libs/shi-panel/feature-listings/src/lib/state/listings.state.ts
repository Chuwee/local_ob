import { StateProperty } from '@OneboxTM/utils-state';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { GetListingsResponse } from '../models/get-listings-response.model';
import { Transition } from '../models/listing-transition.model';
import { Listing } from '../models/listings.model';

@Injectable()
export class ListingsState {
    readonly list = new StateProperty<GetListingsResponse>();
    readonly listExport = new StateProperty<ExportResponse>();
    readonly details = new StateProperty<Listing>();
    readonly transitions = new StateProperty<Transition[]>();
}

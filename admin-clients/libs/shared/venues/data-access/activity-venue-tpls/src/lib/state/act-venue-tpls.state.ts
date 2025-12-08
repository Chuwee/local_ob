import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { VenueTemplateQuotaCapacity } from '../models/venue-template-quota-capacity.model';

@Injectable()
export class ActVenueTplsState {

    readonly quotaCapacities = new StateProperty<VenueTemplateQuotaCapacity[]>();
    readonly quotaCapacitiesUpdate = new StateProperty();
}

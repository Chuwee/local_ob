import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { EventRestrictionList, EventRestrictionStructure } from '../models/event-restrictions.model';

@Injectable()
export class EventRestrictionsState {
    readonly restrictions = new StateProperty<EventRestrictionList>();
    readonly restrictionsStructure = new StateProperty<EventRestrictionStructure[]>();
    readonly restrictionsLoading = new StateProperty<Record<string, boolean>>({});
}

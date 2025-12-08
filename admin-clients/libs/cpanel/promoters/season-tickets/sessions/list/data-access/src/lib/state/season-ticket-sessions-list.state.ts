import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { SeasonTicketSessionsAction } from '../models/season-ticket-sessions-action.enum';
import { VmSeasonTicketSession } from '../models/vm-season-ticket-session.model';

@Injectable()
export class SeasonTicketSessionsListState {
    readonly vmSessions = new StateProperty<VmSeasonTicketSession[]>();
    readonly sessionsAction = new StateProperty<SeasonTicketSessionsAction>(SeasonTicketSessionsAction.none);
    readonly validationInProgress = new StateProperty<void>();
    readonly saveChangesInProgress = new StateProperty<void>();
}

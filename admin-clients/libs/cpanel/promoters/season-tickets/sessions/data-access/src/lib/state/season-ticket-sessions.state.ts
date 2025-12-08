import { StateProperty } from '@OneboxTM/utils-state';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetSeasonTicketSessionsEventsResponse } from '../models/get-season-ticket-sessions-events-response.model';
import { GetSeasonTicketSessionsResponse } from '../models/get-season-ticket-sessions-response.model';
import { SeasonTicketSessionUnAssignment } from '../models/season-ticket-session-unassignment.model';
import { SeasonTicketSessionsAssignments } from '../models/season-ticket-sessions-assignments.model';
import { SeasonTicketSessionsValidations } from '../models/season-ticket-sessions-validations.model';

@Injectable()
export class SeasonTicketSessionsState {
    // season ticket sessions
    readonly sessions = new StateProperty<GetSeasonTicketSessionsResponse>();
    readonly allSessions = new StateProperty<GetSeasonTicketSessionsResponse>();

    // season ticket sessions events
    private readonly _sessionsEvents = new BaseStateProp<GetSeasonTicketSessionsEventsResponse>();
    readonly getSessionsEventsList$ = this._sessionsEvents.getValueFunction();
    readonly setSessionsEventsList = this._sessionsEvents.setValueFunction();

    // season ticket sessions validations
    private readonly _sessionsValidations = new BaseStateProp<SeasonTicketSessionsValidations>();
    readonly setSessionsValidations = this._sessionsValidations.setValueFunction();
    readonly getSessionsValidations$ = this._sessionsValidations.getValueFunction();

    // season ticket sessions assignments
    private readonly _sessionsAssignments = new BaseStateProp<SeasonTicketSessionsAssignments>();
    readonly setSessionsAssignments = this._sessionsAssignments.setValueFunction();
    readonly getSessionsAssignments$ = this._sessionsAssignments.getValueFunction();

    // season ticket session unassignment
    private readonly _sessionUnassignment = new BaseStateProp<SeasonTicketSessionUnAssignment>();
    readonly setSessionUnassignments = this._sessionUnassignment.setValueFunction();
    readonly getSessionUnassignment$ = this._sessionUnassignment.getValueFunction();
}

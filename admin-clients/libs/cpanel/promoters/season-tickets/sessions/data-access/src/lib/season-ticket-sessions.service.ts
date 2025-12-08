import { StateManager, getListData, getMetadata, mapMetadata } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { SeasonTicketSessionsApi } from './api/season-ticket-sessions.api';
import { GetSeasonTicketSessionsEventsRequest } from './models/get-season-ticket-sessions-events-request.model';
import { GetSeasonTicketSessionsRequest } from './models/get-season-ticket-sessions-request.model';
import { SeasonTicketSessionUnAssignment } from './models/season-ticket-session-unassignment.model';
import { SeasonTicketSessionsAssignments } from './models/season-ticket-sessions-assignments.model';
import { SeasonTicketSessionsEvent } from './models/season-ticket-sessions-event.model';
import { SeasonTicketSessionsValidations } from './models/season-ticket-sessions-validations.model';
import { SeasonTicketSessionsState } from './state/season-ticket-sessions.state';
import { SeasonTicketSessionStatus } from './models/season-ticket-session-status.enum';

@Injectable()
export class SeasonTicketSessionsService {
    readonly #seasonTicketSessionsApi = inject(SeasonTicketSessionsApi);
    readonly #seasonTicketSessionsState = inject(SeasonTicketSessionsState);

    readonly sessions = Object.freeze({
        load: (seasonTicketId: string, request: GetSeasonTicketSessionsRequest) =>
            StateManager.load(
                this.#seasonTicketSessionsState.sessions,
                this.#seasonTicketSessionsApi.getSessions(seasonTicketId, request)
            ),
        loadAll: (seasonTicketId: string) =>
            StateManager.load(
                this.#seasonTicketSessionsState.allSessions,
                this.#seasonTicketSessionsApi.getSessions(seasonTicketId, {
                    limit: 9999,
                    sort: 'session_starting_date:asc',
                    status: SeasonTicketSessionStatus.assigned,
                    startDate: new Date(Date.now()).toISOString()
                })),
        getData$: () => this.#seasonTicketSessionsState.sessions.getValue$().pipe(getListData()),
        getMetadata$: () => this.#seasonTicketSessionsState.sessions.getValue$().pipe(mapMetadata(), getMetadata()),
        getAllData$: () => this.#seasonTicketSessionsState.allSessions.getValue$().pipe(getListData()),
        clear: () => this.#seasonTicketSessionsState.sessions.setValue(null),
        loading$: () => this.#seasonTicketSessionsState.sessions.isInProgress$(),
        get$: () => this.#seasonTicketSessionsState.sessions.getValue$(),
        getSummary$: () => this.#seasonTicketSessionsState.sessions.getValue$()
            .pipe(map(seasonTicketSessions => seasonTicketSessions?.summary))
    });

    loadSessionsEventsList(seasonTicketId: string, request: GetSeasonTicketSessionsEventsRequest): void {
        this.#seasonTicketSessionsApi.getSessionsEvents(seasonTicketId, request)
            .subscribe(seasonTicketSessionsEvents =>
                this.#seasonTicketSessionsState.setSessionsEventsList(seasonTicketSessionsEvents));
    }

    getSessionsEventsList$(): Observable<SeasonTicketSessionsEvent[]> {
        return this.#seasonTicketSessionsState.getSessionsEventsList$()
            .pipe(map(sessionsEvents => sessionsEvents?.data));
    }

    loadSessionsValidations(seasonTicketId: string, sessions: number[]): void {
        this.#seasonTicketSessionsApi.getSessionsValidations(seasonTicketId, sessions)
            .subscribe(seasonTicketSessionsValidations =>
                this.#seasonTicketSessionsState.setSessionsValidations(seasonTicketSessionsValidations));
    }

    getSessionsValidations$(): Observable<SeasonTicketSessionsValidations> {
        return this.#seasonTicketSessionsState.getSessionsValidations$();
    }

    clearSessionsValidations(): void {
        this.#seasonTicketSessionsState.setSessionsValidations(null);
    }

    saveSessionsAssignments(seasonTicketId: string, sessions: number[]): void {
        this.#seasonTicketSessionsApi.postSessionsAssignments(seasonTicketId, sessions)
            .subscribe(sessionsAssignments =>
                this.#seasonTicketSessionsState.setSessionsAssignments(sessionsAssignments));
    }

    getSessionsAssignments$(): Observable<SeasonTicketSessionsAssignments> {
        return this.#seasonTicketSessionsState.getSessionsAssignments$();
    }

    clearSessionsAssignments(): void {
        this.#seasonTicketSessionsState.setSessionsAssignments(null);
    }

    unassignSessionAssignment(seasonTicketId: string, sessionId: number): void {
        this.#seasonTicketSessionsApi.deleteSessionAssignment(seasonTicketId, sessionId)
            .subscribe(sessionsUnassignment =>
                this.#seasonTicketSessionsState.setSessionUnassignments(sessionsUnassignment));
    }

    getSessionUnassignments$(): Observable<SeasonTicketSessionUnAssignment> {
        return this.#seasonTicketSessionsState.getSessionUnassignment$();
    }

    clearSessionsUnassignments(): void {
        this.#seasonTicketSessionsState.setSessionUnassignments(null);
    }

    updateSessionsBarcodes(seasonTicketId: number): Observable<void> {
        return this.#seasonTicketSessionsApi.updateSessionsBarcodes(seasonTicketId)
            .pipe(catchError(() => of(null)));
    }
}

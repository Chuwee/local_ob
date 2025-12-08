import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { GetSeasonTicketSessionsEventsRequest } from '../models/get-season-ticket-sessions-events-request.model';
import { GetSeasonTicketSessionsEventsResponse } from '../models/get-season-ticket-sessions-events-response.model';
import { GetSeasonTicketSessionsRequest } from '../models/get-season-ticket-sessions-request.model';
import { GetSeasonTicketSessionsResponse } from '../models/get-season-ticket-sessions-response.model';
import {
    SeasonTicketSessionUnAssignment, SeasonTicketUnAssignSessionReason
} from '../models/season-ticket-session-unassignment.model';
import {
    SeasonTicketSessionAssignment, SeasonTicketSessionsAssignments
} from '../models/season-ticket-sessions-assignments.model';
import { SeasonTicketValidateOrAssignSessionReason } from '../models/season-ticket-sessions-reasons.model';
import {
    SeasonTicketSessionsValidations, SeasonTicketSessionValidation
} from '../models/season-ticket-sessions-validations.model';

@Injectable()
export class SeasonTicketSessionsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SEASON_TICKETS_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;

    private readonly _http = inject(HttpClient);

    getSessions(seasonTicketId: string, request: GetSeasonTicketSessionsRequest): Observable<GetSeasonTicketSessionsResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            event_id: request.event_id,
            status: request.status,
            start_date: getRangeParam(request.startDate, request.endDate)
        });
        return this._http.get<GetSeasonTicketSessionsResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions`, { params }
        );
    }

    getSessionsEvents(
        seasonTicketId: string,
        request: GetSeasonTicketSessionsEventsRequest
    ): Observable<GetSeasonTicketSessionsEventsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetSeasonTicketSessionsEventsResponse>(
            (`${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions/events`),
            { params });
    }

    getSessionsValidations(
        seasonTicketId: string,
        sessions: number[]
    ): Observable<SeasonTicketSessionsValidations> {
        const params = buildHttpParams({ sessions });
        return mapValidationReasons(
            this._http.get<SeasonTicketSessionsValidations>(
                `${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions/validations`, { params })
                .pipe(
                    catchError(() => of({ result: [] }))
                ),
            +seasonTicketId,
            sessions,
            SeasonTicketValidateOrAssignSessionReason.notValidReason
        );
    }

    postSessionsAssignments(
        seasonTicketId: string,
        sessions: number[]
    ): Observable<SeasonTicketSessionsAssignments> {
        return mapAssignmentReasons(
            this._http.post<SeasonTicketSessionsAssignments>(
                `${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions`, { sessions, update_barcodes: false })
                .pipe(
                    catchError(() => of({ result: [] }))
                ),
            +seasonTicketId,
            sessions,
            SeasonTicketValidateOrAssignSessionReason.notValidReason
        );
    }

    deleteSessionAssignment(
        seasonTicketId: string,
        sessionId: number
    ): Observable<SeasonTicketSessionUnAssignment> {
        const params = buildHttpParams({ update_barcodes: false });
        return mapUnAssignmentReasons(
            this._http.delete<SeasonTicketSessionUnAssignment>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions/${sessionId}`,
                { params })
                .pipe(
                    catchError(() => of(null))
                ),
            +seasonTicketId,
            sessionId,
            SeasonTicketUnAssignSessionReason.notValidUnAssignReason
        );
    }

    updateSessionsBarcodes(seasonTicketId: number): Observable<void> {
        return this._http.post<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/sessions/update-barcodes`, {});
    }
}

function mapValidationReasons(
    sessions$: Observable<SeasonTicketSessionsValidations>,
    seasonTicketId: number,
    sessions: number[],
    genericReason: SeasonTicketValidateOrAssignSessionReason
): Observable<SeasonTicketSessionsValidations> {
    return sessions$.pipe(
        map(({ result }) => {
            if (result.length) {
                const mappedResult = result.map(element => {
                    if (element.reason && !Object.values(SeasonTicketValidateOrAssignSessionReason).includes(element.reason)) {
                        return {
                            ...element,
                            reason: genericReason
                        };
                    } else {
                        return element;
                    }
                });
                return { result: mappedResult };
            } else {
                const mappedResult: SeasonTicketSessionValidation[] = sessions.map(sessionId => ({
                    season_ticket_id: seasonTicketId,
                    session_id: sessionId,
                    reason: genericReason,
                    session_valid: false
                }));
                return { result: mappedResult };
            }
        })
    );
}

function mapAssignmentReasons(
    sessions$: Observable<SeasonTicketSessionsAssignments>,
    seasonTicketId: number,
    sessions: number[],
    genericReason: SeasonTicketValidateOrAssignSessionReason
): Observable<SeasonTicketSessionsAssignments> {
    return sessions$.pipe(
        map(({ result }) => {
            if (result.length) {
                const mappedResult = result.map(element => {
                    if (element.reason && !Object.values(SeasonTicketValidateOrAssignSessionReason).includes(element.reason)) {
                        return {
                            ...element,
                            reason: genericReason
                        };
                    } else {
                        return element;
                    }
                });

                return { result: mappedResult };
            } else {
                const mappedResult: SeasonTicketSessionAssignment[] = sessions.map(sessionId => ({
                    season_ticket_id: seasonTicketId,
                    session_id: sessionId,
                    reason: genericReason,
                    session_assigned: false
                }));
                return { result: mappedResult };
            }
        })
    );
}

function mapUnAssignmentReasons(
    sessions$: Observable<SeasonTicketSessionUnAssignment>,
    seasonTicketId: number,
    sessionId: number,
    genericReason: SeasonTicketUnAssignSessionReason
): Observable<SeasonTicketSessionUnAssignment> {
    return sessions$.pipe(
        map(element => {
            if (element?.reason && !Object.values(SeasonTicketUnAssignSessionReason).includes(element.reason)) {
                return {
                    ...element,
                    reason: genericReason
                };
            } else if (element) {
                return element;
            } else {
                return {
                    session_unassigned: false,
                    season_ticket_id: seasonTicketId,
                    session_id: sessionId,
                    reason: genericReason
                };
            }
        })
    );
}


import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SessionPacksLoadCase } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import {
    EventSessionsService, EventSessionsState, Session, SessionsFilterFields, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { GuardsCheckEnd, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { filter, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';

export type SessionPacksStateParams = {
    state: SessionPacksLoadCase;
    idPath?: string;
    innerPath?: string;
};

@Injectable()
export class SessionPacksStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: string;
    private _innerPath: string;
    private _sessionId: number;

    constructor(
        private _sessionsState: EventSessionsState,
        private _sessionsSrv: EventSessionsService,
        private _eventsSrv: EventsService,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                filter(state => state !== null),
                tap(state => {
                    switch (state) {
                        case SessionPacksLoadCase.loadSessionPack:
                            this.loadSessionPack();
                            break;
                        case SessionPacksLoadCase.selectedSessionPack:
                            this.selectedSessionPack();
                            break;
                        case SessionPacksLoadCase.none:
                        default:
                            break;
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath, innerPath }: SessionPacksStateParams): void {
        this._idPath = idPath;
        this._innerPath = innerPath || null;
        this._sessionsState.listDetailsState.setValue(state);
    }

    getListDetailState$(): Observable<SessionPacksLoadCase> {
        return this._sessionsState.listDetailsState.getValue$();
    }

    loadSessionPacksList(): void {
        this._eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                tap(event => {
                    this._sessionsSrv.clearAllSessions();
                    this._sessionsSrv.loadAllSessions(event.id, {
                        sort: `${SessionsFilterFields.startDate}:asc`,
                        type: [SessionType.restrictedPack, SessionType.unrestrictedPack]
                    });
                })
            ).subscribe();
    }

    private loadSessionPack(): void {
        this.loadSessionPacksList();
        this.getSessionPacksList()
            .pipe(
                tap(sessions => {
                    if (sessions.length) {
                        this.setSessionIdOfSessionsList(sessions);
                        this.loadSession();
                        this.navigateToSession();
                    }
                })
            ).subscribe();
    }

    private selectedSessionPack(): void {
        this.getSessionPacksList()
            .pipe(
                tap(sessions => {
                    if (sessions.length) {
                        this.setSessionId(Number(this._idPath));
                        this.navigateToSession();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first(event => event instanceof GuardsCheckEnd),
            tap((event: GuardsCheckEnd) => {
                if (event.shouldActivate) {
                    this.loadSession();
                }
            })
        ).subscribe();
    }

    private setSessionId(sessionId: number): void {
        this._sessionId = sessionId;
    }

    private setSessionIdOfSessionsList(sessions: Session[]): void {
        if (this._idPath && !!sessions.length && sessions.some(sessionFromList => String(sessionFromList.id) === this._idPath)) {
            this.setSessionId(Number(this._idPath));
        } else {
            this.setSessionId(sessions[0].id);
        }
    }

    private navigateToSession(): void {
        this._eventsSrv.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    const pathArray = ['/events', event.id, 'session-packs', this._sessionId];
                    if (this._innerPath) {
                        pathArray.push(this._innerPath);
                    }
                    this._router.navigate(pathArray);
                })
            ).subscribe();
    }

    private loadSession(): void {
        this._eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                tap(event => {
                    this._sessionsSrv.session.load(event.id, this._sessionId);
                })
            ).subscribe();
    }

    private getSessionPacksList(): Observable<Session[]> {
        return this._sessionsSrv.getAllSessionsData$()
            .pipe(
                first(Boolean),
                switchMap(sessions => {
                    if (!sessions.length) {
                        return this._eventsSrv.event.get$()
                            .pipe(
                                tap(event => this._router.navigate(['/events', event.id, 'session-packs'])),
                                mapTo(sessions)
                            );
                    } else {
                        return of(sessions);
                    }
                }),
                take(1)
            );
    }
}


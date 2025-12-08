import {
    SeasonTicketSessionsService, SeasonTicketSessionsSummary, SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import {
    VmSeasonTicketSessionsSummary, SeasonTicketSessionsListService, SeasonTicketSessionsListActionsService,
    SeasonTicketSessionsAction, VmSeasonTicketSession
} from '@admin-clients/cpanel-promoters-season-tickets-sessions-list-data-access';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, shareReplay, switchMap, take } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-sessions-list-summary',
    templateUrl: './season-ticket-sessions-list-summary.component.html',
    styleUrls: ['./season-ticket-sessions-list-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketSessionsListSummaryComponent implements OnInit {
    private _paginatedAssignedSessionsValue = 0;
    private _initialSummary: VmSeasonTicketSessionsSummary = {
        assigned_sessions: 0,
        valid_sessions: 0,
        sessions_on_sale: 0,
        total_sessions: 0,
        listed_events: 0
    };

    summary$: Observable<VmSeasonTicketSessionsSummary>;
    noSummaryInfo = 0;

    constructor(
        private _seasonTicketSessionsSrv: SeasonTicketSessionsService,
        private _sessionsListSrv: SeasonTicketSessionsListService,
        private _actionsSrv: SeasonTicketSessionsListActionsService
    ) {
    }

    ngOnInit(): void {
        this.model();
    }

    private model(): void {
        // To show assigned sessions coherently. The data from backend isn't update fast enough, so we have to falsify the data in the front
        // when saving, assign or unassign
        this.summary$ = combineLatest([
            this._seasonTicketSessionsSrv.sessions.getSummary$(),
            this._sessionsListSrv.getSessionsList$()
        ]).pipe(
            switchMap(([summary, sessions]) =>
                this._actionsSrv.getAction$().pipe(
                    filter(sessionsAction =>
                        sessionsAction !== SeasonTicketSessionsAction.toggleTableRows
                    ),
                    take(1),
                    map(sessionsAction => {
                        if (
                            sessionsAction === SeasonTicketSessionsAction.none ||
                            sessionsAction === SeasonTicketSessionsAction.init ||
                            sessionsAction === SeasonTicketSessionsAction.tableAction ||
                            sessionsAction === SeasonTicketSessionsAction.cancel
                        ) {
                            this.setTotalPaginatedAssignedSessionsValue(
                                sessions,
                                summary,
                                sessionsAction === SeasonTicketSessionsAction.init ||
                                sessionsAction === SeasonTicketSessionsAction.tableAction ||
                                sessionsAction === SeasonTicketSessionsAction.cancel
                            );
                            return summary ?? this._initialSummary;
                        } else if (
                            sessionsAction === SeasonTicketSessionsAction.save ||
                            sessionsAction === SeasonTicketSessionsAction.assign ||
                            sessionsAction === SeasonTicketSessionsAction.unassign
                        ) {
                            return this.getUpdatedSummary(sessions, summary);
                        } else {
                            return undefined;
                        }
                    })
                )),
            shareReplay(1)
        );
    }

    private setTotalPaginatedAssignedSessionsValue(
        sessions: VmSeasonTicketSession[],
        summary: SeasonTicketSessionsSummary,
        isCondition: boolean
    ): void {
        if (
            (sessions?.length) &&
            summary &&
            isCondition
        ) {
            this._paginatedAssignedSessionsValue = 0;
            sessions.forEach(session => {
                if (session.status === SeasonTicketSessionStatus.assigned) {
                    this._paginatedAssignedSessionsValue++;
                }
            });
        }
    }

    private getUpdatedSummary(
        sessions: VmSeasonTicketSession[],
        summary: SeasonTicketSessionsSummary
    ): VmSeasonTicketSessionsSummary {
        let assignedSessions = 0;
        const updatedSummary: VmSeasonTicketSessionsSummary = sessions.reduce((accSummary, session) => {
            if (session.status === SeasonTicketSessionStatus.assigned) {
                assignedSessions++;
            }
            return {
                ...accSummary,
                valid_sessions: session.is_session_valid ? accSummary.valid_sessions + 1 : accSummary.valid_sessions
            };
        }, { ...summary, valid_sessions: 0 });
        const diff = assignedSessions - this._paginatedAssignedSessionsValue;
        updatedSummary.assigned_sessions = updatedSummary.assigned_sessions + diff;
        return updatedSummary;
    }
}

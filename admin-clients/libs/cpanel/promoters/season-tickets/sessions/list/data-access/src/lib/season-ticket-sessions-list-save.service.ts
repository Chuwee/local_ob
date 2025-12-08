import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketSessionsAssignments,
    SeasonTicketSessionsService, SeasonTicketSessionUnAssignment
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { Observable, Subject } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { SeasonTicketSessionsListService } from './season-ticket-sessions-list.service';
import { SeasonTicketSessionsListState } from './state/season-ticket-sessions-list.state';

@Injectable()
export class SeasonTicketSessionsListSaveService {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #listState = inject(SeasonTicketSessionsListState);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #sessionsListSrv = inject(SeasonTicketSessionsListService);
    readonly #destroyRef = inject(DestroyRef);

    #savingValidationsFinish = new Subject<void>();
    #savingUnAssignmentsFinish = new Subject<void>();
    #$seasonTicketId = toSignal(this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(
            first(value => value !== null),
            map(seasonTicket => seasonTicket.id.toString())
        ));

    readonly savingValidationsFinish$ = this.#savingValidationsFinish.asObservable();
    readonly savingUnAssignmentsFinish$ = this.#savingUnAssignmentsFinish.asObservable();

    constructor() {
        this.sessionsSavingValidationHandler();
        this.sessionsSavingUnAssignmentHandler();
    }

    saveSessionStatus(): void {
        if (this.#sessionsListSrv.isSomeSessionValid()) {
            this.setValidSessionsToSave();
        } else if (this.#sessionsListSrv.isSomeSessionToBeUnassigned()) {
            this.setUnassignedSessionsToSave();
        }
    }

    initSavingProgress(): void {
        this.#listState.saveChangesInProgress.setInProgress(true);
    }

    finishSavingProgress(): void {
        this.resetSavingParams();
        this.#listState.saveChangesInProgress.setInProgress(false);
    }

    isSavingChangesInProgress$(): Observable<boolean> {
        return this.#listState.saveChangesInProgress.isInProgress$();
    }

    private sessionsSavingValidationHandler(): void {
        this.#seasonTicketSessionsSrv.getSessionsAssignments$()
            .pipe(
                filter(value => value !== null),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(assignedSessions => {
                this.setAssignedSessions(assignedSessions);
                this.setNextValidSessionsToSave();
                this.saveGroupOfValidSessions();
            });
    }

    private sessionsSavingUnAssignmentHandler(): void {
        this.#seasonTicketSessionsSrv.getSessionUnassignments$()
            .pipe(
                filter(value => value !== null),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(unassignedSession => {
                this.setUnassignedSession(unassignedSession);
                this.setNextUnassignedSessionsToSave();
                this.saveGroupOfUnassignedSessions();
            });
    }

    private resetSavingParams(): void {
        this.#seasonTicketSessionsSrv.clearSessionsUnassignments();
        this.#seasonTicketSessionsSrv.clearSessionsAssignments();
        this.#sessionsListSrv.resetUnAssignmentSessionProcess();
        this.#sessionsListSrv.resetAssigmentSessionProcess();
    }

    private saveGroupOfValidSessions(): void {
        if (this.#sessionsListSrv.isSomeSessionValid()) {
            const groupOfValidSessionsToSave = this.#sessionsListSrv.getGroupOfValidSessionsToSave();
            const groupOfValidSessionIdsToSave = [...groupOfValidSessionsToSave.keys()];
            this.#sessionsListSrv.setValidSessionsWithSavingInProgress(groupOfValidSessionsToSave);
            this.#seasonTicketSessionsSrv.saveSessionsAssignments(
                this.#$seasonTicketId(),
                groupOfValidSessionIdsToSave
            );
        } else {
            this.#savingValidationsFinish.next();
        }
    }

    private setAssignedSessions(assignedSessions: SeasonTicketSessionsAssignments): void {
        if (assignedSessions.result && assignedSessions.result.length > 0) {
            this.#sessionsListSrv.setAssignedSessions(assignedSessions);
        }
    }

    private setNextValidSessionsToSave(): void {
        this.#sessionsListSrv.setNextValidSessionToSave();
    }

    private saveGroupOfUnassignedSessions(): void {
        if (this.#sessionsListSrv.isSomeSessionToBeUnassigned()) {
            const groupOfUnassignedSessionsToSave = this.#sessionsListSrv.getGroupOfUnassignedSessionsToSave();
            const groupOfUnassignedSessionIdsToSave = [...groupOfUnassignedSessionsToSave.keys()];
            this.#sessionsListSrv.setUnassignedSessionsWithSavingInProgress(groupOfUnassignedSessionsToSave);
            this.#seasonTicketSessionsSrv.unassignSessionAssignment(
                this.#$seasonTicketId(),
                groupOfUnassignedSessionIdsToSave[0]
            );
        } else {
            this.#savingUnAssignmentsFinish.next();
        }
    }

    private setUnassignedSession(unassignedSession: SeasonTicketSessionUnAssignment): void {
        if (unassignedSession?.season_ticket_id !== null) {
            this.#sessionsListSrv.setUnassignedSession(unassignedSession);
        }
    }

    private setNextUnassignedSessionsToSave(): void {
        this.#sessionsListSrv.setNextUnassignedSessionsToSave();

    }

    private setValidSessionsToSave(): void {
        this.#sessionsListSrv.setValidSessionsToSave();
        this.saveGroupOfValidSessions();
    }

    private setUnassignedSessionsToSave(): void {
        this.#sessionsListSrv.setUnassignedSessionsToSave();
        this.saveGroupOfUnassignedSessions();
    }
}

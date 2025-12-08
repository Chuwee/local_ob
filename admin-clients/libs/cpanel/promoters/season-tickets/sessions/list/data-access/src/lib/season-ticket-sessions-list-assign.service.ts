import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketSessionsService,
    SeasonTicketSessionsValidations
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { inject, Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { filter, first, takeUntil } from 'rxjs/operators';
import { SeasonTicketSessionsListService } from './season-ticket-sessions-list.service';
import { SeasonTicketSessionsListState } from './state/season-ticket-sessions-list.state';

@Injectable()
export class SeasonTicketSessionsListAssignService implements OnDestroy {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #listState = inject(SeasonTicketSessionsListState);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #sessionsListSrv = inject(SeasonTicketSessionsListService);

    #validatingFinish = new Subject<void>();
    #reassigningFinish = new Subject<void>();
    #onDestroy = new Subject<void>();
    #seasonTicketId: string;

    readonly validatingFinish$ = this.#validatingFinish.asObservable();
    readonly reassigningFinish$ = this.#reassigningFinish.asObservable();

    constructor() {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                first(value => value !== null)
            )
            .subscribe(seasonTicket => {
                this.#seasonTicketId = seasonTicket.id.toString();
            });
        this.sessionsValidationHandler();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    assign(): void {
        if (this.#sessionsListSrv.isSomeSessionToReassign()) {
            this.#sessionsListSrv.reassignSessions();
            this.#reassigningFinish.next();
        } else if (this.#sessionsListSrv.isSomeSessionToValidate()) {
            this.setSessionsToValidate();
        }
    }

    initAssigningProgress(): void {
        this.#listState.validationInProgress.setInProgress(true);
    }

    finishAssigningProcess(): void {
        this.resetAssigningParams();
        this.#listState.validationInProgress.setInProgress(false);
    }

    isValidationInProgress(): Observable<boolean> {
        return this.#listState.validationInProgress.isInProgress$();
    }

    private sessionsValidationHandler(): void {
        this.#seasonTicketSessionsSrv.getSessionsValidations$()
            .pipe(
                filter(value => value !== null),
                takeUntil(this.#onDestroy)
            ).subscribe(validatedSessions => {
                this.setValidatedSessions(validatedSessions);
                this.setNextSessionsToValidate();
                this.validateGroupOfSessions();
            });
    }

    private resetAssigningParams(): void {
        this.#seasonTicketSessionsSrv.clearSessionsValidations();
        this.#sessionsListSrv.resetValidatedSessionsParams();
        this.#sessionsListSrv.resetReassignedSessionsParams();
    }

    private setValidatedSessions(validatedSessions: SeasonTicketSessionsValidations): void {
        if (
            validatedSessions.result &&
            validatedSessions.result.length > 0 &&
            this.#sessionsListSrv.isSomeGroupOfSessionToValidate()
        ) {
            this.#sessionsListSrv.setValidatedSessions(validatedSessions);
        }
    }

    private setNextSessionsToValidate(): void {
        if (this.#sessionsListSrv.isSomeGroupOfSessionToValidate()) {
            this.#sessionsListSrv.setNextSessionsToValidate();
        }
    }

    private validateGroupOfSessions(): void {
        if (this.#sessionsListSrv.isSomeGroupOfSessionToValidate()) {
            const groupOfSessionsToValidate = this.#sessionsListSrv.getGroupOfSessionsToValidate();
            const groupOfSessionIdsToValidate = [...groupOfSessionsToValidate.keys()];
            this.#sessionsListSrv.setSessionsWithValidationInProgress(groupOfSessionsToValidate);
            this.#seasonTicketSessionsSrv.loadSessionsValidations(
                this.#seasonTicketId,
                groupOfSessionIdsToValidate
            );
        } else {
            this.#validatingFinish.next();
        }
    }

    private setSessionsToValidate(): void {
        this.#sessionsListSrv.setSessionsToValidate();
        this.validateGroupOfSessions();
    }
}

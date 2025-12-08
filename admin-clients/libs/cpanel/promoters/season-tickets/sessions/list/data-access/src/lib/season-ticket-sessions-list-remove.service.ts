import { inject, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { SeasonTicketSessionsListService } from './season-ticket-sessions-list.service';

@Injectable()
export class SeasonTicketSessionsListRemoveService {
    readonly #sessionsListSrv = inject(SeasonTicketSessionsListService);
    readonly #removingValidationFinish = new Subject<void>();
    readonly #removingUnAssigmentFinish = new Subject<void>();

    readonly removingValidationFinish$ = this.#removingValidationFinish.asObservable();
    readonly removingUnAssigmentFinish$ = this.#removingUnAssigmentFinish.asObservable();

    removeStatus(): void {
        if (this.#sessionsListSrv.isSomeSessionToUnAssign()) {
            this.#sessionsListSrv.setSessionsToUnAssign();
            this.#removingUnAssigmentFinish.next();
        } else if (this.#sessionsListSrv.isSomeSessionValidationToRemove()) {
            this.#sessionsListSrv.setSessionValidationsToRemove();
            this.#removingValidationFinish.next();
        }
    }

    finishRemovingStatusProcess(): void {
        this.#sessionsListSrv.deselectAllSessions();
        this.#sessionsListSrv.resetSessionsToUnAssignParams();
        this.#sessionsListSrv.resetUnvalidatedSessionsParams();
    }
}

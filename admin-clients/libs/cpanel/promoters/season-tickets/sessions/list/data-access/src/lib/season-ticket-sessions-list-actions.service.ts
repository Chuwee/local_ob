import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    SeasonTicketSessionsAction
} from './models/season-ticket-sessions-action.enum';
import { SeasonTicketSessionsListState } from './state/season-ticket-sessions-list.state';

@Injectable()
export class SeasonTicketSessionsListActionsService {
    readonly #listState = inject(SeasonTicketSessionsListState);

    setAction(action: SeasonTicketSessionsAction): void {
        this.#listState.sessionsAction.setValue(action);
    }

    getAction$(): Observable<SeasonTicketSessionsAction> {
        return this.#listState.sessionsAction.getValue$();
    }
}

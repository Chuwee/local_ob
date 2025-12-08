import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    SeasonTicketRenewalsAction
} from './models/season-ticket-renewals-action.enum';
import {
    SeasonTicketRenewalsListState
} from './state/season-ticket-renewals-list.state';

@Injectable()
export class SeasonTicketRenewalsListActionsService {

    constructor(
        private _listState: SeasonTicketRenewalsListState
    ) {
    }

    setAction(action: SeasonTicketRenewalsAction): void {
        this._listState.setRenewalsAction(action);
    }

    getAction$(): Observable<SeasonTicketRenewalsAction> {
        return this._listState.getRenewalsAction$();
    }
}

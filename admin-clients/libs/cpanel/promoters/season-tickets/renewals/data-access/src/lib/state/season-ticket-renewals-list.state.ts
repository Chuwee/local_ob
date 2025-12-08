import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { SeasonTicketRenewalsAction } from '../models/season-ticket-renewals-action.enum';
import { VmSeasonTicketRenewal } from '../models/vm-season-ticket-renewal.model';

@Injectable()
export class SeasonTicketRenewalsListState {
    private readonly _renewals = new BaseStateProp<VmSeasonTicketRenewal[]>();
    readonly getRenewalsList$ = this._renewals.getValueFunction();
    readonly setRenewalsList = this._renewals.setValueFunction();

    private readonly _renewalsAction = new BaseStateProp<SeasonTicketRenewalsAction>(SeasonTicketRenewalsAction.none);
    readonly getRenewalsAction$ = this._renewalsAction.getValueFunction();
    readonly setRenewalsAction = this._renewalsAction.setValueFunction();
}

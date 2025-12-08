import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';

@Injectable()
export class NewSeasonTicketRenewalDialogState {

    private readonly _renewalCandidateIdState = new BaseStateProp<number>();

    readonly getRenewalCandidateId$ = this._renewalCandidateIdState.getValueFunction();
    readonly setRenewalCandidateId = this._renewalCandidateIdState.setValueFunction();

    private readonly _renewalCandidateIdRatesState = new BaseStateProp<number>();

    readonly getRenewalCandidateIdRates$ = this._renewalCandidateIdRatesState.getValueFunction();
    readonly setRenewalCandidateIdRates = this._renewalCandidateIdRatesState.setValueFunction();

    private readonly _seasonTicketIdRatesState = new BaseStateProp<number>();

    readonly getSeasonTicketIdRates$ = this._seasonTicketIdRatesState.getValueFunction();
    readonly setSeasonTicketIdRates = this._seasonTicketIdRatesState.setValueFunction();
}

import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Payout } from '../models/payout.model';

@Injectable({
    providedIn: 'root'
})
export class PayoutsState {
    readonly payoutsList = new StateProperty<ListResponse<Payout>>();
    readonly exportPayouts = new StateProperty<boolean>();
}

import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Transition } from '../models/currency-transition.model';
import { GetCurrenciesResponse } from '../models/get-currencies-response.model';

@Injectable()
export class CurrenciesState {
    readonly list = new StateProperty<GetCurrenciesResponse>();
    readonly transitions = new StateProperty<Transition[]>();
}

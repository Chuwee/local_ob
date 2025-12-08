import { StateProperty } from '@OneboxTM/utils-state';
import { Currency } from '@admin-clients/shared-utility-models';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class CurrenciesState {
    readonly currencies = new StateProperty<Currency[]>();
    readonly operatorCurrencies = new StateProperty<Currency[]>();
}

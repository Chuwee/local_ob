import { StateProperty } from '@OneboxTM/utils-state';
import { Currency } from '@admin-clients/shared-utility-models';
import { Injectable } from '@angular/core';
import { OperatorTax } from '../models/operator-tax.model';
import { Operator } from '../models/operator.model';
import { GetOperatorsResponse } from '../models/operators.model';

@Injectable({
    providedIn: 'root'
})
export class OperatorsState {
    readonly operator = new StateProperty<Operator>();
    readonly operatorsList = new StateProperty<GetOperatorsResponse>();
    readonly operatorCurrencies = new StateProperty<Currency[]>();
    readonly operatorTaxes = new StateProperty<OperatorTax[]>();
    readonly savingOperatorTaxes = new StateProperty(); // to control tax saves (posts or puts)
}

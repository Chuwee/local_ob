import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { finalize, map, Observable } from 'rxjs';
import { OperatorsApi } from './api/operators.api';
import { PostOperatorTaxRequest, PutOperatorTaxRequest } from './models/operator-tax.model';
import { PostOperator, PutOperator, PutOperatorCurrencies } from './models/operator.model';
import { GetOperatorsRequest } from './models/operators.model';
import { OperatorsState } from './state/operators.state';

@Injectable({ providedIn: 'root' })
export class OperatorsService {
    private readonly _api = inject(OperatorsApi);
    private readonly _state = inject(OperatorsState);

    readonly operators = Object.freeze({
        load: (request: GetOperatorsRequest) =>
            StateManager.load(this._state.operatorsList, this._api.getOperators(request).pipe(mapMetadata())),
        getData$: () => this._state.operatorsList.getValue$().pipe(getListData()),
        getMetadata$: () => this._state.operatorsList.getValue$().pipe(getMetadata()),
        loading$: () => this._state.operatorsList.isInProgress$(),
        clear: () => this._state.operatorsList.setValue(null)
    });

    readonly operator = Object.freeze({
        create: (operator: PostOperator): Observable<{ id: number; password: string }> => StateManager.inProgress(
            this._state.operator,
            this._api.postOperator(operator)
        ),
        load: (id: number): void => StateManager.load(
            this._state.operator,
            this._api.getOperator(id)
        ),
        update: (id: number, body: PutOperator) => StateManager.inProgress(
            this._state.operator,
            this._api.putOperator(id, body)
        ),
        get$: () => this._state.operator.getValue$(),
        error$: () => this._state.operator.getError$(),
        loading$: () => this._state.operator.isInProgress$(),
        clear: () => this._state.operator.setValue(null),
        isMultiCurrency$: () => this._state.operator.getValue$().pipe(map(operator => !!operator?.currencies))
    });

    readonly operatorCurrencies = Object.freeze({
        update: (id: number, body: PutOperatorCurrencies) => StateManager.inProgress(
            this._state.operatorCurrencies,
            this._api.putOperatorCurrencies(id, body)
        )
    });

    readonly operatorTaxes = Object.freeze({
        load: (id: number) => StateManager.load(this._state.operatorTaxes, this._api.getOperatorTaxes(id)),
        get$: () => this._state.operatorTaxes.getValue$(),
        loading$: () => this._state.operatorTaxes.isInProgress$(),
        saving$: () => this._state.savingOperatorTaxes.isInProgress$(),
        clear: () => this._state.operatorTaxes.setValue(null),
        create: (id: number, tax: PostOperatorTaxRequest) => {
            this._state.savingOperatorTaxes.setInProgress(true);
            return this._api.postOperatorTax(id, tax).pipe(finalize(() => this._state.savingOperatorTaxes.setInProgress(false)));
        },
        update: (id: number, taxes: PutOperatorTaxRequest[]) => {
            this._state.savingOperatorTaxes.setInProgress(true);
            return this._api.putOperatorTaxes(id, taxes).pipe(finalize(() => this._state.savingOperatorTaxes.setInProgress(false)));
        }
    });
}

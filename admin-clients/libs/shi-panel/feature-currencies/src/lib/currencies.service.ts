import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { filter } from 'rxjs';
import { CurrenciesApi } from './currencies.api';
import { Currency } from './models/currency.model';
import { GetCurrenciesRequest } from './models/get-currencies-request.model';
import { PutCurrency } from './models/put-currency.model';
import { CurrenciesState } from './state/currencies.state';

@Injectable()
export class CurrenciesService {
    private readonly _currenciesApi = inject(CurrenciesApi);
    private readonly _currenciesState = inject(CurrenciesState);

    readonly list = Object.freeze({
        load: (request: GetCurrenciesRequest) => StateManager.load(
            this._currenciesState.list, this._currenciesApi.getCurrencies(request).pipe(mapMetadata())
        ),
        getData$: () => this._currenciesState.list.getValue$().pipe(filter(Boolean), getListData()),
        getMetadata$: () => this._currenciesState.list.getValue$().pipe(getMetadata()),
        loading$: () => this._currenciesState.list.isInProgress$(),
        updateCurrency: (exchangeRate: PutCurrency) => StateManager.inProgress(
            this._currenciesState.list, this._currenciesApi.updateCurrency(exchangeRate)
        )
    });

    readonly transitions = Object.freeze({
        load: (row: Currency) => StateManager.load(this._currenciesState.transitions, this._currenciesApi.getTransitions(row)),
        loading$: () => this._currenciesState.transitions.isInProgress$(),
        get$: () => this._currenciesState.transitions.getValue$()
    });
}

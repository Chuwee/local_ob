import { StateManager } from '@OneboxTM/utils-state';
import { Currency } from '@admin-clients/shared-utility-models';
import { inject, Injectable } from '@angular/core';
import { map } from 'rxjs';
import { CurrenciesApi } from './api/currencies.api';
import { CurrenciesState } from './state/currencies.state';

@Injectable({
    providedIn: 'root'
})
export class CurrenciesService {
    #api = inject(CurrenciesApi);
    #state = inject(CurrenciesState);

    readonly currencies = Object.freeze({
        load: (): void => StateManager.loadIfNull(
            this.#state.currencies,
            this.#api.getCurrencies()
        ),
        get$: () => this.#state.currencies.getValue$(),
        loading$: () => this.#state.currencies.isInProgress$()
    });

    readonly operatorCurrencies = Object.freeze({
        load: (operatorId: number) => StateManager.load(
            this.#state.operatorCurrencies,
            this.#api.getOperatorCurrencies(operatorId).pipe(map(currency => currency?.selected || []))
        ),
        setValue: (data: Currency[]) => this.#state.operatorCurrencies.setValue(data),
        get$: () => this.#state.operatorCurrencies.getValue$(),
        loading$: () => this.#state.operatorCurrencies.isInProgress$(),
        clear: () => this.#state.operatorCurrencies.setValue(null)
    });
}

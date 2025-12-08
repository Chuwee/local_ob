import { buildHttpParams } from '@OneboxTM/utils-http';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Transition } from './models/currency-transition.model';
import { Currency } from './models/currency.model';
import { GetCurrenciesRequest } from './models/get-currencies-request.model';
import { GetCurrenciesResponse } from './models/get-currencies-response.model';
import { PutCurrency } from './models/put-currency.model';

@Injectable()
export class CurrenciesApi {
    private readonly BASE_CURRENCIES_URL = '/api/shi-mgmt-api/v1/exchange-rates';
    private readonly SUPPLIER_CURRENCIES_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly _http = inject(HttpClient);

    getCurrencies(request: GetCurrenciesRequest): Observable<GetCurrenciesResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetCurrenciesResponse>(this.BASE_CURRENCIES_URL, { params });
    }

    updateCurrency(exchangeRate: PutCurrency): Observable<void> {
        return this._http.put<void>(
            `${this.SUPPLIER_CURRENCIES_URL}/${exchangeRate.supplier}/exchange-rates/${exchangeRate.source}${exchangeRate.target}`,
            { rate: exchangeRate.rate }
        );
    }

    getTransitions(row: Currency): Observable<Transition[]> {
        return this._http.get<Transition[]>(
            `${this.SUPPLIER_CURRENCIES_URL}/${row.supplier}/exchange-rates/${row.source}${row.target}/transitions`
        );
    }

}

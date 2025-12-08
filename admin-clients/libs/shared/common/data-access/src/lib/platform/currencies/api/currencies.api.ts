import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Currency, OperatorCurrency } from '@admin-clients/shared-utility-models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class CurrenciesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CURRENCIES_API = `${this.BASE_API}/mgmt-api/v1/currencies`;

    private readonly _http = inject(HttpClient);

    getCurrencies(): Observable<Currency[]> {
        return this._http.get<Currency[]>(`${this.CURRENCIES_API}`);
    }

    getOperatorCurrencies(id: number): Observable<OperatorCurrency> {
        return this._http.get<OperatorCurrency>(`${this.BASE_API}/mgmt-api/v1/operators/${id}/currencies`);
    }
}

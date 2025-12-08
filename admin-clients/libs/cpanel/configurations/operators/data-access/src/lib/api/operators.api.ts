import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import { Currency } from '@admin-clients/shared-utility-models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { OperatorTax, PostOperatorTaxRequest, PutOperatorTaxRequest } from '../models/operator-tax.model';
import { Operator, PostOperator, PutOperator, PutOperatorCurrencies } from '../models/operator.model';
import { GetOperatorsRequest, GetOperatorsResponse } from '../models/operators.model';

@Injectable({
    providedIn: 'root'
})
export class OperatorsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly OPERATORS_API = `${this.BASE_API}/mgmt-api/v1/operators`;

    private readonly _http = inject(HttpClient);

    getOperators(request: GetOperatorsRequest): Observable<GetOperatorsResponse> {
        const params = buildHttpParams(request);

        return this._http.get<GetOperatorsResponse>(this.OPERATORS_API, { params });
    }

    getOperator(id: number): Observable<Operator> {
        return this._http.get<Operator>(`${this.OPERATORS_API}/${id}`);
    }

    postOperator(operator: PostOperator): Observable<{ id: number; password: string }> {
        return this._http.post<{ id: number; password: string }>(this.OPERATORS_API, operator);
    }

    putOperator(id: number, operator: PutOperator): Observable<void> {
        return this._http.put<void>(`${this.OPERATORS_API}/${id}`, operator);
    }

    getOperatorCurrencies(id: number): Observable<Currency[]> {
        return this._http.get<Currency[]>(`${this.OPERATORS_API}/${id}/currencies`);
    }

    putOperatorCurrencies(id: number, putOperCurrencies: PutOperatorCurrencies): Observable<void> {
        return this._http.put<void>(`${this.OPERATORS_API}/${id}/currencies`, putOperCurrencies);
    }

    getOperatorTaxes(id: number): Observable<OperatorTax[]> {
        return this._http.get<OperatorTax[]>(`${this.OPERATORS_API}/${id}/taxes`);
    }

    postOperatorTax(id: number, tax: PostOperatorTaxRequest): Observable<Id> {
        return this._http.post<Id>(`${this.OPERATORS_API}/${id}/taxes`, tax);
    }

    putOperatorTaxes(id: number, taxes: PutOperatorTaxRequest[]): Observable<void> {
        return this._http.put<void>(`${this.OPERATORS_API}/${id}/taxes`, taxes);
    }
}

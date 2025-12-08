import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { GetFilterOptionsResponse } from '@admin-clients/shi-panel/utility-models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSalesRequest } from './models/get-sales-request.model';
import { GetSalesResponse } from './models/get-sales-response.model';
import { Transition } from './models/sale-transition.model';
import { Sale } from './models/sales.model';

@Injectable()
export class SalesApi {
    private readonly BASE_SALES_URL = '/api/shi-mgmt-api/v1/sales';
    private readonly _http = inject(HttpClient);

    getSales(request: GetSalesRequest): Observable<GetSalesResponse> {
        const params = this.#buildGetSalesParams(request);

        return this._http.get<GetSalesResponse>(this.BASE_SALES_URL, { params });
    }

    getSaleDetails(id: number): Observable<Sale> {
        return this._http.get<Sale>(`${this.BASE_SALES_URL}/${id}`);
    }

    exportSalesList(request: GetSalesRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.#buildGetSalesParams(request);
        return this._http.post<ExportResponse>(`${this.BASE_SALES_URL}/exports`, body, {
            params
        });
    }

    exportSalesDailyList(): Observable<void> {
        return this._http.post<void>(`${this.BASE_SALES_URL}/exports/daily`, {});
    }

    getSaleTransitions(id: number): Observable<Transition[]> {
        return this._http.get<Transition[]>(`${this.BASE_SALES_URL}/${id}/transitions`);
    }

    relaunchSale(id: number): Observable<void> {
        return this._http.post<void>(`${this.BASE_SALES_URL}/${id}/confirm`, {});
    }

    relaunchFulfill(id: number): Observable<void> {
        return this._http.post<void>(`${this.BASE_SALES_URL}/${id}/fulfill`, {});
    }

    getDeliveryMethods(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_SALES_URL}/filters/delivery-method`);
    }

    getLastErrors(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_SALES_URL}/filters/last-error`);
    }

    getCountries(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_SALES_URL}/filters/country-code`);
    }

    getCurrencies(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_SALES_URL}/filters/currency`);
    }

    getTaxonomies(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_SALES_URL}/filters/taxonomies`);
    }

    #buildGetSalesParams(request: GetSalesRequest): HttpParams {
        const {
            daysToEventLte,
            daysToEventGte,
            sale_date_from: saleDateFrom,
            sale_date_to: saleDateTo,
            update_date_from: updateDateFrom,
            update_date_to: updateDateTo,
            inhand_date_from: inhandDateFrom,
            inhand_date_to: inhandDateTo,
            ...restOfRequest
        } = request;
        return buildHttpParams({
            ...restOfRequest,
            created: getRangeParam(saleDateFrom, saleDateTo),
            last_update: getRangeParam(updateDateFrom, updateDateTo),
            inhand_date: getRangeParam(inhandDateFrom, inhandDateTo),
            days_to_event: getRangeParam(daysToEventGte?.toString(), daysToEventLte?.toString())
        });
    }
}

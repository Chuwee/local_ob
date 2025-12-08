
import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ErrorDashboardData, ErrorDashboardRequest } from '../models/dashboard-error.model';

@Injectable()
export class ErrorDashboardApi {
    readonly #BASE_URL = '/api/shi-mgmt-api/v1/sales';
    readonly #http = inject(HttpClient);

    getErrorDashboardData(request: ErrorDashboardRequest): Observable<ErrorDashboardData> {
        const params = this.#buildErrorDashboardHttpParams(request);
        return this.#http.get<ErrorDashboardData>(`${this.#BASE_URL}/aggregated/error-responsible`, { params });
    }

    exportErrorRates(request: ErrorDashboardRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.#buildErrorDashboardHttpParams(request);
        return this.#http.post<ExportResponse>(`${this.#BASE_URL}/responsible/exports`, body, {
            params
        });
    }

    #buildErrorDashboardHttpParams(request: ErrorDashboardRequest): HttpParams {
        const {
            daysToEventLte,
            daysToEventGte,
            date_from: dateFrom,
            date_to: dateTo,
            ...restOfRequest
        } = request;
        return buildHttpParams({
            ...restOfRequest,
            created: getRangeParam(dateFrom, dateTo),
            days_to_event: getRangeParam(daysToEventGte?.toString(), daysToEventLte?.toString())
        });
    }
}

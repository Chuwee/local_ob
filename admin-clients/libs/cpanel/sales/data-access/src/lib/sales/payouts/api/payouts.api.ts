import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterRequest } from '../../models/get-filter-request.model';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetPayoutsRequest } from '../models/get-payouts-request.model';
import { Payout, PayoutStatus } from '../models/payout.model';

@Injectable({
    providedIn: 'root'
})
export class PayoutsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_URL = `${this.BASE_API}/orders-mgmt-api/v1/secondary-market-payouts`;

    private readonly _http = inject(HttpClient);

    getPayouts(request: GetPayoutsRequest): Observable<ListResponse<Payout>> {
        const params = buildHttpParams(request);
        return this._http.get<ListResponse<Payout>>(this.BASE_URL, {
            params
        });
    }

    exportPayouts(request: GetPayoutsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(this.BASE_URL + '/exports', body, {
            params
        });
    }

    getFilterOptions$(filterName: string, request: GetFilterRequest): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetFilterResponse>(this.BASE_URL + '/filters/' + filterName, { params });
    }

    putPayoutStatus(payoutId: string, status: PayoutStatus): Observable<void> {
        return this._http.put<void>(`${this.BASE_URL}/${payoutId}/status`, { payout_status: status });
    }
}

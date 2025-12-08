import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetPacksSaleRequestsReq, PackSaleRequestListElem, PackSaleRequestStatus } from '../models/pack-sale-request.model';

@Injectable()
export class PacksSaleRequestsApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #PACKS_SALE_REQUESTS_API = `${this.#BASE_API}/mgmt-api/v1/packs-sale-requests`;

    readonly #http = inject(HttpClient);

    getPackSaleRequests(request: GetPacksSaleRequestsReq): Observable<ListResponse<PackSaleRequestListElem>> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            status: request.status,
            date: getRangeParam(request.startDate, request.endDate)
        });
        return this.#http.get<ListResponse<PackSaleRequestListElem>>(this.#PACKS_SALE_REQUESTS_API, { params });
    }

    putPackSaleRequestStatus(
        packSaleRequestId: number, status: PackSaleRequestStatus
    ): Observable<{ status: PackSaleRequestStatus }> {
        return this.#http.put<{ status: PackSaleRequestStatus }>(
            `${this.#PACKS_SALE_REQUESTS_API}/${packSaleRequestId}/status`, { status }
        );
    }
}

import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetListingsRequest } from './models/get-listings-request.model';
import { GetListingsResponse } from './models/get-listings-response.model';
import { Transition } from './models/listing-transition.model';
import { Listing } from './models/listings.model';

@Injectable()
export class ListingsApi {
    private readonly BASE_LISTINGS_URL = '/api/shi-mgmt-api/v1/listings';
    private readonly _http = inject(HttpClient);

    getListings(request: GetListingsRequest): Observable<GetListingsResponse> {
        const params = this.buildGetListingsParams(request);
        return this._http.get<GetListingsResponse>(this.BASE_LISTINGS_URL, { params });
    }

    getListingDetails(code: string): Observable<Listing> {
        return this._http.get<Listing>(`${this.BASE_LISTINGS_URL}/${code}`);
    }

    exportListingsList(request: GetListingsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.buildGetListingsParams(request);

        return this._http.post<ExportResponse>(`${this.BASE_LISTINGS_URL}/exports`, body, {
            params
        });
    }

    getListingTransitions(id: number): Observable<Transition[]> {
        return this._http.get<Transition[]>(`${this.BASE_LISTINGS_URL}/${id}/transitions`);
    }

    updateListingBlacklist(listingCode: string, blacklisted: boolean): Observable<void> {
        return this._http.put<void>(`${this.BASE_LISTINGS_URL}/blacklisted`, { codes: [listingCode], blacklisted });
    }

    bulkManageBlacklist(blacklisted: boolean, event_ids: number[], codes: string[]): Observable<void> {
        return this._http.put<void>(`${this.BASE_LISTINGS_URL}/blacklisted`, { blacklisted, event_ids, codes });
    }

    private buildGetListingsParams(request: GetListingsRequest): HttpParams {
        return buildHttpParams({
            ...request,
            created: getRangeParam(request.import_date_from, request.import_date_to),
            last_update: getRangeParam(request.update_date_from, request.update_date_to)
        });
    }
}

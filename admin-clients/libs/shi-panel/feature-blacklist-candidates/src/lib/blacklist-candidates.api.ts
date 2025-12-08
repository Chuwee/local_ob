import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import {
    GetBlacklistedMatchingsRequest, GetMatchingCountriesResponse, GetMatchingsRequest, GetMatchingsResponse, SupplierName
} from '@admin-clients/shi-panel/utility-models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class BlacklistedMatchingsApi {
    private readonly BASE_SUPPLIERS_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly EVENT_MATCHINGS_URL = 'event-matchings';
    private readonly BLACKLISTED_URL = 'blacklisted';
    private readonly _http = inject(HttpClient);

    getBlacklistedMatchings(supplier: string, request: GetBlacklistedMatchingsRequest): Observable<GetMatchingsResponse> {
        const params = this.buildGetMatchingsParams(request);
        return this._http.get<GetMatchingsResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/${this.BLACKLISTED_URL}`, { params }
        );
    }

    getCountries(supplier: string): Observable<GetMatchingCountriesResponse> {
        return this._http.get<GetMatchingCountriesResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/matching-configuration/filters/countries`
        );
    }

    deleteBlacklistedMatching(supplier: SupplierName, id: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/${this.BLACKLISTED_URL}/${id}`);
    }

    private buildGetMatchingsParams(request: GetMatchingsRequest): HttpParams {
        return buildHttpParams({
            ...request,
            date: getRangeParam(request.date, request.date_end)
        });
    }
}

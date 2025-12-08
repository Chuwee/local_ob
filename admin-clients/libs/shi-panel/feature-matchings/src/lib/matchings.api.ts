import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import {
    GetMatchingCountriesResponse, GetMatchingsRequest, GetMatchingsResponse, Matching, SupplierName
} from '@admin-clients/shi-panel/utility-models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetMatcherStatusResponse } from './models/get-matcher-status-response.model';
import { GetTaxonomiesResponse } from './models/get-taxonomies-response.model';
import { PostBlacklistedMatchingResponse } from './models/post-blacklisted-matching-response.model';

@Injectable()
export class MatchingsApi {
    private readonly BASE_SUPPLIERS_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly EVENT_MATCHINGS_URL = 'event-matchings';
    private readonly BLACKLISTED_URL = 'blacklisted';
    private readonly _http = inject(HttpClient);

    getMatchings(supplier: string, request: GetMatchingsRequest): Observable<GetMatchingsResponse> {
        const params = this.buildGetMatchingsParams(request);
        return this._http.get<GetMatchingsResponse>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}`, { params });
    }

    getCountries(supplier: string): Observable<GetMatchingCountriesResponse> {
        return this._http.get<GetMatchingCountriesResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/matching-configuration/filters/countries`
        );
    }

    createMatching(supplier: string, id: string): Observable<void> {
        return this._http.put<void>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/${id}/mapped`, {});
    }

    createBlacklistedMatchings(supplier: SupplierName, matchings: Matching[]): Observable<PostBlacklistedMatchingResponse> {
        return this._http.post<PostBlacklistedMatchingResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/${this.BLACKLISTED_URL}`, { matchings }
        );
    }

    exportMatchingsList(supplier: string, request: GetMatchingsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.buildGetMatchingsParams(request);
        return this._http.post<ExportResponse>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/exports`, body, {
            params
        });
    }

    launchMatcher(supplier: string, countries: string[], taxonomies: string[]): Observable<void> {
        return this._http.post<void>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}`, { countries, taxonomies });
    }

    createMatchingsFromCandidates(supplier: string, ids: string[]): Observable<void> {
        return this._http.put<void>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/mapped/bulk`, { ids });
    }

    getMatchingDetails(supplier: string, id: number): Observable<Matching> {
        return this._http.get<Matching>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/${id}`);
    }

    getMatcherStatus(supplier: string): Observable<GetMatcherStatusResponse> {
        return this._http.get<GetMatcherStatusResponse>(`${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/status`);
    }

    getShiTaxonomies(supplier: string): Observable<GetTaxonomiesResponse> {
        return this._http.get<GetTaxonomiesResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/filters/shi_taxonomies`
        );
    }

    getSupplierTaxonomies(supplier: string): Observable<GetTaxonomiesResponse> {
        return this._http.get<GetTaxonomiesResponse>(
            `${this.BASE_SUPPLIERS_URL}/${supplier}/${this.EVENT_MATCHINGS_URL}/filters/supplier_taxonomies`
        );
    }

    private buildGetMatchingsParams(request: GetMatchingsRequest): HttpParams {
        return buildHttpParams({
            ...request,
            date: getRangeParam(request.date, request.date_end)
        });
    }
}

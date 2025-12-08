import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { GetFavoritesResponse, GetFilterOptionsResponse } from '@admin-clients/shi-panel/utility-models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetMappingsRequest } from './models/get-mappings-request.model';
import { GetMappingsResponse } from './models/get-mappings-response.model';
import { MappingToCreate, PutMappingsRequest } from './models/mapping.model';
import { PostMappingResponse } from './models/post-mapping-response.model';
import { PostMapping } from './models/post-mapping.model';
import { PutMapping } from './models/put-mapping.model';

@Injectable()
export class MappingsApi {
    private readonly BASE_MAPPINGS_URL = '/api/shi-mgmt-api/v1/event-mappings';
    private readonly _http = inject(HttpClient);

    getMappings(request: GetMappingsRequest): Observable<GetMappingsResponse> {
        const params = this.buildGetMappingsParams(request);
        return this._http.get<GetMappingsResponse>(this.BASE_MAPPINGS_URL, { params });
    }

    exportMappingsList(request: GetMappingsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.buildGetMappingsParams(request);
        return this._http.post<ExportResponse>(`${this.BASE_MAPPINGS_URL}/exports`, body, {
            params
        });
    }

    postMapping(mapping: PostMapping): Observable<PostMappingResponse> {
        return this._http.post<PostMappingResponse>(`${this.BASE_MAPPINGS_URL}`, mapping);
    }

    putMapping(id: number, mapping: PutMapping): Observable<void> {
        return this._http.put<void>(`${this.BASE_MAPPINGS_URL}/${id}`, mapping);
    }

    putMappingsFavorites(mappings: PutMappingsRequest): Observable<void> {
        return this._http.put<void>(`${this.BASE_MAPPINGS_URL}/favorites`, mappings);
    }

    deleteMapping(code: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_MAPPINGS_URL}/${code}`);
    }

    getCountries(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_MAPPINGS_URL}/filters/country-code`);
    }

    getTaxonomies(): Observable<GetFilterOptionsResponse> {
        return this._http.get<GetFilterOptionsResponse>(`${this.BASE_MAPPINGS_URL}/filters/taxonomies`);
    }

    cleanListings(code: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_MAPPINGS_URL}/${code}/missing-listings`);
    }

    bulkCleanListings(codes: number[]): Observable<void> {
        return this._http.post<void>(`${this.BASE_MAPPINGS_URL}/missing-listings`, { event_ids: codes });
    }

    bulkCreateMapping(mappingsToCreate: MappingToCreate[]): Observable<void> {
        return this._http.post<void>(`${this.BASE_MAPPINGS_URL}/bulk`, { mappings: mappingsToCreate });
    }

    bulkUpdateStatus(codes: string[], status: string): Observable<void> {
        return this._http.put<void>(`${this.BASE_MAPPINGS_URL}`, { codes, status });
    }

    getFavorites(supplier: string): Observable<GetFavoritesResponse> {
        return this._http.get<GetFavoritesResponse>(`${this.BASE_MAPPINGS_URL}/favorites/available/${supplier}`);
    }

    private buildGetMappingsParams(request: GetMappingsRequest): HttpParams {
        return buildHttpParams({
            ...request,
            date: getRangeParam(request.event_date_from, request.event_date_to),
            created: getRangeParam(request.create_date_from, request.create_date_to),
            updated: getRangeParam(request.update_date_from, request.update_date_to)
        });
    }
}

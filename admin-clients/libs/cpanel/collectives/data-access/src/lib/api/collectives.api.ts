import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CollectiveEntity } from '../models/collective-entities.model';
import { CollectiveExternalValidator } from '../models/collective-external-validator.model';
import { CollectiveStatus } from '../models/collective-status.enum';
import { Collective } from '../models/collective.model';
import { GetCollectiveCodesResponse } from '../models/get-collective-codes-response.model';
import { GetCollectivesRequest } from '../models/get-collectives-request.model';
import { GetCollectivesResponse } from '../models/get-collectives-response.model';
import { PostCollectiveCode } from '../models/post-collective-code.model';
import { PostCollective } from '../models/post-collective.model';
import { PutCollectiveCode } from '../models/put-collective-code.model';
import { PutCollectiveExternalValidatorProperties } from '../models/put-collective-external-validator-properties.model';
import { PutCollective } from '../models/put-collective.model';

@Injectable({
    providedIn: 'root'
})
export class CollectivesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly COLLECTIVES_API = `${this.BASE_API}/mgmt-api/v1/collectives`;

    private readonly _http = inject(HttpClient);

    getCollectives(request: GetCollectivesRequest): Observable<GetCollectivesResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetCollectivesResponse>(`${this.COLLECTIVES_API}`, { params });
    }

    getCollective(collectiveId: number): Observable<Collective> {
        return this._http.get<Collective>(`${this.COLLECTIVES_API}/${collectiveId}`);
    }

    postCollective(collective: PostCollective): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.COLLECTIVES_API}`, collective);
    }

    putCollective(collectiveId: number, collective: PutCollective): Observable<void> {
        return this._http.put<void>(`${this.COLLECTIVES_API}/${collectiveId}`, collective);
    }

    putCollectiveStatus(collectiveId: number, status: CollectiveStatus): Observable<void> {
        return this._http.put<void>(`${this.COLLECTIVES_API}/${collectiveId}/status`, { status });
    }

    deleteCollective(collectiveId: number): Observable<void> {
        return this._http.delete<void>(`${this.COLLECTIVES_API}/${collectiveId}`);
    }

    getCollectiveEntities(collectiveId: number): Observable<CollectiveEntity[]> {
        return this._http.get<CollectiveEntity[]>(`${this.COLLECTIVES_API}/${collectiveId}/entities`);
    }

    putCollectiveEntities(collectiveId: number, entities: number[]): Observable<void> {
        return this._http.put<void>(`${this.COLLECTIVES_API}/${collectiveId}/entities`, { entities });
    }

    getCollectiveExternalValidators(): Observable<CollectiveExternalValidator[]> {
        return this._http.get<CollectiveExternalValidator[]>(`${this.COLLECTIVES_API}/external-validators`);
    }

    // External validator user and password
    putCollectiveExternalValidatorProperties(
        collectiveId: number, externalValidatorProperties: PutCollectiveExternalValidatorProperties
    ): Observable<void> {
        return this._http.put<void>(`${this.COLLECTIVES_API}/${collectiveId}/external-validators`, externalValidatorProperties);
    }

    getCollectiveCodes(collectiveId: number, request: PageableFilter): Observable<GetCollectiveCodesResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetCollectiveCodesResponse>(`${this.COLLECTIVES_API}/${collectiveId}/codes`, { params });
    }

    postCollectiveCode(collectiveId: number, collectiveCodeData: PostCollectiveCode): Observable<void> {
        return this._http.post<void>(`${this.COLLECTIVES_API}/${collectiveId}/codes`, collectiveCodeData);
    }

    postCollectiveCodes(collectiveId: number, collectiveCodeData: PostCollectiveCode[]): Observable<void> {
        return this._http.post<void>(`${this.COLLECTIVES_API}/${collectiveId}/codes/bulk`, collectiveCodeData);
    }

    putCollectiveCodes(
        collectiveId: number, collectiveCodes: string[], collectiveCodeData: PutCollectiveCode, q: string = null
    ): Observable<void> {
        const params = buildHttpParams({ q });
        return this._http.put<void>(`${this.COLLECTIVES_API}/${collectiveId}/codes/bulk-unified`,
            { data: collectiveCodeData, codes: collectiveCodes }, { params });
    }

    deleteCollectiveCode(collectiveId: number, collectiveCode: string): Observable<void> {
        return this._http.delete<void>(`${this.COLLECTIVES_API}/${collectiveId}/codes/${collectiveCode}`);
    }

    deleteCollectiveCodes(collectiveId: number, codes: string[], q: string = null): Observable<void> {
        const params = buildHttpParams({ q });
        return this._http.post<void>(`${this.COLLECTIVES_API}/${collectiveId}/codes/bulk-delete`, { codes }, { params });
    }

    exportCollectiveCodes(collectiveId: number, request: GetCollectivesRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(`${this.COLLECTIVES_API}/${collectiveId}/codes/exports`, body, {
            params
        });
    }
}

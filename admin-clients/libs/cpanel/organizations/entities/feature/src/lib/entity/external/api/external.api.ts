import { buildHttpParams } from '@OneboxTM/utils-http';
import { ExternalInvetories, InventoryProviders } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ExternalEntityConfiguration } from '../models/configuration.model';
import { ExternalCapacity } from '../models/external-capacity.model';
import { ExternalPeriodicities } from '../models/external-periodicities.model';
import { ExternalRoles } from '../models/external-roles.model';

@Injectable({
    providedIn: 'root'
})
export class ExternalEntitiesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly ENTITIES_API = `${this.BASE_API}/mgmt-api/v1/entities`;

    private readonly _http = inject(HttpClient);

    getConfiguration(id: number): Observable<ExternalEntityConfiguration> {
        return this._http.get<ExternalEntityConfiguration>(`${this.ENTITIES_API}/${id}/external-configurations`);
    }

    putConfiguration(id: number, payload: Partial<ExternalEntityConfiguration>): Observable<void> {
        return this._http.put<void>(`${this.ENTITIES_API}/${id}/external-configurations`, payload);
    }

    linkClubCode(id: number, code: string): Observable<void> {
        return this._http.post<void>(`${this.ENTITIES_API}/${id}/external-configurations/link`, { club_code: code });
    }

    unlinkClubCode(id: number): Observable<void> {
        return this._http.post<void>(`${this.ENTITIES_API}/${id}/external-configurations/unlink`, null);
    }

    getClubCodes(): Observable<string[]> {
        return this._http.get<string[]>(`${this.ENTITIES_API}/external-configurations/codes/available`);
    }

    getExternalCapacities(entityId: number): Observable<ExternalCapacity[]> {
        return this._http.get<ExternalCapacity[]>(`${this.ENTITIES_API}/${entityId}/external-capacities`);
    }

    importExternalCapacity(entityId: number, id: number): Observable<void> {
        return this._http.post<void>(`${this.ENTITIES_API}/${entityId}/external-capacities/${id}`, null);
    }

    deleteExternalCapacity(entityId: number, id: number): Observable<void> {
        return this._http.delete<void>(`${this.ENTITIES_API}/${entityId}/external-capacities/${id}`);
    }

    postRefreshExternalCapacity(entityId: number, id: number): Observable<void> {
        return this._http.post<void>(`${this.ENTITIES_API}/${entityId}/external-capacities/${id}/refresh`, null);
    }

    postMappingExternalCapacity(entityId: number, id: number): Observable<void> {
        return this._http.post<void>(`${this.ENTITIES_API}/${entityId}/external-capacities/${id}/mapping`, null);
    }

    getExternalPeriodicities(entityId: number): Observable<ExternalPeriodicities[]> {
        return this._http.get<ExternalPeriodicities[]>(`${this.ENTITIES_API}/${entityId}/external-periodicities`);
    }

    getExternalRoles(entityId: number): Observable<ExternalRoles[]> {
        return this._http.get<ExternalRoles[]>(`${this.ENTITIES_API}/${entityId}/external-roles`);
    }

    getExternalTerms(entityId: number): Observable<object[]> {
        return this._http.get<object[]>(`${this.ENTITIES_API}/${entityId}/external-terms`);
    }

    getInventoryProviders(entityId: number): Observable<InventoryProviders> {
        return this._http.get<InventoryProviders>(`${this.ENTITIES_API}/${entityId}/inventory-providers`);
    }

    getExternalInventories(entityId: number, providerId: string, req?: { skip_used: boolean }): Observable<ExternalInvetories[]> {
        const params = buildHttpParams(req);
        return this._http.get<ExternalInvetories[]>(
            `${this.ENTITIES_API}/${entityId}/provider/${providerId}/external-inventories`, { params }
        );
    }

}

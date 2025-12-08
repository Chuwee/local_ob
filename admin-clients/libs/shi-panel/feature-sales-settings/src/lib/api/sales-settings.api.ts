
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PatchShiConfiguration, ShiConfiguration } from '../models/sales-configuration.model';

@Injectable()
export class SalesSettingsApi {
    private readonly BASE_USERS_URL = '/api/shi-mgmt-api/v1/shi-configuration';
    private readonly _http = inject(HttpClient);

    getSalesConfiguration(): Observable<ShiConfiguration> {
        return this._http.get<ShiConfiguration>(`${this.BASE_USERS_URL}`);
    }

    patchSalesConfiguration(shiConfiguration: PatchShiConfiguration): Observable<void> {
        return this._http.patch<void>(`${this.BASE_USERS_URL}/sales`, shiConfiguration);
    }
}

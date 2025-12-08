import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { FlcIncompatibilitiesEngineData } from '../models/flc-incompatibilities-engine-data.model';

@Injectable({
    providedIn: 'root'
})
export class ExternalManagementApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly FLC_INCOMPATIBILITIES_ENGINE_API = `${this.BASE_API}/flc-api/v1/incompatibilities-engine/login-data`;

    constructor(private _http: HttpClient) { }

    getFlcIncompatibilitiesEngineData(): Observable<FlcIncompatibilitiesEngineData> {
        return this._http.get<FlcIncompatibilitiesEngineData>(`${this.FLC_INCOMPATIBILITIES_ENGINE_API}`);
    }
}

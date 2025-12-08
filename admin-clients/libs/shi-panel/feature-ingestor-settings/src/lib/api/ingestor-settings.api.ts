
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IngestorConfiguration, PutIngestorConfigurationRequest } from '../models/ingestor-configuration.model';

@Injectable()
export class IngestorSettingsApi {
    private readonly BASE_USERS_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly _http = inject(HttpClient);

    getIngestorConfiguration(supplierId: string): Observable<IngestorConfiguration> {
        return this._http.get<IngestorConfiguration>(`${this.BASE_USERS_URL}/${supplierId}/ingestor-configuration`);
    }

    putIngestorConfiguration(supplierId: string, ingestorConfiguration: PutIngestorConfigurationRequest): Observable<void> {
        return this._http.put<void>(`${this.BASE_USERS_URL}/${supplierId}/ingestor-configuration`, ingestorConfiguration);
    }
}

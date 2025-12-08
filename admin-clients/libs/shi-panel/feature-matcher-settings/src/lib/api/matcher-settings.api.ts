
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatcherConfiguration, PutMatcherConfigurationRequest } from '../models/matcher-configuration.model';

@Injectable()
export class MatcherSettingsApi {
    private readonly BASE_USERS_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly _http = inject(HttpClient);

    getMatchingConfiguration(supplierId: string): Observable<MatcherConfiguration> {
        return this._http.get<MatcherConfiguration>(`${this.BASE_USERS_URL}/${supplierId}/matching-configuration`);
    }

    putMatcherConfiguration(supplierId: string, matcherConfiguration: PutMatcherConfigurationRequest): Observable<void> {
        return this._http.put<void>(`${this.BASE_USERS_URL}/${supplierId}/matching-configuration`, matcherConfiguration);
    }
}

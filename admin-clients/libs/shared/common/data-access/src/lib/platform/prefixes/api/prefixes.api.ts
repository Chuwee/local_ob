import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Prefix } from '../model/prefix.model';

@Injectable({
    providedIn: 'root'
})
export class PrefixesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_URL = `${this.BASE_API}/mgmt-api/v1`;

    private readonly _http = inject(HttpClient);

    getPrefixes(): Observable<Prefix[]> {
        return this._http.get<Prefix[]>(`${this.BASE_URL}/international_phone_prefixes`);
    }

}

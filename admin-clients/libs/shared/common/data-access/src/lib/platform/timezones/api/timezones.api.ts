import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Timezone } from '../model/timezone.model';

@Injectable({
    providedIn: 'root'
})
export class TimezonesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly TIMEZONES_API = `${this.BASE_API}/mgmt-api/v1/timezones`;

    private readonly _http = inject(HttpClient);

    getTimezones(): Observable<Timezone[]> {
        return this._http.get<Timezone[]>(`${this.TIMEZONES_API}`);
    }
}

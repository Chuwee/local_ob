import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetAttendantFields } from '../model/attendant-field.model';

@Injectable({
    providedIn: 'root'
})
export class AttendantsServicesApi {

    readonly #BASE_API = inject(APP_BASE_API);
    readonly #ATTENDANTS_API = `${this.#BASE_API}/mgmt-api/v1/attendants-available-fields`;

    readonly #http = inject(HttpClient);

    getAttendantFields(): Observable<GetAttendantFields> {
        return this.#http.get<GetAttendantFields>(`${this.#ATTENDANTS_API}`);
    }

}

import { UserRoles } from '@admin-clients/shi-panel/utility-models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class GrantPermissionsApi {
    private readonly BASE_GRANT_ACCESS_URL = '/api/shi-mgmt-api/v1/grant-access';
    private readonly _http = inject(HttpClient);

    getRoleAvailablePermissions(role: UserRoles): Observable<string[]> {
        return this._http.get<string[]>(`${this.BASE_GRANT_ACCESS_URL}/${role}/available-permissions`);
    }
}

import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Gateway } from '../model/gateway.model';

@Injectable({
    providedIn: 'root'
})
export class GatewaysApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly GATEWAYS_API = `${this.BASE_API}/mgmt-api/v1/gateways`;

    private readonly _http = inject(HttpClient);

    getGateway(gatewayId: string): Observable<Gateway> {
        return this._http.get<Gateway>(`${this.GATEWAYS_API}/${gatewayId}`);
    }

    getGateways(): Observable<Gateway[]> {
        return this._http.get<Gateway[]>(this.GATEWAYS_API);
    }

}

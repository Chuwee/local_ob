import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class IntegrationsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly INTEGRATIONS_API = `${this.BASE_API}/mgmt-api/v1/integrations`;

    private readonly _http = inject(HttpClient);

    getAuthVendors(): Observable<{ id: string }[]> {
        return this._http.get<{ id: string }[]>(`${this.INTEGRATIONS_API}/auth-vendors`);
    }

    getAuthVendor(vendorId: string): Observable<{ id: string; properties?: { [key: string]: string } }> {
        return this._http.get<{ id: string; properties?: { [key: string]: string } }>(`${this.INTEGRATIONS_API}/auth-vendors/${vendorId}`);
    }

    getBarcodeFormats(): Observable<{ id: string }[]> {
        return this._http.get<{ id: string }[]>(`${this.INTEGRATIONS_API}/barcodes`);
    }

}

import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DonationProvider } from '../donation-providers.service';

@Injectable({
    providedIn: 'root'
})
export class DonationProvidersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly DONATION_PROVIDERS_API = `${this.BASE_API}/mgmt-api/v1/donation-providers`;

    private readonly _http = inject(HttpClient);

    getDonationProviders(): Observable<DonationProvider[]> {
        return this._http.get<DonationProvider[]>(`${this.DONATION_PROVIDERS_API}`);
    }

}

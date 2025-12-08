import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RegionWithId } from '../model/region.model';

@Injectable({
    providedIn: 'root'
})
export class RegionsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly REGIONS_API = 'https://client-dists.oneboxtds.com/cpanel-client-translations/regions/';
    private readonly SYSTEM_REGIONS_API = `${this.BASE_API}/mgmt-api/v1/countries`;

    private readonly _http = inject(HttpClient);

    getRegions(locale: string): Observable<{ [key: string]: string }> {
        return this._http.get<{ [key: string]: string }>(`${this.REGIONS_API}${locale}.json`);
    }

    getSystemRegions(code: string): Observable<RegionWithId[]> {
        return this._http.get<RegionWithId[]>(`${this.SYSTEM_REGIONS_API}/${code}/subdivisions`);
    }

}

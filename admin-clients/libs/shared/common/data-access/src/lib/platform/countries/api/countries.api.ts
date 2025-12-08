import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CountryWithId, CountryWithTaxCalculation } from '../model/country.model';

@Injectable({
    providedIn: 'root'
})
export class CountriesApi {

    private readonly BASE_API = inject(APP_BASE_API);

    private readonly COUNTRIES_API = 'https://client-dists.oneboxtds.com/cpanel-client-translations/countries/';
    private readonly SYSTEM_COUNTRIES_API = `${this.BASE_API}/mgmt-api/v1/countries`;

    private readonly _http = inject(HttpClient);

    getCountries(locale: string): Observable<{ [key: string]: string }> {
        return this._http.get<{ [key: string]: string }>(`${this.COUNTRIES_API}${locale}.json`);
    }

    getSystemCountries(code: string = null): Observable<CountryWithId[]> {
        const params = buildHttpParams({ system_country: true, code });
        return this._http.get<CountryWithId[]>(this.SYSTEM_COUNTRIES_API, { params });
    }

    getAllCountries(): Observable<CountryWithTaxCalculation[]> {
        return this._http.get<CountryWithTaxCalculation[]>(this.SYSTEM_COUNTRIES_API);
    }
}

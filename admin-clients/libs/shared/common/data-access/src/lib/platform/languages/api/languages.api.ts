import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Language } from '../model/language.model';

@Injectable({
    providedIn: 'root'
})
export class LanguagesApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly LANGUAGES_API = `${this.BASE_API}/mgmt-api/v1/languages`;
    private readonly _http = inject(HttpClient);

    getLanguages(request: { platform?: boolean }): Observable<Language[]> {
        const params = buildHttpParams({ platform_language: request.platform });
        return this._http.get<Language[]>(`${this.LANGUAGES_API}`, { params });
    }
}

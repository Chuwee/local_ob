import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BiImpersonation } from '../models/_index';
import { BiHistoryReport, BiReport, GetBiReportsHistoryRequest, GetBiReportsRequest } from '../models/bi-reports.model';
import type { BiSupersetToken } from '../models/bi-superset-token.model';

@Injectable({ providedIn: 'root' })
export class BiSupersetApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #BI_API = `${this.#BASE_API}/bi-api/v1`;
    readonly #SUPERSET_SEGMENT = '/superset';
    readonly #HISTORY_SEGMENT = '/recent';
    readonly #IMPERSONATION = 'ob-impersonation';
    readonly #http = inject(HttpClient);

    getSupersetReports(request: GetBiReportsRequest = {}): Observable<BiReport[]> {
        const options = this.getOptions(request);
        return this.#http.get<BiReport[]>(`${this.#BI_API}${this.#SUPERSET_SEGMENT}`, options);
    }

    getSupersetReportsHistory(request: GetBiReportsHistoryRequest): Observable<BiHistoryReport[]> {
        const options = this.getOptions(request);
        return this.#http.get<BiHistoryReport[]>(`${this.#BI_API}${this.#SUPERSET_SEGMENT}${this.#HISTORY_SEGMENT}`, options);
    }

    getEmbeddedToken(dashboardId: string, request: BiImpersonation = {}): Observable<BiSupersetToken> {
        const options = this.getOptions(request);
        return this.#http.get<BiSupersetToken>(`${this.#BI_API}/superset/embedded/${dashboardId}/token`, options);
    }

    private getOptions<T extends BiImpersonation>(request: T): unknown {
        const { impersonation, ...otherParams } = request;
        const params = buildHttpParams(otherParams);
        if (impersonation) {
            return { params, headers: new HttpHeaders().set(this.#IMPERSONATION, impersonation.toString()) };
        } else {
            return { params };
        }
    }
}
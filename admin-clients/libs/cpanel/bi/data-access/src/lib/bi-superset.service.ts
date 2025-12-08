import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BiSupersetApi } from './api/bi-superset.api';
import { BiImpersonation } from './models/_index';
import {
    GetBiReportsHistoryRequest,
    GetBiReportsRequest
} from './models/bi-reports.model';
import { BiSupersetToken } from './models/bi-superset-token.model';
import { BiSupersetState } from './state/bi-superset.state';

@Injectable({ providedIn: 'root' })
export class BiSupersetService {
    readonly #api = inject(BiSupersetApi);
    readonly #state = inject(BiSupersetState);

    readonly reportsList = Object.freeze({
        load: (request: GetBiReportsRequest = {}) => StateManager.load(
            this.#state.reportsSupersetList,
            this.#api.getSupersetReports(request)
        ),
        get$: () => this.#state.reportsSupersetList.getValue$(),
        error$: () => this.#state.reportsSupersetList.getError$(),
        loading$: () => this.#state.reportsSupersetList.isInProgress$(),
        clear: () => this.#state.reportsSupersetList.setValue(null)
    });

    readonly reportsHistoryList = Object.freeze({
        load: (request: GetBiReportsHistoryRequest = {}) => StateManager.load(
            this.#state.reportsSupersetHistoryList,
            this.#api.getSupersetReportsHistory(request)
        ),
        get$: () => this.#state.reportsSupersetHistoryList.getValue$(),
        error$: () => this.#state.reportsSupersetHistoryList.getError$(),
        loading$: () => this.#state.reportsSupersetHistoryList.isInProgress$(),
        clear: () => this.#state.reportsSupersetHistoryList.setValue(null)
    });

    readonly reportsSearch = Object.freeze({
        load: (request: GetBiReportsRequest = {}) => StateManager.load(
            this.#state.reportsSupersetSearch,
            this.#api.getSupersetReports(request)
        ),
        get$: () => this.#state.reportsSupersetSearch.getValue$(),
        error$: () => this.#state.reportsSupersetSearch.getError$(),
        loading$: () => this.#state.reportsSupersetSearch.isInProgress$(),
        clear: () => this.#state.reportsSupersetSearch.setValue(null)
    });

    getEmbeddedToken(dashboardId: string, request: BiImpersonation = {}): Observable<BiSupersetToken> {
        return this.#api.getEmbeddedToken(dashboardId, request);
    }
}

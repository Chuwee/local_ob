import { StateManager } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { inject, Injectable } from '@angular/core';
import { ErrorDashboardApi } from './api/error-dashboard.api';
import { ErrorDashboardRequest } from './models/dashboard-error.model';
import { ErrorDashboardState } from './state/error-dashboard.state';

@Injectable()
export class ErrorDashboardService {
    readonly #errorDashboardApi = inject(ErrorDashboardApi);
    readonly #errorDashboardState = inject(ErrorDashboardState);

    readonly errorDashboard = Object.freeze({
        load: (request: ErrorDashboardRequest) => StateManager.load(
            this.#errorDashboardState.errorDashboard, this.#errorDashboardApi.getErrorDashboardData(request)
        ),
        getErrorDashboardData$: () => this.#errorDashboardState.errorDashboard.getValue$(),
        isInProgress$: () => this.#errorDashboardState.errorDashboard.isInProgress$(),
        exportLoading$: () => this.#errorDashboardState.errorDashboardExport.isInProgress$(),
        exportErrorRates: (request: ErrorDashboardRequest, data: ExportRequest) => StateManager.inProgress(
            this.#errorDashboardState.errorDashboardExport,
            this.#errorDashboardApi.exportErrorRates(request, data)
        )
    });
}


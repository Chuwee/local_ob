import { StateProperty } from '@OneboxTM/utils-state';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { ErrorDashboardData } from '../models/dashboard-error.model';

@Injectable()
export class ErrorDashboardState {
    readonly errorDashboard = new StateProperty<ErrorDashboardData>();
    readonly errorDashboardExport = new StateProperty<ExportResponse>();
}

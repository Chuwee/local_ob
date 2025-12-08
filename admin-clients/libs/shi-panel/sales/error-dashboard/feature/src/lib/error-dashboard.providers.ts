import { ErrorDashboardApi, ErrorDashboardState, ErrorDashboardService } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { Provider } from '@angular/core';

export const dashboardErrorProviders: Provider[] = [
    ErrorDashboardApi,
    ErrorDashboardState,
    ErrorDashboardService
];

import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { SalesApi, SalesService, SalesState } from '@admin-clients/shi-panel/feature-sales';
import { Routes } from '@angular/router';
import { ErrorDashboardApi, ErrorDashboardService, ErrorDashboardState } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';

export const ERROR_DASHBOARD_ROUTES: Routes = [
    {
        path: '',
        providers: [
            SalesService, SalesState, SalesApi, ErrorDashboardService, ErrorDashboardApi, ErrorDashboardState
        ],
        canActivate: [authCanActivateGuard],
        loadComponent: () => import('./error-dashboard/error-dashboard-page.component').then(c => c.ErrorDashboardPageComponent)
    }
];

import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { SalesApi } from './sales.api';
import { SalesService } from './sales.service';
import { SalesState } from './state/sales.state';

export const SALES_ROUTES: Routes = [
    {
        path: '',
        providers: [
            SalesService, SalesState, SalesApi
        ],
        canActivate: [authCanActivateGuard],
        loadComponent: () => import('./list/sales-list.component').then(c => c.SalesListComponent)
    }
];

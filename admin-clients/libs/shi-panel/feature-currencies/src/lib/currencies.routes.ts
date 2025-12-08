import {
    authCanActivateGuard,
    permissionCanActivateGuard
} from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { CurrenciesApi } from './currencies.api';
import { CurrenciesService } from './currencies.service';
import { CurrenciesState } from './state/currencies.state';

export const CURRENCIES_ROUTES: Routes = [
    {
        path: '',
        providers: [CurrenciesApi, CurrenciesService, CurrenciesState],
        loadComponent: () => import('./list/currencies-list.component').then(c => c.CurrenciesListComponent),
        canActivate: [authCanActivateGuard, permissionCanActivateGuard]
    }
];

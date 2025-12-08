import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { ListingsApi } from './listings.api';
import { ListingsService } from './listings.service';
import { ListingsState } from './state/listings.state';

export const LISTINGS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            ListingsService, ListingsState, ListingsApi
        ],
        loadComponent: () => import('./list/listings-list.component').then(c => c.ListingsListComponent),
        canActivate: [authCanActivateGuard]
    }
];

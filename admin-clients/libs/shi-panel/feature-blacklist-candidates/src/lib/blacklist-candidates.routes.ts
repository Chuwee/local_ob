import {
    authCanActivateGuard,
    permissionCanActivateGuard
} from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { BlacklistedMatchingsApi } from './blacklist-candidates.api';
import { BlacklistedMatchingsService } from './blacklist-candidates.service';
import { BlacklistedMatchingsState } from './state/blacklist-candidates.state';

export const BLACKLISTED_MATCHINGS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            BlacklistedMatchingsService, BlacklistedMatchingsState, BlacklistedMatchingsApi
        ],
        loadComponent: () => import('./list/blacklisted-matchings-list.component').then(c => c.MatchingsListComponent),
        canActivate: [authCanActivateGuard, permissionCanActivateGuard]
    }
];

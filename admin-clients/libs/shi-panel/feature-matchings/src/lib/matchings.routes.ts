import {
    authCanActivateGuard,
    permissionCanActivateGuard
} from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { MatchingsApi } from './matchings.api';
import { MatchingsService } from './matchings.service';
import { MatchingsState } from './state/matchings.state';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';

const unsavedWarnParamsToCheck = ['supplier'];

export const MATCHINGS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            MatchingsService, MatchingsState, MatchingsApi
        ],
        loadComponent: () => import('./list/matchings-list.component').then(c => c.MatchingsListComponent),
        canActivate: [authCanActivateGuard, permissionCanActivateGuard],
        canDeactivate: [unsavedChangesGuard(unsavedWarnParamsToCheck)],
        runGuardsAndResolvers: 'paramsOrQueryParamsChange'
    }
];

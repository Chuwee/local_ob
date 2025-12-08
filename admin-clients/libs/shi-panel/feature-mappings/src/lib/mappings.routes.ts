import {
    authCanActivateGuard,
    permissionCanActivateGuard
} from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';
import { MappingsApi } from './mappings.api';
import { MappingsService } from './mappings.service';
import { MappingsState } from './state/mappings.state';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';

export const MAPPINGS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            MappingsService, MappingsState, MappingsApi
        ],
        loadComponent: () => import('./list/mappings-list.component').then(c => c.MappingsListComponent),
        canActivate: [authCanActivateGuard, permissionCanActivateGuard],
        canDeactivate: [unsavedChangesGuard()],
        runGuardsAndResolvers: 'paramsOrQueryParamsChange'
    }
];

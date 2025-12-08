import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { VisibilityComponent } from './visibility.component';

export const VISIBILITY_ROUTES: Routes = [
    {
        path: '',
        component: VisibilityComponent,
        canActivate: [authCanActivateGuard],
        canDeactivate: [unsavedChangesGuard()],
        providers: [...entitiesProviders]
    }
];

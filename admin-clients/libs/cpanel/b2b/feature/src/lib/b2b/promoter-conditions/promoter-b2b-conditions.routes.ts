import { B2bApi, B2bService, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';

export const promoterConditionsRoutes: Routes = [
    {
        path: '',
        loadComponent: () => import('./promoter-b2b-conditions.component'),
        canDeactivate: [unsavedChangesGuard()],
        providers: [
            B2bService, B2bApi, B2bState
        ]
    }
];

export default promoterConditionsRoutes;
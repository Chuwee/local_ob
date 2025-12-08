import { B2bService, B2bApi, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { b2bGuard } from '../b2b.guard';
import { EntityB2bConditionsComponent } from './entity-b2b-conditions.component';

export const entityB2bConditionsRoutes: Routes = [
    {
        path: '',
        canActivate: [b2bGuard],
        canDeactivate: [unsavedChangesGuard()],
        component: EntityB2bConditionsComponent,
        providers: [B2bService, B2bApi, B2bState]
    }
];

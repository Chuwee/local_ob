import { UserRoles, authCanMatchGuard } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { Routes } from '@angular/router';
import { EntitiesLicensesComponent } from './list/entities-licenses-list.component';

export const BI_ENTITIES_LICENSES_ROUTES: Routes = [
    {
        path: '',
        component: EntitiesLicensesComponent,
        canActivate: [roleGuard],
        canMatch: [authCanMatchGuard],
        data: {
            roles: [UserRoles.SYS_MGR]
        }
    }
];

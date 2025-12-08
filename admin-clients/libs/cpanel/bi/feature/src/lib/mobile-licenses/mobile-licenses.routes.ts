import { UserRoles, authCanMatchGuard } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { Routes } from '@angular/router';
import { MobileLicensesComponent } from './list/mobile-licenses-list.component';

export const BI_MOBILE_LICENSES_ROUTES: Routes = [
    {
        path: '',
        component: MobileLicensesComponent,
        canActivate: [roleGuard],
        canMatch: [authCanMatchGuard],
        data: {
            roles: [UserRoles.SYS_MGR]
        }
    }
];


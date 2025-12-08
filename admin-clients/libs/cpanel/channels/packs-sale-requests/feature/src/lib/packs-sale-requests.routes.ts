import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { packsSaleRequestsProviders } from '@admin-clients/cpanel-channels-packs-sale-requests-data-access';
import { Routes } from '@angular/router';
import { PacksSaleRequestsListComponent } from './list/packs-sale-requests-list.component';

export const PACKS_SALE_REQUESTS_ROUTES: Routes = [{
    path: '',
    providers: [
        ...packsSaleRequestsProviders
    ],
    children: [
        {
            path: '',
            component: PacksSaleRequestsListComponent,
            canActivate: [authCanActivateGuard]
        }
    ]
}];

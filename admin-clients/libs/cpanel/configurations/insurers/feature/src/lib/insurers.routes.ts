import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { insurersProviders } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { Routes } from '@angular/router';
import { InsurersListComponent } from './list/insurers-list.component';

export const INSURERS_ROUTES: Routes = [{
    path: '',
    providers: [
        ...insurersProviders
    ],
    children: [
        {
            path: '',
            component: InsurersListComponent,
            canActivate: [authCanActivateGuard]
        },
        {
            path: ':insurerId',
            loadChildren: () => import('./insurer/insurer.routes').then(r => r.INSURER_ROUTES),
            data: {
                breadcrumb: 'insurerName'
            }
        }
    ]
}];

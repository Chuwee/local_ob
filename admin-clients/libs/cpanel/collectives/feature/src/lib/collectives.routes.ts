import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { CollectivesListComponent } from './list/collectives-list.component';

export const routes: Routes = [
    {
        path: '',
        component: CollectivesListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':collectiveId',
        loadChildren: () => import('./collective/collective.module').then(m => m.CollectiveModule),
        data: {
            breadcrumb: 'TITLES.COLLECTIVE_DETAIL'
        }
    }
];
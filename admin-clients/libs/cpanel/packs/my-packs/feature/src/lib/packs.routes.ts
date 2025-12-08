import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { PacksListComponent } from './list/packs-list.component';

export const routes: Routes = [
    {
        path: '',
        component: PacksListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':packId',
        loadChildren: () => import('./pack/pack.routes').then(m => m.routes),
        data: {
            breadcrumb: 'TITLES.PACK_DETAILS'
        }
    }
];
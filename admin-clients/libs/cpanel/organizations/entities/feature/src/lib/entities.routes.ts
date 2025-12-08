import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { categoriesProviders } from '@admin-clients/cpanel/organizations/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Routes } from '@angular/router';
import { EntitiesListComponent } from './list/entities-list.component';

export const entitiesRoutes: Routes = [
    {
        path: '',
        providers: [
            ...entitiesProviders,
            ...categoriesProviders
        ],
        children: [
            {
                path: '',
                component: EntitiesListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':entityId',
                loadChildren: () => import('./entity/entity.routes').then(m => m.entityRoutes),
                data: {
                    breadcrumb: 'TITLES.ENTITY_USER_DETAILS'
                }
            }
        ]
    }
];

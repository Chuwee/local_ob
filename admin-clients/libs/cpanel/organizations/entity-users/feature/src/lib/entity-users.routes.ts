import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Routes } from '@angular/router';
import { EntityUsersListComponent } from './list/entity-users-list.component';

export const usersRoutes: Routes = [
    {
        path: '',
        providers: [
            ...entitiesProviders
        ],
        children: [
            {
                path: '',
                component: EntityUsersListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':userId',
                loadChildren: () => import('./entity-user/entity-user.routes').then(m => m.entityUserRoutes),
                data: {
                    breadcrumb: 'TITLES.ENTITY_USER_DETAILS'
                }
            }
        ]
    }

];

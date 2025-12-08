import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { entityUserDetailsResolver } from './details/entity-user-details-resolver';
import { EntityUserDetailsComponent } from './details/entity-user-details.component';
import { MYSELF_USER_DETAILS_TOKEN } from './entity-user.token';
import { EntityUserNotificationsComponent } from './notifications/entity-user-notifications.component';

const routes: Routes = [{
    path: '',
    component: EntityUserDetailsComponent,
    resolve: {
        event: entityUserDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'register-data',
            pathMatch: 'full'
        },
        {
            path: 'register-data',
            loadChildren: () => import('./register-data/entity-user-register-data.module').then(m => m.EntityUserRegisterDataModule),
            data: {
                breadcrumb: 'USER.REGISTER_DATA'
            }
        },
        {
            path: 'permissions',
            loadChildren: () => import('./permissions/entity-user-permissions.module').then(m => m.EntityUserPermissionsModule),
            data: {
                breadcrumb: 'USER.PERMISSIONS'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'notifications',
            component: EntityUserNotificationsComponent,
            data: {
                breadcrumb: 'USER.NOTIFICATIONS.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

export const myselfRoutes: Routes = [
    {
        path: '',
        providers: [
            ...entitiesProviders,
            { provide: MYSELF_USER_DETAILS_TOKEN, useValue: true }
        ],
        children: routes
    }
];

export const entityUserRoutes: Routes = [
    {
        path: '',
        providers: [
            { provide: MYSELF_USER_DETAILS_TOKEN, useValue: false }
        ],
        children: routes
    }
];
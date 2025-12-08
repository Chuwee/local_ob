import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { MatTabNav } from '@angular/material/tabs';
import { Routes } from '@angular/router';
import { UsersState } from './state/user.state';
import { UsersApi } from './users.api';
import { UsersService } from './users.service';

export const USERS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            UsersService, UsersState, UsersApi
        ],
        canActivate: [authCanActivateGuard],
        loadComponent: () => import('./list/users-list.component').then(c => c.UsersListComponent)
    },
    {
        path: ':userId',
        loadChildren: () => import('./user/user-routing.module').then(m => m.UserRoutingModule),
        providers: [
            UsersService, UsersState, UsersApi, MatTabNav
        ],
        data: {
            breadcrumb: 'TITLES.USERS.USER_DETAILS'
        }
    }
];

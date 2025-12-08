/* eslint-disable @nx/enforce-module-boundaries */
import { MatTabNav } from '@angular/material/tabs';
import { Route } from '@angular/router';
import { preventLoginGuard } from '@admin-clients/shared/core/data-access';
import { provideLoginBackgroundurl } from '@admin-clients/shared/feature/login';
import {
    authCanMatchGuard,
    permissionCanMatchGuard,
    roleCanMatchGuard
} from '@admin-clients/shi-panel/data-access-auth';
import { UsersApi, UsersService, UsersState } from '@admin-clients/shi-panel/feature-users';
import { UserPermissions, UserRoles } from '@admin-clients/shi-panel/utility-models';
import { loginBackgroundFactory } from './loginBackgroundFactory';

const protectedRoutes: Route[] = [
    {
        path: 'sales',
        loadChildren: () => import('@admin-clients/shi-panel/feature-sales').then(m => m.SALES_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.SALES.SALES_LIST_TITLE',
            permissions: [UserPermissions.salesRead]
        }
    },
    {
        path: 'sales-settings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-sales-settings').then(m => m.SALES_SETTINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.SALES_SETTINGS',
            permissions: [UserPermissions.configurationRead]
        }
    },
    {
        path: 'error-dashboard',
        loadChildren: () => import('@admin-clients/shi-panel/sales/error-dashboard-feature').then(m => m.ERROR_DASHBOARD_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.ERROR_DASHBOARD',
            permissions: [UserPermissions.salesRead]
        }
    },
    {
        path: 'listings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-listings').then(m => m.LISTINGS_ROUTES),
        canMatch: [authCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.LISTINGS.LISTINGS_LIST_TITLE',
            permissions: [UserPermissions.listingRead]
        }
    },
    {
        path: 'mappings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-mappings').then(m => m.MAPPINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.MAPPINGS.MAPPINGS_LIST_TITLE',
            permissions: [UserPermissions.mappingRead]
        }
    },
    {
        path: 'matcher-settings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-matcher-settings').then(m => m.MATCHER_SETTINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.MATCHER_SETTINGS',
            permissions: [UserPermissions.matchingRead]
        }
    },
    {
        path: 'ingestor-settings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-ingestor-settings').then(m => m.INGESTOR_SETTINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.INGESTOR_SETTINGS',
            permissions: [UserPermissions.ingestorRead]
        }
    },
    {
        path: 'candidates-blacklist',
        loadChildren: () => import('@admin-clients/shi-panel/feature-blacklist-candidates').then(m => m.BLACKLISTED_MATCHINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.CANDIDATES_BLACKLIST',
            permissions: [UserPermissions.matchingRead]
        }
    },
    {
        path: 'users',
        loadChildren: () => import('@admin-clients/shi-panel/feature-users').then(m => m.USERS_ROUTES),
        canMatch: [authCanMatchGuard, roleCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.USERS.USERS_LIST_TITLE',
            roles: [UserRoles.owner, UserRoles.admin],
            permissions: [UserPermissions.userRead]
        }
    },
    {
        path: 'my-user',
        loadChildren: () => import('@admin-clients/shi-panel/feature-users').then(m => m.UserRoutingModule),
        providers: [
            UsersService, UsersState, UsersApi, MatTabNav
        ],
        data: {
            breadcrumb: 'TITLES.MY_USER'
        }
    },
    {
        path: 'matchings',
        loadChildren: () => import('@admin-clients/shi-panel/feature-matchings').then(m => m.MATCHINGS_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.MATCHINGS.MATCHINGS_LIST_TITLE',
            permissions: [UserPermissions.matchingRead]
        }
    },
    {
        path: 'currencies',
        loadChildren: () => import('@admin-clients/shi-panel/feature-currencies').then(m => m.CURRENCIES_ROUTES),
        canMatch: [authCanMatchGuard, permissionCanMatchGuard],
        data: {
            breadcrumb: 'TITLES.CURRENCIES.CURRENCIES_LIST_TITLE',
            permissions: [UserPermissions.exchangeRateRead]
        }
    },
    // Redirects
    {
        path: '**',
        redirectTo: 'sales'
    }
];

export const routes: Route[] = [
    {
        path: '',
        redirectTo: 'sales',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadChildren: () => import('@admin-clients/shared/feature/login').then(m => m.routes),
        canActivate: [preventLoginGuard],
        providers: [
            provideLoginBackgroundurl(() => loginBackgroundFactory())
        ]
    },
    {
        path: '',
        loadComponent: () => import('./nav/nav.component').then(m => m.NavComponent),
        canMatch: [authCanMatchGuard],
        children: protectedRoutes
    }
];

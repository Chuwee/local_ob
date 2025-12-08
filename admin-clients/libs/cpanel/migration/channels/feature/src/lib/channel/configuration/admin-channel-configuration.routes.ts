import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { Routes } from '@angular/router';

export const ADMIN_CHANNEL_CONFIGURATION: Routes = [
    {
        path: '',
        canActivate: [roleGuard],
        loadComponent: () => import('./admin-channel-configuration.component').then(c => c.AdminChannelConfigurationComponent),
        data: {
            roles: [UserRoles.SYS_MGR]
        },
        children: [
            {
                path: '',
                redirectTo: 'cors',
                pathMatch: 'full'
            },
            {
                path: 'cors',
                loadComponent: () => import('./cors/admin-channel-cors.component').then(c => c.AdminChannelCorsComponent),
                data: {
                    breadcrumb: 'ADMIN_CHANNEL.CONFIGURATION.CORS.TITLE'
                }
            },
            {
                path: 'subdomain',
                loadComponent: () => import('./subdomain/admin-channel-subdomain.component').then(c => c.AdminChannelSubdomainComponent),
                data: {
                    breadcrumb: 'ADMIN_CHANNEL.CONFIGURATION.SUBDOMAIN.TITLE'
                }
            }
        ]
    }
];

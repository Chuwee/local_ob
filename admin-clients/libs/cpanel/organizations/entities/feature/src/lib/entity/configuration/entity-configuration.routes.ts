import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EntityConfigurationComponent } from './entity-configuration.component';

export const ENTITY_CONFIGURATION: Routes = [
    {
        path: '',
        component: EntityConfigurationComponent,
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'third-integrations'
            },
            {
                path: 'other-options',
                loadComponent: () =>
                    import('./advanced/entity-advanced-configuration.component').then(c => c.EntityAdvancedConfigurationComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.TITLES.OTHER_OPTIONS',
                    roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                }
            },
            {
                path: 'third-integrations',
                loadComponent: () => import('./integrations/entity-integrations.component').then(c => c.EntityIntegrationsComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.ADVANCED_CONFIG.INTEGRATIONS',
                    roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                }
            },
            {
                path: 'fever-integrations',
                loadComponent: () => import('./fever-integrations/fever-integrations.component').then(c => c.FeverIntegrationsComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.TITLES.FEVER_INTEGRATIONS',
                    roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                }
            },
            {
                path: 'security',
                loadComponent: () => import('./security/entity-security.component').then(c => c.EntitySecurityComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.GENERAL_DATA.SECURITY',
                    roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                }
            },
            {
                path: 'webhooks',
                loadComponent: () => import('./webhooks/entity-webhooks.component').then(c => c.EntityWebhooksComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.GENERAL_DATA.WEBHOOKS',
                    roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                }
            },
            {
                path: 'domain-settings',
                loadComponent: () =>
                    import('./customer-domain-settings/entity-customer-domain-settings.component')
                        .then(c => c.EntityCustomerDomainSettingsComponent),
                canActivate: [roleGuard],
                pathMatch: 'full',
                data: {
                    breadcrumb: 'ENTITY.GENERAL_DATA.DOMAIN_SETTINGS',
                    roles: [UserRoles.SYS_MGR, UserRoles.SYS_ANS]
                }
            }
        ]
    }
];

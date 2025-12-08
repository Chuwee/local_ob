import { categoriesProviders } from '@admin-clients/cpanel/organizations/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Routes } from '@angular/router';
import { entityDetailsResolver } from './details/entity-details-resolver';
import { EntityDetailsComponent } from './details/entity-details.component';

export const entityRoutes: Routes = [{
    path: '',
    component: EntityDetailsComponent,
    providers: [
        ...entitiesProviders,
        ...categoriesProviders
    ],
    resolve: {
        entity: entityDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            loadChildren: () => import('./general-data/entity-general-data-routing.module').then(m => m.EntityGeneralDataRoutingModule)
        },
        {
            path: 'register-users',
            loadChildren: () => import('./register-users/register-users-routing.module').then(m => m.RegisterUsersRoutingModule)
        },
        {
            path: 'categories',
            loadChildren: () => import('./categories/entity-categories-routing.module').then(m => m.EntityCategoriesRoutingModule)
        },
        {
            path: 'external',
            loadChildren: () => import('./external/entity-external-routing.module').then(m => m.EntityExternalRoutingModule)
        },
        {
            path: 'advanced-config',
            loadChildren: () => import('./configuration/entity-configuration.routes').then(m => m.ENTITY_CONFIGURATION)
        },
        {
            path: 'zone-templates',
            loadChildren: () => import('./zone-templates/entity-zone-templates.routes').then(m => m.ENTITY_ZONE_TEMPLATES_ROUTES)
        }
    ]
}];

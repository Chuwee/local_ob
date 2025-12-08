
import { Routes } from '@angular/router';
import { EntityZoneTemplatesContainerComponent } from './container/entity-zone-templates-container.component';
import { EntityZoneTemplateDetailsComponent } from './zone-template/details/entity-zone-template-details.component';
import { entityZoneTemplateDetailsResolver } from './zone-template/details/entity-zone-template-details.resolvers';

export const ENTITY_ZONE_TEMPLATES_ROUTES: Routes = [
    {
        path: '',
        component: EntityZoneTemplatesContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':templateId',
                component: EntityZoneTemplateDetailsComponent,
                resolve: {
                    configuration: entityZoneTemplateDetailsResolver
                },
                data: {
                    breadcrumb: 'templateName'
                },
                loadChildren: () => import('./zone-template/details/entity-zone-template-details.routes').then(m => m.ENTITY_ZONE_TEMPLATE_DETAILS_ROUTES)
            }
        ]
    }
];

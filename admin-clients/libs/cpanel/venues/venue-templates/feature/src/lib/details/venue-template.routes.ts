import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import type { Routes } from '@angular/router';
import { VenueTemplateCapacityComponent } from '../standard/venue-template-capacity.component';
import { VenueTemplateDetailsComponent } from './venue-template-details.component';

export const VENUE_TEMPLATE_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'standard',
        pathMatch: 'full'
    },
    {
        path: 'standard',
        pathMatch: 'full',
        component: VenueTemplateDetailsComponent,
        children: [
            {
                path: '',
                component: VenueTemplateCapacityComponent,
                data: {
                    breadcrumb: 'VENUE_TPLS.CAPACITY'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    },
    {
        path: 'template-editor',
        pathMatch: 'full',
        loadChildren: () => import('../venue-template-editor/venue-template-editor.routes').then(m => m.routes),
        data: {
            breadcrumb: 'VENUE_TPLS.TEMPLATE_EDITOR'
        }
    }
];

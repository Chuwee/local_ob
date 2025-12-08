import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { Routes } from '@angular/router';
import { venueTemplateDetailsResolver } from '../details/venue-template-details-resolver';
import { VenueTemplatesListComponent } from './venue-templates-list.component';

export const VENUE_TEMPLATES_ROUTES: Routes = [
    {
        path: '',
        providers: [venuesProviders],
        children: [
            {
                path: '',
                component: VenueTemplatesListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':venueTemplateId',
                loadChildren: () => import('../details/venue-template.routes').then(m => m.VENUE_TEMPLATE_ROUTES),
                resolve: {
                    venueTemplate: venueTemplateDetailsResolver
                },
                data: {
                    breadcrumb: 'CONFIG_NAME'
                }
            }
        ]
    }
];

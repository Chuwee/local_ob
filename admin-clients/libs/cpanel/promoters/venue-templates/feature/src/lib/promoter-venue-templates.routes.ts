import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { PromoterVenueTemplatesListComponent } from './list/promoter-venue-templates-list.component';
import { promoterVenueTemplateDetailsResolver } from './venue-template/details/promoter-venue-template-details-resolver';

export const PROMOTER_VENUE_TEMPLATES_ROUTES: Routes = [
    {
        path: '',
        component: PromoterVenueTemplatesListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':venueTemplateId',
        loadChildren: () => import('./venue-template/promoter-venue-template.routes').then(m => m.PROMOTER_VENUE_TEMPLATE_ROUTES),
        resolve: {
            venueTemplate: promoterVenueTemplateDetailsResolver
        },
        data: {
            breadcrumb: 'templateName'
        }
    }
];

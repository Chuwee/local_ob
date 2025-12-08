import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PromoterVenueTemplateGatesComponent } from './activity/gates/promoter-venue-template-gates.component';
import { PromoterVenueTemplateGeneralDataComponent } from './activity/general-data/promoter-venue-template-general-data.component';
import { PromoterVenueTemplateActivityComponent } from './activity/promoter-venue-template-activity.component';
import { PromoterVenueTemplateDetailsComponent } from './details/promoter-venue-template-details.component';
import { PromoterVenueTemplateCapacityComponent } from './standard/promoter-venue-template-capacity.component';

export const PROMOTER_VENUE_TEMPLATE_ROUTES: Routes = [
    {
        path: '',
        component: PromoterVenueTemplateDetailsComponent,
        children: [
            {
                path: 'standard',
                pathMatch: 'full',
                component: PromoterVenueTemplateCapacityComponent,
                data: {
                    breadcrumb: 'VENUE_TPLS.CAPACITY'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'elements-info',
                loadComponent: () =>
                    import('./elements-info/promoter-venue-template-elements-info.component')
                        .then(c => c.PromoterVenueTemplateElementsInfoComponent),
                data: {
                    breadcrumb: 'VENUE_TPLS.ELEMENTS_INFO.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'activity',
                component: PromoterVenueTemplateActivityComponent,

                children: [
                    {
                        path: '',
                        redirectTo: 'general-data',
                        pathMatch: 'full'
                    },
                    {
                        path: 'general-data',
                        component: PromoterVenueTemplateGeneralDataComponent,
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'VENUE_TPLS.GENERAL_DATA'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'gates',
                        component: PromoterVenueTemplateGatesComponent,
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'VENUE_TPLS.GATES'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'elements-info',
                        loadComponent: () =>
                            import('./elements-info/promoter-venue-template-elements-info.component')
                                .then(c => c.PromoterVenueTemplateElementsInfoComponent),
                        data: {
                            breadcrumb: 'VENUE_TPLS.ELEMENTS_INFO.TITLE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    }
                ]
            }
        ]
    },
    {
        path: 'template-editor',
        loadChildren: () => import('./venue-template-editor/promoter-template-editor.routes').then(m => m.PROMOTER_TEMPLATE_EDITOR_ROUTES),
        data: {
            breadcrumb: 'VENUE_TPLS.TEMPLATE_EDITOR'
        }
    }
];

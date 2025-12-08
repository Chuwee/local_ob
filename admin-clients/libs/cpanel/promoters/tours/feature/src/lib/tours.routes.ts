import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ToursContainerComponent } from './container/tours-container.component';
import { TourDetailsComponent } from './tour/details/tour-details.component';
import { TourGeneralDataComponent } from './tour/general-data/tour-general-data.component';

export const TOURS_ROUTES: Routes = [
    {
        path: '',
        component: ToursContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':tourId',
                component: TourDetailsComponent,
                data: {
                    breadcrumb: 'TOUR.DETAILS'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'general-data'
                    },
                    {
                        path: 'general-data',
                        component: TourGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full'
                    }
                ]
            }
        ]
    }
];

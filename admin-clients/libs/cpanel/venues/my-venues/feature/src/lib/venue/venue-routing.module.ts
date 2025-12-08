import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { VenueAccessControlComponent } from './access-control/venue-access-control.component';
import { venueDetailsResolver } from './details/venue-details-resolver';
import { VenueDetailsComponent } from './details/venue-details.component';
import { VenueGeneralDataComponent } from './general-data/venue-general-data.component';

const routes: Routes = [{
    path: '',
    component: VenueDetailsComponent,
    resolve: {
        venue: venueDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: VenueGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VENUE.GENERAL_DATA'
            }
        },
        {
            path: 'spaces',
            loadChildren: () => import('./spaces/venue-spaces.module').then(m => m.VenueSpacesModule),
            data: {
                breadcrumb: 'VENUE.SPACES.TITLE'
            }
        },
        {
            path: 'access-control',
            component: VenueAccessControlComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VENUE.ACCESS_CONTROL.TITLE'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VenueRoutingModule { }

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { VenueSpacesContainerComponent } from './container/venue-spaces-container.component';
import { VenueSpaceDetailsComponent } from './details/venue-space-details.component';

const routes: Routes = [{
    path: '',
    component: VenueSpacesContainerComponent,
    children: [
        {
            path: '',
            component: null,
            pathMatch: 'full',
            children: []
        },
        {
            path: ':spaceId',
            component: VenueSpaceDetailsComponent,
            data: {
                breadcrumb: 'VENUE.SPACES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VenueSpacesRoutingModule { }

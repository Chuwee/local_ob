import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { VenuesListComponent } from './list/venues-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        component: VenuesListComponent
    },
    {
        path: ':venueId',
        loadChildren: () => import('./venue/venue.module').then(m => m.VenueModule),
        data: {
            breadcrumb: 'TITLES.VENUE_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VenuesRoutingModule { }

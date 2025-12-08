import { categoriesProviders } from '@admin-clients/cpanel/organizations/data-access';
import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SEASON_TICKETS_ROUTES } from './season-tickets.routes';

@NgModule({
    providers: [venuesProviders, categoriesProviders],
    imports: [RouterModule.forChild(SEASON_TICKETS_ROUTES)]
})
export class SeasonTicketsModule { }

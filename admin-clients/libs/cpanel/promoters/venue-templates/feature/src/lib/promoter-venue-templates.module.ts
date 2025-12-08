import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PROMOTER_VENUE_TEMPLATES_ROUTES } from './promoter-venue-templates.routes';

@NgModule({
    providers: [venuesProviders],
    imports: [RouterModule.forChild(PROMOTER_VENUE_TEMPLATES_ROUTES)]
})
export class PromoterVenueTemplatesModule { }

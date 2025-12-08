import { categoriesProviders } from '@admin-clients/cpanel/organizations/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { eventPromotionsProviders } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { eventSessionsProviders } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EVENTS_ROUTES } from './events.routes';

@NgModule({
    providers: [
        venuesProviders,
        categoriesProviders,
        eventsProviders,
        eventPromotionsProviders,
        eventSessionsProviders
    ],
    imports: [RouterModule.forChild(EVENTS_ROUTES)]
})
export class EventsModule { }

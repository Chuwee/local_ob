import { channelsProviders } from '@admin-clients/cpanel/channels/data-access';
import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { eventSessionsProviders } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { PayoutsService } from '@admin-clients/cpanel-sales-data-access';
import { Routes } from '@angular/router';
import { PayoutListComponent } from './list/payouts-list.component';

export const payoutsRoutes: Routes = [
    {
        path: '',
        component: PayoutListComponent,
        providers: [PayoutsService, eventsProviders, eventSessionsProviders, channelsProviders],
        canActivate: [authCanActivateGuard]
    }
];

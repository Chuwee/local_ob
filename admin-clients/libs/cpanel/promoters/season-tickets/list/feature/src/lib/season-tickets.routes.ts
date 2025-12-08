import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { SeasonTicketsListComponent } from './list/season-tickets-list.component';
import { seasonTicketDetailsResolver } from './season-ticket-details-resolver';

export const SEASON_TICKETS_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketsListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':seasonTicketId',
        loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-details-feature')
            .then(r => r.SEASON_TICKET_ROUTES),
        resolve: {
            seasonTicket: seasonTicketDetailsResolver
        },
        data: {
            breadcrumb: 'TITLES.SEASON_TICKET_DETAILS'
        }
    }
];

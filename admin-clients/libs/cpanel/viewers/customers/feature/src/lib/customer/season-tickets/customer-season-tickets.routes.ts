import { Routes } from '@angular/router';
import { customerSeasonTicketDetailsResolver } from './details/customer-season-ticket-details.resolver';

export const CUSTOMER_SEASON_TICKETS_ROUTES: Routes = [{
    path: '',
    loadComponent: () => import('./container/customer-season-tickets-container.component')
        .then(m => m.CustomerSeasonTicketsContainerComponent),
    children: [
        {
            path: ':orderItemId',
            loadComponent: () => import('./details/customer-season-ticket-details.component')
                .then(m => m.CustomerSeasonTicketDetailsComponent),
            data: {
                breadcrumb: 'orderItemId'
            },
            resolve: {
                orderItem: customerSeasonTicketDetailsResolver
            },
            children: [
                {
                    path: '',
                    pathMatch: 'full',
                    redirectTo: 'general-data'
                },
                {
                    path: 'general-data',
                    pathMatch: 'full',
                    loadComponent: () => import('./general-data/customer-season-tickets-general-data.component')
                        .then(m => m.CustomerSeasonTicketsGeneralDataComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.SEASON_TICKETS.GENERAL_DATA_TITLE'
                    }
                },
                {
                    path: 'transfer-seats',
                    pathMatch: 'full',
                    loadComponent: () => import('./transfer/customer-transfer-season-tickets.component')
                        .then(m => m.CustomerTransferSeasonTicketsComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.SEASON_TICKETS.TRANSFER_SEATS_TITLE'
                    }
                },
                {
                    path: 'realese-seats',
                    pathMatch: 'full',
                    loadComponent: () => import('./release/customer-release-season-tickets.component')
                        .then(m => m.CustomerReleaseSeasonTicketsComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.SEASON_TICKETS.REALESE_SEATS_TITLE'
                    }
                },
                {
                    path: 'auto-renewal',
                    pathMatch: 'full',
                    loadComponent: () => import('./auto-renewal/customer-season-ticket-auto-renewal.component')
                        .then(m => m.CustomerSeasonTicketAutoRenewalComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.SEASON_TICKETS.AUTO_RENEWAL_TITLE'
                    }
                }
            ]
        }
    ]
}];

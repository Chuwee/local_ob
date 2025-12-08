import { Routes } from '@angular/router';

export const CUSTOMER_TICKETS_ROUTES: Routes = [{
    path: '',
    loadComponent: () => import('./container/customer-tickets-container.component')
        .then(m => m.CustomerTicketsContainerComponent),
    children: [
        {
            path: ':sessionId',
            loadComponent: () => import('./details/customer-ticket-details.component')
                .then(m => m.CustomerTicketDetailsComponent),
            data: {
                breadcrumb: 'sessionId'
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
                    loadComponent: () => import('./general-data/customer-tickets-general-data.component')
                        .then(m => m.CustomerTicketsGeneralDataComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.TICKETS.GENERAL_DATA_TITLE'
                    }
                },
                {
                    path: 'transfer',
                    pathMatch: 'full',
                    loadComponent: () => import('./transfer/customer-transfer-tickets.component')
                        .then(m => m.CustomerTransferTicketsComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.TICKETS.TRANSFER_SEATS_TITLE',
                        onlyTransferredToCustomer: false
                    }
                },
                {
                    path: 'transferred',
                    pathMatch: 'full',
                    loadComponent: () => import('./transfer/customer-transfer-tickets.component')
                        .then(m => m.CustomerTransferTicketsComponent),
                    data: {
                        breadcrumb: 'CUSTOMER.TICKETS.TRANSFERRED_TITLE',
                        onlyTransferredToCustomer: true
                    }
                }
            ]
        }
    ]
}];
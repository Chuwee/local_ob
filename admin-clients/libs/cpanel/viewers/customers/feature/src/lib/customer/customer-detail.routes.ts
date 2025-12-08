import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { ticketsBaseProviders } from '@admin-clients/shared/common/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { customerDetailsResolver } from './details/customer-details-resolver';
import { CustomerTicketsState } from './tickets/customer-tickets.state';

export const CUSTOMER_DETAIL_ROUTES: Routes = [{
    path: '',
    loadComponent: () => import('./details/customer-details.component').then(m => m.CustomerDetailsComponent),
    resolve: {
        customer: customerDetailsResolver
    },
    providers: [
        ...entitiesProviders
    ],
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            loadComponent: () => import('./general-data/customer-general-data.component').then(m => m.CustomerGeneralDataComponent),
            data: {
                breadcrumb: 'CUSTOMER.GENERAL_DATA'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'purchases',
            loadChildren: () => import('./products/customer-products-routes').then(m => m.CUSTOMER_PRODUCTS_ROUTES),
            data: {
                breadcrumb: 'CUSTOMER.PURCHASES'
            }
        },
        {
            path: 'season-tickets',
            providers: [
                ...ticketsBaseProviders
            ],
            loadChildren: () => import('./season-tickets/customer-season-tickets.routes').then(m => m.CUSTOMER_SEASON_TICKETS_ROUTES),
            data: {
                breadcrumb: 'CUSTOMER.SEASON_TICKETS.TITLE'
            }
        },
        {
            path: 'tickets',
            providers: [
                ...ticketsBaseProviders,
                ...eventsProviders,
                CustomerTicketsState
            ],
            loadChildren: () => import('./tickets/customer-tickets.routes').then(m => m.CUSTOMER_TICKETS_ROUTES),
            data: {
                breadcrumb: 'CUSTOMER.TICKETS.TITLE'
            }
        },
        {
            path: 'friends-family',
            loadComponent: () => import('./friends-family/customer-friends-family.component').then(m => m.CustomerFriendsFamilyComponent),
            data: {
                breadcrumb: 'CUSTOMER.FRIENDS_AND_FAMILY.TITLE'
            }
        },
        {
            path: 'loyalty-points',
            loadComponent: () => import('./loyalty-points/customer-loyalty-points.component').then(m => m.CustomerLoyaltyPointsComponent),
            data: {
                breadcrumb: 'CUSTOMER.LOYALTY_POINTS.TITLE'
            }
        },
        {
            path: 'notes',
            loadChildren: () => import('./notes/customer-notes-routes').then(m => m.CUSTOMER_NOTES_ROUTES),
            data: {
                breadcrumb: 'CUSTOMER.NOTES.TITLE'
            }
        }
    ]
}];

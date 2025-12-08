import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { CustomersListComponent } from './list/customers-list.component';

export const CUSTOMERS_ROUTES: Routes = [
    {
        path: '',
        component: CustomersListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':customerId',
        loadChildren: () => import('./customer/customer-detail.routes').then(m => m.CUSTOMER_DETAIL_ROUTES),
        data: {
            breadcrumb: 'TITLES.CUSTOMER_DETAILS'
        }
    }
];
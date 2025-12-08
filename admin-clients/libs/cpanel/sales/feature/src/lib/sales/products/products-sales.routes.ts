import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { ProductsSalesListComponent } from './list/products-sales-list.component';

export const PRODUCTS_SALES_ROUTES: Routes = [
    {
        path: '',
        component: ProductsSalesListComponent,
        canActivate: [authCanActivateGuard]

    },
    {
        path: ':orderCodeAndTicketId',
        loadChildren: () => import('./product/product-sales-routes').then(m => m.PRODUCT_SALE_ROUTES),
        data: {
            breadcrumb: 'productName'
        }
    }
];

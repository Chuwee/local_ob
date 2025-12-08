import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { productsProviders } from '@admin-clients/cpanel/products/my-products/data-access';
import { productsSaleRequestsProviders } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { Routes } from '@angular/router';
import { ProductsSaleRequestsListComponent } from './list/products-sale-requests-list.component';

export const PRODUCTS_SALE_REQUESTS_ROUTES: Routes = [{
    path: '',
    providers: [
        ...productsSaleRequestsProviders,
        ...productsProviders
    ],
    children: [
        {
            path: '',
            component: ProductsSaleRequestsListComponent,
            canActivate: [authCanActivateGuard]
        },
        {
            path: ':saleRequestId',
            loadChildren: () => import('./product-sale-request/product-sale-request.route').then(m => m.PRODUCT_SALE_REQUEST_ROUTES),
            data: {
                breadcrumb: 'productSaleRequestName'
            }
        }
    ]
}];

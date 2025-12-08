import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { productsProviders } from '@admin-clients/cpanel/products/my-products/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { productEventsProviders } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { Routes } from '@angular/router';
import { ProductsListComponent } from './list/products-list.component';

export const PRODUCTS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            ...productsProviders,
            ...productEventsProviders,
            ...entitiesProviders,
            ...eventsProviders
        ],
        children: [
            {
                path: '',
                component: ProductsListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':productId',
                loadChildren: () => import('./product/product.routes').then(m => m.PRODUCT_ROUTES),
                data: {
                    breadcrumb: 'productName'
                }
            }
        ]
    }

];

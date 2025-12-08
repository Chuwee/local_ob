import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { productsDeliveryPointsProviders } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { Routes } from '@angular/router';
import { deliveryPointDetailsResolver } from './delivery-point/details/delivery-point-details.resolver';
import { ProductsDeliveryPointsListComponent } from './list/products-delivery-points-list.component';

export const PRODUCTS_DELIVERY_POINTS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            ...productsDeliveryPointsProviders,
            ...entitiesProviders
        ],
        children: [
            {
                path: '',
                component: ProductsDeliveryPointsListComponent,
                canActivate: [authCanActivateGuard]

            },
            {
                path: ':deliveryPointId',
                resolve: {
                    deliveryPoint: deliveryPointDetailsResolver
                },
                loadChildren: () => import('./delivery-point/product-delivery-point.routes').then(m => m.PRODUCTS_DELIVERY_POINT_ROUTES),
                data: {
                    breadcrumb: 'deliveryPointName'
                }
            }
        ]
    }

];

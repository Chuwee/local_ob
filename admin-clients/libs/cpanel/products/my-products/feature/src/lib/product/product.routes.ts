import { productsDeliveryPointsProviders } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { eventSessionsProviders } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductDetailsComponent } from './details/product-details.component';
import { productDetailsResolver } from './details/product-details.resolver';

export const PRODUCT_ROUTES: Routes = [
    {
        path: '',
        component: ProductDetailsComponent,
        resolve: {
            product: productDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                loadChildren: () =>
                    import('@admin-clients/cpanel-products-my-products-general-data-feature').then(m => m.PRODUCT_GENERAL_DATA_ROUTES),
                data: {
                    breadcrumb: 'PRODUCT.GENERAL_DATA.TITLE'
                }
            },
            {
                path: 'taxes',
                loadComponent: () => import('./taxes/product-taxes.component').then(c => c.ProductTaxesComponent),
                data: {
                    breadcrumb: 'PRODUCT.TAXES_AND_SURCHARGES.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'communication',
                loadChildren: () =>
                    import('./communication/product-communication.routes').then(m => m.PRODUCT_COMMUNICATION_ROUTES),
                data: {
                    breadcrumb: 'PRODUCT.COMMUNICATION.TITLE'
                }
            },
            {
                path: 'events',
                providers: [
                    ...productsDeliveryPointsProviders,
                    ...eventSessionsProviders
                ],
                loadChildren: () =>
                    import('@admin-clients/cpanel-products-my-products-events-feature').then(m => m.PRODUCT_EVENTS_ROUTES),
                data: {
                    breadcrumb: 'PRODUCT.EVENTS.TITLE'
                }
            },
            {
                path: 'delivery',
                canDeactivate: [unsavedChangesGuard()],
                loadComponent: () =>
                    import('./delivery/product-delivery.component').then(m => m.ProductDeliveryComponent),
                data: {
                    breadcrumb: 'PRODUCT.DELIVERY.TITLE'
                }
            },
            {
                path: 'channels',
                loadChildren: () => import('./channels/product-channels.routes').then(m => m.PRODUCT_CHANNELS_ROUTES),
                data: {
                    breadcrumb: 'PRODUCT.CHANNELS.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'promotions',
                loadChildren: () => import('./promotions/product-promotions.routes').then(c => c.PRODUCT_PROMOTIONS_ROUTES),
                data: {
                    breadcrumb: 'PRODUCT.PROMOTIONS.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'design',
                loadComponent: () => import('./design/product-design.component').then(c => c.ProductDesignComponent),
                data: {
                    breadcrumb: 'PRODUCT.DESIGN.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

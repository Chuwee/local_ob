import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductEventDeliveryPointsComponent } from '../delivery-points/product-event-delivery-points.component';
import { ProductEventPricesComponent } from '../prices/product-event-sessions-prices.component';
import { ProductEventDetailsSessionsComponent } from '../sessions/product-event-details-sessions.component';
import { ProductEventStockComponent } from '../stock/product-event-sessions-stock.component';
import { productEventSessionsAndDeliveryResolver } from './product-event-details.resolvers';

export const PRODUCT_EVENT_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'sessions',
        pathMatch: 'full'
    },
    {
        path: 'sessions',
        component: ProductEventDetailsSessionsComponent,
        pathMatch: 'full',
        resolve: {
            sessions: productEventSessionsAndDeliveryResolver
        },
        data: {
            breadcrumb: 'PRODUCT.EVENTS.DETAIL.SESSIONS.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'stock',
        component: ProductEventStockComponent,
        pathMatch: 'full',
        resolve: {
            sessions: productEventSessionsAndDeliveryResolver
        },
        data: {
            breadcrumb: 'PRODUCT.EVENTS.DETAIL.STOCK.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'prices',
        component: ProductEventPricesComponent,
        pathMatch: 'full',
        resolve: {
            sessions: productEventSessionsAndDeliveryResolver
        },
        data: {
            breadcrumb: 'PRODUCT.EVENTS.DETAIL.PRICES.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'delivery-points',
        component: ProductEventDeliveryPointsComponent,
        data: {
            breadcrumb: 'PRODUCT.EVENTS.DETAIL.DELIVERY.TITLE'
        },
        resolve: {
            sessions: productEventSessionsAndDeliveryResolver
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];

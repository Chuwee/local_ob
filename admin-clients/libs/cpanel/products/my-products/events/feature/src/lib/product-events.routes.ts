
import { Routes } from '@angular/router';
import { ProductEventsContainerComponent } from './container/product-events-container.component';
import { ProductEventDetailsComponent } from './event/details/product-event-details.component';
import { productEventDetailsResolver } from './event/details/product-event-details.resolvers';

export const PRODUCT_EVENTS_ROUTES: Routes = [
    {
        path: '',
        component: ProductEventsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':eventId',
                component: ProductEventDetailsComponent,
                resolve: {
                    sessions: productEventDetailsResolver
                },
                data: {
                    breadcrumb: 'eventName'
                },
                loadChildren: () => import('./event/details/product-event-details.routes').then(m => m.PRODUCT_EVENT_DETAILS_ROUTES)
            }
        ]
    }
];

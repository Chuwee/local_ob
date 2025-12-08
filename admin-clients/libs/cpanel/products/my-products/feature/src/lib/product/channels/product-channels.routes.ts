
import { Routes } from '@angular/router';
import { ProductChannelDetailsComponent } from './channel/details/product-channel-details.component';
import { productChannelDetailsResolver } from './channel/details/product-channel-details.resolvers';
import { ProductChannelsContainerComponent } from './container/product-channels-container.component';

export const PRODUCT_CHANNELS_ROUTES: Routes = [
    {
        path: '',
        component: ProductChannelsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':channelId',
                component: ProductChannelDetailsComponent,
                resolve: {
                    configuration: productChannelDetailsResolver
                },
                data: {
                    breadcrumb: 'channelName'
                },
                loadChildren: () => import('./channel/details/product-channel-details.routes').then(m => m.PRODUCT_CHANNEL_DETAILS_ROUTES)
            }
        ]
    }
];

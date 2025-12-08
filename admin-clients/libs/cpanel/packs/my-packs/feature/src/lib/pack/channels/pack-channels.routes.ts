
import { Routes } from '@angular/router';
import { PackChannelDetailsComponent } from './channel/details/pack-channel-details.component';
import { packChannelDetailsResolver } from './channel/details/pack-channel-details.resolvers';
import { PackChannelsContainerComponent } from './container/pack-channels-container.component';

export const PACK_CHANNELS_ROUTES: Routes = [
    {
        path: '',
        component: PackChannelsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':channelId',
                component: PackChannelDetailsComponent,
                resolve: {
                    configuration: packChannelDetailsResolver
                },
                data: {
                    breadcrumb: 'channelName'
                },
                loadChildren: () => import('./channel/details/pack-channel-details.routes').then(m => m.PACK_CHANNEL_DETAILS_ROUTES)
            }
        ]
    }
];

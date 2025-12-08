import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { channelDetailsResolver } from './details/channel-details-resolver';
import { ChannelDetailsComponent } from './details/channel-details.component';

export const routes: Routes = [{
    path: '',
    component: ChannelDetailsComponent,
    resolve: {
        channel: channelDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            loadComponent: () => import('./general-data/channel-general-data.component').then(m => m.ChannelGeneralDataComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.GENERAL_DATA'
            }
        },
        {
            path: 'operative',
            loadChildren: () => import('./operative/channel-operative-routes').then(m => m.routes),
            data: {
                breadcrumb: 'CHANNELS.OPERATIVE'
            }
        },
        {
            path: 'design',
            loadChildren: () => import('@admin-clients/cpanel/channels/design/feature').then(m => m.DESIGN_ROUTES),
            data: {
                breadcrumb: 'CHANNELS.DESIGN.TITLE'
            }
        },
        {
            path: 'communication',
            loadChildren: () => import('./communication/channel-communication.module').then(m => m.ChannelCommunicationModule),
            data: {
                breadcrumb: 'CHANNELS.COMMUNICATION.TITLE'
            }
        },
        {
            path: 'configuration',
            loadChildren: () => import('./configuration/routes'),
            data: {
                breadcrumb: 'CHANNELS.CONFIGURATION'
            }
        },
        {
            path: 'admin-configuration',
            loadChildren: () =>
                import('@admin-clients/cpanel/migration/channels/feature').then(c => c.ADMIN_CHANNEL_CONFIGURATION),
            data: {
                breadcrumb: 'ADMIN_CHANNEL.CONFIGURATION.TITLE'
            }
        },
        {
            path: 'members',
            loadChildren: () => import('@admin-clients/cpanel-channels-member-external-feature').then(m => m.ChannelMemberExternalModule),
            data: {
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.TITLE'
            }
        },
        {
            path: 'promotions',
            loadChildren: () => import('./promotions/channel-promotions.module').then(m => m.ChannelPromotionsModule),
            data: {
                breadcrumb: 'CHANNELS.PROMOTIONS.TITLE'
            }
        },
        {
            path: 'packs',
            loadChildren: () => import('@admin-clients/cpanel/channels/packs/feature').then(m => m.PACKS_ROUTES),
            data: {
                breadcrumb: 'CHANNELS.PACKS.TITLE'
            }
        }
    ]
}];
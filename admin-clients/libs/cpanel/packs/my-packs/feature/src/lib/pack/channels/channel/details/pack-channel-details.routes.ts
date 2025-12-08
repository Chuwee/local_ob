import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PackChannelGeneralDataComponent } from '../general-data/pack-channel-general-data.component';
import { PackChannelOperativeComponent } from '../operative/pack-channel-operative.component';
import { PackChannelPreviewComponent } from '../preview/pack-channel-preview.component';

export const PACK_CHANNEL_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'general-data',
        pathMatch: 'full'
    },
    {
        path: 'general-data',
        component: PackChannelGeneralDataComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'PACK.CHANNELS.DETAIL.GENERAL_DATA.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'operative',
        component: PackChannelOperativeComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'PACK.CHANNELS.DETAIL.OPERATIVE.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'preview',
        component: PackChannelPreviewComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'PACK.CHANNELS.DETAIL.PREVIEW.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];

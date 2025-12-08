import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductChannelConfigurationComponent } from '../configuration/product-channel-configuration.component';
import { ProductChannelPreviewComponent } from '../preview/product-channel-preview.component';

export const PRODUCT_CHANNEL_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'configuration',
        pathMatch: 'full'
    },
    {
        path: 'configuration',
        component: ProductChannelConfigurationComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'PRODUCT.CHANNELS.DETAIL.CONFIGURATION.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'preview',
        component: ProductChannelPreviewComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'PRODUCT.CHANNELS.DETAIL.PREVIEW.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];

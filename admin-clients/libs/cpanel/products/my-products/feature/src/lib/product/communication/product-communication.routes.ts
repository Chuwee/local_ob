import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductChannelContentsComponent } from './channel-contents/product-channel-contents.component';
import { ProductCommunicationComponent } from './product-communication.component';
import { ProductTicketContentComponent } from './ticket-contents/product-ticket-content.component';

export const PRODUCT_COMMUNICATION_ROUTES: Routes = [
    {
        path: '',
        component: ProductCommunicationComponent,
        data: {
            breadcrumb: 'PRODUCT.COMMUNICATION.TITLE'
        },
        children: [
            {
                path: '',
                redirectTo: 'channel-contents',
                pathMatch: 'full'
            },
            {
                path: 'channel-contents',
                component: ProductChannelContentsComponent,
                data: {
                    breadcrumb: 'PRODUCT.CHANNEL_CONTENTS.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'ticket-contents',
                component: ProductTicketContentComponent,
                data: {
                    breadcrumb: 'PRODUCT.COMMUNICATION.TICKET_CONTENTS.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

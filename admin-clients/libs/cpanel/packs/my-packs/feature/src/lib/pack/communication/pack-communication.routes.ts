import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PackCommunicationComponent } from './pack-communication.component';
import { PackContentComponent } from './pack-content/pack-content.component';
import { PackTicketContentComponent } from './ticket-content/pack-ticket-content.component';

export const PACK_COMMUNICATION_ROUTES: Routes = [
    {
        path: '',
        component: PackCommunicationComponent,
        children: [
            {
                path: '',
                redirectTo: 'pack-contents',
                pathMatch: 'full'
            },
            {
                path: 'pack-contents',
                component: PackContentComponent,
                data: {
                    breadcrumb: 'PACK.COMMUNICATION.PACK.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'ticket-contents',
                component: PackTicketContentComponent,
                data: {
                    breadcrumb: 'PACK.COMMUNICATION.TICKET.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

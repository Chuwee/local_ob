import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EventChannelContentComponent } from './channel-content/event-channel-content.component';
import { EventCommunicationComponent } from './event-communication.component';
import { EventInvitationContentComponent } from './invitation-content/event-invitation-content.component';
import { EventTicketContentComponent } from './ticket-content/event-ticket-content/event-ticket-content.component';
import { PriceTypeTicketContentComponent } from './ticket-content/price-type-ticket-content/price-type-ticket-content.component';
import { TicketContentComponent } from './ticket-content/ticket-content.component';

const routes: Routes = [
    {
        path: '',
        component: EventCommunicationComponent,
        children: [
            {
                path: '',
                redirectTo: 'channel-contents',
                pathMatch: 'full'
            },
            {
                path: 'channel-contents',
                component: EventChannelContentComponent,
                data: {
                    breadcrumb: 'EVENTS.COMMUNICATION.CHANNEL.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'ticket-contents',
                component: TicketContentComponent,
                children: [
                    {
                        path: '',
                        redirectTo: 'event',
                        pathMatch: 'full'
                    },
                    {
                        path: 'event',
                        component: EventTicketContentComponent,
                        data: {
                            breadcrumb: 'EVENTS.COMMUNICATION.TICKET.BASE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'price-type',
                        component: PriceTypeTicketContentComponent,
                        data: {
                            breadcrumb: 'EVENTS.COMMUNICATION.TICKET.PRICE_TYPE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    }
                ],
                data: {
                    breadcrumb: 'EVENTS.COMMUNICATION.TICKET.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'invitation-contents',
                component: EventInvitationContentComponent,
                data: {
                    breadcrumb: 'EVENTS.COMMUNICATION.INVITATION.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EventCommunicationRoutingModule { }

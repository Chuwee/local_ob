import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketChannelContentComponent } from './channel-content/season-ticket-channel-content.component';
import { SeasonTicketCommunicationComponent } from './season-ticket-communication.component';
import { SeasonTicketTicketContentComponent } from './ticket-content/season-ticket-ticket-content.component';

const routes: Routes = [
    {
        path: '',
        component: SeasonTicketCommunicationComponent,
        children: [
            {
                path: '',
                redirectTo: 'channel-contents',
                pathMatch: 'full'
            },
            {
                path: 'channel-contents',
                component: SeasonTicketChannelContentComponent,
                data: {
                    breadcrumb: 'SEASON_TICKET.COMMUNICATION.CHANNEL.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'ticket-contents',
                component: SeasonTicketTicketContentComponent,
                data: {
                    breadcrumb: 'SEASON_TICKET.COMMUNICATION.TICKET.TITLE'
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
export class SeasonTicketCommunicationRoutingModule { }

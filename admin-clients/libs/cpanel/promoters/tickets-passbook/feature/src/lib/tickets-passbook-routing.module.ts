import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TicketPassbookListComponent } from './list/ticket-passbook-list.component';
import { ticketPassbookDetailsResolver } from './ticket-passbook/details/ticket-passbook-details-resolver';

const routes: Routes = [
    {
        path: '',
        component: TicketPassbookListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        loadChildren: () => import('./ticket-passbook/ticket-passbook.routes').then(m => m.routes),
        path: ':ticketPassbookId',
        resolve: {
            event: ticketPassbookDetailsResolver
        },
        data: {
            breadcrumb: 'TITLES.TICKET_PASSBOOK_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TicketsPassbookRoutingModule { }

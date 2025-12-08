import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TicketsListComponent } from './list/tickets-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        component: TicketsListComponent
    },
    {
        path: ':orderCodeAndTicketId',
        loadChildren: () => import('./ticket/ticket-details.module').then(m => m.TicketDetailsModule),
        data: {
            breadcrumb: 'TITLES.TICKET_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TicketsRoutingModule {
}

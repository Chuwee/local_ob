import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { TicketTemplatesListComponent } from './list/ticket-templates-list.component';

export const routes: Routes = [
    {
        path: '',
        component: TicketTemplatesListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':ticketTemplateId',
        loadChildren: () => import('./ticket-template/ticket-template.routes').then(m => m.routes),
        data: {
            breadcrumb: 'TITLES.TICKET_TEMPLATE_DETAILS'
        }
    }
];
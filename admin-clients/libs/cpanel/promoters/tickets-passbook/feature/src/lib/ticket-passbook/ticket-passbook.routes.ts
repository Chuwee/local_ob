import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { TicketPassbookDetailsComponent } from './details/ticket-passbook-details.component';
import { TicketPassbookLiteralsComponent } from './literals/ticket-passbook-literals.component';

export const routes: Routes = [{
    path: '',
    component: TicketPassbookDetailsComponent,
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            loadComponent: () => import('./general-data/ticket-passbook-general-data.component')
                .then(m => m.TicketPassbookGeneralDataComponent),
            data: {
                breadcrumb: 'TICKET_PASSBOOK.GENERAL_DATA'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'content',
            loadChildren: () => import('./content/ticket-passbook-content.module')
                .then(m => m.TicketPassbookContentModule),
            data: {
                breadcrumb: 'TICKET_PASSBOOK.CONTENT'
            }
        },
        {
            path: 'literals',
            component: TicketPassbookLiteralsComponent,
            data: {
                breadcrumb: 'TICKET_PASSBOOK.LITERALS'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];
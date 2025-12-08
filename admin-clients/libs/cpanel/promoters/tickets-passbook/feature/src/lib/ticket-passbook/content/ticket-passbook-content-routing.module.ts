import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { TicketPassbookBackPageComponent } from './back-page/ticket-passbook-back-page.component';
import { TicketPassbookCoverPageComponent } from './cover-page/ticket-passbook-cover-page.component';
import { TicketPassbookContentComponent } from './ticket-passbook-content.component';

const routes: Routes = [{
    path: '',
    component: TicketPassbookContentComponent,
    children: [
        {
            path: '',
            redirectTo: 'cover-page',
            pathMatch: 'full'
        },
        {
            path: 'cover-page',
            component: TicketPassbookCoverPageComponent,
            data: {
                breadcrumb: 'TICKET_PASSBOOK.COVER_PAGE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'back-page',
            component: TicketPassbookBackPageComponent,
            data: {
                breadcrumb: 'TICKET_PASSBOOK.BACK_PAGE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TicketPassbookContentRoutingModule {
}

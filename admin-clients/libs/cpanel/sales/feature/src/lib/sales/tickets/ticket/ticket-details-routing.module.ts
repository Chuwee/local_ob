import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ticketDetailsResolver } from './details/ticket-details-resolver';
import { TicketDetailsComponent } from './details/ticket-details.component';
import { TicketDetailsGeneralDataComponent } from './general-data/ticket-general-data.component';

const routes: Routes = [{
    path: '',
    component: TicketDetailsComponent,
    resolve: {
        ticket: ticketDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: TicketDetailsGeneralDataComponent
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TicketDetailsRoutingModule { }

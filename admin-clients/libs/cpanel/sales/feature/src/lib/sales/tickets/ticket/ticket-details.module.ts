import { TicketsApi, TicketsState, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { NgModule } from '@angular/core';
import { TicketDetailsRoutingModule } from './ticket-details-routing.module';

@NgModule({
    imports: [TicketDetailsRoutingModule],
    providers: [TicketsService, TicketsApi, TicketsState]
})
export class TicketDetailsModule { }

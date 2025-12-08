import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketRenewalsContainerComponent } from './container/season-ticket-renewals-container.component';

const routes: Routes = [{
    path: '',
    component: SeasonTicketRenewalsContainerComponent
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SeasonTicketsRenewalsRoutingModule {
}

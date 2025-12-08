import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketSessionsListComponent } from './season-ticket-sessions-list.component';

const routes: Routes = [{
    path: '',
    component: SeasonTicketSessionsListComponent,
    canDeactivate: [unsavedChangesGuard()]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SeasonTicketSessionsRoutingModule {
}

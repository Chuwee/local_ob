import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketCapacityComponent } from './season-ticket-capacity.component';

const routes: Routes = [{
    path: '',
    component: SeasonTicketCapacityComponent,
    canDeactivate: [unsavedChangesGuard()]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SeasonTicketCapacityRoutingModule {
}

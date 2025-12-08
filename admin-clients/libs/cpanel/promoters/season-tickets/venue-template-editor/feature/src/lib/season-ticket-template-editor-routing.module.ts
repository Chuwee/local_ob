import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketTemplateEditorContainerComponent } from './season-ticket-template-editor-container.component';
import { seasonTicketTemplateEditorResolver } from './season-ticket-template-editor-resolver';

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: SeasonTicketTemplateEditorContainerComponent,
        resolve: {
            venueTemplate: seasonTicketTemplateEditorResolver
        },
        data: {
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SeasonTicketTemplateEditorRoutingModule {
}

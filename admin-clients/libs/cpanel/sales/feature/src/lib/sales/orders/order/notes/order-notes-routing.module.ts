import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrderNotesContainerComponent } from './container/order-notes-container.component';
import { OrderNoteDetailsComponent } from './details/order-note-details.component';

const routes: Routes = [{
    path: '',
    component: OrderNotesContainerComponent,
    children: [
        {
            path: '',
            component: OrderNoteDetailsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'ORDER.NOTES.GENERAL_DATA'
            }
        },
        {
            path: ':noteId',
            component: OrderNoteDetailsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'ORDER.NOTES.GENERAL_DATA'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class OrderNotesRoutingModule {
}

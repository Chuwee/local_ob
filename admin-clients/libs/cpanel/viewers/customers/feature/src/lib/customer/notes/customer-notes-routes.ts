import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { CustomerNotesContainerComponent } from './container/customer-notes-container.component';

export const CUSTOMER_NOTES_ROUTES: Routes = [{
    path: '',
    component: CustomerNotesContainerComponent,
    children: [
        {
            path: '',
            loadComponent: () => import('./note/details/customer-note-details.component').then(m => m.CustomerNoteDetailsComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CUSTOMER.NOTES.GENERAL_DATA'
            }
        },
        {
            path: ':noteId',
            loadComponent: () => import('./note/details/customer-note-details.component').then(m => m.CustomerNoteDetailsComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CUSTOMER.NOTES.GENERAL_DATA'
            }
        }
    ]
}];
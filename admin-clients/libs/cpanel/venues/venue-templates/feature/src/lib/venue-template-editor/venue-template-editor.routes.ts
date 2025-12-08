import { Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { VenueTemplateEditorContainerComponent } from './venue-template-editor-container.component';

export const routes: Routes = [
    {
        path: '',
        component: VenueTemplateEditorContainerComponent,
        canDeactivate: [unsavedChangesGuard()]
    }
];

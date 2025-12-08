import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventTemplateEditorContainerComponent } from './event-template-editor-container.component';
import { eventTemplateEditorResolver } from './event-template-editor-resolver';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: '..'
    },
    {
        path: ':venueTemplateId',
        pathMatch: 'full',
        component: EventTemplateEditorContainerComponent,
        resolve: {
            venueTemplate: eventTemplateEditorResolver
        },
        data: {
            breadcrumb: 'templateName'
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];


import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PromoterTemplateEditorContainerComponent } from './promoter-template-editor-container.component';

export const PROMOTER_TEMPLATE_EDITOR_ROUTES: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: PromoterTemplateEditorContainerComponent,
        canDeactivate: [unsavedChangesGuard()],
        data: {
            breadcrumb: 'VENUE_TPLS.TEMPLATE_EDITOR'
        }
    }
];

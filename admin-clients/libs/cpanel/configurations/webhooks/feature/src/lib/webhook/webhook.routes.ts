import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { FormGroupDirective } from '@angular/forms';
import { Routes } from '@angular/router';
import { WebhooksDetailsComponent } from './details/webhooks-details.component';
import { WebhooksGeneralDataComponent } from './general-data/webhooks-general-data.component';

export const WEBHOOK_ROUTES: Routes = [
    {
        path: '',
        providers: [FormGroupDirective],
        component: WebhooksDetailsComponent,
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: WebhooksGeneralDataComponent,
                data: {
                    breadcrumb: 'WEBHOOK.GENERAL_DATA'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

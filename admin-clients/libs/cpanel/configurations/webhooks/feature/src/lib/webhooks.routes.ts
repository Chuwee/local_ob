import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { WebhookApi, WebhookService, WebhookState } from '@admin-clients/cpanel/shared/feature/webhook';
import { Routes } from '@angular/router';
import { WebhooksListComponent } from './list/webhooks-list.component';

export const WEBHOOKS_ROUTES: Routes = [
    {
        path: '',
        providers: [WebhookApi, WebhookState, WebhookService, entitiesProviders],
        children: [
            {
                path: '',
                component: WebhooksListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':webhookId',
                loadChildren: () => import('./webhook/webhook.routes').then(r => r.WEBHOOK_ROUTES),
                data: {
                    breadcrumb: 'TITLES.WEBHOOK_DETAILS'
                }
            }
        ]
    }
];

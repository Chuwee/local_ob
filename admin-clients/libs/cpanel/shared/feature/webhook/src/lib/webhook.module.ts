import { NgModule } from '@angular/core';
import { WebhookApi } from './api/webhook.api';
import { WebhookState } from './state/webhook.state';
import { WebhookComponent } from './webhook/webhook.component';
import { WebhookService } from './webhook.service';

export * from './webhook/webhook.component';
export * from './webhook.model';

@NgModule({
    imports: [
        WebhookComponent
    ],
    exports: [
        WebhookComponent
    ],
    providers: [
        WebhookService,
        WebhookState,
        WebhookApi
    ]
})
export class WebhookModule { }

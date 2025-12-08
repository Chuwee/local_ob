import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { first, Observable, tap } from 'rxjs';
import { Webhook } from '../webhook.model';

@Injectable()
export class WebhookState {

    readonly webhooks = new StateProperty<ListResponse<Webhook>>();
    readonly webhook = new StateProperty<Webhook>();
    readonly updateWebhook$ = (update: Partial<Webhook>): Observable<Webhook> =>
        this.webhook.getValue$().pipe(
            first(),
            tap(webhook => this.webhook.setValue({ ...webhook, ...update }))
        );

}

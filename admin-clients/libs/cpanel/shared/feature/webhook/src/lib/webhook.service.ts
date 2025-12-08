import { getListData, getMetadata, ListResponse, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { first, map, Observable, of } from 'rxjs';
import { WebhookApi } from './api/webhook.api';
import { WebhookState } from './state/webhook.state';
import type { GetWebhooksOptions, PostWebhook, PutWebhook, Webhook } from './webhook.model';

@Injectable()
export class WebhookService {
    readonly #webhooksApi = inject(WebhookApi);
    readonly #webhookState = inject(WebhookState);

    readonly webhooks = Object.freeze({
        load: (options: GetWebhooksOptions) => StateManager.load(
            this.#webhookState.webhooks,
            this.#webhooksApi.getWebhooks(options).pipe(mapMetadata())
        ),
        applyWebhooksUpdate: (webhooks: ListResponse<Webhook>) => StateManager.load(
            this.#webhookState.webhooks,
            of(webhooks).pipe(mapMetadata())
        ),
        get$: () => this.#webhookState.webhooks.getValue$().pipe(getListData()),
        getMetadata$: () => this.#webhookState.webhooks.getValue$().pipe(getMetadata()),
        inProgress$: () => this.#webhookState.webhooks.isInProgress$(),
        error$: () => this.#webhookState.webhooks.getError$(),
        clear: () => this.#webhookState.webhooks.setValue(null)
    });

    readonly webhook = Object.freeze({
        load: (webhookId: string) => StateManager.load(
            this.#webhookState.webhook,
            this.#webhooksApi.getWebhook(webhookId)
        ),
        get$: () => this.#webhookState.webhook.getValue$(),
        inProgress$: () => this.#webhookState.webhook.isInProgress$(),
        create$: (webhook: PostWebhook) => StateManager.inProgress(
            this.#webhookState.webhook,
            this.#webhooksApi.postWebhook(webhook)
        ),
        delete$: (webhookId: string) => StateManager.inProgress(
            this.#webhookState.webhook,
            this.#webhooksApi.deleteWebhook(webhookId)
        ),
        update$: (webhookId: string, webhook: PutWebhook) => StateManager.inProgress(
            this.#webhookState.webhook,
            this.#webhooksApi.putWebhook(webhookId, webhook)
        ),
        refreshApiKey$: (webhookId: string) => StateManager.inProgress(
            this.#webhookState.webhook,
            this.#webhooksApi.regenerateApiKeyWebhook(webhookId)
        ),
        error$: () => this.#webhookState.webhook.getError$(),
        clear: () => this.#webhookState.webhook.setValue(null)
    });

    getUpdatedWebhooksList(updatedWebhook: Webhook): Observable<ListResponse<Webhook>> {
        return this.#webhookState.webhooks.getValue$().pipe(
            first(),
            map(listResponse => {
                if (listResponse) {
                    const { data, metadata } = listResponse;
                    const updatedWebhooks = data?.map(webhook => {
                        if (webhook.id === updatedWebhook.id) {
                            return updatedWebhook;
                        }
                        return webhook;
                    });
                    return { data: updatedWebhooks, metadata };
                }

                return listResponse;
            })
        );
    }
}

import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetWebhooksOptions, PostWebhook, PutWebhook, Webhook } from '../webhook.model';

@Injectable()
export class WebhookApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly WEBHOOKS_URL = `${this.BASE_API}/mgmt-api/v1/webhooks`;

    private readonly _http = inject(HttpClient);

    getWebhooks(options: GetWebhooksOptions = {}): Observable<ListResponse<Webhook>> {
        const params = buildHttpParams(options);
        return this._http.get<ListResponse<Webhook>>(`${this.WEBHOOKS_URL}`, { params });
    }

    postWebhook(body: PostWebhook): Observable<Webhook> {
        return this._http.post<Webhook>(`${this.WEBHOOKS_URL}`, body);
    }

    getWebhook(id: string): Observable<Webhook> {
        return this._http.get<Webhook>(`${this.WEBHOOKS_URL}/${id}`);
    }

    putWebhook(id: string, body: PutWebhook): Observable<void> {
        return this._http.put<void>(`${this.WEBHOOKS_URL}/${id}`, body);
    }

    regenerateApiKeyWebhook(id: string): Observable<Webhook> {
        return this._http.put<Webhook>(`${this.WEBHOOKS_URL}/${id}/apikey/regenerate`, null);
    }

    deleteWebhook(id: string): Observable<void> {
        return this._http.delete<void>(`${this.WEBHOOKS_URL}/${id}`);
    }
}

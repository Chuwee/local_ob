import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSubscriptionListsRequest } from '../models/get-subscription-lists-request.model';
import { PostSubscriptionList } from '../models/post-subscription-list.model';
import { PutSubscriptionList } from '../models/put-subscription-list.model';
import { SubscriptionList } from '../models/subscription-list.model';

@Injectable({
    providedIn: 'root'
})
export class SubscriptionListsApi {

    private readonly BASE_API = inject(APP_BASE_API);

    private readonly SUBCRIPTION_LISTS_API = `${this.BASE_API}/mgmt-api/v1/subscription-lists`;

    private readonly _http = inject(HttpClient);

    getSubscriptionLists(request: GetSubscriptionListsRequest): Observable<SubscriptionList[]> {
        const params = buildHttpParams({
            entity_id: request?.entityId,
            status: request?.status,
            q: request?.q
        });
        return this._http.get<SubscriptionList[]>(`${this.SUBCRIPTION_LISTS_API}`, { params });
    }

    getSubscriptionList(id: number): Observable<SubscriptionList> {
        return this._http.get<SubscriptionList>(`${this.SUBCRIPTION_LISTS_API}/${id}`);
    }

    deleteSubscriptionList(id: number): Observable<void> {
        return this._http.delete<void>(`${this.SUBCRIPTION_LISTS_API}/${id}`);
    }

    postSubscriptionList(subscriptionList: PostSubscriptionList): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.SUBCRIPTION_LISTS_API}`, subscriptionList);
    }

    putSubscriptionList(id: number, subscriptionList: PutSubscriptionList): Observable<void> {
        return this._http.put<void>(`${this.SUBCRIPTION_LISTS_API}/${id}`, subscriptionList);
    }

}

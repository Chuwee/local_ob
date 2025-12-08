import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportDelivery, ExportField, ExportFormat, ExportResponse, Id } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetBuyerOrderItemsRequest } from '../models/_index';
import { Buyer } from '../models/buyer.model';
import { BuyersFilterContent } from '../models/buyers-filter-content.model';
import { BuyersFilterField } from '../models/buyers-filter-field.enum';
import { BuyersQueryDef } from '../models/buyers-query-def.model';
import { BuyersQueryList } from '../models/buyers-query-list.model';
import { BuyersQueryWrapper } from '../models/buyers-query-wrapper.model';
import { BuyersQuery } from '../models/buyers-query.model';
import { GetBuyerResponse } from '../models/get-buyer-response.model';
import { BuyerOrderItemList } from '../models/order-items/buyer-order-item-list.model';

@Injectable({
    providedIn: 'root'
})
export class BuyersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BUYERS_API = `${this.BASE_API}/customers-mgmt-api/v1/buyers`;

    private readonly _http = inject(HttpClient);

    getBuyers(request: BuyersQuery): Observable<GetBuyerResponse> {
        const params = buildHttpParams({
            sort: request?.sort,
            aggs: request?.aggs,
            limit: request?.limit,
            offset: request?.offset
        });
        return this._http.post<GetBuyerResponse>(this.BUYERS_API + '/search', request, { params });
    }

    getBuyer(id: string): Observable<Buyer> {
        return this._http.get<Buyer>(`${this.BUYERS_API}/${id}`);
    }

    postBuyer(buyer: Buyer): Observable<{ id: string }> {
        return this._http.post<{ id: string }>(this.BUYERS_API, buyer);
    }

    putBuyer(buyer: Buyer): Observable<void> {
        return this._http.put<void>(`${this.BUYERS_API}/${buyer.id}`, buyer);
    }

    deleteBuyer(id: string): Observable<void> {
        return this._http.delete<void>(`${this.BUYERS_API}/${id}`);
    }

    exportBuyers(filter: BuyersQuery, format: ExportFormat, fields: ExportField[], delivery: ExportDelivery): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(this.BUYERS_API + '/exports', { filter, format, fields, delivery }, {});
    }

    getBuyerFilters(
        { filterField, entityId, event, limit }: { filterField: BuyersFilterField; entityId: number; event?: number; limit: number }
    ): Observable<BuyersFilterContent> {
        const params = buildHttpParams({
            entity_id: entityId,
            event_id: event,
            limit
        });
        return this._http.get<BuyersFilterContent>(this.BUYERS_API + '/filters/' + filterField, { params });
    }

    getBuyersQueries(): Observable<BuyersQueryList> {
        return this._http.get<BuyersQueryList>(this.BUYERS_API + '/queries');
    }

    getBuyersQuery(queryId: number): Observable<BuyersQueryWrapper> {
        return this._http.get<BuyersQueryWrapper>(this.BUYERS_API + '/queries/' + queryId);
    }

    postBuyersQuery(query: BuyersQueryWrapper): Observable<Id> {
        return this._http.post<Id>(this.BUYERS_API + '/queries', query);
    }

    putBuyersQuery(id: number, query: BuyersQueryDef | BuyersQueryWrapper): Observable<void> {
        return this._http.put<void>(this.BUYERS_API + '/queries/' + id, query);
    }

    deleteBuyersQuery(id: number): Observable<void> {
        return this._http.delete<void>(this.BUYERS_API + '/queries/' + id);
    }

    getBuyerOrderItems(id: string, filter: GetBuyerOrderItemsRequest): Observable<BuyerOrderItemList> {
        const params = buildHttpParams(filter);
        return this._http.get<BuyerOrderItemList>(`${this.BUYERS_API}/${id}/order-items`, { params });
    }
}

import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetProducersResponse } from '../models/get-producers-response.model';
import { PostProducer } from '../models/post-producer.model';
import { ProducerDetails, PutProducerDetails } from '../models/producer-details.model';
import {
    PostProducerInvoicePrefix, PutProducerInvoicePrefix, RequestProducerInvoicePrefixes, GetProducerInvoicePrefixes
} from '../models/producer-invoice-prefix.model';
import {
    GetProducerInvoiceProviderResponse, PostProducerInvoiceProviderResponse, ProducerInvoiceProviderOptions
} from '../models/producer-invoice-provider.model';
import { ProducerStatus } from '../models/producer-status.model';
import { ProducersFilterFields } from '../models/producers-filter-fields.model';

@Injectable({ providedIn: 'root' })
export class ProducersApi {
    private readonly _http = inject(HttpClient);
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly PRODUCERS_API = `${this.BASE_API}/mgmt-api/v1/producers`;

    getProducers(
        limit = 20,
        offset = 0,
        sort = null, // "(name|status):(asc|desc)"
        filter = '',
        fields: ProducersFilterFields[] | null = null,
        entityId: number | null = null,
        status: ProducerStatus[] | null = null
    ): Observable<GetProducersResponse> {
        const params = buildHttpParams({
            limit,
            offset,
            sort,
            q: filter,
            entity_id: entityId,
            fields,
            status
        });
        return this._http.get<GetProducersResponse>(this.PRODUCERS_API, { params });
    }

    getProducer(producerId: number): Observable<ProducerDetails> {
        return this._http.get<ProducerDetails>(`${this.PRODUCERS_API}/${producerId}`);
    }

    putProducerDetails(producer: PutProducerDetails): Observable<void> {
        return this._http.put<void>(`${this.PRODUCERS_API}/${producer.id}`, producer);
    }

    deleteProducer(producerId: string): Observable<void> {
        return this._http.delete<void>(`${this.PRODUCERS_API}/${producerId}`);
    }

    postProducer(producer: PostProducer): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(this.PRODUCERS_API, producer);
    }

    // Producer Invoices

    getInvoicePrefixes(producerId: number, request?: RequestProducerInvoicePrefixes): Observable<GetProducerInvoicePrefixes> {
        const params = buildHttpParams(request);
        return this._http.get<GetProducerInvoicePrefixes>(`${this.PRODUCERS_API}/${producerId}/invoice-prefixes`, { params });
    }

    postInvoicePrefix(producerId: number, payload: PostProducerInvoicePrefix): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.PRODUCERS_API}/${producerId}/invoice-prefixes`, payload);
    }

    putInvoicePrefix(producerId: number, invoicePrefixId: number, payload: PutProducerInvoicePrefix): Observable<void> {
        return this._http.put<void>(`${this.PRODUCERS_API}/${producerId}/invoice-prefixes/${invoicePrefixId}`, payload);
    }

    getInvoiceProvider(producerId: number): Observable<GetProducerInvoiceProviderResponse> {
        return this._http.get<GetProducerInvoiceProviderResponse>(
            `${this.PRODUCERS_API}/${producerId}/invoice-providers`);
    }

    postInvoiceProvider(producerId: number, provider: string): Observable<PostProducerInvoiceProviderResponse> {
        return this._http.post<PostProducerInvoiceProviderResponse>(`${this.PRODUCERS_API}/${producerId}/invoice-providers`, { provider });
    }

    getInvoiceProviderOptions(producerId: number): Observable<ProducerInvoiceProviderOptions[]> {
        return this._http.get<ProducerInvoiceProviderOptions[]>(
            `${this.PRODUCERS_API}/${producerId}/invoice-providers/options`);
    }
}

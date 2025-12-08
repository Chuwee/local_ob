import { mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, switchMap, tap } from 'rxjs/operators';
import { ProducersApi } from './api/producers.api';
import { GetProducersRequest } from './models/get-producers-request.model';
import { PostProducer } from './models/post-producer.model';
import { ProducerDetails, PutProducerDetails } from './models/producer-details.model';
import {
    PostProducerInvoicePrefix, ProducerInvoicePrefix, PutProducerInvoicePrefix, RequestProducerInvoicePrefixes
} from './models/producer-invoice-prefix.model';
import { ProducerStatus } from './models/producer-status.model';
import { Producer } from './models/producer.model';
import { ProducersFilterFields } from './models/producers-filter-fields.model';
import { ProducersState } from './state/producers.state';

@Injectable({ providedIn: 'root' })
export class ProducersService {
    private readonly _api = inject(ProducersApi);
    private readonly _state = inject(ProducersState);

    readonly invoiceProvider = Object.freeze({
        load: (producerId: number) => StateManager.load(
            this._state.invoiceProvider,
            this._api.getInvoiceProvider(producerId)
        ),
        save: (producerId: number, provider: string) => StateManager.inProgress(
            this._state.invoiceProvider,
            this._api.postInvoiceProvider(producerId, provider)
                .pipe(tap(invoiceProvider => this._state.invoiceProvider.setValue(invoiceProvider)))
        ),
        get$: () => this._state.invoiceProvider.getValue$(),
        loading$: () => this._state.invoiceProvider.isInProgress$(),
        clear: () => this._state.invoiceProvider.setValue(null)
    });

    readonly invoiceProviderOptions = Object.freeze({
        load: (producerId: number) => StateManager.load(
            this._state.invoiceProviderOptions,
            this._api.getInvoiceProviderOptions(producerId)
        ),
        get$: () => this._state.invoiceProviderOptions.getValue$(),
        loading$: () => this._state.invoiceProviderOptions.isInProgress$(),
        clear: () => this._state.invoiceProviderOptions.setValue(null)
    });

    readonly invoicePrefixes = Object.freeze({
        loadIfNull: (producerId: number, request: RequestProducerInvoicePrefixes = { limit: 20 }) => StateManager.loadIfNull(
            this._state.invoicePrefixes,
            this._api.getInvoicePrefixes(producerId, request)
        ),
        getData$: () => this._state.invoicePrefixes.getValue$().pipe(map(response => response?.data)),
        loading$: () => this._state.invoicePrefixes.isInProgress$(),
        clear: () => this._state.invoicePrefixes.setValue(null)
    });

    readonly producer = Object.freeze({
        load: (producerId: number) => StateManager.load(
            this._state.producer,
            this._api.getProducer(producerId)
        ),
        get$: () => this._state.producer.getValue$(),
        loading$: () => this._state.producer.isInProgress$(),
        clear: () => this._state.producer.setValue(null)
    });

    readonly producersList = Object.freeze({
        load: (request: GetProducersRequest) =>
            StateManager.load(this._state.producersList,
                this._api.getProducers(request.limit, request.offset, request.sort, request.q, request.fields, request.entityId, request.status)),
        loadMore: (request: GetProducersRequest) =>
            StateManager.loadMore(request, this._state.producersList, r => this._api.getProducers(r.limit, r.offset, r.sort, r.q, r.fields, r.entityId, r.status)),
        clear: () => this._state.producersList.setValue(null),
        getData$: () => this._state.producersList.getValue$().pipe(map(v => v?.data)),
        getMetadata$: () => this._state.producersList.getValue$().pipe(
            map(list => list?.metadata && Object.assign(new Metadata(), list.metadata))
        ),
        setEmpty: () => this._state.producersList.setValue({
            data: [], metadata: Object.assign(new Metadata(), { offset: 0, total: 0 })
        }),
        isLoading$: () => this._state.producersList.isInProgress$()
    });

    loadProducersList(
        limit?: number,
        offset?: number,
        sort?: string,
        filter?: string,
        fields?: ProducersFilterFields[] | null,
        entityId?: number | null,
        status?: ProducerStatus[] | null
    ): void {
        this._state.producersList.setInProgress(true);
        this._api.getProducers(limit, offset, sort, filter, fields, entityId, status)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._state.producersList.setInProgress(false))
            )
            .subscribe(producers =>
                this._state.producersList.setValue(producers)
            );
    }

    getProducersListData$(): Observable<Producer[]> {
        return this._state.producersList.getValue$()
            .pipe(map(producers => producers?.data));
    }

    getProducersListMetadata$(): Observable<Metadata> {
        return this._state.producersList.getValue$()
            .pipe(map(r => r?.metadata));
    }

    isProducersListLoading$(): Observable<boolean> {
        return this._state.producersList.isInProgress$();
    }

    clearProducersList(): void {
        this._state.producersList.setValue(null);
    }

    isProducerLoading$(): Observable<boolean> {
        return this._state.producer.isInProgress$();
    }

    loadProducer(producerId: number): void {
        this._state.producer.setError(null);
        this._state.producer.setInProgress(true);
        this._api.getProducer(producerId)
            .pipe(
                catchError(error => {
                    this._state.producer.setError(error);
                    return of(null);
                }),
                finalize(() => this._state.producer.setInProgress(false))
            )
            .subscribe(producer =>
                this._state.producer.setValue(producer)
            );
    }

    getProducer$(): Observable<ProducerDetails> {
        return this._state.producer.getValue$();
    }

    getProducerError$(): Observable<HttpErrorResponse> {
        return this._state.producer.getError$();
    }

    clearProducer(): void {
        this._state.producer.setValue(null);
    }

    saveProducerDetails(producerDetails: PutProducerDetails): Observable<void> {
        this._state.producerSaving.setValue(true);
        this._state.producerSaving.setError(null);
        return this._api.putProducerDetails(producerDetails)
            .pipe(
                catchError(error => {
                    this._state.producerSaving.setError(error);
                    throw error;
                }),
                finalize(() => this._state.producerSaving.setValue(false))
            );
    }

    deleteProducer(id: string): Observable<void> {
        return this._api.deleteProducer(id);
    }

    createProducer(producer: PostProducer): Observable<number> {
        this._state.producer.setError(null);
        this._state.producerSaving.setValue(true);
        return this._api.postProducer(producer)
            .pipe(
                catchError(error => {
                    this._state.producer.setError(error);
                    return of(null);
                }),
                map(result => result.id),
                finalize(() => this._state.producerSaving.setValue(false))
            );
    }

    isProducerSaving$(): Observable<boolean> {
        return this._state.producerSaving.getValue$();
    }

    // Producer Invoices

    loadInvoicePrefixes(producerId: number, request: RequestProducerInvoicePrefixes = { limit: 20 }): void {
        this._state.invoicePrefixes.setError(null);
        this._state.invoicePrefixes.setInProgress(true);
        this._api.getInvoicePrefixes(producerId, request)
            .pipe(
                catchError(error => {
                    this._state.invoicePrefixes.setError(error);
                    throw error;
                }),
                finalize(() => this._state.invoicePrefixes.setInProgress(false))
            )
            .subscribe(invoicePrefixes =>
                this._state.invoicePrefixes.setValue(invoicePrefixes)
            );
    }

    getInvoicePrefixesData$(): Observable<ProducerInvoicePrefix[]> {
        return this._state.invoicePrefixes.getValue$().pipe(map(producers => producers?.data));
    }

    isInvoicePrefixesLoading$(): Observable<boolean> {
        return this._state.invoicePrefixes.isInProgress$();
    }

    clearInvoicePrefixes(): void {
        this._state.invoicePrefixes.setValue(null);
    }

    createInvoicePrefix(producerId: number, prefix: PostProducerInvoicePrefix): Observable<number> {
        this._state.invoicePrefixes.setInProgress(true);
        return this._api.postInvoicePrefix(producerId, prefix)
            .pipe(
                map(result => result.id),
                finalize(() => this._state.invoicePrefixes.setInProgress(false))
            );
    }

    updateInvoicePrefix(producerId: number, invoicePrefixId: number, payload: PutProducerInvoicePrefix): Observable<boolean> {
        this._state.invoicePrefixes.setInProgress(true);
        return this._api.putInvoicePrefix(producerId, invoicePrefixId, payload)
            .pipe(
                switchMap(() => of(true)), catchError(() => of(false)),
                finalize(() => this._state.invoicePrefixes.setInProgress(false))
            );
    }

    getProducersNames$(ids: number[]): Observable<IdName[]> {
        return this._state.producersCache.getItems$(ids, id => (this._api.getProducer(id)) as Observable<IdName>);
    }
}

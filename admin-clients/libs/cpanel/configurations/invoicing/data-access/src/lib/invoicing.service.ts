import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { finalize, Observable } from 'rxjs';
import { InvoicingApi } from './api/invoicing.api';
import { GetInvoicingEntityEventsRequest } from './models/get-invoicing-entity-events-request.model';
import { InvoicingEntityFilterItem } from './models/invoicing-entities-filter.model';
import {
    GetInvoicingEntityConfigRequest, InvoicingEntityConfigRequest,
    InvoicingEntityConfiguration
} from './models/invoicing-entity-configuration.model';
import { InvoicingEntityOperatorTypes } from './models/invoicing-entity-operator-types.enum';
import { PostInvoicingReport } from './models/invoicing-report.model';
import { InvoicingState } from './state/invoicing.state';

@Injectable({
    providedIn: 'root'
})
export class InvoicingService {

    readonly invoicingEntityEvents = Object.freeze({
        load: (entityId: number, request: GetInvoicingEntityEventsRequest) => StateManager.load(
            this._invoicingState.invoicingEvents, this._invoicingApi.getInvoicingEntitieEvents(entityId, request).pipe(mapMetadata())
        ),
        getList$: () => this._invoicingState.invoicingEvents.getValue$().pipe(getListData()),
        getMetadata$: () => this._invoicingState.invoicingEvents.getValue$().pipe(getMetadata()),
        loading$: () => this._invoicingState.invoicingEvents.isInProgress$(),
        clear: () => this._invoicingState.invoicingEvents.setValue(null)
    });

    constructor(
        private _invoicingApi: InvoicingApi,
        private _invoicingState: InvoicingState
    ) { }

    loadEntitiesConfigs(entityConfig: GetInvoicingEntityConfigRequest = { type: InvoicingEntityOperatorTypes.undefined }): void {
        this._invoicingState.setInvoicingEntitiesConfigsLoading(true);
        this._invoicingApi.getEntitiesConfigs(entityConfig)
            .pipe(finalize(() => this._invoicingState.setInvoicingEntitiesConfigsLoading(false)))
            .subscribe(entities => this._invoicingState.setInvoicingEntitiesConfigs(entities));
    }

    getEntitiesConfigs$(): Observable<InvoicingEntityConfiguration[]> {
        return this._invoicingState.getInvoicingEntitiesConfigs$();
    }

    clearEntitiesConfigs(): void {
        this._invoicingState.setInvoicingEntitiesConfigs(null);
    }

    isEntitiesConfigsLoading$(): Observable<boolean> {
        return this._invoicingState.isInvoicingEntitiesConfigsLoading$();
    }

    createEntityConfig(id: number, config: InvoicingEntityConfigRequest): Observable<void> {
        this._invoicingState.setEntityConfigSaving(true);
        return this._invoicingApi.postEntityConfig(id, config)
            .pipe(finalize(() => this._invoicingState.setEntityConfigSaving(false)));
    }

    updateEntityConfig(id: number, config: InvoicingEntityConfigRequest): Observable<void> {
        this._invoicingState.setEntityConfigSaving(true);
        return this._invoicingApi.putEntityConfig(id, config)
            .pipe(finalize(() => this._invoicingState.setEntityConfigSaving(false)));
    }

    isEntityConfigSaving$(): Observable<boolean> {
        return this._invoicingState.isEntityConfigSaving$();
    }

    loadInvoicingEntitiesFilter(): void {
        this._invoicingState.setInvoicingEntitiesFilterLoading(true);
        this._invoicingApi.getInvoicingEntitiesFilter()
            .pipe(finalize(() => this._invoicingState.setInvoicingEntitiesFilterLoading(false)))
            .subscribe(entities => this._invoicingState.setInvoicingEntitiesFilter(entities));
    }

    getInvoicingEntitiesFilter$(): Observable<InvoicingEntityFilterItem[]> {
        return this._invoicingState.getInvoicingEntitiesFilter$();
    }

    clearInvoicingEntitiesFilter(): void {
        this._invoicingState.setInvoicingEntitiesFilter(null);
    }

    isInvoicingEntitiesFilterLoading$(): Observable<boolean> {
        return this._invoicingState.isInvoicingEntitiesFilterLoading$();
    }

    generateInvoicingReport(report: PostInvoicingReport): Observable<void> {
        this._invoicingState.setInvoicingReportGenerating(true);
        return this._invoicingApi.postInvoicingReport(report)
            .pipe(finalize(() => this._invoicingState.setInvoicingReportGenerating(false)));
    }

    isInvoicingReportGenerating$(): Observable<boolean> {
        return this._invoicingState.isInvoicingReportGenerating$();
    }
}

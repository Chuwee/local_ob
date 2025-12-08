import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { InvoicingEntityFilterItem } from '../models/invoicing-entities-filter.model';
import { InvoicingEntityConfiguration } from '../models/invoicing-entity-configuration.model';

@Injectable({
    providedIn: 'root'
})
export class InvoicingState {

    private _generatingInvoicingReport = new BaseStateProp<boolean>();
    readonly setInvoicingReportGenerating = this._generatingInvoicingReport.setInProgressFunction();
    readonly isInvoicingReportGenerating$ = this._generatingInvoicingReport.getInProgressFunction();

    private _invoicingEntitiesFilter = new BaseStateProp<InvoicingEntityFilterItem[]>();
    readonly setInvoicingEntitiesFilter = this._invoicingEntitiesFilter.setValueFunction();
    readonly getInvoicingEntitiesFilter$ = this._invoicingEntitiesFilter.getValueFunction();
    readonly setInvoicingEntitiesFilterLoading = this._invoicingEntitiesFilter.setInProgressFunction();
    readonly isInvoicingEntitiesFilterLoading$ = this._invoicingEntitiesFilter.getInProgressFunction();

    private _invoicingEntitiesConfigs = new BaseStateProp<InvoicingEntityConfiguration[]>();
    readonly setInvoicingEntitiesConfigs = this._invoicingEntitiesConfigs.setValueFunction();
    readonly getInvoicingEntitiesConfigs$ = this._invoicingEntitiesConfigs.getValueFunction();
    readonly setInvoicingEntitiesConfigsLoading = this._invoicingEntitiesConfigs.setInProgressFunction();
    readonly isInvoicingEntitiesConfigsLoading$ = this._invoicingEntitiesConfigs.getInProgressFunction();

    private _entityConfigSaving = new BaseStateProp<void>();
    readonly setEntityConfigSaving = this._entityConfigSaving.setInProgressFunction();
    readonly isEntityConfigSaving$ = this._entityConfigSaving.getInProgressFunction();

    readonly invoicingEvents = new StateProperty<ListResponse<IdName>>();
}

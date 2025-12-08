import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetInvoicingEntityEventsRequest } from '../models/get-invoicing-entity-events-request.model';
import { InvoicingEntityFilterItem } from '../models/invoicing-entities-filter.model';
import {
    InvoicingEntityConfiguration,
    InvoicingEntityConfigRequest,
    GetInvoicingEntityConfigRequest
} from '../models/invoicing-entity-configuration.model';
import { PostInvoicingReport } from '../models/invoicing-report.model';

@Injectable({
    providedIn: 'root'
})
export class InvoicingApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly INVOICING_API = `${this.BASE_API}/mgmt-api/v1/onebox-invoicing`;

    constructor(private _http: HttpClient) { }

    postInvoicingReport(report: PostInvoicingReport): Observable<void> {
        return this._http.post<void>(this.INVOICING_API, report);
    }

    getEntitiesConfigs(entityConfig: GetInvoicingEntityConfigRequest): Observable<InvoicingEntityConfiguration[]> {
        return this._http.get<InvoicingEntityConfiguration[]>(`${this.INVOICING_API}/entities`, {
            params: buildHttpParams(entityConfig)
        });
    }

    postEntityConfig(id: number, entityConfig: InvoicingEntityConfigRequest): Observable<void> {
        return this._http.post<void>(`${this.INVOICING_API}/entities/${id}`, entityConfig);
    }

    putEntityConfig(id: number, entityConfig: InvoicingEntityConfigRequest): Observable<void> {
        return this._http.put<void>(`${this.INVOICING_API}/entities/${id}/type/${entityConfig.type}`, entityConfig);
    }

    getInvoicingEntitiesFilter(): Observable<InvoicingEntityFilterItem[]> {
        return this._http.get<InvoicingEntityFilterItem[]>(`${this.INVOICING_API}/entities-filter`);
    }

    getInvoicingEntitieEvents(entityId: number, request: GetInvoicingEntityEventsRequest): Observable<ListResponse<IdName>> {
        const params = buildHttpParams(request);
        return this._http.get<ListResponse<IdName>>(`${this.INVOICING_API}/entities/${entityId}/events`, { params });
    }
}

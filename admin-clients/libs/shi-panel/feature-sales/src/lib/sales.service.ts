import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { inject, Injectable } from '@angular/core';
import { map } from 'rxjs';
import { GetSalesRequest } from './models/get-sales-request.model';
import { SalesApi } from './sales.api';
import { SalesState } from './state/sales.state';

@Injectable()
export class SalesService {
    private readonly _salesApi = inject(SalesApi);
    private readonly _salesState = inject(SalesState);

    readonly list = Object.freeze({
        load: (request: GetSalesRequest) => StateManager.load(this._salesState.list, this._salesApi.getSales(request).pipe(mapMetadata())),
        getData$: () => this._salesState.list.getValue$().pipe(getListData()),
        getMetadata$: () => this._salesState.list.getValue$().pipe(getMetadata()),
        getAggData$: () => this._salesState.list.getValue$().pipe(map(sales => sales?.aggregated_data)),
        loading$: () => this._salesState.list.isInProgress$(),
        exportLoading$: () => this._salesState.listExport.isInProgress$(),
        exportSaleslist: (request: GetSalesRequest, data: ExportRequest) => StateManager.inProgress(
            this._salesState.listExport,
            this._salesApi.exportSalesList(request, data)
        ),
        exportSalesDailyList: () => StateManager.inProgress(
            this._salesState.listExport,
            this._salesApi.exportSalesDailyList()
        )
    });

    readonly details = Object.freeze({
        load: (id: number) => StateManager.load(this._salesState.details, this._salesApi.getSaleDetails(id)),
        getData$: () => this._salesState.details.getValue$(),
        loading$: () => this._salesState.details.isInProgress$(),
        relaunchSale: (id: number) => StateManager.inProgress(
            this._salesState.relaunchSale,
            this._salesApi.relaunchSale(id)
        ),
        relaunchFulfill: (id: number) => StateManager.inProgress(
            this._salesState.relaunchFulfill,
            this._salesApi.relaunchFulfill(id)
        )
    });

    readonly transitions = Object.freeze({
        load: (id: number) => StateManager.load(
            this._salesState.transitions,
            this._salesApi.getSaleTransitions(id)
        ),
        loading$: () => this._salesState.transitions.isInProgress$(),
        get$: () => this._salesState.transitions.getValue$()
    });

    readonly deliveryMethods = Object.freeze({
        load: () => StateManager.load(
            this._salesState.deliveryMethods,
            this._salesApi.getDeliveryMethods()
        ),
        get$: () => this._salesState.deliveryMethods.getValue$().pipe(map(methods => methods?.data)),
        loading$: () => this._salesState.deliveryMethods.isInProgress$()
    });

    readonly countries = Object.freeze({
        load: () => StateManager.load(this._salesState.countries, this._salesApi.getCountries().pipe(mapMetadata())),
        getData$: () => this._salesState.countries.getValue$().pipe(getListData()),
        getMetadata$: () => this._salesState.countries.getValue$().pipe(getMetadata()),
        loading$: () => this._salesState.countries.isInProgress$()
    });

    readonly currencies = Object.freeze({
        load: () => StateManager.load(this._salesState.currencies, this._salesApi.getCurrencies().pipe(mapMetadata())),
        getData$: () => this._salesState.currencies.getValue$().pipe(getListData()),
        getMetadata$: () => this._salesState.currencies.getValue$().pipe(getMetadata()),
        loading$: () => this._salesState.currencies.isInProgress$()
    });

    readonly taxonomies = Object.freeze({
        load: () => StateManager.load(this._salesState.taxonomies, this._salesApi.getTaxonomies().pipe(mapMetadata())),
        getData$: () => this._salesState.taxonomies.getValue$().pipe(getListData()),
        getMetadata$: () => this._salesState.taxonomies.getValue$().pipe(getMetadata()),
        loading$: () => this._salesState.taxonomies.isInProgress$()
    });

    readonly lastErrors = Object.freeze({
        load: () => StateManager.load(
            this._salesState.lastErrors,
            this._salesApi.getLastErrors()
        ),
        get$: () => this._salesState.lastErrors.getValue$().pipe(map(methods => methods?.data)),
        loading$: () => this._salesState.lastErrors.isInProgress$()
    });
}

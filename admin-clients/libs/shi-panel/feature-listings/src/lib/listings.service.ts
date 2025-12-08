import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ListingsApi } from './listings.api';
import { GetListingsRequest } from './models/get-listings-request.model';
import { ListingsState } from './state/listings.state';

@Injectable()
export class ListingsService {
    private readonly _listingsApi = inject(ListingsApi);
    private readonly _listingsState = inject(ListingsState);

    readonly list = Object.freeze({
        load: (request: GetListingsRequest) => StateManager.load(
            this._listingsState.list, this._listingsApi.getListings(request).pipe(mapMetadata())
        ),
        getListingsListData$: () => this._listingsState.list.getValue$().pipe(getListData()),
        getListingsListMetadata$: () => this._listingsState.list.getValue$().pipe(getMetadata()),
        getAggData$: () => this._listingsState.list.getValue$().pipe(map(listings => listings?.aggregated_data)),
        loading$: () => this._listingsState.list.isInProgress$(),
        exportLoading$: () => this._listingsState.listExport.isInProgress$(),
        exportListingslist: (request: GetListingsRequest, data: ExportRequest) => StateManager.inProgress(
            this._listingsState.listExport, this._listingsApi.exportListingsList(request, data)
        ),
        bulkManageBlacklist: (blacklisted: boolean, event_ids: number[], codes: string[]) =>
            StateManager.inProgress(
                this._listingsState.list,
                this._listingsApi.bulkManageBlacklist(blacklisted, event_ids, codes)
            )
    });

    readonly details = Object.freeze({
        load: (code: string) => StateManager.load(
            this._listingsState.details,
            this._listingsApi.getListingDetails(code)
        ),
        updateListingBlacklist: (listingCode: string, blackListed: boolean) => StateManager.inProgress(
            this._listingsState.details,
            this._listingsApi.updateListingBlacklist(listingCode, !blackListed)
        ),
        getListingData$: () => this._listingsState.details.getValue$(),
        loading$: () => this._listingsState.details.isInProgress$()
    });

    readonly transitions = Object.freeze({
        load: (id: number) => StateManager.load(
            this._listingsState.transitions,
            this._listingsApi.getListingTransitions(id)
        ),
        loading$: () => this._listingsState.transitions.isInProgress$(),
        get$: () => this._listingsState.transitions.getValue$()
    });
}

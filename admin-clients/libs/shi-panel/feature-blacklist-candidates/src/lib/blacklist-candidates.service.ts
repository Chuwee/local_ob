import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { GetBlacklistedMatchingsRequest, SupplierName } from '@admin-clients/shi-panel/utility-models';
import { inject, Injectable } from '@angular/core';
import { filter, map } from 'rxjs';
import { BlacklistedMatchingsApi } from './blacklist-candidates.api';
import { BlacklistedMatchingsState } from './state/blacklist-candidates.state';

@Injectable()
export class BlacklistedMatchingsService {
    private readonly _blacklistedMatchingsApi = inject(BlacklistedMatchingsApi);
    private readonly _blacklistedMatchingsState = inject(BlacklistedMatchingsState);

    readonly blacklist = Object.freeze({
        load: (supplier: string, request: GetBlacklistedMatchingsRequest) => StateManager.load(
            this._blacklistedMatchingsState.blacklist,
            this._blacklistedMatchingsApi.getBlacklistedMatchings(supplier, request)
        ),
        getMatchingsListData$: () => this._blacklistedMatchingsState.blacklist.getValue$().pipe(
            filter(Boolean),
            getListData()
        ),
        getMatchingsListMetadata$: () => this._blacklistedMatchingsState.blacklist.getValue$().pipe(mapMetadata(), getMetadata()),
        delete: (supplier: SupplierName, id: string) =>
            StateManager.inProgress(
                this._blacklistedMatchingsState.blacklist,
                this._blacklistedMatchingsApi.deleteBlacklistedMatching(supplier, id)
            ),
        loading$: () => this._blacklistedMatchingsState.blacklist.isInProgress$()
    });

    readonly countries = Object.freeze({
        load: (supplier: string) => StateManager.load(
            this._blacklistedMatchingsState.countries,
            this._blacklistedMatchingsApi.getCountries(supplier)
        ),
        getCountriesData$: () => this._blacklistedMatchingsState.countries.getValue$().pipe(filter(Boolean), map(r => r.data)),
        loading$: () => this._blacklistedMatchingsState.countries.isInProgress$()
    });
}

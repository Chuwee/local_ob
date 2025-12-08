import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { GetMatchingsRequest, Matching, SupplierName } from '@admin-clients/shi-panel/utility-models';
import { inject, Injectable } from '@angular/core';
import { filter, map } from 'rxjs';
import { MatchingsApi } from './matchings.api';
import { MatchingsState } from './state/matchings.state';

@Injectable()
export class MatchingsService {
    private readonly _matchingsApi = inject(MatchingsApi);
    private readonly _matchingsState = inject(MatchingsState);

    readonly list = Object.freeze({
        load: (supplier: string, request: GetMatchingsRequest) => StateManager.load(
            this._matchingsState.list,
            this._matchingsApi.getMatchings(supplier, request).pipe(mapMetadata())
        ),
        getMatchingsListData$: () => this._matchingsState.list.getValue$().pipe(getListData()),
        getMatchingsListMetadata$: () => this._matchingsState.list.getValue$().pipe(getMetadata()),
        getMatchingsListAggregatedData$: () => this._matchingsState.list.getValue$().pipe(map(matchings => matchings?.aggregated_data)),
        loading$: () => this._matchingsState.list.isInProgress$(),
        createMatching: (supplier: string, id: string) => StateManager.inProgress(
            this._matchingsState.list,
            this._matchingsApi.createMatching(supplier, id)
        ),
        createMatchingFromDetail: (supplier: string, id: string) => StateManager.inProgress(
            this._matchingsState.details,
            this._matchingsApi.createMatching(supplier, id)
        ),
        exportLoading$: () => this._matchingsState.export.isInProgress$(),
        exportMatchingslist: (supplier: string, request: GetMatchingsRequest, data: ExportRequest) => StateManager.inProgress(
            this._matchingsState.export,
            this._matchingsApi.exportMatchingsList(supplier, request, data)
        ),
        createFromCandidates: (ids: string[], supplier: string) =>
            StateManager.inProgress(
                this._matchingsState.list,
                this._matchingsApi.createMatchingsFromCandidates(supplier, ids)
            ),
        launchMatcher: (supplier: string, countries: string[], taxonomies: string[]) =>
            StateManager.inProgress(
                this._matchingsState.list,
                this._matchingsApi.launchMatcher(supplier, countries, taxonomies)
            )
    });

    readonly blacklist = Object.freeze({
        save: (supplier: SupplierName, blacklistMatchings: Matching[]) => StateManager.inProgress(
            this._matchingsState.blacklist,
            this._matchingsApi.createBlacklistedMatchings(supplier, blacklistMatchings)
        ),
        loading$: () => this._matchingsState.blacklist.isInProgress$()
    });

    readonly countries = Object.freeze({
        load: (supplier: string) => StateManager.load(
            this._matchingsState.countries,
            this._matchingsApi.getCountries(supplier)
        ),
        getCountriesData$: () => this._matchingsState.countries.getValue$().pipe(
            filter(Boolean),
            map(countries => countries.data)),
        loading$: () => this._matchingsState.countries.isInProgress$()
    });

    readonly details = Object.freeze({
        load: (supplier: string, id: number) => StateManager.load(
            this._matchingsState.details,
            this._matchingsApi.getMatchingDetails(supplier, id)
        ),
        getMatchingData$: () => this._matchingsState.details.getValue$(),
        loading$: () => this._matchingsState.details.isInProgress$()
    });

    readonly status = Object.freeze({
        load: (supplier: string) => StateManager.load(
            this._matchingsState.matcherStatus,
            this._matchingsApi.getMatcherStatus(supplier)
        ),
        getMatcherStatus$: () => this._matchingsState.matcherStatus.getValue$(),
        loading$: () => this._matchingsState.matcherStatus.isInProgress$()
    });

    readonly shiTaxonomies = Object.freeze({
        load: (supplier: string) => StateManager.load(
            this._matchingsState.shiTaxonomies,
            this._matchingsApi.getShiTaxonomies(supplier)
        ),
        get$: () => this._matchingsState.shiTaxonomies.getValue$().pipe(map(taxonomies => taxonomies?.data)),
        loading$: () => this._matchingsState.shiTaxonomies.isInProgress$()
    });

    readonly supplierTaxonomies = Object.freeze({
        load: (supplier: string) => StateManager.load(
            this._matchingsState.supplierTaxonomies,
            this._matchingsApi.getSupplierTaxonomies(supplier)
        ),
        get$: () => this._matchingsState.supplierTaxonomies.getValue$().pipe(map(taxonomies => taxonomies?.data)),
        loading$: () => this._matchingsState.supplierTaxonomies.isInProgress$()
    });
}

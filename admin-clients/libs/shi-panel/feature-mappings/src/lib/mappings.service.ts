import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { inject, Injectable } from '@angular/core';
import { catchError, filter, map, of, switchMap, tap } from 'rxjs';
import { MappingsApi } from './mappings.api';
import { GetMappingsRequest } from './models/get-mappings-request.model';
import { MappingToCreate, PutMappingsRequest } from './models/mapping.model';
import { PostMapping } from './models/post-mapping.model';
import { PutMapping } from './models/put-mapping.model';
import { MappingsState } from './state/mappings.state';

@Injectable()
export class MappingsService {
    private readonly _mappingsApi = inject(MappingsApi);
    private readonly _mappingsState = inject(MappingsState);

    readonly list = Object.freeze({
        load: (request: GetMappingsRequest) => StateManager.load(
            this._mappingsState.list,
            this._mappingsApi.getMappings(request).pipe(mapMetadata())
        ),
        getMappingsListData$: () => this._mappingsState.list.getValue$().pipe(
            filter(Boolean),
            getListData(),
            tap(maps => maps.forEach(map => map.id = map.code)) //Necessary to use status-select
        ),
        getMappingsListMetadata$: () => this._mappingsState.list.getValue$().pipe(getMetadata()),
        loading$: () => this._mappingsState.list.isInProgress$(),
        exportLoading$: () => this._mappingsState.export.isInProgress$(),
        exportMappingslist: (request: GetMappingsRequest, data: ExportRequest) => StateManager.inProgress(
            this._mappingsState.export,
            this._mappingsApi.exportMappingsList(request, data)
        ),
        save: (mapping: PostMapping) => StateManager.inProgress(
            this._mappingsState.list,
            this._mappingsApi.postMapping(mapping)
                .pipe(map(mapping => mapping.code))
        ),
        updateMapping: (id: number, mapping: PutMapping) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.putMapping(id, mapping)
            ),
        updateFavorites: (mappings: PutMappingsRequest) =>
            this._mappingsApi.putMappingsFavorites(mappings).pipe(switchMap(() => of(true)), catchError(() => of(false))),
        deleteMapping: (code: string) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.deleteMapping(code)
            ),
        cleanListings: (code: string) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.cleanListings(code)
            ),
        bulkUpdateStatus: (codes: string[], status: string) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.bulkUpdateStatus(codes, status)
            ),
        bulkCleanListings: (codes: number[]) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.bulkCleanListings(codes)
            ),
        bulkCreateMapping: (mappings: MappingToCreate[]) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.bulkCreateMapping(mappings)
            ),
        getFavorites: (supplier: SupplierName) =>
            StateManager.inProgress(
                this._mappingsState.list,
                this._mappingsApi.getFavorites(supplier)
            )
    });

    readonly countries = Object.freeze({
        load: () => StateManager.load(this._mappingsState.countries, this._mappingsApi.getCountries().pipe(mapMetadata())),
        getCountriesData$: () => this._mappingsState.countries.getValue$().pipe(getListData()),
        getCountriesMetadata$: () => this._mappingsState.countries.getValue$().pipe(getMetadata()),
        loading$: () => this._mappingsState.countries.isInProgress$()
    });

    readonly taxonomies = Object.freeze({
        load: () => StateManager.load(
            this._mappingsState.taxonomies,
            this._mappingsApi.getTaxonomies()
        ),
        get$: () => this._mappingsState.taxonomies.getValue$().pipe(map(taxonomies => taxonomies?.data)),
        loading$: () => this._mappingsState.taxonomies.isInProgress$()
    });
}

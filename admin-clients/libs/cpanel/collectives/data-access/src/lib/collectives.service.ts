import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest, ExportResponse, PageableFilter } from '@admin-clients/shared/data-access/models';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { finalize, map, takeUntil } from 'rxjs/operators';
import { CollectivesApi } from './api/collectives.api';
import { CollectiveCode } from './models/collective-code.model';
import { CollectiveEntity } from './models/collective-entities.model';
import { CollectiveExternalValidator } from './models/collective-external-validator.model';
import { CollectiveStatus } from './models/collective-status.enum';
import { Collective } from './models/collective.model';
import { GetCollectivesRequest } from './models/get-collectives-request.model';
import { PostCollectiveCode } from './models/post-collective-code.model';
import { PostCollective } from './models/post-collective.model';
import { PutCollectiveCode } from './models/put-collective-code.model';
import { PutCollectiveExternalValidatorProperties } from './models/put-collective-external-validator-properties.model';
import { PutCollective } from './models/put-collective.model';
import { CollectivesState } from './state/collectives.state';

@Injectable({
    providedIn: 'root'
})
export class CollectivesService {

    private _cancelCollectiveCodes = new Subject<void>();
    private _stopFetchingCollectives = new Subject<void>();

    constructor(
        private _collectivesApi: CollectivesApi,
        private _state: CollectivesState
    ) { }

    loadCollectivesList(request: GetCollectivesRequest): void {
        StateManager.load(
            this._state.collectivesList, this._collectivesApi.getCollectives(request).pipe(mapMetadata())
        );
    }

    fetchCollectives(request?: Partial<GetCollectivesRequest>): void {
        const pageSize = 50;
        this._stopFetchingCollectives.next();
        this._state.collectivesList.setInProgress(true);

        const req: GetCollectivesRequest = {
            offset: 0,
            limit: pageSize,
            ...request
        };

        fetchAll((offset: number) => this._collectivesApi.getCollectives({ ...req, offset }))
            .pipe(
                mapMetadata(),
                finalize(() => this._state.collectivesList.setInProgress(false)),
                takeUntil(this._stopFetchingCollectives)
            )
            .subscribe(result => this._state.collectivesList.setValue(result));
    }

    clearCollectivesList(): void {
        this._state.collectivesList.setValue(null);
    }

    getCollectivesListData$(): Observable<Collective[]> {
        return this._state.collectivesList.getValue$().pipe(getListData());
    }

    getCollectivesListMetadata$(): Observable<Metadata> {
        return this._state.collectivesList.getValue$().pipe(getMetadata());
    }

    isCollectiveListLoading$(): Observable<boolean> {
        return this._state.collectivesList.isInProgress$();
    }

    loadCollective(collectiveId: number): void {
        StateManager.load(
            this._state.collective,
            this._collectivesApi.getCollective(collectiveId)
        );
    }

    getCollective$(): Observable<Collective> {
        return this._state.collective.getValue$();
    }

    isCollectiveLoading$(): Observable<boolean> {
        return this._state.collective.isInProgress$();
    }

    deleteCollective(collectiveId: number): Observable<void> {
        return this._collectivesApi.deleteCollective(collectiveId);
    }

    saveCollective(collectiveId: number, collective: PutCollective): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveSaving,
            this._collectivesApi.putCollective(collectiveId, collective)
        );
    }

    clearCollective(): void {
        this._state.collective.setValue(null);
    }

    createCollective(collective: PostCollective): Observable<number> {
        return StateManager.inProgress(
            this._state.collectiveSaving,
            this._collectivesApi.postCollective(collective).pipe(map(result => result.id))
        );
    }

    isCollectiveSaving$(): Observable<boolean> {
        return this._state.collectiveSaving.isInProgress$();
    }

    updateCollectiveStatus(collectiveId: number, status: CollectiveStatus): Observable<void> {
        return this._collectivesApi.putCollectiveStatus(collectiveId, status);
    }

    loadCollectiveEntities(collectiveId: number): void {
        StateManager.load(
            this._state.collectiveEntities,
            this._collectivesApi.getCollectiveEntities(collectiveId)
        );
    }

    getCollectiveEntities$(): Observable<CollectiveEntity[]> {
        return this._state.collectiveEntities.getValue$();
    }

    isCollectiveEntitiesLoading$(): Observable<boolean> {
        return this._state.collectiveEntities.isInProgress$();
    }

    saveCollectiveEntities(collectiveId: number, entities: number[]): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveEntitiesSaving,
            this._collectivesApi.putCollectiveEntities(collectiveId, entities)
        );
    }

    clearCollectiveEntities(): void {
        this._state.collectiveEntities.setValue(null);
    }

    isCollectiveEntitiesSaving$(): Observable<boolean> {
        return this._state.collectiveSaving.isInProgress$();
    }

    loadCollectiveExternalValidators(): void {
        StateManager.load(
            this._state.collectiveExternalValidators,
            this._collectivesApi.getCollectiveExternalValidators()
        );
    }

    getCollectiveExternalValidators$(): Observable<CollectiveExternalValidator[]> {
        return this._state.collectiveExternalValidators.getValue$();
    }

    // External validator user and password
    saveCollectiveExternalValidators(collectiveId: number, externalValidators: PutCollectiveExternalValidatorProperties): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveExternalValidatorPropertiesSaving,
            this._collectivesApi.putCollectiveExternalValidatorProperties(collectiveId, externalValidators)
        );
    }

    isCollectiveExternalValidatorsLoading$(): Observable<boolean> {
        return this._state.collectiveExternalValidators.isInProgress$();
    }

    isCollectiveExternalValidatorPropertiesSaving$(): Observable<boolean> {
        return this._state.collectiveExternalValidatorPropertiesSaving.isInProgress$();
    }

    clearCollectiveExternalValidators(): void {
        this._state.collectiveExternalValidators.setValue(null);
    }

    cancelCollectiveCodes(): void {
        this._cancelCollectiveCodes.next();
    }

    loadCollectiveCodes(collectiveId: number, request: PageableFilter): void {
        StateManager.load(
            this._state.collectiveCodes,
            this._collectivesApi.getCollectiveCodes(collectiveId, request).pipe(
                mapMetadata(),
                takeUntil(this._cancelCollectiveCodes)
            )
        );
    }

    getCollectiveCodesData$(): Observable<CollectiveCode[]> {
        return this._state.collectiveCodes.getValue$().pipe(getListData());
    }

    getCollectiveCodesMetadata$(): Observable<Metadata> {
        return this._state.collectiveCodes.getValue$().pipe(getMetadata());
    }

    isCollectiveCodesLoading$(): Observable<boolean> {
        return this._state.collectiveCodes.isInProgress$();
    }

    clearCollectiveCodes(): void {
        this._state.collectiveCodes.setValue(null);
    }

    createCollectiveCode(collectiveId: number, collectiveCodeData: PostCollectiveCode): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveCodeSaving,
            this._collectivesApi.postCollectiveCode(collectiveId, collectiveCodeData)
        );
    }

    createCollectiveCodes(collectiveId: number, collectiveCodeData: PostCollectiveCode[]): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveCodesSaving,
            this._collectivesApi.postCollectiveCodes(collectiveId, collectiveCodeData)
        );
    }

    saveCollectiveCode(
        collectiveId: number, collectiveCodes: string[], collectiveCodeData: PutCollectiveCode, q: string = null
    ): Observable<void> {
        return StateManager.inProgress(
            this._state.collectiveCodeSaving,
            this._collectivesApi.putCollectiveCodes(
                collectiveId, collectiveCodes?.length === 0 ? null : collectiveCodes, collectiveCodeData, q
            )
        );
    }

    setCollectiveCodesSaving(saving: boolean): void {
        this._state.collectiveCodesSaving.setInProgress(saving);
    }

    isCollectiveCodeSaving$(): Observable<boolean> {
        return this._state.collectiveCodeSaving.isInProgress$();
    }

    isCollectiveCodesSaving$(): Observable<boolean> {
        return this._state.collectiveCodesSaving.isInProgress$();
    }

    deleteCollectiveCodes(collectiveId: number, collectiveCodes: string[], q: string = null): Observable<void> {
        const action$ = (): Observable<void> => {
            if (collectiveCodes.length === 1) {
                return this._collectivesApi.deleteCollectiveCode(collectiveId, collectiveCodes[0]);
            } else if (collectiveCodes.length > 1) {
                return this._collectivesApi.deleteCollectiveCodes(collectiveId, collectiveCodes);
            } else {
                return this._collectivesApi.deleteCollectiveCodes(collectiveId, null, q);
            }
        };

        return StateManager.inProgress(
            this._state.collectiveCodesDeleting,
            action$()
        );
    }

    isCollectiveCodeDeleting$(): Observable<boolean> {
        return this._state.collectiveCodesDeleting.isInProgress$();
    }

    exportCollectiveCodes(id: number, request: GetCollectivesRequest, data: ExportRequest): Observable<ExportResponse> {
        return StateManager.inProgress(
            this._state.exportCollectiveCodes,
            this._collectivesApi.exportCollectiveCodes(id, request, data)
        );
    }

    isCollectiveCodeExporting$(): Observable<boolean> {
        return this._state.exportCollectiveCodes.isInProgress$();
    }
}

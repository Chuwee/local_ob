import { getListData, getMetadata, mapMetadata, StateManager, StateProperty } from '@OneboxTM/utils-state';
import { ExportRequest } from '@admin-clients/shared/data-access/models';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize, map, take, tap } from 'rxjs/operators';
import { SeasonTicketRenewalsApi } from './api/season-ticket-renewals.api';
import { GetSeasonTicketRenewalsRequest } from './models/get-season-ticket-renewals-request.model';
import { DeleteSeasonTicketDeleteMultipleRenewalsRequest } from './models/post-season-ticket-delete-renewals-request.model';
import { PostSeasonTicketRenewals } from './models/post-season-ticket-renewals.model';
import { PurgeSeasonTicketsRenewalsRequest } from './models/purge-season-ticket-renewals.model';
import { PutSeasonTicketRenewals } from './models/put-season-ticket-renewals.model';
import { SeasonTicketRenewalAvailableSeat } from './models/season-ticket-renewal-available-seat.model';
import { PostSeasonTicketRenewalsGeneration } from './models/season-ticket-renewals-generation.model';
import { SeasonTicketRenewalsState } from './state/season-ticket-renewals.state';

@Injectable()
export class SeasonTicketRenewalsService {
    readonly #seasonTicketRenewalsApi = inject(SeasonTicketRenewalsApi);
    readonly #seasonTicketRenewalsState = inject(SeasonTicketRenewalsState);

    readonly renewalsList = Object.freeze({
        load: (seasonTicketId: number, request: GetSeasonTicketRenewalsRequest) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalsList,
            this.#seasonTicketRenewalsApi.getRenewals(seasonTicketId, request).pipe(mapMetadata())
        ),
        getData$: () => this.#seasonTicketRenewalsState.renewalsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#seasonTicketRenewalsState.renewalsList.getValue$().pipe(getMetadata()),
        getSummary$: () => this.#seasonTicketRenewalsState.renewalsList.getValue$().pipe(map(renewals => renewals?.summary)),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalsList.isInProgress$(),
        deleteOne: (seasonTicketId: number, renewalId: string) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.renewalsList,
            this.#seasonTicketRenewalsApi.deleteRenewal(seasonTicketId, renewalId)
        ),
        deleteMultiple: (seasonTicketId: number, deleteMultipleRenewals: DeleteSeasonTicketDeleteMultipleRenewalsRequest) =>
            StateManager.inProgress(
                this.#seasonTicketRenewalsState.renewalsList,
                this.#seasonTicketRenewalsApi.deleteMultipleRenewals(seasonTicketId, deleteMultipleRenewals)
            ),
        purge: (seasonTicketId: number, deleteAllRenewals: PurgeSeasonTicketsRenewalsRequest) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.renewalsList,
            this.#seasonTicketRenewalsApi.purgeRenewals(seasonTicketId, deleteAllRenewals)
        ),
        export: (seasonTicketId: number, request: GetSeasonTicketRenewalsRequest, data: ExportRequest) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.exportRenewals,
            this.#seasonTicketRenewalsApi.exportRenewalsList(seasonTicketId, request, data)
        ),
        getDeletableRenewalsNumber: (seasonTicketId: number, request: PurgeSeasonTicketsRenewalsRequest) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.renewalsList,
            this.#seasonTicketRenewalsApi.getDeletableRenewalsNumber(seasonTicketId, request)
        ),
        automaticRenewals: Object.freeze({
            generate: (seasonTicketId: number, body: PostSeasonTicketRenewalsGeneration) => StateManager.inProgress(
                this.#seasonTicketRenewalsState.automaticRenewalGeneration,
                this.#seasonTicketRenewalsApi.generateAutomaticRenewals(seasonTicketId, body)
            ),
            inProgress$: () => this.#seasonTicketRenewalsState.automaticRenewalGeneration.isInProgress$(),
            error$: () => this.#seasonTicketRenewalsState.automaticRenewalGeneration.getError$()
        })
    });

    readonly renewalsEntities = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalsEntities,
            this.#seasonTicketRenewalsApi.getRenewalsEntities(seasonTicketId)
        ),
        getData$: () => this.#seasonTicketRenewalsState.renewalsEntities.getValue$(),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalsEntities.isInProgress$(),
        clear: () => this.#seasonTicketRenewalsState.renewalsEntities.setValue(null)
    });

    readonly renewalsExportXmlSepa = Object.freeze({
        export: (seasonTicketId: number) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.exportXmlSepa,
            this.#seasonTicketRenewalsApi.exportXmlSepa(seasonTicketId)
        ),
        inProgress$: () => this.#seasonTicketRenewalsState.exportXmlSepa.isInProgress$(),
        error$: () => this.#seasonTicketRenewalsState.exportXmlSepa.getError$()
    });

    readonly renewalsSubstatus = Object.freeze({
        update: (seasonTicketId: number, putRenewals: PutSeasonTicketRenewals) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.renewalsSubstatus,
            this.#seasonTicketRenewalsApi.putRenewals(seasonTicketId, putRenewals)
        ),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalsSubstatus.isInProgress$(),
        error$: () => this.#seasonTicketRenewalsState.renewalsSubstatus.getError$()
    });

    readonly renewalCandidatesList = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalCandidatesList,
            this.#seasonTicketRenewalsApi.getRenewalCandidates(seasonTicketId)
        ),
        getData$: () => this.#seasonTicketRenewalsState.renewalCandidatesList.getValue$()
            .pipe(map(renewalCandidates => renewalCandidates?.data)),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalCandidatesList.isInProgress$()
    });

    readonly externalRenewalCandidatesList = Object.freeze({
        load: (entityId: number) => StateManager.load(
            this.#seasonTicketRenewalsState.externalRenewalCandidatesList,
            this.#seasonTicketRenewalsApi.getExternalRenewalCandidates(entityId)
        ),
        getData$: () => this.#seasonTicketRenewalsState.externalRenewalCandidatesList.getValue$()
            .pipe(map(renewalCandidates => renewalCandidates?.data)),
        inProgress$: () => this.#seasonTicketRenewalsState.externalRenewalCandidatesList.isInProgress$()
    });

    readonly renewalRates = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalRates,
            this.#seasonTicketRenewalsApi.getRenewalRates(seasonTicketId)
        ),
        get$: () => this.#seasonTicketRenewalsState.renewalRates.getValue$(),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalRates.isInProgress$(),
        clear: () => this.#seasonTicketRenewalsState.renewalRates.setValue(null)
    });

    readonly externalRenewalRates = Object.freeze({
        load: (id: number) => StateManager.load(
            this.#seasonTicketRenewalsState.externalRenewalRates,
            this.#seasonTicketRenewalsApi.getExternalRenewalRates(id)
        ),
        get$: () => this.#seasonTicketRenewalsState.externalRenewalRates.getValue$(),
        inProgress$: () => this.#seasonTicketRenewalsState.externalRenewalRates.isInProgress$(),
        clear: () => this.#seasonTicketRenewalsState.externalRenewalRates.setValue(null)
    });

    readonly renewalsImport = Object.freeze({
        import: (seasonTicketId: number, renewalCandidate: PostSeasonTicketRenewals) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalsImport,
            this.#seasonTicketRenewalsApi.postRenewals(seasonTicketId, renewalCandidate)
        ),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalsImport.isInProgress$(),
        error$: () => this.#seasonTicketRenewalsState.renewalsImport.getError$()
    });

    readonly renewalsCapacityTree = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#seasonTicketRenewalsState.renewalsCapacityTree,
            this.#seasonTicketRenewalsApi.getRenewalsCapacityTreeSectors(seasonTicketId)
        ),
        get$: () => this.#seasonTicketRenewalsState.renewalsCapacityTree.getValue$(),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalsCapacityTree.isInProgress$()
    });

    readonly renewalEdits = Object.freeze({
        save: (seasonTicketId: number, putRenewals: PutSeasonTicketRenewals) => StateManager.inProgress(
            this.#seasonTicketRenewalsState.renewalEdits,
            this.#seasonTicketRenewalsApi.putRenewals(seasonTicketId, putRenewals)
        ),
        inProgress$: () => this.#seasonTicketRenewalsState.renewalEdits.isInProgress$()
    });

    readonly availableRowSeats = Object.freeze({
        load: (seasonTicketId: number, sectorId: number, rowId: number) => this.#loadAvailableRowSeats(seasonTicketId, sectorId, rowId),
        get$: (sectorId: number, rowId: number) =>
            this.#getAvailableSeats(sectorId, rowId, this.#seasonTicketRenewalsState.recordOfAvailableRowSeats),
        inProgress$: () => this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.isInProgress$()
    });

    readonly availableNnzSeats = Object.freeze({
        load: (seasonTicketId: number, sectorId: number, nnzId: number) => this.#loadAvailableNnzSeats(seasonTicketId, sectorId, nnzId),
        get$: (sectorId: number, nnzId: number) =>
            this.#getAvailableSeats(sectorId, nnzId, this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats),
        inProgress$: () => this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.isInProgress$()
    });

    #loadAvailableRowSeats(seasonTicketId: number, sectorId: number, rowId: number): void {
        const rowRecordKey = `${sectorId}-${rowId}`;
        this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.getValue$()
            .pipe(
                take(1),
                tap(recordOfAvailableSeats => {
                    if (recordOfAvailableSeats[rowRecordKey]) {
                        this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.setValue(recordOfAvailableSeats);
                    } else {
                        this.#getAvailableRowSeatsFromApi(seasonTicketId, rowId, rowRecordKey);
                    }
                })
            ).subscribe();
    }

    #getAvailableRowSeatsFromApi(seasonTicketId: number, rowId: number, rowRecordKey: string): void {
        this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.setInProgress(true);
        this.#seasonTicketRenewalsApi.getAvailableRowSeats(seasonTicketId, rowId)
            .pipe(
                finalize(() => this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.setInProgress(false))
            ).subscribe(availableSeats =>
                this.#setRecordOfAvailableRowSeats(availableSeats, rowRecordKey)
            );
    }

    #setRecordOfAvailableRowSeats(availableSeats: SeasonTicketRenewalAvailableSeat[], rowRecordKey: string): void {
        this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.getValue$()
            .pipe(
                take(1),
                tap(recordOfAvailableSeats =>
                    this.#seasonTicketRenewalsState.recordOfAvailableRowSeats.setValue({
                        ...recordOfAvailableSeats,
                        [rowRecordKey]: availableSeats
                    })
                )
            ).subscribe();
    }

    #loadAvailableNnzSeats(seasonTicketId: number, sectorId: number, nnzId: number): void {
        const rowRecordKey = `${sectorId}-${nnzId}`;
        this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.getValue$()
            .pipe(
                take(1),
                tap(recordOfAvailableSeats => {
                    if (recordOfAvailableSeats[rowRecordKey]) {
                        this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.setValue(recordOfAvailableSeats);
                    } else {
                        this.#getAvailableNnzSeatsFromApi(seasonTicketId, nnzId, rowRecordKey);
                    }
                })
            ).subscribe();
    }

    #getAvailableNnzSeatsFromApi(seasonTicketId: number, nnzId: number, rowRecordKey: string): void {
        this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.setInProgress(true);
        this.#seasonTicketRenewalsApi.getAvailableNnzSeats(seasonTicketId, nnzId)
            .pipe(
                finalize(() => this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.setInProgress(false))
            ).subscribe(availableSeats =>
                this.#setRecordOfAvailableNnzSeats(availableSeats, rowRecordKey)
            );
    }

    #setRecordOfAvailableNnzSeats(availableSeats: SeasonTicketRenewalAvailableSeat[], rowRecordKey: string): void {
        this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.getValue$()
            .pipe(
                take(1),
                tap(recordOfAvailableSeats =>
                    this.#seasonTicketRenewalsState.recordOfAvailableNnzSeats.setValue({
                        ...recordOfAvailableSeats,
                        [rowRecordKey]: availableSeats
                    })
                )
            ).subscribe();
    }

    #getAvailableSeats(sectorId: number, itemId: number, recordState: StateProperty<Record<string, SeasonTicketRenewalAvailableSeat[]>>):
        Observable<SeasonTicketRenewalAvailableSeat[]> {
        const recordKey = `${sectorId}-${itemId}`;
        return recordState.getValue$()
            .pipe(
                map(recordOfAvailableSeats => recordOfAvailableSeats[recordKey])
            );
    }
}
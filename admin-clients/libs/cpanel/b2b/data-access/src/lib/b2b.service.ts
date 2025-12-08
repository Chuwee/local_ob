import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest, IdName } from '@admin-clients/shared/data-access/models';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { catchError, finalize, map, takeUntil } from 'rxjs/operators';
import { B2bApi } from './api/b2b.api';
import {
    GetB2bClientsRequest, B2bClient, PutB2bClient, PostB2bClient, B2bClientUser, PutB2bClientUser,
    PostB2bClientUser, B2bConditionsClientsGroupType, GetB2bConditionsClientRequest, B2bConditionsClient,
    B2bConditionsGroupType, GetB2bConditionsRequest, B2bConditions, PutB2bConditions, DeleteB2bConditionsRequest,
    GetB2bConditionsClientsRequest, PutB2bConditionsClients, DeleteB2bConditionsClientsRequest,
    B2bClientOperation, B2bClientOperationType, B2bClientBalance, B2bClientTransactionsExportReq, B2bClientTransaction,
    GetB2bClientTransactionsRequest, GetB2bClientUsersRequest, B2bSeatsFilter, GetB2bSeatsFiltersRequest, GetB2bSeatsListRequest
} from './models';
import { B2bState } from './state/b2b.state';

@Injectable()
export class B2bService {
    private _stopFetchB2bClients = new Subject<void>();
    private readonly _stopFetchingAllEventConditionsClients = new Subject<void>();
    private readonly _stopFetchingEventConditionsClients = new Subject<void>();
    private readonly _stopFetchingB2bClients = new Subject<void>();
    private readonly _b2bApi = inject(B2bApi);
    private readonly _b2bState = inject(B2bState);

    readonly b2bClientTransactions = Object.freeze({
        export: Object.freeze({
            save: (clientId: number, body: B2bClientTransactionsExportReq) =>
                StateManager.inProgress(
                    this._b2bState.b2bClientTransactionsExport,
                    this._b2bApi.postB2bClientTransactionsExport(clientId, body)
                ),
            isInProgress$: () => this._b2bState.b2bClientTransactionsExport.isInProgress$()
        })
    });

    readonly b2bClientBalanceOperation = Object.freeze({
        save: (clientId: number, operationType: B2bClientOperationType, body: B2bClientOperation) =>
            StateManager.inProgress(
                this._b2bState.b2bClientBalanceOperation,
                this._b2bApi.postB2bClientBalanceOperation(clientId, operationType, body)
            ),
        isInProgress$: () => this._b2bState.b2bClientBalanceOperation.isInProgress$()
    });

    readonly b2bSeatsList = Object.freeze({
        load: (request: GetB2bSeatsListRequest) =>
            StateManager.load(this._b2bState.b2bSeatsList, this._b2bApi.getSeats(request).pipe(mapMetadata())),
        loadMore: (request: GetB2bSeatsListRequest) =>
            StateManager.loadMore(request, this._b2bState.b2bSeatsList, r => this._b2bApi.getSeats(r)),
        export: (request: GetB2bSeatsListRequest, data: ExportRequest) => StateManager.inProgress(
            this._b2bState.exportB2bSeatsList,
            this._b2bApi.exportB2bSeatsList(request, data)
        ),
        getList$: () => this._b2bState.b2bSeatsList.getValue$().pipe(getListData()),
        getMetadata$: () => this._b2bState.b2bSeatsList.getValue$().pipe(getMetadata()),
        clear: () => this._b2bState.b2bSeatsList.setValue(null),
        loading$: () => this._b2bState.b2bSeatsList.isInProgress$()
    });

    readonly b2bSeat = Object.freeze({
        // eslint-disable-next-line max-len
        load: (id: number) => StateManager.load(this._b2bState.b2bSeat, this._b2bApi.getSeat(id)),
        get$: () => this._b2bState.b2bSeat.getValue$(),
        clear: () => this._b2bState.b2bSeat.setValue(null),
        loading$: () => this._b2bState.b2bSeat.isInProgress$()
    });

    readonly b2bSeatFiltersEventList = Object.freeze({
        load: (request: GetB2bSeatsFiltersRequest) =>
            StateManager.load(this._b2bState.b2bSeatFilterEventList,
                this._b2bApi.getB2bSeatsFilterOptions$(B2bSeatsFilter.events, request).pipe(mapMetadata())),
        loadMore: (request: GetB2bSeatsFiltersRequest) =>
            StateManager.loadMore(request, this._b2bState.b2bSeatFilterEventList,
                r => this._b2bApi.getB2bSeatsFilterOptions$(B2bSeatsFilter.events, r)),
        getList$: () => this._b2bState.b2bSeatFilterEventList.getValue$().pipe(map(list => list?.data)),
        getMetadata$: () => this._b2bState.b2bSeatFilterEventList.getValue$().pipe(map(list => list?.metadata)),
        clear: () => this._b2bState.b2bSeatFilterEventList.setValue(null),
        loading$: () => this._b2bState.b2bSeatFilterEventList.isInProgress$()
    });

    readonly b2bSeatFiltersSessionList = Object.freeze({
        load: (request: GetB2bSeatsFiltersRequest) =>
            StateManager.load(
                this._b2bState.b2bSeatFilterSessionList,
                this._b2bApi.getB2bSeatsFilterOptions$(B2bSeatsFilter.sessions, request).pipe(mapMetadata())
            ),
        loadMore: (request: GetB2bSeatsFiltersRequest) =>
            StateManager.loadMore(request, this._b2bState.b2bSeatFilterSessionList,
                r => this._b2bApi.getB2bSeatsFilterOptions$(B2bSeatsFilter.sessions, r)),
        getList$: () => this._b2bState.b2bSeatFilterSessionList.getValue$().pipe(getListData()),
        getMetadata$: () => this._b2bState.b2bSeatFilterSessionList.getValue$().pipe(getMetadata()),
        clear: () => this._b2bState.b2bSeatFilterSessionList.setValue(null),
        loading$: () => this._b2bState.b2bSeatFilterSessionList.isInProgress$()
    });

    // CLIENTS

    readonly b2bClientsList = Object.freeze({
        load: (request: GetB2bClientsRequest) =>
            StateManager.load(this._b2bState.b2bClientsList, this._b2bApi.getB2bClients(request).pipe(mapMetadata())),
        loadMore: (request: GetB2bClientsRequest) =>
            StateManager.loadMore(request, this._b2bState.b2bClientsList, r => this._b2bApi.getB2bClients(r).pipe(mapMetadata())),
        loadAll: (request: GetB2bClientsRequest) => {
            const pageSize = 100;
            this._stopFetchingB2bClients.next();
            this._b2bState.b2bClientsList.setInProgress(true);
            const req: GetB2bClientsRequest = {
                offset: 0,
                limit: pageSize,
                ...request
            };
            fetchAll((offset: number) => this._b2bApi.getB2bClients({ ...req, offset }))
                .pipe(
                    mapMetadata(),
                    finalize(() => this._b2bState.b2bClientsList.setInProgress(false)),
                    takeUntil(this._stopFetchingB2bClients)
                )
                .subscribe(result => this._b2bState.b2bClientsList.setValue(result));
        },
        getList$: () => this._b2bState.b2bClientsList.getValue$().pipe(getListData()),
        getMetadata$: () => this._b2bState.b2bClientsList.getValue$().pipe(getMetadata()),
        clear: () => this._b2bState.b2bClientsList.setValue(null),
        loading$: () => this._b2bState.b2bClientsList.isInProgress$()
    });

    readonly b2bClientUserApiKey = Object.freeze({
        refresh$: (clientId: number, clientUserId: number, entityId: number | null): Observable<string> =>
            StateManager.inProgress(
                this._b2bState.b2bClientUserApiKey,
                this._b2bApi.postB2bClientUserApiKey(clientId, clientUserId, entityId).pipe(map(resp => resp?.api_key))
            ),
        loading$: () => this._b2bState.b2bClientUserApiKey.isInProgress$()
    });

    loadB2bClientsList(request: GetB2bClientsRequest): void {
        this._b2bState.b2bClientsList.setInProgress(true);
        this._b2bApi.getB2bClients(request)
            .pipe(
                mapMetadata(),
                finalize(() => this._b2bState.b2bClientsList.setInProgress(false)))
            .subscribe(clients => this._b2bState.b2bClientsList.setValue(clients));
    }

    //Only used for async validator on taxId form field
    checkIfTaxIdExists(controlValue: string, entityId?: number, currentB2bClientId?: number): Observable<boolean> {
        this._stopFetchB2bClients.next();
        const request: GetB2bClientsRequest = {
            limit: 100,
            offset: 0,
            q: controlValue,
            entity_id: entityId
        };
        return fetchAll((offset: number) => this._b2bApi.getB2bClients({ ...request, offset }))
            .pipe(
                map(b2bClients => {
                    if (b2bClients?.metadata.total === 0) {
                        return false;
                    } else {
                        if (currentB2bClientId) {
                            return b2bClients?.data.some(b2bClient =>
                                b2bClient.tax_id === controlValue && b2bClient.id !== currentB2bClientId);
                        } else {
                            return b2bClients?.data.some(b2bClient => b2bClient.tax_id === controlValue);
                        }
                    }
                }),
                takeUntil(this._stopFetchB2bClients)
            );
    }

    clearB2bClientsList(): void {
        this._b2bState.b2bClientsList.setValue(null);
    }
    //CLIENT

    loadB2bClient(clientId: number, entityId?: number): void {
        this._b2bState.b2bClient.setError(null);
        this._b2bState.b2bClient.setInProgress(true);
        this._b2bApi.getB2bClient(clientId, entityId)
            .pipe(
                catchError(error => {
                    this._b2bState.b2bClient.setError(error);
                    return of(null);
                }),
                finalize(() => this._b2bState.b2bClient.setInProgress(false))
            )
            .subscribe(b2bClient => this._b2bState.b2bClient.setValue(b2bClient));
    }

    getB2bClient$(): Observable<B2bClient> {
        return this._b2bState.b2bClient.getValue$();
    }

    getB2bClientError$(): Observable<HttpErrorResponse> {
        return this._b2bState.b2bClient.getError$();
    }

    clearB2bClient(): void {
        this._b2bState.b2bClient.setValue(null);
    }

    saveB2bClient(clientId: number, request: PutB2bClient): Observable<void> {
        this._b2bState.b2bClient.setInProgress(true);
        return this._b2bApi.putB2bClient(clientId, request)
            .pipe(finalize(() => this._b2bState.b2bClient.setInProgress(false)));
    }

    createB2bClient(request: PostB2bClient): Observable<number> {
        this._b2bState.b2bClient.setInProgress(true);
        return this._b2bApi.postB2bClient(request)
            .pipe(
                map(result => result.id),
                finalize(() => this._b2bState.b2bClient.setInProgress(false))
            );
    }

    deleteB2bClient(clientId: number, entityId?: number): Observable<void> {
        this._b2bState.b2bClient.setInProgress(true);
        return this._b2bApi.deleteB2bClient(clientId, entityId)
            .pipe(finalize(() => this._b2bState.b2bClient.setInProgress(false)));
    }

    isB2bClientInProgress$(): Observable<boolean> {
        return this._b2bState.b2bClient.isInProgress$();
    }

    // CLIENT USERS

    loadB2bClientUsersList(clientId: number, request: GetB2bClientUsersRequest): void {
        this._b2bState.b2bClientUsersList.setInProgress(true);
        this._b2bApi.getB2bClientUsers(clientId, request)
            .pipe(
                mapMetadata(),
                finalize(() => this._b2bState.b2bClientUsersList.setInProgress(false))
            )
            .subscribe(clientUsers => this._b2bState.b2bClientUsersList.setValue(clientUsers));
    }

    clearB2bClientUsersList(): void {
        this._b2bState.b2bClientUsersList.setValue(null);
    }

    getB2bClientUsersListData$(): Observable<B2bClientUser[]> {
        return this._b2bState.b2bClientUsersList.getValue$().pipe(getListData());
    }

    getB2bClientUsersListMetadata$(): Observable<Metadata> {
        return this._b2bState.b2bClientUsersList.getValue$().pipe(getMetadata());
    }

    isB2bClientUsersListLoading$(): Observable<boolean> {
        return this._b2bState.b2bClientUsersList.isInProgress$();
    }

    // CLIENT USER

    loadB2bClientUser(clientId: number, clientUserId: number, entityId?: number): void {
        this._b2bState.b2bClientUser.setError(null);
        this._b2bState.b2bClientUser.setInProgress(true);
        this._b2bApi.getB2bClientUser(clientId, clientUserId, entityId)
            .pipe(
                catchError(error => {
                    this._b2bState.b2bClientUser.setError(error);
                    return of(null);
                }),
                finalize(() => this._b2bState.b2bClientUser.setInProgress(false))
            )
            .subscribe(b2bClientUser => this._b2bState.b2bClientUser.setValue(b2bClientUser));
    }

    getB2bClientUser$(): Observable<B2bClientUser> {
        return this._b2bState.b2bClientUser.getValue$();
    }

    getB2bClientUserError$(): Observable<HttpErrorResponse> {
        return this._b2bState.b2bClientUser.getError$();
    }

    clearB2bClientUser(): void {
        this._b2bState.b2bClientUser.setValue(null);
    }

    saveB2bClientUser(clientId: number, clientUserId: number, request: PutB2bClientUser): Observable<void> {
        this._b2bState.b2bClientUser.setInProgress(true);
        return this._b2bApi.putB2bClientUser(clientId, clientUserId, request)
            .pipe(finalize(() => this._b2bState.b2bClientUser.setInProgress(false)));
    }

    createB2bClientUser(clientId: number, request: PostB2bClientUser): Observable<number> {
        this._b2bState.b2bClientUser.setInProgress(true);
        return this._b2bApi.postB2bClientUser(clientId, request)
            .pipe(
                map(result => result.id),
                finalize(() => this._b2bState.b2bClientUser.setInProgress(false))
            );
    }

    deleteB2bClientUser(clientId: number, clientUserId: number, entityId?: number): Observable<void> {
        this._b2bState.b2bClientUser.setInProgress(true);
        return this._b2bApi.deleteB2bClientUser(clientId, clientUserId, entityId)
            .pipe(finalize(() => this._b2bState.b2bClientUser.setInProgress(false)));
    }

    isB2bClientUserInProgress$(): Observable<boolean> {
        return this._b2bState.b2bClientUser.isInProgress$();
    }

    // CLIENT USER PASSWORD
    regenerateB2bClientUserPassword(clientId: number, clientUserId: number, entityId?: number): Observable<void> {
        this._b2bState.b2bClientUserPassword.setInProgress(true);
        return this._b2bApi.postB2bClientUserPassword(clientId, clientUserId, entityId)
            .pipe(finalize(() => this._b2bState.b2bClientUserPassword.setInProgress(false)));
    }

    isB2bClientUserPasswordInProgress$(): Observable<boolean> {
        return this._b2bState.b2bClientUserPassword.isInProgress$();
    }

    // CONDITIONS CLIENTS

    getAllConditionsClientsMetadata$(): Observable<Metadata> {
        return this._b2bState.allB2bConditionsClients.getValue$().pipe(getMetadata());
    }

    // CONDITIONS CLIENT

    loadConditionsClient<T extends B2bConditionsClientsGroupType>(
        groupType: T, clientId: number, request: GetB2bConditionsClientRequest<T>
    ): void {
        this._b2bState.b2bConditionsClient.setInProgress(true);
        this._b2bApi.getB2bConditionsClient(groupType, clientId, request)
            .pipe(finalize(() => this._b2bState.b2bConditionsClient.setInProgress(false)))
            .subscribe(conditions => this._b2bState.b2bConditionsClient.setValue(conditions));
    }

    getConditionsClient$(): Observable<B2bConditionsClient> {
        return this._b2bState.b2bConditionsClient.getValue$();
    }

    isConditionsClientInProgress$(): Observable<boolean> {
        return this._b2bState.b2bConditionsClient.isInProgress$();
    }

    clearConditionsClient(): void {
        this._b2bState.b2bConditionsClient.setValue(null);
    }

    // BALANCE

    loadB2bClientBalance(clientId: number, entityId: number, currencyCode?: string): void {
        this._b2bState.b2bClientBalance.setError(null);
        this._b2bState.b2bClientBalance.setInProgress(true);
        this._b2bApi.getB2bClientBalance(clientId, entityId, currencyCode)
            .pipe(
                catchError(error => {
                    this._b2bState.b2bClientBalance.setError(error);
                    return of(null);
                }),
                finalize(() => this._b2bState.b2bClientBalance.setInProgress(false))
            )
            .subscribe(b2bClientBalance => this._b2bState.b2bClientBalance.setValue(b2bClientBalance));
    }

    getB2bClientBalance$(): Observable<B2bClientBalance> {
        return this._b2bState.b2bClientBalance.getValue$();
    }

    getB2bClientBalanceError$(): Observable<HttpErrorResponse> {
        return this._b2bState.b2bClientBalance.getError$();
    }

    clearB2bClientBalance(): void {
        this._b2bState.b2bClientBalance.setValue(null);
    }

    createB2bClientBalance(clientId: number, entityId: number): Observable<void> {
        this._b2bState.b2bClientBalance.setInProgress(true);
        return this._b2bApi.postB2bClientBalance(clientId, entityId)
            .pipe(finalize(() => this._b2bState.b2bClientBalance.setInProgress(false)));
    }

    isB2bClientBalanceInProgress$(): Observable<boolean> {
        return this._b2bState.b2bClientBalance.isInProgress$();
    }

    // CLIENT TRANSACTIONS

    loadB2bClientTransactionsList(clientId: number, request: GetB2bClientTransactionsRequest): void {
        this._b2bState.b2bClientTransactionsList.setInProgress(true);
        this._b2bApi.getB2bClientTransactions(clientId, request)
            .pipe(
                mapMetadata(),
                finalize(() => this._b2bState.b2bClientTransactionsList.setInProgress(false))
            )
            .subscribe(clientTransactions => this._b2bState.b2bClientTransactionsList.setValue(clientTransactions));
    }

    getB2bClientTransactionsListData$(): Observable<B2bClientTransaction[]> {
        return this._b2bState.b2bClientTransactionsList.getValue$().pipe(getListData());
    }

    getB2bClientTransactionsListMetadata$(): Observable<Metadata> {
        return this._b2bState.b2bClientTransactionsList.getValue$().pipe(getMetadata());
    }

    clearB2bClientTransactionsList(): void {
        this._b2bState.b2bClientTransactionsList.setValue(null);
    }

    isB2bClientTransactionsListLoading$(): Observable<boolean> {
        return this._b2bState.b2bClientTransactionsList.isInProgress$();
    }

    // CONDITIONS

    loadConditions<T extends B2bConditionsGroupType>(groupType: T, request: GetB2bConditionsRequest<T>): void {
        this._b2bState.b2bConditions.setInProgress(true);
        this._b2bApi.getB2bConditions(groupType, request)
            .pipe(
                catchError((error: HttpErrorResponse) => {
                    if (error.status === 404) {
                        // hay que limpiar las condiciones cuando no existen para que se vacíe el formulario
                        this.clearConditions();
                    }
                    throw error;
                }),
                finalize(() => this._b2bState.b2bConditions.setInProgress(false))
            )
            .subscribe(conditions => this._b2bState.b2bConditions.setValue(conditions));
    }

    getConditions$(): Observable<B2bConditions> {
        return this._b2bState.b2bConditions.getValue$();
    }

    isConditionsInProgress$(): Observable<boolean> {
        return this._b2bState.b2bConditions.isInProgress$();
    }

    clearConditions(): void {
        this._b2bState.b2bConditions.setValue(null);
    }

    saveConditions(groupType: B2bConditionsGroupType, conditions: PutB2bConditions): Observable<void> {
        this._b2bState.b2bConditions.setInProgress(true);
        return this._b2bApi.putB2bConditions(groupType, conditions)
            .pipe(finalize(() => this._b2bState.b2bConditions.setInProgress(false)));
    }

    deleteConditions<T extends B2bConditionsGroupType>(
        groupType: T, req: DeleteB2bConditionsRequest<T>
    ): Observable<void> {
        this._b2bState.b2bConditions.setInProgress(true);
        return this._b2bApi.deleteB2bConditions(groupType, req)
            .pipe(finalize(() => this._b2bState.b2bConditions.setInProgress(false)));
    }

    // CONDITIONS CLIENTS

    loadConditionsClients<T extends B2bConditionsClientsGroupType>(groupType: T, request: GetB2bConditionsClientsRequest<T>): void {
        this._stopFetchingEventConditionsClients.next();
        this._b2bState.b2bConditionsClients.setInProgress(true);
        this._b2bApi.getB2bConditionsClients(groupType, request)
            .pipe(
                mapMetadata(),
                takeUntil(this._stopFetchingEventConditionsClients),
                catchError((error: HttpErrorResponse) => {
                    if (error.status === 404) {
                        // hay que limpiar las condiciones cuando no existen para que se vacíe el formulario
                        this._b2bState.b2bConditionsClients.setValue(null);
                    }
                    throw error;
                }),
                finalize(() => this._b2bState.b2bConditionsClients.setInProgress(false))
            )
            .subscribe(conditions => this._b2bState.b2bConditionsClients.setValue(conditions));
    }

    getConditionsClientsData$(): Observable<B2bConditionsClient[]> {
        return this._b2bState.b2bConditionsClients.getValue$().pipe(getListData());
    }

    getConditionsClientsMetadata$(): Observable<Metadata> {
        return this._b2bState.b2bConditionsClients.getValue$().pipe(getMetadata());
    }

    isConditionsClientsInProgress$(): Observable<boolean> {
        return this._b2bState.b2bConditionsClients.isInProgress$();
    }

    loadAllConditionsClients<T extends B2bConditionsClientsGroupType>(
        groupType: T, request?: GetB2bConditionsClientsRequest<T>
    ): void {
        this._stopFetchingAllEventConditionsClients.next();
        this._b2bState.allB2bConditionsClients.setInProgress(true);
        const pageSize = 100;
        const req: GetB2bConditionsClientsRequest<T> = { ...request, offset: 0, limit: pageSize };

        fetchAll((offset: number) => this._b2bApi.getB2bConditionsClients(groupType, { ...req, offset }))
            .pipe(
                mapMetadata(),
                finalize(() => this._b2bState.allB2bConditionsClients.setInProgress(false)),
                takeUntil(this._stopFetchingAllEventConditionsClients)
            )
            .subscribe(condClientsResponse => this._b2bState.allB2bConditionsClients.setValue(condClientsResponse));
    }

    getAllConditionsClients$(): Observable<B2bConditionsClient[]> {
        return this._b2bState.allB2bConditionsClients.getValue$().pipe(map(condClients => condClients?.data));
    }

    clearConditionsClients(): void {
        this._b2bState.b2bConditionsClients.setValue(null);
        this._b2bState.allB2bConditionsClients.setValue(null);
    }

    isAllB2bConditionsClientsLoading$(): Observable<boolean> {
        return this._b2bState.allB2bConditionsClients.isInProgress$();
    }

    saveConditionsClients(
        groupType: B2bConditionsClientsGroupType, conditionsClients: PutB2bConditionsClients
    ): Observable<void> {
        this._b2bState.b2bConditionsClients.setInProgress(true);
        return this._b2bApi.putB2bConditionsClients(groupType, conditionsClients).pipe(
            finalize(() => this._b2bState.b2bConditionsClients.setInProgress(false))
        );
    }

    deleteConditionsClients<T extends B2bConditionsClientsGroupType>(
        groupType: T, req: DeleteB2bConditionsClientsRequest<T>
    ): Observable<void> {
        this._b2bState.b2bConditionsClients.setInProgress(true);
        return this._b2bApi.deleteB2bConditionsClients(groupType, req)
            .pipe(
                finalize(() => this._b2bState.b2bConditionsClients.setInProgress(false))
            );
    }

    getClientsNames$(ids: number[], entityId: number): Observable<IdName[]> {
        return this._b2bState.clientsCache.getItems$(ids, id => (this._b2bApi.getB2bClient(id, entityId)) as Observable<IdName>);
    }

}

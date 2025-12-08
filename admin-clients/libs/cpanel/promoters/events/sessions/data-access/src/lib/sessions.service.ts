import { mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { PutSessionRefundConditions, SessionRefundConditions } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { PresalePost, PresalePut } from '@admin-clients/cpanel/shared/data-access';
import {
    GetPriceTypeRestricion, PostPriceTypeRestriction, RestrictedPriceZones
} from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { AttributeWithValues, PutAttribute } from '@admin-clients/shared/common/data-access';
import { WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, IdName } from '@admin-clients/shared/data-access/models';
import { cloneObject, fetchAll } from '@admin-clients/shared/utility/utils';
import {
    PriceTypeAvailability, SessionActivityGroupsConfig, SessionPriceType
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    BulkPutVenueTemplateElementInfoRequest, ElementsInfoFilterRequest, PostVenueTemplateElementInfoRequest,
    PutVenueTemplateElementInfoRequest, VenueTemplateElementInfo, VenueTemplateElementInfoImage, VenueTemplateElementInfoType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BehaviorSubject, EMPTY, forkJoin, Observable, of, ReplaySubject, Subject, switchMap, take, withLatestFrom, zip } from 'rxjs';
import { catchError, finalize, map, takeUntil, tap } from 'rxjs/operators';
import { EventSessionsApi } from './api/sessions.api';
import { AutomaticSalesExportRequest } from './models/automatic-sales-export-request.model';
import { AutomaticSalesPost } from './models/automatic-sales-post.model';
import { CloneSessionRequest } from './models/clone-session-request.model';
import { DeleteSessionsResponse } from './models/delete-sessions-response.model';
import { GetSessionsGroupsRequest } from './models/get-sessions-groups-request.model';
import { GetSessionsRequest } from './models/get-sessions-request.model';
import { GetSessionsResponse } from './models/get-sessions-response.model';
import { GetInternalBarcodesRequest, GetInternalBarcodesResponse } from './models/internal-barcode.model';
import { LinkedSession } from './models/linked-session.model';
import { PostRelocationSeats } from './models/post-relocation-seats.model';
import { PostSession } from './models/post-session.model';
import { PutSession } from './models/put-session.model';
import { PutSessionsResponse } from './models/put-sessions-response.model';
import { SaleConstraints } from './models/sale-constraints.model';
import { SessionAdditionalConfig } from './models/session-additional-config.model';
import { GetExternalBarcodesRequest, GetExternalBarcodesResponse, PostBarcodesToImport } from './models/session-barcode-to-import.model';
import { PostSessionZoneDynamicPrices, PutSessionDynamicPrices } from './models/session-dynamic-prices.model';
import { SessionExternalBarcodes } from './models/session-external-barcodes.model';
import { SessionExternalSessionsConfigRequest } from './models/session-external-sessions-config-request.model';
import { SessionExternalSessionsConfig } from './models/session-external-sessions-config.model';
import { SessionGroupType } from './models/session-group-type.enum';
import { SessionListFilters } from './models/session-list-filters.model';
import { SessionLoyaltyPoints } from './models/session-loyalty-points.model';
import { SessionQuotaCapacity } from './models/session-quota-capacity.model';
import { SessionStatus } from './models/session-status.enum';
import { SessionTiersAvailability } from './models/session-tiers-availability.model';
import { SessionWrapper } from './models/session-wrapper.model';
import { Session } from './models/session.model';
import { SessionsGroup } from './models/sessions-group.model';
import { EventSessionsState } from './state/sessions.state';

@Injectable()
export class EventSessionsService {
    private readonly _sessionsPageSize = 999;
    private readonly _initialSessionListFilters = {
        status: [
            SessionStatus.preview,
            SessionStatus.ready,
            SessionStatus.scheduled
        ],
        //initStartDate: moment().startOf('month').utc().format(),
        hourRanges: [],
        groupType: SessionGroupType.month
    };

    private readonly _sessionsApi = inject(EventSessionsApi);
    private readonly _sessionsState = inject(EventSessionsState);
    private readonly _selectedSessions = new ReplaySubject<SessionWrapper[]>(1);
    private readonly _selectedSessions$ = this._selectedSessions.asObservable();
    private readonly _refreshSessionList = new Subject<void>();
    private readonly _refreshSessionList$ = this._refreshSessionList.asObservable();
    private readonly _sessionListFilters = new BehaviorSubject<SessionListFilters>(this._initialSessionListFilters);
    private readonly _sessionListFilters$ = this._sessionListFilters.asObservable();

    readonly sessionList = Object.freeze({
        load: (eventId: number, request: GetSessionsRequest) => StateManager.load(
            this._sessionsState.sessionList,
            this._sessionsApi.getSessions(eventId, request).pipe(mapMetadata())
        ),
        loadMore: (eventId: number, request: GetSessionsRequest) =>
            StateManager.loadMore(request, this._sessionsState.sessionList, r => this._sessionsApi.getSessions(eventId, r)),
        get$: () => this._sessionsState.sessionList.getValue$(),
        getData$: () => this._sessionsState.sessionList.getValue$().pipe(map(list => list?.data)),
        getMetadata$: () => this._sessionsState.sessionList.getValue$().pipe(map(r => r?.metadata)),
        inProgress$: () => this._sessionsState.sessionList.isInProgress$(),
        clear: () => this._sessionsState.sessionList.setValue(null)
    });

    readonly session = Object.freeze({
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.session,
            this._sessionsApi.getSession(eventId, sessionId)
        ),
        cancelLoad: () => this._sessionsState.session.triggerCancellation(),
        get$: () => this._sessionsState.session.getValue$(),
        loading$: () => this._sessionsState.session.isInProgress$(),
        error$: () => this._sessionsState.session.getError$(),
        clear: () => this._sessionsState.session.setValue(null)
    });

    readonly automaticSales = Object.freeze({
        create: (sessionId: number, request: AutomaticSalesPost) => StateManager.inProgress(
            this._sessionsState.automaticSales,
            this._sessionsApi.postAutomaticSale(sessionId, request)
        ),
        load: (sessionId: number) => StateManager.load(
            this._sessionsState.automaticSales,
            this._sessionsApi.getAutomaticSale(sessionId)
        ),
        export: (sessionId: number, request: AutomaticSalesExportRequest): Observable<ExportResponse> => StateManager.inProgress(
            this._sessionsState.automaticSales,
            this._sessionsApi.exportAutomaticSale(sessionId, request)
        ),
        stop: (sessionId: number) => StateManager.inProgress(
            this._sessionsState.automaticSales,
            this._sessionsApi.putAutomaticSale(sessionId, { status: 'BLOCKED' })
        ),
        get$: () => this._sessionsState.automaticSales.getValue$(),
        loading$: () => this._sessionsState.automaticSales.isInProgress$(),
        error$: () => this._sessionsState.automaticSales.getError$(),
        clear: () => this._sessionsState.automaticSales.setValue(null)
    });

    readonly rates = Object.freeze({
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.rates,
            this._sessionsApi.getSessionRates(eventId, sessionId)
        ),
        get$: () => this._sessionsState.rates.getValue$(),
        loading$: () => this._sessionsState.rates.isInProgress$(),
        clear: () => this._sessionsState.rates.setValue(null)
    });

    readonly ratesRestrictions = Object.freeze({
        create: (eventId: number, sessionId: number, rateId: number, restrictions: Partial<RateRestrictions>) => StateManager.inProgress(
            this._sessionsState.ratesRestrictions,
            this._sessionsApi.postSessionRatesRestrictions(eventId, sessionId, rateId, restrictions)
        ),
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.ratesRestrictions,
            this._sessionsApi.getSessionRatesRestrictions(eventId, sessionId)
        ),
        get$: () => this._sessionsState.ratesRestrictions.getValue$(),
        loading$: () => this._sessionsState.ratesRestrictions.isInProgress$(),
        clear: () => this._sessionsState.ratesRestrictions.setValue(null),
        delete: (eventId: number, sessionId: number, rateId: number) => StateManager.inProgress(
            this._sessionsState.ratesRestrictions,
            this._sessionsApi.deleteSessionRatesRestrictions(eventId, sessionId, rateId)
        )
    });

    readonly presales = Object.freeze({
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.presales,
            this._sessionsApi.getPresales(eventId, sessionId)
        ),
        create: (eventId: number, sessionId: number, presale: PresalePost) => StateManager.inProgress(
            this._sessionsState.presales,
            this._sessionsApi.postPresale(eventId, sessionId, presale)
        ),
        update: (eventId: number, sessionId: number, presaleId: string, presale: PresalePut) => StateManager.inProgress(
            this._sessionsState.presales,
            this._sessionsApi.putPresale(eventId, sessionId, presaleId, presale)
        ),
        get$: () => this._sessionsState.presales.getValue$(),
        loading$: () => this._sessionsState.presales.isInProgress$(),
        delete: (eventId: number, sessionId: number, presaleId: string) => StateManager.inProgress(
            this._sessionsState.presales,
            this._sessionsApi.deletePresale(eventId, sessionId, presaleId)
        ),
        clear: () => this._sessionsState.presales.setValue(null)
    });

    readonly venueTplsElementInfo = Object.freeze({
        load: (sessionId: number, request: ElementsInfoFilterRequest) => StateManager.load(
            this._sessionsState.venueTplElementInfoList,
            this._sessionsApi.getVenueTemplatesElementInfo(sessionId, request)
        ),
        getData$: () => this._sessionsState.venueTplElementInfoList.getValue$()
            .pipe(map(venueTemplates => venueTemplates?.data)),
        getMetadata$: () => this._sessionsState.venueTplElementInfoList.getValue$()
            .pipe(map(response =>
                (response?.metadata && Object.assign(new Metadata(), response.metadata)))),
        inProgress$: () => this._sessionsState.venueTplElementInfoList.isInProgress$(),
        clear: () => {
            this._sessionsState.venueTplElementInfoList.setError(null);
            this._sessionsState.venueTplElementInfoList.setValue(null);
        }
    });

    readonly venueTplElementInfo = Object.freeze({
        load: (sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType): void => StateManager.load(
            this._sessionsState.venueTplElementInfo,
            this._sessionsApi.getVenueTemplateElementInfo(sessionId, elementInfoId, type)
        ),
        create: (sessionId: number, elementInfo: PostVenueTemplateElementInfoRequest) =>
            StateManager.inProgress(
                this._sessionsState.venueTplElementInfo,
                this._sessionsApi.postVenueTemplateElementInfo(sessionId, elementInfo)
            ),
        update: (sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, elementInfo: PutVenueTemplateElementInfoRequest) =>
            StateManager.inProgress(
                this._sessionsState.venueTplElementInfo,
                this._sessionsApi.putVenueTemplateElementInfo(sessionId, elementInfoId, type, elementInfo)
            ),
        recoverInheritance: (sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType) =>
            StateManager.inProgress(
                this._sessionsState.venueTplElementInfo,
                this._sessionsApi.recoverInheritanceVenueTemplateElementInfo(sessionId, elementInfoId, type)
            ),
        changeStatus: (sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, status: 'ENABLED' | 'DISABLED') =>
            StateManager.inProgress(
                this._sessionsState.venueTplElementInfo,
                this._sessionsApi.changeElementInfoStatus(sessionId, elementInfoId, type, status)
            ),
        updateMultiple: (
            sessionId: number,
            elements: VenueTemplateElementInfo[],
            elementInfo: PutVenueTemplateElementInfoRequest,
            all?: boolean,
            filters?: ElementsInfoFilterRequest
        ) =>
            StateManager.inProgress(
                this._sessionsState.venueTplElementInfo,
                ((sessionId, elements, elementInfo, all, filters) => {
                    const bulkReq: BulkPutVenueTemplateElementInfoRequest = {
                        update_all_elements_info: true,
                        element_info: elementInfo,
                        elements_type_related_id_map: null
                    };
                    if (!all) {
                        bulkReq.update_all_elements_info = false;
                        bulkReq.elements_type_related_id_map = {
                            [VenueTemplateElementInfoType.nnz]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.nnz)
                                .map(elementInfo => elementInfo.id),
                            [VenueTemplateElementInfoType.priceType]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.priceType)
                                .map(elementInfo => elementInfo.id),
                            [VenueTemplateElementInfoType.view]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.view)
                                .map(elementInfo => elementInfo.id)
                        };
                    }
                    return this._sessionsApi.putMultipleVenueTemplateElementInfo(sessionId, bulkReq, filters);
                })(sessionId, elements, elementInfo, all, filters)
            ),
        get$: () => this._sessionsState.venueTplElementInfo.getValue$(),
        inProgress$: () => this._sessionsState.venueTplElementInfo.isInProgress$(),
        clear: () => this._sessionsState.venueTplElementInfo.setValue(null)
    });

    readonly venueTplElementInfoImages = Object.freeze({
        delete: (sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, imagesToDelete: VenueTemplateElementInfoImage[]) => StateManager.inProgress(
            this._sessionsState.venueTplElementInfoImages,
            zip(...imagesToDelete.map(request =>
                this._sessionsApi.deleteVenueTemplateElementInfoImage(sessionId, elementInfoId, type, request.language, request.type, request.position)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this._sessionsState.venueTplElementInfoImages.isInProgress$(),
        clear: () => this._sessionsState.venueTplElementInfoImages.setValue(null)
    });

    readonly loyaltyPoints = Object.freeze({
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.loyaltyPoints, this._sessionsApi.getSessionLoyaltyPoints(eventId, sessionId)),
        get$: () => this._sessionsState.loyaltyPoints.getValue$(),
        update: (eventId: number, sessionId: number, config: SessionLoyaltyPoints) =>
            StateManager.inProgress(this._sessionsState.loyaltyPoints, this._sessionsApi.putSessionLoyaltyPoints(eventId, sessionId, config)),
        loading$: () => this._sessionsState.loyaltyPoints.isInProgress$(),
        clear: () => this._sessionsState.loyaltyPoints.setValue(null)
    });

    readonly mapping = Object.freeze({
        create: (eventId: number, sessionId: number) => StateManager.inProgress(
            this._sessionsState.mapping,
            this._sessionsApi.postMapping(eventId, sessionId)
        ),
        loading$: () => this._sessionsState.mapping.isInProgress$(),
        error$: () => this._sessionsState.mapping.getError$(),
        clear: () => this._sessionsState.mapping.setValue(null)
    });

    readonly dynamicPrices = Object.freeze({
        load: (eventId: number, sessionId: number) => StateManager.load(
            this._sessionsState.dynamicPrices, this._sessionsApi.getDynamicPrices(eventId, sessionId)),
        get$: () => this._sessionsState.dynamicPrices.getValue$(),
        update: (eventId: number, sessionId: number, config: PutSessionDynamicPrices) =>
            StateManager.inProgress(this._sessionsState.dynamicPrices, this._sessionsApi.putDynamicPrices(eventId, sessionId, config)),
        loading$: () => this._sessionsState.dynamicPrices.isInProgress$(),
        clear: () => this._sessionsState.dynamicPrices.setValue(null)
    });

    readonly zoneDynamicPrices = Object.freeze({
        load: (eventId: number, sessionId: number, zoneId: number) => StateManager.load(
            this._sessionsState.zoneDynamicPrices, this._sessionsApi.getZoneDynamicPrices(eventId, sessionId, zoneId)),
        get$: () => this._sessionsState.zoneDynamicPrices.getValue$(),
        loading$: () => this._sessionsState.zoneDynamicPrices.isInProgress$(),
        clear: () => this._sessionsState.zoneDynamicPrices.setValue(null),
        post: (eventId: number, sessionId: number, zoneId: number, body: PostSessionZoneDynamicPrices) => StateManager.inProgress(
            this._sessionsState.zoneDynamicPrices, this._sessionsApi.postZoneDynamicPrices(eventId, sessionId, zoneId, body)),
        delete: (eventId: number, sessionId: number, zoneId: number, orderIndex: number) => StateManager.inProgress(
            this._sessionsState.zoneDynamicPrices, this._sessionsApi.deleteZoneDynamicPrice(eventId, sessionId, zoneId, orderIndex))
    });

    clearSessionsState(): void {
        this.sessionList.clear();
        this.session.clear();
        this.clearAllSessions();
        this._sessionsState.sessionsGroups.setValue(null);
    }

    loadAllSessions(eventId: number, request?: Partial<GetSessionsRequest>): void {
        this._sessionsState.allSessions.triggerCancellation();
        this._sessionsState.allSessions.setInProgress(true);
        const req: GetSessionsRequest = Object.assign({ offset: 0, limit: this._sessionsPageSize }, request);
        fetchAll((offset: number) => this._sessionsApi.getSessions(eventId, { ...req, offset }))
            .pipe(
                finalize(() => this._sessionsState.allSessions.setInProgress(false)),
                takeUntil(this._sessionsState.allSessions.getCancellation$())
            )
            .subscribe(result => this._sessionsState.allSessions.setValue(result));
    }

    getAllSessions$(): Observable<GetSessionsResponse> {
        return this._sessionsState.allSessions.getValue$();
    }

    getAllSessionsData$(): Observable<Session[]> {
        return this._sessionsState.allSessions.getValue$()
            .pipe(map(events => events?.data));
    }

    isAllSessionsLoading$(): Observable<boolean> {
        return this._sessionsState.allSessions.isInProgress$();
    }

    clearAllSessions(): void {
        this._sessionsState.allSessions.setValue(null);
    }

    setAllSessionsUpdatingCapacityUpdater(sessionMessages$: Observable<WsSessionMsg>, destroyRef: DestroyRef): void {
        sessionMessages$
            .pipe(
                withLatestFrom(this._sessionsState.allSessions.getValue$()),
                takeUntilDestroyed(destroyRef)
            )
            .subscribe(([msg, allSessions]) => {
                const session = allSessions?.data?.find(session => session.id === msg.data.id);
                if (session?.id === msg.data.id) {
                    session.updating_capacity = msg.status === WsMsgStatus.inProgress;
                    this._sessionsState.allSessions.setValue(allSessions);
                }
            });
    }

    loadAllSessionsReducedModel(
        eventId: number,
        request?: Partial<GetSessionsRequest>
    ): void {
        this._sessionsState.allSessionsReducedModel.triggerCancellation();
        this._sessionsState.allSessionsReducedModel.setInProgress(true);
        const req: GetSessionsRequest = Object.assign({
            offset: 0,
            limit: this._sessionsPageSize
        }, request);

        fetchAll((offset: number) => this._sessionsApi.getSessions(eventId, { ...req, offset }))
            .pipe(
                finalize(() => this._sessionsState.allSessionsReducedModel.setInProgress(false)),
                takeUntil(this._sessionsState.allSessionsReducedModel.getCancellation$())
            )
            .subscribe(result => {
                this._sessionsState.allSessionsReducedModel.setValue(result);
            });
    }

    getAllSessionsReducedModel$(): Observable<GetSessionsResponse> {
        return this._sessionsState.allSessionsReducedModel.getValue$();
    }

    isAllSessionsReducedModelLoading$(): Observable<boolean> {
        return this._sessionsState.allSessionsReducedModel.isInProgress$();
    }

    clearAllSessionsReducedModel(): void {
        this._sessionsState.allSessionsReducedModel.setValue(null);
    }

    loadSessionsGroups(eventId: number, request: GetSessionsGroupsRequest): void {
        this._sessionsState.sessionsGroups.setInProgress(true);
        this._sessionsApi.getSessionsGroups(eventId, request)
            .pipe(
                finalize(() => this._sessionsState.sessionsGroups.setInProgress(false))
            )
            .subscribe(groups => this._sessionsState.sessionsGroups.setValue(groups));
    }

    getSessionsGroups$(): Observable<SessionsGroup[]> {
        return this._sessionsState.sessionsGroups.getValue$();
    }

    isSessionsGroupsLoading$(): Observable<boolean> {
        return this._sessionsState.sessionsGroups.isInProgress$();
    }

    createSession(eventId: number, session: PostSession): Observable<number> {
        this._sessionsState.savingSession.setInProgress(true);
        return this._sessionsApi.postSession(eventId, session)
            .pipe(
                map(result => result.id),
                finalize(() => this._sessionsState.savingSession.setInProgress(false))
            );
    }

    updateSession(eventId: number, sessionId: number, session: Session | PutSession): Observable<void> {
        this._sessionsState.savingSession.setInProgress(true);
        return this._sessionsApi.putSession(eventId, sessionId, session)
            .pipe(finalize(() => this._sessionsState.savingSession.setInProgress(false)));
    }

    isSessionSaving$(): Observable<boolean> {
        return this._sessionsState.savingSession.isInProgress$();
    }

    // this property is updated only by SessionsCapacityService and required in sessions-container,
    // but session capacity service is optional and in some scenarios it's not instantiated.
    isVenueMapSaving$(): Observable<boolean> {
        return this._sessionsState.venueMapSaving.isInProgress$();
    }

    setVenueMapSaving(saving: boolean): void {
        return this._sessionsState.venueMapSaving.setInProgress(saving);
    }

    deleteSession(eventId: number, sessionId: number, packRelatedSessionsSeats?: number): Observable<void> {
        this._sessionsState.savingSession.setInProgress(true);
        return this._sessionsApi.deleteSession(eventId, sessionId, packRelatedSessionsSeats)
            .pipe(finalize(() => this._sessionsState.savingSession.setInProgress(false)));
    }

    cloneSession(eventId: number, fromSessionId: number, data: CloneSessionRequest): Observable<number> {
        this._sessionsState.savingSession.setInProgress(true);
        return this._sessionsApi.cloneSession(eventId, fromSessionId, data).pipe(
            map(response => response.id),
            finalize(() => this._sessionsState.savingSession.setInProgress(false)));
    }

    createSessions(eventId: number, sessions: PostSession[]): Observable<number[]> {
        this._sessionsState.creatingSessions.setInProgress(true);
        return this._sessionsApi.postSessions(eventId, sessions)
            .pipe(
                finalize(() => this._sessionsState.creatingSessions.setInProgress(false))
            );
    }

    isCreatingSessions$(): Observable<boolean> {
        return this._sessionsState.creatingSessions.isInProgress$();
    }

    updateSessions(eventId: number, sessions: unknown, isPreview = false): Observable<PutSessionsResponse[]> {
        this._sessionsState.updatingSessions.setInProgress(true);
        return this._sessionsApi.putSessions(eventId, sessions, isPreview)
            .pipe(
                finalize(() => {
                    this._sessionsState.updatingSessions.setInProgress(false);
                })
            );
    }

    isUpdateSessionsInProgress$(): Observable<boolean> {
        return this._sessionsState.updatingSessions.isInProgress$();
    }

    deleteSessions(eventId: number, sessionIds: number[], isPreview: boolean): Observable<DeleteSessionsResponse[]> {
        this._sessionsState.deletingSessions.setInProgress(true);
        return this._sessionsApi.deleteSessions(eventId, sessionIds, isPreview)
            .pipe(finalize(() => this._sessionsState.deletingSessions.setInProgress(false)));
    }

    isDeleteSessionsInProgress$(): Observable<boolean> {
        return this._sessionsState.deletingSessions.isInProgress$();
    }

    loadLinkedSessions(eventId: number, sessionId: number): void {
        this._sessionsState.linkedSessions.setInProgress(true);
        this._sessionsApi.getLinkedSessions(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.linkedSessions.setInProgress(false)))
            .subscribe(sessions => this._sessionsState.linkedSessions.setValue(sessions));
    }

    // PENDING CAPACITY UPDATES

    getPendingCapacityUpdates$(): Observable<Map<number, WsMsgStatus>> {
        return this._sessionsState.pendingCapacityUpdates.getValue$();
    }

    clearPendingCapacityUpdates(): void {
        this._sessionsState.pendingCapacityUpdates.setValue(new Map<number, WsMsgStatus>());
    }

    initSessionCapacityUpdateState(sessionId: number): void {
        this._sessionsState.pendingCapacityUpdates.getValue$().pipe(take(1)).subscribe(pendingCapacityUpdates => {
            pendingCapacityUpdates.set(sessionId, WsMsgStatus.inProgress);
            this._sessionsState.pendingCapacityUpdates.setValue(cloneObject(pendingCapacityUpdates));
        });
    }

    setUpdateSessionCapacityUpdateState(sessionMessages$: Observable<WsSessionMsg>, destroyRef: DestroyRef): void {
        sessionMessages$
            .pipe(
                withLatestFrom(this._sessionsState.pendingCapacityUpdates.getValue$()),
                takeUntilDestroyed(destroyRef)
            )
            .subscribe(([msg, pendingCapacityUpdates]) => {
                if (pendingCapacityUpdates.has(msg.data.id)) {
                    pendingCapacityUpdates.set(msg.data.id, msg.status);
                    this._sessionsState.pendingCapacityUpdates.setValue(cloneObject(pendingCapacityUpdates));
                }
            });
    }

    deleteSessionCapacityUpdate(sessionId: number): void {
        this._sessionsState.pendingCapacityUpdates.getValue$().pipe(take(1)).subscribe(pendingCapacityUpdates => {
            if (pendingCapacityUpdates.has(sessionId)) {
                pendingCapacityUpdates.delete(sessionId);
                this._sessionsState.pendingCapacityUpdates.setValue(cloneObject(pendingCapacityUpdates));
            }
        });
    }

    getLinkedSessions$(): Observable<LinkedSession[]> {
        return this._sessionsState.linkedSessions.getValue$();
    }

    clearLinkedSessions(): void {
        this._sessionsState.linkedSessions.setValue(null);
    }

    setSessionUpdatingCapacityUpdater(sessionMessages$: Observable<WsSessionMsg>, destroyRef: DestroyRef): void {
        sessionMessages$
            .pipe(
                withLatestFrom(this._sessionsState.session.getValue$()),
                takeUntilDestroyed(destroyRef)
            )
            .subscribe(([msg, session]) => {
                if (session?.id === msg.data.id) {
                    session.updating_capacity = msg.status === WsMsgStatus.inProgress;
                    this._sessionsState.session.setValue(session);
                }
            });
    }

    loadSessionAdditionalConfig(eventId: number, sessionId: number): void {
        this._sessionsState.sessionAdditionalConfig.setValue(null);
        this._sessionsState.sessionAdditionalConfig.setInProgress(true);
        this._sessionsApi.getSessionAdditionalConfig(eventId, sessionId)
            .pipe(
                finalize(() => this._sessionsState.sessionAdditionalConfig.setInProgress(false))
            )
            .subscribe(additionalConfig => {
                this._sessionsState.sessionAdditionalConfig.setValue(additionalConfig);
            });
    }

    getSessionAdditionalConfig$(): Observable<SessionAdditionalConfig> {
        return this._sessionsState.sessionAdditionalConfig.getValue$();
    }

    clearSessionAdditionalConfig(): void {
        this._sessionsState.sessionAdditionalConfig.setValue(null);
    }

    isSessionAdditionalConfigLoading$(): Observable<boolean> {
        return this._sessionsState.sessionAdditionalConfig.isInProgress$();
    }

    setSelectedSessions(sessionWrappers: SessionWrapper[]): void {
        this._selectedSessions.next(sessionWrappers);
    }

    getSelectedSessions$(): Observable<SessionWrapper[]> {
        return this._selectedSessions$;
    }

    getRefreshSessionsList$(): Observable<void> {
        return this._refreshSessionList$;
    }

    setRefreshSessionsList(): void {
        this._refreshSessionList.next();
    }

    getSessionListFilters$(): Observable<SessionListFilters> {
        return this._sessionListFilters$;
    }

    setSessionListFilters(value: SessionListFilters): void {
        this._sessionListFilters.next(value);
    }

    resetSessionListFilters(): void {
        this._sessionListFilters.next(this._initialSessionListFilters);
    }

    loadSaleConstraints(eventId: number, sessionId: number): void {
        this._sessionsState.saleConstraints.setInProgress(true);
        this._sessionsApi.getSaleConstraints(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.saleConstraints.setInProgress(false)))
            .subscribe(saleConstraints => this._sessionsState.saleConstraints.setValue(saleConstraints));
    }

    getSaleConstraints(): Observable<SaleConstraints> {
        return this._sessionsState.saleConstraints.getValue$();
    }

    updateSaleConstraints(eventId: number, sessionId: number, saleConstraint: SaleConstraints): Observable<void> {
        this._sessionsState.saleConstraints.setInProgress(true);
        return this._sessionsApi.putSaleConstraints(eventId, sessionId, saleConstraint)
            .pipe(finalize(() => this._sessionsState.saleConstraints.setInProgress(false)));
    }

    isSaleConstraintsInProgress$(): Observable<boolean> {
        return this._sessionsState.saleConstraints.isInProgress$();
    }

    clearSaleConstraints(): void {
        this._sessionsState.saleConstraints.setValue(null);
    }

    deleteCartLimit(eventId: number, sessionId: number): Observable<void> {
        this._sessionsState.cartLimit.setInProgress(true);
        return this._sessionsApi.deleteCartLimit(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.cartLimit.setInProgress(false)));
    }

    isCartLimitInProgress$(): Observable<boolean> {
        return this._sessionsState.cartLimit.isInProgress$();
    }

    deletePriceTypeLimit(eventId: number, sessionId: number): Observable<void> {
        this._sessionsState.priceTypeLimit.setInProgress(true);
        return this._sessionsApi.deletePriceTypeLimit(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.priceTypeLimit.setInProgress(false)));
    }

    isPriceTypeLimitInProgress$(): Observable<boolean> {
        return this._sessionsState.priceTypeLimit.isInProgress$();
    }

    loadSessionTiersAvailability(eventId: number, sessionId: number): void {
        this._sessionsState.sessionTiersAvailability.setInProgress(true);
        this._sessionsApi.getSessionTiersAvailability(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.sessionTiersAvailability.setInProgress(false)))
            .subscribe(sessionTiersAvailability => this._sessionsState.sessionTiersAvailability.setValue(sessionTiersAvailability));
    }

    getSessionTiersAvailability$(): Observable<SessionTiersAvailability[]> {
        return this._sessionsState.sessionTiersAvailability.getValue$();
    }

    clearSessionTiersAvailability(): void {
        this._sessionsState.sessionTiersAvailability.setValue(null);
    }

    isSessionTiersAvailabilityInProgress$(): Observable<boolean> {
        return this._sessionsState.sessionTiersAvailability.isInProgress$();
    }

    loadSessionAttributes(eventId: number, sessionId: number, fullLoad: boolean): void {
        this._sessionsState.sessionAttributes.setInProgress(true);
        this._sessionsApi.getSessionAttributes(eventId, sessionId, fullLoad)
            .pipe(
                finalize(() => this._sessionsState.sessionAttributes.setInProgress(false))
            )
            .subscribe(attributes => this._sessionsState.sessionAttributes.setValue(attributes));
    }

    getSessionAttributes$(): Observable<AttributeWithValues[]> {
        return this._sessionsState.sessionAttributes.getValue$();
    }

    isSessionAttributesInProgress$(): Observable<boolean> {
        return this._sessionsState.sessionAttributes.isInProgress$();
    }

    saveSessionAttributes(eventId: number, sessionId: number, attributes: PutAttribute[]): Observable<void> {
        this._sessionsState.sessionAttributes.setInProgress(true);
        return this._sessionsApi.putSessionAttributes(eventId, sessionId, attributes)
            .pipe(
                finalize(() => this._sessionsState.sessionAttributes.setInProgress(false))
            );
    }

    clearSessionAttributes(): void {
        this._sessionsState.sessionAttributes.setValue(null);
    }

    clearQuotaCapacities(): void {
        this._sessionsState.quotaCapacities.setValue(null);
    }

    loadQuotaCapacities(eventId: number, sessionId: number): void {
        this._sessionsState.quotaCapacities.setInProgress(true);
        this._sessionsApi.getQuotasCapacities(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.quotaCapacities.setInProgress(false)))
            .subscribe(quotaCapacities => this._sessionsState.quotaCapacities.setValue(quotaCapacities));
    }

    updateQuotaCapacities(eventId: number, sessionId: number, quotaCapacities: SessionQuotaCapacity[]): Observable<void> {
        this._sessionsState.quotaCapacities.setInProgress(true);
        return this._sessionsApi.putQuotaCapacities(eventId, sessionId, quotaCapacities)
            .pipe(finalize(() => this._sessionsState.quotaCapacities.setInProgress(false)));
    }

    isQuotaCapacitiesInProgress$(): Observable<boolean> {
        return this._sessionsState.quotaCapacities.isInProgress$();
    }

    getQuotaCapacities$(): Observable<SessionQuotaCapacity[]> {
        return this._sessionsState.quotaCapacities.getValue$();
    }

    refreshExternalAvailability(eventId: number, sessionId: number): Observable<void> {
        this._sessionsState.refreshExternalAvailability.setInProgress(true);
        return this._sessionsApi.putExternalAvailability(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.refreshExternalAvailability.setInProgress(false)));
    }

    isRefreshExternalAvailabilityInProgress$(): Observable<boolean> {
        return this._sessionsState.refreshExternalAvailability.isInProgress$();
    }

    refreshExternalMembershipInventory(eventId: number): Observable<void> {
        this._sessionsState.refreshExternalMembershipInventory.setInProgress(true);
        return this._sessionsApi.putExternalMembershipInventory(eventId)
            .pipe(finalize(() => this._sessionsState.refreshExternalMembershipInventory.setInProgress(false)));
    }

    isRefreshExternalMembershipInventoryInProgress$(): Observable<boolean> {
        return this._sessionsState.refreshExternalMembershipInventory.isInProgress$();
    }

    loadPriceTypeAvailability(eventId: number, sessionId: number): void {
        this._sessionsState.priceTypeAvailability.setInProgress(true);
        this._sessionsApi.getSessionPriceTypesAvailability(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.priceTypeAvailability.setInProgress(false)))
            .subscribe(priceTypeAvailabilities => this._sessionsState.priceTypeAvailability.setValue(priceTypeAvailabilities));
    }

    getPriceTypeAvailability$(): Observable<PriceTypeAvailability[]> {
        return this._sessionsState.priceTypeAvailability.getValue$();
    }

    isPriceTypeAvailabilityInProgress$(): Observable<boolean> {
        return this._sessionsState.priceTypeAvailability.isInProgress$();
    }

    loadSessionPriceTypes(eventId: number, sessionId: number): void {
        this._sessionsState.priceTypes.setInProgress(true);
        this._sessionsApi.getSessionPriceTypes(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.priceTypes.setInProgress(false)))
            .subscribe(priceTypes => this._sessionsState.priceTypes.setValue(priceTypes));
    }

    getSessionPriceTypes$(): Observable<SessionPriceType[]> {
        return this._sessionsState.priceTypes.getValue$();
    }

    isSessionPriceTypesLoading$(): Observable<boolean> {
        return this._sessionsState.priceTypes.isInProgress$();
    }

    updateSessionPriceTypes(eventId: number, sessionId: number, priceTypes: SessionPriceType[]): Observable<void> {
        this._sessionsState.savingPriceTypes.setInProgress(true);
        return forkJoin(priceTypes.map(priceType => this._sessionsApi.putSessionPriceType(eventId, sessionId, priceType)))
            .pipe(
                map(() => null),
                finalize(() => this._sessionsState.savingPriceTypes.setInProgress(false))
            );
    }

    isSessionPriceTypesSaving$(): Observable<boolean> {
        return this._sessionsState.savingPriceTypes.isInProgress$();
    }

    loadSessionActivityGroupsConfig(eventId: number, sessionId: number): void {
        this._sessionsState.activityGroupsConfig.setInProgress(true);
        this._sessionsApi.getSessionGroupConfig(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.activityGroupsConfig.setInProgress(false)))
            .subscribe(config => this._sessionsState.activityGroupsConfig.setValue(config));
    }

    updateSessionActivityGroupsConfig(eventId: number, sessionId: number, groupConfig: SessionActivityGroupsConfig): Observable<void> {
        this._sessionsState.activityGroupsConfig.setInProgress(true);
        return this._sessionsApi.putSessionGroupConfig(eventId, sessionId, groupConfig)
            .pipe(finalize(() => this._sessionsState.activityGroupsConfig.setInProgress(false)));
    }

    clearSessionActivityGroupConfig(): void {
        this._sessionsState.activityGroupsConfig.setValue(null);
    }

    getSessionActivityGroupsConfig$(): Observable<SessionActivityGroupsConfig> {
        return this._sessionsState.activityGroupsConfig.getValue$();
    }

    isSessionActivityGroupsConfigLoading$(): Observable<boolean> {
        return this._sessionsState.activityGroupsConfig.isInProgress$();
    }

    loadSessionRefundConditions(eventId: number, sessionId: number): void {
        this._sessionsState.refundConditions.setInProgress(true);
        this._sessionsApi.getRefundConditions(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.refundConditions.setInProgress(false)))
            .subscribe(refundConditions => this._sessionsState.refundConditions.setValue(refundConditions));
    }

    updateSessionRefundConditions(eventId: number, sessionId: number, conditions: PutSessionRefundConditions): Observable<void> {
        this._sessionsState.refundConditions.setInProgress(true);
        return this._sessionsApi.putRefundConditions(eventId, sessionId, conditions)
            .pipe(
                catchError(() => EMPTY),
                finalize(() => this._sessionsState.refundConditions.setInProgress(false))
            );
    }

    getSessionRefundConditions$(): Observable<SessionRefundConditions> {
        return this._sessionsState.refundConditions.getValue$();
    }

    isSessionRefundConditionsLoading$(): Observable<boolean> {
        return this._sessionsState.refundConditions.isInProgress$();
    }

    loadTemplateIsInUse(eventId: number, templateId: number): void {
        this._sessionsState.templateInUse.setInProgress(true);
        this._sessionsApi.getSessions(eventId, {
            offset: 0,
            limit: 0,
            venueTplId: templateId
        })
            .pipe(finalize(() => this._sessionsState.templateInUse.setInProgress(false)))
            .subscribe(result => this._sessionsState.templateInUse.setValue(result.metadata.total > 0));
    }

    isTemplateInUse$(): Observable<boolean> {
        return this._sessionsState.templateInUse.getValue$();
    }

    // SESSION PRICE TYPE RESTRICTIONS

    loadVenueTplRestrictedPriceTypes(eventId: number, sessionId: number): void {
        this._sessionsState.restrictedPriceTypes.setInProgress(true);
        this._sessionsApi.getVenueTplRestrictedPriceTypes(eventId, sessionId)
            .pipe(
                finalize(() => this._sessionsState.restrictedPriceTypes.setInProgress(false))
            )
            .subscribe(priceTypes => this._sessionsState.restrictedPriceTypes.setValue(priceTypes));
    }

    getVenueTplRestrictedPriceTypes$(): Observable<RestrictedPriceZones> {
        return this._sessionsState.restrictedPriceTypes.getValue$();
    }

    loadPriceTypeRestriction$(eventId: number, sessionId: number, priceTypeId: number): Observable<GetPriceTypeRestricion> {
        this._sessionsState.priceTypeRestriction.setInProgress(true);
        return this._sessionsApi.getPriceTypeRestriction(eventId, sessionId, priceTypeId)
            .pipe(
                finalize(() => this._sessionsState.priceTypeRestriction.setInProgress(false)),
                tap(restriction => this._sessionsState.priceTypeRestriction.setValue(restriction))
            );
    }

    savePriceTypeRestriction(
        eventId: number, sessionId: number, priceTypeId: number, restriction: PostPriceTypeRestriction
    ): Observable<void> {
        this._sessionsState.priceTypeRestriction.setInProgress(true);
        return this._sessionsApi.postPriceTypeRestriction(eventId, sessionId, priceTypeId, restriction)
            .pipe(finalize(() => this._sessionsState.priceTypeRestriction.setInProgress(false)));
    }

    deletePriceTypeRestriction(eventId: number, sessionId: number, priceTypeId: number): Observable<void> {
        this._sessionsState.priceTypeRestriction.setInProgress(true);
        return this._sessionsApi.deletePriceTypeRestriction(eventId, sessionId, priceTypeId)
            .pipe(finalize(() => this._sessionsState.priceTypeRestriction.setInProgress(false)));
    }

    clearPriceTypesRestriction(): void {
        return this._sessionsState.priceTypeRestriction.setValue(null);
    }
    // INTERNAL BARCODES

    loadInternalBarcodes(eventId: number, sessionId: number, req?: GetInternalBarcodesRequest): void {
        this._sessionsState.whiteList.setInProgress(true);
        this._sessionsApi.getWhiteList(eventId, sessionId, req)
            .pipe(finalize(() => this._sessionsState.whiteList.setInProgress(false)))
            .subscribe(list => this._sessionsState.whiteList.setValue(list));
    }

    getInternalBarcodesList$(): Observable<GetInternalBarcodesResponse> {
        return this._sessionsState.whiteList.getValue$();
    }

    isInternalBarcodesLoading$(): Observable<boolean> {
        return this._sessionsState.whiteList.isInProgress$();
    }

    clearInternalBarcodes(): void {
        this._sessionsState.whiteList.setValue(null);
    }

    isExportWhitelistLoading$(): Observable<boolean> {
        return this._sessionsState.exportWhitelist.isInProgress$();
    }

    exportSessionWhitelist(eventId: number, sessionId: number, body: ExportRequest): Observable<ExportResponse> {
        this._sessionsState.exportWhitelist.setInProgress(true);
        return this._sessionsApi.exportWhitelist(eventId, sessionId, body)
            .pipe(finalize(() => this._sessionsState.exportWhitelist.setInProgress(false)));
    }

    // EXTERNAL BARCODES

    importBarcodes(eventId: number, sessionId: number, postBarcodesToImport: PostBarcodesToImport): void {
        this._sessionsState.importBarcodesReference.setInProgress(true);
        this._sessionsApi.postExternalBarcodesImport(eventId, sessionId, postBarcodesToImport)
            .pipe(
                finalize(() => this._sessionsState.importBarcodesReference.setInProgress(false))
            )
            .subscribe(reference => this._sessionsState.importBarcodesReference.setValue(reference?.id));
    }

    getBarcodesImportId$(): Observable<number> {
        return this._sessionsState.importBarcodesReference.getValue$();
    }

    clearBarcodesImportId(): void {
        this._sessionsState.importBarcodesReference.setValue(null);
    }

    isBarcodesImportInProgress$(): Observable<boolean> {
        return this._sessionsState.importBarcodesReference.isInProgress$();
    }

    getUploadedExternalBarcodes$(): Observable<GetExternalBarcodesResponse> {
        return this._sessionsState.uploadedExternalBarcodes.getValue$();
    }

    isUploadedExternalBarcodesInProgress$(): Observable<boolean> {
        return this._sessionsState.uploadedExternalBarcodes.isInProgress$();
    }

    clearUploadedExternalBarcodes(): void {
        this._sessionsState.uploadedExternalBarcodes.setValue(null);
    }

    loadUploadedExternalBarcodes(eventId: number, sessionId: number, req?: GetExternalBarcodesRequest): void {
        this._sessionsState.uploadedExternalBarcodes.setInProgress(true);
        this._sessionsApi.getUploadedExternalBarcodes(eventId, sessionId, req)
            .pipe(finalize(() => this._sessionsState.uploadedExternalBarcodes.setInProgress(false)))
            .subscribe(list => {
                list.metadata = Object.assign(new Metadata(), list.metadata);
                this._sessionsState.uploadedExternalBarcodes.setValue(list);
            });
    }

    exportExternalBarcodes(eventId: number, sessionId: number, body: ExportRequest): Observable<ExportResponse> {
        this._sessionsState.exportExternalBarcodes.setInProgress(true);
        return this._sessionsApi.exportExternalBarcodes(eventId, sessionId, body)
            .pipe(finalize(() => this._sessionsState.exportExternalBarcodes.setInProgress(false)));
    }

    //Session external barcodes
    loadSessionExternalBarcodes(eventId: number, sessionId: number): void {
        this._sessionsState.sessionExternalBarcodes.setInProgress(true);
        this._sessionsApi.getSessionExternalBarcodes(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.sessionExternalBarcodes.setInProgress(false)))
            .subscribe(externalBarcodes => this._sessionsState.sessionExternalBarcodes.setValue(externalBarcodes));
    }

    getSessionExternalBarcodes$(): Observable<SessionExternalBarcodes> {
        return this._sessionsState.sessionExternalBarcodes.getValue$();
    }

    clearSessionExternalBarcodes(): void {
        this._sessionsState.sessionExternalBarcodes.setValue(null);
    }

    isSessionExternalBarcodesLoading$(): Observable<boolean> {
        return this._sessionsState.sessionExternalBarcodes.isInProgress$();
    }

    saveSessionExternalBarcodeConfig(eventId: number, sessionId: number, externalBarcodes: Partial<SessionExternalBarcodes>):
        Observable<void> {
        this._sessionsState.sessionExternalBarcodesSaving.setInProgress(true);
        return this._sessionsApi.putSessionExternalBarcodeConfig(eventId, sessionId, externalBarcodes)
            .pipe(finalize(() => this._sessionsState.sessionExternalBarcodesSaving.setInProgress(false)));
    }

    isSessionExternalBarcodesSaving$(): Observable<boolean> {
        return this._sessionsState.sessionExternalBarcodesSaving.isInProgress$();
    }

    // External sessions config
    loadSessionExternalSessionsConfig(eventId: number, sessionId: number): void {
        this._sessionsState.sessionExternalSessionsConfig.setInProgress(true);
        this._sessionsApi.getSessionExternalSessionsConfig(eventId, sessionId)
            .pipe(finalize(() => this._sessionsState.sessionExternalSessionsConfig.setInProgress(false)))
            .subscribe(externalSessionsConfig => this._sessionsState.sessionExternalSessionsConfig.setValue(externalSessionsConfig));
    }

    getSessionExternalSessionsConfig$(): Observable<SessionExternalSessionsConfig> {
        return this._sessionsState.sessionExternalSessionsConfig.getValue$();
    }

    clearSessionExternalSessionsConfig(): void {
        this._sessionsState.sessionExternalSessionsConfig.setValue(null);
    }

    isSessionExternalSessionsConfigLoading$(): Observable<boolean> {
        return this._sessionsState.sessionExternalSessionsConfig.isInProgress$();
    }

    saveSessionExternalSessionsConfig(eventId: number, sessionId: number, request: SessionExternalSessionsConfigRequest): Observable<void> {
        this._sessionsState.sessionExternalSessionsConfigSaving.setInProgress(true);
        return this._sessionsApi.putSessionExternalSessionsConfig(eventId, sessionId, request)
            .pipe(finalize(() => this._sessionsState.sessionExternalSessionsConfigSaving.setInProgress(false)));
    }

    getSessionsNames$(ids: number[], eventId: number): Observable<IdName[]> {
        return this._sessionsState.sessionsCache.getItems$(ids, id => (this._sessionsApi.getSession(eventId, id)) as Observable<IdName>);
    }

    //Export capacity
    exportCapacity(eventId: number, sessionId: number, data: ExportRequest): Observable<ExportResponse> {
        this._sessionsState.exportCapacity.setInProgress(true);
        return this._sessionsApi.exportCapacity(eventId, sessionId, data)
            .pipe(finalize(() => this._sessionsState.exportCapacity.setInProgress(false)));
    }

    // Start relocation
    startRelocation$(eventId: number, sessionId: number, data: PostRelocationSeats): Observable<void> {
        return this._sessionsApi.postRelocationSeats(eventId, sessionId, data);
    }

}

import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, getReleaseStatusIndicator, getSaleStatusIndicator, Session, SessionGenerationStatus,
    sessionGroupTypeFormats, SessionListFilters, SessionsFilterFields, SessionsListCountersService,
    SessionsTotalsCounter, SessionType, SessionWrapper, VmSessionsGroup
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { VenueAccessControlSystems, DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, signal, ViewChild, ViewChildren
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import moment from 'moment-timezone';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, pairwise, shareReplay, startWith, switchMap, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { CloneSessionDialogComponent } from '../clone/clone-session-dialog.component';
import { NewSessionDialogComponent } from '../create/new-session-dialog.component';
import { MultiSessionDeleteDialogComponent } from '../multi/delete/multi-session-delete-dialog.component';
import { SessionsListFilterComponent } from './filter/sessions-list-filter.component';

const CAPACITY_SEGMENT = 'capacity';
const CAPACITY_ACTIVITY_SEGMENT = 'capacity-activity';

@Component({
    selector: 'app-sessions-list',
    templateUrl: './sessions-list.component.html',
    styleUrls: ['./sessions-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionsListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _eventId: number;
    private _selectedSessions: Set<SessionWrapper> = new Set();
    private _previousSelectedSessions: SessionWrapper[]; // used with LastPathGuardListener
    private _pendingToSelectSessionId: number = null;
    private readonly MULTI_SESSION_SEGMENT = 'multi';
    private _sessionsGroupsPage = new BehaviorSubject<number>(0);
    private _deletedSession: Session;
    private _sessionsFilters: SessionListFilters;
    private _expandedGroupPanel: MatExpansionPanel;
    private _totalSessionsGenerationStatus = new BehaviorSubject<{
        inProgress: number;
        success: number;
        error: number;
        total: number;
    }>({ inProgress: 0, success: 0, error: 0, total: 0 });

    private get _innerPath(): string {
        let route = this._route.snapshot.children[0]?.children[0]?.children[0]?.routeConfig.path;
        if (this.selectedSessionWrappers[0]?.session.venue_template.type === VenueTemplateType.activity) {
            if (route === CAPACITY_SEGMENT) {
                route = CAPACITY_ACTIVITY_SEGMENT;
            }
        } else {
            if (route === CAPACITY_ACTIVITY_SEGMENT) {
                route = CAPACITY_SEGMENT;
            }
        }
        return route;
    }

    private get _isMulti(): boolean {
        return this._route.snapshot.children[0]?.routeConfig.path === this.MULTI_SESSION_SEGMENT;
    }

    @ViewChildren(CdkVirtualScrollViewport)
    private _virtualScrollVpQueryList: QueryList<CdkVirtualScrollViewport>;

    @ViewChild(SessionsListFilterComponent) listFilterComponent: SessionsListFilterComponent;
    readonly PAGE_SIZE = 5;
    totalSessions = 0;
    isEventFinalized$: Observable<boolean>;
    isAvet$: Observable<boolean>;
    readonly $isSga = signal(false);
    sessionsGroups$: Observable<VmSessionsGroup[]>;
    pagedSessionsGroups$: Observable<VmSessionsGroup[]>;
    filteredSessionsCounters$: Observable<SessionsTotalsCounter>;
    loading$: Observable<boolean>;
    dateTimeFormats = DateTimeFormats;
    sessionGenerationStatus = SessionGenerationStatus;
    getSaleStatusIndicator = getSaleStatusIndicator;
    getReleaseStatusIndicator = getReleaseStatusIndicator;
    sessionsGroupsPage$ = this._sessionsGroupsPage.asObservable();
    expandedGroup: VmSessionsGroup = null;
    totalSessionsGenerationStatus$ = this._totalSessionsGenerationStatus.asObservable();
    #hasFortressVenue: boolean;

    get selectedSessionWrappers(): SessionWrapper[] {
        return Array.from(this._selectedSessions);
    }

    constructor(
        private _changeDetector: ChangeDetectorRef,
        private _eventsSrv: EventsService,
        private _sessionsSrv: EventSessionsService,
        private _router: Router,
        private _route: ActivatedRoute,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _ephemeralMsg: EphemeralMessageService,
        private _ws: WebsocketsService,
        private _countersSrv: SessionsListCountersService
    ) { }

    trackByFn = (index: number, item: SessionWrapper): number => item.session.id;

    ngOnInit(): void {
        // load sessions total
        this._eventsSrv.event.get$()
            .pipe(first(event => !!event))
            .subscribe(event => {
                this._eventId = event.id;
                if (event.additional_config?.inventory_provider === ExternalInventoryProviders.sga && event.type === EventType.activity) {
                    this.$isSga.set(true);
                }
                this._sessionsSrv.sessionList.load(this._eventId, {
                    offset: 0,
                    limit: 0,
                    type: SessionType.session
                });
                this.#hasFortressVenue = event.venue_templates?.some(tpl =>
                    tpl.venue.access_control_systems?.some(system => system?.name === VenueAccessControlSystems.fortressBRISTOL)) || false;
            });
        this.isEventFinalized$ = this._eventsSrv.event.get$()
            .pipe(map(event => event?.status === EventStatus.finished || event?.status === EventStatus.cancelled));
        this.isAvet$ = this._eventsSrv.event.get$()
            .pipe(
                map(event => event?.type === EventType.avet),
                shareReplay(1)
            );
        // loading observable
        this.loading$ = booleanOrMerge([
            this._sessionsSrv.sessionList.inProgress$(),
            this._sessionsSrv.isSessionsGroupsLoading$(),
            this._sessionsSrv.isAllSessionsLoading$(),
            this._sessionsSrv.isAllSessionsReducedModelLoading$()
        ]);
        // obtenemos y mapeamos los grupos de sesiones
        this.sessionsGroups$ = this.getSessionsGroups$();
        this.pagedSessionsGroups$ = this.getPagedSessionsGroups$();
        this.filteredSessionsCounters$ = this._sessionsSrv.getSessionsGroups$()
            .pipe(
                map(sessionsGroups =>
                    sessionsGroups?.reduce<SessionsTotalsCounter>((acc, current, index) => {
                        const groupPage = Math.floor(index / this.PAGE_SIZE);
                        acc.page[groupPage] = {
                            offset: (acc.page[groupPage - 1]?.offset ?? 0) + (acc.page[groupPage - 1]?.total ?? 0),
                            total: (acc.page[groupPage]?.total ?? 0) + current.total
                        };
                        acc.total = acc.total + current.total;
                        return acc;
                    }, { total: 0, page: [] })
                ),
                shareReplay(1)
            );

        this.initFiltersAndUrlHandler(); // init handlers de cambio de filtros o de URL
        this.initSelectGroupHandler(); // init handlers relativos a la (re)carga de sessionsGroups
        this.initSelectSessionHandler(); // init handlers relativos a la (re)carga de sessionsList
        this.setWSMessageProcessor(); // añadimos el estado de generación de cada sesión y grupo en base a los mensages de WS

        this._sessionsSrv.getSelectedSessions$()
            .pipe(
                startWith([]),
                pairwise(),
                takeUntil(this._onDestroy)
            )
            .subscribe(([prevSelectedSessions, currentSelectedSessions]) => {
                // refresh de la vista centralizado para todas las casuisticas que modifican los selectedSessions:
                this._changeDetector.markForCheck();

                if (currentSelectedSessions.length >= 1) {
                    let sessionIdSegment: string;
                    if (currentSelectedSessions.length === 1) {
                        sessionIdSegment = currentSelectedSessions[0].session.id.toString();
                    } else {
                        sessionIdSegment = this.MULTI_SESSION_SEGMENT;
                    }
                    this._previousSelectedSessions = prevSelectedSessions;
                    this.navigateToSession(sessionIdSegment);
                } else {
                    this._router.navigate(['/events', this._eventId, 'sessions']);
                }
            });

        this._sessionsSrv.getRefreshSessionsList$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.refreshSessionsGroups());
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._sessionsSrv.clearAllSessions();
        this._sessionsSrv.setSelectedSessions([]);
        this._sessionsSrv.session.clear();
        this._sessionsSrv.resetSessionListFilters();
    }

    isPartiallySelectedGroup(sessionsGroup: VmSessionsGroup): boolean {
        return sessionsGroup.totalSessions > 0 &&
            sessionsGroup.selectedSessions > 0 &&
            sessionsGroup.selectedSessions < sessionsGroup.totalSessions;
    }

    isTotallySelectedGroup(sessionsGroup: VmSessionsGroup): boolean {
        return sessionsGroup.totalSessions > 0 &&
            sessionsGroup.selectedSessions === sessionsGroup.totalSessions;
    }

    updateSelectedGroup(checked: boolean, sessionsGroup: VmSessionsGroup): void {
        if (sessionsGroup === this.expandedGroup) {
            if (checked) {
                sessionsGroup.sessions.forEach(sessionWrapper => {
                    sessionWrapper.selected = true;
                    this._selectedSessions.add(sessionWrapper);
                });
                sessionsGroup.selectedSessions = sessionsGroup.totalSessions;
            } else {
                sessionsGroup.selectedSessions = 0;
                sessionsGroup.sessions.forEach(sessionWrapper => {
                    if (this._selectedSessions.size > 1) {
                        sessionWrapper.selected = false;
                        this._selectedSessions.delete(sessionWrapper);
                    } else {
                        sessionsGroup.selectedSessions = 1;
                    }
                });
            }
            this._sessionsSrv.setSelectedSessions(Array.from(this._selectedSessions));
        } else {
            this.loadAllGroupSessionsForSelection(sessionsGroup);
            this._sessionsSrv.getAllSessionsReducedModel$()
                .pipe(
                    first(Boolean),
                    map(data => data.data)
                )
                .subscribe(sessions => {
                    if (checked) {
                        sessions.forEach(session => {
                            let found = false;
                            for (const sessionWrapper of this._selectedSessions) {
                                if (sessionWrapper.session.id === session.id) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                const sessionWrapper = { session, selected: true };
                                this._selectedSessions.add(sessionWrapper);
                            }
                        });
                        sessionsGroup.selectedSessions = sessionsGroup.totalSessions;
                    } else {
                        sessionsGroup.selectedSessions = 0;
                        sessions.forEach(session => {
                            if (this._selectedSessions.size > 1) {
                                for (const sessionWrapper of this._selectedSessions) {
                                    if (sessionWrapper.session.id === session.id) {
                                        sessionWrapper.selected = false;
                                        this._selectedSessions.delete(sessionWrapper);
                                        break;
                                    }
                                }
                            } else {
                                sessionsGroup.selectedSessions = 1;
                            }
                        });
                    }
                    this._sessionsSrv.setSelectedSessions(Array.from(this._selectedSessions));
                    this._sessionsSrv.clearAllSessionsReducedModel();
                });
        }
    }

    isPartiallySelectedList(totalFilteredSessions: number): boolean {
        return this._selectedSessions.size > 0 && this._selectedSessions.size < totalFilteredSessions;
    }

    isTotallySelectedList(totalFilteredSessions: number): boolean {
        return this._selectedSessions.size > 0 && this._selectedSessions.size === totalFilteredSessions;
    }

    updateAllSelected(checked: boolean): void {
        if (checked) {
            this.loadAllGroupSessionsForSelection();
            this._sessionsSrv.getAllSessionsReducedModel$()
                .pipe(
                    first(Boolean),
                    map(data => data.data)
                )
                .subscribe(sessions => {
                    const selectedSWs: SessionWrapper[] = [];
                    sessions.forEach(session => {
                        let foundSW: SessionWrapper = null;
                        for (const sessionWrapper of this.expandedGroup.sessions) {
                            if (sessionWrapper.session.id === session.id) {
                                sessionWrapper.selected = true;
                                foundSW = sessionWrapper;
                                break;
                            }
                        }
                        if (!foundSW) {
                            foundSW = { session, selected: true };
                        }
                        selectedSWs.push(foundSW);
                    });
                    this._selectedSessions = new Set(selectedSWs);
                    this._countersSrv.updateGroupsSelectedSessionsCounters(this.sessionsGroups$, checked, this._changeDetector);
                    this._sessionsSrv.setSelectedSessions(selectedSWs);
                    this._sessionsSrv.clearAllSessionsReducedModel();
                });
        } else {
            for (const sessionWrapper of this._selectedSessions) {
                if (this._selectedSessions.size > 1) {
                    sessionWrapper.selected = false;
                    this._selectedSessions.delete(sessionWrapper);
                }
            }
            const selectedSessions = Array.from(this._selectedSessions);
            const groupKey = this.getSessionsGroupKeyFromDate(selectedSessions[0].session.start_date);
            this._countersSrv.updateGroupsSelectedSessionsCounters(this.sessionsGroups$, checked, this._changeDetector, groupKey);
            this._sessionsSrv.setSelectedSessions(selectedSessions);
        }
    }

    /**
     * triggered on checkbox click or from selectSession()
     */
    updateSelectedSession(checked: boolean, sessionWrapper: SessionWrapper): void {
        if (checked) {
            this._selectedSessions.add(sessionWrapper);
            sessionWrapper.selected = true;
        } else {
            this._selectedSessions.delete(sessionWrapper);
            sessionWrapper.selected = false;
        }
        const groupKey = this.getSessionsGroupKeyFromDate(sessionWrapper.session.start_date);
        this._countersSrv.updateIndividualSelectedSessionsCounter(this.sessionsGroups$, checked, this._changeDetector, groupKey);
        this._sessionsSrv.setSelectedSessions(Array.from(this._selectedSessions));
    }

    /**
     * triggered on session box click (outside of the checkbox) or from autoSelectSession()
     */
    selectSession(sessionWrapper: SessionWrapper, scrollToElem = false): void {
        if (this._selectedSessions.size !== 1 || !this.selectedSessionWrappers.find(sw => sw.session.id === sessionWrapper.session.id)) {
            this._selectedSessions.forEach(sw => sw.selected = false);
            this._selectedSessions.clear();
            this._countersSrv.updateGroupsSelectedSessionsCounters(this.sessionsGroups$, false, this._changeDetector);
            if (scrollToElem) {
                this.scrollToSession(sessionWrapper);
            }
            this.updateSelectedSession(true, sessionWrapper);
        }
    }

    openNewSessionDialog(): void {
        this._matDialog.open(NewSessionDialogComponent, new ObMatDialogConfig({
            eventId: this._eventId
        }))
            .beforeClosed()
            .subscribe((sessionIds: number[]) => {
                if (sessionIds && sessionIds.length > 0) {
                    this.totalSessions += sessionIds.length;
                    this._totalSessionsGenerationStatus.next({ inProgress: 0, success: 0, error: 0, total: sessionIds.length });
                    this.openSessionAndReloadList(sessionIds[0]);
                }
            });
    }

    openCloneSessionDialog(selectedSessionWrapper: SessionWrapper): void {
        this._matDialog.open(CloneSessionDialogComponent, new ObMatDialogConfig({
            fromSession: selectedSessionWrapper.session,
            hasFortressVenue: this.#hasFortressVenue
        }))
            .beforeClosed()
            .subscribe((sessionId: number) => {
                if (sessionId) {
                    this.totalSessions += 1;
                    this._totalSessionsGenerationStatus.next({ inProgress: 0, success: 0, error: 0, total: 1 });
                    this.openSessionAndReloadList(sessionId);
                }
            });
    }

    openDeleteSessionsDialog(): void {
        if (this._selectedSessions.size > 0) {
            const sessionsToDelete = this.selectedSessionWrappers;
            const sessionWrapper = sessionsToDelete[0];
            if (this._selectedSessions.size === 1) {
                const title = 'TITLES.DELETE_SESSION';
                const message = 'EVENTS.DELETE_SESSION_WARNING';
                const messageParams = { sessionName: sessionWrapper.session.name };
                this._msgDialogSrv
                    .showWarn({
                        size: DialogSize.SMALL,
                        title,
                        message,
                        messageParams,
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                    .subscribe(success => {
                        if (success) {
                            this._sessionsSrv.deleteSession(
                                sessionWrapper.session.event.id,
                                sessionWrapper.session.id
                            ).subscribe(() => this.deleteSessionsResult(sessionsToDelete));
                        }
                    });
            } else {
                this._matDialog.open(MultiSessionDeleteDialogComponent, new ObMatDialogConfig({
                    eventId: sessionWrapper.session.event.id,
                    sessions: sessionsToDelete
                }))
                    .beforeClosed()
                    .subscribe((isExecuted: boolean) => {
                        if (isExecuted) {
                            this.deleteSessionsResult(sessionsToDelete);
                        }
                    });
            }
        }
    }

    // click del usuario en el group
    updateExpandedGroup(sessionGroup: VmSessionsGroup, isBeingExpanded: boolean): void {
        if (isBeingExpanded && this.expandedGroup !== sessionGroup) {
            this.expandedGroup = sessionGroup;
            this.loadExpandedGroupSessions();
        }
    }

    undoLastNavigation(): void {
        const selectedSessionsIds = this._selectedSessions && Array.from(this._selectedSessions.values()).map(s => s.session.id);
        const pathSessionId = Number(this._route.snapshot.firstChild.params?.['sessionId']);

        if (
            (selectedSessionsIds?.length !== 1 || !pathSessionId || pathSessionId !== selectedSessionsIds[0])
            && (selectedSessionsIds?.length === 1 || pathSessionId)
        ) {
            this._selectedSessions.forEach(sw => sw.selected = false);
            this._selectedSessions.clear();
            this._previousSelectedSessions.forEach(sessionWrapper => {
                this._selectedSessions.add(sessionWrapper);
                sessionWrapper.selected = true;
            });
            this._countersSrv.updateGroupsSelectedSessionsCountersFromList(
                this.sessionsGroups$, this._previousSelectedSessions, this.getSessionsGroupKeyFromDate.bind(this), this._changeDetector
            );
            this._sessionsSrv.setSelectedSessions(Array.from(this._selectedSessions));
        }
    }

    pageFilter(pageOptions: PageEvent): void {
        this._sessionsGroupsPage.next(pageOptions.pageIndex);
    }

    getPaginatorStartItem(page: number, filteredSessionsCounters: SessionsTotalsCounter): number {
        return filteredSessionsCounters.page[page].offset + 1;
    }

    getPaginatorEndItem(page: number, filteredSessionsCounters: SessionsTotalsCounter): number {
        return filteredSessionsCounters.page[page].offset + filteredSessionsCounters.page[page].total;
    }

    // click en el botón de centrar en la sesión seleccionada
    scrollToSelectedSession(sessionsGroups: VmSessionsGroup[]): void {
        if (this._selectedSessions.size === 1) {
            const selectedSession = this.selectedSessionWrappers[0];
            if (this.isDateContainedInSessionsGroup(this.expandedGroup, moment(selectedSession.session.start_date))) {
                let groupToSelectIndex = sessionsGroups
                    .findIndex(sessionsGroup => sessionsGroup.startDate === this.expandedGroup.startDate);
                if (groupToSelectIndex < 0) {
                    groupToSelectIndex = sessionsGroups.length - 1;
                }
                const selectedSessionPage = Math.floor(groupToSelectIndex / this.PAGE_SIZE);
                if (this._sessionsGroupsPage.value === selectedSessionPage) {
                    if (!this._expandedGroupPanel.expanded) {
                        // si tenemos el grupo seleccionado y visible pero cerrado, lo abrimos y dejamos el resto para el (opened) handler
                        this._expandedGroupPanel.open();
                    } else {
                        // si ya tenemos el grupo que contiene la sesión abierto y visible hacemos scroll sin más
                        this.scrollToSession(selectedSession);
                    }
                } else {
                    // si tenemos el grupo que contiene la sesión abierto pero estamos en una página distinta,
                    // volvemos a la página en la que está el grupo abierto y dejamos el resto para el (opened) handler
                    this._sessionsGroupsPage.next(selectedSessionPage);
                }
            } else {
                // si el grupo al que pertenece la sesión seleccionada está cerrado, lo abrimos y cargamos sus sesiones;
                // una vez cargadas el subscribe se encarga del scroll hacia la seleccionada
                this.selectExpandedGroupAndLoadSessions(sessionsGroups, selectedSession.session);
            }
        }
    }

    // callback que se ejecuta cada vez que se abre un expansion panel
    openedPanelHandler(expandedPanel: MatExpansionPanel, expandedPanelIndex: number): void {
        this._expandedGroupPanel = expandedPanel;
        if (this._selectedSessions.size === 1) {
            const selectedSession = this.selectedSessionWrappers[0];
            if (this.isDateContainedInSessionsGroup(this.expandedGroup, moment(selectedSession.session.start_date))) {
                this._changeDetector.detectChanges();
                if (!this._virtualScrollVpQueryList.get(expandedPanelIndex).getViewportSize()) {
                    this._virtualScrollVpQueryList.forEach(vsv => vsv.checkViewportSize());
                }
                // este scroll cubre los casos en que ya teníamos el grupo seleccionado pero no visible
                // -> cambio de página (manual o por botón de centrar en la sesión seleccionada) o grupo cerrado
                this.scrollToSession(selectedSession);
            }
        }
    }

    private openSessionAndReloadList(sessionId: number): void {
        this._pendingToSelectSessionId = sessionId;
        this.refreshSessionsGroups();
    }

    private deleteSessionsResult(deletedSessions: SessionWrapper[]): void {
        this._deletedSession = deletedSessions[0].session;
        this.totalSessions -= deletedSessions.length;
        this._selectedSessions.clear();
        this._sessionsSrv.session.clear();
        this._countersSrv.updateGroupsSelectedSessionsCounters(this.sessionsGroups$, false, this._changeDetector);
        this._sessionsSrv.setSelectedSessions(Array.from(this._selectedSessions));

        if (deletedSessions.length === 1) {
            this._ephemeralMsg.showSuccess({
                msgKey: 'EVENTS.DELETE_SESSION_SUCCESS',
                msgParams: { sessionName: deletedSessions[0].session.name }
            });
        } else {
            this._ephemeralMsg.showSuccess({ msgKey: 'EVENTS.DELETE_SESSIONS_SUCCESS' });
        }
    }

    private refreshSessionsGroups(): void {
        this._sessionsSrv.loadSessionsGroups(
            this._eventId,
            Object.assign({
                sort: `${SessionsFilterFields.startDate}:asc`,
                type: SessionType.session
            }, this._sessionsFilters)
        );
    }

    private getSessionsGroups$(): Observable<VmSessionsGroup[]> {
        return this._sessionsSrv.getSessionsGroups$()
            .pipe(
                filter(sessionsGroups => !!sessionsGroups),
                filter(() => !!this._sessionsFilters),
                map(sessionsGroups => {
                    const format = sessionGroupTypeFormats[this._sessionsFilters.groupType].token;
                    const isRange = sessionGroupTypeFormats[this._sessionsFilters.groupType].isRange;
                    // mapeamos las agrupaciones
                    const result: VmSessionsGroup[] = sessionsGroups.map(sessionsGroup => {
                        let title = moment.utc(sessionsGroup.start_date).format(format);
                        if (isRange) {
                            title += ' - ' + moment.utc(sessionsGroup.end_date).format(format);
                        }
                        return {
                            startDate: sessionsGroup.start_date,
                            endDate: sessionsGroup.end_date,
                            title,
                            totalSessions: sessionsGroup.total,
                            selectedSessions: 0,
                            sessions: []
                        };
                    });
                    // por si hubiera algun wsMsg que llega antes de que la recarga de grupos sea efectiva:
                    result.forEach(sessionsGroup => {
                        this._countersSrv.processGroupStatusCounters(sessionsGroup, sessionsGroup.startDate);
                    });
                    this._changeDetector.markForCheck();

                    return result;
                }),
                startWith(null as VmSessionsGroup[]),
                shareReplay(1)
            );
    }

    private getPagedSessionsGroups$(): Observable<VmSessionsGroup[]> {
        return combineLatest([
            this.sessionsGroups$,
            this._sessionsGroupsPage
        ])
            .pipe(
                filter(([sessionsGroups, _]) => !!sessionsGroups),
                map(([sessionsGroups, page]) => sessionsGroups.slice(page * this.PAGE_SIZE, (page + 1) * this.PAGE_SIZE)),
                shareReplay(1)
            );
    }

    private initFiltersAndUrlHandler(): void {
        // sync session list filters
        const filters$ = this._sessionsSrv.sessionList.get$()
            .pipe(
                first(md => !!md),
                map(md => md.metadata),
                tap(md => this.totalSessions = md.total),
                switchMap(() => this._sessionsSrv.getSessionListFilters$()),
                tap(filters => this._sessionsFilters = filters)
            );
        // map URL changes
        const currentUrl$ = this._router.events
            .pipe(
                filter(event => event instanceof NavigationEnd),
                map((event: NavigationEnd) => event.url),
                startWith(this._router.url)
            );
        // cuando cambian los filtros del listado o la URL hay que poner al día lo que haga falta
        combineLatest([filters$, currentUrl$])
            .pipe(
                map(([_, currentUrl]) => currentUrl),
                startWith(null as string),
                pairwise(),
                withLatestFrom(this.sessionsGroups$, this._sessionsSrv.session.get$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([[prevUrl, currentUrl], sessionsGroups, session]) => {
                const currentUrlSegments = currentUrl.split('?')[0].split('/');
                // init load | cambio de filtros | acceso a URL '/sessions'
                if (!sessionsGroups || prevUrl === currentUrl || currentUrlSegments.length === 4) {
                    this._selectedSessions.clear();
                    this.refreshSessionsGroups();
                } else { // ha cambiado la URL a algo distinto de '/sessions'
                    const isLoadedSessionNotTheOnlySelected = this.selectedSessionWrappers.length > 1 ||
                        this.selectedSessionWrappers[0]?.session.id !== session?.id;
                    const isOutdatedSelection = !this._isMulti && isLoadedSessionNotTheOnlySelected;
                    if (isOutdatedSelection) {
                        if (this.isDateContainedInSessionsGroup(this.expandedGroup, moment(session?.start_date))) {
                            const sessionToSelect = this.expandedGroup.sessions.find(sw => sw.session.id === session.id);
                            this.selectSession(sessionToSelect, true);
                        } else {
                            // la unica manera de tener cargada una sesión que no está en el grupo expandido es mediante
                            // navegación del browser (backward/forward buttons) o cuando se crea una sesión y accede automáticamente
                            this._selectedSessions.forEach(sw => sw.selected = false);
                            this._selectedSessions.clear();
                            this.selectExpandedGroupAndLoadSessions(sessionsGroups, session);
                        }
                    }
                }
            });
    }

    private initSelectGroupHandler(): void {
        this.sessionsGroups$
            .pipe(
                filter(sessionsGroups => !!sessionsGroups && sessionsGroups.length > 0),
                withLatestFrom(this._sessionsSrv.session.get$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([sessionsGroups, session]) => {
                if (this._pendingToSelectSessionId) { // navegamos en base a un id obtenido por la creación/clonado de una sesión
                    this.expandedGroup = null;
                    this.navigateToSession(String(this._pendingToSelectSessionId));
                    this._pendingToSelectSessionId = null;
                } else {
                    if (this._selectedSessions.size) { // si ya teniamos sesiones seleccionadas
                        let findPredicate: (sessionsGroup: VmSessionsGroup) => boolean;
                        if (this._selectedSessions.size > 1) { // si tenemos varias mantenemos el expandedGroup que ya había
                            findPredicate = (sessionsGroup: VmSessionsGroup): boolean =>
                                sessionsGroup.startDate === this.expandedGroup.startDate;
                        } else { // si tenemos solo 1 expandimos el grupo al que pertenece
                            findPredicate = (sessionsGroup: VmSessionsGroup): boolean =>
                                this.isDateContainedInSessionsGroup(sessionsGroup, moment(session.start_date));
                        }
                        this.setExpandedGroup(sessionsGroups, findPredicate);
                        this.loadExpandedGroupSessions();
                    } else {
                        // sinó tratamos de seleccionar el grupo de la sesión cargada en el detalle o el de la sesión borrada si es el caso
                        this.selectExpandedGroupAndLoadSessions(sessionsGroups, session || this._deletedSession);
                    }
                }
            });
    }

    private isDateContainedInSessionsGroup(sessionsGroup: VmSessionsGroup, date: moment.Moment): boolean {
        // Convert the received date parameter to UTC to avoid inconsistencies due to time zones.
        // This is important because we want a session to be grouped in the correct month according to its actual start date,
        // regardless of the user's or server's time zone.
        // This ensures that the session is always displayed in the correct date group.
        const { years, months, date: days, hours, minutes, seconds, milliseconds } = date.parseZone().toObject();
        const startDate = moment.utc({ years, months, date: days, hours, minutes, seconds, milliseconds });
        return sessionsGroup && startDate.isBetween(sessionsGroup.startDate, sessionsGroup.endDate, undefined, '[]');
    }

    private selectExpandedGroupAndLoadSessions(sessionsGroups: VmSessionsGroup[], sessionOrStartDate: Session | moment.Moment): void {
        // if sessionOrStartDate is undefined, moment() is "now":
        const startDate = moment.isMoment(sessionOrStartDate) ?
            sessionOrStartDate : moment(sessionOrStartDate?.start_date);
        // si no existe ningún grupo que contenga startDate (isBetween) expandimos el último de los grupos, sinó el listado
        // cargaría todas las sesiones de todos los grupos en bucle para buscar cual es la primera sesión "futura"
        const findPredicate = (sessionsGroup: VmSessionsGroup): boolean =>
            startDate.isBefore(sessionsGroup.startDate) || this.isDateContainedInSessionsGroup(sessionsGroup, startDate);
        this.setExpandedGroup(sessionsGroups, findPredicate);
        this.loadExpandedGroupSessions();
    }

    private setExpandedGroup(sessionsGroups: VmSessionsGroup[], findPredicate: (sessionsGroup: VmSessionsGroup) => boolean): void {
        let groupToSelectIndex = sessionsGroups.findIndex(findPredicate);
        if (groupToSelectIndex < 0) {
            groupToSelectIndex = sessionsGroups.length - 1;
        }
        this.expandedGroup = sessionsGroups[groupToSelectIndex];
        this._sessionsGroupsPage.next(Math.floor(groupToSelectIndex / this.PAGE_SIZE));
    }

    private initSelectSessionHandler(): void {
        this._sessionsSrv.getAllSessionsData$()
            .pipe(
                filter(sessions => sessions?.length &&
                    this.isDateContainedInSessionsGroup(this.expandedGroup, moment(sessions[0].start_date))),
                withLatestFrom(this._sessionsSrv.session.get$(), this.sessionsGroups$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([sessionsList, loadedSession, sessionsGroups]) => {
                // cada vez que refrescamos las sesiones de un grupo forzamos re-size de todos los virtual-scroll container
                this._virtualScrollVpQueryList.forEach(vsv => vsv.checkViewportSize());

                const selectedSW = this.selectedSessionWrappers;
                // acceso directo a URL /{sessionId} | recargas posteriores del listado (new/clone) | carga de otro grupo via click
                if (loadedSession) {
                    // marcamos las sesiones que tuvieramos selecionadas en ese grupo y buscamos
                    // la loadedSession dentro del grupo para seleccionarla
                    const {
                        wrappedSessions, sessionToSelect
                    } = getWrappedSessionsAndSessionToSelect(sessionsList, selectedSW, loadedSession);
                    this.expandedGroup.sessions = wrappedSessions;
                    this._selectedSessions = new Set(selectedSW);

                    if (sessionToSelect) {
                        if (!selectedSW.length) {
                            // si tenemos sesión cargada y ninguna selecionada -> acceso directo a [ /{sessionId} ]
                            this.selectSession(sessionToSelect, true);
                        } else {
                            // si hemos abierto el grupo donde tenemos nuestra sesión seleccionada,
                            // scrollamos hacia ella
                            this.scrollToSession(sessionToSelect);
                        }
                    }
                } else { // acceso directo a URL [ /sessions | /multi ] o autoselect por sesión borrada
                    //buscamos la primera sesión posterior al current datetime
                    const result = getWrappedSessionsAndSessionToSelect(sessionsList, selectedSW, moment(this._deletedSession?.start_date));
                    const wrappedSessions = result.wrappedSessions;
                    let sessionToSelect = result.sessionToSelect;
                    if (!selectedSW.length) {
                        if (!sessionToSelect) { // si no está localizamos el group consecutivo
                            const currentGroupIndex = sessionsGroups.findIndex(group => group.startDate === this.expandedGroup.startDate);
                            const nextGroup = currentGroupIndex >= 0 && (currentGroupIndex + 1) < sessionsGroups.length ?
                                sessionsGroups[currentGroupIndex + 1] : null;
                            if (nextGroup) { // hay que expandir el group consecutivo y cargar sus sessions
                                this.selectExpandedGroupAndLoadSessions(sessionsGroups, moment(nextGroup.startDate));
                            } else { // último group -> nos quedamos con la última sesión
                                sessionToSelect = wrappedSessions[wrappedSessions.length - 1];
                            }
                        }
                        // si sessionToSelect está en el listado lo asignamos al expandedGroup y seleccionamos esa sesión
                        if (sessionToSelect) {
                            this.expandedGroup.sessions = wrappedSessions;
                            this._selectedSessions = new Set(selectedSW);
                            this.selectSession(sessionToSelect, true);
                            this._deletedSession = null;
                        }
                    } else {  // refresco de sessionsGroup al guardar cambios en /multi
                        this.expandedGroup.sessions = result.wrappedSessions;
                        this._selectedSessions = new Set(selectedSW);
                    }
                }
            });

        function getWrappedSessionsAndSessionToSelect(
            sessionsList: Session[], selectedSW: SessionWrapper[], sessionOrStartDate: Session | moment.Moment
        ): {
            wrappedSessions: SessionWrapper[];
            sessionToSelect: SessionWrapper;
        } {
            let sessionToSelect: SessionWrapper = null;
            const loadedSession = !moment.isMoment(sessionOrStartDate) && sessionOrStartDate;
            const condWithSession = (session: Session): boolean =>
                !sessionToSelect && session.id === loadedSession.id;
            const startDate = moment.isMoment(sessionOrStartDate) && sessionOrStartDate;
            const condWithoutSession = (session: Session): boolean =>
                !sessionToSelect && startDate.isBefore(session.start_date);
            const selectionCondition = loadedSession ? condWithSession : condWithoutSession;
            const wrappedSessions = sessionsList
                .map(session => {
                    const foundSessionIndex = selectedSW.findIndex(sw => sw.session.id === session.id);
                    const wrappedSession: SessionWrapper = {
                        session,
                        selected: foundSessionIndex >= 0
                    };
                    if (foundSessionIndex >= 0) {
                        // si alguna sesión de este grupo estaba selecionada, sustituimos
                        // la referencia que había en el Set de seleccionadas por la nueva
                        selectedSW[foundSessionIndex] = wrappedSession;
                    }
                    if (selectionCondition(session)) {
                        sessionToSelect = wrappedSession;
                    }
                    return wrappedSession;
                })
                .sort((a: SessionWrapper, b: SessionWrapper) => {
                    const hasRelatedSessionA = !!a.session.settings?.smart_booking?.related_id;
                    const hasRelatedSessionB = !!b.session.settings?.smart_booking?.related_id;

                    if (!hasRelatedSessionA && hasRelatedSessionB) {
                        return 1;
                    }

                    if (hasRelatedSessionA && hasRelatedSessionB) {
                        if (a.session.id < b.session.id) return -1;
                        else return 0;
                    }

                    return 0;
                });

            return {
                wrappedSessions,
                sessionToSelect
            };
        }
    }

    private setWSMessageProcessor(): void {
        this._ws.getMessages$<WsSessionMsg>(Topic.event, this._eventId)
            .pipe(
                filter(wsMsg => wsMsg?.type === WsEventMsgType.session),
                withLatestFrom(this.sessionsGroups$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([wsMsg, sessionsGroups]) => {
                const groupKey = this.getSessionsGroupKeyFromDate(wsMsg.data.startDate);
                this._countersSrv.generateGroupedStatusCounters(groupKey, wsMsg.data.id, wsMsg.status);

                sessionsGroups && sessionsGroups.forEach(sessionsGroup => {
                    this._countersSrv.processGroupStatusCounters(sessionsGroup, sessionsGroup.startDate);
                });

                if (groupKey === this.expandedGroup?.startDate) {
                    // para curarnos en salud, cada vez que recibimos un wsMsg de una sesión que pertenece
                    // al grupo que tenemos abierto re-procesamos el status de todas sus sesiones
                    this._countersSrv.processGroupSessionsStatusCounters(this.expandedGroup);
                }

                const totalCounters = this._countersSrv.getTotalCounters(this._totalSessionsGenerationStatus.value.total);
                this._totalSessionsGenerationStatus.next(totalCounters);

                this._changeDetector.markForCheck();
            });

        this._totalSessionsGenerationStatus
            .pipe(takeUntil(this._onDestroy))
            .subscribe(totals => {
                if (totals.inProgress === 0 && totals.total > 0 && (totals.success + totals.error === totals.total)) {
                    if (totals.success + totals.error === 1) { // alta simple
                        if (totals.error > 0) {
                            this._ephemeralMsg.show({
                                type: MessageType.warn,
                                msgKey: 'EVENTS.ADD_SESSION_ERROR'
                            });
                        } else {
                            this._ephemeralMsg.showSuccess({
                                msgKey: 'EVENTS.ADD_SESSION_SUCCESS'
                            });
                        }
                    } else { // alta multiple
                        if (totals.success > 0 && totals.error === 0) {
                            this._ephemeralMsg.showSuccess({ msgKey: 'EVENTS.ADD_SESSIONS_SUCCESS' });
                        } else if (totals.error > 0 && totals.success === 0) {
                            this._ephemeralMsg.show({
                                type: MessageType.warn,
                                msgKey: 'EVENTS.ADD_SESSIONS_ERROR'
                            });
                        } else {
                            this._ephemeralMsg.show({
                                type: MessageType.alert,
                                msgKey: 'EVENTS.ADD_SESSIONS_PARTIAL_ERROR'
                            });
                        }
                    }
                }
            });
    }

    private loadExpandedGroupSessions(): void {
        // there an edge case, maybe more than one, which there are no expandedGroups
        if (this.expandedGroup) {

            const groupStart = this.expandedGroup.startDate;
            const groupEnd = this.expandedGroup.endDate;

            const { initStartDate: filterStart, finalStartDate: filterEnd } = this._sessionsFilters;

            const groupFilterStart = filterStart && filterStart > groupStart ? filterStart : groupStart;
            const groupFilterEnd = filterEnd && filterEnd < groupEnd ? filterEnd : groupEnd;

            this._sessionsSrv.loadAllSessions(
                this._eventId,
                {
                    ...this._sessionsFilters,
                    sort: `${SessionsFilterFields.startDate}:asc`,
                    type: SessionType.session,
                    initStartDate: groupFilterStart,
                    finalStartDate: groupFilterEnd,
                    fields: [
                        SessionsFilterFields.name,
                        SessionsFilterFields.status,
                        SessionsFilterFields.type,
                        SessionsFilterFields.startDate,
                        SessionsFilterFields.entityId,
                        SessionsFilterFields.capacity,
                        SessionsFilterFields.archived,
                        SessionsFilterFields.statusFlags,
                        SessionsFilterFields.generationStatus,
                        SessionsFilterFields.publicationCancelledReason,
                        SessionsFilterFields.releaseEnabled,
                        SessionsFilterFields.venueTemplateId,
                        SessionsFilterFields.venueTemplateName,
                        SessionsFilterFields.venueTplVenueId,
                        SessionsFilterFields.venueTplVenueName,
                        SessionsFilterFields.venueTplType,
                        SessionsFilterFields.settingsSmartbookingStatus,
                        SessionsFilterFields.settingsSmartbookingRelatedSession
                    ]
                }
            );
        }
    }

    private loadAllGroupSessionsForSelection(sessionsGroup: VmSessionsGroup = null): void {
        this._sessionsSrv.loadAllSessionsReducedModel(
            this._eventId,
            {
                sort: `${SessionsFilterFields.startDate}:asc`,
                type: SessionType.session,
                initStartDate: sessionsGroup?.startDate,
                finalStartDate: sessionsGroup?.endDate,
                fields: [
                    SessionsFilterFields.name,
                    SessionsFilterFields.startDate,
                    SessionsFilterFields.venueTemplateId,
                    SessionsFilterFields.venueTemplateName,
                    SessionsFilterFields.venueTplVenueId,
                    SessionsFilterFields.venueTplType,
                    SessionsFilterFields.archived
                ],
                ...this._sessionsFilters
            }
        );
    }

    private getSessionsGroupKeyFromDate(startDate: string): string {
        // Given a start date, obtain the corresponding group key by converting the date to UTC
        // and extracting only the year and month. This ensures that the session is grouped correctly,
        // regardless of the time zone.
        const { years, months } = moment(startDate).parseZone().toObject();
        return moment.utc({ years, months }).startOf(this._sessionsFilters?.groupType?.toLowerCase() as moment.unitOfTime.StartOf).format();
    }

    private navigateToSession(sessionId: string): void {
        const path = this.appendCurrentSubpath(sessionId);
        this._router.navigate([path], { relativeTo: this._route });
    }

    private scrollToSession(sessionWrapper: SessionWrapper): void {
        this._virtualScrollVpQueryList.forEach(vsv => {
            if (vsv.getViewportSize()) {
                const sessionIndex = this.expandedGroup.sessions.findIndex(sw => sw === sessionWrapper);
                setTimeout(() => vsv.scrollToIndex(sessionIndex, 'smooth'));
            }
        });
    }

    private appendCurrentSubpath(sessionIdSegment: string): string {
        if (
            (this._isMulti && sessionIdSegment !== this.MULTI_SESSION_SEGMENT) ||
            (!this._isMulti && sessionIdSegment === this.MULTI_SESSION_SEGMENT)
        ) {
            return sessionIdSegment;
        } else {
            return this._innerPath ? sessionIdSegment + '/' + this._innerPath : sessionIdSegment;
        }
    }
}

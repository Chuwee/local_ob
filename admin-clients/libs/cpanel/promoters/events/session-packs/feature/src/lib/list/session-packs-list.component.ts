import { getSaleStatusIndicator } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventSessionPackConf, EventsService, SessionsGenerationStatusCounters } from '@admin-clients/cpanel/promoters/events/data-access';
import { SessionPacksLoadCase } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import {
    EventSessionsService, getReleaseStatusIndicator, Session, SessionGenerationStatus, SessionWrapper
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsg, WsMsgStatus, WsSessionData } from '@admin-clients/shared/core/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, combineLatest, firstValueFrom, mapTo, Observable, of, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { CreateSessionPackDialogComponent } from '../create/create-session-pack-dialog.component';
import {
    DeleteSessionPackDialogComponent, DeleteSessionPackDialogData
} from '../session-pack/delete-session-pack-dialog/delete-session-pack-dialog.component';
import { SessionPacksStateMachine } from '../session-packs-state-machine';

@Component({
    selector: 'app-session-packs-list',
    templateUrl: './session-packs-list.component.html',
    styleUrls: ['./session-packs-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionPacksListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _eventId: number;
    private _generationStatusCounters: SessionsGenerationStatusCounters = {
        inProgress: [],
        success: [],
        error: []
    };

    private _totalSessionsGenerationStatus = new BehaviorSubject<{
        inProgress: number;
        success: number;
        error: number;
        total: number;
    }>({ inProgress: 0, success: 0, error: 0, total: 0 });

    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0].params['sessionId'];
    }

    private get _innerPath(): string {
        return this._route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    readonly dateTimeFormats = DateTimeFormats;
    isLoadingList$: Observable<boolean>;
    totalSessions$: Observable<number>;
    sessionsList$: Observable<SessionWrapper[]>;
    currentSession$: Observable<Session>;
    sessionGenerationStatus = SessionGenerationStatus;
    getSaleStatusIndicator = getSaleStatusIndicator;
    getReleaseStatusIndicator = getReleaseStatusIndicator;

    constructor(
        private _eventsSrv: EventsService,
        private _sessionsSrv: EventSessionsService,
        private _route: ActivatedRoute,
        private _ephemeralMsgSrv: EphemeralMessageService,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _sessionPacksSM: SessionPacksStateMachine,
        private _ws: WebsocketsService,
        private _ref: ChangeDetectorRef
    ) {
    }

    ngOnInit(): void {
        this.model();
        // añadimos el estado de generación de cada sesión y grupo en base a los mensages de WS
        this.setWSMessageProcessor();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._ws.unsubscribeMessages(Topic.event, this._eventId);
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._sessionsSrv.clearAllSessions();
        this._sessionsSrv.setSelectedSessions([]);
        this._sessionsSrv.session.clear();
        this._sessionsSrv.resetSessionListFilters();
        this._sessionsSrv.clearSessionsState();
        this._sessionPacksSM.setCurrentState({ state: SessionPacksLoadCase.none });
    }

    async openNewSessionPackDialog(): Promise<void> {
        const lastSession = await firstValueFrom(this.currentSession$);
        this._matDialog.open(CreateSessionPackDialogComponent, new ObMatDialogConfig({
            eventId: this._eventId,
            lastSessionId: lastSession?.id
        }))
            .beforeClosed()
            .subscribe((sessionPackId: number) => {
                if (sessionPackId) {
                    this._totalSessionsGenerationStatus.next({ inProgress: 0, success: 0, error: 0, total: 1 });
                    this._generationStatusCounters = {
                        inProgress: [],
                        success: [],
                        error: []
                    };
                    this._sessionPacksSM.setCurrentState({
                        state: SessionPacksLoadCase.loadSessionPack,
                        idPath: sessionPackId.toString()
                    });
                }
            });
    }

    openDeleteSessionPackDialog(): void {
        combineLatest([
            this._eventsSrv.event.get$(),
            this.currentSession$
        ])
            .pipe(
                first(),
                switchMap(([event, session]) => {
                    let resultObs: Observable<boolean>;
                    if (event.settings.session_pack === EventSessionPackConf.restricted) {
                        resultObs = this._matDialog.open<DeleteSessionPackDialogComponent, DeleteSessionPackDialogData, boolean>(
                            DeleteSessionPackDialogComponent, new ObMatDialogConfig({ sessionPack: session })
                        )
                            .beforeClosed();
                    } else {
                        resultObs = this._msgDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'TITLES.DELETE_SESSION_PACK',
                            message: 'EVENTS.DELETE_SESSION_PACK_WARNING',
                            messageParams: { sessionName: session.name },
                            actionLabel: 'FORMS.ACTIONS.DELETE',
                            showCancelButton: true
                        })
                            .pipe(
                                switchMap(proceed => {
                                    if (proceed) {
                                        return this._sessionsSrv.deleteSession(session.event.id, session.id)
                                            .pipe(mapTo(true));
                                    } else {
                                        return of(false);
                                    }
                                })
                            );
                    }
                    return resultObs.pipe(map(success => ({ session, success })));
                })
            )
            .subscribe(result => {
                if (result.success) {
                    this._ephemeralMsgSrv.showSuccess({
                        msgKey: 'EVENTS.DELETE_SESSION_PACK_SUCCESS',
                        msgParams: { sessionName: result.session.name }
                    });
                    this._sessionPacksSM.setCurrentState({
                        state: SessionPacksLoadCase.loadSessionPack
                    });
                    this._sessionPacksSM.loadSessionPacksList();
                }
            });
    }

    selectionChangeHandler(selectedSessionId: string): void {
        this.currentSession$
            .pipe(first())
            .subscribe(currentSession => {
                if (!!selectedSessionId && currentSession.id !== Number(selectedSessionId)) {
                    this._sessionPacksSM.setCurrentState({
                        state: SessionPacksLoadCase.selectedSessionPack,
                        idPath: selectedSessionId,
                        innerPath: this._innerPath
                    });
                }
            });
    }

    private model(): void {
        this.isLoadingList$ = this._sessionsSrv.isAllSessionsLoading$()
            .pipe(
                distinctUntilChanged(),
                shareReplay(1)
            );
        this.totalSessions$ = this._sessionsSrv.getAllSessions$().pipe(map(sl => sl?.metadata?.total || 0));
        this.sessionsList$ = this._sessionsSrv.getAllSessionsData$()
            .pipe(
                filter(sessions => !!sessions),
                map(sessions =>
                    sessions.map(session => ({ session } as SessionWrapper))
                )
            );
        this.currentSession$ = this._sessionsSrv.session.get$()
            .pipe(
                shareReplay({ bufferSize: 1, refCount: true })
            );
    }

    private loadDataHandler(): void {
        this._sessionPacksSM.getListDetailState$()
            .pipe(
                tap(state => {
                    if (state === SessionPacksLoadCase.none) {
                        this._sessionPacksSM.setCurrentState({
                            state: SessionPacksLoadCase.loadSessionPack,
                            idPath: this._idPath,
                            innerPath: this._innerPath
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();

        this.currentSession$
            .pipe(
                withLatestFrom(this._sessionPacksSM.getListDetailState$()),
                tap(([session, state]) => {
                    if (state === SessionPacksLoadCase.loadSessionPack && session) {
                        this.scrollToSelectedSessionPack(session.id);
                    } else if (state === SessionPacksLoadCase.none) {
                        this._sessionPacksSM.setCurrentState({
                            state: SessionPacksLoadCase.loadSessionPack,
                            idPath: this._route.snapshot.children[0].params['sessionId']
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }

    private setWSMessageProcessor(): void {
        this._eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                tap(event => this._eventId = event.id),
                switchMap(event => this._ws.getMessages$<WsMsg<WsEventMsgType, WsSessionData>>(Topic.event, event.id)),
                filter(wsMsg => wsMsg?.type === WsEventMsgType.session),
                withLatestFrom(this.sessionsList$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([wsMsg, sessionsList]) => {
                this.generateStatusCounters(wsMsg.data.id, wsMsg.status);
                this.processSessionsStatusCounters(sessionsList);

                const totalCounters = {
                    inProgress: this._generationStatusCounters.inProgress.length,
                    success: this._generationStatusCounters.success.length,
                    error: this._generationStatusCounters.error.length,
                    total: this._totalSessionsGenerationStatus.value.total
                };
                this._totalSessionsGenerationStatus.next(totalCounters);

                this._ref.markForCheck();
            });

        this._totalSessionsGenerationStatus
            .pipe(takeUntil(this._onDestroy))
            .subscribe(totals => {
                if (totals.inProgress === 0 && totals.total > 0 && (totals.success + totals.error === totals.total)) {
                    if (totals.success + totals.error === 1) { // alta simple
                        if (totals.error > 0) {
                            this._ephemeralMsgSrv.show({
                                type: MessageType.warn,
                                msgKey: 'EVENTS.ADD_SESSION_PACK_ERROR'
                            });
                        } else {
                            this._ephemeralMsgSrv.showSuccess({
                                msgKey: 'EVENTS.ADD_SESSION_PACK_SUCCESS'
                            });
                        }
                    } else { // alta multiple (no implementado para abonos por ahora, pero ahi lo dejo)
                        if (totals.success > 0 && totals.error === 0) {
                            this._ephemeralMsgSrv.showSuccess({ msgKey: 'EVENTS.ADD_SESSION_PACKS_SUCCESS' });
                        } else if (totals.error > 0 && totals.success === 0) {
                            this._ephemeralMsgSrv.show({
                                type: MessageType.warn,
                                msgKey: 'EVENTS.ADD_SESSION_PACKS_ERROR'
                            });
                        } else {
                            this._ephemeralMsgSrv.show({
                                type: MessageType.alert,
                                msgKey: 'EVENTS.ADD_SESSION_PACKS_PARTIAL_ERROR'
                            });
                        }
                    }
                }
            });
    }

    private generateStatusCounters(sessionId: number, wsMsgStatus: WsMsgStatus): void {
        switch (wsMsgStatus) {
            case WsMsgStatus.inProgress:
                this.clearSessionFromCounters(sessionId);
                this._generationStatusCounters.inProgress.push(sessionId);
                break;
            case WsMsgStatus.done:
                this.clearSessionFromCounters(sessionId);
                this._generationStatusCounters.success.push(sessionId);
                break;
            case WsMsgStatus.error:
                this.clearSessionFromCounters(sessionId);
                this._generationStatusCounters.error.push(sessionId);
                break;
        }
    }

    private clearSessionFromCounters(sessionId: number): void {
        this._generationStatusCounters.success = this.clearElementFromList(sessionId, this._generationStatusCounters.success);
        this._generationStatusCounters.inProgress = this.clearElementFromList(sessionId, this._generationStatusCounters.inProgress);
        this._generationStatusCounters.error = this.clearElementFromList(sessionId, this._generationStatusCounters.error);
    }

    private clearElementFromList(id: number, list: number[]): number[] {
        return list.includes(id) ? list.filter(id => id !== id) : list;
    }

    private processSessionsStatusCounters(sessionsList: SessionWrapper[]): void {
        const inProgress = this._generationStatusCounters.inProgress;
        const success = this._generationStatusCounters.success;
        const error = this._generationStatusCounters.error;
        sessionsList.forEach(sw => {
            if (inProgress.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.inProgress;
                sw.isActiveFromInProgress = false;
            } else if (success.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.active;
                sw.session.updating_capacity = false;
                sw.isActiveFromInProgress = true;
            } else if (error.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.error;
            }
        });
    }

    private scrollToSelectedSessionPack(sessionId: number): void {
        setTimeout(() => {
            const element = document.getElementById('session-packs-option-' + sessionId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}

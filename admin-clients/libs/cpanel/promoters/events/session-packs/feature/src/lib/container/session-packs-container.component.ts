import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EphemeralMessageService, MessageDialogService, MessageType } from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { first, Observable, take } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { SessionPacksListComponent } from '../list/session-packs-list.component';
import { SessionPacksStateMachine } from '../session-packs-state-machine';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-session-packs-container',
    templateUrl: './session-packs-container.component.html',
    styleUrls: ['./session-packs-container.component.scss'],
    providers: [
        SessionPacksStateMachine
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionPacksContainerComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);

    isLoading$: Observable<boolean>;
    @ViewChild(SessionPacksListComponent) listComponent: SessionPacksListComponent;

    constructor(
        private _eventsSrv: EventsService,
        private _sessionsSrv: EventSessionsService,
        private _websocketsSrv: WebsocketsService,
        private _messageDialogSrv: MessageDialogService,
        private _ephemeralSrv: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.isLoading$ = this._sessionsSrv.session.loading$();

        const wsSessionMsgs$ = this._eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                switchMap(event => this._websocketsSrv.getMessages$<WsSessionMsg>(Topic.event, event.id)),
                filter(msg => msg?.type === WsEventMsgType.session)
            );

        this._sessionsSrv.setSessionUpdatingCapacityUpdater(wsSessionMsgs$, this.#destroyRef);
        this._sessionsSrv.setAllSessionsUpdatingCapacityUpdater(wsSessionMsgs$, this.#destroyRef);
        this._sessionsSrv.setUpdateSessionCapacityUpdateState(wsSessionMsgs$, this.#destroyRef);

        this._sessionsSrv.getPendingCapacityUpdates$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(capacityUpdatesMap => this.checkCapacityUpdateProcesses(capacityUpdatesMap));
    }

    ngOnDestroy(): void {
        this._eventsSrv.event.get$().pipe(take(1))
            .subscribe(event => this._websocketsSrv.unsubscribeMessages(Topic.event, event.id));
        this._sessionsSrv.clearSessionsState();
    }

    sidebarToggle(drawer: MatDrawer): void {
        drawer.toggle();
    }

    private checkCapacityUpdateProcesses(capacityUpdatesMap: Map<number, WsMsgStatus>): void {
        if (capacityUpdatesMap.size) {
            const updates = Array.from(capacityUpdatesMap.keys());
            if (!updates.some(sessionId => capacityUpdatesMap.get(sessionId) === WsMsgStatus.inProgress)) {
                const errorIds = updates.filter(sessionId => capacityUpdatesMap.get(sessionId) === WsMsgStatus.error);
                if (errorIds.length) {
                    const errorValues = errorIds.length > 10 ? errorIds.slice(0, 10).join(', ') + '...' : errorIds.join(', ');
                    this._messageDialogSrv.showAlert({
                        message: 'EVENTS.SESSION_CAPACITY_UPDATE_FAIL',
                        messageParams: { length: errorIds.length, values: errorValues }
                    });
                } else {
                    this._ephemeralSrv.show({
                        type: MessageType.success,
                        msgKey: 'EVENTS.SESSION_CAPACITY_UPDATE_OK'
                    });
                }
                this._sessionsSrv.clearPendingCapacityUpdates();
            }
        }
    }
}

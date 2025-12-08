import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDrawer } from '@angular/material/sidenav';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, first, take } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { SessionsListComponent } from '../list/sessions-list.component';
import { SessionCapacityRelocationService } from './session-capacity-relocation.service';

@Component({
    selector: 'app-sessions-container',
    templateUrl: './sessions-container.component.html',
    styleUrls: ['./sessions-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SessionCapacityRelocationService],
    standalone: false
})
export class SessionsContainerComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #eventsSrv = inject(EventsService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #websocketsService = inject(WebsocketsService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #relocationSrv = inject(SessionCapacityRelocationService);
    readonly #route = inject(ActivatedRoute);

    private readonly _drawer = viewChild(MatDrawer);

    readonly $listComponent = viewChild(SessionsListComponent);
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#sessionsSrv.session.loading$(),
        this.#sessionsSrv.sessionList.inProgress$()
    ]));

    ngOnInit(): void {
        const wsSessionMsgs$ = this.#eventsSrv.event.get$()
            .pipe(
                first(event => !!event),
                switchMap(event => this.#websocketsService.getMessages$<WsSessionMsg>(Topic.event, event.id)),
                filter(msg => msg?.type === WsEventMsgType.session)
            );

        this.#sessionsSrv.setSessionUpdatingCapacityUpdater(wsSessionMsgs$, this.#destroyRef);
        this.#sessionsSrv.setAllSessionsUpdatingCapacityUpdater(wsSessionMsgs$, this.#destroyRef);
        this.#sessionsSrv.setUpdateSessionCapacityUpdateState(wsSessionMsgs$, this.#destroyRef);

        combineLatest([
            this.#sessionsSrv.getPendingCapacityUpdates$(),
            this.#sessionsSrv.isVenueMapSaving$()
        ])
            .pipe(
                filter(([_, updatingSessionVenueMap]) => !updatingSessionVenueMap),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([capacityUpdatesMap]) => this.checkCapacityUpdateProcesses(capacityUpdatesMap));

        // When relocating the sessions list will close
        this.#relocationSrv.isRelocating()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isRelocating => {
                if (isRelocating) {
                    this._drawer().close();
                } else {
                    this._drawer().open();
                }
            });
    }

    ngOnDestroy(): void {
        this.#eventsSrv.event.get$().pipe(take(1))
            .subscribe(event => this.#websocketsService.unsubscribeMessages(Topic.event, event.id));
        this.#sessionsSrv.clearSessionsState();
        this.#sessionsSrv.clearPendingCapacityUpdates();
    }

    changeSidebarState(): void {
        this._drawer().toggle();
    }

    openNewSessionDialog(): void {
        this.$listComponent()?.openNewSessionDialog();
    }

    private checkCapacityUpdateProcesses(capacityUpdatesMap: Map<number, WsMsgStatus>): void {
        if (capacityUpdatesMap.size) {
            const updates = Array.from(capacityUpdatesMap.keys());
            if (!updates.some(sessionId => capacityUpdatesMap.get(sessionId) === WsMsgStatus.inProgress)) {
                const errorIds = updates.filter(sessionId => capacityUpdatesMap.get(sessionId) === WsMsgStatus.error);
                if (errorIds.length) {
                    const errorValues = errorIds.length > 10 ? errorIds.slice(0, 10).join(', ') + '...' : errorIds.join(', ');
                    this.#messageDialogService.showAlert({
                        message: 'EVENTS.SESSION_CAPACITY_UPDATE_FAIL',
                        messageParams: { length: errorIds.length, values: errorValues }
                    });
                } else {
                    this.#messageDialogService.showSuccess({
                        title: 'EVENTS.TITLES.SESSION_CAPACITY_UPDATE_OK',
                        message: 'EVENTS.FORMS.INFOS.SESSION_CAPACITY_UPDATE_OK',
                        size: DialogSize.SMALL,
                        actionLabel: 'ACTIONS.CONFIRM'
                    });
                }
                this.#sessionsSrv.clearPendingCapacityUpdates();
            }
        }
    }
}

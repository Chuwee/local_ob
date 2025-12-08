import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionsFilterFields } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { IsAvetEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { Topic, WebsocketsService, WsEventMsgType, WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { StandardVenueTplEditorComponent } from '@admin-clients/shared/venues/feature/standard-venue-tpl-editor';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, interval, Observable, of, Subject, switchMap, tap } from 'rxjs';
import { filter, map, startWith, take, takeUntil } from 'rxjs/operators';

const increaseableEventStates = [EventStatus.inProgramming, EventStatus.planned];

@Component({
    selector: 'app-event-template-editor-container',
    templateUrl: './event-template-editor-container.component.html',
    styleUrls: ['./event-template-editor-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        StandardVenueTplEditorComponent, IsAvetEventPipe, AsyncPipe, NgIf,
        TranslatePipe, MatIcon, RouterLink, MatTooltip, FlexLayoutModule
    ]
})
export class EventTemplateEditorContainerComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _ngZone = inject(NgZone);
    private readonly _routingState = inject(RoutingState);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _sessionsSrv = inject(EventSessionsService);
    private readonly _venueTplsSrv = inject(VenueTemplatesService);
    private readonly _websocketsSrv = inject(WebsocketsService);
    private readonly _increasingCapacity = new BehaviorSubject<boolean>(false);

    @ViewChild(StandardVenueTplEditorComponent)
    private readonly _editor: StandardVenueTplEditorComponent;

    readonly event$ = this._eventsSrv.event.get$();
    readonly venueTemplate$ = this._venueTplsSrv.venueTpl.get$().pipe(filter(Boolean));
    readonly operatorMode$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly isInUse$ = this._sessionsSrv.isTemplateInUse$();
    readonly increasingCapacity$ = this._increasingCapacity.asObservable();

    readonly capacityIncreaseEnabled$ = this._sessionsSrv.isTemplateInUse$().pipe(switchMap(inUse =>
        inUse ? this._eventsSrv.event.get$().pipe(map(e => e && increaseableEventStates.includes(e.status))) : of(false)
    ));

    readonly mmcIntegrationEnabled$
        = this._entitiesSrv.getElementEntity$(this._venueTplsSrv.venueTpl.get$(), this._authSrv.getLoggedUser$())
            .pipe(filter(Boolean), map(entity => entity?.settings?.interactive_venue?.enabled));

    ngOnInit(): void {
        this._routingState.removeLastUrlsWith('template-editor');
        this.venueTemplate$.pipe(take(1))
            .subscribe(venueTemplate => {
                this._sessionsSrv.loadTemplateIsInUse(venueTemplate.event_id, venueTemplate.id);
                this._sessionsSrv.loadAllSessions(venueTemplate.event_id, {
                    venueTplId: venueTemplate.id,
                    fields: [SessionsFilterFields.statusFlags, SessionsFilterFields.generationStatus, SessionsFilterFields.status]
                });
            });
        combineLatest([
            this._sessionsSrv.getAllSessions$().pipe(filter(Boolean)),
            this.venueTemplate$
                .pipe(
                    switchMap(tpl => this._websocketsSrv.getMessages$<WsSessionMsg>(Topic.event, tpl.event_id)),
                    filter(msg => msg?.type === WsEventMsgType.session),
                    startWith(null as WsSessionMsg)
                )
        ])
            .pipe(
                tap(([sessionsResponse, msg]) => {
                    if (msg) {
                        const msgSession = sessionsResponse.data.find(session => session.id === msg.data.id);
                        if (msgSession) {
                            msgSession.updating_capacity = msg.status === WsMsgStatus.inProgress;
                        }
                    }
                }),
                map(([sessionsResponse, _]) => sessionsResponse.data.some(session => session.updating_capacity)),
                // TODO: Waits 5 sec when increasing process ends, to let SVGs be saved by another client,
                // must be removed when api-stream gives feedback about templates changes.
                switchMap(increasing => interval(increasing ? 0 : 5000).pipe(map(() => increasing))),
                takeUntil(this._onDestroy)
            )
            // TODO: This re-entry to the ng zone should preferably be handled in websocketSrv
            .subscribe(increasing => this._ngZone.run(() => this._increasingCapacity.next(increasing)));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canDeactivate(): Observable<boolean> {
        return this._editor?.canDeactivate();
    }
}

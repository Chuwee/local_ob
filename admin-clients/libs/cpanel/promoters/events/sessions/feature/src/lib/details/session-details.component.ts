import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionGenerationStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { AttributeScope, EntitiesBaseService, EntityExternalBarcodesFormat, EventType } from '@admin-clients/shared/common/data-access';
import {
    Topic, WebsocketsService, WsEventMsgType, WsMsg, WsMsgStatus, WsSessionData, WsSessionMsg
} from '@admin-clients/shared/core/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-session-details',
    templateUrl: './session-details.component.html',
    styleUrls: ['./session-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionDetailsComponent {
    private readonly _auth = inject(AuthenticationService);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _sessionsSrv = inject(EventSessionsService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);
    private readonly _ws = inject(WebsocketsService);
    readonly #env = inject(ENVIRONMENT_TOKEN);

    readonly isLowEnv = this.#env.env !== 'pro';

    readonly isOperator$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());

    readonly deepPath$ = getDeepPath$(this._router, this._route);

    readonly isSessionGenerationError$ = this._sessionsSrv.session.get$()
        .pipe(
            filter(session => !!session),
            map(session => session.generation_status === SessionGenerationStatus.error),
            withLatestFrom(this.deepPath$),
            map(([isSessionGenerationError, path]) => {
                if (isSessionGenerationError && path !== 'planning') {
                    this._router.navigate(['planning'], { relativeTo: this._route });
                }
                return isSessionGenerationError;
            }),
            shareReplay(1)
        );

    readonly isSessionGenerationInProgress$ = combineLatest([
        this._sessionsSrv.session.get$().pipe(filter(session => !!session)),
        this._eventsSrv.event.get$().pipe(
            filter(event => !!event),
            switchMap(event => this._ws.getMessages$<WsSessionMsg>(Topic.event, event.id)),
            startWith(null as WsMsg<WsEventMsgType, WsSessionData>)
        )
    ])
        .pipe(
            filter(([session, wsMsg]) => !wsMsg || wsMsg.type === WsEventMsgType.session && Number(wsMsg.data.id) === session.id),
            map(([session, wsMsg]) => {
                if (session.generation_status === SessionGenerationStatus.inProgress ||
                    session.generation_status === SessionGenerationStatus.pending) {
                    return !(wsMsg?.status === WsMsgStatus.done || wsMsg?.status === WsMsgStatus.error);
                }
                return false;
            }),
            shareReplay(1)
        );

    readonly archived$ = this._sessionsSrv.session.get$()
        .pipe(
            withLatestFrom(this.deepPath$),
            map(([session, path]) => {
                if (session?.archived && (path?.includes('capacity') || path?.includes('seat-status'))) {
                    this._router.navigate(['planning'], { relativeTo: this._route });
                }
                return !session || session.archived;
            })
        );

    readonly showSecondaryMarketConfig$ = this._entitiesService.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_secondary_market)
    );

    readonly hasAttributes$ = this._sessionsSrv.session.get$()
        .pipe(
            first(Boolean),
            tap(session => this._entitiesService.loadAttributes(session.entity.id, AttributeScope.session)),
            switchMap(() => this._entitiesService.getAttributes$()),
            filter(Boolean),
            map(attributes => !!attributes.length)
        );

    readonly isActivityCapacity$ = combineLatest([
        this._sessionsSrv.session.get$(),
        this._eventsSrv.event.get$()
    ]).pipe(filter(data => data.every(Boolean)),
        map(([session, event]) =>
            event.type === EventType.activity ||
            event.type === EventType.themePark ||
            session.venue_template.type === VenueTemplateType.activity
        )
    );

    readonly isGraphicTemplate$ = this._sessionsSrv.session.get$().pipe(filter(Boolean),
        map(session => session?.venue_template?.graphic)
    );

    readonly enableSessionCodeConfiguration$ = this._entitiesService.getEntity$()
        .pipe(
            first(entity => !!entity),
            map(entity => {
                const isExternalBarcodeEnabled = entity.settings?.external_integration?.barcode?.enabled;
                const externalBarcodeFormat = entity.settings?.external_integration?.barcode?.integration_id;

                return !!isExternalBarcodeEnabled && externalBarcodeFormat === EntityExternalBarcodesFormat.ifema;
            })
        );

    readonly enableAutomaticSales$ = this._auth.getLoggedUser$()
        .pipe(
            filter(Boolean),
            map(user => !!user.roles.filter(role => role.permissions?.includes(EntityUserPermissions.automaticSales)).length)
        );

}

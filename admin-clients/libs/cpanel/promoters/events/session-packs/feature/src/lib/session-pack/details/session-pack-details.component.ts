import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { EventSessionsService, Session, SessionGenerationStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EntityExternalBarcodesFormat } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsg, WsMsgStatus, WsSessionData } from '@admin-clients/shared/core/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-session-pack-details',
    templateUrl: './session-pack-details.component.html',
    styleUrls: ['./session-pack-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionPackDetailsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #breadcrumbsService = inject(BreadcrumbsService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #ws = inject(WebsocketsService);
    readonly #env = inject(ENVIRONMENT_TOKEN);

    readonly isLowEnv = this.#env.env !== 'pro';

    private get _breadcrumb(): string | undefined {
        return this.#route.snapshot.data['breadcrumb'];
    }

    deepPath$ = getDeepPath$(this.#router, this.#route);
    session$: Observable<Session>;
    archived$: Observable<boolean>;
    isSessionGenerationError$: Observable<boolean>;
    isSessionGenerationInProgress$: Observable<boolean>;
    enableSessionCodeConfiguration$: Observable<boolean>;
    shouldShowPresales$: Observable<boolean>;

    readonly enableAutomaticSales$ = this.#auth.getLoggedUser$()
        .pipe(
            filter(Boolean),
            map(user => !!user.roles.filter(role => role.permissions?.includes(EntityUserPermissions.automaticSales)).length)
        );

    ngOnInit(): void {
        this.model();
        this.loadDataHandler();
    }

    private model(): void {
        this.session$ = this.#sessionsSrv.session.get$()
            .pipe(
                filter(session => !!session),
                shareReplay(1)
            );
        this.isSessionGenerationError$ = this.#sessionsSrv.session.get$()
            .pipe(
                filter(session => !!session),
                map(session => session.generation_status === SessionGenerationStatus.error),
                shareReplay(1)
            );
        this.isSessionGenerationInProgress$ = combineLatest([
            this.#sessionsSrv.session.get$().pipe(
                filter(session => !!session)
            ),
            this.#sessionsSrv.session.get$().pipe(
                filter(session => !!session),
                switchMap(session => this.#ws.getMessages$<WsMsg<WsEventMsgType, WsSessionData>>(Topic.event, session.event.id)),
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

        this.enableSessionCodeConfiguration$ = this.#entitiesService.getEntity$()
            .pipe(
                first(entity => !!entity),
                map(entity => {
                    const isExternalBarcodeEnabled = entity.settings?.external_integration?.barcode?.enabled;
                    const externalBarcodeFormat = entity.settings?.external_integration?.barcode?.integration_id;

                    return !!isExternalBarcodeEnabled && externalBarcodeFormat === EntityExternalBarcodesFormat.ifema;
                })
            );

        this.archived$ = this.#sessionsSrv.session.get$()
            .pipe(
                withLatestFrom(this.deepPath$),
                map(([session, path]) => {
                    if (session?.archived && (path?.includes('capacity') || path?.includes('seat-status'))) {
                        this.#router.navigate(['planning'], { relativeTo: this.#route });
                    }
                    return !session || session.archived;
                })
            );
    }

    private loadDataHandler(): void {
        this.session$
            .pipe(
                tap(session => {
                    this.#breadcrumbsService.addDynamicSegment(this._breadcrumb, session.name);
                }),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();

        this.#sessionsSrv.isAllSessionsLoading$()
            .pipe(
                tap(isLoading => {
                    if (isLoading) {
                        this.#breadcrumbsService.addDynamicSegment(this._breadcrumb, 'LOADING');
                    }
                }),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();
    }
}

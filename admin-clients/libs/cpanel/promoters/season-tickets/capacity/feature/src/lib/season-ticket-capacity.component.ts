import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SEASON_TICKET_SERVICE, StdVenueTplMgrComponent, VenueTemplateAction, VenueTemplateActionType, VenueTemplateEditorType,
    VenueTemplateSeatClickAction
} from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import {
    SeasonTicketCapacityService, SeasonTicketCapacityState
} from '@admin-clients/cpanel-promoters-season-tickets-capacity-data-access';
import { TicketsBaseApi, TicketsBaseService, TicketsBaseState } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Seat, VENUE_MAP_SERVICE } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService, VenueTemplatesState } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { debounceTime, first, Observable, of, startWith, withLatestFrom } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-capacity',
    templateUrl: './season-ticket-capacity.component.html',
    styleUrls: ['./season-ticket-capacity.component.scss'],
    providers: [
        TicketsBaseService,
        TicketsBaseApi,
        TicketsBaseState,
        SeasonTicketCapacityState,
        SeasonTicketCapacityService,
        {
            provide: VENUE_MAP_SERVICE, useExisting: SeasonTicketCapacityService
        },
        {
            provide: SEASON_TICKET_SERVICE, useExisting: SeasonTicketCapacityService
        },
        VenueTemplatesState,
        VenueTemplatesService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketCapacityComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #seasonTicketCapacitySrv = inject(SeasonTicketCapacityService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #wsService = inject(WebsocketsService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #ticketsService = inject(TicketsBaseService);
    readonly #router = inject(Router);
    readonly #destroyRef = inject(DestroyRef);
    readonly #routingState = inject(RoutingState);
    readonly #authSrv = inject(AuthenticationService);

    #standardVenueTemplateComponent: StdVenueTplMgrComponent;
    #eventId: number;
    #sessionId: number;

    readonly venueTemplate$ = this.#venueTemplatesSrv.venueTpl.get$().pipe(filter(Boolean));
    readonly venueTemplateEditorType = VenueTemplateEditorType;
    readonly sidebarWidth$ = inject(BreakpointObserver)
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches ? '240px' : '280px'));

    readonly showEditorBtn$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_EDI]);

    readonly isCapacityUpdateInProgress$ = this.#seasonTicketSrv.seasonTicket.get$().pipe(
        first(Boolean),
        switchMap(seasonTicket => this.#wsService.getMessages$<WsSessionMsg>(Topic.event, seasonTicket.id)),
        filter(wsMsg => !wsMsg || wsMsg?.type === WsEventMsgType.session),
        startWith(null),
        tap(msg => {
            if (msg && msg.status !== WsMsgStatus.inProgress) {
                if (msg.status === WsMsgStatus.error) {
                    this.#msgDialogService.showAlert({
                        message: 'SEASON_TICKET.CAPACITY_UPDATE_FAIL'
                    });
                } else {
                    this.#ephemeralMessageService.show({
                        type: MessageType.success,
                        msgKey: 'SEASON_TICKET.CAPACITY_UPDATE_OK'
                    });
                }
                this.#standardVenueTemplateComponent?.cancelChanges(true, true);
            }
        }),
        switchMap(() => this.#seasonTicketSrv.seasonTicket.get$()),
        map(value => value?.updating_capacity),
        debounceTime(500)
    );

    readonly isGenerationStatusInProgress$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$();
    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$();
    readonly isLoading$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#venueTemplatesSrv.venueTpl.inProgress$(),
        this.#seasonTicketCapacitySrv.isVenueMapSaving$(),
        this.#seasonTicketCapacitySrv.isVenueMapLoading$()
    ]);

    isDirty$: Observable<boolean>;

    readonly $isSga = toSignal(this.#venueTemplatesSrv.venueTpl.get$().pipe(
        filter(Boolean),
        map(venueTemplate => venueTemplate?.inventory_provider === 'sga'))
    );

    ngOnInit(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(st => {
                this.#sessionId = st.session_id;
                this.#eventId = st.id;
            });

        this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isGenerationStatusReady => {
                if (!isGenerationStatusReady) return;
                this.#seasonTicketSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(st => {
                        if (st.venue_templates?.length) {
                            this.#venueTemplatesSrv.venueTpl.load(st.venue_templates[0].id);
                        }
                    });
            });
    }

    ngOnDestroy(): void {
        this.#seasonTicketCapacitySrv.clearLinkableSeats();
    }

    @ViewChild(StdVenueTplMgrComponent)
    set standardVenueTemplateComponent(standardVenueTemplateComponent: StdVenueTplMgrComponent) {
        this.#standardVenueTemplateComponent = standardVenueTemplateComponent;
        if (standardVenueTemplateComponent) {
            this.isDirty$ = standardVenueTemplateComponent.isDirty$
                .pipe(
                    distinctUntilChanged(),
                    shareReplay(1)
                );
        }
    }

    cancel(): void {
        this.#seasonTicketCapacitySrv.clearLinkableSeats();
        this.#standardVenueTemplateComponent?.cancelChanges();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        this.#seasonTicketCapacitySrv.clearLinkableSeats();
        return this.#standardVenueTemplateComponent?.save$() ?? of(true);
    }

    canDeactivate(): Observable<boolean> {
        return this.isDirty$
            ?.pipe(
                withLatestFrom(this.#seasonTicketSrv.seasonTicket.get$()),
                switchMap(([isDirty, seasonTicket]) => {
                    if (isDirty && !seasonTicket.updating_capacity) {
                        return this.#msgDialogService.openRichUnsavedChangesWarn().pipe(
                            switchMap(res => {
                                if (res === UnsavedChangesDialogResult.cancel) {
                                    return of(false);
                                } else if (res === UnsavedChangesDialogResult.continue) {
                                    return of(true);
                                } else {
                                    return this.save$().pipe(
                                        switchMap(() => of(true)),
                                        catchError(() => of(false))
                                    );
                                }
                            }));
                    } else {
                        return of(true);
                    }
                }),
                take(1)
            );
    }

    userVenueAction(action: VenueTemplateAction): void {
        if (VenueTemplateActionType.viewTicketDetail) {
            this.viewTicketDetail((action as VenueTemplateSeatClickAction).data);
        }
    }

    viewTicketDetail(seat: Seat): void {
        this.#ticketsService.getLastSessionSeatOrderItem({
            id: [seat.ticketId],
            event_id: [this.#eventId],
            session_id: [this.#sessionId]
        })
            .subscribe(ticket => {
                const baseUrl = window.location.origin.replace(this.#router.url, '');
                const urlTree = this.#router.createUrlTree(['/tickets', ticket.order.code + '-' + ticket.id]);
                window.open(baseUrl + this.#router.serializeUrl(urlTree), '_blank');
            });
    }

    gotoEditor(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                this.#routingState.removeLastUrlsWith('/season-tickets/');
                this.#router.navigate(['season-tickets', seasonTicket.id, 'template-editor']);
            });
    }

    refreshExternalAvailability(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                first(),
                switchMap(seasonTicket => this.#seasonTicketSrv.refreshExternalAvailability(seasonTicket.id))
            )
            .subscribe(() => {
                this.#msgDialogService.showSuccess({
                    size: DialogSize.MEDIUM,
                    title: 'TITLES.SESSION.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS',
                    message: 'EVENTS.SESSION.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS'
                });
            });
    }
}

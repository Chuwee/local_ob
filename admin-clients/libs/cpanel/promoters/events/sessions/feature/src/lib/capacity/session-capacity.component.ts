import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { EventAvetConnection, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, RelocationSeat, SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    exportDataCapacity, StdVenueTplMgrComponent, VenueTemplateAction, VenueTemplateActionType, VenueTemplateEditorType,
    VenueTemplateGetSeatInfo, VenueTemplateSeatClickAction, VenueTemplateStartRelocation
} from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import {
    EventType, ExternalInventoryProviders, OrderItemDetails, TableColConfigService, TicketsBaseApi, TicketsBaseService, TicketsBaseState, TicketState
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, ExportDialogComponent, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ExportFormat } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    SessionCapacityApi, SessionCapacityService, SessionCapacityState
} from '@admin-clients/shared/venues/data-access/session-standard-venue-tpls';
import { Seat, SESSION_PACK_SERVICE, VENUE_MAP_SERVICE } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, viewChild } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { BehaviorSubject, combineLatest, debounceTime, Observable, of, scan, startWith, withLatestFrom } from 'rxjs';
import { catchError, filter, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { SessionCapacityRelocationService } from '../container/session-capacity-relocation.service';

@Component({
    selector: 'app-session-capacity',
    templateUrl: './session-capacity.component.html',
    styleUrls: ['./session-capacity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        TicketsBaseService,
        TicketsBaseApi,
        TicketsBaseState,
        SessionCapacityService,
        SessionCapacityApi,
        SessionCapacityState,
        { provide: VENUE_MAP_SERVICE, useExisting: SessionCapacityService },
        { provide: SESSION_PACK_SERVICE, useExisting: SessionCapacityService }
    ],
    standalone: false
})
export class SessionCapacityComponent implements OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #eventSrv = inject(EventsService);
    readonly #eventSessionSrv = inject(EventSessionsService);
    readonly #sessionCapacitySrv = inject(SessionCapacityService);
    readonly #venueTemplateService = inject(VenueTemplatesService);
    readonly #ticketsService = inject(TicketsBaseService);
    readonly #router = inject(Router);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #websocketsService = inject(WebsocketsService);
    readonly #ephemeral = inject(EphemeralMessageService);
    readonly #relocationSrv = inject(SessionCapacityRelocationService, { optional: true });
    readonly #externalSrv = inject(ExternalEntityService);

    readonly #seatsToRelocate = new BehaviorSubject([] as number[]);

    private readonly _standardVenueTemplateComponent = viewChild(StdVenueTplMgrComponent);
    #eventId: number;
    #sessionId: number;
    #sessionName: string;
    #sessionType: SessionType;

    readonly $isDirty = toSignal(toObservable(this._standardVenueTemplateComponent).pipe(switchMap(comp => comp?.isDirty$ || of(false))));

    readonly loading$ = booleanOrMerge([
        this.#eventSessionSrv.isRefreshExternalAvailabilityInProgress$(),
        this.#eventSessionSrv.mapping.loading$()
    ]);

    readonly showRefreshExternalAvailability$ = this.#eventSrv.event.get$()
        .pipe(
            tap(event => this.#eventId = event.id),
            map(event => event.type === EventType.avet && event.additional_config?.avet_config === EventAvetConnection.socket));

    readonly showRemapSession$ = this.#eventSrv.event.get$()
        .pipe(
            tap(event => this.#eventId = event.id),
            map(event => event.type === EventType.avet && event.additional_config?.avet_config !== EventAvetConnection.socket));

    readonly session$ = this.#eventSessionSrv.session.get$()
        .pipe(
            filter(session => !!session && !session.archived),
            tap(session => {
                if (this.#sessionId !== session.id) {
                    this.#sessionId = session.id;
                    this.#sessionName = session.name;
                    this.#sessionType = session.type;
                    this.#sessionName = session.name;
                    this.#venueTemplateService.venueTpl.clear();
                    this.#eventSessionSrv.clearLinkedSessions();
                    // At this point, template manager needs this array to be defined, to show or hide session packs group,
                    //but it could be interesting to make the call only when required.
                    if (session.type === SessionType.session) {// && session.session_ids?.length) {
                        this.#eventSessionSrv.loadLinkedSessions(session.event.id, session.id);
                    }
                    this.#externalSrv.inventoryProviders.load(session.entity?.id);
                }
            }),
            shareReplay(1),
            takeUntilDestroyed(this.#destroyRef)
        );

    readonly isSessionCapacityUpdateInProgress$ = combineLatest([
        this.#eventSessionSrv.session.get$().pipe(filter(s => !!s)),
        this.#eventSrv.event.get$().pipe(
            first(event => !!event),
            switchMap(event => this.#websocketsService.getMessages$<WsSessionMsg>(Topic.event, event.id)),
            filter(wsMsg => !wsMsg || wsMsg?.type === WsEventMsgType.session),
            startWith(null)
        ),
        this.#sessionCapacitySrv.isVenueMapSaving$()
    ])
        .pipe(
            tap(([session, msg, updatingSessionVenueMap]) => {
                if (!updatingSessionVenueMap && msg?.data.id === session.id && msg.status !== WsMsgStatus.inProgress) {
                    this._standardVenueTemplateComponent()?.cancelChanges(true, true);
                }
            }),
            map(([session, _, updatingSessionVenueMap]) => session.updating_capacity || updatingSessionVenueMap),
            debounceTime(500),
            takeUntilDestroyed(this.#destroyRef)
        );

    readonly linkedSessions$ = this.#eventSessionSrv.getLinkedSessions$();
    readonly canRelocate$ = this.#authSrv.getLoggedUser$().pipe(map(user => this.#relocationSrv?.canRelocate(user) || false));
    readonly isRelocating$: Observable<boolean> = this.#relocationSrv?.isRelocating() ?? of(false);

    readonly seatsToRelocateInfoSource$ = this.isRelocating$
        .pipe(
            filter(Boolean),
            tap(() => {
                this.#ticketsService.ticketList.clear();
                this.#seatsToRelocate.next([]);
            }),
            switchMap(() => combineLatest([
                this.#ticketsService.ticketList.loading$(),
                this.#seatsToRelocate.asObservable()
                    .pipe(scan((acc, value) => value?.length ? Array.from(new Set([...acc, ...(value || [])])) : [], [])),
                this.#ticketsService.ticketList.getData$()
                    .pipe(scan((acc, value) =>
                        [
                            ...acc,
                            ...(value?.length ? value?.filter(item => item.state === TicketState.purchase
                                && !(acc.map(accItem => accItem.id).includes(item.id))) : [])
                        ], [] as OrderItemDetails[]))
            ])),
            debounceTime(0),
            filter(([loading]) => !loading)
        );

    readonly seatsToRelocateInfo$ = this.seatsToRelocateInfoSource$.pipe(
        map(([, seatIds, orderItems]) => {
            const orderItemsMap = new Map(orderItems?.map(orderItem => [orderItem.id, orderItem]) || []);
            const seatIdsToQuery = seatIds.filter(id => !orderItemsMap.has(id));
            if (seatIdsToQuery?.length) {
                this.#ticketsService.ticketList.load({
                    id: seatIdsToQuery.slice(0, 20),
                    event_id: [this.#eventId],
                    session_id: [this.#sessionId],
                    state: TicketState.purchase
                });
            }
            return orderItems.map(orderItem =>
            ({
                orderCode: orderItem.order?.code, seatId: orderItem.ticket?.allocation?.seat?.id,
                isInSM: (orderItem?.related_product_state === 'SEC_MKT_LOCKED' || orderItem?.origin_market === 'SECONDARY')
            })) || [];
        })
    );

    readonly $isSga = toSignal(this.#externalSrv.inventoryProviders.get$().pipe(
        filter(Boolean),
        map(inv => inv?.inventory_providers?.includes(ExternalInventoryProviders.sga)))
    );

    constructor() {
        toObservable(this._standardVenueTemplateComponent)
            .pipe(
                filter(templateComponent => !!templateComponent),
                switchMap(templateComponent => templateComponent.isRelocating$),
                filter(() => !!this.#relocationSrv),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(isRelocating => this.#relocationSrv.setIsRelocating(isRelocating));

        // When relocating this accumulates the order codes of the selected seats and load the tickets just of the new orders
        this.seatsToRelocateInfoSource$.pipe(
            scan((acc, [, , orderItems]) => {
                if (!orderItems?.length) return [];
                const differentOrders = Array.from(new Set(orderItems.map(item => item.order.code).filter(item => !acc.includes(item))));
                if (!!differentOrders.length) {
                    this.#ticketsService.ticketList.load({
                        q: differentOrders.join(', '),
                        event_id: [this.#eventId],
                        session_id: [this.#sessionId],
                        state: TicketState.purchase
                    });
                }
                return [...acc, ...differentOrders];
            }, [] as string[]),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe();

        // When the api that retreives order information of relocation seats fails
        // twice then clear seats to relocate and exit from relocation mode
        this.seatsToRelocateInfoSource$
            .pipe(
                scan((acc, [, seatIds, orderItems]) => {
                    const orderItemsMap = new Map(orderItems?.map(orderItem => [orderItem.id, orderItem]) || []);
                    const seatIdsToQuery = seatIds.filter(id => !orderItemsMap.has(id));
                    if (seatIdsToQuery?.length) {
                        if (acc[0] === seatIdsToQuery[0]) {
                            acc[1] += 1;
                        } else {
                            acc[0] = seatIdsToQuery[0];
                            acc[1] = 1;
                        }
                    }
                    if (acc[1] > 1) {
                        acc = [0, 0];
                        this.#seatsToRelocate.next([]);
                        this._standardVenueTemplateComponent()?.cancelChanges(true);
                    }
                    return acc;
                }, [0, 0]),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe();
    }

    ngOnDestroy(): void {
        this.#venueTemplateService.clearVenueTemplateData();
        this.#relocationSrv?.setIsRelocating(false);
        this.#venueTemplateService.venueTpl.clear();
        this.#ticketsService.ticketList.clear();
    }

    cancel(): void {
        this._standardVenueTemplateComponent()?.cancelChanges();
    }

    save(): void {
        this._standardVenueTemplateComponent()?.save();
    }

    userVenueAction(action: VenueTemplateAction): void {
        switch (action.type) {
            case VenueTemplateActionType.viewTicketDetail:
                this.viewTicketDetail((action as VenueTemplateSeatClickAction).data);
                break;
            case VenueTemplateActionType.exportCapacity:
                this.exportCapacity();
                break;
            case VenueTemplateActionType.selectRelocationOrigin:
                this.#seatsToRelocate.next((action as VenueTemplateGetSeatInfo).data);
                break;
            case VenueTemplateActionType.startRelocation:
                this.#startRelocation((action as VenueTemplateStartRelocation).data);
                break;
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

    canDeactivate(): Observable<boolean> {
        return toObservable(this.$isDirty)
            .pipe(
                withLatestFrom(this.session$, this.isRelocating$),
                switchMap(([isDirty, session, isRelocating]) => {
                    if ((isDirty && !session.updating_capacity) || isRelocating) {
                        return this.#msgDialogService.defaultUnsavedChangesWarn();
                    } else {
                        return of(true);
                    }
                }),
                take(1)
            );
    }

    getEditorType(): VenueTemplateEditorType {
        return this.#sessionType === SessionType.session ?
            VenueTemplateEditorType.sessionTemplate : VenueTemplateEditorType.sessionPackTemplate;
    }

    isRestrictedPack(): boolean {
        return this.#sessionType === SessionType.session ? null : this.#sessionType === SessionType.unrestrictedPack;
    }

    refreshExternalAvailability(): void {
        this.#eventSessionSrv.refreshExternalAvailability(this.#eventId, this.#sessionId)
            .subscribe(() => {
                this.#msgDialogService.showSuccess({
                    size: DialogSize.MEDIUM,
                    title: 'TITLES.SESSION.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS',
                    message: 'EVENTS.SESSION.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS'
                });
            });
    }

    exportCapacity(): void {
        const config = new ObMatDialogConfig({
            exportData: exportDataCapacity,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SESSION_CAPACITY')
        });
        this.#matDialog.open(ExportDialogComponent, config)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(exportList => {
                    this.#tableSrv.setColumns('EXP_SESSION_CAPACITY', exportList.fields.map(resultData => resultData.field));
                    return this.#eventSessionSrv.exportCapacity(this.#eventId, this.#sessionId, exportList);
                }),
                filter(Boolean)
            )
            .subscribe(() => this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' }));
    }

    remap(): void {
        this.#eventSessionSrv.mapping.create(this.#eventId, this.#sessionId)
            .subscribe(() => this.#ephemeral.showSuccess({
                msgKey: 'EVENTS.SESSION.MAPPING_SUCCESS',
                msgParams: { sessionName: this.#sessionName }
            }));
    }

    #startRelocation(relocationSeats: RelocationSeat[]): void {
        this.#eventSessionSrv.initSessionCapacityUpdateState(this.#sessionId);
        this.#eventSessionSrv.startRelocation$(this.#eventId, this.#sessionId, { seats: relocationSeats })
            .pipe(
                catchError(error => {
                    this.#eventSessionSrv.deleteSessionCapacityUpdate(this.#sessionId);
                    throw error;
                })
            )
            .subscribe();
    }

}

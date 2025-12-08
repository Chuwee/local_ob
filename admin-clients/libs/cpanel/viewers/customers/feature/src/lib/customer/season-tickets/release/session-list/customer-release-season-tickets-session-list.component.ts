import { Metadata } from '@OneboxTM/utils-state';
import { SeasonTicketsService, SeasonTicketStatus } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { GetSeasonTicketSessionsRequest, SeasonTicketSessionsService, SeasonTicketSessionStatus }
    from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { VmCustomerSeasonTicketProduct, VmCustomerSession }
    from '@admin-clients/cpanel-viewers-customers-data-access';
import { ReleaseAction, ReleaseDataSessionStatus, SeatManagementDataRequestUserType, TicketsBaseService, TicketTransferData, TransferDataSessionStatus }
    from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig }
    from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output, ViewChild, ViewContainerRef }
    from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, SortDirection } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil } from 'rxjs/operators';
import { CustomerReleaseRecoverDialogComponent } from '../dialog/release-recover-dialog/customer-release-recover-dialog.component';

const SESSIONS_PAGE_SIZE = 20;

@Component({
    selector: 'app-customer-release-season-tickets-session-list',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MatPaginatorModule, AsyncPipe, TranslatePipe, DateTimePipe,
        MatButtonModule, MatMenuModule, NgClass, MatProgressSpinnerModule, MatIconModule,
        MaterialModule, LocalCurrencyPipe, LocalNumberPipe, EmptyStateTinyComponent,
        EllipsifyDirective
    ],
    styleUrls: ['./customer-release-season-tickets-session-list.component.scss'],
    templateUrl: './customer-release-season-tickets-session-list.component.html'
})
export class CustomerReleaseSeasonTicketsSessionListComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject();
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #viewCrf = inject(ViewContainerRef);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #seasonTicketSessionSrv = inject(SeasonTicketSessionsService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);

    @ViewChild(MatSort, { static: true }) private readonly _matSort: MatSort;
    @ViewChild(MatPaginator, { static: true }) private readonly _matPaginator: MatPaginator;

    private _request: GetSeasonTicketSessionsRequest;

    readonly isHandsetOrTablet$ = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    readonly dateTimeFormats = DateTimeFormats;
    readonly isLoading$ = booleanOrMerge([
        this.#ticketsSrv.ticketDetail.loading$(),
        this.#seasonTicketSessionSrv.sessions.loading$(),
        this.#ticketsSrv.ticketTransfer.linkLoading$(),
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]);

    readonly $isSeasonTicketStatusReady = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$()
        .pipe(
            filter(Boolean),
            map(seasonTicketStatus => seasonTicketStatus.status === SeasonTicketStatus.ready)
        ));

    readonly seasonTicketSessions$ = combineLatest([
        this.#ticketsSrv.ticketDetail.get$(),
        this.#seasonTicketSessionSrv.sessions.getData$(),
        this.#seasonTicketSrv.seasonTicket.get$(),
        this.#seasonTicketSrv.seasonTicketReleaseSeat.get$()
    ]).pipe(
        map(([ticketDetail, sessions, seasonTicket, releaseSeat]) => {
            const releaseSessions: VmCustomerSession[] = [];
            if (ticketDetail && sessions && seasonTicket) {
                const { release_data: releaseData } = ticketDetail;
                const { transfer_data: transferData } = ticketDetail;
                releaseData.sessions.forEach(releaseSession => {
                    if (!seasonTicket.settings.operative.allow_release_seat && releaseSession.status !== ReleaseDataSessionStatus.released
                        && releaseSession.status !== ReleaseDataSessionStatus.sold) {
                        releaseSession.status = ReleaseDataSessionStatus.noOperationAllowed;
                    }
                    const transferSession = transferData.sessions.find(transferSession => transferSession.session_id === releaseSession.session_id);
                    if (transferSession.status === TransferDataSessionStatus.transferred) {
                        releaseSession.status = ReleaseDataSessionStatus.transferred;
                        releaseSession.request_user = null;
                    }

                    releaseSession.showRecover = releaseSession.status === ReleaseDataSessionStatus.released;

                    const index = sessions.findIndex(e => e.session_id === releaseSession.session_id);
                    if (index !== -1) {
                        releaseSessions[index] = { ...sessions[index], releaseSession };
                        const today = new Date();
                        releaseSessions[index].releaseSession.showRelease = seasonTicket.settings.operative.allow_release_seat &&
                            new Date(releaseSessions[index].session_starting_date) > today && (releaseSession.status === ReleaseDataSessionStatus.recovered
                                || releaseSession.status === ReleaseDataSessionStatus.notReleased);

                    }
                });
            }
            let maximumEarnings = releaseSeat?.earnings_limit?.percentage * ticketDetail?.price.base / 100;
            releaseSessions.sort((a, b) =>
                new Date(a.session_starting_date).getTime() - new Date(b.session_starting_date).getTime()
            ).map(session => {
                const sessionEarnings = session.releaseSession?.percentage * session.releaseSession?.price / 100;
                if (session.releaseSession?.status !== ReleaseDataSessionStatus.sold) {
                    return;
                }
                if (releaseSeat?.earnings_limit?.enabled) {
                    if (maximumEarnings >= sessionEarnings) {
                        session.releaseSession.earnings = sessionEarnings;
                        maximumEarnings -= sessionEarnings;
                    } else if (maximumEarnings > 0) {
                        session.releaseSession.earnings = maximumEarnings;
                        maximumEarnings = 0;
                    } else {
                        session.releaseSession.earnings = 0;
                    }
                } else {
                    session.releaseSession.earnings = sessionEarnings;
                }
            });
            return releaseSessions;
        }),
        shareReplay(1)
    );

    readonly seasonTicketReleaseStatus = ReleaseDataSessionStatus;
    readonly initSortCol = 'session_name';
    readonly initSortDir: SortDirection = 'asc';
    readonly displayedColumns = [
        'session_name',
        'session_starting_date',
        'session_purchase_date',
        'session_purchase_price',
        'session_release_percentage',
        'session_release_earnings',
        'session_released_by',
        'session_status',
        'session_release_actions'
    ];

    readonly currency$ = this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(first(), map(seasonTicket => seasonTicket.currency_code));

    @Input() selectedSeat: VmCustomerSeasonTicketProduct;
    @Output() requestChanged = new EventEmitter<GetSeasonTicketSessionsRequest>();

    ngOnInit(): void {
        this.initRequest();
        this.metaDataChangeHandler();
        this.tableChangeHandler();
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                filter(Boolean),
                takeUntil(this.#onDestroy)
            )
            .subscribe(seasonTicket =>
                this.#seasonTicketSrv.seasonTicketReleaseSeat.load(seasonTicket.id)
            );
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#seasonTicketSrv.seasonTicketReleaseSeat.clear();
    }

    openReleaseDialog(row: VmCustomerSession): void {
        this.#matDialog.open<CustomerReleaseRecoverDialogComponent, {
            selectedSeat: VmCustomerSeasonTicketProduct;
            session: VmCustomerSession;
            action: ReleaseAction;
        }, TicketTransferData>(
            CustomerReleaseRecoverDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.selectedSeat,
                    session: row,
                    action: ReleaseAction.release
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(ticketTransferData => {
                if (ticketTransferData) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.RELEASE.RELEASE_DIALOG.RELEASE_OK'
                    });
                    this.#ticketsSrv.ticketDetail.get$()
                        .pipe(first())
                        .subscribe(ticketDetail => {
                            const { release_data: releaseData } = ticketDetail;
                            const sessionsResult = releaseData.sessions.map(session => {
                                if (session.session_id === row.session_id) {
                                    return {
                                        ...session,
                                        request_user: {
                                            type: SeatManagementDataRequestUserType.cpanel,
                                            customer_id: '',
                                            user_id: 0
                                        },
                                        status: ReleaseDataSessionStatus.released
                                    };
                                } else {
                                    return session;
                                }
                            });
                            this.#ticketsSrv.ticketDetail.set({
                                ...ticketDetail,
                                release_data: {
                                    ...releaseData,
                                    sessions: sessionsResult
                                }
                            });
                        });
                }
            });
    }

    openRecoverDialog(row: VmCustomerSession): void {
        this.#matDialog.open<CustomerReleaseRecoverDialogComponent, {
            selectedSeat: VmCustomerSeasonTicketProduct;
            session: VmCustomerSession;
            action: ReleaseAction;
        }, boolean>(
            CustomerReleaseRecoverDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.selectedSeat,
                    session: row,
                    action: ReleaseAction.recover
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(success => {
                if (success) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.RELEASE.RECOVER_DIALOG.RECOVER_OK'
                    });
                    this.#ticketsSrv.ticketDetail.get$()
                        .pipe(first())
                        .subscribe(ticketDetail => {
                            const { release_data: releaseData } = ticketDetail;
                            const sessionsResult = releaseData.sessions.map(session => {
                                if (session.session_id === row.session_id) {
                                    return {
                                        ...session,
                                        request_user: undefined,
                                        status: ReleaseDataSessionStatus.recovered
                                    };
                                } else {
                                    return session;
                                }
                            });
                            this.#ticketsSrv.ticketDetail.set({
                                ...ticketDetail,
                                release_data: {
                                    ...releaseData,
                                    sessions: sessionsResult
                                }
                            });
                        });
                }
            });
    }

    private initRequest(): void {
        this._request = {
            limit: SESSIONS_PAGE_SIZE,
            offset: 0,
            sort: `${this.initSortCol}:${this.initSortDir}`,
            status: SeasonTicketSessionStatus.assigned
        };
        this.requestChanged.emit(this._request);
    }

    private metaDataChangeHandler(): void {
        this.#seasonTicketSessionSrv.sessions.getMetadata$()
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(metadata => this.setMatPaginator(metadata));
    }

    private tableChangeHandler(): void {
        this._matSort.sortChange
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(sortChange => {
                this._matPaginator.firstPage();
                this._request = {
                    ...this._request,
                    offset: 0,
                    sort: `${sortChange.active}:${sortChange.direction}`
                };
                this.requestChanged.emit(this._request);
            });

        this._matPaginator.page
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(value => {
                const offset = value.pageIndex * value.pageSize;
                this._request = {
                    ...this._request,
                    offset
                };
                this.requestChanged.emit(this._request);
            });
    }

    private setMatPaginator(metadata: Metadata): void {
        if (metadata) {
            this._matPaginator.pageIndex = Math.floor(metadata?.offset / metadata?.limit);
            this._matPaginator.pageSize = metadata?.limit;
            this._matPaginator.length = metadata?.total;
        }
    }
}

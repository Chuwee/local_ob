import { Metadata } from '@OneboxTM/utils-state';
import { SeasonTicketsService, SeasonTicketStatus } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { GetSeasonTicketSessionsRequest, SeasonTicketSessionsService, SeasonTicketSessionStatus }
    from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { CustomersService, VmCustomerSeasonTicketProduct, VmCustomerSession }
    from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    TransferDataSessionDeliveryMethod, TransferDataSessionStatus, TicketTransferData, TicketsBaseService,
    ReleaseDataSessionStatus, SeatManagementDataRequestUserType
} from '@admin-clients/shared/common/data-access';
import { DialogSize, EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig }
    from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnDestroy, OnInit, Output, ViewChild, ViewContainerRef }
    from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, SortDirection } from '@angular/material/sort';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { combineLatest, Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil } from 'rxjs/operators';
import { CustomerTransferRecoverDialogComponent } from '../../../common/transfer-dialogs/recover-dialog/customer-transfer-recover-dialog.component';
import { CustomerTransferResendDialogComponent } from '../../../common/transfer-dialogs/resend-dialog/customer-transfer-resend-dialog.component';
import { CustomerTransferTransferDialogComponent } from '../../../common/transfer-dialogs/transfer-dialog/customer-transfer-transfer-dialog.component';

const SESSIONS_PAGE_SIZE = 10;

@Component({
    selector: 'app-customer-transfer-season-tickets-session-list',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MatPaginatorModule, AsyncPipe, TranslatePipe, DateTimePipe,
        MatButtonModule, MatMenuModule, NgClass, MatProgressSpinnerModule, MatIconModule,
        MaterialModule, EmptyStateTinyComponent, EllipsifyDirective
    ],
    styleUrls: ['./customer-transfer-season-tickets-session-list.component.scss'],
    templateUrl: './customer-transfer-season-tickets-session-list.component.html'
})
export class CustomerTransferSeasonTicketsSessionListComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject();
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #viewCrf = inject(ViewContainerRef);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #seasonTicketSessionSrv = inject(SeasonTicketSessionsService);
    readonly #translateService = inject(TranslateService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #customersSrv = inject(CustomersService);
    readonly #destroyRef = inject(DestroyRef);

    @ViewChild(MatSort, { static: true }) private readonly _matSort: MatSort;
    @ViewChild(MatPaginator, { static: true }) private readonly _matPaginator: MatPaginator;

    private _request: GetSeasonTicketSessionsRequest;

    readonly #$seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly #$customerFriends = toSignal(this.#customersSrv.customerFriendsList.getData$());

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
        this.#seasonTicketSrv.seasonTicket.get$()
    ]).pipe(
        map(([ticketDetail, sessions, seasonTicket]) => {
            const transferSessions: VmCustomerSession[] = [];
            if (ticketDetail && sessions) {
                const { transfer_data: transferData } = ticketDetail;
                const { release_data: releaseData } = ticketDetail;
                transferData.sessions.forEach(transferSession => {
                    if (!seasonTicket.settings.operative.allow_transfer && transferSession.status !== TransferDataSessionStatus.transferred) {
                        transferSession.status = TransferDataSessionStatus.noOperationAllowed;
                    }
                    const releaseSession = releaseData.sessions.find(releaseSession => releaseSession.session_id === transferSession.session_id);
                    if (releaseSession.status === ReleaseDataSessionStatus.released) {
                        transferSession.status = TransferDataSessionStatus.released;
                    }

                    transferSession.showRecover = transferSession.status === TransferDataSessionStatus.transferred;

                    const index = sessions.findIndex(e => e.session_id === transferSession.session_id);
                    if (index !== -1) {
                        transferSessions[index] = { ...sessions[index], transferSession };
                        const today = new Date();
                        transferSessions[index].transferSession.showTransfer = seasonTicket.settings.operative.allow_transfer &&
                            new Date(transferSessions[index].session_starting_date) > today && transferSession.status === TransferDataSessionStatus.inSeason;
                    }
                });
            }
            return transferSessions;
        }),
        shareReplay(1)
    );

    readonly seasonTicketTransferStatus = TransferDataSessionStatus;
    readonly initSortCol = 'session_name';
    readonly initSortDir: SortDirection = 'asc';
    readonly displayedColumns = [
        'session_name',
        'session_starting_date',
        'transferred_to',
        'delivery_method',
        'transfer_date',
        'transferred_by',
        'session_status',
        'actions',
        'transfer_actions'
    ];

    @Input() selectedSeat: VmCustomerSeasonTicketProduct;
    @Output() requestChanged = new EventEmitter<GetSeasonTicketSessionsRequest>();

    ngOnInit(): void {
        this.#loadCustomerFriends();
        this.initRequest();
        this.metaDataChangeHandler();
        this.tableChangeHandler();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    openTransferDialog(row: VmCustomerSession): void {
        this.#matDialog.open<CustomerTransferTransferDialogComponent, {
            selectedSeat: VmCustomerSeasonTicketProduct;
            session: VmCustomerSession;
        }, TicketTransferData>(
            CustomerTransferTransferDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.selectedSeat,
                    session: row,
                    transferPolicy: 'ALL', // Forcing ALL for now as per requirements
                    friends: this.#$customerFriends()
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(ticketTransferData => {
                if (ticketTransferData) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.TRANSFER_DIALOG.TRANSFER_OK'
                    });
                    this.#ticketsSrv.ticketDetail.get$()
                        .pipe(first())
                        .subscribe(ticketDetail => {
                            const { transfer_data: transferData } = ticketDetail;
                            const sessionsResult = transferData.sessions.map(session => {
                                if (session.session_id === row.session_id) {
                                    return {
                                        ...session,
                                        request_user: {
                                            type: SeatManagementDataRequestUserType.cpanel,
                                            customer_id: '',
                                            user_id: 0
                                        },
                                        delivery_method: ticketTransferData.email ?
                                            TransferDataSessionDeliveryMethod.email :
                                            TransferDataSessionDeliveryMethod.download,
                                        data: {
                                            name: ticketTransferData.name,
                                            surname: ticketTransferData.surname,
                                            email: ticketTransferData.email,
                                            date: moment.utc(moment.now()).toString()
                                        },
                                        status: TransferDataSessionStatus.transferred
                                    };
                                } else {
                                    return session;
                                }
                            });
                            this.#ticketsSrv.ticketDetail.set({
                                ...ticketDetail,
                                transfer_data: {
                                    ...transferData,
                                    sessions: sessionsResult
                                }
                            });
                        });
                    if (!ticketTransferData.email) {
                        this.downloadTickets(row);
                    }
                }
            });
    }

    openRecoverDialog(row: VmCustomerSession): void {
        this.#matDialog.open<CustomerTransferRecoverDialogComponent, {
            selectedSeat: VmCustomerSeasonTicketProduct;
            session: VmCustomerSession;
        }, boolean>(
            CustomerTransferRecoverDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.selectedSeat,
                    session: row
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(success => {
                if (success) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.RECOVER_DIALOG.RECOVER_OK'
                    });
                    this.#ticketsSrv.ticketDetail.get$()
                        .pipe(first())
                        .subscribe(ticketDetail => {
                            const { transfer_data: transferData } = ticketDetail;
                            const sessionsResult = transferData.sessions.map(session => {
                                if (session.session_id === row.session_id) {
                                    return {
                                        ...session,
                                        request_user: undefined,
                                        delivery_method: undefined,
                                        data: undefined,
                                        status: TransferDataSessionStatus.inSeason
                                    };
                                } else {
                                    return session;
                                }
                            });
                            this.#ticketsSrv.ticketDetail.set({
                                ...ticketDetail,
                                transfer_data: {
                                    ...transferData,
                                    sessions: sessionsResult
                                }
                            });
                        });
                }
            });
    }

    downloadTickets(session: VmCustomerSession): void {
        const orderCode = this.selectedSeat.order.code;
        const sessionId = session.session_id;
        const itemId = this.selectedSeat.id;

        this.#ticketsSrv.ticketTransfer.getPdf$(orderCode, itemId, sessionId)
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                } else {
                    const title = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogService.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }

    openResendEmailDialog(row: VmCustomerSession): void {
        this.#matDialog.open(
            CustomerTransferResendDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.selectedSeat,
                    session: row
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(success => {
                if (success) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.RESEND_DIALOG.SENT_OK'
                    });
                }
            });
    }

    #loadCustomerFriends(): void {
        this.#customersSrv.customer.get$()
            .pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(customer => {
                this.#customersSrv.customerFriendsList.load(customer.id);
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


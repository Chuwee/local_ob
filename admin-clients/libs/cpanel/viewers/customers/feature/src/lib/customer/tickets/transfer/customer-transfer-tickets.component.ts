import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { CustomersService, VmCustomerSession } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EventType, GetTicketsRequest, OrderItemDetails, TicketsBaseService, TicketState } from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, ViewChild, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { delay, filter, finalize, first, map, Subject, tap } from 'rxjs';
import { CustomerTransferRecoverDialogComponent } from '../../common/transfer-dialogs/recover-dialog/customer-transfer-recover-dialog.component';
import { CustomerTransferResendDialogComponent } from '../../common/transfer-dialogs/resend-dialog/customer-transfer-resend-dialog.component';
import { CustomerTransferTransferDialogComponent } from '../../common/transfer-dialogs/transfer-dialog/customer-transfer-transfer-dialog.component';
import { CustomerTicketsState } from '../customer-tickets.state';

const TICKETS_PAGE_SIZE = 10;

@Component({
    selector: 'app-customer-transfer-tickets',
    templateUrl: './customer-transfer-tickets.component.html',
    styleUrls: ['./customer-transfer-tickets.component.scss'],
    imports: [MatIcon, TranslatePipe, MatFormFieldModule, MatSelectModule, ReactiveFormsModule,
        MatTableModule, MatSortModule, MatPaginatorModule, DateTimePipe, MatTooltipModule, EmptyStateTinyComponent,
        FormContainerComponent, MatMenuModule, MatButton, MatIconButton, MatSpinner, RouterLink
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTransferTicketsComponent {

    readonly #customersSrv = inject(CustomersService);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #eventsSrv = inject(EventsService);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #viewCrf = inject(ViewContainerRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #matDialog = inject(MatDialog);
    readonly #customerTicketsState = inject(CustomerTicketsState);

    @ViewChild(MatSort, { static: true }) private readonly _matSort: MatSort;
    @ViewChild(MatPaginator, { static: true }) private readonly _matPaginator: MatPaginator;

    readonly #$routeData = toSignal(this.#route.data);
    readonly #$sessionIdParam = toSignal(this.#route.parent?.paramMap.pipe(map(params => params.get('sessionId'))));
    readonly #$selectedEvent = toSignal(this.#eventsSrv.event.get$().pipe(filter(Boolean)));
    readonly #$selectedProducts = toSignal(this.#customerTicketsState.selectedProducts.getValue$());
    readonly #$customerFriends = toSignal(this.#customersSrv.customerFriendsList.getData$());
    readonly #isTransferOrRecoverLoadingSubj$ = new Subject<boolean>();
    readonly #isTransferOrRecoverLoading$ = this.#isTransferOrRecoverLoadingSubj$.asObservable();
    readonly #$isTransferEnabledInSelectedSession = computed(() => !!this.#$selectedEvent()?.settings?.transfer_settings?.enabled &&
        (!this.#$selectedEvent()?.settings?.transfer_settings?.restrict_transfer_by_sessions ||
            this.#$selectedEvent()?.settings?.transfer_settings?.allowed_transfer_sessions?.includes(Number(this.#$sessionIdParam()))));

    readonly #$filteredProducts = computed(() => {
        const products = this.#$selectedProducts() || [];
        const customerId = this.$currentCustomer()?.id;
        return products.filter(item => {
            const receiverId = item.transfer?.receiver?.customer_id;
            return this.$onlyTransferredToCustomer()
                ? !!receiverId && receiverId === customerId
                : receiverId !== customerId;
        });
    });

    readonly $onlyTransferredToCustomer = computed(() => !!this.#$routeData()?.['onlyTransferredToCustomer']);
    readonly $currentCustomer = toSignal(this.#customersSrv.customer.get$().pipe(filter(Boolean)));
    readonly $isEventStatusReady = computed(() => this.#$selectedEvent()?.status === EventStatus.ready);
    readonly $isMultipleTransfersEnabled = computed(() => this.#$selectedEvent()?.settings?.transfer_settings?.enable_multiple_transfers);
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#isTransferOrRecoverLoading$,
        this.#ticketsSrv.ticketList.loading$(),
        this.#eventsSrv.event.inProgress$()
    ]));

    readonly $dataSource = computed(() => {
        const dataSource = new MatTableDataSource<OrderItemDetails>(this.#$filteredProducts() || []);
        dataSource.sort = this._matSort;
        dataSource.paginator = this._matPaginator;
        dataSource.paginator.pageSize = TICKETS_PAGE_SIZE;
        return dataSource;
    });

    readonly dateTimeFormats = DateTimeFormats;
    readonly initSortCol = 'allocation';
    readonly initSortDir: SortDirection = 'asc';
    readonly $displayedColumns = computed(() => {
        const columns = ['allocation', 'rate', 'transferred_to', 'transfer_date',
            'transferred_by', 'transferred_count', 'session_status', 'actions', 'transfer_actions'];
        if (this.$onlyTransferredToCustomer()) {
            columns.splice(columns.indexOf('transferred_to'), 1);
            columns.splice(columns.indexOf('session_status'), 1);
            if (!this.$isMultipleTransfersEnabled()) {
                columns.splice(columns.indexOf('actions'), 1);
                columns.splice(columns.indexOf('transferred_count'), 1);
            }
        }
        return columns;
    });

    constructor() {
        this.#loadCustomerFriends();
        effect(() => {
            if (!!this.#$selectedEvent() && (!this.#$isTransferEnabledInSelectedSession() || !this.#$filteredProducts()?.length)) {
                this.#router.navigate(['../general-data'], { relativeTo: this.#route });
            }
        });
    }

    openTransferDialog(row: OrderItemDetails): void {
        this.#matDialog.open<CustomerTransferTransferDialogComponent>(
            CustomerTransferTransferDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.#mapOrderItemToItemWithNewSeatName(row),
                    session: this.#getDialogSession(row, false),
                    emailSubmit: true,
                    friends: this.#$customerFriends(),
                    transferPolicy: 'ALL' // Forcing ALL for now as per requirements
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .pipe(
                tap(success => success && this.#isTransferOrRecoverLoadingSubj$.next(true)),
                delay(1000), // wait for backend to process the transfer
                finalize(() => this.#isTransferOrRecoverLoadingSubj$.next(false))
            )
            .subscribe(ticketTransferData => {
                if (ticketTransferData) {
                    this.#loadTicketsList();
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.TRANSFER_DIALOG.TRANSFER_SUCCESS'
                    });
                }
            });
    }

    openRecoverDialog(row: OrderItemDetails): void {
        this.#matDialog.open<CustomerTransferRecoverDialogComponent>(
            CustomerTransferRecoverDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.#mapOrderItemToItemWithNewSeatName(row),
                    session: this.#getDialogSession(row)
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .pipe(
                tap(success => success && this.#isTransferOrRecoverLoadingSubj$.next(true)),
                delay(1000), // wait for backend to process the recover
                finalize(() => this.#isTransferOrRecoverLoadingSubj$.next(false))
            )
            .subscribe(success => {
                if (success) {
                    this.#loadTicketsList();
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.RECOVER_DIALOG.RECOVER_SUCCESS'
                    });
                }
            });
    }

    openResendEmailDialog(row: OrderItemDetails): void {
        this.#matDialog.open(
            CustomerTransferResendDialogComponent,
            new ObMatDialogConfig(
                {
                    selectedSeat: this.#mapOrderItemToItemWithNewSeatName(row),
                    session: this.#getDialogSession(row)
                },
                this.#viewCrf
            )
        )
            .beforeClosed()
            .subscribe(success => {
                if (success) {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'CUSTOMER.TRANSFER.RESEND_DIALOG.RESEND_SUCCESS'
                    });
                }
            });
    }

    #getDialogSession(row: OrderItemDetails, withUserData = true): VmCustomerSession {
        return {
            event_id: row.ticket.allocation.event.id,
            session_id: row.ticket.allocation.session.id,
            session_name: row.ticket.allocation.session.name,
            event_name: row.ticket.allocation.event.name,
            status: null,
            session_assignable: null,
            session_starting_date: row.ticket.allocation.session.date.start,
            transferSession: {
                session_id: row.ticket.allocation.session.id,
                seat_id: row.ticket.allocation.seat.id,
                ...(withUserData && {
                    data: {
                        name: row.transfer?.receiver?.name,
                        surname: row.transfer?.receiver?.surname,
                        email: row.transfer?.receiver?.email,
                        date: row.transfer?.receiver?.date
                    }
                })
            }
        };
    }

    #loadCustomerFriends(): void {
        this.#customersSrv.customer.get$()
            .pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(customer => {
                this.#customersSrv.customerFriendsList.load(customer.id);
            });
    }

    #loadTicketsList(): void {
        this.#customersSrv.customer.get$()
            .pipe(first())
            .subscribe(customer => {
                const req: GetTicketsRequest = {
                    customer_id: customer.id,
                    event_type: EventType.normal,
                    state: TicketState.purchase
                };
                this.#ticketsSrv.ticketList.load(req);
            });
    }

    #mapOrderItemToItemWithNewSeatName(orderItem: OrderItemDetails): OrderItemDetails {
        return {
            ...orderItem,
            ticket: {
                ...orderItem.ticket,
                allocation: {
                    ...orderItem.ticket.allocation,
                    seat: {
                        ...orderItem.ticket.allocation.seat,
                        name: `${orderItem.ticket.allocation.seat.name} - ${orderItem.ticket.rate.name}`
                    }
                }
            }
        };
    }

}
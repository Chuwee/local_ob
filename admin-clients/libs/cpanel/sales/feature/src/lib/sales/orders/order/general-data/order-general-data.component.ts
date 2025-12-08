import {
    OrderDeliveryType, OrderDetailEvent, OrderDetailSession, OrderDetailTicket, OrderDetail, RefundRequest,
    ReimbursementAction, OrdersService
} from '@admin-clients/cpanel-sales-data-access';
import {
    OrderItem, OrderType, ActionsHistoryType, TicketAllocationEvent, TicketAllocationSession,
    TicketAllocationType, TicketDetailState, EventType, OrderItemType
} from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { SelectionModel } from '@angular/cdk/collections';
import { ChangeDetectionStrategy, Component, effect, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { delay, filter, finalize, map, shareReplay, switchMap, take, takeWhile, tap } from 'rxjs/operators';
import { ChangeSeatDialogComponent } from './change-seat/change-seat-link.component';
import { RefundOrderDialogComponent } from './refund/refund-order-dialog.component';
import { ResendOrderDialogComponent } from './resend/resend-order-dialog.component';
import {
    ResendInvoiceDialogComponent
} from './resend-invoice/resend-invoice-dialog.component';

const ordersTypesWithValidation = [OrderType.purchase, OrderType.secMktPurchase, OrderType.issue];

@Component({
    selector: 'app-order-general-data',
    templateUrl: './order-general-data.component.html',
    styleUrls: ['./order-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderGeneralDataComponent implements OnDestroy {
    readonly #router = inject(Router);
    readonly #routingState = inject(RoutingState);
    readonly #ordersService = inject(OrdersService);
    readonly #translateService = inject(TranslateService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #dialog = inject(MatDialog);
    #itemIds: RefundRequest['items'];
    #internationalPhone: { number: string; prefix: string };
    #orderWasSentByWhatsapp: boolean;

    isAllowedPartialRefund = false;
    canOrderTypeHaveValidations: boolean;
    isSeatReallocationOrder: boolean;
    orderCode: string;

    readonly orderTypes = OrderType;
    readonly ticketStates = TicketDetailState;
    readonly dateTimeFormats = DateTimeFormats;
    readonly selections = new Map<number, SelectionModel<OrderDetailTicket>>();
    readonly selectedProducts = new SelectionModel<OrderItem>(true, []);

    readonly loading$ = booleanOrMerge([
        this.#ordersService.isOrderDetailLoading$(),
        this.#ordersService.isResendOrderLoading$(),
        this.#ordersService.isCancelOrderLoading$(),
        this.#ordersService.isRegenerateOrderLoading$(),
        this.#ordersService.isTicketsLinkLoading$(),
        this.#ordersService.isOrderReloading$(),
        this.#ordersService.changeSeat.loading$()
    ]);

    readonly orderDetail$ = this.#ordersService.getOrderDetail$().pipe(
        filter(Boolean),
        tap(orderDetail => {
            if (orderDetail) {
                this.orderCode = orderDetail.code;
                this.#itemIds = orderDetail.items.map(elem => ({ id: elem.id }));
                this.isAllowedPartialRefund = this.setIsAllowedPartialRefund(orderDetail);
                this.canOrderTypeHaveValidations = ordersTypesWithValidation.includes(orderDetail.type);
                this.#orderWasSentByWhatsapp = orderDetail.delivery === OrderDeliveryType.whatsapp;
                this.#internationalPhone = orderDetail.buyer_data.international_phone;
                this.isSeatReallocationOrder = orderDetail.type === OrderType.seatReallocation;
            }
            this.setOrderChangeSeat(orderDetail);
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly $orderChangeSeat = toSignal(
        this.#ordersService.changeSeat.getData$(),
        { initialValue: null }
    );

    readonly enableChangeSeatControl = new FormControl<boolean>(false, { nonNullable: true });

    readonly changeSeatControlEffect = effect(() =>
        this.enableChangeSeatControl.setValue(!!this.$orderChangeSeat()?.enabled));

    readonly $productItems = toSignal(
        this.orderDetail$.pipe(map(orderDetail => orderDetail?.items.filter(item => item.type === OrderItemType.product && !item.pack)))
    );

    readonly $packItems = toSignal(this.orderDetail$.pipe(map(orderDetail => orderDetail?.items.filter(item => item.pack))));

    readonly $packGroups = toSignal(
        this.orderDetail$.pipe(
            map(orderDetail => orderDetail?.items.filter(item => item.pack)),
            map(packItems => {
                const groupedPackItems = packItems?.reduce<{ [key: string]: OrderItem[] }>((result, item) => ({
                    ...result,
                    [item.pack.id]: [...(result[item.pack.id] || []), item]
                }), {});

                const packIds = Object.keys(groupedPackItems);
                const preparedPacks = [];

                packIds.forEach(id => {
                    const totalPacks = groupedPackItems[id]?.reduce<{ [key: string]: OrderItem[] }>((result, item) => ({
                        ...result,
                        [item.pack.code]: [...(result[item.pack.code] || []), item]
                    }), {});

                    const numberOfPacks = Object.keys(totalPacks).length;

                    const packSessions = groupedPackItems[id].filter(item => item.type === 'SEAT');
                    const sessions = this.preparePackSessionsData(packSessions);
                    const products = groupedPackItems[id].filter(item => item.type === 'PRODUCT');
                    preparedPacks.push({
                        id, pack: { name: sessions[0].sessions[0].pack.name }, sessions, products, packCount: numberOfPacks
                    });
                });

                return preparedPacks;
            })
        )
    );

    readonly events$ = this.prepareEventsData$(this.orderDetail$);
    readonly disableActionButton: Record<string, boolean> = {};

    readonly $ticketsCount = toSignal(
        this.orderDetail$.pipe(map(orderDetail =>
            orderDetail?.tickets_count - orderDetail?.items.filter(item => item.type !== OrderItemType.product && item.pack).length)
        )
    );

    ngOnDestroy(): void {
        this.#ordersService.changeSeat.clear();
    }

    get isRefundActive(): boolean {
        return Array.from(this.selections.values()).some(selection => selection.hasValue()) || this.selectedProducts.hasValue();
    }

    openResendDialog(buyerEmail: string): void {
        this.#dialog.open(ResendOrderDialogComponent, new ObMatDialogConfig({
            code: this.orderCode,
            buyerEmail,
            canResendWhatsapp: this.#orderWasSentByWhatsapp,
            internationalPhone: this.#internationalPhone
        }))
            .beforeClosed()
            .pipe(filter(done => done))
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                this.reloadOrderWithTimeout();
            });
    }

    openResendInvoiceDialog(buyerEmail: string): void {
        this.#dialog.open(ResendInvoiceDialogComponent, new ObMatDialogConfig({ code: this.orderCode, buyerEmail }))
            .beforeClosed()
            .pipe(filter(done => done))
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                this.reloadOrderWithTimeout();
            });
    }

    openChangeSeatDialog(): void {
        this.#dialog.open(ChangeSeatDialogComponent, new ObMatDialogConfig());
    }

    openRefundDialog(): void {
        let items: RefundRequest['items']; // { id: number; subitem_ids?: number[]; }[];
        if (this.isAllowedPartialRefund) {
            items = Array.from(this.selections.values()).reduce((items: RefundRequest['items'], selection) => {
                const itemsToPush = selection.selected.reduce((acc: RefundRequest['items'], ticket) => {
                    // creates the parent element or the element itself
                    if (!acc.find(elem => elem.id === (ticket.parentId || ticket.item.id))) {
                        acc.push({ id: ticket.parentId || ticket.item.id });
                    }
                    // if has parent, makes sure that subitem list is inited
                    const parentItem = !!ticket.parentId && acc.find(elem => elem.id === ticket.parentId);
                    if (parentItem && !parentItem.subitem_ids) {
                        parentItem.subitem_ids = [];
                    }
                    // if it is not a subitem element (parent or single item)
                    if (!ticket.parentId) {
                        // and has subitems, searches and pushes its subitems
                        if (ticket.item.subitems) {
                            acc.forEach(elem => {
                                if (elem.id === ticket.item.id) {
                                    elem.subitem_ids = ticket.item.subitems.map(subitem => subitem.id);
                                }
                            });
                        }
                    } else { // is a subitem
                        // pushes itself to an unselected parent item
                        if (ticket.item?.id && parentItem?.subitem_ids && !parentItem.subitem_ids.includes(ticket.item.id)) {
                            parentItem.subitem_ids.push(ticket.item.id);
                        }
                    }
                    return acc;
                }, []);
                items.push(...itemsToPush);
                return items;
            }, []);

            const productItemsToPush = this.selectedProducts.selected.reduce((acc: RefundRequest['items'], product) => {
                acc.push({ id: product?.id });
                return acc;
            }, []);

            if (productItemsToPush) {
                items.push(...productItemsToPush);
            }
        } else {
            items = this.#itemIds;
        }
        if (items?.length) {
            this.#dialog.open(RefundOrderDialogComponent, new ObMatDialogConfig({ items, totalItems: this.#itemIds.length }))
                .beforeClosed()
                .subscribe((result: { reimbursementKO: boolean; refunded: boolean }) => {
                    if (result.reimbursementKO && !result.refunded) {
                        this.reloadOrderWithTimeout();
                    } else if (result.refunded) {
                        this.reloadUntil(orderDetail => orderDetail?.items.every(item =>
                            !items.find(elem => elem.id === item.id) || item.state === TicketDetailState.refunded));
                        this.selectedProducts.clear();
                    }
                });
        }
    }

    showTickets(): void {
        this.#ordersService.getTicketsLink$(this.orderCode)
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                    this.reloadOrderWithTimeout();
                } else {
                    const title = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogService.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }

    refreshExternalPermissions(): void {
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'ORDER.RESEND_PERMISSIONS.TITLE',
            message: 'ORDER.RESEND_PERMISSIONS.MESSAGE',
            actionLabel: 'FORMS.ACTIONS.OK',
            messageParams: { orderCode: this.orderCode },
            showCancelButton: true
        })
            .pipe(
                filter(response => !!response),
                switchMap(() =>
                    this.#ordersService.refreshExternalPermissions(this.orderCode)
                ),
                take(1)
            )
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                this.disableActionButton['resend_permissions'] = true;
            });
    }

    regenerateOrder(): void {
        this.#ordersService.regenerateOrder(this.orderCode)
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                this.reloadOrderWithTimeout();
            });
    }

    cancelOrder(): void {
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'ORDER.CANCEL.TITLE',
            message: 'ORDER.CANCEL.MESSAGE',
            actionLabel: 'FORMS.ACTIONS.OK',
            messageParams: { orderCode: this.orderCode },
            showCancelButton: true
        })
            .pipe(
                filter(response => !!response),
                switchMap(() => this.#ordersService.cancelOrder(this.orderCode)),
                take(1)
            )
            .subscribe(() => {
                this.#ordersService.setOrderReloading(true);
                this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                setTimeout(() => {
                    this.#ordersService.setOrderReloading(false);
                    this.#router.navigate([this.#routingState.getPreviousPath('transactions', '/tickets')],
                        { queryParams: this.#routingState.getPreviousQueryParams('transactions', '/tickets') });
                }, 1500);
            });
    }

    onAvailabilityChangeSeatChange(enabled: boolean, code: string): void {
        this.#ordersService.changeSeat.setEnabled$(code, enabled).pipe(take(1)).subscribe({
            next: () => {
                this.#ephemeralMessageService.showSaveSuccess();
                this.#ordersService.changeSeat.load(code);
            },
            error: () => this.#ordersService.changeSeat.load(code)
        });
    }

    private prepareEventsData$(orderDetail$: Observable<OrderDetail>): Observable<OrderDetailEvent[]> {
        return orderDetail$.pipe(
            map(orderDetails =>
                orderDetails.items.filter(order =>
                    order.type !== OrderItemType.product && !order.pack
                )
            ),
            map(filteredItems =>
                filteredItems.reduce((result, orderItem) => {
                    const eventData = this.getOrCreateEventData(result, orderItem.ticket.allocation.event);
                    const sessionData = this.getOrCreateSessionData(eventData.sessions, orderItem.ticket.allocation.session);
                    sessionData.tickets.push(this.prepareTicketData(orderItem));
                    sessionData.refundAllowed = sessionData.refundAllowed || orderItem.action.refund || orderItem.action.partial_refund;
                    const actionHistory = sessionData.tickets[0].item.actions_history;
                    sessionData.isSkipGeneration = actionHistory?.sort((a1, a2) => {
                        if (a1.date < a2.date) { return 1; } else if (a1.date > a2.date) { return -1; } else { return 0; }
                    })[0].type === ActionsHistoryType.skipGeneration;
                    return result;
                }, [])
            )
        );
    }

    private preparePackSessionsData(packItems: OrderItem[]): OrderDetailEvent[] {
        return packItems?.reduce((result, orderItem) => {
            const eventData = this.getOrCreateEventData(result, orderItem.ticket.allocation.event);
            const sessionData = this.getOrCreateSessionData(eventData.sessions, orderItem.ticket.allocation.session);
            sessionData.tickets.push(this.prepareTicketData(orderItem));
            sessionData.refundAllowed = sessionData.refundAllowed || orderItem.action.refund || orderItem.action.partial_refund;
            const actionHistory = sessionData.tickets[0].item.actions_history;
            sessionData.isSkipGeneration = actionHistory?.sort((a1, a2) => {
                if (a1.date < a2.date) { return 1; } else if (a1.date > a2.date) { return -1; } else { return 0; }
            })[0].type === ActionsHistoryType.skipGeneration;
            sessionData.pack = orderItem.pack;
            return result;
        }, []);
    }

    private getOrCreateEventData(events: OrderDetailEvent[], eventItem: TicketAllocationEvent): OrderDetailEvent {
        let eventData = events.find(it => it.event.id === eventItem.id);
        if (!eventData) {
            eventData = {
                event: eventItem,
                sessions: []
            };
            events.push(eventData);
        }
        return eventData;
    }

    private getOrCreateSessionData(sessions: OrderDetailSession[], sessionItem: TicketAllocationSession): OrderDetailSession {
        let sessionData = sessions.find(it => it.session.id === sessionItem.id);
        if (!sessionData) {
            sessionData = {
                session: sessionItem,
                tickets: [],
                refundAllowed: false,
                isSkipGeneration: false
            };
            sessions.push(sessionData);
            this.selections.set(sessionItem.id, new SelectionModel<OrderDetailTicket>(true, []));
        }
        return sessionData;
    }

    private prepareTicketData(orderItem: OrderItem): OrderDetailTicket {
        let ticketText;
        const allocation = orderItem.ticket.allocation;
        if (allocation.type === TicketAllocationType.numbered) {
            ticketText = this.concatNames([allocation.sector, allocation.row, allocation.seat]);
        } else if (allocation.event.type === EventType.activity || allocation.event.type === EventType.themePark) {
            ticketText = this.concatNames([allocation.price_type]);
        } else {
            ticketText = this.concatNames([allocation.sector, allocation.not_numbered_area]);
        }
        return {
            text: ticketText,
            item: orderItem
        };
    }

    private concatNames(objects: { name?: string }[]): string {
        return objects.reduce((result, obj) => {
            if (obj?.name) {
                if (result) {
                    result += ' ';
                }
                result += obj.name;
            }
            return result;
        }, '');
    }

    private reloadOrderWithTimeout(): void {
        //We have to wait our backend register the new action
        this.#ordersService.setOrderReloading(true);
        setTimeout(() => {
            this.#ordersService.setOrderReloading(false);
            this.#ordersService.loadOrderDetail(this.orderCode);
        }, 500);
    }

    private reloadUntil(until: (orderDetail: OrderDetail) => boolean): void {
        this.#ordersService.setOrderReloading(true);
        this.#ordersService.getOrderDetail$().pipe(
            takeWhile(orderDetail => !until(orderDetail)),
            take(5),
            delay(500),
            finalize(() => this.#ordersService.setOrderReloading(false))
        ).subscribe(() => this.#ordersService.loadOrderDetail(this.orderCode));
    }

    private setIsAllowedPartialRefund(orderDetail: OrderDetail): boolean {
        return orderDetail.payment_detail?.reimbursement_constraints == null ||
            orderDetail.payment_detail.reimbursement_constraints.actions !== ReimbursementAction.total;
    }

    private setOrderChangeSeat(orderDetail: OrderDetail | null): void {
        this.#ordersService.changeSeat.clear();

        if (orderDetail?.action?.change_seat) {
            this.#ordersService.changeSeat.load(orderDetail.code);
        }
    }
}

import { OrderDetailSession, OrderDetailTicket } from '@admin-clients/cpanel-sales-data-access';
import {
    OrderSubItem, TicketDetailState, TicketType, EventType, PriceCharges,
    ActionsHistoryType, OrderItem, OrderType
} from '@admin-clients/shared/common/data-access';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { ObfuscatePattern } from '@admin-clients/shared/utility/pipes';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';

export const REALLOCATION_REFUND = 'REALLOCATION_REFUND';

export type NavigationStateKey = OrderType | TicketDetailState | typeof REALLOCATION_REFUND;

interface OrderDetailNavigation {
    type: 'order' | 'customer';
    translationKey: string;
    route: string[];
    url: string;
    orderCode?: string;
    customerId?: string;
}

interface OrderDetailTicketWithSeasonTicket extends OrderDetailTicket {
    isSubitemOfSeasonTicket?: boolean;
}

@Component({
    selector: 'app-order-details-tickets-table',
    templateUrl: './tickets-table.component.html',
    styleUrls: ['./tickets-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expand', [
            state('void', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
            state('*', style({ height: '*', visibility: 'visible' })),
            transition('void <=> *', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ],
    standalone: false
})
export class OrderDetailsTicketsTableComponent implements OnInit {
    readonly #router = inject(Router);

    readonly $sessionData = input.required<OrderDetailSession>({ alias: 'sessionData' });
    readonly $selection = input.required<Map<number, SelectionModel<OrderDetailTicket | OrderSubItem>>>({ alias: 'selection' });
    readonly $isAllowedPartialRefund = input.required<boolean>({ alias: 'isAllowedPartialRefund' });
    readonly $canHaveValidations = input.required<boolean>({ alias: 'canHaveValidations' });
    readonly $orderCode = input.required<string>({ alias: 'orderCode' });
    readonly $isSeatReallocationOrder = input.required<boolean>({ alias: 'isSeatReallocationOrder' });
    readonly $orderType = input.required<OrderType>({ alias: 'orderType' });
    readonly $originalReallocationCode = input<string | null>(null, { alias: 'originalReallocationCode' });

    readonly $relocatedItems = signal(new Set<number>());
    readonly $ticketInfoMap = computed(() => this.#buildTicketInfoMap(this.$sessionData().tickets));
    readonly $hasSeasonTicket = computed(() =>
        this.$sessionData().tickets.some(ticket => !ticket.parentId && this.#isSeasonTicket(ticket))
    );

    readonly $sessions = computed(() => {
        const sessions: Record<number, IdName> = {};
        this.$sessionData().tickets.forEach(ticket => {
            ticket.item?.subitems?.forEach(subitem => {
                if (subitem.state === TicketDetailState.purchased) {
                    sessions[subitem.session.id] = subitem.session;
                }
            });
        });
        return Object.values(sessions);
    });

    readonly $customerIdsDifferentFromOrder = computed(() => {
        const customerIds = new Set<string>();
        this.$sessionData().tickets.forEach(ticket => {
            if (!ticket.parentId && 'user_id' in ticket.item && ticket.item.user_id) {
                customerIds.add(ticket.item.user_id);
            }
        });
        return customerIds;
    });

    readonly invitation = TicketType.invitation;
    readonly obfuscatePattern = ObfuscatePattern;
    readonly eventType = EventType;
    readonly dateTimeFormats = DateTimeFormats;

    ticketsColumns = ['expand', 'selection', 'ticket', 'barcode', 'rate', 'price', 'print', 'validation', 'status', 'invitation', 'market',
        'relocation', 'icons'];

    get tickets(): OrderDetailTicket[] {
        return this.$sessionData().tickets;
    }

    get tableSelection(): SelectionModel<OrderDetailTicket | OrderSubItem> {
        return this.$selection().get(this.$sessionData().session.id);
    }

    ngOnInit(): void {
        let hasSomeTicketWithSubitems = false, hasSomeActionButton = false, hasTicketInvitation = false, hasSomeSecondaryMarket = false;
        const hasMultipleClients = this.$customerIdsDifferentFromOrder().size > 1;

        for (let index = 0; index < this.tickets?.length; index++) {
            const ticket = this.tickets[index] as OrderDetailTicketWithSeasonTicket;
            ticket.expandable = !!ticket.item?.subitems;
            ticket.expanded = false;
            ticket.isSubitemOfSeasonTicket = this.#isSubitemOfSeasonTicket(ticket);
            hasSomeActionButton = hasSomeActionButton || !!ticket.item.next_order || !!ticket.item.previous_order || hasMultipleClients;
            hasSomeTicketWithSubitems = hasSomeTicketWithSubitems || !!ticket.item?.subitems;
            hasTicketInvitation = hasTicketInvitation || ticket.item?.ticket?.type === TicketType.invitation;
            hasSomeSecondaryMarket = hasSomeSecondaryMarket || ticket.item?.origin_market === 'SECONDARY';
            ticket.item?.actions_history?.some(action => action?.type === ActionsHistoryType.relocated)
                && this.$relocatedItems().add(ticket.item.id);
            ticket.item.state = ticket.item.state === TicketDetailState.purchased &&
                ticket.item?.subitems?.some(subitem => subitem.state === TicketDetailState.refunded) ?
                TicketDetailState.partiallyRefunded : ticket.item.state;
            this.#insertSubitems(ticket, index + 1);
            const nextSubitemsOrders = ticket.item?.subitems
                ?.filter(elem => elem.next_order)
                ?.reduce((acc, elem) => (acc[elem.next_order.code] = elem.next_order, acc), {});
            nextSubitemsOrders && Object.values(nextSubitemsOrders)?.length > 1 ? ticket.hideRelatedOrders = true : null;
        }
        this.#filterColumn(!hasSomeTicketWithSubitems && !this.$isSeatReallocationOrder(), 'expand');
        this.#filterColumn(!hasSomeActionButton, 'icons');
        this.#filterColumn(!hasSomeSecondaryMarket, 'market');
        this.#filterColumn(!this.$relocatedItems().size, 'relocation');
        this.#filterColumn(!(this.$isAllowedPartialRefund() && this.$sessionData().refundAllowed), 'selection');
        this.#filterColumn(!hasTicketInvitation, 'invitation');

        if (this.$isSeatReallocationOrder()) {
            this.$sessionData().tickets = this.$sessionData().tickets.map(order => ({
                ...order,
                expandable: true,
                expanded: false,
                isSubitemOfSeasonTicket: this.#isSubitemOfSeasonTicket(order),
                item: {
                    ...order.item,
                    price: {
                        ...order.item.price,
                        total_charges: this.#computeTotalCharges(order.item.price.charges)
                    },
                    ticket: {
                        ...order.item.ticket,
                        previous_allocation: {
                            ...order.item.ticket?.previous_allocation,
                            price: {
                                ...order.item.ticket?.previous_allocation?.price,
                                total_charges: this.#computeTotalCharges(order.item.ticket?.previous_allocation?.price.charges)
                            }
                        }
                    }
                }
            } as OrderDetailTicketWithSeasonTicket));
        }
    }

    selectSubitemsBySessionId(sessions: number[]): void {
        const selection = this.tableSelection;
        selection.clear();
        this.tickets.forEach(ticket => {
            this.#findSubitemRows(ticket)?.forEach(subitem => {
                subitem.item.action.refund
                    && sessions.includes((subitem.item as OrderSubItem).session.id)
                    && selection.select(subitem);
            });
        });
    }

    // toggles visibility of subitems
    toggleSubitems(ticket: OrderDetailTicket): void {
        ticket.expanded = !ticket.expanded;
        this.#findSubitemRows(ticket)?.forEach(subitem => {
            subitem.hidden = !subitem.hidden;
        });
    }

    toggleChangeSeatSubitems(ticket: OrderDetailTicket): void {
        ticket.expanded = !ticket.expanded;
    }

    // selects or deselects a ticket and it's subitems, updating it's parent
    toggleTicket(ticket: OrderDetailTicket): void {
        const selection = this.tableSelection;
        if (selection && ticket.item.action?.refund || ticket.item.action?.partial_refund) {
            const hasSubitemsSelected = this.areSomeSubitemsSelected(ticket);
            ticket.item.action?.refund && selection.toggle(ticket);
            const isSelected = selection?.isSelected(ticket);
            // Update Childrens (only if NOT a Season Ticket)
            if (!this.#isSeasonTicket(ticket)) {
                this.#findSubitemRows(ticket)?.forEach(subitem => {
                    subitem.item.action.refund && (isSelected || !hasSubitemsSelected) ?
                        selection.select(subitem) : selection.deselect(subitem);
                });
            }
            // Update Parent
            const parent = this.#findParentRow(ticket);
            parent && isSelected ?
                this.isTicketSelected(parent) && selection.select(parent) : selection.deselect(parent);
        }
    }

    // selects or deselects all items
    toggleAllTickets(checked: boolean): void {
        const selection = this.tableSelection;
        if (!checked) {
            selection.clear();
        } else {
            this.tickets
                .filter(ticket => !!ticket.item.action?.refund)
                .forEach(ticket => selection.select(ticket));
        }
    }

    isTicketSelected(ticket: OrderDetailTicket): boolean {
        const selection = this.tableSelection;
        return selection?.isSelected(ticket) ||
            this.#findSubitemRows(ticket)?.every(subitem => selection.isSelected(subitem));
    }

    areAllTicketsSelected(): boolean {
        const selection = this.tableSelection;
        if (selection) {
            const numSelected = selection.selected.length;
            const numRows = this.tickets.filter(ticket => !!ticket.item.action?.refund).length;
            return numSelected === numRows;
        }
        return false;
    }

    areSomeSubitemsSelected(ticket: OrderDetailTicket): boolean {
        const selection = this.tableSelection;
        return selection?.hasValue() &&
            this.#findSubitemRows(ticket)?.some(subitem => selection.isSelected(subitem));
    }

    areSomeTicketsSelected(): boolean {
        const selection = this.tableSelection;
        return selection?.hasValue() && !this.areAllTicketsSelected();
    }

    #generateNavigationUrl(route: string[]): string {
        const urlTree = this.#router.createUrlTree(route);
        return window.location.origin + this.#router.serializeUrl(urlTree);
    }

    goToTicket(event: Event, ticket: OrderDetailTicket): void {
        event.preventDefault();
        // prevent click on borders of row
        if (!ticket.parentId && ticket.item?.id && !(event.target as HTMLElement).classList.contains('mat-row')) {
            this.#router.navigate(['/transactions', this.$orderCode(), 'tickets', ticket.item.id]);
        }
    }

    isTicketNotValidated(ticket: OrderDetailTicket): boolean {
        const validations = ticket.item.ticket?.validations;
        return validations == null || validations.length === 0;
    }

    isLastSubitem(ticket: OrderDetailTicket): boolean {
        if (ticket.parentId) {
            const lastSameParentSubitem = this.$sessionData().tickets.filter(elem => elem.parentId === ticket.parentId).slice(-1)?.pop();
            return (lastSameParentSubitem?.item as OrderSubItem).session.id === (ticket.item as OrderSubItem).session.id;
        }
        return false;
    }

    isLockedItem(ticket: OrderDetailTicket): boolean {
        const lockedStates = [
            TicketDetailState.locked, TicketDetailState.regenerated, TicketDetailState.partialLocked,
            TicketDetailState.partialRegenerated, TicketDetailState.refunded
        ];
        return lockedStates.includes(ticket.item.state);
    }

    #isSubitemOfSeasonTicket(ticket: OrderDetailTicket): boolean {
        if (!ticket.parentId) return false;
        const parent = this.$sessionData().tickets.find(t => t.item.id === ticket.parentId);
        return parent ? this.#isSeasonTicket(parent) : false;
    }

    #isSeasonTicket(ticket: OrderDetailTicket): boolean {
        return ticket.item?.ticket?.allocation?.event?.type === 'SEASON_TICKET';
    }

    #filterColumn(condition: boolean, column: string): void {
        this.ticketsColumns = condition ? this.ticketsColumns.filter(elem => elem !== column) : this.ticketsColumns;
    }

    #findSubitemRows(ticket: OrderDetailTicket): OrderDetailTicket[] {
        return !!ticket.item.subitems && this.$sessionData().tickets.filter(elem => elem.parentId === ticket.item.id) || null;
    }

    #findParentRow(ticket: OrderDetailTicket): OrderDetailTicket {
        return !!ticket.parentId && this.$sessionData().tickets.find(elem => elem.item.id === ticket.parentId);
    }

    #insertSubitems(ticket: OrderDetailTicket, index: number): void {
        ticket.item?.subitems?.forEach(subitem => {
            this.$sessionData().tickets.splice(index, 0, {
                text: subitem.session.name,
                item: {
                    ...subitem,
                    price: {
                        final: subitem.price.final,
                        currency: ticket.item.price.currency
                    },
                    action: {
                        refund: ticket.item.action?.partial_refund && subitem.state === TicketDetailState.purchased
                    }
                },
                parentId: ticket.item.id,
                hidden: true,
                isSubitemOfSeasonTicket: this.#isSubitemOfSeasonTicket(ticket)
            } as OrderDetailTicketWithSeasonTicket);
        });
    }

    #buildTicketInfoMap(tickets: OrderDetailTicket[]): Map<number, {
        navEntries: OrderDetailNavigation[];
        stateKey: NavigationStateKey;
    }> {
        const map = new Map<number, { navEntries: OrderDetailNavigation[]; stateKey: NavigationStateKey }>();

        tickets.forEach(ticket => map.set(ticket.item.id, {
            navEntries: this.#getOrderNavigationInfo(ticket),
            stateKey: this.#getTicketState(ticket.item)
        }));

        return map;
    }

    #getOrderNavigationInfo(order: OrderDetailTicket): OrderDetailNavigation[] {
        if (order.hideRelatedOrders) return [];

        const item = order.item;

        const entries: OrderDetailNavigation[] = [];

        const nextCode = this.#getNextOrderCode(order);
        if (nextCode !== null && item.next_order?.type) {
            entries.push(this.#buildNavigationEntry('NEXT', item.next_order.type, nextCode));
        }

        const previousCode = this.#getPreviousOrderCode(order);
        if (previousCode !== null) {
            const previousKey = previousCode === this.$originalReallocationCode()
                ? REALLOCATION_REFUND
                : item.previous_order?.type;

            if (previousKey) {
                entries.push(this.#buildNavigationEntry('PREVIOUS', previousKey, previousCode));
            }
        }

        if (item.related_reallocation_code && this.#getTicketState(item) === REALLOCATION_REFUND) {
            entries.push(this.#buildNavigationEntry('NEXT', REALLOCATION_REFUND, item.related_reallocation_code));
        }

        if (!order.parentId) {
            const customerId = this.#getCustomerIdIfMultipleClients(order);
            if (customerId) {
                const route = this.#buildCustomerRoute(customerId, order);
                const ticketType = this.#isSeasonTicket(order) ? 'SEASON_TICKET' : 'TICKET';
                entries.push({
                    type: 'customer',
                    translationKey: `ORDER.TICKETS_DATA.GO_TO_CUSTOMER_${ticketType}_DETAIL`,
                    customerId,
                    route,
                    url: this.#generateNavigationUrl(route)
                });
            }
        }

        return entries;
    }

    #computeTotalCharges(charges: PriceCharges): number {
        return (charges?.channel ?? 0) + (charges?.promoter ?? 0) + (charges?.reallocation ?? 0);
    }

    #hasSecondaryMarket(item: OrderItem | OrderSubItem): item is OrderItem & {
        secondary_market: {
            purchase_order?: string;
            original_order?: string;
        };
    } {
        return 'secondary_market' in item && !!item.secondary_market;
    }

    #getTicketState(item: OrderItem): NavigationStateKey {
        const orderType = this.$orderType();

        const isRefunded = item.state === TicketDetailState.refunded;
        const isPurchaseReallocation = orderType === OrderType.purchase && !!item.related_reallocation_code;
        const isRefundReallocation = orderType === OrderType.refund && !!this.$originalReallocationCode();
        const isReallocationRefund = isRefunded && (isPurchaseReallocation || isRefundReallocation);

        return isReallocationRefund ? REALLOCATION_REFUND : item.state;
    }

    #getNextOrderCode(order: OrderDetailTicket): string | null {
        const item = order.item;
        if (!item.next_order) return null;
        return item.next_order.code ?? (this.#hasSecondaryMarket(item) ? item.secondary_market?.purchase_order ?? null : null);
    }

    #getPreviousOrderCode(order: OrderDetailTicket): string | null {
        const item = order.item;
        if (!item.previous_order) return null;
        return item.previous_order.code ?? (this.#hasSecondaryMarket(item) ? item.secondary_market?.original_order ?? null : null);
    }

    #buildNavigationEntry(type: 'NEXT' | 'PREVIOUS', key: NavigationStateKey, code: string): OrderDetailNavigation {
        const route = ['/transactions', code];
        return {
            type: 'order',
            translationKey: `ORDER.TICKETS_DATA.GO_TO_${type}_ORDER_${key}`,
            orderCode: code,
            route,
            url: this.#generateNavigationUrl(route)
        };
    }

    #getCustomerIdIfMultipleClients(order: OrderDetailTicket): string | null {
        const customerIds = this.$customerIdsDifferentFromOrder();

        if (customerIds.size > 0 && 'user_id' in order.item && order.item.user_id && customerIds.has(order.item.user_id)) {
            return order.item.user_id;
        }

        return null;
    }

    #buildCustomerRoute(customerId: string, order: OrderDetailTicket): string[] {
        if (this.#isSeasonTicket(order)) {
            return ['/customers', customerId, 'season-tickets', order.item.id.toString()];
        }
        return ['/customers', customerId, 'tickets', order.item.ticket?.allocation.session.id.toString()];
    }
}

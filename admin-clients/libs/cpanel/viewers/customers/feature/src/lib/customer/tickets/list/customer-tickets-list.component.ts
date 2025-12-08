import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EventType, GetTicketsRequest, OrderItemDetails, TicketsBaseService, TicketState } from '@admin-clients/shared/common/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { debounceTime, filter, first, map, startWith, switchMap } from 'rxjs';
import { CustomerTicketsState } from '../customer-tickets.state';

@Component({
    selector: 'app-customer-tickets-list',
    standalone: true,
    imports: [TranslateModule, MatListModule, MatTooltipModule, LastPathGuardListenerDirective, KeyValuePipe,
        MatExpansionModule, EllipsifyDirective, MatSpinner
    ],
    templateUrl: './customer-tickets-list.component.html',
    styleUrls: ['./customer-tickets-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTicketsListComponent implements OnInit {

    readonly #customersSrv = inject(CustomersService);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #eventsService = inject(EventsService);
    readonly #customerTicketsState = inject(CustomerTicketsState);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #destroyRef = inject(DestroyRef);

    lastSessionId: number | string;
    lastEventId: number;

    readonly eventsProducts$ = this.#ticketsSrv.ticketList.getData$().pipe(map(items => this.#mapEvents(items)));

    readonly $eventsProducts = toSignal(this.eventsProducts$);
    readonly $event = toSignal(this.#eventsService.event.get$());

    readonly $isLoading = toSignal(this.#ticketsSrv.ticketList.loading$());

    ngOnInit(): void {
        this.#loadTicketsList();
        this.#handleProductsChanges();
        this.#handleProductsChangesForScroll();
        this.#handleRouterEventsChanges();
    }

    selectionChangeHandler(item: OrderItemDetails): void {
        if (this.lastSessionId !== item.ticket.allocation.session.id) {
            this.#navigateToProduct(item);
        }
    }

    #mapEvents(items: OrderItemDetails[] | undefined):
        Map<number, { name: string; sessions: Map<number, { name: string; items: OrderItemDetails[] }> }> {
        const map = new Map<number, { name: string; sessions: Map<number, { name: string; items: OrderItemDetails[] }> }>();
        items?.forEach(item => {
            const event = item.ticket.allocation.event;
            const session = item.ticket.allocation.session;
            if (event) {
                if (!map.has(event.id)) {
                    map.set(event.id, { name: event.name, sessions: new Map() });
                }
                const eventEntry = map.get(event.id);
                if (!eventEntry.sessions.has(session.id)) {
                    eventEntry.sessions.set(session.id, { name: session.name, items: [] });
                }
                eventEntry.sessions.get(session.id)?.items.push(item);
            }
        });
        return map;
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

    get #idPath(): number | undefined {
        // eslint-disable-next-line @typescript-eslint/dot-notation
        return parseInt(this.#activatedRoute.snapshot.children[0]?.params['sessionId'], 10);
    }

    get #innerPath(): string {
        return this.#activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    #currentPath(product: OrderItemDetails): string {
        return this.#innerPath ?
            product.ticket.allocation.session.id.toString() + '/' + this.#innerPath :
            product.ticket.allocation.session.id.toString();
    }

    #navigateToProduct(product: OrderItemDetails): void {
        if (!product) return;
        const path = this.#currentPath(product);
        this.#router.navigate([path], { relativeTo: this.#activatedRoute, queryParamsHandling: 'merge' });
    }

    #handleProductsChanges(): void {
        this.eventsProducts$.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(list => {
            if (!this.#idPath) {
                const firstEventKey = Array.from(list.keys())[0];
                if (firstEventKey) {
                    const firstSessionKey = Array.from(list.get(firstEventKey)?.sessions?.keys() || []).at(-1);
                    const firstProduct = list.get(firstEventKey)?.sessions?.get(firstSessionKey)?.items?.[0];
                    if (firstProduct) {
                        this.#navigateToProduct(firstProduct);
                    }
                }
            }
        });
    }

    #handleProductsChangesForScroll(): void {
        this.eventsProducts$
            .pipe(
                filter(seasonProducts => !!seasonProducts.size),
                debounceTime(500),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(() => {
                const element = document.getElementById('st-products-list-option-' + this.lastSessionId);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
    }

    #handleRouterEventsChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.eventsProducts$),
            filter(products => !!products.size),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(products => {
            const selectedProducts = Array.from(products.values())
                .flatMap(event => Array.from(event.sessions.values()))
                .flatMap(session => session.items)
                .filter(product => product.ticket.allocation.session.id === this.#idPath);
            const selectedProduct = selectedProducts[0];
            if (selectedProduct) {
                if (selectedProduct.ticket.allocation.event.id !== this.lastEventId) {
                    this.#eventsService.event.load(selectedProduct.ticket.allocation.event.id.toString());
                }
                this.lastSessionId = selectedProduct.ticket.allocation.session.id;
                this.lastEventId = selectedProduct.ticket.allocation.event.id;
            }
            if (selectedProducts?.length) {
                this.#customerTicketsState.selectedProducts.setValue(selectedProducts);
            }
        });
    }
}
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CustomersService, VmCustomerSeasonTicketProduct } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EventType, GetTicketsRequest, OrderItemBase, TicketsBaseService, TicketState } from '@admin-clients/shared/common/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, filter, first, map, startWith, switchMap } from 'rxjs';

@Component({
    selector: 'app-customer-season-ticket-list',
    imports: [TranslatePipe, MatListModule, MatTooltipModule, LastPathGuardListenerDirective, KeyValuePipe,
        MatExpansionModule, EllipsifyDirective
    ],
    templateUrl: './customer-season-ticket-list.component.html',
    styleUrls: ['./customer-season-ticket-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerSeasonTicketListComponent implements OnInit {

    readonly #customersSrv = inject(CustomersService);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #seasonTicketService = inject(SeasonTicketsService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #destroyRef = inject(DestroyRef);

    lastProductId: number;
    lastSeasonTicketId: number;

    readonly seasonTicketProducts$ = this.#ticketsSrv.ticketList.getData$()
        .pipe(
            filter(Boolean),
            map(res => this.#getMapOfSeasonTicketProducts(res))
        );

    readonly $seasonTicketProducts = toSignal(this.seasonTicketProducts$);
    readonly $seasonTicket = toSignal(this.#seasonTicketService.seasonTicket.get$());

    ngOnInit(): void {
        this.#loadTicketsList();
        this.#handleStProductsChanges();
        this.#handleStProductsChangesForScroll();
        this.#handleRouterEventsChanges();
    }

    selectionChangeHandler(item: VmCustomerSeasonTicketProduct): void {
        if (this.lastProductId !== item.id) {
            this.#navigateToProduct(item);
        }
    }

    #getMapOfSeasonTicketProducts(seasonTickets: OrderItemBase[]): Map<number, VmCustomerSeasonTicketProduct[]> {
        return seasonTickets.reduce((groupedSeasonTickets, seasonTicket) => {
            const eventKey = seasonTicket.ticket.allocation.event.id;
            const keyInMap = groupedSeasonTickets.has(eventKey);
            const seasonTicketName = seasonTicket.ticket.allocation.event.name;
            const element = { ...seasonTicket, seasonTicketName };
            if (keyInMap) {
                groupedSeasonTickets.get(eventKey).push(element);
            } else {
                groupedSeasonTickets.set(eventKey, [element]);
            }
            return groupedSeasonTickets;
        }, new Map<number, VmCustomerSeasonTicketProduct[]>());
    }

    #loadTicketsList(): void {
        this.#customersSrv.customer.get$()
            .pipe(first())
            .subscribe(customer => {
                const req: GetTicketsRequest = {
                    customer_id: customer.id,
                    event_type: EventType.seasonTicket,
                    state: TicketState.purchase
                };
                this.#ticketsSrv.ticketList.load(req);
            });
    }

    get #idPath(): number | undefined {
        // eslint-disable-next-line @typescript-eslint/dot-notation
        return parseInt(this.#activatedRoute.snapshot.children[0]?.params['orderItemId'], 10);
    }

    get #innerPath(): string {
        return this.#activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    #currentPath(product: VmCustomerSeasonTicketProduct): string {
        return this.#innerPath ?
            product.id.toString() + '/' + this.#innerPath :
            product.id.toString();
    }

    #navigateToProduct(product: VmCustomerSeasonTicketProduct): void {
        if (!product) return;
        const path = this.#currentPath(product);
        this.#router.navigate([path], { relativeTo: this.#activatedRoute, queryParamsHandling: 'merge' });
    }

    #handleStProductsChanges(): void {
        this.seasonTicketProducts$.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(list => {
            if (!this.#idPath) {
                const firstKey = Array.from(list.keys())[0];
                if (firstKey) {
                    const firstProduct = list.get(firstKey)?.[0];
                    if (firstProduct) {
                        this.#navigateToProduct(firstProduct);
                    }
                }
            }
        });
    }

    #handleStProductsChangesForScroll(): void {
        this.seasonTicketProducts$
            .pipe(
                filter(seasonProducts => !!seasonProducts.size),
                debounceTime(500),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(() => {
                const element = document.getElementById('st-products-list-option-' + this.lastProductId);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
    }

    #handleRouterEventsChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.seasonTicketProducts$),
            filter(stProducts => !!stProducts.size),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(stProducts => {
            const selectedProduct = Array.from(stProducts.values()).flat()
                .find(item => item.id === this.#idPath);

            if (selectedProduct) {
                if (this.lastProductId !== selectedProduct.id) {
                    this.#ticketsSrv.ticketDetail.load(selectedProduct?.order.code, selectedProduct?.id.toString());
                }
                if (this.lastSeasonTicketId !== selectedProduct?.ticket?.allocation?.event?.id) {
                    this.#seasonTicketService.seasonTicket.load(selectedProduct?.ticket?.allocation?.event?.id?.toString());
                    this.#seasonTicketService.seasonTicketStatus.load(selectedProduct?.ticket?.allocation?.event?.id?.toString());
                }
                this.lastSeasonTicketId = selectedProduct?.ticket?.allocation?.event?.id;
                this.lastProductId = selectedProduct.id;
            }
        });
    }
}
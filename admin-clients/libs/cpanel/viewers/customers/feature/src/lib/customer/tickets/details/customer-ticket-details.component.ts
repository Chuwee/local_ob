import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { OrderItemDetails } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { CustomerTicketsState } from '../customer-tickets.state';

@Component({
    selector: 'app-customer-ticket-details',
    standalone: true,
    imports: [RouterOutlet, NavTabsMenuComponent],
    templateUrl: './customer-ticket-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTicketDetailsComponent {
    readonly #breadcrumbSrv = inject(BreadcrumbsService);
    readonly #route = inject(ActivatedRoute);
    readonly #eventsSrv = inject(EventsService);
    readonly #customersSrv = inject(CustomersService);
    readonly #customerTicketsState = inject(CustomerTicketsState);

    readonly #$selectedProducts = toSignal(this.#customerTicketsState.selectedProducts.getValue$());
    readonly #$sessionId = toSignal(this.#route.paramMap.pipe(map(params => params.get('sessionId'))));
    readonly #$selectedEvent = toSignal(this.#eventsSrv.event.get$());
    readonly $currentCustomer = toSignal(this.#customersSrv.customer.get$().pipe(filter(Boolean)));

    readonly $groupedProducts = computed(() => {
        const groupedProducts = { ownProducts: [] as OrderItemDetails[], transferredProducts: [] as OrderItemDetails[] };
        this.#$selectedProducts()?.forEach(product => {
            const receiverId = product.transfer?.receiver?.customer_id;
            if (receiverId && receiverId === this.$currentCustomer()?.id) groupedProducts.transferredProducts.push(product);
            else groupedProducts.ownProducts.push(product);
        });
        return groupedProducts;
    });

    readonly $isTransferEnabledInSelectedSession = computed(() => {
        const transferSettings = this.#$selectedEvent()?.settings?.transfer_settings;
        return !!transferSettings?.enabled &&
            (!transferSettings?.restrict_transfer_by_sessions ||
                transferSettings?.allowed_transfer_sessions?.includes(Number(this.#$sessionId())));
    });

    constructor() {
        effect(() => this.#breadcrumbSrv.addDynamicSegment('sessionId', this.#$sessionId()));
    }
}
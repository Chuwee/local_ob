import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    OrdersService
} from '@admin-clients/cpanel-sales-data-access';
import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { OrderItemDetails, TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EmptyStateTinyComponent, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CustomerTicketsState } from '../customer-tickets.state';

@Component({
    standalone: true,
    imports: [MatButtonModule, TranslateModule, MatExpansionModule, MatIcon, MatDivider, RouterLink,
        LocalCurrencyPipe, MatSpinner, FormContainerComponent, KeyValuePipe, MatTableModule,
        MatMenuModule, EmptyStateTinyComponent
    ],
    selector: 'app-customer-tickets-general-data',
    templateUrl: './customer-tickets-general-data.component.html',
    styleUrls: ['./customer-tickets-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTicketsGeneralDataComponent {

    readonly #customerTicketsState = inject(CustomerTicketsState);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #eventsSrv = inject(EventsService);
    readonly #ordersService = inject(OrdersService);
    readonly #customersSrv = inject(CustomersService);
    readonly #translateSrv = inject(TranslateService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    readonly #$currentCustomer = toSignal(this.#customersSrv.customer.get$());

    readonly displayedColumns: string[] = ['ticket', 'name', 'surname', 'rate', 'price', 'status', 'actions'];
    readonly dateTimeFormats = DateTimeFormats;

    readonly $event = toSignal(this.#eventsSrv.event.get$());
    readonly $selectedProducts = toSignal(this.#customerTicketsState.selectedProducts.getValue$());

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#eventsSrv.event.inProgress$(),
        this.#ticketsSrv.ticketDetail.loading$(),
        this.#ticketsSrv.ticketDetail.link.loading$(),
        this.#customerTicketsState.selectedProducts.isInProgress$(),
        this.#ordersService.isTicketsLinkLoading$()
    ]));

    readonly $selectedProductsByOrder = computed(() => this.$selectedProducts()
        ?.filter(item => item.transfer?.receiver?.customer_id !== this.#$currentCustomer()?.id)
        .reduce<Record<string, OrderItemDetails[]>>((acc, item) => {
            const orderCode = item.order.code;
            if (!acc[orderCode]) {
                acc[orderCode] = [];
            }
            acc[orderCode].push(item);
            return acc;
        }, {}));

    showTickets(orderCode: string): void {
        this.#ordersService.getTicketsLink$(orderCode)
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                } else {
                    const title = this.#translateSrv.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateSrv.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogSrv.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }

}
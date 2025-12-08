import { OrderType, TicketDetail } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, input, computed } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

const REALLOCATION_REFUND = 'REALLOCATION_REFUND';

type OrderTypeWithReallocation = OrderType | typeof REALLOCATION_REFUND | null;

@Component({
    selector: 'app-ticket-order-data',
    imports: [TranslatePipe, LocalDateTimePipe],
    templateUrl: './order-data.component.html',
    styleUrls: ['./order-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDataComponent {
    readonly $ticket = input.required<TicketDetail>({ alias: 'ticket' });

    readonly dateTimeFormats = DateTimeFormats;

    readonly $orderType = computed(() => this.#getCurrentOrderType());
    readonly $nextOrderType = computed(() => this.#resolveExternalOrderType(ticket => ticket.next_order?.type));
    readonly $previousOrderType = computed(() => this.#resolveExternalOrderType(ticket => ticket.previous_order?.type));

    #getCurrentOrderType(): OrderTypeWithReallocation {
        const order = this.$ticket()?.order;
        if (!order) return null;
        const isReallocated = order.type === OrderType.refund && !!order.related_original_reallocation_code;

        return isReallocated ? REALLOCATION_REFUND : order.type;
    }

    #resolveExternalOrderType(extractOrderTypeFn: (ticket: TicketDetail) => OrderType | undefined): OrderTypeWithReallocation {
        const ticket = this.$ticket();
        const orderType = extractOrderTypeFn(ticket) ?? null;

        if (!ticket || !orderType) {
            return null;
        }

        const isReallocated = orderType === OrderType.refund
            && ticket.order?.type === OrderType.purchase
            && !!ticket.related_reallocation_code;

        return isReallocated ? REALLOCATION_REFUND : orderType;
    }
}

import { TicketDetailState, OrderItem } from '@admin-clients/shared/common/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'orderItemStateCount',
    pure: true,
    standalone: false
})
export class OrderItemStateCountPipe implements PipeTransform {

    constructor() { }

    transform(items: OrderItem[], state: TicketDetailState): number {
        return items?.reduce((itemCount, orderItem) => orderItem.state === state ? itemCount + 1 : itemCount, 0) | 0;
    }

}

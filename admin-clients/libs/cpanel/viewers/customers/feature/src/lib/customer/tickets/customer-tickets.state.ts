import { StateProperty } from '@OneboxTM/utils-state';
import { OrderItemDetails } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';

@Injectable()
export class CustomerTicketsState {
    readonly selectedProducts = new StateProperty<OrderItemDetails[]>();
} 
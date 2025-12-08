import { Provider } from '@angular/core';
import { OrdersApi } from './api/orders.api';
import { OrdersService } from './orders.service';
import { OrdersState } from './state/orders.state';

export const ordersProviders: Provider[] = [
    OrdersApi,
    OrdersState,
    OrdersService
];

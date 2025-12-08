import { Provider } from '@angular/core';
import { ProductEventsApi } from './api/product-events.api';
import { ProductEventsService } from './product-events.service';
import { ProductEventsState } from './state/product-events.state';

export const productEventsProviders: Provider[] = [
    ProductEventsApi,
    ProductEventsState,
    ProductEventsService
];

import { Provider } from '@angular/core';
import { ProductsSaleRequestsApi } from './api/products-sale-requests.api';
import { ProductsSaleRequestsService } from './products-sale-requests.service';
import { ProductsSaleRequestsState } from './state/products-sale-requests.state';

export const productsSaleRequestsProviders: Provider[] = [
    ProductsSaleRequestsApi,
    ProductsSaleRequestsState,
    ProductsSaleRequestsService
];

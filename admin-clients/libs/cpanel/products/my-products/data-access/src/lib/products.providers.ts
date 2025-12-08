import { Provider } from '@angular/core';
import { ProductsApi } from './api/products.api';
import { ProductsService } from './products.service';
import { ProductsState } from './state/products.state';

export const productsProviders: Provider[] = [
    ProductsApi,
    ProductsState,
    ProductsService
];

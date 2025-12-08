import { Provider } from '@angular/core';
import { ProductsDeliveryPointsApi } from './api/products-delivery-points.api';
import { ProductsDeliveryPointsService } from './products-delivery-points.service';
import { ProductsDeliveryPointsState } from './state/products-delivery-points.state';

export const productsDeliveryPointsProviders: Provider[] = [
    ProductsDeliveryPointsApi,
    ProductsDeliveryPointsState,
    ProductsDeliveryPointsService
];

import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { GetCustomerExternalProductsResponse } from '../models/customer-external-products.model';
import { GetCustomerProductsResponse } from '../models/customer-product.model';

@Injectable()
export class CustomerProductsState {
    readonly productsList = new StateProperty<GetCustomerProductsResponse>();
    readonly externalProductsList = new StateProperty<GetCustomerExternalProductsResponse>();
}

import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { ProductSaleRequest, ProductSaleRequestListElem } from '../models/product-sale-request.model';

@Injectable()
export class ProductsSaleRequestsState {
    readonly productsSaleRequestsList = new StateProperty<ListResponse<ProductSaleRequestListElem>>();
    readonly productsSaleRequestStatus = new StateProperty<void>();
    readonly productSaleRequest = new StateProperty<ProductSaleRequest>();
}

import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { DeliveryPoint } from '../models/delivery-point.model';
import { GetProductsDeliveryPoints } from '../models/get-products-delivery-points.model';

@Injectable()
export class ProductsDeliveryPointsState {
    readonly deliveryPointsList = new StateProperty<GetProductsDeliveryPoints>();
    readonly deliveryPoint = new StateProperty<DeliveryPoint>();
}

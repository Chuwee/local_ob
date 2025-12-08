import { StateProperty } from '@OneboxTM/utils-state';
import { ProductDeliveryPoint } from '@admin-clients/cpanel/products/my-products/data-access';
import { Injectable } from '@angular/core';
import { GetProductEventSessionsResponse } from '../models/get-product-event-sessions-response.model';
import { GetProductEventSessionsDeliveryPointsResponse } from '../models/product-event-session-delivery-point.model';
import { ProductEvent } from '../models/product-event.model';
import { GetProductEventSessionsStockAndPricesResponse } from '../models/product-session-stock-and-price.model';

@Injectable()
export class ProductEventsState {
    readonly eventsList = new StateProperty<ProductEvent[]>();
    readonly eventSessions = new StateProperty<GetProductEventSessionsResponse>();
    readonly eventSessionsDeliveryPoints = new StateProperty<GetProductEventSessionsDeliveryPointsResponse>();
    readonly allSessionsDeliveryPoints = new StateProperty<GetProductEventSessionsDeliveryPointsResponse>();
    readonly deliveryPoints = new StateProperty<ProductDeliveryPoint[]>();
    readonly sessionStock = new StateProperty<GetProductEventSessionsStockAndPricesResponse>();
    readonly sessionPrice = new StateProperty<GetProductEventSessionsStockAndPricesResponse>();
}

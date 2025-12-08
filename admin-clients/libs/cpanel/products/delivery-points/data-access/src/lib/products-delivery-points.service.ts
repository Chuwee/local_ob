import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { ProductsDeliveryPointsApi } from './api/products-delivery-points.api';
import { GetProductsDeliveryPointsRequest } from './models/get-products-delivery-points-request.model';
import { PostDeliveryPoint } from './models/post-delivery-point.model';
import { PutProductDeliveryPoint } from './models/put-product-delivery-point.model';
import { ProductsDeliveryPointsState } from './state/products-delivery-points.state';

@Injectable()
export class ProductsDeliveryPointsService {

    private readonly _api = inject(ProductsDeliveryPointsApi);
    private readonly _state = inject(ProductsDeliveryPointsState);

    readonly productsDeliveryPointsList = Object.freeze({
        load: (request?: GetProductsDeliveryPointsRequest) => StateManager.load(
            this._state.deliveryPointsList,
            this._api.getDeliveryPoints(request).pipe(mapMetadata())
        ),
        loadMore: (request: GetProductsDeliveryPointsRequest) =>
            StateManager.loadMore(request, this._state.deliveryPointsList, r => this._api.getDeliveryPoints(r)),
        getData$: () => this._state.deliveryPointsList.getValue$().pipe(getListData()),
        getMetadata$: () => this._state.deliveryPointsList.getValue$().pipe(getMetadata()),
        loading$: () => this._state.deliveryPointsList.isInProgress$(),
        clear: () => this._state.deliveryPointsList.setValue(null)
    });

    readonly deliveryPoint = Object.freeze({
        load: (id: number) => StateManager.load(
            this._state.deliveryPoint,
            this._api.getDeliveryPoint(id)
        ),
        inProgress$: () => this._state.deliveryPoint.isInProgress$(),
        get$: () => this._state.deliveryPoint.getValue$(),
        error$: () => this._state.deliveryPoint.getError$(),
        create: (reqBody: PostDeliveryPoint) => StateManager.inProgress(
            this._state.deliveryPoint,
            this._api.postDeliveryPoint(reqBody)
        ),
        upload: (id: number, reqBody: PutProductDeliveryPoint) => StateManager.inProgress(
            this._state.deliveryPoint,
            this._api.putDeliveryPoint(id, reqBody)
        ),
        delete: (deliveryPointId: number) => StateManager.inProgress(
            this._state.deliveryPoint,
            this._api.deleteDeliveryPoint(deliveryPointId)
        ),
        clear: () => this._state.deliveryPoint.setValue(null)
    });

}

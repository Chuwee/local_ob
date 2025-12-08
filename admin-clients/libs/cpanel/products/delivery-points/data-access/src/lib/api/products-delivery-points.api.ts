import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DeliveryPoint } from '../models/delivery-point.model';
import { GetProductsDeliveryPointsRequest } from '../models/get-products-delivery-points-request.model';
import { GetProductsDeliveryPoints } from '../models/get-products-delivery-points.model';
import { PostDeliveryPoint } from '../models/post-delivery-point.model';
import { PutProductDeliveryPoint } from '../models/put-product-delivery-point.model';

@Injectable()
export class ProductsDeliveryPointsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly DELIVERY_POINTS_API = `${this.BASE_API}/mgmt-api/v1/products-delivery-points`;

    private readonly _http = inject(HttpClient);

    getDeliveryPoints(request: GetProductsDeliveryPointsRequest): Observable<GetProductsDeliveryPoints> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            status: request.status,
            entity_id: request.entityId,
            country: request.country,
            country_subdivision: request.countrySubdivision
        });
        return this._http.get<GetProductsDeliveryPoints>(this.DELIVERY_POINTS_API, { params });
    }

    getDeliveryPoint(id: number): Observable<DeliveryPoint> {
        return this._http.get<DeliveryPoint>(`${this.DELIVERY_POINTS_API}/${id}`);
    }

    postDeliveryPoint(reqBody: PostDeliveryPoint): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.DELIVERY_POINTS_API}`, reqBody);
    }

    putDeliveryPoint(id: number, reqBody: PutProductDeliveryPoint): Observable<void> {
        return this._http.put<void>(`${this.DELIVERY_POINTS_API}/${id}`, reqBody);
    }

    deleteDeliveryPoint(deliveryPointId: number): Observable<void> {
        return this._http.delete<void>(`${this.DELIVERY_POINTS_API}/${deliveryPointId}`);
    }

}

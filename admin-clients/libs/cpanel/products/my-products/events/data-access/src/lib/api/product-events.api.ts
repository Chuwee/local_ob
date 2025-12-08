import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ProductDeliveryPoint } from '@admin-clients/cpanel/products/my-products/data-access';
import { GetSessionsRequest } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetProductEventSessionsResponse } from '../models/get-product-event-sessions-response.model';
import { GetProductEventsRequest } from '../models/get-product-event.model';
import { PostProductEvent } from '../models/post-product-event.model';
import { GetProductEventSessionsDeliveryPointsResponse } from '../models/product-event-session-delivery-point.model';
import { ProductEvent } from '../models/product-event.model';
import {
    GetProductEventSessionsStockAndPricesResponse, GetSessionsPriceRequest, GetSessionsStockRequest,
    PutProductEventSessionsStockAndPrices
} from '../models/product-session-stock-and-price.model';
import { PutProductEventDeliveryPoint } from '../models/put-product-event-delivery-point.model';
import { PutProductEventSessions } from '../models/put-product-event-session.model';
import { PutProductEventSessionDeliveryPoints } from '../models/put-product-event-sessions-delivery-points.model';
import { PutProductEvent } from '../models/put-product-event.model';

@Injectable()
export class ProductEventsApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #PRODUCTS_API = `${this.#BASE_API}/mgmt-api/v1/products`;
    readonly #EVENTS_SEGMENT = 'events';
    readonly #EVENT_SESSIONS_SEGMENT = 'publishing-sessions';
    readonly #EVENT_SESSIONS_PRICES_AND_STOCK_SEGMENT = 'sessions';
    readonly #EVENT_DELIVERY_POINTS_SEGMENT = 'delivery-points';
    readonly #EVENT_SESSION_DELIVERY_POINTS_SEGMENT = 'session-delivery-points';

    readonly #http = inject(HttpClient);

    // EVENTS

    getProductEventsList(productId: number, request?: GetProductEventsRequest): Observable<ProductEvent[]> {
        const params = buildHttpParams({
            event_status: request?.event_status,
            product_event_status: request?.product_event_status,
            start_date: request?.start_date
        });
        return this.#http.get<ProductEvent[]>(`${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}`, { params });
    }

    postProductEvent(productId: number, body: PostProductEvent): Observable<void> {
        return this.#http.post<void>(`${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}`, body);
    }

    updateProductEvent(productId: number, eventId: number, body: PutProductEvent): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventId}`, body);
    }

    deleteProductEvent(productId: number, eventId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventId}`);
    }

    // EVENTS- SESSIONS

    getProductEventSessions(productId: number, eventdId: number): Observable<GetProductEventSessionsResponse> {
        return this.#http.get<GetProductEventSessionsResponse>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_SEGMENT}`
        );
    }

    updateProductEventSessions(productId: number, eventdId: number, eventSessions: PutProductEventSessions): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_SEGMENT}`,
            eventSessions
        );
    }

    // EVENTS- SESSIONS - STOCK
    getProductEventSessionsStock(
        productId: number, eventdId: number, request: GetSessionsStockRequest
    ): Observable<GetProductEventSessionsStockAndPricesResponse> {
        const params = buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate),
            end_date: getRangeParam(request.initEndDate, request.finalEndDate),
            weekdays: request.weekdays,
            status: request.status
        });
        return this.#http.get<GetProductEventSessionsStockAndPricesResponse>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_PRICES_AND_STOCK_SEGMENT}`,
            { params }
        );
    }

    updateProductEventSessionStock(
        productId: number, eventdId: number, sessionId: number, req: PutProductEventSessionsStockAndPrices
    ): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_PRICES_AND_STOCK_SEGMENT}/${sessionId}`,
            req
        );
    }

    // EVENTS- SESSIONS - PRICES
    getProductEventSessionsPrice(
        productId: number, eventdId: number, request: GetSessionsPriceRequest
    ): Observable<GetProductEventSessionsStockAndPricesResponse> {
        const params = buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate),
            end_date: getRangeParam(request.initEndDate, request.finalEndDate),
            weekdays: request.weekdays,
            status: request.status
        });
        return this.#http.get<GetProductEventSessionsStockAndPricesResponse>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_PRICES_AND_STOCK_SEGMENT}`,
            { params }
        );
    }

    updateProductEventSessionPrice(
        productId: number, eventdId: number, sessionId: number, req: PutProductEventSessionsStockAndPrices
    ): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSIONS_PRICES_AND_STOCK_SEGMENT}/${sessionId}`,
            req
        );
    }

    // EVENT - DELIVERY POINTS

    getProductEventDeliveryPoints(productId: number, eventdId: number): Observable<ProductDeliveryPoint[]> {
        return this.#http.get<ProductDeliveryPoint[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_DELIVERY_POINTS_SEGMENT}`
        );
    }

    updateProductEventDeliveryPoints(
        productId: number, eventdId: number, deliveryPoints: PutProductEventDeliveryPoint[]
    ): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_DELIVERY_POINTS_SEGMENT}`,
            deliveryPoints
        );
    }

    // EVENT - SESSIONS - DELIVERY POINTS
    getProductEventSessionDeliveryPoints(productId: number, eventdId: number, request: GetSessionsRequest): Observable<GetProductEventSessionsDeliveryPointsResponse> {
        const params = buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate),
            end_date: getRangeParam(request.initEndDate, request.finalEndDate),
            day_of_week: request.weekdays,
            timezone: request.timezone,
            status: request.status
        });
        return this.#http.get<GetProductEventSessionsDeliveryPointsResponse>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSION_DELIVERY_POINTS_SEGMENT}`, { params }
        );
    }

    updateProductEventSessionsDeliveryPoints(
        productId: number, eventdId: number, deliveryPoints: PutProductEventSessionDeliveryPoints[]
    ): Observable<void> {
        return this.#http.put<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#EVENTS_SEGMENT}/${eventdId}/${this.#EVENT_SESSION_DELIVERY_POINTS_SEGMENT}`,
            deliveryPoints
        );
    }
}

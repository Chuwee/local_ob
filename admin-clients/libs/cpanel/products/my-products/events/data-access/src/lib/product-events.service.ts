import { StateManager } from '@OneboxTM/utils-state';
import { GetSessionsRequest, GetSessionsResponse } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { inject, Injectable } from '@angular/core';
import { finalize, map, Observable, takeUntil } from 'rxjs';
import { ProductEventsApi } from './api/product-events.api';
import { GetProductEventsRequest } from './models/get-product-event.model';
import { PostProductEvent } from './models/post-product-event.model';
import { ProductEventSessionDeliveryPoint } from './models/product-event-session-delivery-point.model';
import {
    GetSessionsPriceRequest, GetSessionsStockRequest, PutProductEventSessionsStockAndPrices
} from './models/product-session-stock-and-price.model';
import { PutProductEventDeliveryPoint } from './models/put-product-event-delivery-point.model';
import { PutProductEventSessions } from './models/put-product-event-session.model';
import { PutProductEventSessionDeliveryPoints } from './models/put-product-event-sessions-delivery-points.model';
import { PutProductEvent } from './models/put-product-event.model';
import { ProductEventsState } from './state/product-events.state';

@Injectable()
export class ProductEventsService {
    readonly #api = inject(ProductEventsApi);
    readonly #state = inject(ProductEventsState);

    readonly productEvents = Object.freeze({
        event: Object.freeze({
            create: (productId: number, body: PostProductEvent) => StateManager.inProgress(
                this.#state.eventsList,
                this.#api.postProductEvent(productId, body)
            ),
            update: (productId: number, eventId: number, body: PutProductEvent) => StateManager.inProgress(
                this.#state.eventsList,
                this.#api.updateProductEvent(productId, eventId, body)
            ),
            delete: (productId: number, eventId: number) => StateManager.inProgress(
                this.#state.eventsList,
                this.#api.deleteProductEvent(productId, eventId)
            )
        }),
        list: Object.freeze({
            load: (productId: number, request?: GetProductEventsRequest) => StateManager.load(
                this.#state.eventsList,
                this.#api.getProductEventsList(productId, request)
            ),
            get$: () => this.#state.eventsList.getValue$(),
            loading$: () => this.#state.eventsList.isInProgress$(),
            clear: () => this.#state.eventsList.setValue(null)
        }),
        sessions: Object.freeze({
            load: (productId: number, eventId: number) => StateManager.load(
                this.#state.eventSessions,
                this.#api.getProductEventSessions(productId, eventId)
            ),
            get$: () => this.#state.eventSessions.getValue$(),
            loading$: () => this.#state.eventSessions.isInProgress$(),
            update: (productId: number, eventId: number, reqBody: PutProductEventSessions) => StateManager.inProgress(
                this.#state.eventSessions,
                this.#api.updateProductEventSessions(productId, eventId, reqBody)
            ),
            clear: () => this.#state.eventSessions.setValue(null),
            error$: () => this.#state.eventSessions.getError$(),
            deliveryPoints: Object.freeze({
                load: (productId: number, eventId: number, request: GetSessionsRequest) => StateManager.load(
                    this.#state.eventSessionsDeliveryPoints,
                    this.#api.getProductEventSessionDeliveryPoints(productId, eventId, request)
                ),
                getData$: () => this.#state.eventSessionsDeliveryPoints.getValue$().pipe(map(r => r?.data)),
                getMetadata$: () => this.#state.eventSessionsDeliveryPoints.getValue$().pipe(map(r => r?.metadata)),
                loading$: () => this.#state.eventSessionsDeliveryPoints.isInProgress$(),
                update: (
                    productId: number, eventId: number, reqBody: PutProductEventSessionDeliveryPoints[]
                ) => StateManager.inProgress(
                    this.#state.eventSessionsDeliveryPoints,
                    this.#api.updateProductEventSessionsDeliveryPoints(productId, eventId, reqBody)
                ),
                clear: () => this.#state.eventSessionsDeliveryPoints.setValue(null),
                error$: () => this.#state.eventSessionsDeliveryPoints.getError$()
            })
        }),
        stock: Object.freeze({
            load: (productId: number, eventId: number, request: GetSessionsStockRequest) => StateManager.load(
                this.#state.sessionStock,
                this.#api.getProductEventSessionsStock(productId, eventId, request)
            ),
            getData$: () => this.#state.sessionStock.getValue$().pipe(map(list => list?.data)),
            getMetadata$: () => this.#state.sessionStock.getValue$().pipe(map(r => r?.metadata)),
            loading$: () => this.#state.sessionStock.isInProgress$(),
            updateSession: (
                productId: number, eventId: number, sessionId: number, reqBody: PutProductEventSessionsStockAndPrices
            ) => StateManager.inProgress(
                this.#state.sessionStock,
                this.#api.updateProductEventSessionStock(productId, eventId, sessionId, reqBody)
            ),
            clear: () => this.#state.sessionStock.setValue(null),
            error$: () => this.#state.sessionStock.getError$()
        }),
        price: Object.freeze({
            load: (productId: number, eventId: number, request: GetSessionsPriceRequest) => StateManager.load(
                this.#state.sessionPrice,
                this.#api.getProductEventSessionsPrice(productId, eventId, request)
            ),
            getData$: () => this.#state.sessionPrice.getValue$().pipe(map(list => list?.data)),
            getMetadata$: () => this.#state.sessionPrice.getValue$().pipe(map(r => r?.metadata)),
            loading$: () => this.#state.sessionPrice.isInProgress$(),
            updateSession: (
                productId: number, eventId: number, sessionId: number, reqBody: PutProductEventSessionsStockAndPrices
            ) => StateManager.inProgress(
                this.#state.sessionPrice,
                this.#api.updateProductEventSessionPrice(productId, eventId, sessionId, reqBody)
            ),
            clear: () => this.#state.sessionPrice.setValue(null),
            error$: () => this.#state.sessionPrice.getError$()
        }),
        deliveryPoints: Object.freeze({
            load: (productId: number, eventId: number) => StateManager.load(
                this.#state.deliveryPoints,
                this.#api.getProductEventDeliveryPoints(productId, eventId)
            ),
            get$: () => this.#state.deliveryPoints.getValue$(),
            loading$: () => this.#state.deliveryPoints.isInProgress$(),
            update: (productId: number, eventId: number, reqBody: PutProductEventDeliveryPoint[]) => StateManager.inProgress(
                this.#state.deliveryPoints,
                this.#api.updateProductEventDeliveryPoints(productId, eventId, reqBody)
            ),
            clear: () => this.#state.deliveryPoints.setValue(null),
            error$: () => this.#state.deliveryPoints.getError$()
        })
    });

    loadAllSessionDeliveries(productId: number, eventId: number, request?: Partial<GetSessionsRequest>): void {
        this.#state.allSessionsDeliveryPoints.triggerCancellation();
        this.#state.allSessionsDeliveryPoints.setInProgress(true);
        const req: GetSessionsRequest = Object.assign({ offset: 0, limit: 999 }, request);
        fetchAll((offset: number) => this.#api.getProductEventSessionDeliveryPoints(productId, eventId, { ...req, offset }))
            .pipe(
                finalize(() => this.#state.allSessionsDeliveryPoints.setInProgress(false)),
                takeUntil(this.#state.allSessionsDeliveryPoints.getCancellation$())
            ).subscribe(result => this.#state.allSessionsDeliveryPoints.setValue(result));
    }

    getAllSessionDeliveries$(): Observable<GetSessionsResponse> {
        return this.#state.allSessionsDeliveryPoints.getValue$();
    }

    getAllSessionDeliveriesData$(): Observable<ProductEventSessionDeliveryPoint[]> {
        return this.#state.allSessionsDeliveryPoints.getValue$().pipe(map(packs => packs?.data));
    }

    isAllSessionDeliveriesLoading$(): Observable<boolean> {
        return this.#state.allSessionsDeliveryPoints.isInProgress$();
    }

    clearAllSessionDeliveries(): void {
        this.#state.allSessionsDeliveryPoints.setValue(null);
    }
}

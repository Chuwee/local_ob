import { Metadata, StateManager } from '@OneboxTM/utils-state';
import { PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { inject, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { first, map, takeUntil } from 'rxjs/operators';
import { EventPromotionsApi } from './api/event-promotions.api';
import { EventPromotionPacks } from './models/event-promotion-packs.model';
import { EventPromotion } from './models/event-promotion.model';
import { GetEventPromotionsRequest } from './models/get-event-promotions-request.model';
import { PostEventPromotion } from './models/post-event-promotion.model';
import { PutEventPromotionPriceTypes } from './models/put-event-promotion-price-types.model';
import { PutEventPromotionRates } from './models/put-event-promotion-rates.model';
import { PutEventPromotionSessions } from './models/put-event-promotion-sessions.model';
import { EventPromotionsState } from './state/event-promotions.state';

@Injectable()
export class EventPromotionsService {
    private readonly _api = inject(EventPromotionsApi);
    private readonly _state = inject(EventPromotionsState);
    private readonly _cancelPromotionRequest = new Subject<void>();
    private readonly _cancelPromotionListRequest = new Subject<void>();

    readonly promotion = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotion,
            this._api.getPromotion(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        cancelLoad: () => this._state.eventPromotion.triggerCancellation(),
        get$: () => this._state.eventPromotion.getValue$(),
        error$: () => this._state.eventPromotion.getError$(),
        loading$: () => this._state.eventPromotion.isInProgress$(),
        clear: () => this._state.eventPromotion.setValue(null),
        create: (eventId: number, promotion: PostEventPromotion) => StateManager.inProgress(
            this._state.eventPromotion,
            this._api.postPromotion(eventId, promotion).pipe(map(result => result.id))
        ),
        update: (eventId: number, promotionId: number, promotion: EventPromotion) => StateManager.inProgress(
            this._state.eventPromotion,
            this._api.putPromotion(eventId, promotionId, promotion)
        ),
        delete: (eventId: number, promotionId: number) => StateManager.inProgress(
            this._state.eventPromotion,
            this._api.deletePromotion(eventId, promotionId)
        ),
        clone: (eventId: number, promotionId: number) => StateManager.inProgress(
            this._state.eventPromotion,
            this._api.clonePromotion(eventId, promotionId).pipe(map(response => response.id))
        )
    });

    // PROMOTION LIST
    readonly promotionsList = Object.freeze({
        load: (eventId: number, request: GetEventPromotionsRequest) => StateManager.load(
            this._state.eventPromotionsList,
            this._api.getPromotionsList(eventId, request).pipe(takeUntil(this._cancelPromotionListRequest))
        ),
        getData$: () => this._state.eventPromotionsList.getValue$()
            .pipe(map(promotions => promotions?.data)),
        getMetadata$: () => this._state.eventPromotionsList.getValue$()
            .pipe(map(promotions =>
                promotions?.metadata && Object.assign(new Metadata(), promotions.metadata))),
        loading$: () => this._state.eventPromotionsList.isInProgress$(),
        clear: () => this._state.eventPromotionsList.setValue(null)
    });

    // PROMOTION CHANNEL TEXT CONTENTS
    readonly promotionChannelTextContents = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionChannelTextContents,
            this._api.getPromotionChannelTextContents(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionChannelTextContents.getValue$(),
        loading$: () => this._state.eventPromotionChannelTextContents.isInProgress$(),
        error$: () => this._state.eventPromotionChannelTextContents.getError$(),
        clear: () => this._state.eventPromotionChannelTextContents.setValue(null),
        update: (eventId: number, promotionId: number, contents: CommunicationTextContent[]) => StateManager.inProgress(
            this._state.eventPromotionChannelTextContents,
            this._api.postPromotionChannelTextContents(eventId, promotionId, contents)
        )
    });

    // PRMOTION CHANNELS
    readonly promotionChannels = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionChannels,
            this._api.getPromotionChannels(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionChannels.getValue$(),
        loading$: () => this._state.eventPromotionChannels.isInProgress$(),
        error$: () => this._state.eventPromotionChannels.getError$(),
        clear: () => this._state.eventPromotionChannels.setValue(null),
        update: (eventId: number, promotionId: number, req: PutPromotionChannels) => StateManager.inProgress(
            this._state.eventPromotionChannels,
            this._api.putPromotionChannels(eventId, promotionId, req)
        )
    });

    // PRMOTION SESSIONS
    readonly promotionSessions = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionSessions,
            this._api.getPromotionSessions(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionSessions.getValue$(),
        loading$: () => this._state.eventPromotionSessions.isInProgress$(),
        error$: () => this._state.eventPromotionSessions.getError$(),
        clear: () => this._state.eventPromotionSessions.setValue(null),
        update: (eventId: number, promotionId: number, req: PutEventPromotionSessions) => StateManager.inProgress(
            this._state.eventPromotionSessions,
            this._api.putPromotionSessions(eventId, promotionId, req)
        )
    });

    // PRMOTION PRICE TYPES
    readonly promotionPriceTypes = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionPriceTypes,
            this._api.getPromotionPriceTypes(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionPriceTypes.getValue$(),
        loading$: () => this._state.eventPromotionPriceTypes.isInProgress$(),
        error$: () => this._state.eventPromotionPriceTypes.getError$(),
        clear: () => this._state.eventPromotionPriceTypes.setValue(null),
        update: (eventId: number, promotionId: number, req: PutEventPromotionPriceTypes) => StateManager.inProgress(
            this._state.eventPromotionPriceTypes,
            this._api.putPromotionPriceTypes(eventId, promotionId, req)
        )
    });

    // PRMOTION RATES
    readonly promotionRates = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionRates,
            this._api.getPromotionRates(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionRates.getValue$(),
        loading$: () => this._state.eventPromotionRates.isInProgress$(),
        error$: () => this._state.eventPromotionRates.getError$(),
        clear: () => this._state.eventPromotionRates.setValue(null),
        update: (eventId: number, promotionId: number, req: PutEventPromotionRates) => StateManager.inProgress(
            this._state.eventPromotionRates,
            this._api.putPromotionRates(eventId, promotionId, req)
        )
    });

    // PROMOTION PACKS
    readonly promotionPacks = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.eventPromotionPacks,
            this._api.getPromotionPacks(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.eventPromotionPacks.getValue$(),
        loading$: () => this._state.eventPromotionPacks.isInProgress$(),
        error$: () => this._state.eventPromotionPacks.getError$(),
        clear: () => this._state.eventPromotionPacks.setValue(null),
        update: (eventId: number, promotionId: number, req: EventPromotionPacks) => StateManager.inProgress(
            this._state.eventPromotionPacks,
            this._api.putPromotionPacks(eventId, promotionId, req)
        )
    });

    cancelRequests(): void {
        this._cancelPromotionListRequest.next();
        this._cancelPromotionRequest.next();
    }

    // PROMOTION DETAILS
    loadPromotionWithDetails(eventId: number, promotionId: number): void {
        this.promotion.load(eventId, promotionId);
        this.promotion.get$().pipe(
            first(promo => promo?.id === promotionId),
            takeUntil(this._state.eventPromotion.getCancellation$())
        ).subscribe(() => {
            this.promotionChannels.load(eventId, promotionId);
            this.promotionSessions.load(eventId, promotionId);
            this.promotionPacks.load(eventId, promotionId);
            this.promotionPriceTypes.load(eventId, promotionId);
            this.promotionRates.load(eventId, promotionId);
            this.promotionChannelTextContents.load(eventId, promotionId);
        });
    }

    clearNestedPromotionData(): void {
        this.promotionChannels.clear();
        this.promotionSessions.clear();
        this.promotionPacks.clear();
        this.promotionPriceTypes.clear();
        this.promotionRates.clear();
        this.promotionChannelTextContents.clear();
    }

}

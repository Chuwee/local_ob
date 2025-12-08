import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { inject, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { first, map, takeUntil } from 'rxjs/operators';
import { SeasonTicketPromotionsApi } from './api/season-ticket-promotions.api';
import {
    GetSeasonTicketPromotionsRequest,
    PostSeasonTicketPromotion,
    PutSeasonTicketPromotionPriceTypes,
    PutSeasonTicketPromotionRates,
    SeasonTicketPromotion
} from './models/season-ticket-promotion.model';
import { SeasonTicketPromotionsState } from './state/season-ticket-promotions.state';

@Injectable({ providedIn: 'root' })
export class SeasonTicketPromotionsService {
    private readonly _state = inject(SeasonTicketPromotionsState);
    private readonly _api = inject(SeasonTicketPromotionsApi);
    private readonly _cancelPromotionRequest = new Subject<void>();
    private readonly _cancelPromotionListRequest = new Subject<void>();

    readonly promotion = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.stPromotion,
            this._api.getPromotion(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        cancelLoad: () => this._state.stPromotion.triggerCancellation(),
        get$: () => this._state.stPromotion.getValue$(),
        error$: () => this._state.stPromotion.getError$(),
        loading$: () => this._state.stPromotion.isInProgress$(),
        clear: () => this._state.stPromotion.setValue(null),
        create: (stId: number, promotion: PostSeasonTicketPromotion) => StateManager.inProgress(
            this._state.stPromotion,
            this._api.postPromotion(stId, promotion).pipe(map(result => result.id))
        ),
        update: (eventId: number, promotionId: number, promotion: SeasonTicketPromotion) => StateManager.inProgress(
            this._state.stPromotion,
            this._api.putPromotion(eventId, promotionId, promotion)
        ),
        delete: (eventId: number, promotionId: number) => StateManager.inProgress(
            this._state.stPromotion,
            this._api.deletePromotion(eventId, promotionId)
        ),
        clone: (eventId: number, promotionId: number) => StateManager.inProgress(
            this._state.stPromotion,
            this._api.clonePromotion(eventId, promotionId).pipe(map(response => response.id))
        )
    });

    // PROMOTION LIST
    readonly promotionsList = Object.freeze({
        load: (eventId: number, request: GetSeasonTicketPromotionsRequest) => StateManager.load(
            this._state.stPromotionsList,
            this._api.getPromotionsList(eventId, request).pipe(mapMetadata(), takeUntil(this._cancelPromotionListRequest))
        ),
        getData$: () => this._state.stPromotionsList.getValue$()
            .pipe(getListData()),
        getMetadata$: () => this._state.stPromotionsList.getValue$()
            .pipe(getMetadata()),
        loading$: () => this._state.stPromotionsList.isInProgress$(),
        clear: () => this._state.stPromotionsList.setValue(null)
    });

    // PROMOTION CHANNEL TEXT CONTENTS
    readonly promotionChannelTextContents = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.stPromotionChannelTextContents,
            this._api.getPromotionChannelTextContents(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.stPromotionChannelTextContents.getValue$(),
        loading$: () => this._state.stPromotionChannelTextContents.isInProgress$(),
        error$: () => this._state.stPromotionChannelTextContents.getError$(),
        clear: () => this._state.stPromotionChannelTextContents.setValue(null),
        update: (eventId: number, promotionId: number, contents: CommunicationTextContent[]) => StateManager.inProgress(
            this._state.stPromotionChannelTextContents,
            this._api.postPromotionChannelTextContents(eventId, promotionId, contents)
        )
    });

    // PRMOTION CHANNELS
    readonly promotionChannels = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.stPromotionChannels,
            this._api.getPromotionChannels(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.stPromotionChannels.getValue$(),
        loading$: () => this._state.stPromotionChannels.isInProgress$(),
        error$: () => this._state.stPromotionChannels.getError$(),
        clear: () => this._state.stPromotionChannels.setValue(null),
        update: (eventId: number, promotionId: number, req: PutPromotionChannels) => StateManager.inProgress(
            this._state.stPromotionChannels,
            this._api.putPromotionChannels(eventId, promotionId, req)
        )
    });

    // PRMOTION PRICE TYPES
    readonly promotionPriceTypes = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.stPromotionPriceTypes,
            this._api.getPromotionPriceTypes(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.stPromotionPriceTypes.getValue$(),
        loading$: () => this._state.stPromotionPriceTypes.isInProgress$(),
        error$: () => this._state.stPromotionPriceTypes.getError$(),
        clear: () => this._state.stPromotionPriceTypes.setValue(null),
        update: (eventId: number, promotionId: number, req: PutSeasonTicketPromotionPriceTypes) => StateManager.inProgress(
            this._state.stPromotionPriceTypes,
            this._api.putPromotionPriceTypes(eventId, promotionId, req)
        )
    });

    // PRMOTION RATES
    readonly promotionRates = Object.freeze({
        load: (eventId: number, promotionId: number) => StateManager.load(
            this._state.stPromotionRates,
            this._api.getPromotionRates(eventId, promotionId).pipe(takeUntil(this._cancelPromotionRequest))
        ),
        get$: () => this._state.stPromotionRates.getValue$(),
        loading$: () => this._state.stPromotionRates.isInProgress$(),
        error$: () => this._state.stPromotionRates.getError$(),
        clear: () => this._state.stPromotionRates.setValue(null),
        update: (eventId: number, promotionId: number, req: PutSeasonTicketPromotionRates) => StateManager.inProgress(
            this._state.stPromotionRates,
            this._api.putPromotionRates(eventId, promotionId, req)
        )
    });

    cancelRequests(): void {
        this._cancelPromotionListRequest.next();
        this._cancelPromotionRequest.next();
    }

    // PROMOTION DETAILS
    loadPromotionWithDetails(stId: number, promotionId: number): void {
        this.promotion.load(stId, promotionId);
        this.promotion.get$().pipe(
            first(promo => promo?.id === promotionId),
            takeUntil(this._state.stPromotion.getCancellation$())
        ).subscribe(() => {
            this.promotionChannels.load(stId, promotionId);
            this.promotionPriceTypes.load(stId, promotionId);
            this.promotionRates.load(stId, promotionId);
            this.promotionChannelTextContents.load(stId, promotionId);
        });
    }

    clearNestedPromotionData(): void {
        this.promotionChannels.clear();
        this.promotionPriceTypes.clear();
        this.promotionRates.clear();
        this.promotionChannelTextContents.clear();
    }
}

import { Metadata, StateManager } from '@OneboxTM/utils-state';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { ChannelPromotionsApi } from './api/channel-promotions.api';
import { ChannelPromotionEvents, PutChannelPromotionEvents } from './models/channel-promotion-events.model';
import { ChannelPromotionPriceTypes, PutChannelPromotionPriceTypes } from './models/channel-promotion-price-types.model';
import { ChannelPromotionSessions, PutChannelPromotionSessions } from './models/channel-promotion-sessions.model';
import { ChannelPromotion } from './models/channel-promotion.model';
import { ChannelPromotionListElement } from './models/get-channel-promotions-response.model';
import { PostChannelPromotion } from './models/post-channel-promotion.model';
import { ChannelPromotionsState } from './state/channel-promotions.state';

@Injectable({
    providedIn: 'root'
})
export class ChannelPromotionsService {

    private _cancelPromotionRequest = new Subject<void>();
    private _cancelPromotionListRequest = new Subject<void>();

    constructor(
        private _state: ChannelPromotionsState,
        private _api: ChannelPromotionsApi
    ) { }

    cancelRequests(): void {
        this._cancelPromotionListRequest.next();
        this._cancelPromotionRequest.next();
    }

    // PROMOTION LIST

    loadPromotionsList(channelId: number, request: PageableFilter): void {
        StateManager.load(
            this._state.promotionsList,
            this._api.getPromotionsList(channelId, request).pipe(
                takeUntil(this._cancelPromotionListRequest)
            )
        );
    }

    getPromotionsListData$(): Observable<ChannelPromotionListElement[]> {
        return this._state.promotionsList.getValue$().pipe(map(promotions => promotions?.data));
    }

    getPromotionsListMetaData$(): Observable<Metadata> {
        return this._state.promotionsList.getValue$().pipe(map(promotions => promotions?.metadata));
    }

    isPromotionsListInProgress$(): Observable<boolean> {
        return this._state.promotionsList.isInProgress$();
    }

    clearPromotionsList(): void {
        this._state.promotionsList.setValue(null);
    }

    // PROMOTION DETAILS

    clearPromotionDetails(): void {
        this.clearPromotion();
        this.clearPromotionContents();
        this.clearPromotionEvents();
        this.clearPromotionSessions();
        this.clearPromotionPriceTypes();
    }

    loadPromotionDetails(channelId: number, promotionId: number): void {
        this.loadPromotion(channelId, promotionId);
        this.loadPromotionContents(channelId, promotionId);
        this.loadPromotionEvents(channelId, promotionId);
        this.loadPromotionSessions(channelId, promotionId);
        this.loadPromotionPriceTypes(channelId, promotionId);
    }

    loadPromotion(channelId: number, promotionId: number): void {
        this._cancelPromotionRequest.next();
        StateManager.load(
            this._state.promotion,
            this._api.getPromotion(channelId, promotionId).pipe(
                takeUntil(this._cancelPromotionRequest)
            )
        );
    }

    getPromotion$(): Observable<ChannelPromotion> {
        return this._state.promotion.getValue$();
    }

    getPromotionError$(): Observable<HttpErrorResponse> {
        return this._state.promotion.getError$();
    }

    isPromotionInProgress$(): Observable<boolean> {
        return combineLatest([
            this.isPromotionLoading$(),
            this.isPromotionSaving$()
        ]).pipe(map(loadings => loadings.some(isLoading => isLoading)));
    }

    isPromotionLoading$(): Observable<boolean> {
        return this._state.promotion.isInProgress$();
    }

    isPromotionSaving$(): Observable<boolean> {
        return this._state.promotionSaving.isInProgress$();
    }

    clearPromotion(): void {
        this._state.promotion.setValue(null);
    }

    createPromotion(channelId: number, promotion: PostChannelPromotion): Observable<number> {
        return StateManager.inProgress(
            this._state.promotion,
            this._api.postPromotion(channelId, promotion).pipe(map(result => result.id))
        );
    }

    updatePromotion(channelId: number, promotionId: number, promotion: ChannelPromotion): Observable<void> {
        return StateManager.inProgress(
            this._state.promotionSaving,
            this._api.putPromotion(channelId, promotionId, promotion)
        );
    }

    deletePromotion(channelId: number, promotionId: number): Observable<void> {
        return StateManager.inProgress(
            this._state.promotion,
            this._api.deletePromotion(channelId, promotionId)
        );
    }

    clonePromotion(channelId: number, promotionId: number): Observable<number> {
        return StateManager.inProgress(
            this._state.promotion,
            this._api.clonePromotion(channelId, promotionId).pipe(map(response => response.id))
        );
    }

    // PROMOTION CHANNEL TEXT CONTENTS

    loadPromotionContents(channelId: number, promotionId: number): void {
        StateManager.load(
            this._state.promotionContents,
            this._api.getPromotionContents(channelId, promotionId).pipe(
                takeUntil(this._cancelPromotionRequest)
            )
        );
    }

    isPromotionContentsInProgress$(): Observable<boolean> {
        return this._state.promotionContents.isInProgress$();
    }

    getPromotionContents$(): Observable<CommunicationTextContent[]> {
        return this._state.promotionContents.getValue$();
    }

    getPromotionContentsError$(): Observable<HttpErrorResponse> {
        return this._state.promotionContents.getError$();
    }

    clearPromotionContents(): void {
        this._state.promotionContents.setValue(null);
    }

    updatePromotionContents(channelId: number, promotionId: number, contents: CommunicationTextContent[]): Observable<void> {
        return StateManager.inProgress(
            this._state.promotionContents,
            this._api.postPromotionContents(channelId, promotionId, contents)
        );
    }

    // CHANNEL PROMOTION EVENTS

    loadPromotionEvents(channelId: number, promotionId: number): void {
        StateManager.load(
            this._state.promotionEvents,
            this._api.getPromotionEvents(channelId, promotionId).pipe(
                takeUntil(this._cancelPromotionRequest)
            )
        );
    }

    isPromotionEventsInProgress$(): Observable<boolean> {
        return this._state.promotionEvents.isInProgress$();
    }

    getPromotionEvents$(): Observable<ChannelPromotionEvents> {
        return this._state.promotionEvents.getValue$();
    }

    getPromotionEventsError$(): Observable<HttpErrorResponse> {
        return this._state.promotionEvents.getError$();
    }

    clearPromotionEvents(): void {
        this._state.promotionEvents.setValue(null);
    }

    updatePromotionEvents(channelId: number, promotionId: number, req: PutChannelPromotionEvents): Observable<void> {
        return StateManager.inProgress(
            this._state.promotionEvents,
            this._api.putPromotionEvents(channelId, promotionId, req)
        );
    }

    // CHANNEL PROMOTION SESSIONS

    loadPromotionSessions(channelId: number, promotionId: number): void {
        StateManager.load(
            this._state.promotionSessions,
            this._api.getPromotionSessions(channelId, promotionId).pipe(
                takeUntil(this._cancelPromotionRequest)
            )
        );
    }

    isPromotionSessionsInProgress$(): Observable<boolean> {
        return this._state.promotionSessions.isInProgress$();
    }

    getPromotionSessions$(): Observable<ChannelPromotionSessions> {
        return this._state.promotionSessions.getValue$();
    }

    getPromotionSessionsError$(): Observable<HttpErrorResponse> {
        return this._state.promotionSessions.getError$();
    }

    clearPromotionSessions(): void {
        this._state.promotionSessions.setValue(null);
    }

    updatePromotionSessions(channelId: number, promotionId: number, req: PutChannelPromotionSessions): Observable<void> {
        return StateManager.inProgress(
            this._state.promotionSessions,
            this._api.putPromotionSessions(channelId, promotionId, req)
        );
    }

    // CHANNEL PROMOTION PRICE TYPES

    loadPromotionPriceTypes(channelId: number, promotionId: number): void {
        StateManager.load(
            this._state.promotionPriceTypes,
            this._api.getPromotionPriceTypes(channelId, promotionId)
        );
    }

    isPromotionPriceTypesInProgress$(): Observable<boolean> {
        return this._state.promotionPriceTypes.isInProgress$();
    }

    getPromotionPriceTypes$(): Observable<ChannelPromotionPriceTypes> {
        return this._state.promotionPriceTypes.getValue$();
    }

    getPromotionPriceTypesError$(): Observable<HttpErrorResponse> {
        return this._state.promotionPriceTypes.getError$();
    }

    clearPromotionPriceTypes(): void {
        this._state.promotionPriceTypes.setValue(null);
    }

    updatePromotionPriceTypes(channelId: number, promotionId: number, req: PutChannelPromotionPriceTypes): Observable<void> {
        return StateManager.inProgress(
            this._state.promotionPriceTypes,
            this._api.putPromotionPriceTypes(channelId, promotionId, req)
        );
    }

}

import { getListData, getMetadata, mapMetadata, Metadata } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { catchError, finalize, takeUntil } from 'rxjs/operators';
import { EventTierApi } from './api/event-tier.api';
import { EventTiersChannelContent } from './models/event-tiers-channel-content.model';
import { EventTiers } from './models/event-tiers.model';
import { GetEventTierRequest } from './models/get-event-tiers-request.model';
import { PostEventTierQuota } from './models/post-event-tier-quota.model';
import { PostEventTier } from './models/post-event-tier.model';
import { PutEventTierQuota } from './models/put-event-tier-quota.model';
import { PutEventTier } from './models/put-event-tier.model';
import { EventTierState } from './state/event-tier.state';

@Injectable({
    providedIn: 'root'
})
export class EventTiersService {
    private _templateChange = new Subject<void>();

    constructor(private _eventTiersApi: EventTierApi, private _eventTiersState: EventTierState) { }

    loadEventTiersList(eventId: string, request: GetEventTierRequest): void {
        this._eventTiersState.setEventTiersListError(null);
        this._eventTiersState.setEventTiersListInProgress(true);
        this._eventTiersApi.getEventTiers(eventId, request)
            .pipe(
                mapMetadata(),
                catchError(error => {
                    this._eventTiersState.setEventTiersListError(error);
                    return of(null);
                }),
                finalize(() => this._eventTiersState.setEventTiersListInProgress(false))
            )
            .subscribe(tiers =>
                this._eventTiersState.setEventTiersList(tiers)
            );
    }

    clearEventTiersList(): void {
        this._eventTiersState.setEventTiersList(null);
    }

    getEventTiersListData$(): Observable<EventTiers[]> {
        return this._eventTiersState.getEventTiersList$().pipe(getListData());
    }

    getEventTiersListMetadata$(): Observable<Metadata> {
        return this._eventTiersState.getEventTiersList$().pipe(getMetadata());
    }

    isEventTiersListInProgress$(): Observable<boolean> {
        return this._eventTiersState.isEventTiersListInProgress$();
    }

    isEventTiersListSaveInProgress$(): Observable<boolean> {
        return this._eventTiersState.isEventTiersListSaveInProgress$();
    }

    saveEventTier(eventId: string, tierId: string, tier: PutEventTier): Observable<void> {
        this._eventTiersState.setEventTiersListError(null);
        this._eventTiersState.setEventTiersListSaveInProgress(true);
        return this._eventTiersApi.putEventTier(eventId, tierId, tier)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTiersListError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTiersListSaveInProgress(false))
            );
    }

    createEventTier(eventId: string, eventTier: PostEventTier): Observable<number> {
        this._eventTiersState.setEventTiersListError(null);
        this._eventTiersState.setEventTiersListSaveInProgress(true);
        return this._eventTiersApi.postEventTier(eventId, eventTier)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTiersListError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTiersListSaveInProgress(false))
            );
    }

    deleteEventTier(eventId: string, tierId: string): Observable<any> {
        this._eventTiersState.setEventTiersListError(null);
        this._eventTiersState.setEventTiersListSaveInProgress(true);
        return this._eventTiersApi.deleteEventTier(eventId, tierId)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTiersListError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTiersListSaveInProgress(false))
            );
    }

    loadEventTier(eventId: string, tierId: string): void {
        this._eventTiersState.setEventTierError(null);
        this._eventTiersState.setEventTierInProgress(true);
        this._eventTiersApi.getEventTier(eventId, tierId)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    return of(null);
                }),
                finalize(() => this._eventTiersState.setEventTierInProgress(false))
            )
            .subscribe(tier =>
                this._eventTiersState.setEventTier(tier)
            );
    }

    clearEventTier(): void {
        this._eventTiersState.setEventTier(null);
    }

    getEventTier$(): Observable<EventTiers> {
        return this._eventTiersState.getEventTier$();
    }

    isEventTierInProgress$(): Observable<boolean> {
        return this._eventTiersState.isEventTierInProgress$();
    }

    isEventTierSaveInProgress$(): Observable<boolean> {
        return this._eventTiersState.isEventTierSaveInProgress$();
    }

    getEventTiersListError$(): Observable<HttpErrorResponse> {
        return this._eventTiersState.getEventTiersListError$();
    }

    createEventTierQuota(eventId: string, tierId: string, quota: PostEventTierQuota): Observable<void> {
        this._eventTiersState.setEventTierError(null);
        this._eventTiersState.setEventTierSaveInProgress(true);
        return this._eventTiersApi.postEventTierQuota(eventId, tierId, quota)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTierSaveInProgress(false))
            );
    }

    updateEventTierQuota(eventId: string, tierId: string, quotaId: string, quota: PutEventTierQuota): Observable<void> {
        this._eventTiersState.setEventTierError(null);
        this._eventTiersState.setEventTierSaveInProgress(true);
        return this._eventTiersApi.putEventTierQuota(eventId, tierId, quotaId, quota)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTierSaveInProgress(false))
            );
    }

    deleteEventTierQuota(eventId: string, tierId: string, quotaId: string): Observable<void> {
        this._eventTiersState.setEventTierError(null);
        this._eventTiersState.setEventTierSaveInProgress(true);
        return this._eventTiersApi.deleteEventTierQuota(eventId, tierId, quotaId)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTierSaveInProgress(false))
            );
    }

    getEventTierError$(): Observable<HttpErrorResponse> {
        return this._eventTiersState.getEventTierError$();
    }

    deleteTierLimit(eventId: string, tierId: string): Observable<void> {
        this._eventTiersState.setEventTierLimitRemoveInProgress(true);
        this._eventTiersState.setEventTierError(null);
        return this._eventTiersApi.deleteTierLimit(eventId, tierId)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setEventTierLimitRemoveInProgress(false))
            );
    }

    isEventTierLimitRemoveInProgress$(): Observable<boolean> {
        return this._eventTiersState.isEventTierLimitRemoveInProgress$();
    }

    loadTierChannelContent(eventId: string, tierId: string): void {
        this._eventTiersState.setTiersChannelContentsInProgress(true);
        this._eventTiersApi.getTiersChannelContents(eventId, tierId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._eventTiersState.setTiersChannelContentsInProgress(false))
            )
            .subscribe(priceTypes => this._eventTiersState.setTiersTypeChannelContent(priceTypes));
    }

    getTierChannelContent$(): Observable<EventTiersChannelContent[]> {
        return this._eventTiersState.getTiersChannelContents$();
    }

    updateTierChannelContent(eventId: string, tierId: string, content: EventTiersChannelContent[]): Observable<void> {
        this._eventTiersState.setTiersChannelContentsInProgress(true);
        this._eventTiersState.setEventTierError(null);
        return this._eventTiersApi.postTiersTypeChannelContent(eventId, tierId, content)
            .pipe(
                catchError(error => {
                    this._eventTiersState.setEventTierError(error);
                    throw error;
                }),
                finalize(() => this._eventTiersState.setTiersChannelContentsInProgress(false))
            );
    }

    isTierChannelContentInProgress$(): Observable<boolean> {
        return this._eventTiersState.isTiersChannelContentsInProgress$();
    }

    clearTierContentData(): void {
        this._eventTiersState.setTiersTypeChannelContent(null);
        this._templateChange.next();
    }

}

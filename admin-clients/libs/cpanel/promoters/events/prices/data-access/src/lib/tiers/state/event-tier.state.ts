import { Injectable } from '@angular/core';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { EventTiersChannelContent } from '../models/event-tiers-channel-content.model';
import { EventTiers } from '../models/event-tiers.model';
import { GetEventTiersResponse } from '../models/get-event-tiers-response.model';

@Injectable({
    providedIn: 'root'
})
export class EventTierState {
    // event tiers
    private _eventTiersList: BaseStateProp<GetEventTiersResponse> = new BaseStateProp<GetEventTiersResponse>();
    readonly setEventTiersList = this._eventTiersList.setValueFunction();
    readonly getEventTiersList$ = this._eventTiersList.getValueFunction();
    readonly setEventTiersListInProgress = this._eventTiersList.setInProgressFunction();
    readonly isEventTiersListInProgress$ = this._eventTiersList.getInProgressFunction();
    readonly setEventTiersListError = this._eventTiersList.setErrorFunction();
    readonly getEventTiersListError$ = this._eventTiersList.getErrorFunction();

    private _eventTiersListSave = new BaseStateProp<void>();
    readonly isEventTiersListSaveInProgress$ = this._eventTiersListSave.getInProgressFunction();
    readonly setEventTiersListSaveInProgress = this._eventTiersListSave.setInProgressFunction();

    // event tier
    private _eventTier: BaseStateProp<EventTiers> = new BaseStateProp<EventTiers>();
    readonly setEventTier = this._eventTier.setValueFunction();
    readonly getEventTier$ = this._eventTier.getValueFunction();
    readonly setEventTierInProgress = this._eventTier.setInProgressFunction();
    readonly isEventTierInProgress$ = this._eventTier.getInProgressFunction();
    readonly setEventTierError = this._eventTier.setErrorFunction();
    readonly getEventTierError$ = this._eventTier.getErrorFunction();

    private _eventTierSave = new BaseStateProp<void>();
    readonly isEventTierSaveInProgress$ = this._eventTierSave.getInProgressFunction();
    readonly setEventTierSaveInProgress = this._eventTierSave.setInProgressFunction();

    private _eventTierLimitRemove = new BaseStateProp<void>();
    readonly isEventTierLimitRemoveInProgress$ = this._eventTierLimitRemove.getInProgressFunction();
    readonly setEventTierLimitRemoveInProgress = this._eventTierLimitRemove.setInProgressFunction();

    // event tier channel content
    private _eventTiersTypeChannelContent = new BaseStateProp<EventTiersChannelContent[]>();
    readonly setTiersTypeChannelContent = this._eventTiersTypeChannelContent.setValueFunction();
    readonly getTiersChannelContents$ = this._eventTiersTypeChannelContent.getValueFunction();
    readonly isTiersChannelContentsInProgress$ = this._eventTiersTypeChannelContent.getInProgressFunction();
    readonly setTiersChannelContentsInProgress = this._eventTiersTypeChannelContent.setInProgressFunction();
    readonly setTiersTypeChannelContentError = this._eventTiersTypeChannelContent.setErrorFunction();
    readonly getTiersChannelContentsError$ = this._eventTiersTypeChannelContent.getErrorFunction();
}

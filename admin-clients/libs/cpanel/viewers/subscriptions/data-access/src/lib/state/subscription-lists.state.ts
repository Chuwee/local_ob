import { StateProperty } from '@OneboxTM/utils-state';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { SubscriptionListLoadCase } from '../models/subscription-list-load.case';
import { SubscriptionList } from '../models/subscription-list.model';

@Injectable({
    providedIn: 'root'
})
export class SubscriptionListsState {
    // SUBSCRIPTION LIST
    readonly subscriptionList = new StateProperty<SubscriptionList[]>();
    private _subscriptionListsList = new BaseStateProp<SubscriptionList[]>();
    readonly setSubscriptionListsList = this._subscriptionListsList.setValueFunction();
    readonly getSubscriptionListsList$ = this._subscriptionListsList.getValueFunction();
    readonly setSubscriptionListsListLoading = this._subscriptionListsList.setInProgressFunction();
    readonly isSubscriptionListsListLoading$ = this._subscriptionListsList.getInProgressFunction();

    // SUBSCRIPTION LIST DETAIL
    private _subscriptionList = new BaseStateProp<SubscriptionList>();
    readonly setSubscriptionList = this._subscriptionList.setValueFunction();
    readonly getSubscriptionList$ = this._subscriptionList.getValueFunction();
    readonly setSubscriptionListLoading = this._subscriptionList.setInProgressFunction();
    readonly isSubscriptionListLoading$ = this._subscriptionList.getInProgressFunction();
    readonly setSubscriptionListError = this._subscriptionListsList.setErrorFunction();
    readonly getSubscriptionListError$ = this._subscriptionList.getErrorFunction();

    // LIST DETAIL STATE
    private _listDetailState = new BaseStateProp<SubscriptionListLoadCase>(SubscriptionListLoadCase.none);
    readonly getListDetailState$ = this._listDetailState.getValueFunction();
    readonly setListDetailState = this._listDetailState.setValueFunction();
}

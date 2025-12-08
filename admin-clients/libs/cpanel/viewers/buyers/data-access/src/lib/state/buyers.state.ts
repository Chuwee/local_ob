import { StateProperty } from '@OneboxTM/utils-state';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { Buyer } from '../models/buyer.model';
import { BuyersFilterContent } from '../models/buyers-filter-content.model';
import { BuyersQueryList } from '../models/buyers-query-list.model';
import { BuyersQueryWrapper } from '../models/buyers-query-wrapper.model';
import { GetBuyerResponse } from '../models/get-buyer-response.model';
import { BuyerOrderItemList } from '../models/order-items/buyer-order-item-list.model';

@Injectable({
    providedIn: 'root'
})
export class BuyersState {
    // LIST
    private readonly _buyersList = new BaseStateProp<GetBuyerResponse>();
    readonly getBuyersList$ = this._buyersList.getValueFunction();
    readonly setBuyersList = this._buyersList.setValueFunction();
    readonly isBuyersListLoading$ = this._buyersList.getInProgressFunction();
    readonly setBuyersListLoading = this._buyersList.setInProgressFunction();
    // single buyer
    private readonly _buyer = new BaseStateProp<Buyer>();
    readonly getBuyer$ = this._buyer.getValueFunction();
    readonly setBuyer = this._buyer.setValueFunction();
    readonly getBuyerError$ = this._buyer.getErrorFunction();
    readonly setBuyerError = this._buyer.setErrorFunction();
    readonly isBuyerLoading$ = this._buyer.getInProgressFunction();
    readonly setBuyerLoading = this._buyer.setInProgressFunction();
    // ORDER ITEMS
    readonly orderItemsSeat = new StateProperty<BuyerOrderItemList>();
    readonly orderItemsProduct = new StateProperty<BuyerOrderItemList>();
    // updating
    private readonly _buyerUpdating = new BaseStateProp<void>();
    readonly isBuyerUpdating$ = this._buyerUpdating.getInProgressFunction();
    readonly setBuyerUpdating = this._buyerUpdating.setInProgressFunction();
    // deleting
    private readonly _buyerDeleting = new BaseStateProp<void>();
    readonly isBuyerDeleting$ = this._buyerDeleting.getInProgressFunction();
    readonly setBuyerDeleting = this._buyerDeleting.setInProgressFunction();
    // FILTER CONTENTS
    // CHANNELS
    private readonly _filterChannels = new BaseStateProp<BuyersFilterContent>();
    readonly getFilterChannels$ = this._filterChannels.getValueFunction();
    readonly setFilterChannels = this._filterChannels.setValueFunction();
    readonly isFilterChannelsLoading$ = this._filterChannels.getInProgressFunction();
    readonly setFilterChannelsLoading = this._filterChannels.setInProgressFunction();
    // EVENTS
    private readonly _filterEvents = new BaseStateProp<BuyersFilterContent>();
    readonly getFilterEvents$ = this._filterEvents.getValueFunction();
    readonly setFilterEvents = this._filterEvents.setValueFunction();
    readonly isFilterEventsLoading$ = this._filterEvents.getInProgressFunction();
    readonly setFilterEventsLoading = this._filterEvents.setInProgressFunction();
    // SESSIONS
    private readonly _filterSessions = new BaseStateProp<BuyersFilterContent>();
    readonly getFilterSessions$ = this._filterSessions.getValueFunction();
    readonly setFilterSessions = this._filterSessions.setValueFunction();
    readonly isFilterSessionsLoading$ = this._filterSessions.getInProgressFunction();
    readonly setFilterSessionsLoading = this._filterSessions.setInProgressFunction();
    // COLLECTIVE LISTS
    private readonly _filterCollectives = new BaseStateProp<BuyersFilterContent>();
    readonly getFilterCollectives$ = this._filterCollectives.getValueFunction();
    readonly setFilterCollectives = this._filterCollectives.setValueFunction();
    readonly isFilterCollectivesLoading$ = this._filterCollectives.getInProgressFunction();
    readonly setFilterCollectivesLoading = this._filterCollectives.setInProgressFunction();
    // SUBSCRIPTIONS LISTS
    private readonly _filterSubscriptionLists = new BaseStateProp<BuyersFilterContent>();
    readonly getFilterSubscriptionLists$ = this._filterSubscriptionLists.getValueFunction();
    readonly setFilterSubscriptionLists = this._filterSubscriptionLists.setValueFunction();
    readonly isFilterSubscriptionListsLoading$ = this._filterSubscriptionLists.getInProgressFunction();
    readonly setFilterSubscriptionListsLoading = this._filterSubscriptionLists.setInProgressFunction();
    // EXPORT
    private readonly _exportBuyers = new BaseStateProp<void>();
    readonly setExportBuyersLoading = this._exportBuyers.setInProgressFunction();
    // QUERY LIST
    private readonly _queries = new BaseStateProp<BuyersQueryList>();
    readonly getQueries$ = this._queries.getValueFunction();
    readonly setQueries = this._queries.setValueFunction();
    readonly isQueriesLoading$ = this._queries.getInProgressFunction();
    readonly setQueriesLoading = this._queries.setInProgressFunction();
    // LOADED QUERY
    private readonly _query = new BaseStateProp<BuyersQueryWrapper>({ query: { limit: undefined, offset: undefined } });
    readonly getQuery$ = this._query.getValueFunction();
    readonly setQuery = this._query.setValueFunction();
    readonly isQueryLoading$ = this._query.getInProgressFunction();
    readonly setQueryLoading = this._query.setInProgressFunction();
    // SAVING QUERY
    private readonly _savingQuery = new BaseStateProp<void>();
    readonly isQuerySaving$ = this._savingQuery.getInProgressFunction();
    readonly setQuerySaving = this._savingQuery.setInProgressFunction();
    // DELETING QUERY
    private readonly _deletingQuery = new BaseStateProp<void>();
    readonly isQueryDeleting$ = this._deletingQuery.getInProgressFunction();
    readonly setQueryDeleting = this._deletingQuery.setInProgressFunction();
}

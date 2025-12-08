import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, ReplaySubject } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { SubscriptionListsApi } from './api/subscriptionLists.api';
import { GetSubscriptionListsRequest } from './models/get-subscription-lists-request.model';
import { PostSubscriptionList } from './models/post-subscription-list.model';
import { PutSubscriptionList } from './models/put-subscription-list.model';
import { SubscriptionListFilter } from './models/subscription-list-filters.model';
import { SubscriptionList } from './models/subscription-list.model';
import { SubscriptionListsState } from './state/subscription-lists.state';

@Injectable({
    providedIn: 'root'
})
export class SubscriptionListsService {
    private readonly _api = inject(SubscriptionListsApi);
    private readonly _state = inject(SubscriptionListsState);

    private _subscriptionListFilters = new ReplaySubject<SubscriptionListFilter>(1);
    private _subscriptionListFilters$ = this._subscriptionListFilters.asObservable();

    loadSubscriptionListsList(request: GetSubscriptionListsRequest): void {
        this._state.setSubscriptionListsListLoading(true);
        this._api.getSubscriptionLists(request)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setSubscriptionListsListLoading(false))
            )
            .subscribe(subscriptionList =>
                this._state.setSubscriptionListsList(subscriptionList)
            );
    }

    clearSubscriptionListsList(): void {
        this._state.setSubscriptionListsList(null);
    }

    getSubscriptionListsListData$(): Observable<SubscriptionList[]> {
        return this._state.getSubscriptionListsList$();
    }

    isSubscriptionListsListLoading$(): Observable<boolean> {
        return this._state.isSubscriptionListsListLoading$();
    }

    loadSubscriptionList(id: number): void {
        this._state.setSubscriptionListError(null);
        this._state.setSubscriptionListLoading(true);
        this._api.getSubscriptionList(id)
            .pipe(
                catchError(error => {
                    this._state.setSubscriptionListError(error);
                    return of(null);
                }),
                finalize(() => this._state.setSubscriptionListLoading(false))
            )
            .subscribe(subscriptionList =>
                this._state.setSubscriptionList(subscriptionList)
            );
    }

    clearSubscriptionList(): void {
        this._state.setSubscriptionList(null);
    }

    getSubscriptionList$(): Observable<SubscriptionList> {
        return this._state.getSubscriptionList$();
    }

    getSubscriptionListError$(): Observable<HttpErrorResponse> {
        return this._state.getSubscriptionListError$();
    }

    isSubscriptionListLoading$(): Observable<boolean> {
        return this._state.isSubscriptionListLoading$();
    }

    setSubscriptionListFilters(value: SubscriptionListFilter): void {
        this._subscriptionListFilters.next(value);
    }

    getSubscriptionListFilters$(): Observable<SubscriptionListFilter> {
        return this._subscriptionListFilters$;
    }

    deleteSubscriptionList(id: number): Observable<void> {
        return this._api.deleteSubscriptionList(id);
    }

    createSubscriptionList(subscriptionList: PostSubscriptionList): Observable<number> {
        this._state.setSubscriptionListLoading(true);
        return this._api.postSubscriptionList(subscriptionList)
            .pipe(
                map(result => result.id),
                finalize(() => this._state.setSubscriptionListLoading(false))
            );
    }

    saveSubscriptionList(id: number, subscriptionList: PutSubscriptionList): Observable<void> {
        this._state.setSubscriptionListLoading(true);
        return this._api.putSubscriptionList(id, subscriptionList)
            .pipe(finalize(() => this._state.setSubscriptionListLoading(false)));
    }
}

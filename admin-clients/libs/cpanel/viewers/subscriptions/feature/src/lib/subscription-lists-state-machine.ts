import {
    SubscriptionListFilter, SubscriptionListLoadCase, SubscriptionListsService, SubscriptionList, SubscriptionListsState
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { GuardsCheckEnd, Router } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, take, takeUntil, tap } from 'rxjs/operators';

export type SubscriptionListStateParams = {
    state: SubscriptionListLoadCase;
    idPath?: string;
    entityId?: string;
};

@Injectable()
export class SubscriptionListsStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: number;
    private _subscriptionListId: number;
    private _subscriptionFilters: SubscriptionListFilter;

    constructor(
        private _subscriptionListsState: SubscriptionListsState,
        private _subscriptionListsSrv: SubscriptionListsService,
        private _router: Router
    ) {
        combineLatest([
            this._subscriptionListsSrv.getSubscriptionListFilters$(),
            this.getListDetailState$()
        ]).pipe(
            filter(([_, state]) => state !== null),
            tap(([filters, state]) => {
                this._subscriptionFilters = filters || {};
                switch (state) {
                    case SubscriptionListLoadCase.loadSubscriptionList:
                        this.loadSubscriptionListsList();
                        break;
                    case SubscriptionListLoadCase.loadWithoutNavigating:
                        this.loadSubscriptionLists();
                        break;
                    case SubscriptionListLoadCase.selectedSubscriptionList:
                        this.selectedSubscriptionList();
                        break;
                    case SubscriptionListLoadCase.none:
                    default:
                        break;
                }
            }),
            takeUntil(this._onDestroy)
        ).subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    clearCurrentState(): void {
        this._subscriptionListsState.setListDetailState(SubscriptionListLoadCase.none);
    }

    setCurrentState({ state, idPath }: SubscriptionListStateParams): void {
        this._idPath = Number(idPath);
        this._subscriptionListsState.setListDetailState(state);
    }

    getListDetailState$(): Observable<SubscriptionListLoadCase> {
        return this._subscriptionListsState.getListDetailState$();
    }

    private loadSubscriptionListsList(): void {
        this.loadSubscriptionLists();
        this.getSubscriptionList()
            .pipe(
                tap(subscriptionListsList => {
                    if (subscriptionListsList.length) {
                        this.setSubscriptionListIdOfSubscriptionListsList(subscriptionListsList);
                        this.loadSubscriptionListDetail();
                        this.navigateToSubscriptionList();
                    }
                })
            ).subscribe();
    }

    private selectedSubscriptionList(): void {
        this.getSubscriptionList()
            .pipe(
                tap(subscriptionList => {
                    if (subscriptionList.length) {
                        this.setSubscriptionListId(this._idPath);
                        this.navigateToSubscriptionList();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first(event => event instanceof GuardsCheckEnd),
            tap(event => {
                if (event.shouldActivate) {
                    this.loadSubscriptionListDetail();
                }
            })
        ).subscribe();
    }

    private setSubscriptionListId(subscriptionListId: number): void {
        this._subscriptionListId = subscriptionListId;
    }

    private setSubscriptionListIdOfSubscriptionListsList(subscriptionLists: SubscriptionList[]): void {
        if (this._idPath && !!subscriptionLists.length &&
            subscriptionLists.some(subscriptionFromList => subscriptionFromList.id === this._idPath)
        ) {
            this.setSubscriptionListId(this._idPath);
        } else {
            this.setSubscriptionListId(subscriptionLists[0].id);
        }
    }

    private navigateToSubscriptionList(): void {
        this._router.navigate(['/subscription-lists', this._subscriptionListId]);
    }

    private loadSubscriptionListDetail(): void {
        this._subscriptionListsSrv.clearSubscriptionList();
        this._subscriptionListsSrv.loadSubscriptionList(this._subscriptionListId);
    }

    private loadSubscriptionLists(): void {
        this._subscriptionListsSrv.clearSubscriptionListsList();
        this._subscriptionListsSrv.loadSubscriptionListsList(this._subscriptionFilters);
    }

    private getSubscriptionList(): Observable<SubscriptionList[]> {
        return this._subscriptionListsSrv.getSubscriptionListsListData$()
            .pipe(
                first(subscriptionList => !!subscriptionList),
                take(1)
            );
    }
}


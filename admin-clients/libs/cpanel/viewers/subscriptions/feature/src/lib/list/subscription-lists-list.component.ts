/* eslint-disable @typescript-eslint/dot-notation */
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    SubscriptionListLoadCase, SubscriptionListsService, SubscriptionList
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { SubscriptionListsStateMachine } from '../subscription-lists-state-machine';

@Component({
    selector: 'app-subscription-lists-list',
    templateUrl: './subscription-lists-list.component.html',
    styleUrls: ['./subscription-lists-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionListsListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    canLoggedUserWrite$: Observable<boolean>;
    isLoadingList$: Observable<boolean>;
    subscriptionListsList$: Observable<SubscriptionList[]>;
    selectedSubscriptionListId: number;
    subscriptionList$: Observable<SubscriptionList>;
    readonly dateTimeFormats = DateTimeFormats;

    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0]?.params['tourId'] || undefined;
    }

    constructor(
        private _subscriptionListsSrv: SubscriptionListsService,
        private _route: ActivatedRoute,
        private _ephemeralMessageService: EphemeralMessageService,
        private _auth: AuthenticationService,
        private _msgDialog: MessageDialogService,
        private _subscriptionListSM: SubscriptionListsStateMachine
    ) { }

    ngOnInit(): void {
        this.init();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._subscriptionListsSrv.clearSubscriptionListsList();
        this._subscriptionListSM.clearCurrentState();
    }

    openDeleteSubscriptionListDialog(): void {
        this.subscriptionList$
            .pipe(
                take(1),
                switchMap(subscriptionList =>
                    this._msgDialog.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_SUBSCRIPTION_LIST',
                        message: 'SUBSCRIPTION_LIST.DELETE_SUBSCRIPTION_LIST_WARNING',
                        messageParams: { name: subscriptionList.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                        .pipe(
                            switchMap(success => {
                                if (!success) {
                                    return of(null);
                                } else {
                                    return this._subscriptionListsSrv.deleteSubscriptionList(subscriptionList.id)
                                        .pipe(
                                            tap(() => {
                                                this._ephemeralMessageService.showSuccess({
                                                    msgKey: 'SUBSCRIPTION_LIST.DELETE_SUCCESS',
                                                    msgParams: subscriptionList
                                                });
                                                this._subscriptionListSM.setCurrentState({
                                                    state: SubscriptionListLoadCase.loadSubscriptionList
                                                });
                                            })
                                        );
                                }
                            })
                        )
                )
            ).subscribe();
    }

    selectionChangeHandler(subscriptionListId: number): void {
        if (!!subscriptionListId && this.selectedSubscriptionListId !== subscriptionListId) {
            this._subscriptionListSM.setCurrentState({
                state: SubscriptionListLoadCase.selectedSubscriptionList,
                idPath: subscriptionListId.toString()
            });
        }
    }

    private loadDataHandler(): void {
        this._subscriptionListSM.getListDetailState$()
            .pipe(
                tap(state => {
                    if (state === SubscriptionListLoadCase.none) {
                        this._subscriptionListSM.setCurrentState({
                            state: SubscriptionListLoadCase.loadSubscriptionList,
                            idPath: this._idPath
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();

        this.subscriptionList$
            .pipe(
                withLatestFrom(this._subscriptionListSM.getListDetailState$()),
                tap(([subscriptionList, state]) => {
                    this.selectedSubscriptionListId = subscriptionList.id;
                    if (state === SubscriptionListLoadCase.loadSubscriptionList) {
                        this.scrollToSelectedSubscriptionList(subscriptionList.id);
                    }
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }

    private init(): void {
        this.isLoadingList$ = combineLatest([
            this._subscriptionListsSrv.isSubscriptionListsListLoading$()
        ]).pipe(
            map(loadings => loadings.some(loading => loading)),
            distinctUntilChanged(),
            shareReplay(1)
        );
        this.subscriptionListsList$ = this._subscriptionListsSrv.getSubscriptionListsListData$()
            .pipe(
                filter(subscriptionList => !!subscriptionList)
            );
        this.subscriptionList$ = this._subscriptionListsSrv.getSubscriptionList$()
            .pipe(
                filter(subscriptionList => !!subscriptionList),
                shareReplay(1)
            );
        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$().pipe(first(user => user !== null));

        const writingRoles = [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.CRM_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );
    }

    private scrollToSelectedSubscriptionList(subscriptionListId: number): void {
        setTimeout(() => {
            const element = document.getElementById('subscription-list-option-' + subscriptionListId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}

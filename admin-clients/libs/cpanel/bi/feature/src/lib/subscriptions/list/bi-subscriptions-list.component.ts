import { BiService, BiSubscription } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { biSubmit } from '@admin-clients/cpanel-bi-utility-utils';
import {
    EmptyStateComponent, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, isHandset$ } from '@admin-clients/shared/utility/utils';
import { Platform } from '@angular/cdk/platform';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { delay, EMPTY, filter, first, map, switchMap, tap } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { BiImpersonationComponent } from '../../impersonation/bi-impersonation.component';
import { BiSubscriptionsListCardsComponent } from './cards/bi-subscriptions-list-cards.component';
import { BiSubscriptionsListTableComponent } from './table/bi-subscriptions-list-table.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-subscriptions-list',
    imports: [
        AsyncPipe, FlexLayoutModule, TranslatePipe, BiSubscriptionsListTableComponent, BiSubscriptionsListCardsComponent,
        EmptyStateComponent, BiImpersonationComponent, MatButton, MatProgressSpinner, MatIcon
    ],
    providers: [
        ListFiltersService
    ],
    templateUrl: './bi-subscriptions-list.component.html'
})
export class BiSubscriptionsListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #biSubsSrv = inject(BiService);
    readonly #entityUsersSrv = inject(EntityUsersService);
    readonly #auth = inject(AuthenticationService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #platform = inject(Platform);
    readonly #destroyRef = inject(DestroyRef);

    @ViewChild(BiImpersonationComponent) private readonly _biImpersonationComponent: BiImpersonationComponent;

    readonly canImpersonate$ = this.#auth.getLoggedUser$().pipe(first(), map(user => AuthenticationService.canBiImpersonate(user)));
    readonly isHandset$ = isHandset$().pipe(shareReplay({ bufferSize: 1, refCount: true }));

    readonly isLoading$ = booleanOrMerge([
        this.#biSubsSrv.subscriptions.loading$(),
        this.#entityUsersSrv.isEntityUserLoading$()
            // delay because of ExpressionChangedAfterItHasBeenCheckedError
            // from applyFiltersByUrlParams$ from bi-impersonation
            .pipe(delay(0))
    ]);

    readonly subscriptions$ = this.#biSubsSrv.subscriptions.get$().pipe(filter(Boolean));

    constructor(
        router: Router,
        route: ActivatedRoute
    ) {
        super();
        this.#auth.getLoggedUser$()
            .pipe(
                first(),
                switchMap(user => {
                    if (!AuthenticationService.canBiImpersonate(user)) return EMPTY;

                    return this.#auth.impersonation.get$()
                        .pipe(
                            first(),
                            map(impersonation => {
                                if (impersonation) return impersonation;

                                this.#auth.impersonation.set(user.id.toString());
                                return user.id.toString();
                            })
                        );
                })
            )
            .subscribe(userId => {
                const urlParameters = Object.assign({}, route.snapshot.queryParams);
                if (!urlParameters['userId']) {
                    urlParameters['userId'] = userId;
                    router.navigate(['.'], { relativeTo: route, queryParams: urlParameters });
                }
            });
    }

    ngOnInit(): void {
        this.#auth.getLoggedUser$()
            .pipe(first())
            .subscribe(user => {
                if (AuthenticationService.canBiImpersonate(user)) return;
                this.#biSubsSrv.subscriptions.load();
            });
    }

    ngAfterViewInit(): void {
        this.#auth.getLoggedUser$()
            .pipe(first())
            .subscribe(user => {
                if (!AuthenticationService.canBiImpersonate(user)) return;
                this.initListFilteredComponent([this._biImpersonationComponent]);
            });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#biSubsSrv.subscriptions.clear();
    }

    loadData(filters: FilterItem[]): void {
        const impersonation = filters[0].values?.[0].value;
        this.#auth.impersonation.set(impersonation);
        this.#biSubsSrv.subscriptions.load({ impersonation });
    }

    editSubscriptions(): void {
        this.#biSubsSrv.subscriptionsLink.clear();
        this.#auth.impersonation.get$()
            .pipe(
                first(),
                switchMap(impersonation => {
                    this.#biSubsSrv.subscriptionsLink.load(impersonation);
                    return this.#biSubsSrv.subscriptionsLink.get$()
                        .pipe(
                            first(Boolean),
                            takeUntilDestroyed(this.#destroyRef),
                            switchMap(subscriptionLink => this.#auth.getLoggedUser$()
                                .pipe(
                                    first(),
                                    tap(user => {
                                        biSubmit(subscriptionLink.url, user.reports.load, user.reports.logout, this.#platform);
                                    })
                                ))
                        );
                })
            )
            .subscribe();
    }

    deleteSubscription(subscription: BiSubscription): void {
        this.#msgDialogSrv.showDeleteConfirmation({
            confirmation: {
                title: 'BI.SUBSCRIPTIONS.TITLES.DELETE_SUBS',
                message: 'BI.SUBSCRIPTIONS.FORMS.INFOS.DELETE_SUBS',
                messageParams: { subscription: subscription.name }
            },
            delete$: this.#auth.impersonation.get$()
                .pipe(
                    first(),
                    switchMap(impersonation =>
                        this.#biSubsSrv.subscription.delete(subscription.id, impersonation)
                            .pipe(
                                tap(() => {
                                    this.#ephemeralMsgSrv.showSuccess({
                                        msgKey: 'BI.SUBSCRIPTIONS.DELETE_SUBS_SUCCESS',
                                        msgParams: { subscription: subscription.name }
                                    });
                                    this.#biSubsSrv.subscriptions.load({ impersonation });
                                })
                            )
                    )
                )
        });
    }

    sendSubscription(subscription: BiSubscription): void {
        this.#auth.impersonation.get$()
            .pipe(
                first(),
                switchMap(impersonation => this.#biSubsSrv.subscription.send(subscription.id, impersonation))
            )
            .subscribe(() => {
                this.#ephemeralMsgSrv.showSuccess({
                    msgKey: 'BI.SUBSCRIPTIONS.SEND_SUBS_SUCCESS',
                    msgParams: { subscription: subscription.name }
                });
            });
    }
}

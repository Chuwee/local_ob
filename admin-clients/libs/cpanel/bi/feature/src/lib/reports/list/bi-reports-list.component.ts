import { BiService } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { FilterItem, ListFilteredComponent, ListFiltersService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, isHandset$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { delay, EMPTY, filter, first, map, switchMap } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { BiImpersonationComponent } from '../../impersonation/bi-impersonation.component';
import { BiReportsListRecentsComponent } from './recent/bi-reports-list-recents.component';
import { BiReportsListSearchInputComponent } from './search-input/bi-reports-list-search-input.component';
import { BiReportsListSearchResultsComponent } from './search-results/bi-reports-list-search-results.component';
import { BiReportsListTabsComponent } from './tabs/bi-reports-list-tabs.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-reports-list',
    imports: [
        AsyncPipe, FlexLayoutModule, TranslatePipe, FormContainerComponent, BiReportsListTabsComponent, BiReportsListRecentsComponent,
        BiReportsListSearchResultsComponent, ReactiveFormsModule, BiReportsListSearchInputComponent, BiImpersonationComponent,
        MatProgressSpinner
    ],
    providers: [ListFiltersService],
    templateUrl: './bi-reports-list.component.html'
})
export class BiReportsListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    private readonly _biService = inject(BiService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _entityUsersSrv = inject(EntityUsersService);

    @ViewChild(BiImpersonationComponent) private readonly _biImpersonationComponent: BiImpersonationComponent;

    readonly canImpersonate$ = this._auth.getLoggedUser$().pipe(first(), map(user => AuthenticationService.canBiImpersonate(user)));
    readonly isHandset$ = isHandset$().pipe(shareReplay({ bufferSize: 1, refCount: true }));

    readonly isLoading$ = booleanOrMerge([
        this._biService.reportsList.loading$(),
        this._biService.reportsSearch.loading$(),
        this._biService.reportsHistoryList.loading$(),
        this._biService.report.loading$(),
        this._biService.reportPrompts.loading$(),
        this._entityUsersSrv.isEntityUserLoading$()
            // delay because of ExpressionChangedAfterItHasBeenCheckedError
            // from applyFiltersByUrlParams$ from bi-impersonation
            .pipe(delay(0))
    ]);

    readonly recentReports$ = this._biService.reportsHistoryList.get$().pipe(filter(Boolean));
    readonly reports$ = this._biService.reportsList.get$().pipe(filter(Boolean));

    readonly notReportsSearch$ = this._biService.reportsSearch.get$().pipe(map(biReports => !biReports));

    constructor(
        router: Router,
        route: ActivatedRoute
    ) {
        super();
        this._auth.getLoggedUser$()
            .pipe(
                first(),
                switchMap(user => {
                    if (!AuthenticationService.canBiImpersonate(user)) return EMPTY;

                    return this._auth.impersonation.get$()
                        .pipe(
                            first(),
                            map(impersonation => {
                                if (impersonation) return impersonation;

                                this._auth.impersonation.set(user.id.toString());
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
        this._auth.getLoggedUser$()
            .pipe(first())
            .subscribe(user => {
                if (AuthenticationService.canBiImpersonate(user)) return;
                this._biService.reportsList.load();
                this._biService.reportsHistoryList.load();
            });
    }

    ngAfterViewInit(): void {
        this._auth.getLoggedUser$()
            .pipe(first())
            .subscribe(user => {
                if (!AuthenticationService.canBiImpersonate(user)) return;
                this.initListFilteredComponent([this._biImpersonationComponent]);
            });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._biService.reportsHistoryList.clear();
        this._biService.reportsList.clear();
        this._biService.reportsSearch.clear();
        this._entityUsersSrv.clearEntityUser();
    }

    loadData(filters: FilterItem[]): void {
        const impersonation = filters[0].values?.[0].value;
        this._auth.impersonation.set(impersonation);
        this._biService.reportsList.load({ impersonation });
        this._biService.reportsHistoryList.load({ impersonation });
        this._biService.reportsSearch.clear();
    }
}

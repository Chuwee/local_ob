import {
    ListFilteredComponent, ListFiltersService, SortFilterComponent, PaginatorComponent, FilterItem, ContextNotificationComponent,
    DateTimeModule, SearchInputComponent, ChipsComponent, CopyTextComponent, DialogSize, EphemeralMessageService, MessageDialogService, PopoverFilterDirective, PopoverComponent, ChipsFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SupplierSelectionButtonComponent } from '@admin-clients/shi-panel/feature-supplier-selection';
import { GetMatchingsRequest, Matching, SupplierName, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { BlacklistedMatchingsService } from '../blacklist-candidates.service';
import { BlacklistedMatchingsListFilterComponent } from './filter/blacklisted-matchings-list-filter.component';

const PAGE_SIZE = 20;
const EXCLUDED_FILTER_KEYS = ['q', 'supplier', 'sort', 'limit', 'offset', 'aggs'];

@Component({
    imports: [
        CommonModule, TranslatePipe, PaginatorComponent, FlexLayoutModule, ContextNotificationComponent,
        RouterModule, DateTimeModule, SharedUtilityDirectivesModule, SearchInputComponent,
        BlacklistedMatchingsListFilterComponent, ChipsComponent, DateTimePipe, MatDialogModule, SupplierSelectionButtonComponent,
        MatProgressSpinnerModule, MatTableModule, MatSortModule, MatTooltipModule, CopyTextComponent, MatIconModule, MatButtonModule,
        PopoverFilterDirective, PopoverComponent, ChipsFilterDirective
    ],
    selector: 'app-blacklisted-matchings-list',
    templateUrl: './blacklisted-matchings-list.component.html',
    styleUrls: ['./blacklisted-matchings-list.component.scss'],
    providers: [ListFiltersService, MatDialogModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MatchingsListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _blacklistedMatchingsService = inject(BlacklistedMatchingsService);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _selectedSupplierName = new BehaviorSubject<string>(SupplierName.tevo);
    private readonly _authService = inject(AuthenticationService);
    private readonly _msgDialogSrv = inject(MessageDialogService);

    private _request: GetMatchingsRequest;
    private _sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(BlacklistedMatchingsListFilterComponent) private readonly _filterComponent: BlacklistedMatchingsListFilterComponent;
    @ViewChild(SupplierSelectionButtonComponent) private readonly _supplierSelectButtonComponent: SupplierSelectionButtonComponent;

    readonly displayedColumns = [
        'supplier_event_name',
        'supplier_id',
        'supplier_venue_name',
        'supplier_date',
        'supplier_country_code',
        'shi_event_name',
        'shi_id',
        'shi_venue_name',
        'shi_date',
        'actions'
    ];

    readonly initSortCol = 'supplier_id';
    readonly initSortDir: SortDirection = 'asc';
    readonly matchingsPageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;

    readonly blacklistedMatchings$ = this._blacklistedMatchingsService.blacklist.getMatchingsListData$();
    readonly blacklistedMatchingsMetadata$ = this._blacklistedMatchingsService.blacklist.getMatchingsListMetadata$();
    readonly selectedSupplierName$ = this._selectedSupplierName.asObservable();
    readonly loading$ = this._blacklistedMatchingsService.blacklist.loading$();

    readonly hasWritePermissions$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.matchingWrite])));

    readonly isHandsetOrTablet$ = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly emptyListWithWordAndFilters$ = this._blacklistedMatchingsService.blacklist.getMatchingsListMetadata$()
        .pipe(
            map(metadata => !!(metadata?.total === 0 && this._request?.q && (
                Object.keys(this._request).filter(key => !EXCLUDED_FILTER_KEYS.includes(key)).length > 0
            ))));

    constructor(
        private _router: Router,
        activatedRoute: ActivatedRoute
    ) {
        super();
        const urlParameters = Object.assign({}, activatedRoute.snapshot.queryParams);
        if (!urlParameters['supplier']) {
            const urlParameters = Object.assign({}, this.activatedRoute.snapshot.queryParams);
            urlParameters['supplier'] = SupplierName.tevo;
            this._router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: urlParameters });
        } else {
            this._selectedSupplierName.next(urlParameters['supplier']);
        }
        this.loadAsyncFilters();
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent,
            this._supplierSelectButtonComponent
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadData(filters: FilterItem[]): void {
        this._request = {
            limit: this.matchingsPageSize,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.q = values[0].value;
                        break;
                    case 'COUNTRY_CODE':
                        this._request.country_code = values.map(val => val.value);
                        break;
                    case 'DATE':
                        this._request.date = values[0].value;
                        break;
                    case 'DATE_END':
                        this._request.date_end = values[0].value;
                        break;
                }
            }
        });

        this.loadBlacklistedMatchings();
    }

    changeSelectedSupplier(event: string): void {
        this._selectedSupplierName.next(event);
        this.listFiltersService.resetFilters();
        this.loadAsyncFilters();
    }

    openUnblockCandidateDialog(matching: Matching): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CANDIDATES_BLACKLIST.FORMS.UNBLOCK_CANDIDATE_WARNING_TITLE',
            message: 'CANDIDATES_BLACKLIST.FORMS.UNBLOCK_CANDIDATE_WARNING',
            actionLabel: 'CANDIDATES_BLACKLIST.ACTIONS.UNBLOCK_CANDIDATE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._blacklistedMatchingsService.blacklist.delete(
                    this._selectedSupplierName.getValue() as SupplierName, matching.id)
                )
            )
            .subscribe(() => {
                this._ephemeralMsg.showSuccess({
                    msgKey: 'CANDIDATES_BLACKLIST.FORMS.FEEDBACKS.CANDIDATE_UNBLOCKED'
                });
                this.loadBlacklistedMatchings();
            });
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    removeFilters(): void {
        this.listFiltersService.resetFilters('SEARCH_INPUT');
    }

    private loadBlacklistedMatchings(): void {
        this._blacklistedMatchingsService.blacklist.load(this._selectedSupplierName.getValue(), this._request);
    }

    private loadAsyncFilters(): void {
        this._blacklistedMatchingsService.countries.load(this._selectedSupplierName.getValue());
    }
}

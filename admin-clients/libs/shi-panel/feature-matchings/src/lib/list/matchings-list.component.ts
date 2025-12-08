import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import { Metadata } from '@OneboxTM/utils-state';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ListFilteredComponent, ListFiltersService, FilterItem, ContextNotificationComponent,
    DateTimeModule, ChipsComponent, CopyTextComponent, DialogSize, EphemeralMessageService, MessageDialogService,
    ExportDialogComponent, ObMatDialogConfig, PopoverFilterDirective,
    PopoverComponent, ChipsFilterDirective, HoverOverlayDirective, openDialog,
    SearchablePaginatedSelectionModule,
    SearchablePaginatedSelectionLoadEvent,
    SearchablePaginatedSelectionComponent,
    Chip,
    SortFilterComponent,
    AggregatedDataComponent
} from '@admin-clients/shared/common/ui/components';
import { AggregatedData, DateTimeFormats, ExportFormat, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ErrorCardComponent } from '@admin-clients/shi-panel/common/ui-components';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SupplierSelectionButtonComponent } from '@admin-clients/shi-panel/feature-supplier-selection';
import {
    GetMatchingsRequest, Matching, MatchingStatus, SupplierName, UserPermissions
} from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, OnDestroy, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, map, shareReplay, startWith, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { MatchingsService } from '../matchings.service';
import { MatcherStatus } from '../models/matcher.status';
import { aggDataMatchings } from '../models/matchings-aggregated-data';
import { MatchingDetailsComponent } from './details/matching-details.component';
import { MatchingsListFilterComponent } from './filter/matchings-list-filter.component';
import { LaunchMatcherComponent } from './launch-matcher/launch-matcher.component';
import { exportDataMatchingsList } from './matchings-list-export-data';

const PAGE_SIZE = 20;
const EXCLUDED_FILTER_KEYS = ['q', 'supplier', 'sort', 'limit', 'offset', 'aggs'];

@Component({
    imports: [
        CommonModule, TranslatePipe, FlexLayoutModule, ContextNotificationComponent,
        RouterModule, DateTimeModule, SharedUtilityDirectivesModule, MatchingsListFilterComponent,
        ChipsComponent, DateTimePipe, MatDialogModule, SupplierSelectionButtonComponent, MatProgressSpinnerModule,
        MatTableModule, MatSortModule, MatTooltipModule, CopyTextComponent, MatIconModule, MatButtonModule,
        PopoverFilterDirective, PopoverComponent, ChipsFilterDirective, HoverOverlayDirective, ErrorCardComponent, MatMenuModule,
        SearchablePaginatedSelectionModule, MatCheckboxModule, AggregatedDataComponent
    ],
    selector: 'app-matchings-list',
    templateUrl: './matchings-list.component.html',
    styleUrls: ['./matchings-list.component.scss'],
    providers: [ListFiltersService, MatDialogModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MatchingsListComponent extends ListFilteredComponent implements OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #matchingsService = inject(MatchingsService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #selectedSupplierName = new BehaviorSubject<string>(SupplierName.tevo);
    readonly #authService = inject(AuthenticationService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly #listFiltersSrv = inject(ListFiltersService);
    readonly #tableSrv = inject(TableColConfigService);

    #request: GetMatchingsRequest;
    #filtersInited = false;
    #clickedRow = new BehaviorSubject<Matching>(null);
    @ViewChild(SearchablePaginatedSelectionComponent) private readonly _searchablePaginatedSelection: SearchablePaginatedSelectionComponent;
    @ViewChild(MatchingsListFilterComponent) private readonly _filterComponent: MatchingsListFilterComponent;
    @ViewChild(SupplierSelectionButtonComponent) private readonly _supplierSelectButtonComponent: SupplierSelectionButtonComponent;
    @ViewChild(MatSort) set matSort(matSort: MatSort) {
        if (matSort?.active && !this.#filtersInited) {
            this.#filtersInited = true;
            this.initListFilteredComponent([
                new SortFilterComponent(matSort),
                this._searchablePaginatedSelection,
                this._filterComponent,
                this._supplierSelectButtonComponent
            ]);
        }
    }

    readonly displayedColumns = [
        'active',
        'status',
        'supplier_event_name',
        'supplier_id',
        'supplier_venue_name',
        'supplier_date',
        'supplier_country_code',
        'supplier_listings',
        'supplier_tickets',
        'shi_event_name',
        'shi_id',
        'shi_venue_name',
        'shi_date',
        'actions'
    ];

    readonly initSortCol = 'status';
    readonly initSortDir: SortDirection = 'asc';
    readonly matchingsPageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly matchingStatus = MatchingStatus;
    readonly matcherStatus = MatcherStatus;

    readonly chips$ = this.#listFiltersSrv.onFilterValuesApplied$().pipe(
        startWith([]),
        map(filters => {
            const chips = [] as Chip[];
            filters
                .filter(filterItem => filterItem.label && filterItem.values)
                .forEach(filterItem => {
                    filterItem.values
                        .forEach(val => {
                            chips.push({
                                key: filterItem.key,
                                label: filterItem.label,
                                value: val.value,
                                valueText: val.text
                            });
                        });
                });
            return chips;
        }));

    form = new UntypedFormControl([]);

    readonly matchings$ = this.#matchingsService.list.getMatchingsListData$()
        .pipe(
            filter(matchings => !!matchings),
            tap(matchings => {
                // Update selected values
                this.form.value?.forEach((selectedMatching, index) => {
                    const updatedCollective = matchings.find(m => selectedMatching.id === m.id);
                    if (updatedCollective) {
                        this.form.value[index] = updatedCollective;
                    }
                });
            }),
            shareReplay(1)
        );

    readonly aggDataMatchings = aggDataMatchings;
    readonly matchingsAggregatedData$ = this.#matchingsService.list.getMatchingsListAggregatedData$().pipe(
        filter(Boolean),
        map(aggData => this.mapAggData(aggData))
    );

    readonly selectedSupplierName$ = this.#selectedSupplierName.asObservable();
    readonly isExportLoading$ = this.#matchingsService.list.exportLoading$();
    readonly matcherStatus$ = this.#matchingsService.status.getMatcherStatus$().pipe(filter(Boolean), map(matcher => matcher[0]));
    readonly loadingData$ = booleanOrMerge([
        this.#matchingsService.list.loading$(),
        this.#matchingsService.status.loading$(),
        this.#matchingsService.blacklist.loading$()
    ]);

    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly allSelectedClick = new EventEmitter<boolean>();

    readonly selectedOnly$ = this.showSelectedOnlyClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly matchingsList$ = this.selectedOnly$.pipe(
        switchMap(isActive => isActive ? this.selectedMatchings$ : this.matchings$),
        shareReplay(1)
    );

    readonly columnsDisabled$ = this.matchingsList$.pipe(
        map(arr =>
            arr
                .map((obj, index) => ({ obj, index }))
                .filter(({ obj }) => obj.status !== MatchingStatus.candidate)
                .map(({ index }) => index)
        )
    );

    readonly allSelected$ = this.allSelectedClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly selectedMatchings$ = this.form.valueChanges
        .pipe(
            map(selected => {
                if (!selected || selected.length === 0) {
                    this.showSelectedOnlyClick.next(false);
                    return [];
                }
                return selected.sort((a, b) => a.id.localeCompare(b.id));
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly allMatchingsMetadata$ = this.#matchingsService.list.getMatchingsListMetadata$();
    readonly matchingsMetadata$ = this.selectedOnly$.pipe(
        switchMap(isActive => isActive ?
            this.selectedMatchings$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
            this.#matchingsService.list.getMatchingsListMetadata$()
        ),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly hasWriteMatchingPermissions$: Observable<boolean> = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.matchingWrite])));

    readonly hasWriteMappingPermissions$: Observable<boolean> = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.mappingWrite])));

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly clickedRow$ = this.#clickedRow.asObservable();

    readonly emptyListWithWordAndFilters$ = this.#matchingsService.list.getMatchingsListMetadata$()
        .pipe(
            map(metadata => !!(metadata?.total === 0 && this.#request?.q && (
                Object.keys(this.#request).filter(key => !EXCLUDED_FILTER_KEYS.includes(key)).length > 0
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
            this.#selectedSupplierName.next(urlParameters['supplier']);
        }
        this.loadAsyncFilters();
        this.selectedMatchings$.subscribe();
    }

    canDeactivate(): Observable<boolean> {
        if (this.form.value?.length > 0) {
            return this.#msgDialogSrv.defaultUnsavedChangesWarn()
                .pipe(
                    switchMap(result => {
                        if (result) {
                            this.form.setValue([]);
                            return of(true);
                        } else {
                            this._supplierSelectButtonComponent.resetSupplier();
                            return of(false);
                        }
                    })
                );
        }
        return of(true);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#detailOverlayService.close();
    }

    get selectedMatchings(): number {
        return this.form?.value?.length || 0;
    }

    clickShowSelected(): void {
        this.selectedOnly$.pipe(take(1)).subscribe((isSelected => this.showSelectedOnlyClick.emit(!isSelected)));
    }

    mapBulk(): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: this.form.value.length === 1 ?
                'MATCHINGS.TITLES.BULK_CREATE_MATCHING_FROM_CANDIDATE' : 'MATCHINGS.TITLES.BULK_CREATE_MATCHINGS_FROM_CANDIDATES',
            message: this.form.value.length === 1 ?
                'MATCHINGS.FORMS.INFOS.BULK_CREATE_MATCHING_FROM_CANDIDATE' : 'MATCHINGS.FORMS.INFOS.BULK_CREATE_MATCHINGS_FROM_CANDIDATES',
            actionLabel: 'MATCHINGS.ACTIONS.MATCH',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() =>
                    this.#matchingsService.list.createFromCandidates(this.form.value.map(c => c.id), this.#selectedSupplierName.getValue())
                )
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'MATCHINGS.FORMS.BULK_CREATE_MATCHINGS_FROM_CANDIDATES_SUCCESS'
                });
                this.loadMatchings(this.#request);
                this.form.reset();
            });
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.matchingsPageSize,
            aggs: true,
            offset: 0,
            includeAggs: true
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'COUNTRY_CODE':
                        this.#request.country_code = values.map(val => val.value);
                        break;
                    case 'SHI_TAXONOMIES':
                        this.#request.shi_taxonomies = values.map(val => val.value);
                        break;
                    case 'SUPPLIER_TAXONOMIES':
                        this.#request.supplier_taxonomies = values.map(val => val.value);
                        break;
                    case 'DATE':
                        this.#request.date = values[0].value;
                        break;
                    case 'DATE_END':
                        this.#request.date_end = values[0].value;
                        break;
                }
            }
        });
        this.loadMatchings(this.#request);
    }

    changeSelectedSupplier(event: string): void {
        this.#selectedSupplierName.next(event);
        this.listFiltersService.resetFilters();
        this.loadAsyncFilters();
    }

    openCreateMatchingDialog(matching: Matching): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'MATCHINGS.TITLES.BULK_CREATE_MATCHING_FROM_CANDIDATE',
            message: 'MATCHINGS.FORMS.INFOS.BULK_CREATE_MATCHING_FROM_CANDIDATE',
            actionLabel: 'FORMS.ACTIONS.MAP',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#matchingsService.list.createMatching(this.#selectedSupplierName.getValue(), matching.id))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'MATCHINGS.CREATE_MAPPING_SUCCESS'
                });
                matching.status = MatchingStatus.mapped;
            });
    }

    openBlockCandidateDialog(matchings: Matching[]): void {
        console.log(matchings);
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: matchings.length === 1 ?
                'MATCHINGS.FORMS.BLOCK_CANDIDATE_WARNING_TITLE' : 'MATCHINGS.FORMS.BLOCK_CANDIDATES_WARNING_TITLE',
            message: matchings.length === 1 ?
                'MATCHINGS.FORMS.BLOCK_CANDIDATE_WARNING' : 'MATCHINGS.FORMS.BLOCK_CANDIDATES_WARNING',
            actionLabel: 'MATCHINGS.ACTIONS.BLOCK_CANDIDATE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#matchingsService.blacklist.save(
                    this.#selectedSupplierName.getValue() as SupplierName, matchings
                ))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({
                    msgKey: matchings.length === 1 ?
                        'MATCHINGS.FORMS.FEEDBACKS.CANDIDATE_BLOCKED' : 'MATCHINGS.FORMS.FEEDBACKS.CANDIDATES_BLOCKED'
                });
            });
    }

    openExportMatchingslistDialog(): void {
        this.#matDialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataMatchingsList,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SHI_MATCHINGS')
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(exportList => this.#tableSrv.setColumns('EXP_SHI_MATCHINGS', exportList.fields.map(resultData => resultData.field))),
                switchMap(exportList =>
                    this.#matchingsService.list.exportMatchingslist(this.#selectedSupplierName.getValue(), this.#request, exportList)
                ),
                filter(result => !!result.export_id)
            )
            .subscribe(() => this.#ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' }));
    }

    openLaunchMatcherDialog(): void {
        openDialog(this.#matDialog, LaunchMatcherComponent)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(data =>
                    this.#matchingsService.list.launchMatcher(this.#selectedSupplierName.getValue(), data.countries, data.taxonomies)
                )
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({ msgKey: 'MATCHINGS.INFOS.LAUNCH_MATCHER_SUCCESS' });
            });
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    open(row: Matching): void {
        this.#clickedRow.next(row);
        const data = { id: row.id, supplier: this.#selectedSupplierName.getValue() };
        const overlayData = new DetailOverlayData(data, 'MATCHINGS.MATCHING_DETAILS_TITLE');
        this.#detailOverlayService.open(MatchingDetailsComponent, overlayData).pipe(
            take(1)
        ).subscribe(mapped => {
            if (mapped) {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'MATCHINGS.CREATE_MAPPING_SUCCESS'
                });
                row.status = MatchingStatus.mapped;
            }
            this.#clickedRow.next(null);
        });
    }

    removeFilters(): void {
        this.listFiltersService.resetFilters('SEARCH_INPUT');
    }

    loadMatchings({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#request = { ...this.#request, limit, offset, q: q?.length ? q : null };

        this.#detailOverlayService.close();
        this.#matchingsService.list.load(this.#selectedSupplierName.getValue(), this.#request);

        // change to non selected only view if a search is made
        this.#matchingsService.list.getMatchingsListData$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnlyMode]) => {
            this.showSelectedOnlyClick.emit(isSelectedOnlyMode);
        });
    }

    private mapAggData(aggData: ResponseAggregatedData): AggregatedData {
        const overallListings = [];

        const total = aggData.overall.find(overall => overall.name === 'total_matchings')?.value ?? 0;

        const matchingsMapped = {
            name: 'total_matchings_mapped',
            type: 'SUM',
            value: aggData.type.find(sale => sale.agg_value === MatchingStatus.mapped)?.agg_metric[0]?.value ?? 0
        };

        overallListings.push(matchingsMapped);

        const matchingsMatched = {
            name: 'total_matchings_matched',
            type: 'SUM',
            value: aggData.type.find(sale => sale.agg_value === MatchingStatus.matched)?.agg_metric[0]?.value ?? 0
        };

        overallListings.push(matchingsMatched);

        const matchingsInCandidate = {
            name: 'total_matchings_candidate',
            type: 'SUM',
            value: aggData.type.find(sale => sale.agg_value === MatchingStatus.candidate)?.agg_metric[0]?.value ?? 0
        };

        overallListings.push(matchingsInCandidate);

        const matchingsNotRelated = {
            name: 'total_matchings_not_related',
            type: 'SUM',
            value: aggData.type.find(sale => sale.agg_value === MatchingStatus.notRelated)?.agg_metric[0]?.value ?? 0
        };

        overallListings.push(matchingsNotRelated);

        const totalMatchings = {
            name: 'total_matchings',
            type: 'SUM',
            value: total
        };

        overallListings.push(totalMatchings);

        const aggregatedData = {
            overall: overallListings,
            type: []
        };

        return new AggregatedData(aggregatedData, aggDataMatchings);
    }

    private loadAsyncFilters(): void {
        this.#matchingsService.countries.load(this.#selectedSupplierName.getValue());
        this.#matchingsService.status.load(this.#selectedSupplierName.getValue());
        this.#matchingsService.shiTaxonomies.load(this.#selectedSupplierName.getValue());
        this.#matchingsService.supplierTaxonomies.load(this.#selectedSupplierName.getValue());
    }
}

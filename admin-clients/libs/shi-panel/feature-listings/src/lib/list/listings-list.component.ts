import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ListFilteredComponent,
    ListFiltersService,
    SortFilterComponent,
    PaginatorComponent,
    FilterItem,
    ContextNotificationComponent,
    SearchInputComponent,
    DateTimeModule,
    PopoverDateRangePickerFilterComponent,
    DateRangeShortcut,
    ChipsComponent,
    ExportDialogComponent,
    ObMatDialogConfig,
    EphemeralMessageService,
    HoverOverlayDirective,
    AggregatedDataComponent,
    CopyTextComponent,
    PopoverFilterDirective,
    PopoverComponent,
    ChipsFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    AggregatedData, AggregationMetrics, DateTimeFormats, ExportFormat, ResponseAggregatedData
} from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ErrorCardComponent } from '@admin-clients/shi-panel/common/ui-components';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { ListingsService } from '../listings.service';
import { GetListingsRequest } from '../models/get-listings-request.model';
import { ListingStatus } from '../models/listing-status.enum';
import { Listing } from '../models/listings.model';
import { BulkBlacklistManagementComponent } from './bulk-manage-blacklist/bulk-blacklist-management.component';
import { ListingDetailsComponent } from './details/listing-details.component';
import { ListingsListFilterComponent } from './filter/listings-list-filter.component';
import { aggDataListings } from './listings-aggregated-data';
import { exportDataListingsList } from './listings-list-export-data';

const PAGE_SIZE = 20;
const EXCLUDED_FILTER_KEYS = ['q', 'noDate', 'sort', 'limit', 'offset', 'aggs'];

@Component({
    imports: [
        CommonModule, TranslatePipe, PaginatorComponent, MaterialModule, FlexLayoutModule, ContextNotificationComponent,
        RouterModule, SharedUtilityDirectivesModule, SearchInputComponent, DateTimeModule,
        ListingsListFilterComponent, ListingsListFilterComponent, ChipsComponent,
        ErrorCardComponent, HoverOverlayDirective, AggregatedDataComponent, DateTimePipe, CopyTextComponent, PopoverFilterDirective,
        PopoverComponent, ChipsFilterDirective, PopoverDateRangePickerFilterComponent
    ],
    selector: 'app-listings-list',
    templateUrl: './listings-list.component.html',
    styleUrls: ['./listings-list.component.scss'],
    providers: [ListFiltersService, DetailOverlayService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ListingsListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly #listingsService = inject(ListingsService);
    readonly #authService = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    #request: GetListingsRequest;
    #sortFilterComponent: SortFilterComponent;
    #clickedRow = new BehaviorSubject<Listing>(null);

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(ListingsListFilterComponent) private readonly _filterComponent: ListingsListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    readonly displayedColumns = [
        'listing_id',
        'last_update',
        'created',
        'event_id',
        'status',
        'quantity',
        'supplier',
        'supplier_event_id',
        'supplier_id'
    ];

    readonly initSortCol = 'created';
    readonly initSortDir: SortDirection = 'desc';
    readonly listingsPageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly listingStatus = ListingStatus;
    readonly dateRangeShortcut = DateRangeShortcut;
    readonly aggDataListings: AggregationMetrics = aggDataListings;

    readonly listings$ = this.#listingsService.list.getListingsListData$();
    readonly listingsMetadata$ = this.#listingsService.list.getListingsListMetadata$();
    readonly loadingData$ = this.#listingsService.list.loading$();
    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly isExportLoading$ = this.#listingsService.list.exportLoading$();
    readonly listingsAggregatedData$ = this.#listingsService.list.getAggData$()
        .pipe(
            filter(Boolean),
            map(aggData => this.mapAggData(aggData))
        );

    readonly clickedRow$ = this.#clickedRow.asObservable();

    readonly emptyListWithWordAndFilters$ = this.#listingsService.list.getListingsListMetadata$()
        .pipe(
            map(metadata => !!(metadata?.total === 0 && this.#request?.q && (
                Object.keys(this.#request).filter(key => !EXCLUDED_FILTER_KEYS.includes(key)).length > 0
            ))));

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.listingWrite])));

    constructor(
        private _router: Router,
        activatedRoute: ActivatedRoute
    ) {
        super();

        //Set default last_update dates from yesterday 00 to today 23:59
        const urlParameters = Object.assign({}, activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            const yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            urlParameters['startDate'] = new Date(yesterday.setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            _router.navigate(['.'], { relativeTo: activatedRoute, queryParams: urlParameters });
        }
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent,
            this._dateRangePickerComponent
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#detailOverlayService.close();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.listingsPageSize,
            aggs: true,
            offset: 0
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
                    case 'DATE_RANGE':
                        this.#request.update_date_from = values[0].value.start;
                        this.#request.update_date_to = values[0].value.end;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'SUPPLIER':
                        this.#request.supplier = values.map(val => val.value);
                        break;
                    case 'START_DATE_LISTING':
                        this.#request.import_date_from = values[0].value;
                        break;
                    case 'END_DATE_LISTING':
                        this.#request.import_date_to = values[0].value;
                        break;
                }
            }
        });
        this.loadListings();
    }

    openExportListingslistDialog(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataListingsList,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SHI_LISTINGS')
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(exportList => this.#tableSrv.setColumns('EXP_SHI_LISTINGS', exportList.fields.map(resultData => resultData.field))),
                switchMap(exportList => this.#listingsService.list.exportListingslist(this.#request, exportList)),
                filter(result => !!result.export_id)
            )
            .subscribe(() =>
                this.#ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' })
            );
    }

    open(row: Listing): void {
        this.#clickedRow.next(row);

        const data = new DetailOverlayData(row.code, row.event_id.toString(), '1024px');
        this.#detailOverlayService.open(ListingDetailsComponent, data).subscribe(() => {
            this.#clickedRow.next(null);
        });
    }

    openBulkBlacklistManagementDialog(): void {
        this.#dialog.open(BulkBlacklistManagementComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(data => this.#listingsService.list.bulkManageBlacklist(data.blacklisted, data.event_ids, data.codes))
            )
            .subscribe(() => {
                this.#ephemeralMsg.showSuccess({ msgKey: 'LISTINGS.INFOS.BULK_MANAGE_BLACKLIST_SUCCESS' });
                this.loadListings();
            });
    }

    removeFilters(): void {
        this.listFiltersService.resetFilters('SEARCH_INPUT');
    }

    searchMapping(id: number): void {
        const baseUrl = window.location.origin;
        const urlTree = this._router.createUrlTree(['/mappings'], { queryParams: { q: id } });
        window.open(baseUrl + this._router.serializeUrl(urlTree), '_blank');
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    private loadListings(): void {
        this.#detailOverlayService.close();
        this.#listingsService.list.load(this.#request);
    }

    private mapAggData(aggData: ResponseAggregatedData): AggregatedData {
        const overallListings = [];

        const total = aggData.overall.find(overall => overall.name === 'total_listings')?.value ?? 0;

        const listingsImported = {
            name: 'total_listings_imported',
            type: 'SUM',
            value: aggData.type.find(
                listing => listing.agg_value === ListingStatus.imported
            )?.agg_metric[0].value ?? 0
        };
        overallListings.push(listingsImported);

        const importedErrorValue = aggData?.type.find(
            listing => listing.agg_value === ListingStatus.importedWithError
        )?.agg_metric[0].value ?? 0;

        const listingsImportedWithError = {
            name: 'total_listings_imported_with_error',
            type: 'SUM',
            value: importedErrorValue !== 0 ? (importedErrorValue / total * 100) : 0
        };
        overallListings.push(listingsImportedWithError);

        const listingsDeleted = {
            name: 'total_listings_deleted',
            type: 'SUM',
            value: aggData?.type.find(
                listing => listing.agg_value === ListingStatus.deleted
            )?.agg_metric[0].value ?? 0
        };
        overallListings.push(listingsDeleted);

        const totalListings = {
            name: 'total_listings',
            type: 'SUM',
            value: total
        };
        overallListings.push(totalListings);

        const agregatedData = {
            overall: overallListings,
            type: []
        };

        return new AggregatedData(agregatedData, aggDataListings);
    }
}

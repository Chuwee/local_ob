import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ListFilteredComponent, ListFiltersService, SortFilterComponent, PaginatorComponent, FilterItem, ContextNotificationComponent,
    AggregatedDataComponent, SearchInputComponent, DateTimeModule, PopoverDateRangePickerFilterComponent, ChipsComponent, EphemeralMessageService, ExportDialogComponent, ObMatDialogConfig, HoverOverlayDirective,
    CopyTextComponent, PopoverFilterDirective, PopoverComponent, ChipsFilterDirective
} from '@admin-clients/shared/common/ui/components';
import {
    AggregatedData, AggregationMetrics, DateTimeFormats, ExportFormat, ResponseAggregatedData
} from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ErrorCardComponent } from '@admin-clients/shi-panel/common/ui-components';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, TitleCasePipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, viewChild
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSort, SortDirection } from '@angular/material/sort';
import {
    MatTableModule
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { DeliveryMethod } from '../models/delivery-method.enum';
import { GetSalesRequest } from '../models/get-sales-request.model';
import { SaleStatus } from '../models/sale-status.enum';
import { Sale } from '../models/sales.model';
import { SalesService } from '../sales.service';
import { SaleDetailsComponent } from './details/sale-details.component';
import { SalesListFilterComponent } from './filter/sales-list-filter.component';
import { aggDataSales } from './sales-aggregated-data';
import { exportDataSalesList } from './sales-list-export-data';

const PAGE_SIZE = 20;
const EXCLUDED_FILTER_KEYS = ['q', 'noDate', 'sort', 'limit', 'offset', 'aggs'];

@Component({
    selector: 'app-sales-list',
    templateUrl: './sales-list.component.html',
    styleUrls: ['./sales-list.component.scss'],
    providers: [ListFiltersService, DetailOverlayService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TitleCasePipe, TranslatePipe, PaginatorComponent, FlexLayoutModule, ContextNotificationComponent,
        RouterModule, AggregatedDataComponent, SearchInputComponent, DateTimeModule, SalesListFilterComponent, ChipsComponent,
        SharedUtilityDirectivesModule, ErrorCardComponent, HoverOverlayDirective, DateTimePipe, LocalCurrencyPipe,
        CopyTextComponent, PopoverComponent, PopoverFilterDirective, ChipsFilterDirective, PopoverDateRangePickerFilterComponent,
        MatIcon, MatIconButton, MatButton, MatTooltip, MatProgressSpinner, MatTableModule, MatSort
    ]
})
export class SalesListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    readonly #salesService = inject(SalesService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly #authService = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    #request: GetSalesRequest;
    #clickedRow = new BehaviorSubject<Sale>(null);

    private readonly _$matSort = viewChild(MatSort);
    private readonly _$paginatorComponent = viewChild(PaginatorComponent);
    private readonly _$searchInputComponent = viewChild(SearchInputComponent);
    private readonly _$filterComponent = viewChild(SalesListFilterComponent);
    private readonly _$dateRangePickerComponent = viewChild(PopoverDateRangePickerFilterComponent);

    readonly displayedColumns = [
        'id', //transaction id
        'listing_id',
        'created',
        'status',
        'delivery_method',
        'products', //quantity
        'payout_per_product',
        'total_price',
        'event.name',
        'event.id',
        'supplier',
        'supplier_id'
    ];

    readonly initSortCol = 'created';
    readonly initSortDir: SortDirection = 'desc';
    readonly salesPageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly deliveryMethods = DeliveryMethod;
    readonly saleStatus = SaleStatus;

    readonly aggDataSales: AggregationMetrics = aggDataSales;
    readonly sales$ = this.#salesService.list.getData$();
    readonly salesMetadata$ = this.#salesService.list.getMetadata$();
    readonly loadingData$ = this.#salesService.list.loading$();
    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly salesAggregatedData$ = this.#salesService.list.getAggData$().pipe(
        filter(Boolean),
        map(aggData => this.#mapAggData(aggData))
    );

    readonly isExportLoading$ = this.#salesService.list.exportLoading$();
    readonly clickedRow$ = this.#clickedRow.asObservable();

    readonly emptyListWithWordAndFilters$ = this.#salesService.list.getMetadata$()
        .pipe(
            map(metadata => !!(metadata?.total === 0 && this.#request?.q && (
                Object.keys(this.#request).filter(key => !EXCLUDED_FILTER_KEYS.includes(key)).length > 0
            ))));

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.salesWrite])));

    constructor() {
        super();

        //Set default last_update dates from yesterday 00 to today 23:59
        const urlParameters = Object.assign({}, this.#activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            const yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            urlParameters['startDate'] = new Date(yesterday.setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            this.#router.navigate(['.'], { relativeTo: this.#activatedRoute, queryParams: urlParameters });
        }
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._$paginatorComponent(),
            new SortFilterComponent(this._$matSort()),
            this._$searchInputComponent(),
            this._$filterComponent(),
            this._$dateRangePickerComponent()
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#detailOverlayService.close();
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.salesPageSize,
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
                        this.#request.sale_date_from = values[0].value.start;
                        this.#request.sale_date_to = values[0].value.end;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'DELIVERY':
                        this.#request.delivery_method = values.map(val => val.value);
                        break;
                    case 'LAST_ERROR':
                        this.#request.last_error_description = values.map(val => val.value);
                        break;
                    case 'SUPPLIER':
                        this.#request.supplier = values.map(val => val.value);
                        break;
                    case 'START_DATE_UPDATE':
                        this.#request.update_date_from = values[0].value;
                        break;
                    case 'END_DATE_UPDATE':
                        this.#request.update_date_to = values[0].value;
                        break;
                    case 'START_DATE_INHAND':
                        this.#request.inhand_date_from = values[0].value;
                        break;
                    case 'END_DATE_INHAND':
                        this.#request.inhand_date_to = values[0].value;
                        break;
                    case 'COUNTRY':
                        this.#request.country_code = values.map(val => val.value);
                        break;
                    case 'CURRENCY':
                        this.#request.currency = values.map(val => val.value);
                        break;
                    case 'TAXONOMIES':
                        this.#request.taxonomies = values.map(val => val.value);
                        break;
                    case 'DAYS_TO_EVENT_LTE':
                        this.#request.daysToEventLte = values[0].value;
                        break;
                    case 'DAYS_TO_EVENT_GTE':
                        this.#request.daysToEventGte = values[0].value;
                        break;
                }
            }
        });
        this.#loadSales();
    }

    openExportSaleslistDialog(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataSalesList,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_SHI_SALES')
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(exportList => this.#tableSrv.setColumns('EXP_SHI_SALES', exportList.fields.map(resultData => resultData.field))),
                switchMap(exportList => this.#salesService.list.exportSaleslist(this.#request, exportList)),
                filter(result => !!result.export_id)
            )
            .subscribe(() =>
                this.#ephemeralMsg.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' })
            );
    }

    open(row: Sale): void {
        this.#clickedRow.next(row);

        const data = new DetailOverlayData(row.id, row.event.name || 'SALES.SALE_DETAILS_TITLE', '1024px');
        this.#detailOverlayService.open(SaleDetailsComponent, data).pipe(
            take(1)
        ).subscribe(relaunched => {
            if (relaunched) {
                this.#ephemeralMsg.showSuccess({
                    msgKey: 'SALES.RELAUNCH_SALE_SUCCESS',
                    msgParams: { saleId: row.id }
                });
                this.#loadSales();
            }
            this.#clickedRow.next(null);
        });
    }

    removeFilters(): void {
        this.listFiltersService.resetFilters('SEARCH_INPUT');
    }

    searchId(id: number, redirection: string): void {
        const baseUrl = window.location.origin;
        const urlTree = this.router.createUrlTree([redirection], { queryParams: { q: id } });
        window.open(baseUrl + this.router.serializeUrl(urlTree), '_blank');
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    #loadSales(): void {
        this.#detailOverlayService.close();
        this.#salesService.list.load(this.#request);
    }

    #mapAggData(aggData: ResponseAggregatedData): AggregatedData {
        const overallSales = [];

        const total = aggData.overall.find(overall => overall.name === 'total_sales')?.value ?? 0;

        const salesSold = {
            name: 'total_sales_sold',
            type: 'SUM',
            value: aggData.type.find(sale => sale.agg_value === SaleStatus.sold)?.agg_metric[0]?.value ?? 0
        };
        overallSales.push(salesSold);

        const soldErrorValue =
            aggData?.type.find(sale => sale.agg_value === SaleStatus.soldWithError)?.agg_metric[0]?.value ?? 0;

        const salesSoldError = {
            name: 'total_sales_sold_with_error',
            type: 'SUM',
            value: soldErrorValue !== 0 ? (soldErrorValue / total * 100) : 0
        };
        overallSales.push(salesSoldError);

        const salesFulfilled = {
            name: 'total_sales_fulfilled',
            type: 'SUM',
            value: aggData?.type.find(sale => sale.agg_value === SaleStatus.fulfilled)?.agg_metric[0]?.value ?? 0
        };
        overallSales.push(salesFulfilled);

        const salesFulfilledError = {
            name: 'total_sales_fulfilled_with_error',
            type: 'SUM',
            value: aggData?.type
                .find(sale => sale.agg_value === SaleStatus.fulfilledWithError)?.agg_metric[0]?.value ?? 0
        };
        overallSales.push(salesFulfilledError);

        const totalSales = {
            name: 'total_sales',
            type: 'SUM',
            value: total
        };
        overallSales.push(totalSales);

        const totalPrice = {
            name: 'total_sales_price',
            type: 'SUM',
            value: aggData.overall.find(overall => overall.name === 'total_sales_price')?.value ?? 0
        };
        overallSales.push(totalPrice);

        const aggregatedData = {
            overall: overallSales,
            type: []
        };

        return new AggregatedData(aggregatedData, aggDataSales);
    }
}
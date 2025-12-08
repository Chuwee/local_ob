import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    aggDataVoucherOrderWithoutCurrency, voucherOrdersColumnList, VoucherOrdersService, VoucherOrdersFields,
    GetVoucherOrdersRequest, VoucherOrder, aggDataVoucherOrder
} from '@admin-clients/cpanel-sales-data-access';
import {
    TableColConfigService
} from '@admin-clients/shared/common/data-access';
import {
    ListFiltersService, ListFilteredComponent, SortFilterComponent, PaginatorComponent, SearchInputComponent,
    FilterItem, ObMatDialogConfig, PopoverDateRangePickerFilterComponent, ExportDialogComponent, EphemeralMessageService,
    ColSelectionDialogComponent
} from '@admin-clients/shared/common/ui/components';
import {
    DateTimeFormats, ExportFormat, FieldData
} from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild, inject, signal, computed, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { first, forkJoin, Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { VoucherOrdersListFilterComponent } from './filter/voucher-orders-list-filter.component';
import { exportDataVoucherOrder } from './voucher-orders-export-data';

@Component({
    selector: 'app-voucher-orders-list',
    templateUrl: './voucher-orders-list.component.html',
    styleUrls: ['./voucher-orders-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderListComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {
    readonly #voucherOrdersSrv = inject(VoucherOrdersService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$operatorDefaultCurrency = computed(() => AuthenticationService.operatorDefaultCurrency(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetVoucherOrdersRequest>({});
    readonly #$combinedAggregatedData = toSignal(this.#voucherOrdersSrv.currencyAggregatedData.getCombined$());
    readonly #$baseAggregatedData = toSignal(this.#voucherOrdersSrv.getVoucherOrdersListAggregatedData$(
        aggDataVoucherOrderWithoutCurrency));

    #sortFilterComponent: SortFilterComponent;
    #applyRelevance = false;
    #showAggregates: boolean;

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(VoucherOrdersListFilterComponent) private readonly _filterComponent: VoucherOrdersListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    readonly voucherOrdersColumns = VoucherOrdersFields;
    readonly defaultDisplayedColumns = [
        VoucherOrdersFields.channel,
        VoucherOrdersFields.code,
        VoucherOrdersFields.type,
        VoucherOrdersFields.purchaseDate,
        VoucherOrdersFields.client,
        VoucherOrdersFields.price,
        VoucherOrdersFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('VOUCHER_ORDERS')?.filter(
        col => Object.values(VoucherOrdersFields).map(col => String(col)).includes(col)) || this.defaultDisplayedColumns;

    readonly fixedColumns: string[] = [VoucherOrdersFields.price];

    readonly initSortCol = 'purchaseDate';
    readonly initSortDir = 'desc';
    readonly pageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CNL_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#voucherOrdersSrv.getVoucherOrdersListMetadata$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request()?.q && this.#$request()?.purchase_date_from)));

    readonly voucherOrders$ = this.#voucherOrdersSrv.getVoucherOrdersListData$();
    readonly $voucherOrdersMetadata = toSignal(this.#voucherOrdersSrv.getVoucherOrdersListMetadata$());
    readonly $voucherOrdersAggregatedData = computed(() => {
        if (this.#$request().currency_code) {
            return {
                aggregatedData: this.#$combinedAggregatedData(), currency: this.#$request().currency_code,
                aggregationMetrics: aggDataVoucherOrder
            };
        } else {
            return { aggregatedData: this.#$baseAggregatedData(), aggregationMetrics: aggDataVoucherOrderWithoutCurrency, currency: null };
        }
    });

    readonly $reqInProgress = toSignal(booleanOrMerge([
        this.#voucherOrdersSrv.isVoucherOrdersListLoading$(),
        this.#voucherOrdersSrv.isExportVoucherOrdersLoading$()
    ]));

    readonly isHandsetOrTablet$: Observable<boolean> = isHandsetOrTablet$();
    startDate: string;
    endDate: string;

    trackByFn = (_: number, item: VoucherOrder): string => item.code;

    ngOnInit(): void {
        this.#showAggregates = AuthenticationService.isSomeRoleInUserRoles(this.#$user(), [UserRoles.OPR_MGR, UserRoles.ENT_MGR,
        UserRoles.CNL_MGR]);
    }

    ngAfterViewInit(): void {
        // Set default filters
        const obs: Observable<boolean>[] = [of(false)];
        const urlParameters = Object.assign({}, this.activatedRoute.snapshot.queryParams);

        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            urlParameters['startDate'] = new Date(new Date().setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            obs.push(of(true));
        }
        this.startDate = urlParameters['startDate'];
        this.endDate = urlParameters['endDate'];

        if (this.#$areCurrenciesShown() && !urlParameters['currency']) {
            this.#voucherOrdersSrv.filterCurrencyList.load({
                purchase_date_from: this.startDate,
                purchase_date_to: this.endDate,
                limit: 200
            });

            obs.push(this.#voucherOrdersSrv.filterCurrencyList.getData$()
                .pipe(
                    first(Boolean),
                    map(filterOptions => {
                        const operatorDefaultCurrency = this.#$operatorDefaultCurrency();
                        const foundCurrency = filterOptions.find(filterOption => filterOption.id === operatorDefaultCurrency);
                        if (foundCurrency) {
                            urlParameters['currency'] = foundCurrency.id;
                            return true;
                        } else {
                            return false;
                        }
                    }),
                    takeUntilDestroyed(this.#destroyRef)
                )
            );
        }

        forkJoin(obs).subscribe(setDefaultFilters => {
            this.#sortFilterComponent = new SortFilterComponent(this._matSort);
            if (setDefaultFilters.some(Boolean)) {
                this.router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: urlParameters, replaceUrl: true })
                    .then(() => {
                        this.initListFilteredComponent([
                            this._paginatorComponent,
                            this.#sortFilterComponent,
                            this._searchInputComponent,
                            this._filterComponent,
                            this._dateRangePickerComponent]);
                    });
            } else {
                this.initListFilteredComponent([
                    this._paginatorComponent,
                    this.#sortFilterComponent,
                    this._searchInputComponent,
                    this._filterComponent,
                    this._dateRangePickerComponent]);
            }
        });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#voucherOrdersSrv.getVoucherOrdersListData$();
        this.#voucherOrdersSrv.currencyAggregatedData.clear();
    }

    loadData(filters: FilterItem[]): void {
        const request: GetVoucherOrdersRequest = {
            limit: this.pageSize,
            aggs: this.#showAggregates,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        request.limit = values[0].value.limit;
                        request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        request.q = values[0].value;
                        break;
                    case 'CHANNEL':
                        request.channel_id = values.map(val => val.value);
                        break;
                    case 'CHANNEL_ENTITY':
                        request.entity_id = values.map(val => val.value);
                        break;
                    case 'DATE_RANGE':
                        request.purchase_date_from = values[0].value.start;
                        request.purchase_date_to = values[0].value.end;
                        break;
                    case 'CURRENCY':
                        // currency_code is not set by the filter in case of only one operator currency or monocurrency
                        request.currency_code = values[0].value;
                        break;
                    case 'MERCHANT':
                        request.merchant = values[0].value;
                        break;
                }
            }
        });

        //TODO(MULTICURRENCY): delete this.#$user().currency when the multicurrency functionality is finished
        request.currency_code = this.#$areCurrenciesShown() ? request.currency_code :
            (this.#$operatorCurrencyCodes()?.[0] ?? this.#$user().currency);
        this.#$request.set(request);
        if (this.#$request().currency_code) {
            this.#voucherOrdersSrv.currencyAggregatedData.load(this.#$request());
            this.#voucherOrdersSrv.loadVoucherOrdersList(this.#$request(), this.#applyRelevance);
        } else {
            this.#voucherOrdersSrv.loadVoucherOrdersList(this.#$request(), this.#applyRelevance);
        }
    }

    exportVoucherOrders(): void {
        this.#dialog.open(
            ExportDialogComponent,
            new ObMatDialogConfig({
                exportData: exportDataVoucherOrder,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_VOUCHER_ORDERS')
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_VOUCHER_ORDERS', exportList.fields.map(resultData => resultData.field));
                this.#voucherOrdersSrv.exportVoucherOrders(this.#$request(), exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({
                            msgKey: 'ACTIONS.EXPORT.OK.MESSAGE'
                        });
                    });
            });
    }

    removeDatesFilter(): void {
        this.listFiltersService.removeFilter('DATE_RANGE', null);
    }

    applyRelevance(value: string): void {
        this.#applyRelevance = !!value;
    }

    unapplyRelevance(): void {
        this.#applyRelevance = false;
    }

    changeColSelection(): void {
        this.#dialog.open(ColSelectionDialogComponent, new ObMatDialogConfig(
            {
                fieldDataGroups: voucherOrdersColumnList,
                selectedFields: voucherOrdersColumnList
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(column => this.displayedColumns.includes(column.field))
            }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...voucherOrdersColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, VoucherOrdersFields.actions];
                this.#tableSrv.setColumns('VOUCHER_ORDERS', this.displayedColumns);
                this.#ref.markForCheck();
            });
    }

    dropItem(event: CdkDragDrop<VoucherOrdersFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('VOUCHER_ORDERS', this.displayedColumns);
        }
    }
}

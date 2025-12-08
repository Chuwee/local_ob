import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { productsProviders, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductsFields, TicketsApi, TicketsService, TicketsState } from '@admin-clients/cpanel-sales-data-access';
import {
    TableColConfigService, GetTicketsRequest, TicketState, EventType, TicketDetailType, OrderItem, aggDataTicketWithoutCurrency,
    OrderItemType
} from '@admin-clients/shared/common/data-access';
import {
    AggregatedDataComponent, ChipsComponent, ChipsFilterDirective, ColSelectionDialogComponent,
    ContextNotificationComponent, CopyTextComponent, EphemeralMessageService,
    ExportDialogComponent, FilterItem, ListFilteredComponent, ListFiltersService, ObMatDialogConfig, PaginatorComponent,
    PopoverComponent, PopoverDateRangePickerFilterComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, ExportFormat, FieldData } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    DateTimePipe, LocalCurrencyPipe, ObfuscatePattern, ObfuscateStringPipe, VariantTextPipe
} from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe, NgIf } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, inject, viewChild, signal, computed, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { first, forkJoin, Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ProductsSalesListFilterComponent } from './filter/products-sales-list-filter.component';
import { aggDataProduct, aggDataProductWithoutCurrency } from './products-aggregated-data';
import { productsColumnList } from './products-sales-column-list';
import { exportDataProduct } from './products-sales-export-data';

@Component({
    selector: 'app-products-sales-list',
    templateUrl: './products-sales-list.component.html',
    styleUrls: ['./products-sales-list.component.scss'],
    providers: [
        ListFiltersService,
        TicketsService, TicketsApi, TicketsState,
        ...productsProviders
    ],
    imports: [
        MaterialModule, RouterModule, TranslatePipe, FlexLayoutModule, AsyncPipe, NgIf,
        PopoverDateRangePickerFilterComponent, PopoverComponent, PopoverFilterDirective, ChipsFilterDirective,
        PaginatorComponent, SearchInputComponent, ChipsComponent, ProductsSalesListFilterComponent,
        ContextNotificationComponent, AggregatedDataComponent, CopyTextComponent, EllipsifyDirective,
        DragDropModule, ObfuscateStringPipe, DateTimePipe, LocalCurrencyPipe, VariantTextPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsSalesListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #ticketsService = inject(TicketsService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #dialog = inject(MatDialog);
    readonly #authSrv = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #productsSrv = inject(ProductsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$operatorDefaultCurrency = computed(() => AuthenticationService.operatorDefaultCurrency(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetTicketsRequest>({});
    readonly #$combinedAggregatedData = toSignal(this.#ticketsService.currencyAggregatedData.getCombined$(aggDataProduct));
    readonly #$baseAggregatedData = toSignal(this.#ticketsService.ticketList.getAggregatedData$(aggDataProductWithoutCurrency));
    #sortFilterComponent: SortFilterComponent;
    #showAggregates: boolean;
    #applyRelevance = false;

    private readonly _matSort = viewChild(MatSort);
    private readonly _paginatorComponent = viewChild(PaginatorComponent);
    private readonly _searchInputComponent = viewChild(SearchInputComponent);
    private readonly _filterComponent = viewChild(ProductsSalesListFilterComponent);
    private readonly _dateRangePickerComponent = viewChild(PopoverDateRangePickerFilterComponent);

    readonly obfuscatePattern = ObfuscatePattern;
    readonly eventType = EventType;

    readonly productsListColumns = ProductsFields;
    readonly defaultDisplayedColumns = [
        ProductsFields.code,
        ProductsFields.product,
        ProductsFields.variant,
        ProductsFields.purchaseDate,
        ProductsFields.barcode,
        ProductsFields.channel,
        ProductsFields.client,
        ProductsFields.prints,
        ProductsFields.validation,
        ProductsFields.state,
        ProductsFields.price,
        ProductsFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('PRODUCTS')?.filter(
        col => Object.values(ProductsFields).map(col => String(col)).includes(col)) || this.defaultDisplayedColumns;

    readonly fixedColumns: string[] = [ProductsFields.price];

    readonly initSortCol = 'purchase_date';
    readonly initSortDir = 'desc';
    readonly productsPageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.REC_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#ticketsService.ticketList.getMetaData$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request()?.q && this.#$request()?.purchase_date_from)));

    readonly products$ = this.#ticketsService.ticketList.getData$();
    readonly $productsMetadata = toSignal(this.#ticketsService.ticketList.getMetaData$());
    readonly $productsAggregatedData = computed(() => {
        if (this.#$request().currency_code) {
            return {
                aggregatedData: this.#$combinedAggregatedData(), currency: this.#$request().currency_code,
                aggregationMetrics: aggDataProduct
            };
        } else {
            return { aggregatedData: this.#$baseAggregatedData(), aggregationMetrics: aggDataTicketWithoutCurrency, currency: null };
        }
    });

    readonly $loadingData = toSignal(booleanOrMerge([
        this.#ticketsService.ticketList.loading$(),
        this.#ticketsService.ticketList.loadingExport$(),
        this.#ticketsService.currencyAggregatedData.loading$(),
        this.#productsSrv.product.inProgress$()
    ]));

    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    startDate: string;
    endDate: string;

    trackByFn = (_: number, item: OrderItem): number => item.id;

    ngOnInit(): void {
        this.#showAggregates = AuthenticationService.isSomeRoleInUserRoles(
            this.#$user(), [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.EVN_MGR, UserRoles.CNL_MGR,
            UserRoles.REC_MGR]);
    }

    ngAfterViewInit(): void {
        // Set default filters
        const obs: Observable<boolean>[] = [of(false)];
        const urlParameters = Object.assign({}, this.activatedRoute.snapshot.queryParams);

        if (!urlParameters['state'] || (!urlParameters['startDate'] && !urlParameters['endDate'] && !urlParameters['noDate'])) {
            urlParameters['state'] = urlParameters['state'] || TicketState.purchase;
            if (!urlParameters['noDate']) {
                urlParameters['startDate'] = urlParameters['startDate'] || new Date(new Date().setHours(0, 0, 0, 0)).toISOString();
                urlParameters['endDate'] = urlParameters['endDate'] || new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            }
            obs.push(of(true));
        }
        this.startDate = urlParameters['startDate'];
        this.endDate = urlParameters['endDate'];

        if (this.#$areCurrenciesShown() && !urlParameters['currency']) {
            this.#ticketsService.filterCurrencyList.load({
                item_type: OrderItemType.product,
                purchase_date_from: this.startDate,
                purchase_date_to: this.endDate,
                limit: 200
            });

            obs.push(this.#ticketsService.filterCurrencyList.getData$()
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
            this.#sortFilterComponent = new SortFilterComponent(this._matSort());
            if (setDefaultFilters.some(Boolean)) {
                this.router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: urlParameters, replaceUrl: true })
                    .then(() => {
                        this.initListFilteredComponent([
                            this._paginatorComponent(),
                            this.#sortFilterComponent,
                            this._searchInputComponent(),
                            this._filterComponent(),
                            this._dateRangePickerComponent()
                        ]);
                    });
            } else {
                this.initListFilteredComponent([
                    this._paginatorComponent(),
                    this.#sortFilterComponent,
                    this._searchInputComponent(),
                    this._filterComponent(),
                    this._dateRangePickerComponent()
                ]);
            }
        });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#ticketsService.ticketList.clear();
        this.#ticketsService.currencyAggregatedData.clear();
    }

    loadData(filters: FilterItem[]): void {
        const request: GetTicketsRequest = {
            limit: this.productsPageSize,
            aggs: this.#showAggregates,
            offset: 0,
            type: [TicketDetailType.product]
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
                    case 'DATE_RANGE':
                        request.purchase_date_from = values[0].value.start;
                        request.purchase_date_to = values[0].value.end;
                        break;
                    case 'CHANNEL_ENTITY':
                        request.channel_entity_id = values[0].value;
                        break;
                    case 'EVENT_ENTITY':
                        request.event_entity_id = values[0].value;
                        break;
                    case 'CHANNEL':
                        request.channel_id = values.map(val => val.value);
                        break;
                    case 'PRODUCT_ID':
                        request.product_id = values.map(val => val.value);
                        break;
                    case 'STATE':
                        request.state = values[0].value;
                        break;
                    case 'CURRENCY':
                        // currency_code is not set by the filter in case of only one operator currency or monocurrency
                        request.currency_code = values[0].value;
                        break;
                }
            }
        });

        //TODO(MULTICURRENCY): delete this.#$user().currency when the multicurrency functionality is finished
        request.currency_code = this.#$areCurrenciesShown() ? request.currency_code :
            (this.#$operatorCurrencyCodes()?.[0] ?? this.#$user().currency);
        this.#$request.set(request);
        if (this.#$request().currency_code) {
            this.#ticketsService.currencyAggregatedData.load(this.#$request());
            this.#ticketsService.ticketList.load(this.#$request(), this.#applyRelevance);
        } else {
            this.#ticketsService.ticketList.load(this.#$request(), this.#applyRelevance);
        }
    }

    isTicketStateValidForValidations(ticketState: TicketState): boolean {
        return [TicketState.purchase, TicketState.secMktPurchase, TicketState.issue].includes(ticketState);
    }

    exportProducts(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataProduct,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_PRODUCTS')
        }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_PRODUCTS', exportList.fields.map(resultData => resultData.field));
                this.#ticketsService.ticketList.export(this.#$request(), exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
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
                fieldDataGroups: productsColumnList,
                selectedFields: productsColumnList
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(buyerColumn => this.displayedColumns.includes(buyerColumn.field))
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...productsColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, ProductsFields.actions];
                this.#tableSrv.setColumns('PRODUCTS', this.displayedColumns);
                this.#ref.markForCheck();
            });
    }

    dropItem(event: CdkDragDrop<ProductsFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('PRODUCTS', this.displayedColumns);
        }
    }
}

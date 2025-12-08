import {
    GetB2bClientTransactionsRequest, B2bClientOperationType, B2bService
} from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, ExportDialogComponent, FilterItem, ListFilteredComponent, ListFiltersService, ObDialogService,
    PaginatorComponent, PopoverDateRangePickerFilterComponent, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { AggregatedData, AggregationMetricType, DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, skip, switchMap } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { b2bClientBalanceAggMetrics } from './b2b-client-balance-aggregated-data';
import { b2bClientTransactionsExportData } from './b2b-client-transactions-export-data';
import { B2bClientBalanceOperationDialogComponent } from './balance-operation/b2b-client-balance-operation-dialog.component';
import {
    B2bClientEconomicManagementCurrencyFilterComponent
} from './currency-filter/b2b-client-economic-management-currency-filter.component';
import { B2bClientEconomicManagementFilterComponent } from './filter/b2b-client-economic-management-filter.component';

@Component({
    selector: 'app-b2b-client-economic-management',
    templateUrl: './b2b-client-economic-management.component.html',
    styleUrls: ['./b2b-client-economic-management.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientEconomicManagementComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {
    readonly #tableSrv = inject(TableColConfigService);
    readonly #b2bSrv = inject(B2bService);
    readonly #auth = inject(AuthenticationService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #dialogSrv = inject(ObDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    #request: GetB2bClientTransactionsRequest;
    #entityId: number;
    #b2bClientId: number;
    #clientHasAccount: boolean;
    readonly #$clientBalance = toSignal(this.#b2bSrv.getB2bClientBalance$());

    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(B2bClientEconomicManagementFilterComponent) private readonly _filterComponent: B2bClientEconomicManagementFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;
    @ViewChild('currencyFilter') private readonly _currencyFilterComponent: B2bClientEconomicManagementCurrencyFilterComponent;

    readonly displayedColumns = [
        'created', 'effective_date', 'transaction_type', 'channel', 'user', 'order_code',
        'notes', 'previous_balance', 'amount', 'balance', 'credit', 'debt'
    ];

    readonly clientTransactionsPageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly clientTransactionsMetadata$ = this.#b2bSrv.getB2bClientTransactionsListMetadata$();
    readonly clientTransactions$ = this.#b2bSrv.getB2bClientTransactionsListData$();
    readonly clientHasAccount$ = this.#b2bSrv.getB2bClientBalance$().pipe(map(Boolean));
    readonly entityId$ = this.#b2bSrv.getB2bClient$().pipe(first(Boolean), map(b2bClient => b2bClient.entity?.id));
    readonly isOperatorUser$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly operationTypes = B2bClientOperationType;
    readonly isInProgress$ = booleanOrMerge([
        this.#b2bSrv.isB2bClientBalanceInProgress$(),
        this.#b2bSrv.isB2bClientTransactionsListLoading$()
    ]);

    readonly $currencies = toSignal(this.#auth.getLoggedUser$()
        .pipe(first(), map(user => AuthenticationService.operatorCurrencyCodes(user) ?? null)));

    readonly b2bClientBalanceAggregatedData$ = this.#b2bSrv.getB2bClientBalance$()
        .pipe(
            filter(Boolean),
            map(balance => new AggregatedData({
                overall: [
                    {
                        name: 'balance',
                        type: AggregationMetricType.count,
                        value: balance.balance
                    },
                    {
                        name: 'credit_limit',
                        type: AggregationMetricType.count,
                        value: balance.credit_limit
                    },
                    {
                        name: 'debt',
                        type: AggregationMetricType.count,
                        value: balance.debt
                    },
                    {
                        name: 'total_available',
                        type: AggregationMetricType.count,
                        value: balance.total_available
                    }
                ]
            }, b2bClientBalanceAggMetrics))
        );

    readonly b2bClientBalanceAggMetrics = b2bClientBalanceAggMetrics;
    readonly $selectedCurrency = signal<string>(null);
    // A user can have a monoCurrency structure in a multiCurrency operator, so it will only work with one currency
    readonly $migrated = signal(false);

    constructor() {
        super();
        //Date filter required and max range 6 months
        const urlParameters = Object.assign({}, this.activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['endDate']) {
            urlParameters['startDate'] =
                new Date(
                    new Date(
                        new Date().setHours(0, 0, 0, 0)
                    ).setMonth(new Date().getMonth() - 5)
                ).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            // Replace navigator state to prevent back navigation button from crashing the component
            this.router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: urlParameters, replaceUrl: true });
        }
    }

    onRemoveFunction = (): void => this.listFiltersService.resetFilters(['DATE_RANGE', 'CURRENCY']);

    ngOnInit(): void {
        // First Load
        // When there is no account, there is an error in loadB2bClient, so this subscribe is not executed
        this.#b2bSrv.getB2bClient$()
            .pipe(first(Boolean))
            .subscribe(b2bClient => {
                this.#b2bClientId = b2bClient.id;
                this.#entityId = b2bClient.entity?.id;
                this.#b2bSrv.loadB2bClientBalance(this.#b2bClientId, this.#entityId);
            });

        // First Load
        this.#b2bSrv.getB2bClientBalance$()
            .pipe(first(Boolean))
            .subscribe(balance => {
                this.#clientHasAccount = true;
                if (balance.currencies_balance) { // Multicurrency
                    this.$migrated.set(true);
                    this.refresh();
                } else { // Monocurrency
                    this.$selectedCurrency.set(balance.currency_code);
                    this.#b2bSrv.loadB2bClientTransactionsList(this.#b2bClientId, this.#request);
                }
            });
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._searchInputComponent,
            this._filterComponent,
            this._dateRangePickerComponent,
            this._currencyFilterComponent
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#b2bSrv.clearB2bClientBalance();
        this.#b2bSrv.clearB2bClientTransactionsList();
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {};
        this.#request.entity_id = this.#entityId;
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entity_id = values[0].value;
                        break;
                    case 'TRANSACTION_TYPE':
                        this.#request.type = values[0].value;
                        break;
                    case 'DATE_RANGE':
                        this.#request.transaction_date_from = values[0].value.start;
                        this.#request.transaction_date_to = values[0].value.end;
                        break;
                    case 'CURRENCY':
                        this.#request.currency_code = values[0].value;
                        break;
                }
            }
        });
        if (this.#clientHasAccount && !this.$migrated()) { // monocurrency or client without multicurrency structure
            this.#b2bSrv.loadB2bClientTransactionsList(this.#b2bClientId, this.#request);
        } else if (this.#clientHasAccount && this.$migrated()) { // multicurrency

            if (!this.#request.currency_code) { //there is no currency in the filter
                this._currencyFilterComponent.openCurrencySelectionDialog();
            } else {
                if (
                    (!this.$selectedCurrency() && this.#request.currency_code) || //currency in the filter for the first time
                    (this.$selectedCurrency() !== this.#request.currency_code) // currency changed in the filter
                ) {
                    this.$selectedCurrency.set(this.#request.currency_code);
                    this.#b2bSrv.loadB2bClientBalance(this.#b2bClientId, this.#entityId, this.$selectedCurrency());
                }
                this.#b2bSrv.loadB2bClientTransactionsList(this.#b2bClientId, this.#request);
            }
        }
    }

    createAccount(): void {
        this.#b2bSrv.createB2bClientBalance(this.#b2bClientId, this.#entityId)
            .subscribe(() => {
                this.#b2bSrv.loadB2bClientBalance(this.#b2bClientId, this.#entityId);
                this.#ephemeralMsg.showSuccess({ msgKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.ACCOUNT.CREATION_SUCCESS' });
            });
    }

    openNewOperationDialog(operationType: B2bClientOperationType): void {
        const data = {
            entityId: this.#entityId,
            clientId: this.#b2bClientId,
            operationType,
            clientBalance: this.#$clientBalance(),
            currency: this.$selectedCurrency()
        };

        this.#dialogSrv.open(B2bClientBalanceOperationDialogComponent, data)
            .beforeClosed()
            .subscribe(actionPerformed => {
                if (actionPerformed) {
                    this.#b2bSrv.loadB2bClientBalance(this.#b2bClientId, this.#entityId, this.$selectedCurrency());

                    this.#b2bSrv.getB2bClientBalance$()
                        .pipe(skip(1), first(Boolean))
                        .subscribe(() => {
                            this.#b2bSrv.loadB2bClientTransactionsList(this.#b2bClientId, this.#request);
                        });

                    this.#ephemeralMsg.showSaveSuccess();
                }
            });
    }

    exportTransactions(): void {
        this.#dialogSrv.open(
            ExportDialogComponent, {
            exportData: b2bClientTransactionsExportData,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_B2B_CLIENTS')
        })
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(exportList => {
                    this.#tableSrv.setColumns('EXP_B2B_CLIENTS', exportList.fields.map(resultData => resultData.field));
                    return this.#b2bSrv.b2bClientTransactions.export.save(this.#b2bClientId, {
                        entity_id: this.#request.entity_id,
                        filter: {
                            transaction_date_from: this.#request.transaction_date_from,
                            transaction_date_to: this.#request.transaction_date_to,
                            currency_code: this.$selectedCurrency(),
                            ...(this.#request.q ? { q: this.#request.q } : {}),
                            ...(this.#request.type ? { q: this.#request.type } : {})
                        },
                        ...exportList
                    });
                }),
                filter(result => !!result?.export_id)
            )
            .subscribe(() => this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' }));
    }
}

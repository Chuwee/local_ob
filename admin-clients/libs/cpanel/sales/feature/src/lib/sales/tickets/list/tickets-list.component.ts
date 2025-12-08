import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { TicketsFields, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import {
    TableColConfigService, GetTicketsRequest, TicketActionTypes, TicketState, TicketType, EventType, aggDataTicketWithoutCurrency,
    OrderItem, aggDataTicket
} from '@admin-clients/shared/common/data-access';
import {
    ColSelectionDialogComponent,
    EphemeralMessageService, ExportDialogComponent, FilterItem, ListFilteredComponent, ListFiltersService,
    ObMatDialogConfig, PaginatorComponent, PopoverDateRangePickerFilterComponent, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, ExportFormat, FieldData } from '@admin-clients/shared/data-access/models';
import { ObfuscatePattern } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild, inject, computed,
    signal, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { first, forkJoin, Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { TicketsListFilterComponent } from './filter/tickets-list-filter.component';
import { exportDataTicketAction } from './ticket-actions-export-data';
import { ticketsColumnList } from './tickets-column-list';
import { exportDataTicket } from './tickets-export-data';

@Component({
    selector: 'app-tickets-list',
    templateUrl: './tickets-list.component.html',
    styleUrls: ['./tickets-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketsListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #ticketsService = inject(TicketsService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #dialog = inject(MatDialog);
    readonly #authSrv = inject(AuthenticationService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$operatorDefaultCurrency = computed(() => AuthenticationService.operatorDefaultCurrency(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetTicketsRequest>({});
    readonly #$combinedAggregatedData = toSignal(this.#ticketsService.currencyAggregatedData.getCombined$());
    readonly #$baseAggregatedData = toSignal(this.#ticketsService.ticketList.getAggregatedData$(aggDataTicketWithoutCurrency));
    #sortFilterComponent: SortFilterComponent;
    #showAggregates: boolean;
    #applyRelevance = false;

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(TicketsListFilterComponent) private readonly _filterComponent: TicketsListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    readonly invitation = TicketType.invitation;
    readonly obfuscatePattern = ObfuscatePattern;
    readonly eventType = EventType;

    readonly ticketsListColumns = TicketsFields;
    readonly defaultDisplayedColumns = [
        TicketsFields.code,
        TicketsFields.event,
        TicketsFields.session,
        TicketsFields.sessionDate,
        TicketsFields.purchaseDate,
        TicketsFields.sector,
        TicketsFields.priceType,
        TicketsFields.barcode,
        TicketsFields.channel,
        TicketsFields.client,
        TicketsFields.prints,
        TicketsFields.validation,
        TicketsFields.state,
        TicketsFields.price,
        TicketsFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('TICKETS')?.filter(
        col => Object.values(TicketsFields).map(col => String(col)).includes(col)) || this.defaultDisplayedColumns;

    readonly fixedColumns: string[] = [TicketsFields.price];

    readonly initSortCol = 'purchase_date';
    readonly initSortDir = 'desc';
    readonly ticketsPageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.REC_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#ticketsService.ticketList.getMetaData$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request()?.q && this.#$request()?.purchase_date_from)));

    readonly tickets$ = this.#ticketsService.ticketList.getData$();
    readonly $ticketsMetadata = toSignal(this.#ticketsService.ticketList.getMetaData$());
    readonly $ticketsAggregatedData = computed(() => {
        if (this.#$request().currency_code) {
            return {
                aggregatedData: this.#$combinedAggregatedData(), currency: this.#$request().currency_code,
                aggregationMetrics: aggDataTicket
            };
        } else {
            return { aggregatedData: this.#$baseAggregatedData(), aggregationMetrics: aggDataTicketWithoutCurrency, currency: null };
        }
    });

    readonly $loadingData = toSignal(booleanOrMerge([
        this.#ticketsService.ticketList.loading$(),
        this.#ticketsService.ticketList.loadingExport$(),
        this.#ticketsService.currencyAggregatedData.loading$()
    ]));

    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    startDate: string;
    endDate: string;

    ngOnInit(): void {
        this.#showAggregates = AuthenticationService.isSomeRoleInUserRoles(
            this.#$user(),
            [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.REC_MGR]);
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

    trackByFn = (_: number, item: OrderItem): number => item.id;

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#ticketsService.ticketList.clear();
        this.#ticketsService.currencyAggregatedData.clear();
    }

    loadData(filters: FilterItem[]): void {
        const request: GetTicketsRequest = {
            limit: this.ticketsPageSize,
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
                    case 'STATE':
                        request.state = Object.values(TicketState).find(val => val === values[0].value);
                        break;
                    case 'CHANNEL':
                        request.channel_id = values.map(val => val.value);
                        break;
                    case 'CHANNEL_ENTITY':
                        request.channel_entity_id = values[0].value;
                        break;
                    case 'EVENT':
                        request.event_id = values.map(val => val.value);
                        break;
                    case 'TICKET_TYPE':
                        request.ticket_type = values[0].value;
                        break;
                    case 'VALIDATION':
                        request.validation = values[0].value;
                        break;
                    case 'PRINT':
                        request.print = values[0].value;
                        break;
                    case 'EVENT_ENTITY':
                        request.event_entity_id = values[0].value;
                        break;
                    case 'SESSION':
                        request.session_id = values.map(val => val.value);
                        break;
                    case 'SECTOR':
                        request.sector_id = values.map(val => val.value);
                        break;
                    case 'PRICE_TYPE':
                        request.price_type_id = values.map(val => val.value);
                        break;
                    case 'PROFESSIONAL_CLIENT':
                        request.client_entity_id = values.map(val => val.value);
                        break;
                    case 'DATE_RANGE':
                        request.purchase_date_from = values[0].value.start;
                        request.purchase_date_to = values[0].value.end;
                        break;
                    case 'SESSION_DATE_FROM':
                        request.session_start_date_from = values[0].value;
                        break;
                    case 'SESSION_DATE_TO':
                        request.session_start_date_to = values[0].value;
                        break;
                    case 'CURRENCY':
                        // currency_code is not set by the filter in case of only one operator currency or monocurrency
                        request.currency_code = values[0].value;
                        break;
                    case 'ORIGIN_MARKET':
                        request.origin_market = values[0].value;
                        break;
                    case 'REALLOCATION_REFUND':
                        request.reallocation_refund = values[0].value;
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

    exportTickets(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataTicket,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_TICKETS')
        }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_TICKETS', exportList.fields.map(resultData => resultData.field));
                this.#ticketsService.ticketList.export(this.#$request(), exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });

    }

    exportTicketActions(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataTicketAction,
            exportFormat: ExportFormat.csv,
            selectedFields: this.#tableSrv.getColumns('EXP_TICKETS_ACTIONS')
        }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_TICKETS_ACTIONS', exportList.fields.map(resultData => resultData.field));
                this.#ticketsService.ticketList.exportActions({
                    ...this.#$request(),
                    action_types: [TicketActionTypes.print, TicketActionTypes.download]
                }, exportList)
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
                fieldDataGroups: ticketsColumnList,
                selectedFields: ticketsColumnList
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(buyerColumn => this.displayedColumns.includes(buyerColumn.field))
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...ticketsColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, TicketsFields.actions];
                this.#tableSrv.setColumns('TICKETS', this.displayedColumns);
                this.#ref.markForCheck();
            });
    }

    dropItem(event: CdkDragDrop<TicketsFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('TICKETS', this.displayedColumns);
        }
    }
}

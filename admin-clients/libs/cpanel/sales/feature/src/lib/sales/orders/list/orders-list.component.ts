import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    aggDataOrder, aggDataOrderWithoutCurrency, OrdersService, GetOrdersWithFieldsRequest, OrderWithFieldsItem, VmOrderWithFields,
    OrdersFields, ordersListFields, PostMassiveRefundOrdersRequest, PostMassiveRefundOrdersResponse, OrderWithFields
} from '@admin-clients/cpanel-sales-data-access';
import { TableColConfigService, EntitiesBaseService, EntityExternalBarcodesFormat } from '@admin-clients/shared/common/data-access';
import {
    ListFiltersService, ListFilteredComponent, SortFilterComponent, PaginatorComponent, SearchInputComponent,
    EphemeralMessageService, MessageDialogService, FilterItem, ObMatDialogConfig, DialogSize, MessageType,
    PopoverDateRangePickerFilterComponent, ExportDialogComponent,
    AggregatedDataComponent, ContextNotificationComponent,
    CopyTextComponent, ChipsComponent, PopoverComponent, PopoverFilterDirective, ChipsFilterDirective,
    ColSelectionDialogComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    DateTimeFormats, ExportDialogData, ExportRequest, ExportFormat, FieldData
} from '@admin-clients/shared/data-access/models';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { EllipsifyDirective, TableMaxHeightDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, OnDestroy,
    OnInit, ViewChild, inject, computed, signal
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { forkJoin, Observable, of } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import { MassiveRefundDialogComponent } from '../massive-refund/massive-refund-dialog.component';
import { MassiveRefundSummaryDialogComponent } from '../massive-refund-summary/massive-refund-summary-dialog.component';
import { ResendCodesDialogComponent } from '../resend-codes/resend-codes-dialog.component';
import { OrdersListFilterComponent } from './filter/orders-list-filter.component';
import { exportDataOrder, exportLoyaltyProgramOrderData } from './orders-export-data';
import { ordersListColumn } from './orders-list-column';

const DEFAULT_HIDDEN_AGGREGATIONS = ['totalPrimaryMktTickets', 'totalSecMktTickets', 'totalDonations'];
@Component({
    selector: 'app-orders-list',
    templateUrl: './orders-list.component.html',
    styleUrls: ['./orders-list.component.scss'],
    providers: [ListFiltersService],
    imports: [
        MaterialModule, CdkDropList, CdkDrag, RouterLink, AsyncPipe, FlexLayoutModule,
        PaginatorComponent, SearchInputComponent, PopoverDateRangePickerFilterComponent,
        OrdersListFilterComponent, PaginatorComponent, ChipsComponent, ChipsFilterDirective, PopoverComponent, PopoverFilterDirective,
        AggregatedDataComponent, EllipsifyDirective, CopyTextComponent, ContextNotificationComponent, TranslatePipe, DateTimePipe,
        LocalCurrencyPipe, TableMaxHeightDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrdersListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #ordersService = inject(OrdersService);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #trackingService = inject(TrackingService);
    readonly #tableSrv = inject(TableColConfigService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$operatorDefaultCurrency = computed(() => AuthenticationService.operatorDefaultCurrency(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetOrdersWithFieldsRequest>({});
    readonly #$combinedAggregatedData = toSignal(this.#ordersService.currencyAggregatedData.getCombined$());
    readonly #$baseAggregatedData = toSignal(this.#ordersService.getOrdersWithFieldsListAggregatedData$(aggDataOrderWithoutCurrency));
    readonly #canReadMultipleEntities$ = toSignal(this.#authSrv.canReadMultipleEntities$());

    #sortFilterComponent: SortFilterComponent;
    #applyRelevance = false;
    #showAggregates: boolean;

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(OrdersListFilterComponent) private readonly _filterComponent: OrdersListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    readonly ordersListColumns = OrdersFields;
    readonly defaultDisplayedColumns = [
        OrdersFields.channel,
        OrdersFields.code,
        OrdersFields.type,
        OrdersFields.date,
        OrdersFields.client,
        OrdersFields.event,
        OrdersFields.ticketsCount,
        OrdersFields.basePrice,
        OrdersFields.promotions,
        OrdersFields.charges,
        OrdersFields.finalPrice,
        OrdersFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('ORDERS')?.filter(col => Object.values(OrdersFields).map(col => String(col)).includes(col))
        || this.defaultDisplayedColumns;

    hiddenAggsColumn = DEFAULT_HIDDEN_AGGREGATIONS;

    readonly fixedColumns: string[] = [OrdersFields.finalPrice];

    readonly initSortCol = 'date';
    readonly initSortDir = 'desc';
    readonly ordersPageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isInternalOpr$ = this.#authSrv.getLoggedUser$()
        .pipe(
            filter(user => !!user),
            map(user =>
                AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])
                && AuthenticationService.isInternalUser(user)
            ),
            takeUntilDestroyed(this.#destroyRef)
        );

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.CNL_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#ordersService.getOrdersWithFieldsListMetadata$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request().q && this.#$request().purchase_date_from)));

    readonly $orders = toSignal(this.#ordersService.getOrdersWithFieldsListData$()
        .pipe(filter(Boolean), map(orders => this.#mapToVmOrderWithFields(orders))));

    readonly $ordersMetadata = toSignal(this.#ordersService.getOrdersWithFieldsListMetadata$());
    readonly $ordersAggregatedData = computed(() => {
        if (this.#$request().currency_code) {
            return {
                aggregatedData: this.#$combinedAggregatedData(), currency: this.#$request().currency_code,
                aggregationMetrics: aggDataOrder
            };
        } else {
            return { aggregatedData: this.#$baseAggregatedData(), aggregationMetrics: aggDataOrderWithoutCurrency, currency: null };
        }
    });

    readonly enableResendCodes$ = this.#entitiesService.getEntity$()
        .pipe(
            first(entity => !!entity),
            map(entity => {
                const isExternalBarcodeEnabled = entity.settings?.external_integration?.barcode?.enabled;
                const externalBarcodeFormat: string | undefined = entity.settings?.external_integration?.barcode?.integration_id;
                return isExternalBarcodeEnabled && externalBarcodeFormat === EntityExternalBarcodesFormat.ifema;
            })
        );

    readonly $loadingData = toSignal(booleanOrMerge([
        this.#ordersService.isOrdersWithFieldsListLoading$(),
        this.#ordersService.isExportOrdersLoading$(),
        this.#ordersService.currencyAggregatedData.loading$()
    ]));

    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    startDate: string;
    endDate: string;

    ngOnInit(): void {
        this.#entitiesService.loadEntity(this.#$user().entity.id);
        this.#showAggregates = AuthenticationService.isSomeRoleInUserRoles(
            this.#$user(), [UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.CNL_MGR]);
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

        if (this.#$areCurrenciesShown() && !urlParameters['currency'] && !urlParameters['noCurrency']) {
            this.#ordersService.filterCurrencyList.load({
                purchase_date_from: this.startDate,
                purchase_date_to: this.endDate,
                limit: 200
            });

            obs.push(this.#ordersService.filterCurrencyList.getData$()
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
        this.#entitiesService.clearEntity();
        this.#ordersService.clearOrdersWithFields();
        this.#ordersService.currencyAggregatedData.clear();
    }

    loadData(filters: FilterItem[]): void {
        const requestFields = {
            fields: ordersListFields
        };
        const request: GetOrdersWithFieldsRequest = {
            limit: this.ordersPageSize,
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
                    case 'ORDER_ALIVE':
                        request.order_alive = values[0].value;
                        break;
                    case 'REALLOCATION_REFUND':
                        request.reallocation_refund = values[0].value;
                        break;
                    case 'PAGINATION':
                        request.limit = values[0].value.limit;
                        request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        request.q = values[0].value;
                        break;
                    case 'TYPE':
                        request.type = values.map(val => val.value);
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
                    case 'EVENT_ENTITY':
                        request.event_entity_id = values[0].value;
                        break;
                    case 'MERCHANT':
                        request.merchant = values[0].value;
                        break;
                    case 'USER':
                        request.user_id = values[0].value;
                        break;
                    case 'PROFESSIONAL_CLIENT':
                        request.client_entity_id = values.map(val => val.value);
                        break;
                    case 'SESSION':
                        request.session_id = values.map(val => val.value);
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
                    case 'DELIVERY':
                        request.delivery = values.map(val => val.value);
                        break;
                    case 'CURRENCY':
                        // currency_code is not set by the filter in case of only one operator currency or monocurrency
                        request.currency_code = values[0].value;
                        break;
                }
            }
        });

        //TODO(MULTICURRENCY): delete this.#$user().currency when the multicurrency functionality is finished
        const urlParams = this.activatedRoute.snapshot.queryParams;
        const noCurrencyFlag = urlParams['noCurrency'] === 'true';

        request.currency_code = this.#$areCurrenciesShown() && !noCurrencyFlag ? request.currency_code :
            (this.#$areCurrenciesShown() && noCurrencyFlag ? undefined :
                (this.#$operatorCurrencyCodes()?.[0] ?? this.#$user().currency));
        this.#$request.set(request);
        if (this.#$request().currency_code) {
            this.#ordersService.currencyAggregatedData.load(this.#$request());
            this.#ordersService.loadOrdersWithFieldsList(this.#$request(), requestFields, this.#applyRelevance);
        } else {
            this.#ordersService.loadOrdersWithFieldsList(this.#$request(), requestFields, this.#applyRelevance);
        }
    }

    exportOrders(): void {
        const exportData = [...exportDataOrder];
        if (this.#canReadMultipleEntities$() || this.#$user().entity.settings.allow_loyalty_points) {
            exportData.splice(exportData.length - 1, 0, exportLoyaltyProgramOrderData);
        }
        this.#dialog.open<ExportDialogComponent, Partial<ExportDialogData>, ExportRequest>(
            ExportDialogComponent, new ObMatDialogConfig({
                exportData,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_ORDERS')
            })
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_ORDERS', exportList.fields.map(resultData => resultData.field));
                this.#ordersService.exportOrders(this.#$request(), exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                        this.#trackingService.sendEventTrack(
                            'Export',
                            'Orders',
                            exportList.fields.some(el => el.field === 'customer.metadata') ?
                                'Exported with UTMs' :
                                'Exported without UTMs');
                    });
            });
    }

    openResendCodesDialog(): void {
        this.#dialog.open<ResendCodesDialogComponent, null, string>(ResendCodesDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(response => {
                if (response) {
                    this.#msgDialogSrv.showInfo({
                        title: 'ORDERS.RESEND_CODES.CONFIRMATION_TITLE',
                        message: 'ORDERS.RESEND_CODES.CONFIRMATION_MESSAGE'
                    });
                }
            });
    }

    openMassiveRefundDialog(): void {
        this.#dialog.open<MassiveRefundDialogComponent, null, PostMassiveRefundOrdersRequest>(
            MassiveRefundDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(
                filter(request => !!request),
                switchMap(request => this.#msgDialogSrv.showWarn({
                    size: DialogSize.MEDIUM,
                    title: 'ORDERS.MASSIVE_REFUND.CONFIRM.TITLE',
                    message: 'ORDERS.MASSIVE_REFUND.CONFIRM.MESSAGE',
                    actionLabel: 'ACTIONS.REFUND.CONFIRM'
                })
                    .pipe(map(confirmed => ({ confirmed, request })))),
                filter(({ confirmed }) => !!confirmed),
                switchMap(({ request }) => this.#ordersService.massiveRefundOrders(request)),
                filter(summary => !!summary),
                tap(() => this.#ephemeralMessageSrv.show({
                    type: MessageType.info,
                    msgKey: 'ORDERS.MASSIVE_REFUND.REFUNDING'
                })),
                switchMap(summary =>
                    this.#dialog.open<MassiveRefundSummaryDialogComponent, Record<string, PostMassiveRefundOrdersResponse>, void>(
                        MassiveRefundSummaryDialogComponent, new ObMatDialogConfig({ summary })
                    ).beforeClosed())
            )
            .subscribe();
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
                fieldDataGroups: ordersListColumn,
                selectedFields: ordersListColumn
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(buyerColumn => this.displayedColumns.includes(buyerColumn.field))
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...ordersListColumn
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, OrdersFields.actions];
                this.#tableSrv.setColumns('ORDERS', this.displayedColumns);
            });
    }

    dropItem(event: CdkDragDrop<OrdersFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('ORDERS', this.displayedColumns);
        }
    }

    #mapToVmOrderWithFields(orders: OrderWithFields[]): VmOrderWithFields[] {
        return orders.map(order => ({
            ...order,
            eventColumnData: this.#prepareEventColumnData(order.items),
            eventColumnExtendedData: this.#prepareEventColumnExtendedData(order.items),
            totalCharges: this.#getTotalCharges(order),
            totalPromotions: this.#getTotalPromotions(order)
        }));
    }

    #prepareEventColumnData(items: OrderWithFieldsItem[]): string {
        let eventColumnData = '';

        //Prevent event name duplicates
        const eventNamesSet = new Set<string>();
        const eventNames = items.reduce<string[]>((acc, item) => {
            const eventName = item?.ticket?.allocation?.event?.name;
            if (eventName && !eventNamesSet.has(eventName)) {
                eventNamesSet.add(eventName);
                acc.push(eventName);
            }
            return acc;
        }, []);

        //Build column content
        eventColumnData = eventNames.join(', ');
        return eventColumnData;
    }

    #prepareEventColumnExtendedData(items: OrderWithFieldsItem[]): string {
        let eventColumnExtendedData = '';

        //Must filter by sessionIds to prevent duplicate allocations
        const sessionIds = new Set<number>();
        const allocationsToShow = items.reduce((acc, item) => {
            const currentItemSessionId = item?.ticket?.allocation?.session?.id;
            if (currentItemSessionId && !sessionIds.has(currentItemSessionId)) {
                sessionIds.add(currentItemSessionId);
                acc.push(item?.ticket?.allocation);
            }
            return acc;
        }, []);

        //Build tooltip content
        const allocationsData = allocationsToShow.reduce((acc, allocation) => {
            const dateTime = moment(allocation?.session?.date?.start).format(DateTimeFormats.shortDateTime);
            acc.push(allocation?.event?.name + '\n' + allocation?.venue?.name + '\n' + dateTime + '\n' + allocation?.session?.name);
            return acc;
        }, []);
        eventColumnExtendedData = allocationsData.join('\n ----- \n');
        return eventColumnExtendedData;
    }

    #getTotalPromotions(order: OrderWithFields): number {
        return -1 * ((order.price?.sales?.automatic ?? 0) + (order.price?.sales?.promotion ?? 0)
            + (order.price?.sales?.discount ?? 0) + (order.price?.sales?.order_automatic ?? 0)
            + (order.price?.sales?.order_collective ?? 0));
    }

    #getTotalCharges(order: OrderWithFields): number {
        return (order.price?.charges?.channel ?? 0) + (order.price?.charges?.promoter ?? 0)
            + (order.price?.charges?.reallocation ?? 0) + (order.price?.charges?.ticket_reallocation ?? 0)
            + (order.price?.charges?.secondary_market_channel ?? 0) + (order.price?.charges?.secondary_market_promoter ?? 0);
    }
}

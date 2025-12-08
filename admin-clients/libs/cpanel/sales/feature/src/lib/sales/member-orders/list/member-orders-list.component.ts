import {
    AuthenticationService, UserRoles
} from '@admin-clients/cpanel/core/data-access';
import { MemberOrdersService, GetMemberOrdersRequest, MemberOrderFields } from '@admin-clients/cpanel-sales-data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ListFiltersService, ListFilteredComponent, SortFilterComponent, PaginatorComponent, SearchInputComponent,
    EphemeralMessageService, FilterItem, ObMatDialogConfig, PopoverDateRangePickerFilterComponent, ExportDialogComponent,
    ColSelectionDialogComponent
} from '@admin-clients/shared/common/ui/components';
import {
    DateTimeFormats, ExportDialogData, ExportRequest, ExportFormat, FieldData
} from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, ViewChild, inject, signal, computed
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, first, map } from 'rxjs/operators';
import { MemberOrdersListFilterComponent } from './filter/member-orders-list-filter.component';
import { exportDataMemberOrderByMember } from './member-orders-by-member-export-data';
import { memberOrderColumnList } from './member-orders-column-list';
import { exportDataMemberOrder } from './member-orders-export-data';

@Component({
    selector: 'app-member-orders-list',
    templateUrl: './member-orders-list.component.html',
    styleUrls: ['./member-orders-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    readonly #tableSrv = inject(TableColConfigService);
    readonly #memberOrdersSrv = inject(MemberOrdersService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);

    readonly #$user = toSignal(this.#authSrv.getLoggedUser$().pipe(first()));
    readonly #$operatorCurrencyCodes = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user()));
    readonly #$areCurrenciesShown = computed(() => AuthenticationService.operatorCurrencyCodes(this.#$user())?.length > 1);
    readonly #$request = signal<GetMemberOrdersRequest>({});
    #sortFilterComponent: SortFilterComponent;
    #applyRelevance = false;

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(MemberOrdersListFilterComponent) private readonly _filterComponent: MemberOrdersListFilterComponent;
    @ViewChild(PopoverDateRangePickerFilterComponent) private readonly _dateRangePickerComponent: PopoverDateRangePickerFilterComponent;

    readonly memberOrderListColumns = MemberOrderFields;
    readonly defaultDisplayedColumns = [
        MemberOrderFields.clubName,
        MemberOrderFields.code,
        MemberOrderFields.type,
        MemberOrderFields.purchaseDate,
        MemberOrderFields.client,
        MemberOrderFields.basePrice,
        MemberOrderFields.promotions,
        MemberOrderFields.charges,
        MemberOrderFields.finalPrice,
        MemberOrderFields.actions
    ];

    displayedColumns = this.#tableSrv.getColumns('MEMBER_ORDERS')?.filter(
        col => Object.values(MemberOrderFields).map(col => String(col)).includes(col)) || this.defaultDisplayedColumns;

    readonly fixedColumns: string[] = [MemberOrderFields.finalPrice];

    readonly initSortCol = 'purchaseDate';
    readonly initSortDir = 'desc';
    readonly ordersPageSize = 20;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isExportEnabled$ = this.#authSrv.getLoggedUser$()
        .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.ENT_ANS, UserRoles.EVN_MGR, UserRoles.CNL_MGR]
        )));

    readonly emptyListWithWordAndDates$ = this.#memberOrdersSrv.getMemberOrdersListMetadata$()
        .pipe(map(metadata => !!(metadata?.total === 0 && this.#$request()?.q && this.#$request()?.purchase_date_from)));

    readonly memberOrders$ = this.#memberOrdersSrv.getMemberOrdersListData$();
    readonly memberOrdersMetadata$ = this.#memberOrdersSrv.getMemberOrdersListMetadata$();
    readonly reqInProgress$ = booleanOrMerge([this.#memberOrdersSrv.isMemberOrdersListLoading$()]);
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    readonly startDate: string;
    readonly endDate: string;

    constructor(
        router: Router,
        activatedRoute: ActivatedRoute
    ) {
        super();
        const urlParameters = Object.assign({}, activatedRoute.snapshot.queryParams);
        if (!urlParameters['startDate'] && !urlParameters['startDate'] && !urlParameters['noDate']) {
            urlParameters['startDate'] = new Date(new Date().setHours(0, 0, 0, 0)).toISOString();
            urlParameters['endDate'] = new Date(new Date().setHours(23, 59, 59, 999)).toISOString();
            router.navigate(['.'], { relativeTo: activatedRoute, queryParams: urlParameters, replaceUrl: true });
        }
        this.startDate = urlParameters['startDate'];
        this.endDate = urlParameters['endDate'];
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent,
            this._dateRangePickerComponent]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    loadData(filters: FilterItem[]): void {
        const request: GetMemberOrdersRequest = {
            limit: this.ordersPageSize,
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
                    case 'CHANNEL_ENTITY':
                        request.entity_id = values.map(val => val.value);
                        break;
                    case 'TYPE':
                        request.type = values.map(val => val.value);
                        break;
                    case 'STATE':
                        request.state = values.map(val => val.value);
                        break;
                    case 'DATE_RANGE':
                        request.purchase_date_from = values[0].value.start;
                        request.purchase_date_to = values[0].value.end;
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
        this.#memberOrdersSrv.loadMemberOrdersList(this.#$request(), this.#applyRelevance);
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

    exportMemberOrders(byMember: boolean): void {
        this.#dialog.open<ExportDialogComponent, Partial<ExportDialogData>, ExportRequest>(
            ExportDialogComponent, new ObMatDialogConfig({
                exportData: byMember ? exportDataMemberOrderByMember : exportDataMemberOrder,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_MEMBER_ORDERS')
            }
            ))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_MEMBER_ORDERS', exportList.fields.map(resultData => resultData.field));
                this.#memberOrdersSrv.exportMemberOrders(this.#$request(), byMember, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralSrv.showSuccess({
                            msgKey: 'ACTIONS.EXPORT.OK.MESSAGE'
                        });
                    });
            });
    }

    changeColSelection(): void {
        this.#dialog.open(ColSelectionDialogComponent, new ObMatDialogConfig(
            {
                fieldDataGroups: memberOrderColumnList,
                selectedFields: memberOrderColumnList
                    .map(columnGroup => columnGroup.fields)
                    .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                    .filter(buyerColumn => this.displayedColumns.includes(buyerColumn.field))
            }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...memberOrderColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, MemberOrderFields.actions];
                this.#tableSrv.setColumns('MEMBER_ORDERS', this.displayedColumns);
                this.#ref.markForCheck();
            });
    }

    dropItem(event: CdkDragDrop<MemberOrderFields[]>): void {
        if (!this.fixedColumns.includes(this.displayedColumns[event.currentIndex])
            && !this.fixedColumns.includes(this.displayedColumns[event.previousIndex])) {
            moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
            this.#tableSrv.setColumns('MEMBER_ORDERS', this.displayedColumns);
        }
    }
}

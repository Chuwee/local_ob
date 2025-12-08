import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntityFilterButtonComponent } from '@admin-clients/cpanel/organizations/entities/feature';
import {
    BuyersService, BuyerGender, BuyerType, Buyer, BuyersQuery, BuyerFields, buyersColumnList
} from '@admin-clients/cpanel-viewers-buyers-data-access';
import {
    EntitiesBaseService, EntitiesFilterFields, GetEntitiesRequest, TableColConfigService
} from '@admin-clients/shared/common/data-access';
import {
    ColSelectionDialogComponent,
    DialogSize, EphemeralMessageService, ExportDialogComponent, FilterComponent, FilterItem, ListFilteredComponent, ListFiltersService,
    MessageDialogService, ObMatDialogConfig, PaginatorComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import {
    DateTimeFormats, ExportDelivery, ExportField, ExportFormat, FieldData, IdName, PageableFilter
} from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, deepEqual, mergeObjects } from '@admin-clients/shared/utility/utils';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, effect, inject, OnDestroy, OnInit, viewChild, ViewContainerRef
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, take } from 'rxjs/operators';
import { NewBuyerDialogData } from '../create/model/new-buyer-dialog-data.model';
import { NewBuyerDialogComponent } from '../create/new-buyer-dialog.component';
import { buyerFilterElements as bfe } from './buyers-filter-elements';
import { BuyersSearchKeywordInputComponent } from './search-input/buyers-search-keyword-input.component';

@Component({
    selector: 'app-buyers-lists',
    templateUrl: './buyers-list.component.html',
    styleUrls: ['./buyers-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    standalone: false
})
export class BuyersListComponent extends ListFilteredComponent implements OnInit, OnDestroy {

    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #changeDet = inject(ChangeDetectorRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #buyersSrv = inject(BuyersService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #tableSrv = inject(TableColConfigService);

    readonly #onDestroy = new Subject<void>();
    // end filter components
    private _fieldOrder: Record<string, number> = {};
    private _showAggregates: boolean;
    private _oldBuyersQueryWithoutOffset: BuyersQuery;

    private readonly _$buyersFilterComponent = viewChild<FilterComponent>('filterContainer');
    private readonly _$matSort = viewChild(MatSort);
    private readonly _$paginatorComponent = viewChild(PaginatorComponent);
    private readonly _$searchKeywordInput = viewChild(BuyersSearchKeywordInputComponent);
    private readonly _$entityFilterButton = viewChild<EntityFilterButtonComponent>('entityFilterButton');

    private readonly _$entityId = toSignal(this.#auth.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultipleEntities =>
                canReadMultipleEntities ?
                    this.#entitiesService.getEntity$().pipe(map(entity => entity?.id))
                    : this.#auth.getLoggedUser$().pipe(first(Boolean), map(user => user.entity.id))
            )
        ));

    readonly PAGE_SIZE = 20;
    readonly RESERVED_COL_WIDTH = 150;
    readonly INIT_SORT_COL = BuyerFields.email;
    readonly INIT_SORT_DIR = 'asc' as SortDirection;
    readonly buyerListColumns = BuyerFields;
    readonly dateTimeFormats = DateTimeFormats;
    readonly defaultDisplayedColumns = [
        BuyerFields.email,
        BuyerFields.name,
        BuyerFields.surname,
        BuyerFields.purchaseCount,
        BuyerFields.ticketCount,
        BuyerFields.productCount,
        BuyerFields.actions
    ];

    readonly isDesktop$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(breakPointState => !breakPointState.matches));

    readonly smallDevice$ = this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(breakPointState => breakPointState.matches));

    readonly isEntitySelected$ = this.#auth.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultEnt => {
                if (canReadMultEnt) {
                    return this.#entitiesService.getEntity$().pipe(map(entity => !!entity));
                } else {
                    return of(true);
                }
            }),
            startWith(false),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly entitiesRequest: GetEntitiesRequest = {
        limit: 999,
        offset: 0,
        sort: 'name:asc',
        fields: [EntitiesFilterFields.name]
    };

    readonly $canReadMultipleEntities = toSignal(this.#auth.canReadMultipleEntities$());
    readonly loggedUser$ = this.#auth.getLoggedUser$().pipe(first(), shareReplay(1));
    readonly $buyersAggs = toSignal(this.#buyersSrv.getBuyersAggregatedData$().pipe(filter(Boolean)));
    readonly buyers$ = this.#buyersSrv.getBuyersData$();
    readonly buyersMetadata$ = this.#buyersSrv.getBuyersMetadata$().pipe(shareReplay(1));
    readonly isExportEnabled$ = this.loggedUser$.pipe(
        map(user => user && AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS, UserRoles.ENT_MGR, UserRoles.CRM_MGR]
        )));

    readonly currency$ = this.loggedUser$
        .pipe(
            map(user => user.currency),
            shareReplay(1)
        );

    readonly isLoading$ = booleanOrMerge([
        this.#buyersSrv.isBuyersListLoading$(),
        this.#buyersSrv.isBuyerDeleting$()
    ]);

    displayedColumns = this.#tableSrv.getColumns('BUYERS') || this.defaultDisplayedColumns;

    constructor() {
        super();

        let allTheFiltersRegistered = false;
        let onlyEntityFilterRegistered = false;

        // in the case where $canReadMultipleEntities() is true, the effect will make sense when changing from no entity
        // selected (only entity filter button registered) to entity selected (all the filters will be registered).
        // In the case where $canReadMultipleEntities() is false, all the filters will be registered at once.
        effect(() => {
            if (
                !allTheFiltersRegistered && this._$buyersFilterComponent() && this._$matSort() && this._$paginatorComponent() &&
                this._$searchKeywordInput() && (!this.$canReadMultipleEntities() || Number(this._$entityId()))
            ) {
                allTheFiltersRegistered = true;
                onlyEntityFilterRegistered = false;
                super.initListFilteredComponent([this._$entityFilterButton(), this._$buyersFilterComponent(),
                this._$paginatorComponent(), new SortFilterComponent(this._$matSort()), this._$searchKeywordInput()
                ].filter(Boolean));
            } else if (
                !onlyEntityFilterRegistered && !Number(this._$entityId()) && !this._$buyersFilterComponent() &&
                !this._$matSort() && !this._$paginatorComponent() && !this._$searchKeywordInput()
            ) {
                allTheFiltersRegistered = false;
                onlyEntityFilterRegistered = true;
                super.initListFilteredComponent([this._$entityFilterButton()]);
            }
        });
    }

    ngOnInit(): void {
        this.setFieldOrder();
        this.loggedUser$
            .subscribe(user => this._showAggregates = AuthenticationService.isSomeRoleInUserRoles(
                user, [UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS, UserRoles.ENT_MGR, UserRoles.CRM_MGR]
            ));
    }

    override ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#buyersSrv.clearQuery();
        this.#buyersSrv.clearBuyersList();
        this.#entitiesService.clearEntity();
    }

    openNewBuyerDialog(): void {
        this.#matDialog.open(
            NewBuyerDialogComponent,
            new ObMatDialogConfig({ entityId: this._$entityId() } as NewBuyerDialogData, this.#viewContainerRef)
        )
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe(id => this.router.navigate([id, 'basic-info'], { relativeTo: this.activatedRoute }));
    }

    deleteBuyer(buyer: Buyer): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_BUYER',
            message: 'BUYERS.DELETE_BUYER_WARNING',
            messageParams: { name: buyer.email },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#buyersSrv.deleteBuyer(buyer.id)
                        .subscribe(() => {
                            this.#ephemeralMsg.showSuccess({
                                msgKey: 'BUYERS.DELETE_BUYER_SUCCESS',
                                msgParams: { eventName: buyer.email }
                            });
                            this.refresh();
                        });
                }
            });
    }

    loadData(filterItems: FilterItem[]): void {
        if (
            !this.$canReadMultipleEntities() ||
            (filterItems.find(filterItem => (filterItem.key === bfe.entity.key) && filterItem.values?.length > 0) && this._$matSort())
        ) {
            const buyersQuery: BuyersQuery = { limit: this.PAGE_SIZE, offset: 0, aggs: this._showAggregates };
            filterItems.forEach(filterItem => this.setFilterFieldToFilter(buyersQuery, filterItem));
            const { offset, ...rest } = buyersQuery;
            if (deepEqual(rest, this._oldBuyersQueryWithoutOffset)) {
                buyersQuery.aggs = false;
            }
            this._oldBuyersQueryWithoutOffset = rest;
            this.#buyersSrv.updateQueryInMemory(buyersQuery);
            this.#buyersSrv.loadBuyers(buyersQuery);
        }
    }

    changeColSelection(): void {
        this.#matDialog.open(ColSelectionDialogComponent, new ObMatDialogConfig(
            {
                fieldDataGroups: buyersColumnList,
                selectedFields: this.#tableSrv.getColumns('BUYERS')
            }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...buyersColumnList
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                this.displayedColumns = [...sortResult, BuyerFields.actions];
                this.#tableSrv.setColumns('BUYERS', this.displayedColumns);
                this.#changeDet.markForCheck();
            });
    }

    idNameCollectionRenderer(items: IdName[]): string {
        return items?.map(idName => idName?.name).join(', ');
    }

    export(): void {
        this.#matDialog.open(ExportDialogComponent, new ObMatDialogConfig(
            {
                exportData: buyersColumnList,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_BUYERS')
            }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((exportList: { fields: ExportField[]; delivery: ExportDelivery; format: ExportFormat }) => {
                const fields = Object.values(BuyerFields);
                const sortedSelectedFields = exportList.fields
                    .sort((a, b) => fields.indexOf(a.field as BuyerFields) - fields.indexOf(b.field as BuyerFields));
                const exportReq = { ...exportList, fields: sortedSelectedFields };
                this.#tableSrv.setColumns('EXP_BUYERS', exportList.fields.map(resultData => resultData.field));
                this.#buyersSrv.getQuery$()
                    .pipe(take(1))
                    .subscribe(queryWrapper => {
                        this.#buyersSrv.exportBuyers(queryWrapper.query, exportReq)
                            .subscribe(result => {
                                if (result) {
                                    this.#ephemeralMsg.showSuccess({
                                        msgKey: 'ACTIONS.EXPORT.OK.MESSAGE'
                                    });
                                }
                            });
                    });
            });
    }

    dropItem(event: CdkDragDrop<BuyerFields[]>): void {
        moveItemInArray(this.displayedColumns, event.previousIndex, event.currentIndex);
        this.#tableSrv.setColumns('BUYERS', this.displayedColumns);
    }

    private setFieldOrder(): void {
        let i = 0;
        buyersColumnList.forEach(buyerColumnGroup =>
            buyerColumnGroup.fields.forEach(childDataField => this._fieldOrder[childDataField.field] = ++i));
    }

    private setFilterFieldToFilter(buyersQuery: BuyersQuery, filterItem: FilterItem): void {
        const value = filterItem?.values?.length > 1 ? filterItem.values.map(itemValue => itemValue.value) : filterItem?.values?.[0]?.value;
        switch (filterItem.key) {
            case bfe.entity.key:
                buyersQuery.entity_id = Number(value);
                break;
            case bfe.sort.key:
                buyersQuery.sort = this.filterStringParse(value);
                break;
            case bfe.pagination.key:
                buyersQuery.limit = (value as PageableFilter).limit;
                buyersQuery.offset = (value as PageableFilter).offset;
                break;
            case bfe.name.key:
                buyersQuery.name = value;
                break;
            case bfe.surname.key:
                buyersQuery.surname = value;
                break;
            case bfe.gender.key:
                buyersQuery.gender = value as BuyerGender;
                break;
            case bfe.type.key:
                buyersQuery.type = value as BuyerType;
                break;
            case bfe.allowComercialMailing.key:
                buyersQuery.allow_commercial_mailing = coerceBooleanProperty(value);
                break;
            case bfe.age.from.key:
                buyersQuery.age = buyersQuery.age || {};
                buyersQuery.age.from = Number(value);
                break;
            case bfe.age.to.key:
                buyersQuery.age = buyersQuery.age || {};
                buyersQuery.age.to = Number(value);
                break;
            case bfe.country.key:
                buyersQuery.country = this.filterStringParse(value);
                break;
            case bfe.countrySubdivision.key:
                buyersQuery.country_subdivision
                    = Array.isArray(value) ? value.map(v => this.filterStringParse(v)) : [this.filterStringParse(value)];
                break;
            case bfe.phone.key:
                buyersQuery.phone = value;
                break;
            case bfe.email.key:
                buyersQuery.email = value;
                break;
            case bfe.subscriptionLists.key:
                buyersQuery.subscription_list_id = this.filterNumberArrayParse(value);
                break;
            case bfe.channels.key:
                buyersQuery.channel_id = this.filterNumberArrayParse(value);
                break;
            case bfe.collectives.key:
                buyersQuery.collective_id = this.filterNumberArrayParse(value);
                break;
            case bfe.orderDate.from.key:
                mergeObjects(buyersQuery, { order: { dates: { purchase: { from: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.orderDate.to.key:
                mergeObjects(buyersQuery, { order: { dates: { purchase: { to: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.firstOrderDate.from.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { first_purchase: { from: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.firstOrderDate.to.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { first_purchase: { to: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.withoutOrdersDate.from.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { without_transactions: { from: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.withoutOrdersDate.to.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { without_transactions: { to: this.filterStringParse(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersPurchased.from.key:
                mergeObjects(buyersQuery, { order: { transactions: { orders_purchased: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersPurchased.to.key:
                mergeObjects(buyersQuery, { order: { transactions: { orders_purchased: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.itemsPurchased.from.key:
                mergeObjects(buyersQuery,
                    { order: { transactions: { order_items_purchased: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.itemsPurchased.to.key:
                mergeObjects(buyersQuery,
                    { order: { transactions: { order_items_purchased: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.itemsRefunded.from.key:
                mergeObjects(buyersQuery,
                    { order: { transactions: { order_items_refunded: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.itemsRefunded.to.key:
                mergeObjects(buyersQuery,
                    { order: { transactions: { order_items_refunded: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.presaleDays.from.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { presale_days: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.presaleDays.to.key:
                mergeObjects(buyersQuery,
                    { order: { dates: { presale_days: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersPurchasedPrice.from.key:
                mergeObjects(buyersQuery, { order: { prices: { orders_purchased: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersPurchasedPrice.to.key:
                mergeObjects(buyersQuery, { order: { prices: { orders_purchased: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersRefundedPrice.from.key:
                mergeObjects(buyersQuery, { order: { prices: { orders_refunded: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ordersRefundedPrice.to.key:
                mergeObjects(buyersQuery, { order: { prices: { orders_refunded: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemAvgPrice.from.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_avg: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemAvgPrice.to.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_avg: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.ticketData.events.key:
                buyersQuery.event_id = this.filterNumberArrayParse(value);
                break;
            case bfe.ticketData.sessions.key:
                buyersQuery.session_id = this.filterNumberArrayParse(value);
                break;
            case bfe.ticketData.promotions.key:
                buyersQuery.event_promotion_id = this.filterNumberArrayParse(value);
                break;
            case bfe.ticketData.sessionDatesFrom.key:
                mergeObjects(buyersQuery, { session_dates: { from: this.filterStringParse(value) } } as BuyersQuery);
                break;
            case bfe.ticketData.sessionDatesTo.key:
                mergeObjects(buyersQuery, { session_dates: { to: this.filterStringParse(value) } } as BuyersQuery);
                break;
            case bfe.orderItemValue.basePriceFrom.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_base_price: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemValue.basePriceTo.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_base_price: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemValue.finalPriceFrom.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_final_price: { from: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemValue.finalPriceTo.key:
                mergeObjects(buyersQuery, { order: { prices: { order_items_final_price: { to: Number(value) } } } } as BuyersQuery);
                break;
            case bfe.orderItemValue.invitations.key:
                mergeObjects(buyersQuery, { order: { prices: { invitations: Boolean(value) } } } as BuyersQuery);
                break;
            case bfe.keyword.key:
                mergeObjects(buyersQuery, { q: this.filterStringParse(value) } as BuyersQuery);
                break;
            case bfe.orderCode.key:
                mergeObjects(buyersQuery, { order: { code: this.filterStringParse(value) } } as BuyersQuery);
                break;
            case bfe.barcode.key:
                mergeObjects(buyersQuery, { order: { barcode: this.filterStringParse(value) } } as BuyersQuery);
                break;
        }
    }

    private filterNumberArrayParse(value: unknown): number[] {
        return Array.isArray(value) ? value.map(v => Number(v)) : [Number(value)];
    }

    private filterStringParse(value: unknown): string {
        return value && String(value).length && String(value) || undefined;
    }
}

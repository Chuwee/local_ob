import { Metadata } from '@OneboxTM/utils-state';
import { UserRoles, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { SubscriptionList } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { BuyersService, BuyersQuery, Buyer, buyersColumnList } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService,
    SortFilterComponent, PaginatorComponent, FilterItem, ExportDialogComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { Observable, Subject } from 'rxjs';
import { filter, take, takeUntil, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-subscription-clients',
    templateUrl: './subscription-clients.component.html',
    styleUrls: ['./subscription-clients.component.scss'],
    providers: [BuyersService, ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionClientsComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    private _tableSrv = inject(TableColConfigService);

    private _onDestroy: Subject<void> = new Subject();
    private _request: BuyersQuery;
    private _sortFilterComponent: SortFilterComponent;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(MatSort) private _matSort: MatSort;
    @Input() currentSubscriptionList$: Observable<SubscriptionList>;
    readonly dateTimeFormats = DateTimeFormats;
    subscriptionListBuyers$: Observable<Buyer[]>;
    subscriptionListBuyersMetadata$: Observable<Metadata>;
    displayedColumns = ['email', 'name', 'surname', 'gender', 'date.create', 'identityCard.type', 'identityCard.id'];
    buyersPageSize = 5;
    initSortCol = this.displayedColumns[0];
    initSortDir: SortDirection = 'asc';

    constructor(
        private _buyersSrv: BuyersService,
        private _matDialog: MatDialog,
        private _ephemeralSrv: EphemeralMessageService,
        private _authService: AuthenticationService
    ) {
        super();
    }

    override ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._buyersSrv.clearBuyersList();
    }

    ngOnInit(): void {
        this.currentSubscriptionList$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(subscriptionList => {
                this._request = {
                    entity_id: subscriptionList.entity.id,
                    subscription_list_id: [subscriptionList.id],
                    limit: this.buyersPageSize,
                    offset: 0,
                    sort: 'email:asc'
                };
                this.loadBuyers();
            });

        this.subscriptionListBuyers$ = this._buyersSrv.getBuyersData$()
            .pipe(
                filter(buyer => !!buyer)
            );
        this.subscriptionListBuyersMetadata$ = this._buyersSrv.getBuyersMetadata$();
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent
        ]);
    }

    loadBuyers(): void {
        this._buyersSrv.loadBuyers(this._request);
    }

    loadData(filters: FilterItem[]): void {
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values?.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        break;
                }
            }
        });
        this.loadBuyers();
    }

    exportBuyers(): void {
        this._matDialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: buyersColumnList,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_SUBSCRIPTION_CLIENTS')
        }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.currentSubscriptionList$
                    .pipe(
                        take(1),
                        withLatestFrom(this._authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]))
                    )
                    .subscribe(([subscriptionList, isOperator]) => {
                        this._tableSrv.setColumns('EXP_SUBSCRIPTION_CLIENTS', exportList.fields.map(resultData => resultData.field));
                        const filters: BuyersQuery = { subscription_list_id: [subscriptionList.id] };
                        if (isOperator) {
                            filters.entity_id = subscriptionList.entity.id;
                        }
                        this._buyersSrv.exportBuyers(filters, exportList)
                            .subscribe(result => {
                                if (result) {
                                    this._ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                                }
                            });

                    });
            });
    }

}

import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    GetSalesRequestsRequest, SalesListElementModel,
    SalesRequestsStatus, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    PaginatorComponent, SortFilterComponent, ListFiltersService, ListFilteredComponent, SearchInputComponent,
    FilterItem
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { MatSort, SortDirection } from '@angular/material/sort';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { SalesRequestsListFilterComponent } from './filter/sales-requests-list-filter.component';

@Component({
    selector: 'app-sales-requests-list',
    templateUrl: './sales-requests-list.component.html',
    styleUrls: ['./sales-requests-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})

export class SalesRequestsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    #sortFilterComponent: SortFilterComponent;
    #request: GetSalesRequestsRequest;

    readonly #salesRequestSrv = inject(SalesRequestsService);
    readonly #auth = inject(AuthenticationService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ref = inject(ChangeDetectorRef);

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(SalesRequestsListFilterComponent) private _filterComponent: SalesRequestsListFilterComponent;

    dateTimeFormats = DateTimeFormats;
    salesRequestsStatus = SalesRequestsStatus;
    salesRequests$: Observable<SalesListElementModel[]>;
    displayedColumns = ['date', 'event_name', 'channel_name', 'event_entity_name', 'city', 'venue', 'status', 'actions'];
    initSortCol = 'date';
    initSortDir: SortDirection = 'desc';
    channelsPageSize = 20;
    salesRequestsMetadata$: Observable<Metadata>;
    salesRequestsLoading$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    updateStatus: (id: number, status: SalesRequestsStatus) => Observable<{ status: SalesRequestsStatus }>;

    readonly canReadMultipleEntities$ = this.#auth.canReadMultipleEntities$();

    trackByFn = (_: number, item: SalesListElementModel): number => item.id;

    ngOnInit(): void {
        this.salesRequestsMetadata$ = this.#salesRequestSrv.getSalesRequestsListMetadata$();
        this.salesRequestsLoading$ = this.#salesRequestSrv.isSalesRequestsListLoading$();
        this.salesRequests$ = this.#salesRequestSrv.getSalesRequestsListData$()
            .pipe(
                filter(salesRequests => !!salesRequests),
                map(salesRequests => salesRequests.map(salesRequest => new SalesListElementModel(salesRequest)))
            );

        this.updateStatus = (id, status) => this.#salesRequestSrv.updateSaleRequestStatus(id, status);
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: 20,
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
                    case 'CHANNEL_ENTITY':
                        this.#request.channelEntity = values[0].value;
                        break;
                    case 'EVENT_ENTITY':
                        this.#request.eventEntity = values[0].value;
                        break;
                    case 'CHANNEL':
                        this.#request.channel = values[0].value;
                        break;
                    case 'START_DATE':
                        this.#request.startDate = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#request.endDate = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                }
            }
        });

        this.loadSalesRequests();
    }

    private loadSalesRequests(): void {
        this.#salesRequestSrv.loadSalesRequestsList(this.#request);
        this.#ref.detectChanges();
    }
}

import { GetPacksSaleRequestsReq, PackSaleRequest, PackSaleRequestStatus, packSaleRequestStatus, PacksSaleRequestsService } from '@admin-clients/cpanel-channels-packs-sale-requests-data-access';
import { ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, EmptyStateComponent, FilterItem, ListFilteredComponent, ListFiltersService, PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent, StatusSelectComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { map, Observable } from 'rxjs';
import { PacksSaleRequestsListFilterComponent } from './filter/packs-sale-requests-list-filter.component';

@Component({
    selector: 'app-packs-sale-requests-list',
    imports: [
        TranslatePipe, AsyncPipe, /* RouterLink, */ NgClass, DateTimePipe,
        MatDivider, MatProgressSpinner, MatTable, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
        MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, MatColumnDef, MatSort, MatSortHeader,
        PaginatorComponent, SearchInputComponent, PopoverFilterDirective, PopoverComponent, ChipsComponent, ChipsFilterDirective,
        StatusSelectComponent, ContextNotificationComponent, EmptyStateComponent, PacksSaleRequestsListFilterComponent
    ],
    providers: [ListFiltersService],
    templateUrl: './packs-sale-requests-list.component.html',
    styleUrls: ['./packs-sale-requests-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PacksSaleRequestsListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #packsSalesRequestSrv = inject(PacksSaleRequestsService);

    #sortFilterComponent: SortFilterComponent;
    #request: GetPacksSaleRequestsReq;

    readonly matSort = viewChild(MatSort);
    readonly paginatorComponent = viewChild(PaginatorComponent);
    readonly searchInputComponent = viewChild(SearchInputComponent);
    readonly filterComponent = viewChild(PacksSaleRequestsListFilterComponent);

    readonly packsSaleRequests$ = this.#packsSalesRequestSrv.packsSaleRequestsList.getData$();
    readonly packsSaleRequestsMetadata$ = this.#packsSalesRequestSrv.packsSaleRequestsList.getMetadata$();
    readonly displayedColumns = ['request_date', 'pack_name', 'channel_name', 'pack_entity_name', 'status'];
    readonly pageSize = 20;
    readonly initSortCol = 'request_date';
    readonly initSortDir = 'desc';
    readonly dateTimeFormats = DateTimeFormats;
    readonly packSaleRequestsStatus = packSaleRequestStatus;

    readonly isLoading$ = booleanOrMerge([
        this.#packsSalesRequestSrv.packsSaleRequestsList.loading$(),
        this.#packsSalesRequestSrv.packSaleRequest.status.inProgress$()
    ]);

    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    hasAppliedFilters = false;

    trackByFn = (_, packSaleRequest: PackSaleRequest): number => packSaleRequest.id;

    updateStatus: (id: number, status: PackSaleRequestStatus) => Observable<{ status: PackSaleRequestStatus }> = (id, status) =>
        this.#packsSalesRequestSrv.packSaleRequest.status.update(id, status);

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this.matSort());
        this.initListFilteredComponent([
            this.paginatorComponent(),
            this.#sortFilterComponent,
            this.searchInputComponent(),
            this.filterComponent()
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.hasAppliedFilters = false;
        this.#request = {
            limit: 20,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                this.hasAppliedFilters = true;
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        this.hasAppliedFilters = false;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        this.hasAppliedFilters = false;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
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
        this.#packsSalesRequestSrv.packsSaleRequestsList.load(this.#request);
    }
}

import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    DeliveryPoint, DeliveryPointStatus,
    GetProductsDeliveryPointsRequest, ProductsDeliveryPointsService
} from '@admin-clients/cpanel/products/delivery-points/data-access';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import {
    ChipsComponent, ChipsFilterDirective,
    ContextNotificationComponent, DialogSize, EmptyStateComponent,
    EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService,
    MessageDialogService, ObMatDialogConfig, PaginatorComponent, PopoverComponent,
    PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, map, shareReplay, switchMap } from 'rxjs';
import { NewDeliveryPointDialogComponent } from '../create/new-delivery-point-dialog.component';
import { ProductsDeliveryPointsListFilterComponent } from './filter/products-delivery-points-list-filter.component';

@Component({
    selector: 'app-products-delivery-points-list',
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        ContextNotificationComponent,
        PopoverFilterDirective,
        PopoverComponent,
        ChipsComponent,
        ChipsFilterDirective,
        EmptyStateComponent,
        SearchInputComponent,
        PaginatorComponent,
        RouterModule,
        ProductsDeliveryPointsListFilterComponent
    ],
    providers: [
        ListFiltersService
    ],
    templateUrl: './products-delivery-points-list.component.html',
    styleUrls: ['./products-delivery-points-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsDeliveryPointsListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(ProductsDeliveryPointsListFilterComponent) private _filterComponent: ProductsDeliveryPointsListFilterComponent;

    private readonly _auth = inject(AuthenticationService);
    private readonly _deliveryPointsSrv = inject(ProductsDeliveryPointsService);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _countriesService = inject(CountriesService);
    private readonly _regionsService = inject(RegionsService);
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);

    private _sortFilterComponent: SortFilterComponent;
    private _request = new GetProductsDeliveryPointsRequest();

    readonly canReadMultipleEntities$ = this._auth.canReadMultipleEntities$();
    readonly deliveryPointsMetadata$ = this._deliveryPointsSrv.productsDeliveryPointsList.getMetadata$();
    readonly deliveryPointsLoading$ = this._deliveryPointsSrv.productsDeliveryPointsList.loading$();
    readonly deliveryPoints$ = this._deliveryPointsSrv.productsDeliveryPointsList.getData$()
        .pipe(shareReplay({ refCount: true, bufferSize: 1 }));

    readonly displayedColumns = ['name', 'entity_name', 'country', 'country_subdivision', 'status', 'actions'];

    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    productsPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    hasAppliedFilters = false;

    trackByFn = (_: number, item: DeliveryPoint): number => item.id;

    ngAfterViewInit(): void {
        this._countriesService.loadCountries();
        this._regionsService.loadRegions();
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._countriesService.clearCountries();
        this._regionsService.clearRegions();
    }

    openNewDeliveryPointDialog(): void {
        this._matDialog.open(NewDeliveryPointDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(deliveryPointId => {
                if (deliveryPointId) {
                    this._router.navigate([deliveryPointId], { relativeTo: this._route });
                }
            });
    }

    openDeleteDeliveryPointDialog(deliveryPoint: DeliveryPoint): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_DELIVERY_POINT',
            message: 'DELIVERY_POINT.DELETE_PRODUCT_WARNING',
            messageParams: { deliveryPointName: deliveryPoint.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).pipe(
            filter(Boolean),
            switchMap(() => this._deliveryPointsSrv.deliveryPoint.delete(deliveryPoint.id))
        ).subscribe(() => {
            this._ephemeralMsg.showSuccess({
                msgKey: 'DELIVERY_POINT.DELETE_SUCCESS',
                msgParams: { deliveryPointName: deliveryPoint.name }
            });
            this.loadDeliveryPoints();
        });
    }

    loadData(filters: FilterItem[]): void {
        this.hasAppliedFilters = false;
        this._request = new GetProductsDeliveryPointsRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                this.hasAppliedFilters = true;
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        this.hasAppliedFilters = false;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        this.hasAppliedFilters = false;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entityId = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values.map(val => val.value).join(',') as DeliveryPointStatus;
                        break;
                    case 'COUNTRY':
                        this._request.country = values[0].value;
                        break;
                    case 'COUNTRY_SUBDIVISION':
                        this._request.countrySubdivision = values[0].value;
                        break;
                }
            }
        });

        this.loadDeliveryPoints();
    }

    private loadDeliveryPoints(): void {
        this._deliveryPointsSrv.productsDeliveryPointsList.load(this._request);
    }

}

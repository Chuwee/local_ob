import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetProductsRequest, Product, ProductStatus, ProductStockType, ProductType, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent,
    EmptyStateComponent, PopoverComponent, PopoverFilterDirective, ObMatDialogConfig,
    SearchInputComponent, PaginatorComponent, ListFiltersService, SortFilterComponent,
    ListFilteredComponent, FilterItem, DialogSize, EphemeralMessageService, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, first, map, shareReplay, switchMap } from 'rxjs';
import { NewProductDialogComponent } from '../create/new-product-dialog.component';
import { ProductsListFilterComponent } from './filter/products-list-filter.component';

@Component({
    selector: 'app-products-list',
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
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
        ProductsListFilterComponent,
        LocalCurrencyPartialTranslationPipe
    ],
    providers: [
        ListFiltersService
    ],
    templateUrl: './products-list.component.html',
    styleUrls: ['./products-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(ProductsListFilterComponent) private _filterComponent: ProductsListFilterComponent;

    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #productsService = inject(ProductsService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);

    #sortFilterComponent: SortFilterComponent;
    #request = new GetProductsRequest();

    readonly canReadMultipleEntities$ = this.#auth.canReadMultipleEntities$();
    readonly productsMetadata$ = this.#productsService.productsList.getMetadata$();
    readonly productsLoading$ = this.#productsService.productsList.loading$();
    readonly products$ = this.#productsService.productsList.getData$().pipe(shareReplay({ refCount: true, bufferSize: 1 }));
    readonly canWrite$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]).pipe(
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly displayedColumns$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencyCodes),
            switchMap(currencies => this.canWrite$
                .pipe(
                    map(canWrite => {
                        const columns = ['name', 'entity_name', 'stock_type', 'status', 'product_type'];
                        currencies?.length > 1 && columns.push('currency');
                        canWrite && columns.push('actions');
                        return columns;
                    })
                )
            )
        );

    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    productsPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    hasAppliedFilters = false;

    trackByFn = (_, product: Product): number => product.product_id;

    ngOnInit(): void {
        // Set default filters
        const urlParameters = Object.assign({}, this.#route.snapshot.queryParams);
        if (!urlParameters['status']) {
            urlParameters['status'] = ProductStatus.active;
            this.#router.navigate(['.'], { relativeTo: this.#route, queryParams: urlParameters });
        }
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    openNewProductDialog(): void {
        this.#matDialog.open(NewProductDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(productId => {
                if (productId) {
                    this.#router.navigate([productId, 'general-data'], { relativeTo: this.#route });
                }
            });
    }

    openDeleteProductDialog(product: Product): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_PRODUCT',
            message: 'PRODUCT.DELETE_PRODUCT_WARNING',
            messageParams: { productName: product.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).pipe(
            filter(Boolean),
            switchMap(() => this.#productsService.product.delete(product.product_id))
        ).subscribe(() => {
            this.#ephemeralMsg.showSuccess({
                msgKey: 'PRODUCT.DELETE_PRODUCT_SUCCESS',
                msgParams: { productName: product.name }
            });
            this.loadProducts();
        });
    }

    loadData(filters: FilterItem[]): void {
        this.hasAppliedFilters = false;
        this.#request = new GetProductsRequest();
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
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values[0].value;
                        break;
                    case 'STOCK':
                        this.#request.stock = values.map(val => val.value).join(',') as ProductStockType;
                        break;
                    case 'TYPE':
                        this.#request.type = values.map(val => val.value).join(',') as ProductType;
                        break;
                    case 'CURRENCY':
                        this.#request.currency = values[0].value;
                        break;
                }
            }
        });

        this.loadProducts();
    }

    private loadProducts(): void {
        this.#productsService.productsList.load(this.#request);
    }

}

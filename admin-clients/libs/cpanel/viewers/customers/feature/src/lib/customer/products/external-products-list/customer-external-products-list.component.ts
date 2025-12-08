import { Metadata } from '@OneboxTM/utils-state';
import { CustomersService, Customer, CustomerProductsService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EmptyStateTinyComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { first, map, take, takeUntil } from 'rxjs/operators';
import { CustomerExternalProductListItem, CustomerProductSeatType } from '../models/customer-external-products.model';

@Component({
    selector: 'app-customer-external-products-list',
    imports: [TranslatePipe, FlexLayoutModule, MatSortModule, MatTableModule,
        AsyncPipe, MatPaginatorModule, LocalDateTimePipe, EmptyStateTinyComponent
    ],
    templateUrl: './customer-external-products-list.component.html',
    styleUrls: ['./customer-external-products-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerExternalProductsListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject();
    private _productsPageSize = 20;
    private _customer: Customer;
    private _products: CustomerExternalProductListItem[];
    private _paginatedProducts: CustomerExternalProductListItem[];
    private _paginatedProductsBS = new BehaviorSubject<CustomerExternalProductListItem[]>(null);
    @ViewChild(MatPaginator, { static: true }) private _matPaginator: MatPaginator;

    displayedColumns = ['event_name', 'type', 'seat', 'rate', 'purchase_date'];
    dateTimeFormats = DateTimeFormats;
    paginatedProducts$: Observable<CustomerExternalProductListItem[]>;
    productsMetadata$: Observable<Metadata>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _customersSrv: CustomersService,
        private _customerProductsSrv: CustomerProductsService
    ) {
    }

    ngOnInit(): void {
        this.setCustomer();
        this.setProducts();
        this.initMatPaginator();
        this.tableChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getLocationInfo(externalProduct: CustomerExternalProductListItem): string {
        if (externalProduct.seat_type === CustomerProductSeatType.numbered) {
            return `${externalProduct.sector_name} | ${externalProduct.row_name} | ${externalProduct.seat_name}`;
        } else if (externalProduct.seat_type === CustomerProductSeatType.notNumbered) {
            return `${externalProduct.sector_name} | ${externalProduct.not_numbered_zone_name}`;
        }
        return '-';
    }

    private setCustomer(): void {
        this._customersSrv.customer.get$()
            .pipe(take(1))
            .subscribe(customer => {
                this._customer = customer;
            });
    }

    private setProducts(): void {
        this.paginatedProducts$ = this._paginatedProductsBS.asObservable();
        this.productsMetadata$ = this._customerProductsSrv.getExternalProductsListMetadata$();

        this._customerProductsSrv.getExternalProductsListData$()
            .pipe(first(value => !!value))
            .subscribe(products => {
                this._products = products;
                this.setPaginatedProducts();
            });

        this.loadProducts();
    }

    private loadProducts(): void {
        this._customerProductsSrv.loadExternalProductsList(
            this._customer.id,
            this._customer.entity?.id?.toString()
        );
    }

    private initMatPaginator(): void {
        this._customerProductsSrv.getExternalProductsListMetadata$()
            .pipe(first(value => !!value))
            .subscribe(metadata => {
                this._matPaginator.pageIndex = 0;
                this._matPaginator.pageSize = this._productsPageSize;
                this._matPaginator.length = metadata.total;
            });
    }

    private setPaginatedProducts(): void {
        this._paginatedProducts = this._products.slice(
            this._matPaginator.pageIndex * this._matPaginator.pageSize,
            this._matPaginator.pageIndex * this._matPaginator.pageSize + this._matPaginator.pageSize
        );
        this._paginatedProductsBS.next(this._paginatedProducts);
    }

    private tableChangeHandler(): void {
        this._matPaginator.page
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this.setPaginatedProducts();
            });
    }
}

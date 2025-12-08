import { Metadata } from '@OneboxTM/utils-state';
import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { CustomersService, CustomerStatus, Customer, CustomerProductsService, CustomerProductsFilters }
    from '@admin-clients/cpanel-viewers-customers-data-access';
import { TicketDetailState } from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent } from '@admin-clients/shared/common/ui/components';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { CustomerProductListItem, CustomerProductStatus } from '../models/customer-product.model';

@Component({
    selector: 'app-customer-products-list',
    imports: [TranslatePipe, FlexLayoutModule, MatSortModule, MatTableModule, LocalCurrencyPipe,
        AsyncPipe, MatPaginatorModule, NgClass, EmptyStateTinyComponent
    ],
    templateUrl: './customer-products-list.component.html',
    styleUrls: ['./customer-products-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [CustomerProductsService]
})
export class CustomerProductsListComponent implements OnInit, OnDestroy {

    #customerProductsSrv = inject(CustomerProductsService);
    #customersSrv = inject(CustomersService);

    #productsRequest: CustomerProductsFilters;
    #customer: Customer;
    #customerLockUnlockStatus: CustomerStatus;
    #products: CustomerProductListItem[];

    readonly #onDestroy = inject(DestroyRef);
    readonly #productsWithStatus = new BehaviorSubject<CustomerProductListItem[]>(null);
    readonly #eventInactiveStatus = [EventStatus.cancelled, EventStatus.notAccomplished, EventStatus.finished];

    @ViewChild(MatPaginator, { static: true })
    private readonly _matPaginator: MatPaginator;

    @ViewChild(MatSort, { static: true })
    private readonly _martSort: MatSort;

    readonly $productType = input.required<'SEAT' | 'PRODUCT'>({ alias: 'tableType' });

    readonly pageSize = 20;
    readonly productsMetadata$ = this.#customerProductsSrv.getProductsListMetadata$();
    readonly productsWithStatus$ = this.#productsWithStatus.asObservable();
    displayedColumns;
    initSortCol;
    readonly initSortDir: SortDirection = 'asc';

    ngOnInit(): void {
        this.displayedColumns = this.$productType() === 'SEAT' ?
            ['event_name', 'session_name', 'entity', 'type', 'seat', 'price', 'status']
            : ['product_name', 'variant', 'entity', 'price', 'status'];
        this.initSortCol = this.displayedColumns[0];
        this.setCustomer();
        this.setProducts();
        this.productsChangeHandler();
        this.lockChangeHandler();
        this.tableChangeHandler();
    }

    ngOnDestroy(): void {
        this.#customersSrv.customerLockUnLock.clear();
    }

    private setCustomer(): void {
        this.#customersSrv.customer.get$()
            .pipe(take(1))
            .subscribe(customer => {
                this.#customer = customer;
            });
    }

    private setProducts(): void {

        this.#productsRequest = {
            limit: this.pageSize,
            offset: 0,
            product_type: this.$productType()
        };

        if (this.$productType() === 'SEAT') {
            this.#productsRequest.sort = `${this.initSortCol}:${this.initSortDir}`;
        }

        this.loadProducts();
    }

    private loadProducts(): void {
        this.#customerProductsSrv.loadProductsList(
            this.#customer.id,
            this.#customer.entity?.id?.toString(),
            this.#productsRequest
        );
    }

    private productsChangeHandler(): void {
        this.#customerProductsSrv.getProductsListData$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(products => {
                this.#products = products;
                let productsWithStatus: CustomerProductListItem[];
                if (this.#customerLockUnlockStatus) {
                    productsWithStatus = this.#products
                        .map(product => this.modifyProductStatus(product, this.#customerLockUnlockStatus));
                } else {
                    productsWithStatus = this.#products
                        .map(product => this.initialModifyProductStatus(product, this.#customer.status));
                }
                this.#productsWithStatus.next(productsWithStatus);
            });

        this.#customerProductsSrv.getProductsListMetadata$()
            .pipe(
                filter(value => !!value),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(metadata => {
                this.setMatPaginator(metadata);
            });
    }

    private setMatPaginator(metadata: Metadata): void {
        this._matPaginator.pageIndex = Math.floor(metadata?.offset / metadata.limit);
        this._matPaginator.pageSize = metadata.limit;
        this._matPaginator.length = metadata.total;
    }

    private modifyProductStatus(product: CustomerProductListItem, customerStatus: CustomerStatus): CustomerProductListItem {
        return this.modifyProductStatusByLockOrUnlock(
            product.status === TicketDetailState.refunded ||
            product.status === TicketDetailState.locked ||
            product.status === TicketDetailState.regenerated ||
            this.#eventInactiveStatus.includes(product?.event?.status),
            product,
            customerStatus
        );
    }

    private initialModifyProductStatus(product: CustomerProductListItem, customerStatus: CustomerStatus): CustomerProductListItem {
        return this.modifyProductStatusByLockOrUnlock(
            product.status === TicketDetailState.refunded ||
            product.status === TicketDetailState.regenerated ||
            product.status === TicketDetailState.locked ||
            this.#eventInactiveStatus.includes(product?.event?.status) ||
            product.status === TicketDetailState.partialRegenerated,
            product,
            customerStatus
        );
    }

    private modifyProductStatusByLockOrUnlock(
        productCondition: boolean,
        product: CustomerProductListItem,
        customerStatus: CustomerStatus
    ): CustomerProductListItem {
        if (productCondition) {
            return product;
        } else if (customerStatus === CustomerStatus.active) {
            return { ...product, product_status: CustomerProductStatus.active };
        } else if (customerStatus === CustomerStatus.locked) {
            return { ...product, product_status: CustomerProductStatus.locked };
        } else {
            return product;
        }
    }

    private lockChangeHandler(): void {
        this.#customersSrv.customerLockUnLock.get$()
            .pipe(
                filter(value => !!value),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(customerStatus => {
                this.#customerLockUnlockStatus = customerStatus;
                const productsWithStatus = this.#products
                    .map(product => this.modifyProductStatus(product, this.#customerLockUnlockStatus));
                this.#productsWithStatus.next(productsWithStatus);
            });
    }

    private tableChangeHandler(): void {
        this._martSort.sortChange
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(sortChange => {
                this.#productsRequest = {
                    ...this.#productsRequest,
                    sort: `${sortChange.active}:${sortChange.direction}`
                };
                this._matPaginator.firstPage();
                this.changePage();
            });

        this._matPaginator.page
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                this.changePage();
            });
    }

    private changePage(): void {
        const offset = this._matPaginator.pageSize * this._matPaginator.pageIndex;
        this.#productsRequest = {
            ...this.#productsRequest,
            offset
        };
        this.loadProducts();
    }
}

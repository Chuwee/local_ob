import { Metadata } from '@OneboxTM/utils-state';
import { BuyerOrderItem, BuyerOrderItemType, BuyersService, GetBuyerOrderItemsRequest } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { EmptyStateTinyComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalCurrencyPipe, DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, OnInit, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-order-items-list',
    templateUrl: './buyer-order-items-list.component.html',
    styleUrls: ['./buyer-order-items-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [BuyersService],
    imports: [TranslatePipe, FlexLayoutModule, MatSortModule, MatTableModule, LocalCurrencyPipe,
        AsyncPipe, MatPaginatorModule, NgClass, EmptyStateTinyComponent, DateTimePipe, MatProgressSpinnerModule,
        RouterLink
    ]
})
export class BuyerOrderItemsListComponent implements OnInit, OnDestroy {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #buyersSrv = inject(BuyersService);

    readonly $productType = input.required<BuyerOrderItemType>({ alias: 'tableType' });

    readonly PAGE_SIZE = 10;
    readonly dataTimeFormat = DateTimeFormats.shortDateTime;
    readonly initSortDir: SortDirection = 'asc';
    displayedColumns: string[];
    initSortCol: string;

    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    orderItemList$: Observable<BuyerOrderItem[]>;
    orderItemListMetadata$: Observable<Metadata>;

    #$buyerId = toSignal(this.#buyersSrv.getBuyer$().pipe(
        first(Boolean),
        map(buyer => buyer.id))
    );

    ngOnInit(): void {
        this.displayedColumns = this.$productType() === BuyerOrderItemType.seat ?
            ['orderCode', 'orderDate', 'event_name', 'session_name', 'type', 'base_price', 'final_price', 'status']
            : ['orderCode', 'orderDate', 'product_name', 'variant', 'base_price', 'final_price', 'status'];
        this.initSortCol = this.displayedColumns[1];

        this.loadData();

        this.orderItemList$ = this.$productType() === BuyerOrderItemType.seat
            ? this.#buyersSrv.orderItems.getSeatData$().pipe(filter(Boolean))
            : this.#buyersSrv.orderItems.getProductData$().pipe(filter(Boolean));

        this.orderItemListMetadata$ = this.$productType() === BuyerOrderItemType.seat
            ? this.#buyersSrv.orderItems.getSeatMetadata$().pipe(filter(Boolean))
            : this.#buyersSrv.orderItems.getProductMetadata$().pipe(filter(Boolean));
    }

    ngOnDestroy(): void {
        if (this.$productType() === BuyerOrderItemType.seat) {
            this.#buyersSrv.orderItems.clearSeat();
        } else {
            this.#buyersSrv.orderItems.clearProduct();
        }
    }

    loadData(pageOptions?: PageEvent): void {

        const request: GetBuyerOrderItemsRequest = {
            limit: this.PAGE_SIZE,
            offset: pageOptions ? this.PAGE_SIZE * pageOptions?.pageIndex : 0,
            sort: this.initSortCol,
            product_type: this.$productType()
        };

        this.#buyersSrv.orderItems.load(this.#$buyerId(), request);
    }
}

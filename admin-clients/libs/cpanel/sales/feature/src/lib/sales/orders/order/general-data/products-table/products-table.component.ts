import { EventType, OrderItem } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ObfuscatePattern } from '@admin-clients/shared/utility/pipes';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-order-details-products-table',
    templateUrl: './products-table.component.html',
    styleUrls: ['./products-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expand', [
            state('void', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
            state('*', style({ height: '*', visibility: 'visible' })),
            transition('void <=> *', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ],
    standalone: false
})
export class OrderDetailsProductsTableComponent {
    private _router = inject(Router);

    readonly $products = input.required<OrderItem[]>({ alias: 'products' });
    readonly $selection = input.required<SelectionModel<OrderItem>>({ alias: 'selection' });
    readonly $canHaveValidations = input.required<boolean>({ alias: 'canHaveValidations' });
    readonly $isAllowedPartialRefund = input.required<boolean>({ alias: 'isAllowedPartialRefund' });
    readonly $orderCode = input.required<string>({ alias: 'orderCode' });

    readonly obfuscatePattern = ObfuscatePattern;
    readonly eventType = EventType;
    readonly dateTimeFormats = DateTimeFormats;

    productsColumns = [
        'selection',
        'product',
        'variant',
        'barcode',
        'price',
        'print',
        'validation',
        'status',
        'icons'
    ];

    // selects or deselects a product
    toggleProduct(product: OrderItem): void {
        if (this.$selection() && product.action?.refund || product.action?.partial_refund) this.$selection().toggle(product);
    }

    // selects or deselects all items
    toggleAllProducts(checked: boolean): void {
        if (!checked) {
            this.$selection().clear();
        } else {
            this.$products()
                .filter(product => !!product.action?.refund)
                .forEach(product => this.$selection().select(product));
        }
    }

    isProductSelected(product: OrderItem): boolean {
        return this.$selection()?.isSelected(product);
    }

    areAllProductsSelected(): boolean {
        if (this.$selection().hasValue()) {
            const numSelected = this.$selection().selected.length;
            const numRows = this.$products().filter(product => !!product.action?.refund).length;
            return numSelected === numRows;
        }
        return false;
    }

    areSomeProductsSelected(): boolean {
        return this.$selection().hasValue() && !this.areAllProductsSelected();
    }

    canRefundAnyProduct(): boolean {
        return this.$products().some(product => product.action?.partial_refund || product.action?.refund);
    }

    goToOrder(orderCode: string): void {
        this._router.navigate(['/transactions', orderCode]);
    }

    goToTicket(event: Event, product: OrderItem): void {
        event.preventDefault();
        // prevent click on borders of row
        if (product.id && !(event.target as HTMLElement).classList.contains('mat-row')) {
            this._router.navigate(['/transactions', this.$orderCode(), 'products', product.id]);
        }
    }

    isProductNotValidated(product: OrderItem): boolean {
        const validations = product?.product?.validations;
        return validations == null || validations.length === 0;
    }
}

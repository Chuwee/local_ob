import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    OrderDetail,
    OrdersService,
    PaymentData, PaymentType, RefundAmount, RefundRequest,
    RefundType
} from '@admin-clients/cpanel-sales-data-access';
import { OrderItemType, OrderSubItem, Price, TicketDetailState } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { cloneObject } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

export type RefundOrderDialogData = { items: RefundRequest['items']; totalItems: number };

@Component({
    selector: 'app-refund-order-dialog',
    templateUrl: './refund-order-dialog.component.html',
    styleUrls: ['./refund-order-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class RefundOrderDialogComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #dialogRef = inject(MatDialogRef<RefundOrderDialogComponent>);
    readonly #ordersService = inject(OrdersService);
    readonly #elemRef = inject(ElementRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #data = inject<RefundOrderDialogData>(MAT_DIALOG_DATA);
    readonly #fb = inject(UntypedFormBuilder);

    #orderDetail: OrderDetail;
    #orderCode: string;
    #items: RefundRequest['items'] = this.#data.items;
    #hasMixedVoucherPayment: boolean;

    readonly refundType = RefundType.refund;
    readonly reimbursementType = RefundType.reimbursement;
    readonly voucherType = RefundType.voucher;

    isAllowedExternalReimbursement = false;
    isAllowedReimbursement = false;
    isAllowedVoucherReimbursement = false;
    payments: PaymentData[];
    currency: string;
    amount: RefundAmount;
    type: RefundType;
    refunding$: Observable<boolean>;
    reimbursementKO = false;
    totalItems: number = this.#data.totalItems;
    selectedItems: number = this.#items.length;
    form: UntypedFormGroup = this.#fb.group({
        surcharges: { value: false, disabled: true },
        insurance: { value: false, disabled: true },
        delivery: { value: false, disabled: true },
        gateway: { value: false, disabled: true }
    });

    ngOnInit(): void {
        this.setInitialDialogConfig();
        this.refunding$ = this.#ordersService.isRefundOrderLoading$();
        this.#ordersService.getOrderDetail$().pipe(
            filter(value => value !== null),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(orderDetail => {
            this.#orderDetail = orderDetail;
            this.#orderCode = orderDetail.code;
            this.isAllowedReimbursement = this.allowedReimbursement(orderDetail);
            this.isAllowedExternalReimbursement = this.allowedExternalReimbursement(orderDetail);
            this.isAllowedVoucherReimbursement = orderDetail.action.voucher_refund;
            this.type = this.isAllowedReimbursement || this.isAllowedExternalReimbursement
                ? this.reimbursementType
                : this.refundType;

            this.payments = orderDetail.payment_data;
            this.currency = orderDetail.price.currency;
            this.prepareAmounts(orderDetail);

            if (this.payments.length > 1) {
                if (this.payments.filter(payment => payment.type === PaymentType.voucher)) {
                    this.#hasMixedVoucherPayment = true;
                    this.isAllowedReimbursement = this.#items.length === orderDetail.items.length;
                    if (this.isAllowedReimbursement) {
                        this.disableRefundFlags();
                    } else {
                        this.type = this.refundType;
                    }
                }
                const paymentsControls = this.#fb.array([]);
                this.payments.forEach(payment => {
                    paymentsControls.push(this.#fb.group({
                        [payment.type]: [payment.value, [Validators.required, Validators.min(0)]]
                    }));
                });
                this.form.setControl('payments', paymentsControls);
                this.form.setValidators(this.setupPaymentsValidators());
                if (this.type !== this.refundType) {
                    this.form.get('payments').disable();
                }
            }
        });

        this.checkAndManageExternalReimbursement();

        this.form.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.calculateToRefundAmount());
    }

    close(refunded = false): void {
        this.#dialogRef.close({ reimbursementKO: this.reimbursementKO, refunded });
    }

    updateType(type: RefundType): void {
        this.type = type;
        if (this.payments.length > 1) {
            if (type === this.refundType) {
                this.form.get('payments').enable();
                this.enableRefundFlags();
            } else if (type === this.reimbursementType) {
                if (this.#hasMixedVoucherPayment) {
                    this.disableRefundFlags();
                }
                this.disableAndRestorePayments();
            } else if (type === this.voucherType) {
                this.enableRefundFlags();
                this.disableAndRestorePayments();
            }
        }
    }

    refund(): void {
        let payments = null;
        if (this.type === this.refundType && this.payments.length > 1) {
            const paymentValues = this.form.get('payments').value;
            payments = paymentValues.map(value => ({ type: Object.keys(value)[0], value: Object.values(value)[0] }));
        }
        //Remove subItems if is total refund (if all subitems selected)
        const requestItems = this.#items.map(requestItem => {
            const item = this.#orderDetail.items.find(orderItem => orderItem.id === requestItem.id);
            const result = cloneObject(requestItem);
            if (requestItem.subitem_ids?.length === item.subitems?.length) {
                result.subitem_ids = undefined;
            }
            return result;
        });
        this.#ordersService.refundOrder(this.#orderCode,
            {
                items: requestItems,
                type: this.type,
                include_surcharges: this.form.get('surcharges').value,
                include_delivery: this.form.get('delivery').value,
                include_insurance: this.form.get('insurance').value,
                include_gateway: this.form.get('gateway').value,
                payments
            })
            .subscribe(response => {
                if (response.status === 206) {
                    this.reimbursementKO = true;
                    scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
                    return;
                }
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'ACTIONS.REFUND.OK.MESSAGE'
                });
                this.close(true);
            });
    }

    private enableRefundFlags(): void {
        this.form.get('surcharges').enable();
        this.form.get('delivery').enable();
        this.form.get('insurance').enable();
        this.form.get('gateway').enable();
    }

    private disableRefundFlags(): void {
        this.form.get('surcharges').patchValue(true);
        this.form.get('surcharges').disable();
        this.form.get('delivery').patchValue(true);
        this.form.get('delivery').disable();
        this.form.get('insurance').patchValue(true);
        this.form.get('insurance').disable();
        this.form.get('gateway').patchValue(true);
        this.form.get('gateway').disable();
        this.calculateToRefundAmount();
    }

    private disableAndRestorePayments(): void {
        this.form.get('payments').disable();
        this.payments.forEach((payment, i) => {
            this.form.get(['payments', i, payment.type]).setValue(payment.value);
        });
    }

    private allowedReimbursement(orderDetail: OrderDetail): boolean {
        return orderDetail.payment_detail?.reimbursement_constraints?.allowed;
    }

    private allowedExternalReimbursement(orderDetail: OrderDetail): boolean {
        return orderDetail.action.external_reimbursement;
    }

    private prepareAmounts(orderDetail: OrderDetail): void {
        this.amount = {
            tickets: 0.0,
            products: 0.0,
            discounts: 0.0,
            promotions: 0.0,
            orderSales: 0.0,
            totalTickets: 0.0,
            fees: 0.0,
            delivery: orderDetail.price?.delivery || 0.0,
            insurance: orderDetail.price?.insurance || 0.0,
            gateway: orderDetail.price?.gateway || 0.0,
            max: 0.0,
            toRefund: 0.0
        };
        let allSubitemsSelected = true;
        orderDetail.items
            .forEach(item => {
                const found = this.#items.find(elem => elem.id === item.id);
                const purchasedSubitems = item.subitems?.filter(elem => elem.state === TicketDetailState.purchased);
                const selectedSubitems = purchasedSubitems?.filter(subitem => found?.subitem_ids.includes(subitem.id));

                if (found && selectedSubitems?.length === purchasedSubitems?.length) {
                    const price = item.price;
                    if (selectedSubitems && selectedSubitems.length !== item.subitems.length) {
                        this.addSubitems(selectedSubitems);
                    } else {
                        if (item.type === OrderItemType.product) {
                            this.amount.products += price?.base ? price.base : 0;
                        } else {
                            this.amount.tickets += price?.base ? price.base : 0;
                        }
                        this.addSales(price);
                        this.addB2bDiscounts(price);
                    }
                    this.addFees(price);
                } else if (found?.subitem_ids && !!item.subitems) {
                    this.addSubitems(selectedSubitems);
                    allSubitemsSelected = false;
                }
            });

        this.amount.totalTickets = this.amount.tickets + this.amount.products + this.amount.discounts +
            this.amount.promotions + this.amount.orderSales;
        this.amount.totalTickets = this.amount.totalTickets < 0 ? 0 : this.amount.totalTickets;
        this.amount.max = this.amount.totalTickets + this.amount.fees;

        const itemsRefunded = orderDetail.items.filter(item => item.state === TicketDetailState.refunded);
        if (orderDetail.items.length === this.#items.length + itemsRefunded.length && allSubitemsSelected) {
            this.form.get('delivery').enable();
            this.form.get('delivery').patchValue(true);
            this.form.get('insurance').enable();
            this.form.get('insurance').patchValue(true);
            this.form.get('gateway').enable();
            this.form.get('gateway').patchValue(true);
            this.amount.max += this.amount.delivery + this.amount.insurance + this.amount.gateway;
        }
        if (this.amount.fees !== 0) {
            this.form.get('surcharges').enable();
            this.form.get('surcharges').patchValue(true);
        }

        this.calculateToRefundAmount();
    }

    private addSubitems(subitems: OrderSubItem[]): void {
        subitems.forEach(subitem => this.amount.tickets += subitem.price?.final ? subitem.price.final : 0);
    }

    private addB2bDiscounts(price: Partial<Price>): void {
        const b2b = price.b2b;
        if (b2b) {
            this.amount.discounts -= b2b.conditions ? b2b.conditions : 0;
        }
    }

    private addSales(price: Partial<Price>): void {
        const sales = price.sales;
        if (sales) {
            this.amount.discounts -= sales.discount ? sales.discount : 0;
            this.amount.promotions -= sales.promotion ? sales.promotion : 0;
            this.amount.promotions -= sales.automatic ? sales.automatic : 0;
            this.amount.orderSales -= sales.order_automatic ? sales.order_automatic : 0;
            this.amount.orderSales -= sales.order_collective ? sales.order_collective : 0;
        }
    }

    private addFees(price: Partial<Price>): void {
        this.amount.fees += price?.charges?.channel ? price.charges.channel : 0;
        this.amount.fees += price?.charges?.promoter ? price.charges.promoter : 0;
        this.amount.fees += price?.charges?.secondary_market_channel ? price.charges.secondary_market_channel : 0;
        this.amount.fees += price?.charges?.secondary_market_promoter ? price.charges.secondary_market_promoter : 0;
    }

    private calculateToRefundAmount(): void {
        this.amount.toRefund = this.amount.totalTickets;
        if (this.form.get('surcharges').value) {
            this.amount.toRefund += this.amount.fees;
        }
        if (this.form.get('delivery').value) {
            this.amount.toRefund += this.amount.delivery;
        }
        if (this.form.get('insurance').value) {
            this.amount.toRefund += this.amount.insurance;
        }
        if (this.form.get('gateway').value) {
            this.amount.toRefund += this.amount.gateway;
        }
    }

    private setupPaymentsValidators(): ValidatorFn {
        return (_: AbstractControl): ValidationErrors | null => {
            // Lleig pero hem de forçar tenir el valor de refund actualitzat
            if (this.type === this.refundType) {
                this.calculateToRefundAmount();
                const sumPayments = this.form.get('payments').value
                    .map(payment => payment[Object.keys(payment)[0]])
                    .reduce((sum, curr) => sum + curr, 0);
                return sumPayments !== this.amount.toRefund ? { sumPaymentsDoesntMatch: this.amount.toRefund } : null;
            }
            return null;
        };
    }

    private setInitialDialogConfig(): void {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#dialogRef.disableClose = true;
    }

    private checkAndManageExternalReimbursement(): void {
        if (this.isAllowedExternalReimbursement) {
            this.disableRefundFlags();
            const notRefundedItemsIds = this.#orderDetail.items
                .filter(item => item.state !== TicketDetailState.refunded)
                .map(item => item.id);
            const selectedItemsIds = this.#data.items.map(item => item.id);
            // If is partial reimbursement
            if (!notRefundedItemsIds.every(itemId => selectedItemsIds.includes(itemId))) {
                this.form.get('delivery').patchValue(false);
                this.form.get('insurance').patchValue(false);
                this.form.get('gateway').patchValue(false);
                this.calculateToRefundAmount();
            }
        }
    }
}

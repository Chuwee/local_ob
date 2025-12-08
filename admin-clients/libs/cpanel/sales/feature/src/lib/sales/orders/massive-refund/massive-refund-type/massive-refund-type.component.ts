import { ChannelsVouchersService, channelsVouchersProviders } from '@admin-clients/cpanel/channels/vouchers/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PostMassiveRefundOrdersSummaryResponse, RefundAmount, RefundType, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { filter, map, Observable, Subject, take, takeUntil, withLatestFrom } from 'rxjs';

@Component({
    selector: 'app-massive-refund-type',
    templateUrl: './massive-refund-type.component.html',
    styleUrls: ['./massive-refund-type.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [...channelsVouchersProviders],
    standalone: false
})
export class MassiveRefundTypeComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    form: UntypedFormGroup;

    refundType = RefundType;
    refundTypeList = Object.values(this.refundType);
    amount$: Observable<RefundAmount>;
    amountToRefund: RefundAmount['toRefund'];
    disableVouchersRefund$: Observable<boolean>;
    currency: string;
    refundTypeForm: UntypedFormGroup;

    constructor(
        private _ordersService: OrdersService,
        private _channelVouchersService: ChannelsVouchersService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {

        this.refundTypeForm = this.form.get('refundTypeForm') as UntypedFormGroup;
        const channelValue: FilterOption = this.form.get('refundDetailsForm').value.channel;

        if (channelValue) {
            this._channelVouchersService.loadChannelVouchers(Number(channelValue.id));
        }

        this._auth.getLoggedUser$()
            .pipe(take(1), map(user => user.currency))
            .subscribe(currency => this.currency = currency);

        this.disableVouchersRefund$ = this._channelVouchersService.getChannelVouchers$()
            .pipe(
                filter(config => !!config),
                takeUntil(this._onDestroy),
                map(config => !config.allow_refund_to_vouchers)
            );

        this.amount$ = this._ordersService.getMassiveRefundSummary$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy),
                map(refundSummary => this.prepareAmounts(refundSummary))
            );

        this.refundTypeForm.valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                withLatestFrom(this.amount$)
            )
            .subscribe(([_, amount]) => this.calculateToRefundAmount(amount));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    updateType(type: RefundType): void {
        this.refundTypeForm.get('type').patchValue(type);
    }

    private prepareAmounts({ total_amount: totalAmount }: PostMassiveRefundOrdersSummaryResponse): RefundAmount {
        const amount = {
            tickets: totalAmount.base,
            discounts: totalAmount.sales.discount,
            promotions: totalAmount.sales.promotion,
            orderSales: totalAmount.sales.order_automatic + totalAmount.sales.order_collective,
            totalTickets: 0.0,
            fees: totalAmount.channel_charges + totalAmount.promoter_charges,
            delivery: totalAmount.delivery,
            insurance: totalAmount.insurance,
            max: 0.0
        };

        amount.totalTickets = amount.tickets - amount.discounts - amount.promotions - amount.orderSales;
        amount.totalTickets = amount.totalTickets < 0 ? 0 : amount.totalTickets;
        amount.max = amount.totalTickets + amount.fees + amount.delivery + amount.insurance;

        this.resetCharges();

        if (amount.fees > 0) {
            this.refundTypeForm.get('surcharges').enable();
            this.refundTypeForm.get('surcharges').patchValue(true);
        }
        if (amount.delivery > 0) {
            this.refundTypeForm.get('delivery').enable();
            this.refundTypeForm.get('delivery').patchValue(true);
        }
        if (amount.insurance > 0) {
            this.refundTypeForm.get('insurance').enable();
            this.refundTypeForm.get('insurance').patchValue(true);
        }

        this.calculateToRefundAmount(amount);

        return amount;
    }

    private calculateToRefundAmount(amount: RefundAmount): void {
        amount.toRefund = amount.totalTickets;

        if (this.refundTypeForm.get('surcharges').value) {
            amount.toRefund += amount.fees;
        }
        if (this.refundTypeForm.get('delivery').value) {
            amount.toRefund += amount.delivery;
        }
        if (this.refundTypeForm.get('insurance').value) {
            amount.toRefund += amount.insurance;
        }

        this.amountToRefund = amount.toRefund;
    }

    private resetCharges(): void {
        this.refundTypeForm.get('surcharges').reset();
        this.refundTypeForm.get('delivery').reset();
        this.refundTypeForm.get('insurance').reset();
        this.refundTypeForm.get('surcharges').disable();
        this.refundTypeForm.get('delivery').disable();
        this.refundTypeForm.get('insurance').disable();
    }
}

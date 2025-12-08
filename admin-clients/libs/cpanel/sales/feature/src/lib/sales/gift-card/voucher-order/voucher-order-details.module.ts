import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { VoucherOrderDetailsComponent } from './details/voucher-order-details.component';
import { VoucherOrderDetailsGiftCardTableComponent } from './general-data/gift-card-table/gift-card-table.component';
import { VoucherOrderDetailsPaymentsComponent } from './general-data/payments/voucher-order-details-payments.component';
import { ResendVoucherOrderDialogComponent } from './general-data/resend/resend-voucher-order-dialog.component';
import { VoucherOrderBuyerDataComponent } from './general-data/voucher-order-buyer-data/voucher-order-buyer-data.component';
import { VoucherOrderDataComponent } from './general-data/voucher-order-data/voucher-order-data.component';
import { VoucherOrderDeliveryDataComponent } from './general-data/voucher-order-delivery-data/voucher-order-delivery-data.component';
import { VoucherOrderGeneralDataComponent } from './general-data/voucher-order-general-data.component';
import { VoucherOrderPriceComponent } from './general-data/voucher-order-price/voucher-order-price.component';
import { VoucherOrderDetailsRoutingModule } from './voucher-order-details-routing.module';

@NgModule({
    declarations: [
        VoucherOrderDetailsComponent,
        VoucherOrderGeneralDataComponent,
        VoucherOrderDetailsGiftCardTableComponent,
        VoucherOrderDetailsPaymentsComponent,
        ResendVoucherOrderDialogComponent,
        VoucherOrderPriceComponent,
        VoucherOrderDeliveryDataComponent,
        VoucherOrderBuyerDataComponent,
        VoucherOrderDataComponent
    ],
    imports: [
        FormContainerComponent,
        CommonModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        GoBackComponent,
        VoucherOrderDetailsRoutingModule,
        TranslatePipe,
        MaterialModule,
        LocalCurrencyPipe,
        DateTimePipe,
        LocalDateTimePipe,
        EllipsifyDirective
    ]
})
export class VoucherOrderDetailsModule { }

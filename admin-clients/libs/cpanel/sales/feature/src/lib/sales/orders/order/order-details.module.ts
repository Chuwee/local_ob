import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { TableDetailRowModule } from '@admin-clients/cpanel/common/utils';
import {
    ContextNotificationComponent, CopyTextComponent, CurrencyInputComponent, GoBackComponent, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe, ObfuscateStringPipe, VariantTextPipe
} from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderDetailsComponent } from './details/order-details.component';
import { OrderDetailsB2BBuyerDataComponent } from './general-data/b2b-buyer-data/b2b-buyer-data.component';
import { OrderDetailsBuyerDataComponent } from './general-data/buyer-data/buyer-data.component';
import { OrderDetailsInvoiceDataComponent } from './general-data/invoice-data/invoice-data.component';
import { OrderDataComponent } from './general-data/order-data/order-data.component';
import { OrderGeneralDataComponent } from './general-data/order-general-data.component';
import { OrderPriceComponent } from './general-data/order-price/order-price.component';
import { OrderProfileAttributesDialogComponent } from './general-data/order-profile-attributes/order-profile-attributes-dialog.component';
import { OrderDetailsPaymentsComponent } from './general-data/payments/order-details-payments.component';
import { OrderDetailsProductsTableComponent } from './general-data/products-table/products-table.component';
import { RefundOrderDialogComponent } from './general-data/refund/refund-order-dialog.component';
import { ReimburseOrderDialogComponent } from './general-data/reimbursement/reimburse-order-dialog.component';
import { OrderDetailsReimbursementsComponent } from './general-data/reimbursements/order-details-reimbursements.component';
import {
    ResendInvoiceDialogComponent
} from './general-data/resend-invoice/resend-invoice-dialog.component';
import { ResendOrderDialogComponent } from './general-data/resend/resend-order-dialog.component';
import {
    TicketTableAdvanceSelectorComponent
} from './general-data/tickets-table/advance-selection/ticket-table-advance-selector.component';
import { OrderDetailsTicketsTableComponent } from './general-data/tickets-table/tickets-table.component';
import { OrderDetailsRoutingModule } from './order-details-routing.module';
import { OrderItemStateCountPipe } from './pipes/order-item-state-count.pipe';

@NgModule({
    declarations: [
        OrderDetailsComponent,
        RefundOrderDialogComponent,
        ResendOrderDialogComponent,
        ResendInvoiceDialogComponent,
        OrderDetailsReimbursementsComponent,
        ReimburseOrderDialogComponent,
        OrderDetailsPaymentsComponent,
        OrderDetailsTicketsTableComponent,
        OrderDetailsProductsTableComponent,
        TicketTableAdvanceSelectorComponent,
        OrderProfileAttributesDialogComponent,
        OrderGeneralDataComponent,
        OrderItemStateCountPipe
    ],
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        OrderDetailsRoutingModule,
        FlexLayoutModule,
        FormContainerComponent,
        FormControlErrorsComponent,
        CopyTextComponent,
        GoBackComponent,
        CurrencyInputComponent,
        ContextNotificationComponent,
        SatPopoverModule,
        LocalCurrencyPipe,
        ObfuscateStringPipe,
        LocalDateTimePipe,
        DateTimePipe,
        AsyncPipe,
        EllipsifyDirective,
        NavTabsMenuComponent,
        VariantTextPipe,
        TableDetailRowModule,
        OrderDataComponent,
        OrderDetailsBuyerDataComponent,
        OrderDetailsB2BBuyerDataComponent,
        OrderDetailsInvoiceDataComponent,
        OrderPriceComponent
    ]
})
export class OrderDetailsModule { }

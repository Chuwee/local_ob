import { TableDetailRowModule } from '@admin-clients/cpanel/common/utils';
import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    AbsoluteAmountPipe,
    AmountPrefixPipe,
    DateTimePipe,
    LocalCurrencyPipe
} from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { MemberOrderDetailsComponent } from './details/member-order-details.component';
import { MemberOrderDataComponent } from './general-data/member-order-data/member-order-data.component';
import { MemberOrderGeneralDataComponent } from './general-data/member-order-general-data.component';
import { MemberOrderItemsComponent } from './general-data/member-order-items/member-order-items.component';
import { MemberOrderPaymentsComponent } from './general-data/member-order-payments/member-order-payments.component';
import { MemberOrderPriceComponent } from './general-data/member-order-price/member-order-price.component';
import { MemberOrderUsersComponent } from './general-data/member-order-users/member-order-users.component';
import { MemberOrderDetailsRoutingModule } from './member-order-details-routing.module';

@NgModule({
    declarations: [
        MemberOrderDetailsComponent,
        MemberOrderGeneralDataComponent,
        MemberOrderPriceComponent,
        MemberOrderUsersComponent,
        MemberOrderDataComponent,
        MemberOrderItemsComponent,
        MemberOrderPaymentsComponent
    ],
    imports: [
        GoBackComponent,
        FormContainerComponent,
        TableDetailRowModule,
        MemberOrderDetailsRoutingModule,
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        MaterialModule,
        LocalCurrencyPipe,
        AsyncPipe,
        DateTimePipe,
        EllipsifyDirective,
        AbsoluteAmountPipe,
        AmountPrefixPipe
    ]
})
export class MemberOrderDetailsModule { }

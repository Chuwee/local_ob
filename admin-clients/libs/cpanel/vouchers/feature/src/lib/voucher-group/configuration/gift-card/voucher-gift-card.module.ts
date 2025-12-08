import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import {
    CurrencyInputComponent, ImageUploaderComponent,
    RichTextAreaComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { GiftCardGroupEmailComponent } from './email/gift-card-group-email.component';
import { GiftCardGroupSaleComponent } from './sale/gift-card-group-sale.component';
import { VoucherGiftCardRoutingModule } from './voucher-gift-card-routing.module';

@NgModule({
    declarations: [
        GiftCardGroupEmailComponent,
        GiftCardGroupSaleComponent
    ],
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        MaterialModule,
        VoucherGiftCardRoutingModule,
        FormControlErrorsComponent,
        RichTextAreaComponent,
        ImageUploaderComponent,
        CurrencyInputComponent,
        FormContainerComponent,
        LocalCurrencyPipe,
        TabsMenuComponent,
        TabDirective
    ],
    exports: []
})
export class VoucherGiftCardModule { }

import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import {
    AggregatedDataComponent, CurrencyInputComponent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { NewVoucherDialogComponent } from './create/new-voucher-dialog.component';
import {
    ImportVoucherCodesHeaderMatchComponent
} from './list/import/import-dialog/header-match/import-voucher-codes-header-match.component';
import { ImportVoucherCodesDialogComponent } from './list/import/import-dialog/import-voucher-codes-dialog.component';
import { ImportVoucherCodesSelectionComponent } from './list/import/import-dialog/selection/import-voucher-codes-selection.component';
import { VoucherListComponent } from './list/voucher-list.component';
import { UpdateBalanceDialogComponent } from './update-balance/update-balance-dialog.component';
import { VoucherEmailDialogComponent } from './voucher/email-dialog/voucher-email-dialog.component';
import { VoucherComponent } from './voucher/voucher.component';
import { VoucherRoutingModule } from './voucher-routing.module';

@NgModule({
    declarations: [
        VoucherListComponent,
        VoucherComponent,
        NewVoucherDialogComponent,
        UpdateBalanceDialogComponent,
        ImportVoucherCodesDialogComponent,
        ImportVoucherCodesSelectionComponent,
        ImportVoucherCodesHeaderMatchComponent,
        VoucherEmailDialogComponent
    ],
    imports: [
        MaterialModule,
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        FormControlErrorsComponent,
        FormContainerComponent,
        CurrencyInputComponent,
        VoucherRoutingModule,
        AggregatedDataComponent,
        CsvModule,
        SearchablePaginatedSelectionModule,
        LocalCurrencyPipe,
        DateTimePipe,
        LocalDateTimePipe,
        WizardBarComponent
    ],
    exports: []
})
export class VoucherModule { }

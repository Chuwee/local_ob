import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import {
    PopoverFilterDirective,
    StatusSelectComponent,
    PopoverComponent,
    ChipsFilterDirective,
    ChipsComponent,
    PaginatorComponent,
    SelectSearchComponent,
    SearchInputComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    LocalCurrencyFullTranslationPipe, LocalCurrenciesFullTranslation$Pipe,
    LocalCurrencyPartialTranslationPipe, CurrencyFilterItemValuesPipe
} from '@admin-clients/shared/utility/pipes';
import { VoucherGroupsFilterComponent } from './list/filter/voucher-groups-filter.component';
import { VoucherGroupsListComponent } from './list/voucher-groups-list.component';
import { VouchersRoutingModule } from './vouchers-routing.module';

@NgModule({
    declarations: [
        VoucherGroupsListComponent,
        VoucherGroupsFilterComponent
    ],
    imports: [
        MaterialModule,
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        SelectSearchComponent,
        SearchInputComponent,
        FormControlErrorsComponent,
        VouchersRoutingModule,
        StatusSelectComponent,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        ChipsComponent,
        PaginatorComponent,
        ContextNotificationComponent,
        LocalCurrencyFullTranslationPipe,
        LocalCurrenciesFullTranslation$Pipe,
        LocalCurrencyPartialTranslationPipe,
        CurrencyFilterItemValuesPipe,
        EllipsifyDirective
    ]
})
export class VouchersModule { }

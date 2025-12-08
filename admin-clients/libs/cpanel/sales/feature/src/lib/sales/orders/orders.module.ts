import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsApi, ChannelsService, ChannelsState } from '@admin-clients/cpanel/channels/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import {
  AggregatedDataComponent,
  DateTimeModule,
  SearchInputComponent,
  PaginatorComponent,
  CopyTextComponent,
  ContextNotificationComponent,
  SelectServerSearchComponent,
  PopoverFilterDirective,
  PopoverComponent,
  ChipsFilterDirective,
  ChipsComponent,
  PopoverDateRangePickerFilterComponent,
  HelpButtonComponent,
  SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
  CurrencyFilterItemsFullTranslationPipe,
  DateTimePipe,
  LocalCurrencyFullTranslationPipe,
  LocalCurrencyPipe
} from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MassiveRefundDetailsComponent } from './massive-refund/massive-refund-details/massive-refund-details.component';
import { MassiveRefundDialogComponent } from './massive-refund/massive-refund-dialog.component';
import { MassiveRefundTypeComponent } from './massive-refund/massive-refund-type/massive-refund-type.component';
import { MassiveRefundSummaryDialogComponent } from './massive-refund-summary/massive-refund-summary-dialog.component';
import { OrdersRoutingModule } from './orders-routing.module';
import { ResendCodesDialogComponent } from './resend-codes/resend-codes-dialog.component';

@NgModule({
  declarations: [
    ResendCodesDialogComponent,
    MassiveRefundDialogComponent,
    MassiveRefundDetailsComponent,
    MassiveRefundTypeComponent,
    MassiveRefundSummaryDialogComponent
  ],
  imports: [
    OrdersRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    CommonModule,
    DateTimeModule,
    AggregatedDataComponent,
    SearchInputComponent,
    SelectServerSearchComponent,
    PaginatorComponent,
    CopyTextComponent,
    ContextNotificationComponent,
    WizardBarComponent,
    FormControlErrorsComponent,
    LocalCurrencyPipe,
    DateTimePipe,
    AsyncPipe,
    TranslatePipe,
    DragDropModule,
    CsvModule,
    PopoverComponent,
    PopoverFilterDirective,
    ChipsFilterDirective,
    ChipsComponent,
    PopoverDateRangePickerFilterComponent,
    EllipsifyDirective,
    HelpButtonComponent,
    LocalCurrencyFullTranslationPipe,
    SelectSearchComponent,
    CurrencyFilterItemsFullTranslationPipe
  ],
  providers: [
    ChannelsService,
    ChannelsApi,
    ChannelsState
  ]
})
export class OrdersModule {
}

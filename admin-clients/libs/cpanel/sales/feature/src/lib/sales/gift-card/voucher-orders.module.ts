import {
  AggregatedDataComponent,
  ContextNotificationComponent,
  CopyTextComponent,
  DateTimeModule,
  PaginatorComponent,
  SearchInputComponent,
  SelectServerSearchComponent,
  PopoverFilterDirective,
  ChipsFilterDirective,
  ChipsComponent,
  PopoverComponent,
  PopoverDateRangePickerFilterComponent,
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
import { CurrencySalesFilterComponent } from '../filter-currency-list/filter-currency-list.component';
import { VoucherOrdersListFilterComponent } from './list/filter/voucher-orders-list-filter.component';
import { VoucherOrderListComponent } from './list/voucher-orders-list.component';
import { VoucherOrdersRoutingModule } from './voucher-orders-routing.module';

@NgModule({
  declarations: [
    VoucherOrderListComponent,
    VoucherOrdersListFilterComponent
  ],
  imports: [
    CommonModule,
    FlexLayoutModule,
    AggregatedDataComponent,
    SearchInputComponent,
    PaginatorComponent,
    PopoverFilterDirective,
    PopoverComponent,
    PopoverDateRangePickerFilterComponent,
    ChipsFilterDirective,
    ChipsComponent,
    CopyTextComponent,
    ContextNotificationComponent,
    SelectServerSearchComponent,
    DateTimeModule,
    VoucherOrdersRoutingModule,
    DragDropModule,
    ReactiveFormsModule,
    MaterialModule,
    LocalCurrencyPipe,
    DateTimePipe,
    TranslatePipe,
    AsyncPipe,
    EllipsifyDirective,
    LocalCurrencyFullTranslationPipe,
    SelectSearchComponent,
    CurrencyFilterItemsFullTranslationPipe,
    CurrencySalesFilterComponent
  ]
})
export class VoucherOrdersModule {
}

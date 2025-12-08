import {
  AggregatedDataComponent, ContextNotificationComponent, CopyTextComponent, DateTimeModule,
  PaginatorComponent, SearchInputComponent, SelectServerSearchComponent, PopoverDateRangePickerFilterComponent,
  PopoverFilterDirective, ChipsFilterDirective, PopoverComponent, ChipsComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
  CurrencyFilterItemsFullTranslationPipe, DateTimePipe, LocalCurrencyFullTranslationPipe, LocalCurrencyPipe
} from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { CurrencySalesFilterComponent } from '../filter-currency-list/filter-currency-list.component';
import { MemberOrdersListFilterComponent } from './list/filter/member-orders-list-filter.component';
import { MemberOrderListComponent } from './list/member-orders-list.component';
import { MemberOrdersRoutingModule } from './member-orders-routing.module';

@NgModule({
  declarations: [
    MemberOrderListComponent,
    MemberOrdersListFilterComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    DateTimeModule,
    MemberOrdersRoutingModule,
    LocalCurrencyPipe,
    DateTimePipe,
    AsyncPipe,
    TranslatePipe,
    DragDropModule,
    AggregatedDataComponent,
    SearchInputComponent,
    PaginatorComponent,
    CopyTextComponent,
    ContextNotificationComponent,
    SelectServerSearchComponent,
    PopoverDateRangePickerFilterComponent,
    PopoverComponent,
    ChipsComponent,
    PopoverFilterDirective,
    ChipsFilterDirective,
    EllipsifyDirective,
    LocalCurrencyFullTranslationPipe,
    SelectSearchComponent,
    CurrencyFilterItemsFullTranslationPipe,
    CurrencySalesFilterComponent
  ]
})
export class MemberOrdersModule { }

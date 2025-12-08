import { TicketsApi, TicketsState, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import {
    AggregatedDataComponent, ContextNotificationComponent, CopyTextComponent, DateTimeModule,
    PaginatorComponent, SearchInputComponent, SelectServerSearchComponent, PopoverFilterDirective, PopoverComponent,
    ChipsComponent, ChipsFilterDirective, PopoverDateRangePickerFilterComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective, TableMaxHeightDirective } from '@admin-clients/shared/utility/directives';
import {
    CurrencyFilterItemsFullTranslationPipe, DateTimePipe, LocalCurrencyFullTranslationPipe, LocalCurrencyPipe, LocalDateTimePipe,
    ObfuscateStringPipe
} from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { CurrencySalesFilterComponent } from '../filter-currency-list/filter-currency-list.component';
import { TicketsListFilterComponent } from './list/filter/tickets-list-filter.component';
import { TicketsListComponent } from './list/tickets-list.component';
import { TicketsRoutingModule } from './tickets-routing.module';

@NgModule({
    declarations: [
        TicketsListComponent
    ],
    imports: [
        TicketsListFilterComponent,
        TableMaxHeightDirective,
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        TicketsRoutingModule,
        AggregatedDataComponent,
        PaginatorComponent,
        SearchInputComponent,
        SelectServerSearchComponent,
        CopyTextComponent,
        ContextNotificationComponent,
        TranslatePipe,
        DateTimeModule,
        LocalCurrencyPipe,
        ObfuscateStringPipe,
        DateTimePipe,
        LocalDateTimePipe,
        AsyncPipe,
        DragDropModule,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsComponent,
        ChipsFilterDirective,
        PopoverDateRangePickerFilterComponent,
        EllipsifyDirective,
        LocalCurrencyFullTranslationPipe,
        SelectSearchComponent,
        CurrencyFilterItemsFullTranslationPipe,
        CurrencySalesFilterComponent
    ],
    providers: [TicketsService, TicketsApi, TicketsState]
})
export class TicketsModule {
}

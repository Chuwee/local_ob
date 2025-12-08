import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import { eventPromotionsProviders } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { HeaderSummaryComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    CurrencyInputComponent,
    EmptyStateComponent,
    PaginatorComponent,
    SearchInputSelectComponent, SelectSearchComponent, SidebarFilterComponent,
    ContextNotificationComponent,
    ColSelectionDialogComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BuyersRoutingModule } from './buyers-routing.module';
import { NewBuyerDialogComponent } from './create/new-buyer-dialog.component';
import { BuyersListComponent } from './list/buyers-list.component';
import { BuyersSidebarFilterComponent } from './list/filter/buyers-sidebar-filter.component';
import { BuyersQueryDialogComponent } from './list/query-dialog/buyers-query-dialog.component';
import { BuyersQueryListComponent } from './list/query-list/buyers-query-list.component';
import { BuyersSearchKeywordInputComponent } from './list/search-input/buyers-search-keyword-input.component';

@NgModule({
    providers: [
        eventPromotionsProviders
    ],
    declarations: [
        BuyersSidebarFilterComponent,
        BuyersListComponent,
        BuyersQueryListComponent,
        BuyersQueryDialogComponent,
        NewBuyerDialogComponent,
        BuyersSearchKeywordInputComponent
    ],
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        LocalCurrencyPipe,
        LocalNumberPipe,
        CurrencyInputComponent,
        ContextNotificationComponent,
        FormControlErrorsComponent,
        SelectSearchComponent,
        FlexLayoutModule,
        ColSelectionDialogComponent,
        SidebarFilterComponent,
        BuyersRoutingModule,
        SearchInputSelectComponent,
        DateTimePipe,
        DragDropModule,
        EntityFilterModule,
        EmptyStateComponent,
        HeaderSummaryComponent,
        PaginatorComponent,
        EllipsifyDirective
    ]
})
export class BuyersModule { }

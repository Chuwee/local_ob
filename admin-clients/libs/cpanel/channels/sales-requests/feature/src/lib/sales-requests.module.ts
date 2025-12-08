import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    PopoverFilterDirective, StatusSelectComponent, PopoverComponent, ChipsFilterDirective, ChipsComponent,
    PaginatorComponent, SearchInputComponent, SelectServerSearchComponent, ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SalesRequestsListFilterComponent } from './list/filter/sales-requests-list-filter.component';
import { SalesRequestsListComponent } from './list/sales-requests-list.component';
import { SaleRequestModule } from './sale-request/sale-request.module';
import { SalesRequestsRoutingModule } from './sales-requests-routing.module';

@NgModule({
    declarations: [
        SalesRequestsListComponent,
        SalesRequestsListFilterComponent
    ],
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormContainerComponent,
        FormControlErrorsComponent,
        FlexLayoutModule,
        SearchInputComponent,
        SelectServerSearchComponent,
        SalesRequestsRoutingModule,
        SaleRequestModule,
        StatusSelectComponent,
        DateTimePipe,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        ChipsComponent,
        PaginatorComponent,
        ContextNotificationComponent
    ]
})
export class SalesRequestsModule { }

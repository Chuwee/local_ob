import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    PopoverFilterDirective,
    PopoverComponent,
    ChipsFilterDirective,
    ChipsComponent,
    PaginatorComponent,
    SearchInputComponent,
    SelectSearchComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { NewTicketPassbookDialogComponent } from './create/new-ticket-passbook-dialog.component';
import { TicketPassbookListFilterComponent } from './list/list-filter/ticket-passbook-list-filter.component';
import { TicketPassbookListComponent } from './list/ticket-passbook-list.component';
import { TicketsPassbookRoutingModule } from './tickets-passbook-routing.module';

@NgModule({
    declarations: [
        TicketPassbookListComponent,
        TicketPassbookListFilterComponent,
        NewTicketPassbookDialogComponent
    ],
    imports: [
        DefaultIconComponent,
        TicketsPassbookRoutingModule,
        LocalDateTimePipe,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        ChipsComponent,
        PaginatorComponent,
        MaterialModule,
        FlexLayoutModule,
        CommonModule,
        TranslatePipe,
        SearchInputComponent,
        ReactiveFormsModule,
        SelectSearchComponent,
        FormControlErrorsComponent,
        ContextNotificationComponent,
        EllipsifyDirective
    ]
})
export class TicketsPassbookModule { }

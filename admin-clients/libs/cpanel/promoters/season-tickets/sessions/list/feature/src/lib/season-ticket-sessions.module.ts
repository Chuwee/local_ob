import { HeaderSummaryComponent } from '@admin-clients/cpanel/shared/ui/components';
import { StdVenueTplMgrComponent } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import {
    PopoverFilterDirective, PopoverComponent, ChipsComponent, ChipsFilterDirective,
    PaginatorComponent, CopyTextComponent, ContextNotificationComponent, SearchInputComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketSessionsListDialogComponent } from './dialog/season-ticket-sessions-list-dialog.component';
import { SeasonTicketSessionsListFilterComponent } from './filter/season-ticket-sessions-list-filter.component';
import { SeasonTicketSessionsListComponent } from './season-ticket-sessions-list.component';
import { SeasonTicketSessionsRoutingModule } from './season-ticket-sessions-routing.module';
import { SeasonTicketSessionsListSummaryComponent } from './summary/season-ticket-sessions-list-summary.component';

@NgModule({
    declarations: [
        SeasonTicketSessionsListComponent,
        SeasonTicketSessionsListFilterComponent,
        SeasonTicketSessionsListDialogComponent,
        SeasonTicketSessionsListSummaryComponent
    ],
    imports: [
        StdVenueTplMgrComponent,
        SeasonTicketSessionsRoutingModule,
        LocalNumberPipe,
        LocalDateTimePipe,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsComponent,
        ChipsFilterDirective,
        HeaderSummaryComponent,
        PaginatorComponent,
        CopyTextComponent,
        MaterialModule,
        ContextNotificationComponent,
        TranslatePipe,
        CommonModule,
        FlexLayoutModule,
        SearchInputComponent,
        ReactiveFormsModule,
        SelectSearchComponent,
        EllipsifyDirective
    ]
})
export class SeasonTicketSessionsModule {
}

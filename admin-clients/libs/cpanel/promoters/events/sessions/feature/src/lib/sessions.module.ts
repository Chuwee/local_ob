import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { provideCalendarSettings } from '@admin-clients/cpanel/core/data-access';
import { SessionsListCountersService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { SessionRatesComponent } from '@admin-clients/cpanel-promoters-events-sessions-planning-feature';
import { EventPipesModule } from '@admin-clients/cpanel-promoters-events-utils';
import { DateTimeModule, EmptyStateComponent, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { CalendarModule } from 'angular-calendar';
import { CloneSessionDialogComponent } from './clone/clone-session-dialog.component';
import { SessionsContainerComponent } from './container/sessions-container.component';
import { CreateSessionBaseDataComponent } from './create/base-data/create-session-base-data.component';
import { BaseSessionComponent } from './create/multiple/base-session/base-session.component';
import { SessionsCalendarComponent } from './create/multiple/calendar/sessions-calendar.component';
import { CreateMultiSessionComponent } from './create/multiple/create-multi-session.component';
import { NewHourDialogComponent } from './create/multiple/repetitions/new-hour/new-hour-dialog.component';
import { SessionRepetitionsComponent } from './create/multiple/repetitions/session-repetitions.component';
import { NewSessionDialogComponent } from './create/new-session-dialog.component';
import { CreateSessionSelectorComponent } from './create/selector/create-session-selector.component';
import { CreateSingleSessionComponent } from './create/single/create-single-session.component';
import { SessionsListFilterComponent } from './list/filter/sessions-list-filter.component';
import { SessionsListComponent } from './list/sessions-list.component';
import { SessionsRoutingModule } from './sessions-routing.module';

@NgModule({
    declarations: [
        SessionsContainerComponent,
        SessionsListComponent,
        NewSessionDialogComponent,
        CreateSessionSelectorComponent,
        CreateSessionBaseDataComponent,
        CreateSingleSessionComponent,
        CreateMultiSessionComponent,
        BaseSessionComponent,
        SessionRepetitionsComponent,
        NewHourDialogComponent,
        SessionsCalendarComponent,
        CloneSessionDialogComponent,
        SessionsListFilterComponent
    ],
    providers: [
        provideCalendarSettings(),
        SessionsListCountersService
    ],
    imports: [
        CommonModule,
        SessionsRoutingModule,
        ScrollingModule,
        CalendarModule,
        DateTimeModule,
        EventPipesModule,
        LocalNumberPipe,
        LocalDateTimePipe,
        DateTimePipe,
        EmptyStateComponent,
        WizardBarComponent,
        SessionRatesComponent,
        MaterialModule,
        TranslatePipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        SatPopoverModule,
        SelectSearchComponent,
        FormControlErrorsComponent,
        EllipsifyDirective
    ]
})
export class SessionsModule {
}

import { B2bService, B2bApi, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import { EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { eventSessionsProviders } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import {
    ObDialogService, PaginatorComponent,
    SearchInputComponent, EmptyStateComponent, ChipsComponent, PopoverComponent,
    ListFiltersService, SelectSearchComponent, SelectServerSearchComponent, PopoverFilterDirective,
    ChipsFilterDirective,
    CopyTextComponent,
    GoBackComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { B2bPublishingsRoutingModule } from './b2b-publishings-routing.module';
import { B2bPublishingsListFilterComponent } from './filter/b2b-publishings-list-filter.component';
import { B2bPublishingsListComponent } from './list/b2b-publishings-list.component';

@NgModule({
    declarations: [
        B2bPublishingsListComponent,
        B2bPublishingsListFilterComponent
    ],
    providers: [
        eventsProviders,
        eventSessionsProviders,
        venuesProviders,
        B2bService,
        B2bApi,
        B2bState,
        ObDialogService,
        ListFiltersService
    ],
    imports: [
        DateTimePipe,
        ContextNotificationComponent,
        SelectServerSearchComponent,
        CommonModule,
        B2bPublishingsRoutingModule,
        TranslatePipe,
        ReactiveFormsModule,
        EntityFilterModule,
        MaterialModule,
        FlexLayoutModule,
        FormContainerComponent,
        EmptyStateComponent,
        PopoverComponent,
        SearchInputComponent,
        ChipsComponent,
        PaginatorComponent,
        RouterLink,
        LocalDateTimePipe,
        SelectSearchComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        CopyTextComponent,
        LocalCurrencyPipe,
        GoBackComponent
    ]
})
export class B2bPublishingsModule { }

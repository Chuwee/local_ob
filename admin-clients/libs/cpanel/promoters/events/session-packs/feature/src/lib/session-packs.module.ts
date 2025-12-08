
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    ColorPickerComponent,
    ContextNotificationComponent,
    DateTimeModule, EmptyStateComponent, HelpButtonComponent, NavTabsMenuComponent,
    PercentageInputComponent,
    SearchablePaginatedSelectionModule,
    SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SessionPacksContainerComponent } from './container/session-packs-container.component';
import { CreateSessionPackDialogComponent } from './create/create-session-pack-dialog.component';
import { SessionPacksListComponent } from './list/session-packs-list.component';
import { DeleteSessionPackDialogComponent } from './session-pack/delete-session-pack-dialog/delete-session-pack-dialog.component';
import { SessionPackDetailsComponent } from './session-pack/details/session-pack-details.component';
import { PartialRefundPercentagesTableComponent }
    from './session-pack/refunds/partial-refund-percentages-table/partial-refund-percentages-table.component';
import { SessionPackRefundsComponent } from './session-pack/refunds/session-pack-refunds.component';
import { SessionPacksRoutingModule } from './session-packs-routing.module';

@NgModule({
    declarations: [
        SessionPacksContainerComponent,
        SessionPacksListComponent,
        SessionPackDetailsComponent,
        SessionPackRefundsComponent,
        PartialRefundPercentagesTableComponent,
        CreateSessionPackDialogComponent,
        DeleteSessionPackDialogComponent
    ],
    imports: [
        CommonModule,
        MaterialModule,
        FormContainerComponent,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        HelpButtonComponent,
        PercentageInputComponent,
        SelectSearchComponent,
        ContextNotificationComponent,
        FormControlErrorsComponent,
        SessionPacksRoutingModule,
        LastPathGuardListenerDirective,
        DateTimeModule,
        SearchablePaginatedSelectionModule,
        NavTabsMenuComponent,
        LocalNumberPipe,
        LocalCurrencyPipe,
        DateTimePipe,
        LocalDateTimePipe,
        EmptyStateComponent,
        ColorPickerComponent,
        EllipsifyDirective
    ]
})
export class SessionPacksModule { }

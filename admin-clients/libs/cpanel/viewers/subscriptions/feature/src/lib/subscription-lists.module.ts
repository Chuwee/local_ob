import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    EmptyStateComponent, PaginatorComponent, SelectSearchComponent, ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { SubscriptionListsContainerComponent } from './container/subscription-lists-container.component';
import { NewSubscriptionListsDialogComponent } from './create/new-subscription-lists-dialog.component';
import { SubscriptionListFilterComponent } from './list/filter/subscription-list-filter.component';
import { SubscriptionListsListComponent } from './list/subscription-lists-list.component';
import { SubscriptionClientsComponent } from './subscription-list/clients/subscription-clients.component';
import { SubscriptionListGeneralDataComponent } from './subscription-list/general-data/subscription-list-general-data.component';
import { SubscriptionListsRoutingModule } from './subscription-lists-routing.module';

@NgModule({
    declarations: [
        SubscriptionListsContainerComponent,
        SubscriptionListsListComponent,
        NewSubscriptionListsDialogComponent,
        SubscriptionListGeneralDataComponent,
        SubscriptionListFilterComponent,
        SubscriptionClientsComponent
    ],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        SatPopoverModule,
        FlexLayoutModule,
        FormContainerComponent,
        SelectSearchComponent,
        FormControlErrorsComponent,
        LastPathGuardListenerDirective,
        SubscriptionListsRoutingModule,
        LocalDateTimePipe,
        EmptyStateComponent,
        PaginatorComponent,
        ContextNotificationComponent,
        EllipsifyDirective
    ]
})
export class SubscriptionListsModule { }

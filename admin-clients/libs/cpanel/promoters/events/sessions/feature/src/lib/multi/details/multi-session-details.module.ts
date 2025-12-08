import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { StdVenueTplMgrComponent } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { SessionCommunicationContentModule } from '@admin-clients/cpanel-promoters-events-sessions-communication-feature';
import {
    ContextNotificationComponent, DateTimeModule, LanguageBarComponent, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent, FormContainerFullComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MultiSessionCapacityComponent } from '../capacity/multi-session-capacity.component';
import {
    MultiSessionChangesVerificationDialogComponent
} from '../changes-verification/multi-session-changes-verification-dialog.component';
import { MultiSessionCommunicationComponent } from '../communication/multi-session-communication.component';
import { MultiSessionDeleteDialogComponent } from '../delete/multi-session-delete-dialog.component';
import { MultiSessionPlanningComponent } from '../planning/multi-session-planning.component';
import { MultiSessionDetailsRoutingModule } from './multi-session-details-routing.module';
import { MultiSessionDetailsComponent } from './multi-session-details.component';

@NgModule({
    declarations: [
        MultiSessionDetailsComponent,
        MultiSessionPlanningComponent,
        MultiSessionCommunicationComponent,
        MultiSessionChangesVerificationDialogComponent,
        MultiSessionDeleteDialogComponent,
        MultiSessionCapacityComponent
    ],
    imports: [
        CommonModule,
        SessionCommunicationContentModule,
        MultiSessionDetailsRoutingModule,
        StdVenueTplMgrComponent,
        DateTimeModule,
        FormContainerFullComponent,
        ArchivedEventMgrComponent,
        NavTabsMenuComponent,
        DateTimePipe,
        MaterialModule,
        ContextNotificationComponent,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        FormContainerComponent,
        LanguageBarComponent,
        EllipsifyDirective
    ]
})
export class MultiSessionDetailsModule {
}

import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ArchivedEventMgrComponent, AttributesComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { StdVenueTplMgrComponent } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import {
    ActivityVenueTemplatePriceTypesComponent, ActivityVenueTemplateLimitsComponent,
    ActivityVenueTemplatePriceTypesGatesComponent, ActivityVenueTemplateGatesComponent,
    ActivityVenueTemplateQuotasComponent, ActivityVenueTemplateGroupsComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { FeverZonePlanLinkComponent } from '@admin-clients/cpanel-fever-feature';
import {
    SessionCommunicationContentModule
} from '@admin-clients/cpanel-promoters-events-sessions-communication-feature';
import {
    SessionRatesComponent
} from '@admin-clients/cpanel-promoters-events-sessions-planning-feature';
import { SessionPresalesComponent } from '@admin-clients/cpanel-promoters-events-sessions-presales-feature';
import { PriceTypeRestrictionsComponent } from '@admin-clients/cpanel-promoters-venue-templates-feature';
import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import {
    ContextNotificationComponent,
    CopyTextComponent, DateTimeModule, NavTabsMenuComponent, SearchablePaginatedSelectionModule, SearchTableComponent,
    SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent, FormContainerFullComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalDateTimePipe, ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SessionAttributesComponent } from './attributes/attributes.component';
import { SessionCapacityComponent } from './capacity/session-capacity.component';
import { SessionCapacityActivityComponent } from './capacity-activity/session-capacity-activity.component';
import { SessionCodeConfigurationComponent } from './code-configuration/session-code-configuration.component';
import { SessionDetailsComponent } from './details/session-details.component';
import { SessionOcupationComponent } from './ocupation/session-ocupation.component';
import { TiersOcupationComponent } from './ocupation/tiers-ocupation/tiers-ocupation.component';
import { SessionRoutingModule } from './session-routing.module';

@NgModule({
    declarations: [
        SessionAttributesComponent,
        SessionCapacityActivityComponent,
        SessionCapacityComponent,
        SessionCodeConfigurationComponent,
        SessionDetailsComponent,
        SessionOcupationComponent,
        TiersOcupationComponent
    ],
    imports: [
        ActivityVenueTemplatePriceTypesComponent,
        ActivityVenueTemplateLimitsComponent,
        ActivityVenueTemplatePriceTypesGatesComponent,
        ActivityVenueTemplateGatesComponent,
        ActivityVenueTemplateQuotasComponent,
        ActivityVenueTemplateGroupsComponent,
        ArchivedEventMgrComponent,
        CommonModule,
        SearchablePaginatedSelectionModule,
        CsvModule,
        DateTimeModule,
        FormContainerFullComponent,
        PriceTypeRestrictionsComponent,
        ScrollingModule,
        SessionCommunicationContentModule,
        SessionRoutingModule,
        SearchTableComponent,
        StdVenueTplMgrComponent,
        NavTabsMenuComponent,
        ObfuscateStringPipe,
        LocalDateTimePipe,
        AttributesComponent,
        WizardBarComponent,
        CopyTextComponent,
        SessionRatesComponent,
        SessionPresalesComponent,
        ErrorMessage$Pipe,
        ErrorIconDirective,
        ContextNotificationComponent,
        MaterialModule,
        TranslatePipe,
        FormContainerComponent,
        ReactiveFormsModule,
        SelectSearchComponent,
        FormControlErrorsComponent,
        FlexLayoutModule,
        EllipsifyDirective,
        FeverZonePlanLinkComponent
    ]
})
export class SessionModule {
}

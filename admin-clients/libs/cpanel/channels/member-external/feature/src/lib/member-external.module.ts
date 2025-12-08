import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelMemberExternalApi, ChannelMemberExternalService, ChannelMemberExternalState
} from '@admin-clients/cpanel-channels-member-external-data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    DateTimeModule,
    SearchTableComponent,
    TabDirective,
    TabsMenuComponent,
    SearchablePaginatedSelectionModule, NavTabsMenuComponent,
    ContextNotificationComponent,
    SelectSearchComponent,
    RichTextAreaComponent,
    HelpButtonComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MemberExternalContainerComponent } from './container/member-external-container.component';
import { FreeSeatOptionsComponent } from './free-seat/channel-member-free-seat-options.component';
import { MemberExternalRoutingModule } from './member-external-routing.module';
import { ChannelMembersConfigComponent } from './options/channel-members-config.component';
import {
    ChannelMemberExternalPeriodsChangeSeatLimitationsComponent
} from './periods/change-seat-limitations/channel-member-periods-change-seat-limitations.component';
import { ChannelMemberExternalPeriodsContainerComponent } from './periods/container/channel-member-periods-container.component';
import { ChannelMemberExternalPeriodsStepsComponent } from './periods/steps/channel-member-periods-steps.component';
import { MemberExternalRestrictionDialogComponent } from './restrictions/dialog/restriction-dialog.component';
import { MemberExternalRestrictionFieldsComponent } from './restrictions/fields/restriction-fields.component';
import { MemberExternalRestrictionComponent } from './restrictions/restriction/restriction.component';
import { MemberExternalRestrictionsComponent } from './restrictions/restrictions.component';
import { MemberExternalRestrictionTranslationsDialogComponent } from './restrictions/translations/translations-dialog.component';
import { CapacitiesComponent } from './settings/capacities/capacities.component';
import { MemberExternalSettingsComponent } from './settings/container/member-external-settings.component';
import { PeriodicityDialogComponent } from './settings/periodicities/dialog/periodicity-dialog.component';
import { PeriodicitiesComponent } from './settings/periodicities/periodicities.component';
import { RoleDialogComponent } from './settings/roles/dialog/roles-dialog.component';
import { RolesComponent } from './settings/roles/roles.component';
import { CommunicationFieldsComponent } from './settings/shared/communication/communication.component';
import { SubscriptionModeGeneralSettingsComponent } from './settings/subscription-modes/dialog/general-settings/general-settings.component';
import { SubscriptionModeDialogComponent } from './settings/subscription-modes/dialog/subscription-mode-dialog.component';
import { SubscriptionModesComponent } from './settings/subscription-modes/subscription-modes.component';

@NgModule({
    declarations: [
        CapacitiesComponent,
        ChannelMemberExternalPeriodsStepsComponent,
        ChannelMemberExternalPeriodsContainerComponent,
        ChannelMemberExternalPeriodsChangeSeatLimitationsComponent,
        CommunicationFieldsComponent,
        MemberExternalRestrictionsComponent,
        MemberExternalRestrictionComponent,
        MemberExternalRestrictionDialogComponent,
        MemberExternalRestrictionTranslationsDialogComponent,
        MemberExternalRestrictionFieldsComponent,
        MemberExternalSettingsComponent,
        PeriodicitiesComponent,
        PeriodicityDialogComponent,
        RoleDialogComponent,
        RolesComponent,
        SubscriptionModeDialogComponent,
        SubscriptionModeGeneralSettingsComponent,
        SubscriptionModesComponent
    ],
    providers: [
        ChannelMemberExternalApi,
        ChannelMemberExternalService,
        ChannelMemberExternalState
    ],
    imports: [
        FormContainerComponent,
        TranslatePipe,
        CommonModule,
        ReactiveFormsModule,
        MaterialModule,
        SelectSearchComponent,
        RichTextAreaComponent,
        FlexLayoutModule,
        HelpButtonComponent,
        FormControlErrorsComponent,
        ContextNotificationComponent,
        SearchTableComponent,
        DateTimeModule,
        MemberExternalRoutingModule,
        SearchablePaginatedSelectionModule,
        TabsMenuComponent,
        TabDirective,
        NavTabsMenuComponent,
        WizardBarComponent,
        MemberExternalContainerComponent,
        ChannelMembersConfigComponent,
        FreeSeatOptionsComponent
    ]
})
export class ChannelMemberExternalModule { }

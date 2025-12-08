import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { MemberPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AvailabilityQueryComponent } from './availability-query/availability-query.component';
import { MemberExternalContainerComponent } from './container/member-external-container.component';
import { FreeSeatOptionsComponent } from './free-seat/channel-member-free-seat-options.component';
import { ChannelMembersConfigComponent } from './options/channel-members-config.component';
import { ConfigurationsComponent } from './periods/advanced/configurations/configurations.component';
import {
    ChannelMemberExternalPeriodsBuySeatConfigsComponent
} from './periods/buy-seat-configs/channel-member-periods-buy-seat-configs.component';
import {
    ChannelMemberExternalPeriodsChangeSeatConfigsComponent
} from './periods/change-seat-configs/channel-member-periods-change-seat-configs.component';
import {
    ChannelMemberExternalPeriodsChangeSeatLimitationsComponent
} from './periods/change-seat-limitations/channel-member-periods-change-seat-limitations.component';
import { ChannelMemberExternalPeriodsContainerComponent } from './periods/container/channel-member-periods-container.component';
import {
    ChannelMemberPeriodsNewMemberConfigsComponent
} from './periods/new-member-configs/channel-member-periods-new-member-configs.component';
import {
    ChannelMemberExternalPeriodsRenewalConfigsComponent
} from './periods/renewal-configs/channel-member-periods-renewal-configs.component';
import { ChannelMemberExternalPeriodsStepsComponent } from './periods/steps/channel-member-periods-steps.component';
import { MemberExternalRestrictionsComponent } from './restrictions/restrictions.component';
import { CapacitiesComponent } from './settings/capacities/capacities.component';
import { MemberExternalSettingsComponent } from './settings/container/member-external-settings.component';
import { PeriodicitiesComponent } from './settings/periodicities/periodicities.component';
import { RolesComponent } from './settings/roles/roles.component';
import { SubscriptionModesComponent } from './settings/subscription-modes/subscription-modes.component';

const routes: Routes = [{
    path: '',
    component: MemberExternalContainerComponent,
    canActivate: [roleGuard],
    data: {
        roles: [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CNL_MGR, UserRoles.ENT_ANS]
    },
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'options'
        },
        {
            path: 'options',
            component: ChannelMembersConfigComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.MEMBERS_CONFIG.TITLE'
            }
        },
        {
            path: 'renewal',
            component: ChannelMemberExternalPeriodsContainerComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                period: MemberPeriods.renewal,
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.RENEWAL.TITLE'
            },
            children: [
                {
                    path: 'steps',
                    component: ChannelMemberExternalPeriodsStepsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.renewal,
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS'
                    }
                },
                {
                    path: 'configurations',
                    component: ChannelMemberExternalPeriodsRenewalConfigsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.renewal,
                        breadcrumb: 'MEMBER_EXTERNAL.RENEWAL_CONFIGS.TITLE'
                    }
                },
                {
                    path: 'advanced',
                    component: ConfigurationsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.renewal,
                        breadcrumb: 'MEMBER_EXTERNAL.ADVANCED.TITLE'
                    }
                }
            ]
        },
        {
            path: 'change-seat',
            component: ChannelMemberExternalPeriodsContainerComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                period: MemberPeriods.change,
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.CHANGE_SEAT.TITLE'
            },
            children: [
                {
                    path: 'steps',
                    component: ChannelMemberExternalPeriodsStepsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.change,
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS'
                    }
                },
                {
                    path: 'limitations',
                    component: ChannelMemberExternalPeriodsChangeSeatLimitationsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.CHANGE_SEAT_LIMITS.TITLE'
                    }
                },
                {
                    path: 'configurations',
                    component: ChannelMemberExternalPeriodsChangeSeatConfigsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.renewal,
                        breadcrumb: 'MEMBER_EXTERNAL.CHANGE_SEAT_CONFIGS.TITLE'
                    }
                },
                {
                    path: 'advanced',
                    component: ConfigurationsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.change,
                        breadcrumb: 'MEMBER_EXTERNAL.ADVANCED.TITLE'
                    }
                }
            ]
        },
        {
            path: 'buy-seat',
            component: ChannelMemberExternalPeriodsContainerComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                period: MemberPeriods.buy,
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.BUY_SEAT.TITLE'
            },
            children: [
                {
                    path: 'steps',
                    component: ChannelMemberExternalPeriodsStepsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buy,
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS'
                    }
                },
                {
                    path: 'configurations',
                    component: ChannelMemberExternalPeriodsBuySeatConfigsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buy,
                        breadcrumb: 'MEMBER_EXTERNAL.BUY_SEAT_CONFIGS.TITLE'
                    }
                },
                {
                    path: 'advanced',
                    component: ConfigurationsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buy,
                        breadcrumb: 'MEMBER_EXTERNAL.ADVANCED.TITLE'
                    }
                }
            ]
        },
        {
            path: 'new-member',
            component: ChannelMemberExternalPeriodsContainerComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                period: MemberPeriods.buyNew,
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.NEW_MEMBER.TITLE'
            },
            children: [
                {
                    path: 'steps',
                    component: ChannelMemberExternalPeriodsStepsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buyNew,
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS'
                    }
                },
                {
                    path: 'configurations',
                    component: ChannelMemberPeriodsNewMemberConfigsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buyNew,
                        breadcrumb: 'MEMBER_EXTERNAL.NEW_MEMBER_CONFIGS.TITLE'
                    }
                },
                {
                    path: 'advanced',
                    component: ConfigurationsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        period: MemberPeriods.buyNew,
                        breadcrumb: 'MEMBER_EXTERNAL.ADVANCED.TITLE'
                    }
                }
            ]
        },
        {
            path: 'free-seat',
            component: FreeSeatOptionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.MEMBERS_CONFIG.FREE_SEAT_OPTIONS'
            }
        },
        {
            path: 'settings',
            component: MemberExternalSettingsComponent,
            data: {
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.SETTINGS'
            },
            children: [
                {
                    path: '',
                    pathMatch: 'full',
                    redirectTo: 'capacities'
                },
                {
                    path: 'capacities',
                    component: CapacitiesComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.CAPACITIES.TITLE'
                    }
                },
                {
                    path: 'roles',
                    component: RolesComponent,
                    data: {
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.ROLES.TITLE'
                    }
                },
                {
                    path: 'periodicities',
                    component: PeriodicitiesComponent,
                    data: {
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.PERIODICITIES.TITLE'
                    }
                },
                {
                    path: 'subscription-modes',
                    component: SubscriptionModesComponent,
                    data: {
                        breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.SUBSCRIPTION_MODES.TITLE'
                    }
                }
            ]
        },
        {
            path: 'restrictions',
            component: MemberExternalRestrictionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'MEMBER_EXTERNAL.RESTRICTIONS.TITLE'
            }
        },
        {
            path: 'availability-query',
            component: AvailabilityQueryComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.MEMBER_EXTERNAL.AVAILABILITY_QUERY.TITLE'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MemberExternalRoutingModule { }

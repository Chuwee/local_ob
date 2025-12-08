import { StateProperty } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { ChannelCapacities } from './models/capacities.model';
import { DynamicConfigurations } from './models/dynamic-configuration.model';
import { MemberDatesFilter } from './models/member-dates-filter.model';
import { MembershipPaymentInfo, MembersOptions, MembersPermissions } from './models/members-options';
import { NextMatch } from './models/next-match.model';
import { PeriodicityCommunications } from './models/periodicities.model';
import { RestrictionList, RestrictionStructure } from './models/restrictions.model';
import { RoleCommunications } from './models/roles.model';
import { SubscriptionMode, SubscriptionModeCommunications } from './models/subscription-modes.model';

@Injectable()
export class ChannelMemberExternalState {
    // Channel configurations
    readonly channelOptions = new StateProperty<MembersOptions>();
    readonly channelModes = new StateProperty<SubscriptionMode[]>();
    readonly channelCapacities = new StateProperty<ChannelCapacities>();
    readonly updateMapping = new StateProperty<void>();

    // Dynamic Configuration Channel Value
    readonly channelConfigurations = new StateProperty<DynamicConfigurations>();
    // Dynamic Configuration Structure
    readonly configurations = new StateProperty<DynamicConfigurations>();
    // Communications
    readonly modeCommunication = new StateProperty<SubscriptionModeCommunications>();
    readonly roleCommunication = new StateProperty<RoleCommunications>();
    readonly periodCommunication = new StateProperty<PeriodicityCommunications>();
    // AVET External
    readonly roles = new StateProperty<IdName[]>();
    readonly periodicities = new StateProperty<IdName[]>();
    readonly capacities = new StateProperty<IdName[]>();
    readonly terms = new StateProperty<IdName[]>();
    readonly nextMatches = new StateProperty<NextMatch[]>();
    // Channel Restrictions
    readonly restrictions = new StateProperty<RestrictionList>();
    readonly restrictionsStructure = new StateProperty<RestrictionStructure[]>();
    readonly restrictionsLoading = new StateProperty<Record<string, boolean>>({});
    // MEMBERS BATCH PRICES
    readonly updateMembersBatchPrices = new StateProperty<void>();
    // MEMBERS PERMISSIONS
    readonly membersPermissions = new StateProperty<MembersPermissions[]>();
    // MEMBERS PAYMENT INFO
    readonly membersPaymentInfo = new StateProperty<Partial<MembershipPaymentInfo>>();
    // MEMBERS DATES FILTER
    readonly datesFilter = new StateProperty<MemberDatesFilter>();
}

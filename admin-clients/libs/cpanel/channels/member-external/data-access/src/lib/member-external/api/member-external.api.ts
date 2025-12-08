import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelCapacities } from '../models/capacities.model';
import { DynamicConfiguration, DynamicConfigurations } from '../models/dynamic-configuration.model';
import { IgnoredSteps } from '../models/ignored-steps.model';
import { MemberDatesFilter } from '../models/member-dates-filter.model';
import { MemberPeriods, MembersOptions, MembersPermissions, MembershipPaymentInfo } from '../models/members-options';
import { NextMatch } from '../models/next-match.model';
import { PeriodicityCommunications } from '../models/periodicities.model';
import { RestrictionList, Restriction, RestrictionStructure } from '../models/restrictions.model';
import { RoleCommunications } from '../models/roles.model';
import { SubscriptionMode, SubscriptionModeCommunications } from '../models/subscription-modes.model';
import { Surcharges } from '../models/surchages.model';

@Injectable()
export class ChannelMemberExternalApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNEL_API = `${this.BASE_API}/mgmt-api/v1/channels`;

    constructor(private _http: HttpClient) { }

    getOptions(channelId: number): Observable<MembersOptions> {
        return this._http.get<MembersOptions>(this.url(channelId, 'member-config'));
    }

    putOptions(channelId: number, membersConfig: MembersOptions): Observable<void> {
        return this._http.put<void>(this.url(channelId, 'member-config'), membersConfig);
    }

    // SUBSCRIPTION MODES

    getSubscriptionModes(channelId: number): Observable<SubscriptionMode[]> {
        return this._http.get<SubscriptionMode[]>(this.url(channelId, 'member-config/subscription-modes'));
    }

    postSubscriptionMode(channelId: number, mode: SubscriptionMode): Observable<SubscriptionMode> {
        return this._http.post<SubscriptionMode>(this.url(channelId, 'member-config/subscription-modes'), mode);
    }

    putSubscriptionMode(channelId: number, sid: string, mode: Partial<SubscriptionMode>): Observable<SubscriptionMode> {
        return this._http.put<SubscriptionMode>(this.url(channelId, `member-config/subscription-modes/${sid}`), mode);
    }

    deleteSubscriptionMode(channelId: number, sid: string): Observable<unknown> {
        return this._http.delete(this.url(channelId, `member-config/subscription-modes/${sid}`));
    }

    // CHANNEL CAPACITIES

    getChannelCapacities(channelId: number): Observable<ChannelCapacities> {
        return this._http.get<ChannelCapacities>(this.url(channelId, 'member-config/capacities'));
    }

    putChannelCapacities(channelId: number, capacities: ChannelCapacities): Observable<void> {
        return this._http.put<void>(this.url(channelId, `member-config/capacities`), capacities);
    }

    updateMapping(channelId: number): Observable<void> {
        return this._http.post<void>(this.url(channelId, `capacity-mapping`), null);
    }

    // SUBSCRIPTION MODE COMMUNICATIONS

    getSubscriptionModeCommunications(channelId: number, sid: string): Observable<SubscriptionModeCommunications> {
        return this._http.get<SubscriptionModeCommunications>(
            this.url(channelId, `member-config/subscription-modes/${sid}/communications`)
        );
    }

    putSubscriptionModeCommunications(channelId: number, sid: string, communication: SubscriptionModeCommunications): Observable<void> {
        return this._http.put<void>(
            this.url(channelId, `member-config/subscription-modes/${sid}/communications`), communication
        );
    }

    // CHANNEL ROLE COMMUNICATIONS

    getRoleCommunications(channelId: number, id: number): Observable<RoleCommunications> {
        return this._http.get<RoleCommunications>(
            this.url(channelId, `member-config/roles/${id}/communications`)
        );
    }

    putRoleCommunications(channelId: number, id: number, communication: RoleCommunications): Observable<void> {
        return this._http.put<void>(
            this.url(channelId, `member-config/roles/${id}/communications`), communication
        );
    }

    // CHANNEL PERIDOCITY COMMUNICATIONS

    getPeriodicityCommunications(channelId: number, id: number): Observable<PeriodicityCommunications> {
        return this._http.get<PeriodicityCommunications>(
            this.url(channelId, `member-config/periodicities/${id}/communications`)
        );
    }

    putPeriodicityCommunications(channelId: number, id: number, communication: PeriodicityCommunications): Observable<void> {
        return this._http.put<void>(
            this.url(channelId, `member-config/periodicities/${id}/communications`), communication
        );
    }

    // CAPACITIES
    getCapacities(channelId: number): Observable<IdName[]> {
        return this._http.get<IdName[]>(this.url(channelId, 'capacities'));
    }

    // PERIODICITIES
    getPeriodicities(channelId: number): Observable<IdName[]> {
        return this._http.get<IdName[]>(this.url(channelId, 'periodicities'));
    }

    // TERMS
    getTerms(channelId: number): Observable<IdName[]> {
        return this._http.get<IdName[]>(this.url(channelId, 'terms'));
    }

    // NextMatches
    getNextMatches(channelId: number): Observable<NextMatch[]> {
        return this._http.get<NextMatch[]>(this.url(channelId, 'next-matches'));
    }

    // ROLES
    getRoles(channelId: number): Observable<IdName[]> {
        return this._http.get<IdName[]>(this.url(channelId, 'roles'));
    }

    // CONFIGURATIONS
    getConfigurations(): Observable<DynamicConfigurations> {
        return this._http.get<DynamicConfigurations>(`${this.CHANNEL_API}/member-config/dynamic-configuration`);
    }

    getChannelConfigurations(channelId: number): Observable<DynamicConfigurations> {
        return this._http.get<DynamicConfigurations>(this.url(channelId, 'member-config/dynamic-configuration'));
    }

    putChannelConfiguration(channelId: number, configuration: DynamicConfiguration): Observable<void> {
        return this._http.put<void>(
            this.url(channelId, `member-config/dynamic-configuration/${configuration.operation_name}`),
            configuration
        );
    }

    // IGNORED STEPS
    getIgnoredSteps(channelId: number, orderType: MemberPeriods): Observable<IgnoredSteps> {
        return this._http.get<IgnoredSteps>(this.url(channelId, `member-config/${orderType}/ignored-steps`));
    }

    putIgnoredSteps(channelId: number, orderType: MemberPeriods, ignoredSteps: IgnoredSteps): Observable<void> {
        return this._http.put<void>(this.url(channelId, `member-config/${orderType}/ignored-steps`), ignoredSteps);
    }

    // SURCHARGES

    putSurcharges(channelId: number, charges: Surcharges): Observable<void> {
        return this._http.put<void>(this.url(channelId, `member-config-charges`), { charges });
    }

    // RESTRICTIONS

    getRestrictions(channelId: number): Observable<RestrictionList> {
        return this._http.get<RestrictionList>(this.url(channelId, `member-config/restrictions`));
    }

    getRestriction(channelId: number, sid: string): Observable<Restriction> {
        return this._http.get<Restriction>(this.url(channelId, `member-config/restrictions/${sid}`));
    }

    postRestriction(channelId: number, restriction: Partial<Restriction>): Observable<void> {
        return this._http.post<void>(this.url(channelId, `member-config/restrictions`), restriction);
    }

    putRestriction(channelId: number, restriction: Partial<Restriction>): Observable<void> {
        const sid = restriction?.sid;
        return this._http.put<void>(this.url(channelId, `member-config/restrictions/${sid}`), restriction);
    }

    deleteRestriction(channelId: number, restriction: Partial<Restriction> | string): Observable<void> {
        const sid = typeof restriction === 'string' ? restriction : restriction?.sid;
        return this._http.delete<void>(this.url(channelId, `member-config/restrictions/${sid}`));
    }

    getRestrictionsStructure(): Observable<RestrictionStructure[]> {
        return this._http.get<RestrictionStructure[]>(`${this.CHANNEL_API}/member-config/restrictions-dynamic-configuration`);
    }

    // MEMBERS BATCH PRICES
    updateMembersBatchPrices(channelId: number): Observable<void> {
        return this._http.put<void>(this.url(channelId, `members-batch-prices`), null);
    }

    //MEMBERS PERMISSIONS
    getMembersPermissions(channelId: number): Observable<MembersPermissions[]> {
        return this._http.get<MembersPermissions[]>(this.url(channelId, 'permissions'));
    }

    // MEMBERS allowed unpaid term id
    putMembershipPaymentInfo(channelId: number, body: Partial<MembershipPaymentInfo>): Observable<void> {
        return this._http.put<void>(this.url(channelId, `member-config/membership`), body);
    }

    // MEMBERS dates filter
    getMembersDatesFilter(channelId: number, period: MemberPeriods): Observable<MemberDatesFilter> {
        return this._http.get<MemberDatesFilter>(this.url(channelId, `member-config/dates-filter/${period}`));
    }

    updateMembersDatesFilter(channelId: number, period: MemberPeriods, datesFilter: MemberDatesFilter): Observable<void> {
        return this._http.put<void>(this.url(channelId, `member-config/dates-filter/${period}`), datesFilter);
    }

    private readonly url = (id: number, suffix = ''): string => `${this.CHANNEL_API}/${id}/${suffix}`;

}

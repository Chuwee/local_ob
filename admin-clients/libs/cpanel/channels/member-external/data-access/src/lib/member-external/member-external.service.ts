import { StateManager } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, filter, finalize, firstValueFrom, map, Observable, tap, throwError } from 'rxjs';
import { ChannelMemberExternalApi } from './api/member-external.api';
import { ChannelMemberExternalState } from './member-external.state';
import { ChannelCapacities } from './models/capacities.model';
import { DynamicConfiguration } from './models/dynamic-configuration.model';
import { MemberDatesFilter } from './models/member-dates-filter.model';
import { MemberPeriods, MembersOptions, MembershipPaymentInfo } from './models/members-options';
import { PeriodicityCommunications } from './models/periodicities.model';
import { RestrictionListElem, RestrictionType } from './models/restrictions.model';
import { RoleCommunications } from './models/roles.model';
import { SubscriptionMode, SubscriptionModeCommunications } from './models/subscription-modes.model';
import { Surcharges } from './models/surchages.model';

const compareBySid = (a: RestrictionListElem, b: RestrictionListElem): boolean => a.sid === b.sid;

@Injectable()
export class ChannelMemberExternalService {
    private readonly _api = inject(ChannelMemberExternalApi);
    private readonly _state = inject(ChannelMemberExternalState);

    readonly roles = Object.freeze({
        load: (channelId: number): void => StateManager.loadIfNull(
            this._state.roles,
            this._api.getRoles(channelId)
        ),
        get$: () => this._state.roles.getValue$(),
        error$: () => this._state.roles.getError$(),
        loading$: () => this._state.roles.isInProgress$(),
        clear: () => this._state.roles.setValue(null)
    });

    readonly nextMatches = Object.freeze({
        load: (channelId: number): void => StateManager.loadIfNull(
            this._state.nextMatches,
            this._api.getNextMatches(channelId)
        ),
        get$: () => this._state.nextMatches.getValue$(),
        error$: () => this._state.nextMatches.getError$(),
        loading$: () => this._state.nextMatches.isInProgress$(),
        clear: () => this._state.nextMatches.setValue(null)
    });

    readonly terms = Object.freeze({
        load: (channelId: number): void => StateManager.loadIfNull(
            this._state.terms,
            this._api.getTerms(channelId)
        ),
        get$: () => this._state.terms.getValue$(),
        error$: () => this._state.terms.getError$(),
        loading$: () => this._state.terms.isInProgress$(),
        clear: () => this._state.terms.setValue(null)
    });

    readonly putMembershipPaymentInfo = Object.freeze({
        update: (channelId: number, body: Partial<MembershipPaymentInfo>) =>
            StateManager.inProgress(
                this._state.membersPaymentInfo,
                this._api.putMembershipPaymentInfo(channelId, body).pipe(
                    tap(() => this._state.membersPaymentInfo.setValue(body))
                )
            )
    });

    readonly datesFilter = Object.freeze({
        load: (channelId: number, period: MemberPeriods): void => StateManager.load(
            this._state.datesFilter,
            this._api.getMembersDatesFilter(channelId, period)
        ),
        get$: () => this._state.datesFilter.getValue$(),
        update: (channelId: number, period: MemberPeriods, datesFilter: MemberDatesFilter) =>
            StateManager.inProgress(
                this._state.datesFilter,
                this._api.updateMembersDatesFilter(channelId, period, datesFilter).pipe(
                    tap(() => this._state.datesFilter.setValue(datesFilter))
                )
            ),
        error$: () => this._state.datesFilter.getError$(),
        loading$: () => this._state.datesFilter.isInProgress$(),
        clear: () => this._state.datesFilter.setValue(null)
    });

    readonly capacities = Object.freeze({
        load: (channelId: number) => StateManager.loadIfNull(
            this._state.capacities,
            this._api.getCapacities(channelId)
        ),
        get$: () => this._state.capacities.getValue$(),
        error$: () => this._state.capacities.getError$(),
        loading$: () => this._state.capacities.isInProgress$(),
        clear: () => this._state.capacities.setValue(null)
    });

    readonly periodicities = Object.freeze({
        load: (channelId: number) => StateManager.loadIfNull(
            this._state.periodicities,
            this._api.getPeriodicities(channelId)
        ),
        get$: () => this._state.periodicities.getValue$(),
        error$: () => this._state.periodicities.getError$(),
        loading$: () => this._state.periodicities.isInProgress$(),
        clear: () => this._state.periodicities.setValue(null)
    });

    readonly period = Object.freeze({
        communication: Object.freeze({
            load: (channelId: number, id: number) => StateManager.load(
                this._state.periodCommunication,
                this._api.getPeriodicityCommunications(channelId, id)
            ),
            save: (channelId: number, id: number, comms: PeriodicityCommunications) =>
                StateManager.inProgress(
                    this._state.periodCommunication,
                    this._api.putPeriodicityCommunications(channelId, id, comms).pipe(
                        tap(() => this._state.periodCommunication.setValue(comms))
                    )
                ),
            get$: () => this._state.periodCommunication.getValue$(),
            error$: () => this._state.periodCommunication.getError$(),
            loading$: () => this._state.periodCommunication.isInProgress$(),
            clear: () => this._state.periodCommunication.setValue(null)
        })
    });

    readonly role = Object.freeze({
        communication: Object.freeze({
            load: (channelId: number, id: number) => StateManager.load(
                this._state.roleCommunication,
                this._api.getRoleCommunications(channelId, id)
            ),
            save: (channelId: number, id: number, comms: RoleCommunications) => StateManager.inProgress(
                this._state.roleCommunication,
                this._api.putRoleCommunications(channelId, id, comms).pipe(
                    tap(() => this._state.roleCommunication.setValue(comms))
                )
            ),
            get$: () => this._state.roleCommunication.getValue$(),
            error$: () => this._state.roleCommunication.getError$(),
            loading$: () => this._state.roleCommunication.isInProgress$(),
            clear: () => this._state.roleCommunication.setValue(null)
        })
    });

    readonly subscription = Object.freeze({
        list: Object.freeze({
            load: (channelId: number) => StateManager.load(
                this._state.channelModes,
                this._api.getSubscriptionModes(channelId)
            ),
            get$: () => this._state.channelModes.getValue$(),
            error$: () => this._state.channelModes.getError$(),
            loading$: () => this._state.channelModes.isInProgress$(),
            clear: () => this._state.channelModes.setValue(null)
        }),

        create: (channelId: number, mode: SubscriptionMode): Observable<SubscriptionMode> => StateManager.inProgress(
            this._state.channelModes,
            this._api.postSubscriptionMode(channelId, mode).pipe(
                tap(() => StateManager.addElement(this._state.channelModes, mode))
            )
        ),

        update: (channelId: number, sid: string, mode: SubscriptionMode): Observable<SubscriptionMode> =>
            StateManager.inProgress(
                this._state.channelModes,
                this._api.putSubscriptionMode(channelId, sid, mode).pipe(
                    tap(() => StateManager.updateElement(this._state.channelModes, mode, compareBySid))
                )
            ),

        delete: (channelId: number, mode: SubscriptionMode): Observable<unknown> =>
            StateManager.inProgress(
                this._state.channelModes,
                this._api.deleteSubscriptionMode(channelId, mode.sid).pipe(
                    tap(() => StateManager.deleteElement(this._state.channelModes, mode, compareBySid))
                )
            ),

        communication: Object.freeze({
            load: (channelId: number, sid: string) => StateManager.load(
                this._state.modeCommunication,
                this._api.getSubscriptionModeCommunications(channelId, sid)
            ),
            save: (channelId: number, sid: string, comms: SubscriptionModeCommunications) =>
                StateManager.inProgress(
                    this._state.modeCommunication,
                    this._api.putSubscriptionModeCommunications(channelId, sid, comms).pipe(
                        tap(() => this._state.modeCommunication.setValue(comms))
                    )
                ),
            get$: () => this._state.modeCommunication.getValue$(),
            error$: () => this._state.modeCommunication.getError$(),
            loading$: () => this._state.modeCommunication.isInProgress$(),
            clear: () => this._state.modeCommunication.setValue(null)
        })

    });

    readonly configurations = Object.freeze({
        load: (): void =>
            StateManager.loadIfNull(
                this._state.configurations,
                this._api.getConfigurations()
            ),
        implementations$: (operation: string) =>
            this._state.configurations.getValue$().pipe(
                map(configurations => configurations.filter(elem => elem.operation_name === operation)),
                map(configurations => configurations.map(elem => elem.implementation))
            ),
        fields$: (implementation: string) =>
            this._state.configurations.getValue$().pipe(
                map(configurations => configurations.find(elem => elem.implementation === implementation)),
                map(configurations => configurations.fields)
            ),
        get$: () => this._state.configurations.getValue$(),
        error$: () => this._state.configurations.getError$(),
        loading$: () => this._state.configurations.isInProgress$(),
        clear: () => this._state.configurations.setValue(null)
    });

    readonly channelConfigurations = Object.freeze({
        load: (channelId: number): void =>
            StateManager.loadIfNull(
                this._state.channelConfigurations,
                this._api.getChannelConfigurations(channelId)
            ),
        reload: (channelId: number): void =>
            StateManager.load(
                this._state.channelConfigurations,
                this._api.getChannelConfigurations(channelId)
            ),
        save: (channelId: number, configuration: DynamicConfiguration) =>
            StateManager.inProgress(
                this._state.channelConfigurations,
                this._api.putChannelConfiguration(channelId, configuration)
            ).pipe(
                finalize(() => this.channelConfigurations.reload(channelId))
            ),
        get$: () => this._state.channelConfigurations.getValue$(),
        error$: () => this._state.channelConfigurations.getError$(),
        loading$: () => this._state.channelConfigurations.isInProgress$(),
        clear: () => this._state.channelConfigurations.setValue(null)
    });

    readonly channelOptions = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._state.channelOptions,
            this._api.getOptions(channelId)
        ),
        save: (channelId: number, options: MembersOptions) => StateManager.inProgress(
            this._state.channelOptions,
            this._api.putOptions(channelId, options)
        ),
        saveCharges: (channelId: number, surcharges: Surcharges) =>
            StateManager.inProgress(this._state.channelOptions, this._api.putSurcharges(channelId, surcharges)),
        get$: () => this._state.channelOptions.getValue$(),
        error$: () => this._state.channelOptions.getError$(),
        loading$: () => this._state.channelOptions.isInProgress$(),
        clear: () => this._state.channelOptions.setValue(null)
    });

    readonly channelCapacities = Object.freeze({
        load: (channelId: number) => StateManager.loadIfNull(
            this._state.channelCapacities,
            this._api.getChannelCapacities(channelId)
        ),
        save: (channelId: number, capacities: ChannelCapacities) => StateManager.inProgress(
            this._state.channelCapacities,
            this._api.putChannelCapacities(channelId, capacities).pipe(
                tap(() => this._state.channelCapacities.setValue(capacities))
            )
        ),
        get$: () => this._state.channelCapacities.getValue$(),
        error$: () => this._state.channelCapacities.getError$(),
        loading$: () => this._state.channelCapacities.isInProgress$(),
        clear: () => this._state.channelCapacities.setValue(null),
        updateMapping: (channelId: number) => StateManager.inProgress(
            this._state.updateMapping,
            this._api.updateMapping(channelId)
        ),
        updateMappingInProgress$: () => this._state.updateMapping.isInProgress$()
    });

    readonly restrictions = Object.freeze({
        structure: Object.freeze({
            load: () => StateManager.loadIfNull(
                this._state.restrictionsStructure,
                this._api.getRestrictionsStructure()
            ),
            get$: () => this._state.restrictionsStructure.getValue$(),
            fields$: (type: RestrictionType) => this._state.restrictionsStructure.getValue$().pipe(
                filter(structures => !!structures),
                map(structures => structures?.find(struct => struct.restriction_type === type)),
                map(structure => structure?.fields)
            ),
            error$: () => this._state.restrictionsStructure.getError$(),
            loading$: () => this._state.restrictionsStructure.isInProgress$()
        }),
        list: Object.freeze({
            load: (channelId: number) => StateManager.loadIfNull(
                this._state.restrictions,
                this._api.getRestrictions(channelId)
            ),
            get$: () => this._state.restrictions.getValue$(),
            error$: () => this._state.restrictions.getError$(),
            loading$: () => this._state.restrictions.isInProgress$()
        }),
        clear: () => {
            this._state.restrictions.setValue(null);
            this._state.restrictionsLoading.setValue({});
        },
        create: (channelId: number, restriction: RestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.postRestriction(channelId, restriction).pipe(
                tap(() => StateManager.addElement(this._state.restrictions, restriction))
            )
        ),
        update: (channelId: number, restriction: RestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.putRestriction(channelId, restriction).pipe(
                tap(() => StateManager.updateElement<RestrictionListElem>(this._state.restrictions, restriction, compareBySid))
            )
        ),
        delete: (channelId: number, restriction: RestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.deleteRestriction(channelId, restriction).pipe(
                tap(() => StateManager.deleteElement<RestrictionListElem>(this._state.restrictions, restriction, compareBySid))
            )
        ),
        load: (channelId: number, restriction: RestrictionListElem) => StateManager.inProgress(
            async loading => {
                const state = this._state.restrictionsLoading;
                const value = await firstValueFrom(state.getValue$());
                state.setValue({ ...value, [restriction.sid]: loading });
            },
            this._api.getRestriction(channelId, restriction.sid).pipe(
                tap(restriction => this.restrictions.set({ ...restriction, loaded: true })),
                catchError((err: HttpErrorResponse) => {
                    if (err?.status === 404) {
                        StateManager.deleteElement<RestrictionListElem>(this._state.restrictions, restriction, compareBySid);
                    }
                    return throwError(() => err);
                })
            )
        ),
        set: (update: RestrictionListElem) => {
            StateManager.updateElement<RestrictionListElem>(this._state.restrictions, update, compareBySid);
        },
        loading$: () => this._state.restrictionsLoading.getValue$()
    });

    readonly membersBatchPrices = Object.freeze({
        update: (channelId: number) => StateManager.inProgress(
            this._state.updateMembersBatchPrices,
            this._api.updateMembersBatchPrices(channelId)
        ),
        loading$: () => this._state.updateMembersBatchPrices.isInProgress$()
    });

    readonly membersPermissions = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._state.membersPermissions,
            this._api.getMembersPermissions(channelId)
        ),
        get$: () => this._state.membersPermissions.getValue$(),
        loading$: () => this._state.membersPermissions.isInProgress$()
    });
}

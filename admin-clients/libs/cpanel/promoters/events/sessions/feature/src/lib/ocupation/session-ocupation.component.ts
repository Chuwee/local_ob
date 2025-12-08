import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, computed, effect, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { VmSessionAvailability } from './models/vm-session-availability.model';

interface CapacityGroup {
    name: string;
    data: VmSessionAvailability[];
    totals: VmSessionAvailability;
    hidePZ: boolean;
}

@Component({
    selector: 'app-session-ocupation',
    templateUrl: './session-ocupation.component.html',
    styleUrls: ['./session-ocupation.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionOcupationComponent implements OnDestroy {
    readonly #authSrv = inject(AuthenticationService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #route = inject(ActivatedRoute);

    #generalBreakdownColumns: string[];
    #collapsedBreakdownColumns: string[];
    #expandedBreakdownColumns: string[];
    #isTieredEvent: boolean;// not set

    busyBreakdownColumns: string[];
    breakdownColumns: string[];
    columnsToDisplay: string[];

    readonly type = this.#route.snapshot.data['type'];
    readonly $event = toSignal(this.#eventsSrv.event.get$().pipe(filter(Boolean)));
    readonly $session = toSignal(this.#sessionsSrv.session.get$().pipe(filter(Boolean)));
    readonly $priceTypeAvailability = toSignal(this.#sessionsSrv.getPriceTypeAvailability$().pipe(filter(Boolean)));
    readonly $loading = toSignal(booleanOrMerge([
        this.#sessionsSrv.isSessionTiersAvailabilityInProgress$(),
        this.#sessionsSrv.isPriceTypeAvailabilityInProgress$()
    ]));

    readonly $isFeverZoneEnabled = toSignal(
        this.#authSrv.getLoggedUser$()
            .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.FV_REPORTING])))
    );

    readonly $capacityGroups = computed(() => {
        const priceTypeAvailability = this.$priceTypeAvailability();
        const event = this.$event();

        if (event && priceTypeAvailability) {
            const isActivityEvent = event.type === EventType.activity || event.type === EventType.themePark;
            const capacityGroupsMap = priceTypeAvailability
                .reduce<{
                    [key: string]: {
                        name: string;
                        data: VmSessionAvailability[];
                        totals: VmSessionAvailability;
                    };
                }>((capacityGroups, pta) => {
                    const groupId = pta.quota?.id ||
                        (pta.ticket_type === ActivityTicketType.group && 'group') ||
                        'total';
                    if (!capacityGroups[groupId]) {
                        capacityGroups[groupId] = {
                            name: pta.quota?.name ||
                                (pta.ticket_type === ActivityTicketType.group && 'group') ||
                                'total',
                            data: [],
                            totals: null
                        };
                    }
                    if (pta.price_type) {
                        capacityGroups[groupId].data.push({
                            price_type: pta.price_type.name,
                            ...pta.availability,
                            busy: this.busyBreakdownColumns
                                .reduce((counter, item) => counter + (pta.availability[item] as number || 0), 0),
                            total: pta.availability.total.value
                        });
                    } else {
                        capacityGroups[groupId].totals = {
                            ...pta.availability,
                            busy: this.busyBreakdownColumns
                                .reduce((counter, item) => counter + (pta.availability[item] as number || 0), 0),
                            total: pta.availability.total.value
                        };
                    }

                    return capacityGroups;
                }, {});

            const capacityGroups = [
                ...Object.keys(capacityGroupsMap)
                    .sort((a, b) => (a === 'total' && -1) || (b === 'total' && 1) || 0)
                    .map(groupId => ({
                        name: capacityGroupsMap[groupId].name,
                        data: capacityGroupsMap[groupId].data,
                        totals: capacityGroupsMap[groupId].totals,
                        hidePZ: groupId !== 'total'
                    } as CapacityGroup))
            ];

            if (!isActivityEvent && capacityGroups.length === 2) {
                capacityGroups.pop();
            }

            return capacityGroups;
        } else {
            return [];
        }

    });

    constructor() {
        effect(() => {
            const event = this.$event();
            const session = this.$session();
            if (event && session) {
                this.#setTablesColumns(event.type);
                this.#sessionsSrv.loadPriceTypeAvailability(event.id, session.id);
                if (event?.settings?.use_tiered_pricing) {
                    this.#sessionsSrv.loadSessionTiersAvailability(event.id, session.id);
                }
            }
        });
    }

    ngOnDestroy(): void {
        this.#sessionsSrv.clearSessionTiersAvailability();
    }

    switchDisplayedColumns(columnsToDisplay: string[]): void {
        if (columnsToDisplay === this.#collapsedBreakdownColumns) {
            this.columnsToDisplay = this.#expandedBreakdownColumns;
        } else {
            this.columnsToDisplay = this.#collapsedBreakdownColumns;
        }
    }

    refresh(): void {
        this.#sessionsSrv.loadPriceTypeAvailability(this.$event()?.id, this.$session()?.id);
        if (this.#isTieredEvent) {
            this.#sessionsSrv.loadSessionTiersAvailability(this.$event()?.id, this.$session()?.id);
        }
    }

    #setTablesColumns(eventType: EventType): void {
        this.#generalBreakdownColumns = ['available'];
        if (eventType !== EventType.activity && eventType !== EventType.themePark) {
            this.#generalBreakdownColumns.push('promoter_blocked', 'kill', 'session_pack');
        }
        this.busyBreakdownColumns = ['purchase', 'invitation', 'booking', 'issue', 'in_progress'];
        this.#collapsedBreakdownColumns = ['price_type', ...this.#generalBreakdownColumns, 'busy', 'total'];
        this.#expandedBreakdownColumns = ['price_type', ...this.#generalBreakdownColumns, ...this.busyBreakdownColumns, 'total'];
        this.breakdownColumns = [...this.#generalBreakdownColumns, ...this.busyBreakdownColumns, 'busy', 'total'];
        this.columnsToDisplay = this.#collapsedBreakdownColumns;
    }
}

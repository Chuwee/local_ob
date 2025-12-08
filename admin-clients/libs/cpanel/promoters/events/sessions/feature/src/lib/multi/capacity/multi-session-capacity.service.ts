import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionCapacityService } from '@admin-clients/shared/venues/data-access/session-standard-venue-tpls';
import {
    NotNumberedZone, Seat, StdVenueTplsApi, VenueMap, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';
import { catchError, finalize, Observable, tap } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { MultiSessionCapacityState } from './state/multi-session-capacity.state';

@Injectable()
export class MultiSessionCapacityService implements VenueMapService {

    constructor(
        private _sessionsService: EventSessionsService,
        private _sessionCapacitySrv: SessionCapacityService,
        private _multiSessionCapacityState: MultiSessionCapacityState,
        private _stdVenueTplApi: StdVenueTplsApi
    ) { }

    loadVenueMap({ tplId }: { tplId: number }): void {
        this._multiSessionCapacityState.venueMap.setInProgress(true);
        this._stdVenueTplApi.getVenueTplVenueMap(tplId)
            .pipe(
                tap(venueMap =>
                    venueMap?.sectors?.forEach(sector => {
                        sector.rows?.forEach(row => {
                            row.seats.forEach(seat => {
                                seat.status = null;
                                seat.blockingReason = null;
                                seat.priceType = null;
                                seat.quota = null;
                                seat.visibility = null;
                                seat.accessibility = null;
                                seat.gate = null;
                            });
                        });
                        sector.notNumberedZones.forEach(zone => {
                            zone.record = null;
                            zone.statusCounters = [];
                            zone.blockingReasonCounters = null;
                            zone.priceType = null;
                            zone.quotaCounters = null;
                            zone.visibility = null;
                            zone.accessibility = null;
                            zone.gate = null;
                        });
                    })
                ),
                finalize(() => this._multiSessionCapacityState.venueMap.setInProgress(false))
            )
            .subscribe(venueMap => this._multiSessionCapacityState.venueMap.setValue(venueMap));
    }

    getVenueMap$(): Observable<VenueMap> {
        return this._multiSessionCapacityState.venueMap.getValue$();
    }

    clearVenueMap(): void {
        this._multiSessionCapacityState.venueMap.setValue(null);
    }

    isVenueMapLoading$(): Observable<boolean> {
        return this._multiSessionCapacityState.venueMap.isInProgress$();
    }

    updateVenueMap(_: unknown, seats: Seat[], nnzs: NotNumberedZone[]): Observable<boolean> {
        return this._sessionsService.getSelectedSessions$()
            .pipe(
                switchMap(sessionWrappers => {
                    const eventId = sessionWrappers.find(Boolean).session.event.id;
                    const sessionIds = sessionWrappers.map(sessionWrapper => sessionWrapper.session.id);
                    sessionIds.forEach(sessionId => this._sessionsService.initSessionCapacityUpdateState(sessionId));
                    return this._sessionCapacitySrv.updateSessionVenueMapBulk(eventId, sessionIds, seats, nnzs)
                        .pipe(
                            catchError(error => {
                                sessionIds.forEach(sessionId => this._sessionsService.deleteSessionCapacityUpdate(sessionId));
                                throw error;
                            })
                        );
                }),
                map(() => null)
            );
    }

    get isCapacityUpdateAsync(): boolean {
        return true;
    }

    isVenueMapSaving$(): Observable<boolean> {
        return this._sessionCapacitySrv.isVenueMapSaving$();
    }
}

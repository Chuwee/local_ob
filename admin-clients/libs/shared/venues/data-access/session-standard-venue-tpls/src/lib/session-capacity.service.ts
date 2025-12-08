import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    NotNumberedZone, Seat, SessionPackService, VenueMap, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { inject, Injectable } from '@angular/core';
import { bufferCount, catchError, concat, filter, map, mapTo, Observable, switchMap, take } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { SessionCapacityApi } from './api/session-capacity.api';
import { SessionCapacityState } from './state/session-capacity.state';

@Injectable()
export class SessionCapacityService implements VenueMapService, SessionPackService {

    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _sessionCapacityState = inject(SessionCapacityState);
    private readonly _sessionCapacityApi = inject(SessionCapacityApi);

    private readonly _capacityUpdateMaxLength = 5000;

    loadVenueMap({ eventId, sessionId, updatingCapacity }: { eventId: number; sessionId: number; updatingCapacity: boolean }): void {
        this._sessionCapacityState.venueMap.isInProgress$()
            .pipe(
                take(1),
                filter(saving => !saving && !updatingCapacity),
                switchMap(() => {
                    this._sessionCapacityState.venueMap.setInProgress(true);
                    return this._sessionCapacityApi.getSessionVenueMap(eventId, sessionId);
                }),
                finalize(() => this._sessionCapacityState.venueMap.setInProgress(false))
            )
            .subscribe(venueMap => this._sessionCapacityState.venueMap.setValue(venueMap));
    }

    getVenueMap$(): Observable<VenueMap> {
        return this._sessionCapacityState.venueMap.getValue$();
    }

    clearVenueMap(): void {
        this._sessionCapacityState.venueMap.setValue(null);
    }

    isVenueMapLoading$(): Observable<boolean> {
        return this._sessionCapacityState.venueMap.isInProgress$();
    }

    updateVenueMap(
        { eventId, sessionId }: { eventId: number; sessionId: number }, seats: Seat[], nnzs: NotNumberedZone[]
    ): Observable<boolean> {
        this._sessionsService.initSessionCapacityUpdateState(sessionId);
        return this.updateSessionVenueMap(eventId, sessionId, seats, nnzs)
            .pipe(
                catchError(error => {
                    this._sessionsService.deleteSessionCapacityUpdate(sessionId);
                    throw error;
                }),
                map(() => true)
            );
    }

    get isCapacityUpdateAsync(): boolean {
        return true;
    }

    isVenueMapSaving$(): Observable<boolean> {
        return this._sessionsService.isVenueMapSaving$();
    }

    updateSessionVenueMapBulk(eventId: number, sessionIds: number[], seats: Seat[], nnzs: NotNumberedZone[]): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        const puts: Observable<void>[] = [];
        if (seats?.length) {
            for (let i = 0; i < seats.length; i += this._capacityUpdateMaxLength) {
                const endIndex = seats.length < i + this._capacityUpdateMaxLength ? seats.length : i + this._capacityUpdateMaxLength;
                puts.push(this._sessionCapacityApi.putSeatsBulk(eventId, sessionIds, seats.slice(i, endIndex)));
            }
        }

        const nnzGroups: NotNumberedZone[][] = this.divideNNZForReq(nnzs);
        nnzGroups.forEach(nnzs => puts.push(this._sessionCapacityApi.putNotNumberedZonesBulk(eventId, sessionIds, nnzs)));

        return concat(...puts)
            .pipe(
                bufferCount(puts.length),
                map(() => null),
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    //SESSION PACK

    linkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota: string // quota id
    ): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        return this._sessionCapacityApi.postLinkSeatsToSessionPack(eventId, sessionId, seatIds, target, quota && Number(quota) || null)
            .pipe(
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    unlinkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota: string // quota id
    ): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        return this._sessionCapacityApi.postUnlinkSeatsToSessionPack(eventId, sessionId, seatIds, target, quota && Number(quota) || null)
            .pipe(
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    linkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        return this._sessionCapacityApi.postLinkNnzToSessionPack(eventId, sessionId, nnzId, source, target, count)
            .pipe(
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    unlinkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        return this._sessionCapacityApi.postUnlinkNnzToSessionPack(eventId, sessionId, nnzId, source, target, count)
            .pipe(
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    isLinkToSessionPackSaving$(): Observable<boolean> {
        return this._sessionsService.isVenueMapSaving$();
    }

    private updateSessionVenueMap(eventId: number, sessionId: number, seats: Seat[], nnzs: NotNumberedZone[]): Observable<void> {
        this._sessionsService.setVenueMapSaving(true);
        const puts: Observable<void>[] = [];
        if (seats?.length) {
            for (let i = 0; i < seats.length; i += this._capacityUpdateMaxLength) {
                const endIndex = seats.length < i + this._capacityUpdateMaxLength ? seats.length : i + this._capacityUpdateMaxLength;
                puts.push(this._sessionCapacityApi.putSeats(eventId, sessionId, seats.slice(i, endIndex)));
            }
        }

        const nnzGroups: NotNumberedZone[][] = this.divideNNZForReq(nnzs);
        nnzGroups.forEach(nnzs => puts.push(this._sessionCapacityApi.putNotNumberedZones(eventId, sessionId, nnzs)));

        return concat(...puts)
            .pipe(
                bufferCount(puts.length),
                mapTo(null),
                finalize(() => this._sessionsService.setVenueMapSaving(false))
            );
    }

    private divideNNZForReq(nnzs: NotNumberedZone[]): NotNumberedZone[][] {
        const nnzGroups: NotNumberedZone[][] = [];
        if (nnzs?.length) {
            // Divide zones in groups, each group it's a different request
            nnzs.forEach(nnz => {
                const nnzCapacityToUpdate = this.getNNZCapacityToUpdate(nnz);
                let pushed = false;
                // search a group to fit the current nnz
                nnzGroups.forEach(nnzGroup => {
                    if (!nnzGroup.length ||
                        nnzGroup.map(n => this.getNNZCapacityToUpdate(n)).reduce((p, c) => p + c) + nnzCapacityToUpdate
                        < this._capacityUpdateMaxLength
                    ) {
                        pushed = true;
                        nnzGroup.push(nnz);
                    }
                });
                // If doesn't fit in any group, it creates a new group (new call)
                if (!pushed) {
                    nnzGroups.push([nnz]);
                }
            });
        }
        return nnzGroups;
    }

    private getNNZCapacityToUpdate(nnz: NotNumberedZone): number {
        return Math.max(
            nnz.priceType || nnz.accessibility || nnz.visibility || nnz.gate ? nnz.capacity : 0,
            ...(nnz?.statusCounters?.filter(sc => sc.source).map(sc => sc.count) || []),
            ...(nnz?.blockingReasonCounters?.filter(brc => brc.source).map(brc => brc.count) || []),
            ...(nnz?.quotaCounters?.filter(qc => qc.source).map(qc => qc.count) || [])
        );
    }
}

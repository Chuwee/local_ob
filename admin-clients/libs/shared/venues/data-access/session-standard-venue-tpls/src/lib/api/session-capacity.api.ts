import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { decodeAndMapVenueMap, NotNumberedZone, Seat, VenueMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class SessionCapacityApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;

    private readonly _http = inject(HttpClient);

    getSessionVenueMap(eventId: number, sessionId: number): Observable<VenueMap> {
        return this._http.get(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity`,
            {
                headers: new HttpHeaders({ accept: 'application/x-protobuf' }),
                responseType: 'arraybuffer'
            })
            .pipe(map((arrayBuffer: ArrayBuffer) => decodeAndMapVenueMap(arrayBuffer)));
    }

    putSeats(eventId: number, sessionId: number, seats: Seat[]): Observable<void> {
        const seatsToSave = seats.map(seat => ({
            id: seat.id,
            status: seat.status,
            blocking_reason: seat.blockingReason !== -1 ? seat.blockingReason : null,
            price_type: seat.priceType,
            quota: seat.quota,
            visibility: seat.visibility,
            accessibility: seat.accessibility,
            gate: seat.gate,
            gate_update_type: seat.gateUpdateType,
            dynamic_tag1: seat.firstCustomTag > 0 ? seat.firstCustomTag : undefined,
            dynamic_tag2: seat.secondCustomTag > 0 ? seat.secondCustomTag : undefined
        }));
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/seats`, seatsToSave);
    }

    putNotNumberedZones(eventId: number, sessionId: number, nnzs: NotNumberedZone[]): Observable<void> {
        const zonesToSave = nnzs.map(nnz => ({
            id: nnz.id,
            status_counters: nnz.statusCounters,
            blocking_reason_counters: nnz.blockingReasonCounters,
            quota_counters: nnz.quotaCounters,
            price_type: nnz.priceType,
            visibility: nnz.visibility,
            accessibility: nnz.accessibility,
            gate: nnz.gate,
            gate_update_type: nnz.gateUpdateType,
            dynamic_tag1: nnz.firstCustomTag > 0 ? nnz.firstCustomTag : undefined,
            dynamic_tag2: nnz.secondCustomTag > 0 ? nnz.secondCustomTag : undefined
        }));
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/not-numbered-zones`, zonesToSave);
    }

    postLinkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota?: number // quota id
    ): Observable<void> {
        const payload = {
            ids: seatIds,
            target,
            quota
        };
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/seats/link`, payload);
    }

    postUnlinkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota: number // quota id
    ): Observable<void> {
        const payload = {
            ids: seatIds,
            target,
            quota
        };
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/seats/unlink`, payload);
    }

    postLinkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void> {
        const payload = {
            id: nnzId,
            source,
            target,
            count
        };
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/not-numbered-zones/link`, payload);
    }

    postUnlinkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void> {
        const payload = {
            id: nnzId,
            source,
            target,
            count
        };
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/not-numbered-zones/unlink`, payload);
    }

    putSeatsBulk(eventId: number, sessionIds: number[], seats: Seat[]): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/sessions/capacity/seats/bulk`,
            {
                ids: sessionIds,
                values: seats.map(seat => ({
                    id: seat.id,
                    status: seat.status || undefined,
                    blocking_reason: seat.blockingReason !== -1 && seat.blockingReason !== null ? seat.blockingReason : undefined,
                    price_type: seat.priceType || undefined,
                    quota: seat.quota || undefined,
                    visibility: seat.visibility || undefined,
                    accessibility: seat.accessibility || undefined,
                    gate: seat.gate || undefined,
                    dynamic_tag1: seat.firstCustomTag > 0 ? seat.firstCustomTag : undefined,
                    dynamic_tag2: seat.secondCustomTag > 0 ? seat.secondCustomTag : undefined
                }))
            });
    }

    putNotNumberedZonesBulk(eventId: number, sessionIds: number[], nnzs: NotNumberedZone[]): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/capacity/not-numbered-zones/bulk`,
            {
                ids: sessionIds,
                values: nnzs.map(nnz => ({
                    id: nnz.id,
                    status: nnz.statusCounters?.length && nnz.statusCounters[0].status || undefined,
                    blocking_reason: nnz.blockingReasonCounters?.length && nnz.blockingReasonCounters[0].blocking_reason || undefined,
                    price_type: nnz.priceType || undefined,
                    quota: nnz.quotaCounters?.length && nnz.quotaCounters[0].quota || undefined,
                    visibility: nnz.visibility || undefined,
                    accessibility: nnz.accessibility || undefined,
                    gate: nnz.gate || undefined,
                    dynamic_tag1: nnz.firstCustomTag > 0 ? nnz.firstCustomTag : undefined,
                    dynamic_tag2: nnz.secondCustomTag > 0 ? nnz.secondCustomTag : undefined
                }))
            });
    }
}

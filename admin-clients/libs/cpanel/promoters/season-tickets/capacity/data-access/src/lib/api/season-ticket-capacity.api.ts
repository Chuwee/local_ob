import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { SeasonTicketLinkNNZResponse, SeasonTicketUnlinkNNZResponse } from '@admin-clients/shared/data-access/models';
import { decodeAndMapVenueMap, NotNumberedZone, Seat, VenueMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { SeasonTicketLinkableSeats } from '../models/season-ticket-linkable-seats.model';
import { SeasonTicketUnLinkableSeats } from '../models/season-ticket-unlinkable-seats.model';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketCapacityApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SESSION_TICKETS_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;

    private readonly _http = inject(HttpClient);

    getSeasonVenueMap(id: number): Observable<VenueMap> {
        return this._http.get(
            `${this.SESSION_TICKETS_API}/${id}/capacity`,
            {
                headers: new HttpHeaders({ accept: 'application/x-protobuf' }),
                responseType: 'arraybuffer'
            })
            .pipe(map((arrayBuffer: ArrayBuffer) => decodeAndMapVenueMap(arrayBuffer)));
    }

    putSeats(seasonTicketId: number, seats: Seat[]): Observable<void> {
        const seatsToSave = seats.map(seat => ({
            id: seat.id,
            status: seat.status,
            blocking_reason: seat.blockingReason === -1 ? null : seat.blockingReason,
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
            `${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/seats`, seatsToSave);
    }

    putNotNumberedZones(seasonTicketId: number, nnzs: NotNumberedZone[]): Observable<void> {
        const zonesToSave = nnzs.map(nnz => ({
            id: nnz.id,
            status_counters: nnz.statusCounters,
            blocking_reason_counters: nnz.blockingReasonCounters,
            price_type: nnz.priceType,
            visibility: nnz.visibility,
            accessibility: nnz.accessibility,
            gate: nnz.gate,
            gate_update_type: nnz.gateUpdateType,
            quota_counters: nnz.quotaCounters,
            dynamic_tag1: nnz.firstCustomTag > 0 ? nnz.firstCustomTag : undefined,
            dynamic_tag2: nnz.secondCustomTag > 0 ? nnz.secondCustomTag : undefined
        }));
        return this._http.put<void>(`${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/not-numbered-zones`, zonesToSave);
    }

    putSeatsLinkable(seasonTicketId: number, seatsLinkableUpdate: Seat[]): Observable<SeasonTicketLinkableSeats> {
        const seatsLinkableUpdateIds = seatsLinkableUpdate.map(seat => seat.id);
        return this._http.post<SeasonTicketLinkableSeats>(
            `${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/seats/link`, { ids: seatsLinkableUpdateIds });
    }

    putSeatsUnLinkable(seasonTicketId: number, seatsUnLinkableUpdate: Seat[]): Observable<SeasonTicketUnLinkableSeats> {
        const seatsUnLinkableUpdateIds = seatsUnLinkableUpdate.map(seat => seat.id);
        return this._http.post<SeasonTicketUnLinkableSeats>(
            `${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/seats/unlink`, { ids: seatsUnLinkableUpdateIds });
    }

    putNnzLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketLinkNNZResponse> {
        return this._http.post<SeasonTicketLinkNNZResponse>(
            `${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/not-numbered-zones/link`, { id, count });
    }

    putNnzNotLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketUnlinkNNZResponse> {
        return this._http.post<SeasonTicketUnlinkNNZResponse>(
            `${this.SESSION_TICKETS_API}/${seasonTicketId}/capacity/not-numbered-zones/unlink`, { id, count });
    }
}

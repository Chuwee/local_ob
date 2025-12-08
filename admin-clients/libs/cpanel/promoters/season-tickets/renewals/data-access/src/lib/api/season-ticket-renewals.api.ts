import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { SeasonTicketRate } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, IdNameListResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSeasonTicketExternalRenewalCandidateResponse } from '../models/get-season-ticket-external-renewal-candidate-response.model';
import { GetSeasonTicketRenewalCandidatesResponse } from '../models/get-season-ticket-renewal-candidates-response.model';
import { GetSeasonTicketRenewalsRequest } from '../models/get-season-ticket-renewals-request.model';
import { GetSeasonTicketRenewalsResponse } from '../models/get-season-ticket-renewals-response.model';
import { DeleteSeasonTicketDeleteMultipleRenewalsRequest } from '../models/post-season-ticket-delete-renewals-request.model';
import { DeleteSeasonTicketDeleteMultipleRenewalsResponse } from '../models/post-season-ticket-delete-renewals-response.model';
import { PostSeasonTicketRenewals } from '../models/post-season-ticket-renewals.model';
import {
    PurgeSeasonTicketsRenewalsRequest, GetDeletableSeasonTicketRenewalsNumberResponse
} from '../models/purge-season-ticket-renewals.model';
import { PutSeasonTicketRenewalsResponse } from '../models/put-season-ticket-renewals-response.model';
import { PutSeasonTicketRenewals } from '../models/put-season-ticket-renewals.model';
import { SeasonTicketRenewalAvailableSeat } from '../models/season-ticket-renewal-available-seat.model';
import { SeasonTicketRenewalCapacityTreeSector } from '../models/season-ticket-renewal-capacity-tree-sector.model';
import { SeasonTicketRenewalRate } from '../models/season-ticket-renewal-rate.model';
import { PostSeasonTicketRenewalsGeneration } from '../models/season-ticket-renewals-generation.model';

@Injectable()
export class SeasonTicketRenewalsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SEASON_TICKETS_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;
    private readonly EXTERNAL_EVENTS_API = `${this.BASE_API}/mgmt-api/v1/external-events`;
    private readonly INTERNAL_API = `${this.BASE_API}/internal-api/v1`;
    private readonly AUTOMATIC_SALES_SESSIONS = `${this.INTERNAL_API}/automatic-renewals/season-tickets`;

    private readonly _http = inject(HttpClient);

    getRenewalCandidates(seasonTicketId: number): Observable<GetSeasonTicketRenewalCandidatesResponse> {
        return this._http.get<GetSeasonTicketRenewalCandidatesResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewal-candidates`
        );
    }

    getExternalRenewalCandidates(entityId: number): Observable<GetSeasonTicketExternalRenewalCandidateResponse> {
        const params = buildHttpParams({
            entity_id: entityId,
            event_type: 'SEASON_TICKET'
        });
        return this._http.get<GetSeasonTicketExternalRenewalCandidateResponse>(`${this.EXTERNAL_EVENTS_API}`, { params });
    }

    postRenewals(seasonTicketId: number, renewalCandidate: PostSeasonTicketRenewals): Observable<void> {
        return this._http.post<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals`, renewalCandidate);
    }

    getParams(request: GetSeasonTicketRenewalsRequest): HttpParams {
        let sort = request.sort;
        const sortParts = sort?.split(':');
        if (sortParts?.length > 1 && sortParts[0] === 'name') {
            sort += `,surname:${sortParts[1]}`;
        }
        return buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            mapping_status: request.mapping_status,
            renewal_status: request.renewal_status,
            renewal_substatus: request.renewal_substatus,
            auto_renewal: request.auto_renewal,
            sort,
            birthday: getRangeParam(request.startDate, request.endDate),
            entity_id: request.entityId
        });
    }

    getRenewals(seasonTicketId: number, request: GetSeasonTicketRenewalsRequest): Observable<GetSeasonTicketRenewalsResponse> {
        const params = this.getParams(request);
        return this._http.get<GetSeasonTicketRenewalsResponse>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals`, { params });
    }

    getRenewalsEntities(seasonTicketId: number): Observable<IdNameListResponse> {
        return this._http.get<IdNameListResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewal-entities`
        );
    }

    getRenewalsCapacityTreeSectors(seasonTicketId: number): Observable<SeasonTicketRenewalCapacityTreeSector[]> {
        return this._http.get<SeasonTicketRenewalCapacityTreeSector[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/capacity-tree`
        );
    }

    getAvailableRowSeats(seasonTicketId: number, rowId: number): Observable<SeasonTicketRenewalAvailableSeat[]> {
        return this._http.get<SeasonTicketRenewalAvailableSeat[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/row/${rowId}/available-seats`
        );
    }

    getAvailableNnzSeats(seasonTicketId: number, nnzId: number): Observable<SeasonTicketRenewalAvailableSeat[]> {
        return this._http.get<SeasonTicketRenewalAvailableSeat[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/not-numbered-zones/${nnzId}/available-seats`
        );
    }

    putRenewals(seasonTicketId: number, request: PutSeasonTicketRenewals): Observable<PutSeasonTicketRenewalsResponse> {
        return this._http.put<PutSeasonTicketRenewalsResponse>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals`, request);
    }

    deleteRenewal(seasonTicketId: number, renewalId: string): Observable<void> {
        return this._http.delete<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/${renewalId}`);
    }

    deleteMultipleRenewals(
        seasonTicketId: number, request: DeleteSeasonTicketDeleteMultipleRenewalsRequest
    ): Observable<DeleteSeasonTicketDeleteMultipleRenewalsResponse> {
        const params = buildHttpParams({ renewal_ids: request.renewal_ids });
        return this._http.delete<DeleteSeasonTicketDeleteMultipleRenewalsResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals`, { params }
        );
    }

    getRenewalRates(seasonTicketId: number): Observable<SeasonTicketRate[]> {
        return this._http.get<SeasonTicketRate[]>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/rates`);
    }

    getExternalRenewalRates(id: number): Observable<SeasonTicketRenewalRate[]> {
        return this._http.get<SeasonTicketRenewalRate[]>(`${this.EXTERNAL_EVENTS_API}/${id}/rates`);
    }

    purgeRenewals(seasonTicketId: number, request: PurgeSeasonTicketsRenewalsRequest): Observable<void> {
        const params = buildHttpParams({
            q: request.q,
            mapping_status: request.mapping_status,
            renewal_status: request.renewal_status,
            birthday: getRangeParam(request.startDate, request.endDate)
        });
        return this._http.post<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/purge`, {}, { params });
    }

    getDeletableRenewalsNumber(
        seasonTicketId: number,
        request: PurgeSeasonTicketsRenewalsRequest
    ): Observable<GetDeletableSeasonTicketRenewalsNumberResponse> {
        const params = buildHttpParams({
            q: request.q,
            mapping_status: request.mapping_status,
            renewal_status: request.renewal_status,
            birthday: getRangeParam(request.startDate, request.endDate)
        });
        return this._http.get<GetDeletableSeasonTicketRenewalsNumberResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/purge`, { params });
    }

    exportRenewalsList(seasonTicketId: number, request: GetSeasonTicketRenewalsRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.getParams(request);
        return this._http.post<ExportResponse>(`${this.SEASON_TICKETS_API}/${seasonTicketId}/renewals/exports`, body, { params });
    }

    exportXmlSepa(seasonTicketId: number): Observable<void> {
        const body = { season_ticket_id: seasonTicketId };
        return this._http.post<void>(`${this.INTERNAL_API}/xml-sepa/generate`, body);
    }

    generateAutomaticRenewals(seasonTicketId: number, body: PostSeasonTicketRenewalsGeneration): Observable<void> {
        return this._http.post<void>(`${this.AUTOMATIC_SALES_SESSIONS}/${seasonTicketId}/automatic-renewals`, body);
    }
}

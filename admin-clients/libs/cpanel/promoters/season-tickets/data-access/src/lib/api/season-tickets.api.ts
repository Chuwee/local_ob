import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import {
    PutSeasonTicketLoyaltyPoint, PutSeasonTicketReleaseSeats, PutSeasonTicketTicketRedemption, PutSeasonTicketTransferSeats,
    ReleasedList, SeasonTicketLoyaltyPointList, SeasonTicketReleaseSeats, SeasonTicketTicketRedemption, SeasonTicketTransferSeats
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import { CustomerTypeAssignation, PutCustomerTypeAssignation, RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Presale, PresalePost, PresalePut } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSeasonTicketValidationsResponse } from '../models/get-season-ticket-validations.model';
import { GetSeasonTicketsRequest } from '../models/get-season-tickets-request.model';
import { GetSeasonTicketsResponse } from '../models/get-season-tickets-response.model';
import { PostSeasonTicketRate } from '../models/post-season-ticket-rate.model';
import { PostSeasonTicket } from '../models/post-season-ticket.model';
import { PutSeasonTicketChangeSeatPrice } from '../models/put-season-ticket-change-seat-price.model';
import { PutSeasonTicketChangeSeats } from '../models/put-season-ticket-change-seats.model';
import { PutSeasonTicketPrice } from '../models/put-season-ticket-price.model';
import { PutSeasonTicketRate } from '../models/put-season-ticket-rate.model';
import { PutSeasonTicketStatus } from '../models/put-season-ticket-status.model';
import { PutSeasonTicket } from '../models/put-season-ticket.model';
import { SeasonTicketChangeSeatPrice } from '../models/season-ticket-change-seat-price.model';
import { SeasonTicketChangeSeats } from '../models/season-ticket-change-seats.model';
import { SeasonTicketPrice } from '../models/season-ticket-price.model';
import { SeasonTicketRate } from '../models/season-ticket-rate.model';
import { SeasonTicketReleaseSeatListRequest } from '../models/season-ticket-release-seat-list-request.model';
import { GetSeasonTicketStatusResponse } from '../models/season-ticket-status.model';
import { SeasonTicketSurcharge } from '../models/season-ticket-surcharge.model';
import { PutSeasonTicketTaxes, SeasonTicketTaxes } from '../models/season-ticket-taxes.model';
import { SeasonTicket } from '../models/season-ticket.model';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketsApi {

    readonly #BASE_API = inject(APP_BASE_API);
    readonly #SEASON_TICKETS_API = `${this.#BASE_API}/mgmt-api/v1/season-tickets`;

    readonly #http = inject(HttpClient);

    getSeasonTickets(request: GetSeasonTicketsRequest): Observable<GetSeasonTicketsResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            entity_id: request.entityId,
            producer_id: request.producerId,
            venue_id: request.venueId,
            currency_code: request.currency,
            status: request.status
        });
        return this.#http.get<GetSeasonTicketsResponse>(this.#SEASON_TICKETS_API, { params });
    }

    getSeasonTicket(seasonTicketId: string): Observable<SeasonTicket> {
        return this.#http.get<SeasonTicket>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}`);
    }

    postSeasonTicket(seasonTicket: PostSeasonTicket): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(this.#SEASON_TICKETS_API, {
            name: seasonTicket.name,
            entity_id: seasonTicket.entityId,
            producer_id: seasonTicket.producerId,
            category_id: seasonTicket.categoryId,
            charges_tax_id: seasonTicket.chargesTaxId,
            tax_id: seasonTicket.taxId,
            venue_config_id: seasonTicket.venueConfigId,
            currency_code: seasonTicket.currencyCode,
            additional_config: seasonTicket.additionalConfig,
            ...(seasonTicket.customCategoryId && { custom_category_id: seasonTicket.customCategoryId }),
            ...(seasonTicket.startDate && { start_date: seasonTicket.startDate }),
            ...(seasonTicket.endDate && { end_date: seasonTicket.endDate })
        });
    }

    putSeasonTicket(id: string, putSeasonTicket: PutSeasonTicket): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${id}`, putSeasonTicket);
    }

    deleteSeasonTicket(seasonTicketId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}`);
    }

    getSeasonTicketStatus(seasonTicketId: string): Observable<GetSeasonTicketStatusResponse> {
        return this.#http.get<GetSeasonTicketStatusResponse>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/status`);
    }

    putSeasonTicketStatus(seasonTicketId: number, putSeasonTicketStatus: PutSeasonTicketStatus): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/status`, putSeasonTicketStatus);
    }

    getSeasonTicketRates(seasonTicketId: string): Observable<SeasonTicketRate[]> {
        return this.#http.get<SeasonTicketRate[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates`);
    }

    putSeasonTicketRates(seasonTicketId: string, rates: PutSeasonTicketRate[]): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates`, rates);
    }

    postSeasonTicketRate(seasonTicketId: string, rate: PostSeasonTicketRate): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates`, rate);
    }

    deleteSeasonTicketRate(seasonTicketId: string, rateId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates/${rateId}`);
    }

    getSeasonTicketRatesRestrictions(seasonTicketId: number): Observable<ListResponse<RateRestrictions>> {
        return this.#http.get<ListResponse<RateRestrictions>>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates/restrictions`);
    }

    putSeasonTicketRateRestrictions(seasonTicketId: number, rateId: number, restrictions: Partial<RateRestrictions>): Observable<void> {
        return this.#http.post<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates/${rateId}/restrictions`, restrictions);
    }

    deleteSeasonTicketRateRestrictions(seasonTicketId: number, rateId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/rates/${rateId}/restrictions`);
    }

    getSeasonTicketPrices(seasonTicketId: number): Observable<SeasonTicketPrice[]> {
        return this.#http.get<SeasonTicketPrice[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/prices`);
    }

    getSeasonTicketChangeSeatPrices(seasonTicketId: number): Observable<SeasonTicketChangeSeatPrice[]> {
        return this.#http.get<SeasonTicketChangeSeatPrice[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/change-seats/prices`);
    }

    putSeasonTicketChangeSeatPrices(seasonTicketId: number, prices: PutSeasonTicketChangeSeatPrice[]): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/change-seats/prices`, prices);
    }

    putEventPrices(seasonTicketId: number, prices: PutSeasonTicketPrice[]): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/prices`, prices);
    }

    getSeasonTicketSurcharges(seasonTicketId: number): Observable<SeasonTicketSurcharge[]> {
        return this.#http.get<SeasonTicketSurcharge[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/surcharges`);
    }

    postSeasonTicketSurcharges(seasonTicketId: string, surcharges: SeasonTicketSurcharge[]): Observable<void> {
        return this.#http.post<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/surcharges`, surcharges);
    }

    getSeasonTicketValidations(
        seasonTicketId: string, hasLinkableSeats?: boolean, hasAssignedSessions?: boolean, hasPendingRenewals?: boolean
    ): Observable<GetSeasonTicketValidationsResponse> {
        const params = buildHttpParams({
            has_linkable_seats: hasLinkableSeats,
            has_assigned_sessions: hasAssignedSessions,
            has_pending_renewals: hasPendingRenewals
        });
        return this.#http.get<GetSeasonTicketValidationsResponse>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/validations`, { params });
    }

    getSeasonTicketChangeSeats(seasonTicketId: number): Observable<SeasonTicketChangeSeats> {
        return this.#http.get<SeasonTicketChangeSeats>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/change-seats`);
    }

    putSeasonTicketChangeSeats(seasonTicketId: number, changeSeats: PutSeasonTicketChangeSeats): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/change-seats`, changeSeats);
    }

    getSeasonTicketReleaseSeat(seasonTicketId: number): Observable<SeasonTicketReleaseSeats> {
        return this.#http.get<SeasonTicketReleaseSeats>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/release-seat`);
    }

    putSeasonTicketReleaseSeat(seasonTicketId: number, changeSeats: PutSeasonTicketReleaseSeats): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/release-seat`, changeSeats);
    }

    getSeasonTicketTransferSeat(seasonTicketId: number): Observable<SeasonTicketTransferSeats> {
        return this.#http.get<SeasonTicketTransferSeats>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/transfer-seat`);
    }

    putSeasonTicketTransferSeat(seasonTicketId: number, changeSeats: PutSeasonTicketTransferSeats): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/transfer-seat`, changeSeats);
    }

    getSeasonTicketTicketRedemption(seasonTicketId: number): Observable<SeasonTicketTicketRedemption> {
        return this.#http.get<SeasonTicketTicketRedemption>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/redemption`);
    }

    putSeasonTicketTicketRedemption(seasonTicketId: number, req: PutSeasonTicketTicketRedemption): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/redemption`, req);
    }

    getSeasonTicketLoyaltyPoint(seasonTicketId: number): Observable<SeasonTicketLoyaltyPointList> {
        return this.#http.get<SeasonTicketLoyaltyPointList>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/loyalty-points`);
    }

    putSeasonTicketLoyaltyPoint(seasonTicketId: number, changeSeats: PutSeasonTicketLoyaltyPoint): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/loyalty-points`, changeSeats);
    }

    getSeasonTicketReleaseSeatList(
        seasonTicketId: number, request: SeasonTicketReleaseSeatListRequest
    ): Observable<ListResponse<ReleasedList>> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            session_id: request.session_id,
            release_status: request.release_status
        });
        return this.#http.get<ListResponse<ReleasedList>>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/release-seat/releases`, { params });
    }

    exportSeasonTicketReleaseSeatList(seasonTicketId: number, body: ExportRequest): Observable<ExportResponse> {
        return this.#http.post<ExportResponse>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/releases/exports`, body);
    }

    getSeasonTicketPresales(seasonTicketId: number): Observable<Presale[]> {
        return this.#http.get<Presale[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/presales`);
    }

    postSeasonTicketPresale(seasonTicketId: number, body: PresalePost): Observable<Presale> {
        return this.#http.post<Presale>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/presales`, body);
    }

    putSeasonTicketPresale(seasonTicketId: number, presaleId: string, body: PresalePut): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/presales/${presaleId}`, body);
    }

    deleteSeasonTicketPresale(seasonTicketId: number, presaleId: string): Observable<void> {
        return this.#http.delete<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/presales/${presaleId}`);
    }

    getSeasonTicketCustomerTypeAssignation(seasonTicketId: number): Observable<CustomerTypeAssignation[]> {
        return this.#http.get<CustomerTypeAssignation[]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/customer-types`);
    }

    putSeasonTicketCustomerTypeAssignation(seasonTicketId: number, customerTypeAssignation: PutCustomerTypeAssignation[]): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/customer-types`, customerTypeAssignation);
    }

    getSeasonTicketForms(seasonTicketId: string, formType: string): Observable<FormsField[][]> {
        return this.#http.get<FormsField[][]>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/forms/${formType}`);
    }

    updateSeasonTicketForms(seasonTicketId: string, formType: string, formsData: FormsField[][]): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/forms/${formType}`, formsData);
    }

    putExternalAvailability(seasonTicketId: number): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/external-inventory`, {});
    }

    getSeasonTicketTaxes(seasonTicketId: number): Observable<SeasonTicketTaxes> {
        return this.#http.get<SeasonTicketTaxes>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/taxes`);
    }

    putSeasonTicketTaxes(seasonTicketId: number, seasonTicketTaxes: PutSeasonTicketTaxes): Observable<void> {
        return this.#http.put<void>(`${this.#SEASON_TICKETS_API}/${seasonTicketId}/taxes`, seasonTicketTaxes);
    }
}

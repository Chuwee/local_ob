import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { PutSessionRefundConditions, SessionRefundConditions } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Presale, PresalePost, PresalePut } from '@admin-clients/cpanel/shared/data-access';
import {
    GetPriceTypeRestricion, PostPriceTypeRestriction, RestrictedPriceZones
} from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { AttributeWithValues, PutAttribute } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import {
    PriceTypeAvailability, SessionActivityGroupsConfig, SessionPriceType
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    BulkPutVenueTemplateElementInfoRequest, ElementsInfoFilterRequest, PostVenueTemplateElementInfoRequest,
    PutVenueTemplateElementInfoRequest, VenueTemplateElementInfo, VenueTemplateElementInfoDetail,
    VenueTemplateElementInfoImage, VenueTemplateElementInfoType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AutomaticSaleEntry } from '../models/automatic-sale-entry.model';
import { AutomaticSalesPut } from '../models/automatic-sale.model';
import { AutomaticSalesPost } from '../models/automatic-sales-post.model';
import { CloneSessionRequest } from '../models/clone-session-request.model';
import { DeleteSessionsResponse } from '../models/delete-sessions-response.model';
import { GetSessionsGroupsRequest } from '../models/get-sessions-groups-request.model';
import { GetSessionsRequest } from '../models/get-sessions-request.model';
import { GetSessionsResponse } from '../models/get-sessions-response.model';
import { GetInternalBarcodesRequest, GetInternalBarcodesResponse } from '../models/internal-barcode.model';
import { LinkedSession } from '../models/linked-session.model';
import { PostRelocationSeats } from '../models/post-relocation-seats.model';
import { PostSession } from '../models/post-session.model';
import { PutSession } from '../models/put-session.model';
import { PutSessionsResponse } from '../models/put-sessions-response.model';
import { SaleConstraints } from '../models/sale-constraints.model';
import { SessionAdditionalConfig } from '../models/session-additional-config.model';
import { GetExternalBarcodesRequest, GetExternalBarcodesResponse, PostBarcodesToImport } from '../models/session-barcode-to-import.model';
import {
    GetSessionDynamicPricesResponse, GetSessionZoneDynamicPricesResponse, PostSessionZoneDynamicPrices, PutSessionDynamicPrices
} from '../models/session-dynamic-prices.model';
import { SessionExternalBarcodes } from '../models/session-external-barcodes.model';
import { SessionExternalSessionsConfigRequest } from '../models/session-external-sessions-config-request.model';
import { SessionExternalSessionsConfig } from '../models/session-external-sessions-config.model';
import { SessionLoyaltyPoints } from '../models/session-loyalty-points.model';
import { SessionQuotaCapacity } from '../models/session-quota-capacity.model';
import { SessionRate } from '../models/session-rate.model';
import { SessionTiersAvailability } from '../models/session-tiers-availability.model';
import { Session } from '../models/session.model';
import { SessionsGroup } from '../models/sessions-group.model';

@Injectable()
export class EventSessionsApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;
    private readonly SESSIONS_API = `${this.BASE_API}/mgmt-api/v1/sessions`;
    private readonly INTERNAL_API = `${this.BASE_API}/internal-api/v1`;
    private readonly AUTOMATIC_SALES_SESSIONS = `${this.INTERNAL_API}/automatic-sales/sessions`;

    private readonly _http = inject(HttpClient);

    getSessions(eventId: number, request: GetSessionsRequest): Observable<GetSessionsResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            id: request.filterByIds,
            status: request.status,
            type: request.type,
            venue_template_id: request.venueTplId,
            fields: request.fields,
            day_of_week: request.weekdays,
            timezone: request.timezone,
            hour_range: request.hourRanges,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate),
            end_date: getRangeParam(request.initEndDate, request.finalEndDate)
        });
        return this._http.get<GetSessionsResponse>(
            `${this.EVENTS_API}/${eventId}/sessions`,
            { params }
        );
    }

    getSessionsGroups(eventId: number, request: GetSessionsGroupsRequest): Observable<SessionsGroup[]> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            id: request.filterByIds,
            status: request.status,
            type: request.type,
            venue_template_id: request.venueTplId,
            day_of_week: request.weekdays,
            timezone: request.timezone,
            hour_range: request.hourRanges,
            group_type: request.groupType,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate)
        });
        return this._http.get<SessionsGroup[]>(
            `${this.EVENTS_API}/${eventId}/sessions/groups`,
            { params }
        );
    }

    getSession(eventId: number, sessionId: number): Observable<Session> {
        return this._http.get<Session>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}`);
    }

    postSession(eventId: number, session: PostSession): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.EVENTS_API}/${eventId}/sessions`, session);
    }

    putSession(eventId: number, sessionId: number, session: Session | PutSession): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}`, session);
    }

    deleteSession(eventId: number, sessionId: number, packRelatedSessionsSeats?: number): Observable<void> {
        const params = buildHttpParams(packRelatedSessionsSeats ?? { pack_related_sessions_seats: packRelatedSessionsSeats });
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}`, { params });
    }

    cloneSession(eventId: number, fromSessionId: number, data: CloneSessionRequest): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.EVENTS_API}/${eventId}/sessions/${fromSessionId}/clone`, data);
    }

    getSessionAdditionalConfig(eventId: number, sessionId: number): Observable<SessionAdditionalConfig> {
        return this._http.get<SessionAdditionalConfig>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/additional-config`
        );
    }

    postSessions(eventId: number, sessions: PostSession[]): Observable<number[]> {
        return this._http.post<number[]>(`${this.EVENTS_API}/${eventId}/sessions/bulk`, sessions);
    }

    putSessions(eventId: number, sessions: unknown, isPreview: boolean): Observable<PutSessionsResponse[]> {
        return this._http.put<PutSessionsResponse[]>(`${this.EVENTS_API}/${eventId}/sessions/bulk`, sessions, {
            params: { preview: isPreview ? 'true' : 'false' }
        });
    }

    deleteSessions(eventId: number, sessionIds: number[], isPreview: boolean): Observable<DeleteSessionsResponse[]> {
        return this._http.delete<DeleteSessionsResponse[]>(
            `${this.EVENTS_API}/${eventId}/sessions/bulk`,
            {
                params: {
                    preview: isPreview ? 'true' : 'false',
                    ids: sessionIds.join(',')
                }
            });
    }

    getSaleConstraints(eventId: number, sessionId: number): Observable<SaleConstraints> {
        return this._http.get<SaleConstraints>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/sale-constraints`);
    }

    putSaleConstraints(eventId: number, sessionId: number, saleConstraint: SaleConstraints): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/sale-constraints`, saleConstraint);
    }

    deleteCartLimit(eventId: number, sessionId: number): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/sale-constraints/cart-limit`);
    }

    deletePriceTypeLimit(eventId: number, sessionId: number): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/sale-constraints/price-type-limit`);
    }

    getPresales(eventId: number, sessionId: number): Observable<Presale[]> {
        return this._http.get<Presale[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/presales`);
    }

    postPresale(eventId: number, sessionId: number, presale: PresalePost): Observable<Presale> {
        return this._http.post<Presale>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/presales`, presale);
    }

    putPresale(eventId: number, sessionId: number, presaleId: string, presale: PresalePut): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/presales/${presaleId}`, presale);
    }

    deletePresale(eventId: number, sessionId: number, presaleId: string): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/presales/${presaleId}`);
    }

    getSessionTiersAvailability(eventId: number, sessionId: number): Observable<SessionTiersAvailability[]> {
        return this._http.get<SessionTiersAvailability[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/availability/tiers`);
    }

    putSessionAttributes(eventId: number, sessionId: number, attributes: PutAttribute[]): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/attributes`, attributes);
    }

    getSessionAttributes(eventId: number, sessionId: number, fullLoad: boolean): Observable<AttributeWithValues[]> {
        const params = buildHttpParams({ full_load: fullLoad ? true : undefined });
        return this._http.get<AttributeWithValues[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/attributes`, { params });
    }

    getQuotasCapacities(eventId: number, sessionId: number): Observable<SessionQuotaCapacity[]> {
        return this._http.get<SessionQuotaCapacity[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/quotas`);
    }

    putQuotaCapacities(eventId: number, sessionId: number, quotaCapacities: SessionQuotaCapacity[]): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/quotas`,
            quotaCapacities);
    }

    postRelocationSeats(eventId: number, sessionId: number, relocationSeats: PostRelocationSeats): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/relocation`, relocationSeats);
    }

    putExternalAvailability(eventId: number, sessionId: number): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-inventory`, {});
    }

    putExternalMembershipInventory(eventId: number): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/external-inventory`, {});
    }

    getSessionPriceTypesAvailability(eventId: number, sessionId: number): Observable<PriceTypeAvailability[]> {
        return this._http.get<PriceTypeAvailability[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/availability/price-types`);
    }

    getSessionPriceTypes(eventId: number, sessionId: number): Observable<SessionPriceType[]> {
        return this._http.get<SessionPriceType[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/price-types`);
    }

    putSessionPriceType(eventId: number, sessionId: number, priceType: SessionPriceType): Observable<void> {
        return this._http.put<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/price-types/${priceType.id}`,
            {
                id: priceType.id,
                additional_config: {
                    gate_id: priceType.additional_config.gate_id
                }
            });
    }

    getSessionGroupConfig(eventId: number, sessionId: number): Observable<SessionActivityGroupsConfig> {
        return this._http.get<SessionActivityGroupsConfig>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/groups`);
    }

    putSessionGroupConfig(eventId: number, sessionId: number, groupsConfig: SessionActivityGroupsConfig): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/groups`, groupsConfig);
    }

    getWhiteList(eventId: number, sessionId: number, request?: GetInternalBarcodesRequest): Observable<GetInternalBarcodesResponse> {
        const params = buildHttpParams(request ? request : {});
        return this._http.get<GetInternalBarcodesResponse>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/whitelist`, { params });
    }

    exportWhitelist(eventId: number, sessionId: number, body: ExportRequest): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/whitelist/exports`, body);
    }

    exportExternalBarcodes(eventId: number, sessionId: number, body: ExportRequest): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-barcodes/exports`, body);
    }

    getLinkedSessions(eventId: number, sessionId: number): Observable<LinkedSession[]> {
        return this._http.get<LinkedSession[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/linked-sessions`);
    }

    getRefundConditions(eventId: number, sessionId: number): Observable<SessionRefundConditions> {
        return this._http.get<SessionRefundConditions>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/refund-conditions`);
    }

    putRefundConditions(
        eventId: number,
        sessionId: number,
        conditions: PutSessionRefundConditions
    ): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/refund-conditions`, conditions);
    }

    // RESTRICTIONS

    getVenueTplRestrictedPriceTypes(eventId: number, sessionId: number): Observable<RestrictedPriceZones> {
        return this._http.get<RestrictedPriceZones>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/restricted-price-types`);
    }

    getPriceTypeRestriction(eventId: number, sessionId: number, priceTypeId: number): Observable<GetPriceTypeRestricion> {
        return this._http.get<GetPriceTypeRestricion>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/price-types/${priceTypeId}/restrictions`);
    }

    postPriceTypeRestriction(
        eventId: number,
        sessionId: number,
        priceTypeId: number,
        restriction: PostPriceTypeRestriction
    ): Observable<void> {
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/price-types/${priceTypeId}/restrictions`, restriction);
    }

    deletePriceTypeRestriction(eventId: number, sessionId: number, priceTypeId: number): Observable<void> {
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/price-types/${priceTypeId}/restrictions`);
    }

    // WHITE LIST

    postExternalBarcodesImport(eventId: number, sessionId: number, postBarcodesToImport: PostBarcodesToImport): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-barcodes/import`,
            postBarcodesToImport
        );
    }

    getUploadedExternalBarcodes(
        eventId: number,
        sessionId: number,
        req?: GetExternalBarcodesRequest
    ): Observable<GetExternalBarcodesResponse> {
        const params = buildHttpParams(req);
        return this._http.get<GetExternalBarcodesResponse>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-barcodes`, { params });
    }

    // Session external barcodes
    getSessionExternalBarcodes(eventId: number, sessionId: number): Observable<SessionExternalBarcodes> {
        return this._http.get<SessionExternalBarcodes>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-barcodes/config`);
    }

    putSessionExternalBarcodeConfig(
        eventId: number, sessionId: number, externalBarcodes: Partial<SessionExternalBarcodes>
    ): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-barcodes/config`, externalBarcodes);
    }

    // External sessions config
    getSessionExternalSessionsConfig(eventId: number, sessionId: number): Observable<SessionExternalSessionsConfig> {
        return this._http.get<SessionExternalSessionsConfig>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-session-config`);
    }

    putSessionExternalSessionsConfig(eventId: number, sessionId: number, request: SessionExternalSessionsConfigRequest): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/external-session-config`, request);
    }

    //Export venue capacity
    exportCapacity(eventId: number, sessionId: number, body: ExportRequest): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/capacity/exports`, body);
    }

    //Session Rates
    getSessionRates(eventId: number, sessionId: number): Observable<SessionRate[]> {
        return this._http.get<SessionRate[]>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/rates`);
    }

    getSessionRatesRestrictions(eventId: number, sessionId: number): Observable<ListResponse<RateRestrictions>> {
        return this._http.get<ListResponse<RateRestrictions>>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/rates/restrictions`);
    }

    postSessionRatesRestrictions(eventId: number, sessionId: number, rateId: number, restrictions: Partial<RateRestrictions>): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/rates/${rateId}/restrictions`, restrictions);
    }

    deleteSessionRatesRestrictions(eventId: number, sessionId: number, rateId: number): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/rates/${rateId}/restrictions`);
    }

    getVenueTemplatesElementInfo(
        sessionId: number, request: ElementsInfoFilterRequest
    ): Observable<ListResponse<VenueTemplateElementInfo>> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            q: request.q,
            type: request.type,
            status: request.status
        });
        return this._http.get<ListResponse<VenueTemplateElementInfo>>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info`, { params }
        );
    }

    getVenueTemplateElementInfo(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType
    ): Observable<VenueTemplateElementInfoDetail> {
        return this._http.get<VenueTemplateElementInfoDetail>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}`
        );
    }

    postVenueTemplateElementInfo(sessionId: number, req: PostVenueTemplateElementInfoRequest): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.SESSIONS_API}/${sessionId}/venue-template-elements-info`, req);
    }

    putVenueTemplateElementInfo(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, saveValue: PutVenueTemplateElementInfoRequest
    ): Observable<void> {
        return this._http.put<void>(`${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}`, saveValue);
    }

    recoverInheritanceVenueTemplateElementInfo(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType): Observable<void> {
        return this._http.delete<void>(`${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}`);
    }

    changeElementInfoStatus(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, status: 'ENABLED' | 'DISABLED'
    ): Observable<void> {
        return this._http.put<void>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}/status`,
            { status }
        );
    }

    putMultipleVenueTemplateElementInfo(
        sessionId: number, saveValue: BulkPutVenueTemplateElementInfoRequest, filters: ElementsInfoFilterRequest = {}): Observable<void> {
        const params = buildHttpParams({ q: filters?.q?.length ? filters.q : null, type: filters.type, status: filters.status });
        return this._http.put<void>(`${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/`, saveValue, { params });
    }

    postAutomaticSale(sessionId: number, request: AutomaticSalesPost): Observable<void> {
        return this._http.post<void>(`${this.AUTOMATIC_SALES_SESSIONS}/${sessionId}/sales`, request);
    }

    putAutomaticSale(sessionId: number, status: AutomaticSalesPut): Observable<void> {
        return this._http.put<void>(`${this.AUTOMATIC_SALES_SESSIONS}/${sessionId}/sales`, status);
    }

    exportAutomaticSale(sessionId: number, request: ExportRequest): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(`${this.AUTOMATIC_SALES_SESSIONS}/${sessionId}/file/exports`, request);
    }

    getAutomaticSale(sessionId: number): Observable<AutomaticSaleEntry[]> {
        return this._http.get<AutomaticSaleEntry[]>(`${this.AUTOMATIC_SALES_SESSIONS}/${sessionId}/file`);
    }

    getVenueTemplateElementInfoImages(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType
    ): Observable<VenueTemplateElementInfoImage[]> {
        return this._http.get<VenueTemplateElementInfoImage[]>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}/images`
        );
    }

    postVenueTemplateElementInfoImages(
        sessionId: number, elementInfoId: number, type: VenueTemplateElementInfoType, contents: VenueTemplateElementInfoImage[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}/images`
            , contents
        );
    }

    deleteVenueTemplateElementInfoImage(
        sessionId: number,
        elementInfoId: number,
        type: VenueTemplateElementInfoType,
        language: string,
        imageType: 'SLIDER' | 'HIGHLIGHTED',
        position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.SESSIONS_API}/${sessionId}/venue-template-elements-info/${type}/${elementInfoId}/images/${imageType}/languages/${language}`,
            { params }
        );
    }

    //LOYALTY POINTS
    getSessionLoyaltyPoints(eventId: number, sessionId: number): Observable<SessionLoyaltyPoints> {
        return this._http.get<SessionLoyaltyPoints>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/loyalty-points`);
    }

    putSessionLoyaltyPoints(eventId: number, sessionId: number, config: SessionLoyaltyPoints): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/loyalty-points`, config);
    }

    postMapping(eventId: number, sessionId: number): Observable<void> {
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/mapping`, null);
    }

    //DYNAMIC PRICES
    getDynamicPrices(eventId: number, sessionId: number): Observable<GetSessionDynamicPricesResponse> {
        return this._http.get<GetSessionDynamicPricesResponse>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/dynamic-prices`);
    }

    putDynamicPrices(eventId: number, sessionId: number, config: PutSessionDynamicPrices): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/sessions/${sessionId}/dynamic-prices`, config);
    }

    getZoneDynamicPrices(eventId: number, sessionId: number, zoneId: number): Observable<GetSessionZoneDynamicPricesResponse> {
        return this._http.get<GetSessionZoneDynamicPricesResponse>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/dynamic-prices/price-zones/${zoneId}`);
    }

    postZoneDynamicPrices(eventId: number, sessionId: number, zoneId: number, body: PostSessionZoneDynamicPrices): Observable<void> {
        return this._http.post<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/dynamic-prices/price-zones/${zoneId}`, body);
    }

    deleteZoneDynamicPrice(eventId: number, sessionId: number, zoneId: number, orderIndex: number): Observable<void> {
        return this._http.delete<void>(
            `${this.EVENTS_API}/${eventId}/sessions/${sessionId}/dynamic-prices/price-zones/${zoneId}/zone/${orderIndex}`);
    }
}

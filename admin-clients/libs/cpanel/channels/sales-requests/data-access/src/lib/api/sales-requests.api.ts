import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import {
    AdditionalCondition, ChannelCommission, ChannelPriceSimulation, ChannelSurcharge
} from '@admin-clients/cpanel/channels/data-access';
import { ContentLinkRequest, ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterResponse } from '../models/get-filter-response';
import { GetSaleRequestChannelSessionLinksRequest } from '../models/get-sale-request-channel-session-links-request';
import { GetSaleRequestPriceTypesRequest } from '../models/get-sale-request-price-types-request';
import { GetSaleRequestPromotionsRequest } from '../models/get-sale-request-promotions-request';
import { GetSaleRequestSessionsRequest } from '../models/get-sale-request-sessions-request';
import { GetSalesRequestsRequest } from '../models/get-sales-requests-request';
import { GetSalesRequestsResponse } from '../models/get-sales-requests-response';
import { PutSaleRequestGateways } from '../models/put-sale-request-gateways.model';
import { PutSaleRequestPurchaseContentImage } from '../models/put-sale-request-purchase-content-image.model';
import { PutSaleRequestTicketContentImage } from '../models/put-sale-request-ticket-content-image.model';
import { SaleRequestAdditionalBannerTexts } from '../models/sale-request-additional-banner-texts.model';
import { SaleRequestBenefitsContentBadge } from '../models/sale-request-benefits-content-badge.model';
import { SaleRequestConfigurationModel } from '../models/sale-request-configuration.model';
import { SaleRequestDeliveryConditions } from '../models/sale-request-delivery-conditions.model';
import { SaleRequestEmailContentTexts } from '../models/sale-request-email-content-texts.model';
import {
    SaleRequestGateway, SaleRequestGatewayBenefits
} from '../models/sale-request-gateway.model';
import { SaleRequestPriceTypesResponse } from '../models/sale-request-price-types.model';
import { GetSaleRequestPromotionsResponse } from '../models/sale-request-promotion.model';
import { SaleRequestPurchaseContentImageType } from '../models/sale-request-purchase-content-image-type.enum';
import { SaleRequestPurchaseContentImage } from '../models/sale-request-purchase-content-image.model';
import { SaleRequestPurchaseContentText } from '../models/sale-request-purchase-content-text.model';
import { SaleRequestSessionResponse } from '../models/sale-request-session.model';
import { PutSaleRequestSurchargeTaxes, SaleRequestSurchargeTaxes } from '../models/sale-request-surcharge-taxes.model';
import { SaleRequestTicketContentFormat } from '../models/sale-request-ticket-content-format.enum';
import { SaleRequestTicketContentImageType } from '../models/sale-request-ticket-content-image-type.enum';
import { SaleRequestTicketContentImage } from '../models/sale-request-ticket-content-image.model';
import { SaleRequestTicketContentTemplate } from '../models/sale-request-ticket-content-template.model';
import { SaleRequestTicketContentText } from '../models/sale-request-ticket-content-text.model';
import { SaleRequest } from '../models/sale-request.model';
import { SalesRequestsStatus } from '../models/sales-requests-status.model';

@Injectable({
    providedIn: 'root'
})
export class SalesRequestsApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #SALES_REQUESTS_API = `${this.#BASE_API}/mgmt-api/v1/catalog-sale-requests`;
    readonly #SURCHARGES_SEGMENT = '/surcharges';
    readonly #SURCHARGES_TAXES_SEGMENT = '/surcharges-taxes';
    readonly #PRICE_SIMULATION = '/price-simulation';
    readonly #COMMISSIONS_SEGMENT = '/commissions';
    readonly #SESSIONS_SEGMENT = '/sessions';
    readonly #PRICE_TYPES_SEGMENT = '/price-types';
    readonly #PROMOTIONS_SEGMENT = '/promotions';
    readonly #CONFIGURATION_SEGMENT = '/config';
    readonly #PAYMENT_METHODS_SEGMENT = '/gateways';
    readonly #PURCHASE_CONTENT_SEGMENT = '/purchase-contents';
    readonly #CHANNEL_CONTENT_SEGMENT = '/channel-contents';
    readonly #DELIVERY_CONDITIONS_SEGMENT = '/delivery';
    readonly #ADDITIONAL_CONDITIONS_SEGMENT = '/additional-agreements';

    readonly #http = inject(HttpClient);

    getSalesRequests(request: GetSalesRequestsRequest): Observable<GetSalesRequestsResponse> {
        const params = buildHttpParams({
            sort: request.sort,
            offset: request.offset,
            limit: request.limit,
            channel_entity_id: request.channelEntity,
            event_entity_id: request.eventEntity,
            channel_id: request.channel,
            status: request.status,
            event_status: request.event_status,
            q: request.q,
            include_third_party_entity_events: request.include_third_party_entity_events,
            fields: request.fields,
            date: getRangeParam(request.startDate, request.endDate),
            currency: request.currencyCode
        });
        return this.#http.get<GetSalesRequestsResponse>(this.#SALES_REQUESTS_API, {
            params
        });
    }

    getFilterOptions$(filterName: string, request: unknown): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this.#http.get<GetFilterResponse>(`${this.#SALES_REQUESTS_API}/filters/${filterName}`, { params });
    }

    getSaleRequest(saleRequestId: number): Observable<SaleRequest> {
        return this.#http.get<SaleRequest>(`${this.#SALES_REQUESTS_API}/${saleRequestId}`);
    }

    putSaleRequestStatus(saleRequestId: number, status: SalesRequestsStatus): Observable<{ status: SalesRequestsStatus }> {
        return this.#http.put<{ status: SalesRequestsStatus }>(`${this.#SALES_REQUESTS_API}/${saleRequestId}/status`, { status });
    }

    getSaleRequestSurcharges(saleRequestId: number): Observable<ChannelSurcharge[]> {
        return this.#http.get<ChannelSurcharge[]>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#SURCHARGES_SEGMENT}`);
    }

    postSaleRequestSurcharges(saleRequestId: number, surcharges: ChannelSurcharge[]): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#SURCHARGES_SEGMENT}`, surcharges);
    }

    getSaleRequestSurchargeTaxes(saleRequestId: number): Observable<SaleRequestSurchargeTaxes> {
        return this.#http.get<SaleRequestSurchargeTaxes>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#SURCHARGES_TAXES_SEGMENT}`);
    }

    putSaleRequestSurchargeTaxes(saleRequestId: number, surcharges: PutSaleRequestSurchargeTaxes): Observable<void> {
        return this.#http.put<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#SURCHARGES_TAXES_SEGMENT}`, surcharges);
    }

    getSaleRequestPriceSimulation(saleRequestId: number): Observable<ChannelPriceSimulation[]> {
        return this.#http.get<ChannelPriceSimulation[]>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PRICE_SIMULATION}`);
    }

    postSaleRequestPriceSimulationExport(saleRequestId: number, body: ExportRequest): Observable<ExportResponse> {
        return this.#http.post<ExportResponse>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PRICE_SIMULATION}/exports`, body);
    }

    getSaleRequestCommissions(saleRequestId: number): Observable<ChannelCommission[]> {
        return this.#http.get<ChannelCommission[]>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#COMMISSIONS_SEGMENT}`);
    }

    postSaleRequestCommissions(saleRequestId: number, commissions: ChannelCommission[]): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#COMMISSIONS_SEGMENT}`, commissions);
    }

    getSaleRequestConfigurationRefundAllowed(saleRequestId: number): Observable<SaleRequestConfigurationModel> {
        return this.#http.get<SaleRequestConfigurationModel>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#CONFIGURATION_SEGMENT}/allow-refund`
        );
    }

    postSaleRequestConfigurationRefundAllowed(saleRequestId: number, isRefundAllowed: boolean): Observable<void> {
        return this.#http.post<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#CONFIGURATION_SEGMENT}/allow-refund`,
            { allow_refund: isRefundAllowed }
        );
    }

    putSaleRequestConfigurationCustomCategory(saleRequestId: number, categoryId: number): Observable<void> {
        return this.#http.put<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#CONFIGURATION_SEGMENT}/event-category`,
            { id: categoryId }
        );
    }

    putSaleRequestConfigurationSubscriptionList(saleRequestId: number, subscriptionListId: number): Observable<void> {
        return this.#http.put<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#CONFIGURATION_SEGMENT}/subscription-list`,
            { id: subscriptionListId, enable: !!subscriptionListId }
        );
    }

    getSaleRequestSessions(request: GetSaleRequestSessionsRequest): Observable<SaleRequestSessionResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            q: request.q,
            offset: request.offset,
            sort: request.sort,
            status: request.status,
            day_of_week: request.weekdays,
            start_date: getRangeParam(request.start_date?.from, request.start_date?.to)
        });
        return this.#http.get<SaleRequestSessionResponse>(
            `${this.#SALES_REQUESTS_API}/${request.saleRequestId}${this.#SESSIONS_SEGMENT}`, { params }
        );
    }

    getSaleRequestPriceTypes(request: GetSaleRequestPriceTypesRequest): Observable<SaleRequestPriceTypesResponse> {
        const params = buildHttpParams({
            offset: request.offset,
            limit: request.limit,
            q: request.q
        });
        return this.#http.get<SaleRequestPriceTypesResponse>(
            `${this.#SALES_REQUESTS_API}/${request.saleRequestId}${this.#PRICE_TYPES_SEGMENT}`,
            { params }
        );
    }

    getSaleRequestPromotions(request: GetSaleRequestPromotionsRequest): Observable<GetSaleRequestPromotionsResponse> {
        const params = buildHttpParams({
            offset: request.offset,
            limit: request.limit
        });
        return this.#http.get<GetSaleRequestPromotionsResponse>(
            `${this.#SALES_REQUESTS_API}/${request.saleRequestId}${this.#PROMOTIONS_SEGMENT}`,
            { params }
        );
    }

    getSaleRequestPaymentMethods(saleRequestId: number): Observable<SaleRequestGateway> {
        return this.#http.get<SaleRequestGateway>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PAYMENT_METHODS_SEGMENT}`);
    }

    putSaleRequestPaymentMethods(saleRequestId: number, saleRequestGateways: PutSaleRequestGateways): Observable<void> {
        return this.#http.put<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PAYMENT_METHODS_SEGMENT}`, saleRequestGateways);
    }

    getPurchaseContentImages(
        saleRequestId: number, language: string, type: SaleRequestPurchaseContentImageType
    ): Observable<SaleRequestPurchaseContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this.#http.get<SaleRequestPurchaseContentImage[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PURCHASE_CONTENT_SEGMENT}/images`, { params }
        );
    }

    postPurchaseContentImages(saleRequestId: number, contents: PutSaleRequestPurchaseContentImage[]): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PURCHASE_CONTENT_SEGMENT}/images`, contents);
    }

    deletePurchaseContentImage(
        saleRequestId: number, language: string, type: SaleRequestPurchaseContentImageType
    ): Observable<void> {
        return this.#http.delete<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PURCHASE_CONTENT_SEGMENT}/images/languages/${language}/types/${type}`
        );
    }

    getPurchaseContentTexts(saleRequestId: number): Observable<SaleRequestPurchaseContentText[]> {
        return this.#http.get<SaleRequestPurchaseContentText[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PURCHASE_CONTENT_SEGMENT}/links`);
    }

    postPurchaseContentTexts(saleRequestId: number, contents: SaleRequestPurchaseContentText[]): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#PURCHASE_CONTENT_SEGMENT}/links`, contents);
    }

    getChannelContentLinks(saleRequestId: number): Observable<ContentLinkRequest[]> {
        return this.#http.get<ContentLinkRequest[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#CHANNEL_CONTENT_SEGMENT}/links`);
    }

    getChannelContentSessionLinks(
        request: GetSaleRequestChannelSessionLinksRequest
    ): Observable<ContentLinkResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            q: request.q,
            offset: request.offset,
            sort: request.sort
        });
        return this.#http.get<ContentLinkResponse>(
            `${this.#SALES_REQUESTS_API}/${request.saleRequestId}${this.#CHANNEL_CONTENT_SEGMENT}/language/${request.language}/session-links`,
            { params }
        );
    }

    getTicketContentImages(
        saleRequestId: number, format: SaleRequestTicketContentFormat, language: string, type: SaleRequestTicketContentImageType
    ): Observable<SaleRequestTicketContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this.#http.get<SaleRequestTicketContentImage[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/images`, { params }
        );
    }

    postTicketContentImages(
        saleRequestId: number, format: SaleRequestTicketContentFormat, contents: PutSaleRequestTicketContentImage[]
    ): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/images`, contents);
    }

    deleteTicketContentImage(
        saleRequestId: number, format: SaleRequestTicketContentFormat, language: string, type: SaleRequestTicketContentImageType
    ): Observable<void> {
        return this.#http.delete<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/images/languages/${language}/types/${type}`
        );
    }

    getTicketContentTemplate(
        saleRequestId: number, format: SaleRequestTicketContentFormat): Observable<SaleRequestTicketContentTemplate> {
        return this.#http.get<SaleRequestTicketContentTemplate>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/ticket-template`);
    }

    getTicketContentTexts(saleRequestId: number, format: SaleRequestTicketContentFormat): Observable<SaleRequestTicketContentText[]> {
        return this.#http.get<SaleRequestTicketContentText[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/texts`
        );
    }

    downloadTicketPdfPreview$(
        saleRequestId: number, format: SaleRequestTicketContentFormat, language: string
    ): Observable<{ url: string }> {
        const params = buildHttpParams({ language });
        return this.#http.get<{ url: string }>(`${this.#SALES_REQUESTS_API}/${saleRequestId}/ticket-contents/${format}/preview`, { params });
    }

    getSaleRequestDeliveryConditions(saleRequestId: number): Observable<SaleRequestDeliveryConditions> {
        return this.#http.get<SaleRequestDeliveryConditions>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#DELIVERY_CONDITIONS_SEGMENT}`
        );
    }

    putSaleRequestDeliveryConditions(saleRequestId: number, deliveryConditions: SaleRequestDeliveryConditions): Observable<void> {
        return this.#http.put<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#DELIVERY_CONDITIONS_SEGMENT}`, deliveryConditions);
    }

    getSaleRequestAdditionalConditions(saleRequestId: number): Observable<AdditionalCondition[]> {
        return this.#http.get<AdditionalCondition[]>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#ADDITIONAL_CONDITIONS_SEGMENT}`);
    }

    postAdditionalCondition(saleRequestId: number, additionalCondition: AdditionalCondition): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#ADDITIONAL_CONDITIONS_SEGMENT}`,
            additionalCondition
        );
    }

    putAdditionalCondition(saleRequestId: number, addCondId: number, additionalCondition: AdditionalCondition): Observable<void> {
        return this.#http.put<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#ADDITIONAL_CONDITIONS_SEGMENT}/${addCondId}`,
            additionalCondition
        );
    }

    deleteAdditionalCondition(saleRequestId: number, addCondId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}${this.#ADDITIONAL_CONDITIONS_SEGMENT}/${addCondId}`);
    }

    getEmailContentText(saleRequestId: number): Observable<SaleRequestEmailContentTexts[]> {
        return this.#http.get<SaleRequestEmailContentTexts[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/purchase-contents/texts`
        );
    }

    postEmailContentText(
        saleRequestId: number, contents: SaleRequestEmailContentTexts[]
    ): Observable<void> {
        return this.#http.post<void>(`${this.#SALES_REQUESTS_API}/${saleRequestId}/purchase-contents/texts`, contents);
    }

    getAdditionalBannerTexts(saleRequestId: number, language: string): Observable<SaleRequestAdditionalBannerTexts[]> {
        const params = buildHttpParams({ language });
        return this.#http.get<SaleRequestAdditionalBannerTexts[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/channel-contents/texts`, { params }
        );
    }

    postAdditionalBannerTexts(saleRequestId: number, textsToSave: SaleRequestAdditionalBannerTexts[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/channel-contents/texts`,
            textsToSave
        );
    }

    getSaleRequestGatewayBenefits(saleRequestId: number, gatewaySid: string, configurationSid: string
    ): Observable<SaleRequestGatewayBenefits> {
        return this.#http.get<SaleRequestGatewayBenefits>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/gateways/${gatewaySid}/configs/${configurationSid}/benefits`);
    }

    postSaleRequestGatewayBenefits(saleRequestId: number, gatewaySid: string, configurationSid: string,
        request: SaleRequestGatewayBenefits
    ): Observable<SaleRequestGatewayBenefits> {
        return this.#http.post<SaleRequestGatewayBenefits>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/gateways/${gatewaySid}/configs/${configurationSid}/benefits`, request);
    }

    getPaymentMethodsBenefitsContents(saleRequestId: number): Observable<SaleRequestBenefitsContentBadge[]> {
        return this.#http.get<SaleRequestBenefitsContentBadge[]>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/payment-benefits-contents/tags`
        );
    }

    postPaymentMethodsBenefitsContents(saleRequestId: number, data: SaleRequestBenefitsContentBadge[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#SALES_REQUESTS_API}/${saleRequestId}/payment-benefits-contents/tags`, data
        );
    }
}

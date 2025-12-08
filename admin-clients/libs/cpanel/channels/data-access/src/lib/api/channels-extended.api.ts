import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import {
    ChannelContent, ChannelHistoricalContent, ChannelPurchaseContentImage, ChannelPurchaseContentImageType, ChannelPurchaseContentText,
    ChannelTicketContentFormat, ChannelTicketContentImage, ChannelTicketContentImageType, ChannelTicketContentText,
    PutChannelPurchaseContentImage, PutChannelTicketContentImage
} from '@admin-clients/cpanel/channels/communication/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AdditionalCondition } from '../models/additional-condition.model';
import { ChannelAuthVendorsSso, ChannelAuthVendorsUserData } from '../models/auth-vendors-model';
import { ChannelCancellationServices, PutChannelCancellationServices } from '../models/cancellation-services.model';
import { ChannelBlacklistItem } from '../models/channel-blacklist-item.model';
import { ChannelBlacklistStatus } from '../models/channel-blacklist-status.model';
import { ChannelBlacklistType } from '../models/channel-blacklist-type.enum';
import { ChannelBookingSettings } from '../models/channel-booking-settings.model';
import { ChannelCommission } from '../models/channel-commission.model';
import { ChannelCrossSaleRestriction } from '../models/channel-cross-sale-restriction.model';
import { ChannelDeliverySettings, PutChannelDeliverySettings } from '../models/channel-delivery-settings.model';
import { ChannelExternalTool, ChannelExternalToolName } from '../models/channel-external-tool.model';
import { ChannelFormsDataType } from '../models/channel-forms-data-type.model';
import { ChannelForms } from '../models/channel-forms.model';
import { ChannelGatewayConfigRequest } from '../models/channel-gateway-config-request.model';
import { ChannelGatewayConfig } from '../models/channel-gateway-config.model';
import { ChannelGateway } from '../models/channel-gateway.model';
import { ChannelPurchaseConfig } from '../models/channel-purchase-config.model';
import { ChannelSessionsFilter, GetChannelSessionsResponse } from '../models/channel-session.model';
import { ChannelSharingSettings } from '../models/channel-sharing-settings.model';
import { ChannelSuggestionType } from '../models/channel-suggestion-type';
import { ChannelSurchargeTaxes, PutChannelSurchargeTaxes } from '../models/channel-surcharge-taxes.model';
import { ChannelSurcharge } from '../models/channel-surcharge.model';
import { TextContent } from '../models/channel-text-content';
import { EmailServerConf } from '../models/email-server-conf.model';
import { EmailServerTestRequest } from '../models/email-server-test-request.model';
import { GetChannelBlacklistRequest } from '../models/get-channel-blacklist-request.model';
import { GetChannelBlacklistResponse } from '../models/get-channel-blacklist-response.model';
import { GetChannelEventsRequest } from '../models/get-channel-events-request.model';
import { GetChannelEventsResponse } from '../models/get-channel-events-response.model';
import { GetChannelSuggestionsRequest } from '../models/get-channel-suggestions-request.model';
import { GetChannelSuggestionsResponse } from '../models/get-channel-suggestions-response.model';
import { NotificationEmailTemplate } from '../models/notification-email-template.model';
import { PostChannelContentsCloneRequest } from '../models/post-channel-contents-clone-request.model';
import { PostChannelContentsCloneResponseItem } from '../models/post-channel-contents-clone-response.model';
import { PostChannelGateway } from '../models/post-channel-gateway.model';
import { PostChannelSuggestionReq } from '../models/post-channel-suggestion-request.model';
import { PutChannelEventRequest } from '../models/put-channel-events-request.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelsExtendedApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    private readonly SURCHARGES_SEGMENT = '/surcharges';
    private readonly SURCHARGES_TAXES_SEGMENT = '/surcharges-taxes';
    private readonly COMMISSIONS_SEGMENT = '/commissions';
    private readonly DELIVERY_METHODS_SEGMENT = '/delivery';
    private readonly ADDITIONAL_CONDITIONS_SEGMENT = '/additional-agreements';
    private readonly BLACKLIST_SEGMENT = '/blacklists';
    private readonly EVENTS_SEGMENT = '/events';
    private readonly SUGGESTIONS_SEGMENT = '/suggestions';
    private readonly BOOKING_SEGMENT = '/booking-settings';
    private readonly SHARING_SEGMENT = '/sharing';
    private readonly SESIONS_SEGMENT = '/sessions';

    private readonly TEXT_CONTENTS_SEGMENT = '/text-contents';
    private readonly CONTENTS_SEGMENT = '/contents';
    private readonly LANGUAGES_SEGMENT = '/languages';

    private readonly PAYMENT_METHODS_SEGMENT = '/gateways';
    private readonly CONFIGURATIONS_SEGMENT = '/configurations';
    private readonly EXTERNAL_TOOLS_SEGMENT = '/external-tools';
    private readonly PURCHASE_CONFIG = '/purchase-config';
    private readonly NOTIFICATIONS_EMAIL_SERVER = '/notifications/email/server';
    private readonly NOTIFICATIONS_EMAIL_TEMPLATES = '/notifications/email/templates';
    private readonly CANCELLATION_SERVICES = '/cancellation-services';
    private readonly VENDOR_USER_DATA = '/auth-vendors/user-data';
    private readonly VENDOR_SSO = '/auth-vendors/sso';
    private readonly SALE_RESTRICTIONS = '/event-sale-restrictions';

    private readonly _http = inject(HttpClient);

    // OPERATIVE

    getChannelSurcharges(channelId: string): Observable<ChannelSurcharge[]> {
        return this._http.get<ChannelSurcharge[]>(`${this.CHANNELS_API}/${channelId}${this.SURCHARGES_SEGMENT}`);
    }

    postChannelSurcharges(channelId: string, surcharges: ChannelSurcharge[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.SURCHARGES_SEGMENT}`, surcharges);
    }

    getChannelSurchargeTaxes(channelId: number): Observable<ChannelSurchargeTaxes> {
        return this._http.get<ChannelSurchargeTaxes>(`${this.CHANNELS_API}/${channelId}${this.SURCHARGES_TAXES_SEGMENT}`);
    }

    putChannelSurchargeTaxes(channelId: number, surcharges: PutChannelSurchargeTaxes): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.SURCHARGES_TAXES_SEGMENT}`, surcharges);
    }

    getChannelCommissions(channelId: string): Observable<ChannelCommission[]> {
        return this._http.get<ChannelCommission[]>(`${this.CHANNELS_API}/${channelId}${this.COMMISSIONS_SEGMENT}`);
    }

    postChannelCommissions(channelId: string, commissions: ChannelCommission[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.COMMISSIONS_SEGMENT}`, commissions);
    }

    getChannelDeliveryMethods(channelId: string): Observable<ChannelDeliverySettings> {
        return this._http.get<ChannelDeliverySettings>(`${this.CHANNELS_API}/${channelId}${this.DELIVERY_METHODS_SEGMENT}`);
    }

    putChannelDeliveryMethods(channelId: string, deliveryMethods: PutChannelDeliverySettings): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.DELIVERY_METHODS_SEGMENT}`, deliveryMethods);
    }

    getChannelForms(channelId: number, type: ChannelFormsDataType): Observable<ChannelForms> {
        return this._http.get<ChannelForms>(`${this.CHANNELS_API}/${channelId}/${type}`);
    }

    putChannelForms(channelId: number, channelForms: ChannelForms, type: ChannelFormsDataType): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}/${type}`, channelForms);
    }

    getAdditionalConditions(channelId: number): Observable<AdditionalCondition[]> {
        return this._http.get<AdditionalCondition[]>(`${this.CHANNELS_API}/${channelId}${this.ADDITIONAL_CONDITIONS_SEGMENT}`);
    }

    postAdditionalCondition(channelId: number, additionalCondition: AdditionalCondition): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(
            `${this.CHANNELS_API}/${channelId}${this.ADDITIONAL_CONDITIONS_SEGMENT}`,
            additionalCondition
        );
    }

    putAdditionalCondition(channelId: number, addCondId: number, additionalCondition: AdditionalCondition): Observable<void> {
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.ADDITIONAL_CONDITIONS_SEGMENT}/${addCondId}`,
            additionalCondition
        );
    }

    deleteAdditionalCondition(channelId: number, addCondId: number): Observable<void> {
        return this._http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.ADDITIONAL_CONDITIONS_SEGMENT}/${addCondId}`);
    }

    getChannelEvents(channelId: number, filter?: GetChannelEventsRequest): Observable<GetChannelEventsResponse> {
        const params = buildHttpParams({
            limit: filter?.limit,
            offset: filter?.offset,
            q: filter?.q,
            published: typeof filter?.published === 'boolean' ? filter.published : undefined,
            on_sale: typeof filter?.on_sale === 'boolean' ? filter.published : undefined
        });
        return this._http.get<GetChannelEventsResponse>(`${this.CHANNELS_API}/${channelId}${this.EVENTS_SEGMENT}`, { params });
    }

    putChannelEvents(channelId: number, req: PutChannelEventRequest[]): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.EVENTS_SEGMENT}`, req);
    }

    getChannelSessions(
        channelId: number, eventId: number, request?: ChannelSessionsFilter
    ): Observable<GetChannelSessionsResponse> {
        const params = buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            day_of_week: request.weekdays,
            timezone: request.timezone,
            start_date: getRangeParam(request.initStartDate, request.finalStartDate)
        });
        return this._http.get<GetChannelSessionsResponse>(
            `${this.CHANNELS_API}/${channelId}${this.EVENTS_SEGMENT}/${eventId}${this.SESIONS_SEGMENT}`, { params }
        );
    }

    getChannelSuggestions(channelId: number, filter: GetChannelSuggestionsRequest): Observable<GetChannelSuggestionsResponse> {
        const params = filter ? buildHttpParams(filter) : {};
        return this._http.get<GetChannelSuggestionsResponse>(`${this.CHANNELS_API}/${channelId}${this.SUGGESTIONS_SEGMENT}`, { params });
    }

    postChannelSuggestion(channelId: number, type: ChannelSuggestionType, id: number, req: PostChannelSuggestionReq[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.SUGGESTIONS_SEGMENT}/${type}/${id}`, req);
    }

    deleteChannelSuggestion(
        channelId: number, source: { type: ChannelSuggestionType; id: number }, target: { type: ChannelSuggestionType; id: number }
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.CHANNELS_API}/${channelId}${this.SUGGESTIONS_SEGMENT}/${source.type}/${source.id}/targets/${target.type}/${target.id}`
        );
    }

    deleteSourceTargets(channelId: number, source: { type: ChannelSuggestionType; id: number }): Observable<void> {
        return this._http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.SUGGESTIONS_SEGMENT}/${source.type}/${source.id}`);
    }

    getChannelBookingSettings(channelId: string): Observable<ChannelBookingSettings> {
        return this._http.get<ChannelBookingSettings>(`${this.CHANNELS_API}/${channelId}${this.BOOKING_SEGMENT}`);
    }

    putChannelBookingSettings(channelId: string, bookingConfig: ChannelBookingSettings): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.BOOKING_SEGMENT}`, bookingConfig);
    }

    getChannelBlacklist(
        channelId: number, type: ChannelBlacklistType, filter?: GetChannelBlacklistRequest
    ): Observable<GetChannelBlacklistResponse> {
        const params = buildHttpParams({
            limit: filter?.limit,
            offset: filter?.offset,
            sort: filter?.sort,
            q: filter?.q,
            date: getRangeParam(filter?.startDate, filter?.endDate)
        });
        return this._http.get<GetChannelBlacklistResponse>(
            `${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}`, { params }
        );
    }

    postChannelBlacklist(channelId: number, type: ChannelBlacklistType, blacklist: ChannelBlacklistItem[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}`, blacklist);
    }

    deleteChannelBlacklist(channelId: number, type: ChannelBlacklistType): Observable<void> {
        return this._http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}`);
    }

    getChannelBlacklistStatus(channelId: number, type: ChannelBlacklistType): Observable<ChannelBlacklistStatus> {
        return this._http.get<ChannelBlacklistStatus>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}/status`);
    }

    putChannelBlacklistStatus(channelId: number, type: ChannelBlacklistType, status: ChannelBlacklistStatus): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}/status`, status);
    }

    getChannelBlacklistItem(channelId: number, type: ChannelBlacklistType, value: string): Observable<ChannelBlacklistItem> {
        return this._http.get<ChannelBlacklistItem>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}/${value}`);
    }

    deleteChannelBlacklistItem(channelId: number, type: ChannelBlacklistType, value: string): Observable<void> {
        return this._http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.BLACKLIST_SEGMENT}/${type}/${value}`);
    }

    getSharingSettings(channelId: number): Observable<ChannelSharingSettings> {
        return this._http.get<ChannelSharingSettings>(`${this.CHANNELS_API}/${channelId}${this.SHARING_SEGMENT}`);
    }

    putSharingSettings(channelId: number, sharingSettings: ChannelSharingSettings): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.SHARING_SEGMENT}`, sharingSettings);
    }

    // COMMUNICATION

    postContentsClone(channelId: number, reqBody: PostChannelContentsCloneRequest): Observable<PostChannelContentsCloneResponseItem[]> {
        return this._http.post<PostChannelContentsCloneResponseItem[]>(
            `${this.CHANNELS_API}/${channelId}${this.CONTENTS_SEGMENT}/clone`,
            reqBody
        );
    }

    getTextContents(channelId: number, languageId: string, newLiterals: boolean): Observable<TextContent[]> {
        const params = buildHttpParams({
            channelVersion: newLiterals ? 'V2' : undefined
        });
        return this._http.get<TextContent[]>(
            `${this.CHANNELS_API}/${channelId}${this.TEXT_CONTENTS_SEGMENT}${this.LANGUAGES_SEGMENT}/${languageId}`,
            { params }
        );
    }

    postTextContents(channelId: number, languageId: string, textContents: TextContent[], newLiterals: boolean): Observable<void> {
        const params = buildHttpParams({
            channelVersion: newLiterals ? 'V2' : undefined
        });
        return this._http.post<void>(
            `${this.CHANNELS_API}/${channelId}${this.TEXT_CONTENTS_SEGMENT}${this.LANGUAGES_SEGMENT}/${languageId}`,
            textContents,
            { params }
        );
    }

    getContents(channelId: number, category: string, language?: string): Observable<ChannelContent[]> {
        const params = language ? buildHttpParams({ language }) : {};
        return this._http.get<ChannelContent[]>(
            `${this.CHANNELS_API}/${channelId}${this.CONTENTS_SEGMENT}/${category}`, { params }
        );
    }

    getHistoricalContent(channelId: number, contentId: number, language: string): Observable<ChannelHistoricalContent[]> {
        const params = buildHttpParams({ language });
        return this._http.get<ChannelHistoricalContent[]>(
            `${this.CHANNELS_API}/${channelId}${this.CONTENTS_SEGMENT}/${contentId}/historical`, { params }
        );
    }

    putContents(channelId: number, category: string, contents: ChannelContent[], languageId?: string): Observable<void> {
        const params = languageId ? contents.map(content => ({
            ...content,
            language: languageId
        })) : contents;
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.CONTENTS_SEGMENT}/${category}`, params);
    }

    putProfiledContents(channelId: number, contentId: string, contents: ChannelContent[]): Observable<void> {
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.CONTENTS_SEGMENT}/${contentId}/profiles`,
            contents
        );
    }

    getTicketContentImages(
        channelId: number, format: ChannelTicketContentFormat, language: string, type: ChannelTicketContentImageType
    ): Observable<ChannelTicketContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<ChannelTicketContentImage[]>(
            `${this.CHANNELS_API}/${channelId}/ticket-contents/${format}/images`, { params }
        );
    }

    putTicketContentImages(
        channelId: number, format: ChannelTicketContentFormat, contents: PutChannelTicketContentImage[]
    ): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}/ticket-contents/${format}/images`, contents);
    }

    deleteTicketContentImage(
        channelId: number, format: ChannelTicketContentFormat, language: string, type: ChannelTicketContentImageType
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.CHANNELS_API}/${channelId}/ticket-contents/${format}/images/languages/${language}/types/${type}`
        );
    }

    getTicketContentTexts(channelId: number, format: ChannelTicketContentFormat): Observable<ChannelTicketContentText[]> {
        return this._http.get<ChannelTicketContentText[]>(`${this.CHANNELS_API}/${channelId}/ticket-contents/${format}/texts`);
    }

    putTicketContentTexts(channelId: number, format: ChannelTicketContentFormat, contents: ChannelTicketContentText[]): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}/ticket-contents/${format}/texts`, contents);
    }

    getPurchaseContentImages(
        channelId: number, language: string, type: ChannelPurchaseContentImageType
    ): Observable<ChannelPurchaseContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<ChannelPurchaseContentImage[]>(
            `${this.CHANNELS_API}/${channelId}/purchase-contents/images`, { params }
        );
    }

    postPurchaseContentImages(channelId: number, contents: PutChannelPurchaseContentImage[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}/purchase-contents/images`, contents);
    }

    deletePurchaseContentImage(
        channelId: number, language: string, type: ChannelPurchaseContentImageType
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.CHANNELS_API}/${channelId}/purchase-contents/images/languages/${language}/types/${type}`
        );
    }

    getPurchaseContentTexts(channelId: number): Observable<ChannelPurchaseContentText[]> {
        return this._http.get<ChannelPurchaseContentText[]>(`${this.CHANNELS_API}/${channelId}/purchase-contents/texts`);
    }

    postPurchaseContentTexts(channelId: number, contents: ChannelPurchaseContentText[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}/purchase-contents/texts`, contents);
    }

    // CONFIGURATION

    postEmailServerTest(channelId: number, email: EmailServerTestRequest): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}/notifications/email/server/test`, email);
    }

    getPaymentMethods(channelId: string): Observable<ChannelGateway[]> {
        return this._http.get<ChannelGateway[]>(`${this.CHANNELS_API}/${channelId}${this.PAYMENT_METHODS_SEGMENT}`);
    }

    putChannelPaymentMethods(channelId: string, paymentMethods: PostChannelGateway[]): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PAYMENT_METHODS_SEGMENT}`, paymentMethods);
    }

    getGatewayConfig(channelId: string, gatewayId: string, configId: string): Observable<ChannelGatewayConfig> {
        return this._http.get<ChannelGatewayConfig>(
            `${this.CHANNELS_API}/${channelId}` +
            `${this.PAYMENT_METHODS_SEGMENT}/${gatewayId}` +
            `${this.CONFIGURATIONS_SEGMENT}/${configId}`);
    }

    deleteChannelGatewayConfig(channelId: number, gatewayId: string, configId: string): Observable<void> {
        return this._http.delete<void>(
            `${this.CHANNELS_API}/${channelId}` +
            `${this.PAYMENT_METHODS_SEGMENT}/${gatewayId}` +
            `${this.CONFIGURATIONS_SEGMENT}/${configId}`
        );
    }

    postChannelGatewayConfig(
        channelId: string, gatewayId: string, paymentMethod: ChannelGatewayConfigRequest
    ): Observable<{ configSid: number }> {
        return this._http.post<{ configSid: number }>(
            `${this.CHANNELS_API}/${channelId}` +
            `${this.PAYMENT_METHODS_SEGMENT}/${gatewayId}` +
            `${this.CONFIGURATIONS_SEGMENT}`, paymentMethod);
    }

    putChannelGatewayConfig(channelId: string,
        gatewayId: string, configId: string,
        gatewayConfig: ChannelGatewayConfigRequest): Observable<void> {
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}` +
            `${this.PAYMENT_METHODS_SEGMENT}/${gatewayId}` +
            `${this.CONFIGURATIONS_SEGMENT}/${configId}`,
            gatewayConfig
        );
    }

    getExternalTools(channelId: number): Observable<ChannelExternalTool[]> {
        return this._http.get<ChannelExternalTool[]>(`${this.CHANNELS_API}/${channelId}${this.EXTERNAL_TOOLS_SEGMENT}`);
    }

    putExternalTool(channelId: number, toolName: string, toolConfig: ChannelExternalTool): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.EXTERNAL_TOOLS_SEGMENT}/${toolName}`, toolConfig);
    }

    postDatalayerReset(channelId: number, externalTool: ChannelExternalToolName): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.EXTERNAL_TOOLS_SEGMENT}/${externalTool}/reset`, []);
    }

    getPurchaseConfig(channelId: number): Observable<ChannelPurchaseConfig> {
        return this._http.get<ChannelPurchaseConfig>(`${this.CHANNELS_API}/${channelId}${this.PURCHASE_CONFIG}`);
    }

    putPurchaseConfig(channelId: number, config: ChannelPurchaseConfig): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PURCHASE_CONFIG}`, config);
    }

    getNotificationsEmailServer(channelId: number): Observable<EmailServerConf> {
        return this._http.get<EmailServerConf>(`${this.CHANNELS_API}/${channelId}${this.NOTIFICATIONS_EMAIL_SERVER}`);
    }

    putNotificationsEmailServer(channelId: number, conf: EmailServerConf): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.NOTIFICATIONS_EMAIL_SERVER}`, conf);
    }

    getNotificationsEmailTemplates(channelId: number): Observable<NotificationEmailTemplate[]> {
        return this._http.get<NotificationEmailTemplate[]>(`${this.CHANNELS_API}/${channelId}${this.NOTIFICATIONS_EMAIL_TEMPLATES}`);
    }

    putNotificationsEmailTemplates(channelId: number, templates: NotificationEmailTemplate[]): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.NOTIFICATIONS_EMAIL_TEMPLATES}`, templates);
    }

    getCancellationServices(channelId: number): Observable<ChannelCancellationServices> {
        return this._http.get<ChannelCancellationServices>(`${this.CHANNELS_API}/${channelId}${this.CANCELLATION_SERVICES}`);
    }

    putCancellationServices(channelId: number, cancellationServices: PutChannelCancellationServices): Observable<void> {
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.CANCELLATION_SERVICES}`, cancellationServices
        );
    }

    // Auth Vendors

    getAuthVendorsUserData(channelId: number): Observable<ChannelAuthVendorsUserData> {
        return this._http.get<ChannelAuthVendorsUserData>(`${this.CHANNELS_API}/${channelId}${this.VENDOR_USER_DATA}`);
    }

    putAuthVendorsUserData(channelId: number, payload: ChannelAuthVendorsUserData): Observable<void> {
        return this._http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.VENDOR_USER_DATA}`, payload
        );
    }

    getAuthVendorsSso(channelId: number): Observable<ChannelAuthVendorsSso> {
        return this._http.get<ChannelAuthVendorsSso>(`${this.CHANNELS_API}/${channelId}${this.VENDOR_SSO}`);
    }

    putAuthVendorsSso(channelId: number, payload: ChannelAuthVendorsSso): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.VENDOR_SSO}`, payload);
    }

    // Event Sales Restrictions - Cross Event Restrictions

    getCrossSaleRestrictions(channelId: number): Observable<ChannelCrossSaleRestriction[]> {
        return this._http.get<ChannelCrossSaleRestriction[]>(`${this.CHANNELS_API}/${channelId}${this.SALE_RESTRICTIONS}`);
    }

    putCrossSaleRestrictions(channelId: number, payload: ChannelCrossSaleRestriction[]): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.SALE_RESTRICTIONS}`, payload);
    }

}

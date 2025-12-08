import { StateProperty } from '@OneboxTM/utils-state';
import {
    ChannelSurcharge, ChannelPriceSimulation, ChannelCommission, AdditionalCondition
} from '@admin-clients/cpanel/channels/data-access';
import { ContentLinkRequest, ContentLinkResponse } from '@admin-clients/cpanel/shared/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { GetFilterResponse } from '../models/get-filter-response';
import { GetSalesRequestsResponse } from '../models/get-sales-requests-response';
import { SaleRequestAdditionalBannerTexts } from '../models/sale-request-additional-banner-texts.model';
import { SaleRequestBenefitsContentBadge } from '../models/sale-request-benefits-content-badge.model';
import { SaleRequestChannelContentImage } from '../models/sale-request-channel-content-image.model';
import { SaleRequestChannelContentText } from '../models/sale-request-channel-content-text.model';
import { SaleRequestConfigurationModel } from '../models/sale-request-configuration.model';
import { SaleRequestDeliveryConditions } from '../models/sale-request-delivery-conditions.model';
import { SaleRequestEmailContentTexts } from '../models/sale-request-email-content-texts.model';
import {
    SaleRequestGateway,
    SaleRequestGatewayBenefits
} from '../models/sale-request-gateway.model';
import { SaleRequestPriceTypesResponse } from '../models/sale-request-price-types.model';
import { GetSaleRequestPromotionsResponse } from '../models/sale-request-promotion.model';
import { SaleRequestPurchaseContentImage } from '../models/sale-request-purchase-content-image.model';
import { SaleRequestPurchaseContentText } from '../models/sale-request-purchase-content-text.model';
import { SaleRequestSessionResponse } from '../models/sale-request-session.model';
import { SaleRequestSurchargeTaxes } from '../models/sale-request-surcharge-taxes.model';
import { SaleRequestTicketContentImage } from '../models/sale-request-ticket-content-image.model';
import { SaleRequestTicketContentTemplate } from '../models/sale-request-ticket-content-template.model';
import { SaleRequestTicketContentText } from '../models/sale-request-ticket-content-text.model';
import { SaleRequest } from '../models/sale-request.model';

@Injectable({
    providedIn: 'root'
})
export class SalesRequestsState {
    // SalesRequests list
    private _salesRequestsList = new BaseStateProp<GetSalesRequestsResponse>();
    readonly setSalesRequestsList = this._salesRequestsList.setValueFunction();
    readonly getSalesRequestsList$ = this._salesRequestsList.getValueFunction();
    readonly setSalesRequestsListLoading = this._salesRequestsList.setInProgressFunction();
    readonly isSalesRequestsListLoading$ = this._salesRequestsList.getInProgressFunction();

    private _allsalesRequests = new BaseStateProp<GetSalesRequestsResponse>();
    readonly setAllSalesRequestsList = this._allsalesRequests.setValueFunction();
    readonly getAllSalesRequestsList$ = this._allsalesRequests.getValueFunction();
    readonly setAllSalesRequestsListLoading = this._allsalesRequests.setInProgressFunction();
    readonly isAllSalesRequestsListLoading$ = this._allsalesRequests.getInProgressFunction();

    // Filters list
    private _filterChannelList = new BehaviorSubject<GetFilterResponse>(null);
    private _filterChannelList$ = this._filterChannelList.asObservable();
    private _filterChannelEntityList = new BehaviorSubject<GetFilterResponse>(null);
    private _filterChannelEntityList$ = this._filterChannelEntityList.asObservable();
    private _filterEventEntityList = new BehaviorSubject<GetFilterResponse>(null);
    private _filterEventEntityList$ = this._filterEventEntityList.asObservable();

    // Sale Request detail
    private _saleRequest = new BaseStateProp<SaleRequest>();
    readonly setSaleRequest = this._saleRequest.setValueFunction();
    readonly getSaleRequest$ = this._saleRequest.getValueFunction();
    readonly setSaleRequestLoading = this._saleRequest.setInProgressFunction();
    readonly isSaleRequestLoading$ = this._saleRequest.getInProgressFunction();
    readonly getSaleRequestError$ = this._saleRequest.getErrorFunction();
    readonly setSaleRequestError = this._saleRequest.setErrorFunction();

    // channel surcharges
    private _saleRequestSurcharges = new BaseStateProp<ChannelSurcharge[]>();
    readonly setSaleRequestSurcharges = this._saleRequestSurcharges.setValueFunction();
    readonly getSaleRequestSurcharges$ = this._saleRequestSurcharges.getValueFunction();
    readonly setSaleRequestSurchargesLoading = this._saleRequestSurcharges.setInProgressFunction();
    readonly isSaleRequestSurchargesLoading$ = this._saleRequestSurcharges.getInProgressFunction();
    readonly setSaleRequestSurchargesError = this._saleRequestSurcharges.setErrorFunction();
    readonly getSaleRequestSurchargesError$ = this._saleRequestSurcharges.getErrorFunction();
    // post channel surcharges
    private _saleRequestSurchargesSaving = new BaseStateProp<void>();
    readonly setSaleRequestSurchargesSaving = this._saleRequestSurchargesSaving.setInProgressFunction();
    readonly isSaleRequestSurchargesSaving$ = this._saleRequestSurchargesSaving.getInProgressFunction();

    // sale request surcharge taxes
    readonly saleRequestSurchargeTaxes = new StateProperty<SaleRequestSurchargeTaxes>();

    // saleRequest commissions
    private _saleRequestCommissions = new BaseStateProp<ChannelCommission[]>();
    readonly setSaleRequestCommissions = this._saleRequestCommissions.setValueFunction();
    readonly getSaleRequestCommissions$ = this._saleRequestCommissions.getValueFunction();
    readonly setSaleRequestCommissionsLoading = this._saleRequestCommissions.setInProgressFunction();
    readonly isSaleRequestCommissionsLoading$ = this._saleRequestCommissions.getInProgressFunction();
    readonly setSaleRequestCommissionsError = this._saleRequestCommissions.setErrorFunction();
    readonly getSaleRequestCommissionsError$ = this._saleRequestCommissions.getErrorFunction();
    // post saleRequest commissions
    private _saleRequestCommissionsSaving = new BaseStateProp<void>();
    readonly setSaleRequestCommissionsSaving = this._saleRequestCommissionsSaving.setInProgressFunction();
    readonly isSaleRequestCommissionsSaving$ = this._saleRequestCommissionsSaving.getInProgressFunction();

    // saleRequest configuration
    private _saleRequestConfiguration = new BaseStateProp<SaleRequestConfigurationModel>();
    readonly setSaleRequestConfiguration = this._saleRequestConfiguration.setValueFunction();
    readonly getSaleRequestConfiguration$ = this._saleRequestConfiguration.getValueFunction();
    readonly setSaleRequestConfigurationLoading = this._saleRequestConfiguration.setInProgressFunction();
    readonly isSaleRequestConfigurationLoading$ = this._saleRequestConfiguration.getInProgressFunction();
    readonly setSaleRequestConfigurationError = this._saleRequestConfiguration.setErrorFunction();
    readonly getSaleRequestConfigurationError$ = this._saleRequestConfiguration.getErrorFunction();
    // post saleRequest configuration
    private _saleRequestConfigurationSaving = new BaseStateProp<void>();
    readonly setSaleRequestConfigurationSaving = this._saleRequestConfigurationSaving.setInProgressFunction();
    readonly isSaleRequestConfigurationSaving$ = this._saleRequestConfigurationSaving.getInProgressFunction();

    // saleRequest sessions
    private _saleRequestSessions = new BaseStateProp<SaleRequestSessionResponse>();
    readonly setSaleRequestSessions = this._saleRequestSessions.setValueFunction();
    readonly getSaleRequestSessions$ = this._saleRequestSessions.getValueFunction();
    readonly setSaleRequestSessionsLoading = this._saleRequestSessions.setInProgressFunction();
    readonly isSaleRequestSessionsLoading$ = this._saleRequestSessions.getInProgressFunction();
    readonly setSaleRequestSessionsError = this._saleRequestSessions.setErrorFunction();
    readonly getSaleRequestSessionsError$ = this._saleRequestSessions.getErrorFunction();

    private _saleRequestAllSessions = new BaseStateProp<SaleRequestSessionResponse>();
    readonly setAllSessionsList = this._saleRequestAllSessions.setValueFunction();
    readonly getAllSessionsList$ = this._saleRequestAllSessions.getValueFunction();
    readonly setAllSessionsListLoading = this._saleRequestAllSessions.setInProgressFunction();
    readonly isAllSessionsListLoading$ = this._saleRequestAllSessions.getInProgressFunction();

    // saleRequest price types

    private _saleReqPriceTypes = new BaseStateProp<SaleRequestPriceTypesResponse>();
    readonly setSaleRequestPriceTypes = this._saleReqPriceTypes.setValueFunction();
    readonly getSaleRequestPriceTypes$ = this._saleReqPriceTypes.getValueFunction();
    readonly setSaleRequestPriceTypesLoading = this._saleReqPriceTypes.setInProgressFunction();
    readonly isSaleRequestPriceTypesLoading$ = this._saleReqPriceTypes.getInProgressFunction();
    readonly setSaleRequestPriceTypesError = this._saleReqPriceTypes.setErrorFunction();
    readonly getSaleRequestPriceTypesError$ = this._saleReqPriceTypes.getErrorFunction();

    private _saleReqAllPriceTypes = new BaseStateProp<SaleRequestPriceTypesResponse>();
    readonly setAllPriceTypesList = this._saleReqAllPriceTypes.setValueFunction();
    readonly getAllPriceTypesList$ = this._saleReqAllPriceTypes.getValueFunction();
    readonly setAllPriceTypesListLoading = this._saleReqAllPriceTypes.setInProgressFunction();
    readonly isAllPriceTypesListLoading$ = this._saleReqAllPriceTypes.getInProgressFunction();

    // saleRequest promotions
    private _saleRequestPromotions = new BaseStateProp<GetSaleRequestPromotionsResponse>();
    readonly setSaleRequestPromotions = this._saleRequestPromotions.setValueFunction();
    readonly getSaleRequestPromotions$ = this._saleRequestPromotions.getValueFunction();
    readonly setSaleRequestPromotionsLoading = this._saleRequestPromotions.setInProgressFunction();
    readonly isSaleRequestPromotionsLoading$ = this._saleRequestPromotions.getInProgressFunction();
    readonly setSaleRequestPromotionsError = this._saleRequestPromotions.setErrorFunction();
    readonly getSaleRequestPromotionsError$ = this._saleRequestPromotions.getErrorFunction();

    // Payment Methods / Gateways
    private readonly _saleRequestPaymentMethods = new BaseStateProp<SaleRequestGateway>();
    readonly setSaleRequestPaymentMethods = this._saleRequestPaymentMethods.setValueFunction();
    readonly getSaleRequestPaymentMethods$ = this._saleRequestPaymentMethods.getValueFunction();
    readonly setSaleRequestPaymentMethodsLoading = this._saleRequestPaymentMethods.setInProgressFunction();
    readonly isSaleRequestPaymentMethodsLoading$ = this._saleRequestPaymentMethods.getInProgressFunction();
    readonly setSaleRequestPaymentMethodsError = this._saleRequestPaymentMethods.setErrorFunction();
    readonly getSaleRequestPaymentMethodsError$ = this._saleRequestPaymentMethods.getErrorFunction();
    // post channel Payment Methods / Gateways
    private readonly _saleRequestPaymentMethodsSaving = new BaseStateProp<void>();
    readonly setSaleRequestPaymentMethodsSaving = this._saleRequestPaymentMethodsSaving.setInProgressFunction();
    readonly isSaleRequestPaymentMethodsSaving$ = this._saleRequestPaymentMethodsSaving.getInProgressFunction();

    // purchase content images
    private readonly _purchaseContentImages = new BaseStateProp<SaleRequestPurchaseContentImage[]>();
    readonly setPurchaseContentImages = this._purchaseContentImages.setValueFunction();
    readonly getPurchaseContentImages$ = this._purchaseContentImages.getValueFunction();
    readonly setPurchaseContentImagesLoading = this._purchaseContentImages.setInProgressFunction();
    readonly isPurchaseContentImagesLoading$ = this._purchaseContentImages.getInProgressFunction();
    // purchase content images saving
    private readonly _purchaseContentImagesSaving = new BaseStateProp<void>();
    readonly setPurchaseContentImagesSaving = this._purchaseContentImagesSaving.setInProgressFunction();
    readonly isPurchaseContentImagesSaving$ = this._purchaseContentImagesSaving.getInProgressFunction();
    // purchase content images removing
    private readonly _purchaseContentImagesRemoving = new BaseStateProp<void>();
    readonly setPurchaseContentImagesRemoving = this._purchaseContentImagesRemoving.setInProgressFunction();
    readonly isPurchaseContentImagesRemoving$ = this._purchaseContentImagesRemoving.getInProgressFunction();

    // purchase content texts
    private readonly _purchaseContentTexts = new BaseStateProp<SaleRequestPurchaseContentText[]>();
    readonly setPurchaseContentTexts = this._purchaseContentTexts.setValueFunction();
    readonly getPurchaseContentTexts$ = this._purchaseContentTexts.getValueFunction();
    readonly setPurchaseContentTextsLoading = this._purchaseContentTexts.setInProgressFunction();
    readonly isPurchaseContentTextsLoading$ = this._purchaseContentTexts.getInProgressFunction();
    // purchase content texts saving
    private readonly _purchaseContentTextsSaving = new BaseStateProp<void>();
    readonly setPurchaseContentTextsSaving = this._purchaseContentTextsSaving.setInProgressFunction();
    readonly isPurchaseContentTextsSaving$ = this._purchaseContentTextsSaving.getInProgressFunction();

    // channel content texts
    private readonly _channelContentTexts = new BaseStateProp<SaleRequestChannelContentText[]>();
    readonly setChannelContentTexts = this._channelContentTexts.setValueFunction();
    readonly getChannelContentTexts$ = this._channelContentTexts.getValueFunction();
    readonly setChannelContentTextsLoading = this._channelContentTexts.setInProgressFunction();
    readonly isChannelContentTextsLoading$ = this._channelContentTexts.getInProgressFunction();

    // channel content images
    private readonly _channelContentImages = new BaseStateProp<SaleRequestChannelContentImage[]>();
    readonly setChannelContentImages = this._channelContentImages.setValueFunction();
    readonly getChannelContentImages$ = this._channelContentImages.getValueFunction();
    readonly setChannelContentImagesLoading = this._channelContentImages.setInProgressFunction();
    readonly isChannelContentImagesLoading$ = this._channelContentImages.getInProgressFunction();

    // channel content links
    private readonly _channelContentLinks = new BaseStateProp<ContentLinkRequest[]>();
    readonly setChannelContentLinks = this._channelContentLinks.setValueFunction();
    readonly getChannelContentLinks$ = this._channelContentLinks.getValueFunction();
    readonly setChannelContentLinksLoading = this._channelContentLinks.setInProgressFunction();
    readonly isChannelContentLinksLoading$ = this._channelContentLinks.getInProgressFunction();

    readonly salesRequestSessionLinks = new StateProperty<ContentLinkResponse>();
    readonly saleRequests = new StateProperty<GetSalesRequestsResponse>();
    readonly saleRequestSessions = new StateProperty<SaleRequestSessionResponse>();
    readonly saleRequestPriceSimulation = new StateProperty<ChannelPriceSimulation[]>();
    readonly gatewayBenefits = new StateProperty<SaleRequestGatewayBenefits>();
    readonly paymentMethodsBenefitsContents = new StateProperty<SaleRequestBenefitsContentBadge[]>();

    // ticket PDF content images
    private readonly _ticketPdfContentImages = new BaseStateProp<SaleRequestTicketContentImage[]>();
    readonly setTicketPdfContentImages = this._ticketPdfContentImages.setValueFunction();
    readonly getTicketPdfContentImages$ = this._ticketPdfContentImages.getValueFunction();
    readonly setTicketPdfContentImagesLoading = this._ticketPdfContentImages.setInProgressFunction();
    readonly isTicketPdfContentImagesLoading$ = this._ticketPdfContentImages.getInProgressFunction();
    // ticket PDF content images saving
    private readonly _ticketPdfContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesSaving = this._ticketPdfContentImagesSaving.setInProgressFunction();
    readonly isTicketPdfContentImagesSaving$ = this._ticketPdfContentImagesSaving.getInProgressFunction();
    // ticket PDF content images removing
    private readonly _ticketPdfContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesRemoving = this._ticketPdfContentImagesRemoving.setInProgressFunction();
    readonly isTicketPdfContentImagesRemoving$ = this._ticketPdfContentImagesRemoving.getInProgressFunction();

    // ticket PDF content template
    private readonly _ticketPdfContentTemplate = new BaseStateProp<SaleRequestTicketContentTemplate>();
    readonly setTicketPdfContentTemplate = this._ticketPdfContentTemplate.setValueFunction();
    readonly getTicketPdfContentTemplate$ = this._ticketPdfContentTemplate.getValueFunction();
    readonly setTicketPdfContentTemplateLoading = this._ticketPdfContentTemplate.setInProgressFunction();
    readonly isTicketPdfContentTemplateLoading$ = this._ticketPdfContentTemplate.getInProgressFunction();

    // ticket Pdf content texts
    private readonly _ticketPdfContentTexts = new BaseStateProp<SaleRequestTicketContentText[]>();
    readonly setTicketPdfContentTexts = this._ticketPdfContentTexts.setValueFunction();
    readonly getTicketPdfContentTexts$ = this._ticketPdfContentTexts.getValueFunction();
    readonly setTicketPdfContentTextsLoading = this._ticketPdfContentTexts.setInProgressFunction();
    readonly isTicketPdfContentTextsLoading$ = this._ticketPdfContentTexts.getInProgressFunction();

    private _ticketPdfPreviewDownloading = new BaseStateProp<void>();
    readonly setTicketPdfPreviewDownloading = this._ticketPdfPreviewDownloading.setInProgressFunction();
    readonly isTicketPdfPreviewDownloading$ = this._ticketPdfPreviewDownloading.getInProgressFunction();

    // ticket PRINTER content images
    private readonly _ticketPrinterContentImages = new BaseStateProp<SaleRequestTicketContentImage[]>();
    readonly setTicketPrinterContentImages = this._ticketPrinterContentImages.setValueFunction();
    readonly getTicketPrinterContentImages$ = this._ticketPrinterContentImages.getValueFunction();
    readonly setTicketPrinterContentImagesLoading = this._ticketPrinterContentImages.setInProgressFunction();
    readonly isTicketPrinterContentImagesLoading$ = this._ticketPrinterContentImages.getInProgressFunction();
    // ticket PRINTER content images saving
    private readonly _ticketPrinterContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesSaving = this._ticketPrinterContentImagesSaving.setInProgressFunction();
    readonly isTicketPrinterContentImagesSaving$ = this._ticketPrinterContentImagesSaving.getInProgressFunction();
    // ticket PRINTER content images removing
    private readonly _ticketPrinterContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesRemoving = this._ticketPrinterContentImagesRemoving.setInProgressFunction();
    readonly isTicketPrinterContentImagesRemoving$ = this._ticketPrinterContentImagesRemoving.getInProgressFunction();

    // ticket PRINTER content template
    private readonly _ticketPrinterContentTemplate = new BaseStateProp<SaleRequestTicketContentTemplate>();
    readonly setTicketPrinterContentTemplate = this._ticketPrinterContentTemplate.setValueFunction();
    readonly getTicketPrinterContentTemplate$ = this._ticketPrinterContentTemplate.getValueFunction();
    readonly setTicketPrinterContentTemplateLoading = this._ticketPrinterContentTemplate.setInProgressFunction();
    readonly isTicketPrinterContentTemplateLoading$ = this._ticketPrinterContentTemplate.getInProgressFunction();

    // ticket Printer content texts
    private readonly _ticketPrinterContentTexts = new BaseStateProp<SaleRequestTicketContentText[]>();
    readonly setTicketPrinterContentTexts = this._ticketPrinterContentTexts.setValueFunction();
    readonly getTicketPrinterContentTexts$ = this._ticketPrinterContentTexts.getValueFunction();
    readonly setTicketPrinterContentTextsLoading = this._ticketPrinterContentTexts.setInProgressFunction();
    readonly isTicketPrinterContentTextsLoading$ = this._ticketPrinterContentTexts.getInProgressFunction();

    // saleRequest delivery conditions
    private _saleRequestDeliveryConditions = new BaseStateProp<SaleRequestDeliveryConditions>();
    readonly setSaleRequestDeliveryConditions = this._saleRequestDeliveryConditions.setValueFunction();
    readonly getSaleRequestDeliveryConditions$ = this._saleRequestDeliveryConditions.getValueFunction();
    readonly setSaleRequestDeliveryConditionsLoading = this._saleRequestDeliveryConditions.setInProgressFunction();
    readonly isSaleRequestDeliveryConditionsLoading$ = this._saleRequestDeliveryConditions.getInProgressFunction();
    readonly setSaleRequestDeliveryConditionsError = this._saleRequestDeliveryConditions.setErrorFunction();
    readonly getSaleRequestDeliveryConditionsError$ = this._saleRequestDeliveryConditions.getErrorFunction();

    // post saleRequest delivery conditions
    private _saleRequestDeliveryConditionsSaving = new BaseStateProp<void>();
    readonly setSaleRequestDeliveryConditionsSaving = this._saleRequestDeliveryConditionsSaving.setInProgressFunction();
    readonly isSaleRequestDeliveryConditionsSaving$ = this._saleRequestDeliveryConditionsSaving.getInProgressFunction();

    // saleRequest additional conditions
    private _saleRequestAdditionalConditions = new BaseStateProp<AdditionalCondition[]>();
    readonly setSaleRequestAdditionalConditions = this._saleRequestAdditionalConditions.setValueFunction();
    readonly getSaleRequestAdditionalConditions$ = this._saleRequestAdditionalConditions.getValueFunction();
    readonly setSaleRequestAdditionalConditionsLoading = this._saleRequestAdditionalConditions.setInProgressFunction();
    readonly isSaleRequestAdditionalConditionsLoading$ = this._saleRequestAdditionalConditions.getInProgressFunction();
    readonly setSaleRequestAdditionalConditionsError = this._saleRequestAdditionalConditions.setErrorFunction();
    readonly getSaleRequestAdditionalConditionsError$ = this._saleRequestAdditionalConditions.getErrorFunction();

    // post saleRequest additional conditions
    private _saleRequestAdditionalConditionsSaving = new BaseStateProp<void>();
    readonly setSaleRequestAdditionalConditionsSaving = this._saleRequestAdditionalConditionsSaving.setInProgressFunction();
    readonly isSaleRequestAdditionalConditionsSaving$ = this._saleRequestAdditionalConditionsSaving.getInProgressFunction();

    // additional conditions (channel-options)
    private readonly _additionalConditions = new BaseStateProp<AdditionalCondition[]>();
    readonly getAdditionalConditions$ = this._additionalConditions.getValueFunction();
    readonly setAdditionalConditions = this._additionalConditions.setValueFunction();
    readonly setAdditionalConditionsLoading = this._additionalConditions.setInProgressFunction();
    readonly isAdditionalConditionsLoading$ = this._additionalConditions.getInProgressFunction();
    // additional condition saving (channel-options)
    private readonly _additionalConditionSaving = new BaseStateProp<void>();
    readonly getAdditionalConditionError$ = this._additionalConditionSaving.getErrorFunction();
    readonly setAdditionalConditionError = this._additionalConditionSaving.setErrorFunction();
    readonly setAdditionalConditionSaving = this._additionalConditionSaving.setInProgressFunction();
    readonly isAdditionalConditionSaving$ = this._additionalConditionSaving.getInProgressFunction();

    // saleRequest email texts
    private _saleRequestEmailContentTexts = new BaseStateProp<SaleRequestEmailContentTexts[]>();
    readonly setSaleRequestEmailContentTexts = this._saleRequestEmailContentTexts.setValueFunction();
    readonly getSaleRequestEmailContentTexts$ = this._saleRequestEmailContentTexts.getValueFunction();
    readonly setSaleRequestEmailContentTextsLoading = this._saleRequestEmailContentTexts.setInProgressFunction();
    readonly isSaleRequestEmailContentTextsLoading$ = this._saleRequestEmailContentTexts.getInProgressFunction();

    // saleRequest email texts saving
    private _saleRequestEmailContentTextsSaving = new BaseStateProp<void>();
    readonly setSaleRequestEmailContentTextsSaving = this._saleRequestEmailContentTextsSaving.setInProgressFunction();
    readonly isSaleRequestEmailContentTextsSaving$ = this._saleRequestEmailContentTextsSaving.getInProgressFunction();

    // saleRequest channel additional banner texts
    private _saleRequestAdditionalBannerTexts = new BaseStateProp<SaleRequestAdditionalBannerTexts[]>();
    readonly setSaleRequestAdditionalBannerTexts = this._saleRequestAdditionalBannerTexts.setValueFunction();
    readonly getSaleRequestAdditionalBannerTexts$ = this._saleRequestAdditionalBannerTexts.getValueFunction();
    readonly setSaleRequestAdditionalBannerTextsLoading = this._saleRequestAdditionalBannerTexts.setInProgressFunction();
    readonly isSaleRequestAdditionalBannerTextsLoading$ = this._saleRequestAdditionalBannerTexts.getInProgressFunction();

    // saleRequest channel additional banner texts saving
    private _saleRequestAdditionalBannerTextsSaving = new BaseStateProp<void>();
    readonly setSaleRequestAdditionalBannerTextsSaving = this._saleRequestAdditionalBannerTextsSaving.setInProgressFunction();
    readonly isSaleRequestAdditionalBannerTextsSaving$ = this._saleRequestAdditionalBannerTextsSaving.getInProgressFunction();

    setFilterChannelList(filterChannelList: GetFilterResponse): void {
        this._filterChannelList.next(filterChannelList);
    }

    getFilterChannelList$(): Observable<GetFilterResponse> {
        return this._filterChannelList$;
    }

    setFilterChannelEntityList(filterChannelEntityList: GetFilterResponse): void {
        this._filterChannelEntityList.next(filterChannelEntityList);
    }

    getFilterChannelEntityList$(): Observable<GetFilterResponse> {
        return this._filterChannelEntityList$;
    }

    setFilterEventEntityList(filterEventEntityList: GetFilterResponse): void {
        this._filterEventEntityList.next(filterEventEntityList);
    }

    getFilterEventEntityList$(): Observable<GetFilterResponse> {
        return this._filterEventEntityList$;
    }
}

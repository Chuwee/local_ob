/* eslint-disable @typescript-eslint/member-ordering, @typescript-eslint/naming-convention*/
import { StateProperty } from '@OneboxTM/utils-state';
import {
    ChannelContent, ChannelHistoricalContent,
    ChannelPurchaseContentImage, ChannelPurchaseContentText,
    ChannelTicketContentImage, ChannelTicketContentText
} from '@admin-clients/cpanel/channels/communication/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdditionalCondition } from '../models/additional-condition.model';
import { ChannelAuthVendorsSso, ChannelAuthVendorsUserData } from '../models/auth-vendors-model';
import { ChannelCancellationServices } from '../models/cancellation-services.model';
import { ChannelBlacklistItem } from '../models/channel-blacklist-item.model';
import { ChannelBlacklistStatus } from '../models/channel-blacklist-status.model';
import { ChannelBlacklistType } from '../models/channel-blacklist-type.enum';
import { ChannelBookingSettings } from '../models/channel-booking-settings.model';
import { ChannelCommission } from '../models/channel-commission.model';
import { ChannelCrossSaleRestriction } from '../models/channel-cross-sale-restriction.model';
import { ChannelDeliverySettings } from '../models/channel-delivery-settings.model';
import { ChannelEvent } from '../models/channel-event.model';
import { ChannelExternalTool } from '../models/channel-external-tool.model';
import { ChannelForms } from '../models/channel-forms.model';
import { ChannelGatewayConfig } from '../models/channel-gateway-config.model';
import { ChannelGateway } from '../models/channel-gateway.model';
import { ChannelPurchaseConfig } from '../models/channel-purchase-config.model';
import { GetChannelSessionsResponse } from '../models/channel-session.model';
import { ChannelSharingSettings } from '../models/channel-sharing-settings.model';
import { ChannelSurchargeTaxes } from '../models/channel-surcharge-taxes.model';
import { ChannelSurcharge } from '../models/channel-surcharge.model';
import { TextContent } from '../models/channel-text-content';
import { EmailServerConf } from '../models/email-server-conf.model';
import { GetChannelBlacklistResponse } from '../models/get-channel-blacklist-response.model';
import { GetChannelEventsResponse } from '../models/get-channel-events-response.model';
import { GetChannelSuggestionsResponse } from '../models/get-channel-suggestions-response.model';
import { NotificationEmailTemplate } from '../models/notification-email-template.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelsExtendedState {

    // OPERATIVE
    // channel surcharges
    private readonly _channelSurcharges = new BaseStateProp<ChannelSurcharge[]>();
    readonly setChannelSurcharges = this._channelSurcharges.setValueFunction();
    readonly getChannelSurcharges$ = this._channelSurcharges.getValueFunction();
    readonly setChannelSurchargesLoading = this._channelSurcharges.setInProgressFunction();
    readonly isChannelSurchargesLoading$ = this._channelSurcharges.getInProgressFunction();
    readonly setChannelSurchargesError = this._channelSurcharges.setErrorFunction();
    readonly getChannelSurchargesError$ = this._channelSurcharges.getErrorFunction();
    // post channel surcharges
    private readonly _channelSurchargesSaving = new BaseStateProp<void>();
    readonly setChannelSurchargesSaving = this._channelSurchargesSaving.setInProgressFunction();
    readonly isChannelSurchargesSaving$ = this._channelSurchargesSaving.getInProgressFunction();

    // channel surcharge taxes
    readonly channelSurchargeTaxes = new StateProperty<ChannelSurchargeTaxes>();

    // channel commissions
    private readonly _channelCommissions = new BaseStateProp<ChannelCommission[]>();
    readonly setChannelCommissions = this._channelCommissions.setValueFunction();
    readonly getChannelCommissions$ = this._channelCommissions.getValueFunction();
    readonly setChannelCommissionsLoading = this._channelCommissions.setInProgressFunction();
    readonly isChannelCommissionsLoading$ = this._channelCommissions.getInProgressFunction();
    readonly setChannelCommissionsError = this._channelCommissions.setErrorFunction();
    readonly getChannelCommissionsError$ = this._channelCommissions.getErrorFunction();
    // post channel commissions
    private readonly _channelCommissionsSaving = new BaseStateProp<void>();
    readonly setChannelCommissionsSaving = this._channelCommissionsSaving.setInProgressFunction();
    readonly isChannelCommissionsSaving$ = this._channelCommissionsSaving.getInProgressFunction();

    // channel delivery methods
    private readonly _channelDeliveryMethods = new BaseStateProp<ChannelDeliverySettings>();
    readonly getChannelDeliveryMethods$ = this._channelDeliveryMethods.getValueFunction();
    readonly setChannelDeliveryMethods = this._channelDeliveryMethods.setValueFunction();
    readonly getChannelDeliveryMethodsError$ = this._channelDeliveryMethods.getErrorFunction();
    readonly setChannelDeliveryMethodsError = this._channelDeliveryMethods.setErrorFunction();
    readonly setChannelDeliveryMethodsLoading = this._channelDeliveryMethods.setInProgressFunction();
    readonly isChannelDeliveryMethodsLoading$ = this._channelDeliveryMethods.getInProgressFunction();
    // post channel delivery methods
    private readonly _channelDeliveryMethodSaving = new BaseStateProp<void>();
    readonly setChannelDeliveryMethodsSaving = this._channelDeliveryMethodSaving.setInProgressFunction();
    readonly isChannelDeliveryMethodsSaving$ = this._channelDeliveryMethodSaving.getInProgressFunction();

    //buyer data protection data forms (channel-options)
    private readonly _buyerDataProtectionDataForms = new BaseStateProp<ChannelForms>();
    readonly getBuyerDataProtectionDataForms$ = this._buyerDataProtectionDataForms.getValueFunction();
    readonly setBuyerDataProtectionDataForms = this._buyerDataProtectionDataForms.setValueFunction();
    readonly getBuyerDataProtectionDataFormsError$ = this._buyerDataProtectionDataForms.getErrorFunction();
    readonly setBuyerDataProtectionDataFormsError = this._buyerDataProtectionDataForms.setErrorFunction();
    readonly setBuyerDataProtectionDataFormsLoading = this._buyerDataProtectionDataForms.setInProgressFunction();
    readonly isBuyerDataProtectionDataFormsLoading$ = this._buyerDataProtectionDataForms.getInProgressFunction();

    // buyer data forms (channel-options)
    private readonly _channelForms = new BaseStateProp<ChannelForms>();
    readonly getChannelForms$ = this._channelForms.getValueFunction();
    readonly setChannelForms = this._channelForms.setValueFunction();
    readonly getChannelFormsError$ = this._channelForms.getErrorFunction();
    readonly setChannelFormsError = this._channelForms.setErrorFunction();
    readonly setChannelFormsLoading = this._channelForms.setInProgressFunction();
    readonly isChannelFormsLoading$ = this._channelForms.getInProgressFunction();
    // buyer data forms saving (channel-options)
    private readonly _channelFormsSaving = new BaseStateProp<void>();
    readonly setChannelFormsSaving = this._channelFormsSaving.setInProgressFunction();
    readonly isChannelFormsSaving$ = this._channelFormsSaving.getInProgressFunction();

    // buyer data protection data forms saving (channel-options)
    private readonly _buyerDataProtectionDataFormsSaving = new BaseStateProp<void>();
    readonly setBuyerDataProtectionDataFormsSaving = this._buyerDataProtectionDataFormsSaving.setInProgressFunction();
    readonly isBuyerDataProtectionDataFormsSaving$ = this._buyerDataProtectionDataFormsSaving.getInProgressFunction();

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

    // channel blacklist
    private readonly _channelBlacklist = {
        [ChannelBlacklistType.email]: new BaseStateProp<GetChannelBlacklistResponse>(),
        [ChannelBlacklistType.nif]: new BaseStateProp<GetChannelBlacklistResponse>()
    };

    readonly getChannelBlacklist$:
        (type: ChannelBlacklistType) => () => Observable<GetChannelBlacklistResponse> =
        type => this._channelBlacklist[type].getValueFunction();

    readonly setChannelBlacklist:
        (type: ChannelBlacklistType) => (value: GetChannelBlacklistResponse) => void =
        type => this._channelBlacklist[type].setValueFunction();

    readonly setChannelBlacklistLoading:
        (type: ChannelBlacklistType) => (isLoading: boolean) => void =
        type => this._channelBlacklist[type].setInProgressFunction();

    readonly isChannelBlacklistLoading$:
        (type: ChannelBlacklistType) => () => Observable<boolean> =
        type => this._channelBlacklist[type].getInProgressFunction();

    // post/delete channel blacklist
    private readonly _channelBlacklistSaving = new BaseStateProp<void>();
    readonly setChannelBlacklistSaving = this._channelBlacklistSaving.setInProgressFunction();
    readonly isChannelBlacklistSaving$ = this._channelBlacklistSaving.getInProgressFunction();

    // channel blacklist status
    private readonly _channelBlacklistStatus = {
        [ChannelBlacklistType.email]: new BaseStateProp<ChannelBlacklistStatus>(),
        [ChannelBlacklistType.nif]: new BaseStateProp<ChannelBlacklistStatus>()
    };

    readonly getChannelBlacklistStatus$:
        (type: ChannelBlacklistType) => () => Observable<ChannelBlacklistStatus> =
        type => this._channelBlacklistStatus[type].getValueFunction();

    readonly setChannelBlacklistStatus:
        (type: ChannelBlacklistType) => (blacklist: ChannelBlacklistStatus) => void =
        type => this._channelBlacklistStatus[type].setValueFunction();

    readonly setChannelBlacklistStatusLoading:
        (type: ChannelBlacklistType) => (isLoading: boolean) => void =
        type => this._channelBlacklistStatus[type].setInProgressFunction();

    readonly isChannelBlacklistStatusLoading$:
        (type: ChannelBlacklistType) => () => Observable<boolean> =
        type => this._channelBlacklistStatus[type].getInProgressFunction();

    // put channel blacklist status
    private readonly _channelBlacklistStatusSaving = {
        [ChannelBlacklistType.email]: new BaseStateProp<void>(),
        [ChannelBlacklistType.nif]: new BaseStateProp<void>()
    };

    readonly setChannelBlacklistStatusSaving:
        (type: ChannelBlacklistType) => (isSaving: boolean) => void =
        type => this._channelBlacklistStatusSaving[type].setInProgressFunction();

    readonly isChannelBlacklistStatusSaving$:
        (type: ChannelBlacklistType) => () => Observable<boolean> =
        type => this._channelBlacklistStatusSaving[type].getInProgressFunction();

    // channel blacklist item
    private readonly _channelBlacklistItem = new BaseStateProp<ChannelBlacklistItem>();
    readonly getChannelBlacklistItem$ = this._channelBlacklistItem.getValueFunction();
    readonly setChannelBlacklistItem = this._channelBlacklistItem.setValueFunction();
    readonly getChannelBlacklistItemError$ = this._channelBlacklistItem.getErrorFunction();
    readonly setChannelBlacklistItemError = this._channelBlacklistItem.setErrorFunction();
    readonly setChannelBlacklistItemLoading = this._channelBlacklistItem.setInProgressFunction();
    readonly isChannelBlacklistItemLoading$ = this._channelBlacklistItem.getInProgressFunction();
    // delete channel blacklist item
    private readonly _channelBlacklistItemSaving = new BaseStateProp<void>();
    readonly setChannelBlacklistItemSaving = this._channelBlacklistItemSaving.setInProgressFunction();
    readonly isChannelBlacklistItemSaving$ = this._channelBlacklistItemSaving.getInProgressFunction();

    // channel events (cross-selling && catalog-order)
    private readonly _channelEvents = new BaseStateProp<GetChannelEventsResponse>();
    readonly getChannelEvents$ = this._channelEvents.getValueFunction();
    readonly setChannelEvents = this._channelEvents.setValueFunction();
    readonly getChannelEventsError$ = this._channelEvents.getErrorFunction();
    readonly setChannelEventsError = this._channelEvents.setErrorFunction();
    readonly setChannelEventsLoading = this._channelEvents.setInProgressFunction();
    readonly isChannelEventsLoading$ = this._channelEvents.getInProgressFunction();
    // put channel catalog (cross-selling && catalog-order)
    private readonly _channelCatalogSaving = new BaseStateProp<void>();
    readonly setChannelCatalogSaving = this._channelCatalogSaving.setInProgressFunction();
    readonly isChannelCatalogSaving$ = this._channelCatalogSaving.getInProgressFunction();
    // channel event (cross-selling && catalog-order)
    private readonly _channelEvent = new BaseStateProp<ChannelEvent>();
    readonly getChannelEvent$ = this._channelEvent.getValueFunction();
    readonly setChannelEvent = this._channelEvent.setValueFunction();
    readonly getChannelEventError$ = this._channelEvent.getErrorFunction();
    readonly setChannelEventError = this._channelEvent.setErrorFunction();
    readonly setChannelEventLoading = this._channelEvent.setInProgressFunction();
    readonly isChannelEventLoading$ = this._channelEvent.getInProgressFunction();
    // put channel event (cross-selling && catalog-order)
    private readonly _channelEventSaving = new BaseStateProp<void>();
    readonly setChannelEventSaving = this._channelEventSaving.setInProgressFunction();
    readonly isChannelEventSaving$ = this._channelEventSaving.getInProgressFunction();

    // channel booking
    private readonly _channelBookingSettings = new BaseStateProp<ChannelBookingSettings>();
    readonly getChannelBookingSettings$ = this._channelBookingSettings.getValueFunction();
    readonly setChannelBookingSettings = this._channelBookingSettings.setValueFunction();
    readonly getChannelBookingSettingsError$ = this._channelBookingSettings.getErrorFunction();
    readonly setChannelBookingSettingsError = this._channelBookingSettings.setErrorFunction();
    readonly setChannelBookingSettingsInProgress = this._channelBookingSettings.setInProgressFunction();
    readonly isChannelBookingSettingsInProgress$ = this._channelBookingSettings.getInProgressFunction();

    // CONFIGURATION

    // Payment Methods / Gateways
    private readonly _channelPaymentMethods = new BaseStateProp<ChannelGateway[]>();
    readonly setChannelPaymentMethods = this._channelPaymentMethods.setValueFunction();
    readonly getChannelPaymentMethods$ = this._channelPaymentMethods.getValueFunction();
    readonly setChannelPaymentMethodsLoading = this._channelPaymentMethods.setInProgressFunction();
    readonly isChannelPaymentMethodsLoading$ = this._channelPaymentMethods.getInProgressFunction();
    readonly setChannelPaymentMethodsError = this._channelPaymentMethods.setErrorFunction();
    readonly getChannelPaymentMethodsError$ = this._channelPaymentMethods.getErrorFunction();
    // post channel Payment Methods / Gateways
    private readonly _channePaymentMethodsSaving = new BaseStateProp<void>();
    readonly setChannelPaymentMethodsSaving = this._channePaymentMethodsSaving.setInProgressFunction();
    readonly isChannelPaymentMethodsSaving$ = this._channePaymentMethodsSaving.getInProgressFunction();

    // Gateway configuration
    readonly gatewayConfiguration = new StateProperty<ChannelGatewayConfig>();

    // COMMUNICATION

    // clone contents saving
    private readonly _cloneContentsSaving = new BaseStateProp<void>();
    readonly setCloneContentsSaving = this._cloneContentsSaving.setInProgressFunction();
    readonly isCloneContentsSaving$ = this._cloneContentsSaving.getInProgressFunction();

    // text contents
    private readonly _textContents = new BaseStateProp<TextContent[]>();
    readonly getTextContents$ = this._textContents.getValueFunction();
    readonly setTextContents = this._textContents.setValueFunction();
    readonly getTextContentsError$ = this._textContents.getErrorFunction();
    readonly setTextContentsError = this._textContents.setErrorFunction();
    readonly setTextContentsLoading = this._textContents.setInProgressFunction();
    readonly isTextContentsLoading$ = this._textContents.getInProgressFunction();
    // text contents saving
    private readonly _textContentsSaving = new BaseStateProp<void>();
    readonly setTextContentsSaving = this._textContentsSaving.setInProgressFunction();
    readonly isTextContentsSaving$ = this._textContentsSaving.getInProgressFunction();

    // contents
    private readonly _contents = new BaseStateProp<ChannelContent[]>();
    readonly getContents$ = this._contents.getValueFunction();
    readonly setContents = this._contents.setValueFunction();
    readonly getContentsError$ = this._contents.getErrorFunction();
    readonly setContentsError = this._contents.setErrorFunction();
    readonly setContentsLoading = this._contents.setInProgressFunction();
    readonly isContentsLoading$ = this._contents.getInProgressFunction();
    // contents saving
    private readonly _contentsSaving = new BaseStateProp<void>();
    readonly setContentsSaving = this._contentsSaving.setInProgressFunction();
    readonly isContentsSaving$ = this._contentsSaving.getInProgressFunction();
    // profiled contents saving
    private readonly _profiledContentsSaving = new BaseStateProp<void>();
    readonly setProfiledContentsSaving = this._profiledContentsSaving.setInProgressFunction();
    readonly isProfiledContentsSaving$ = this._profiledContentsSaving.getInProgressFunction();
    // historical content
    private readonly _historicalCcontent = new BaseStateProp<ChannelHistoricalContent[]>();
    readonly getHistoricalContent$ = this._historicalCcontent.getValueFunction();
    readonly setHistoricalContent = this._historicalCcontent.setValueFunction();
    readonly getHistoricalContentError$ = this._historicalCcontent.getErrorFunction();
    readonly setHistoricalContentError = this._historicalCcontent.setErrorFunction();
    readonly setHistoricalContentLoading = this._historicalCcontent.setInProgressFunction();
    readonly isHistoricalContentLoading$ = this._historicalCcontent.getInProgressFunction();

    // ticket PDF content images
    private readonly _ticketPdfContentImages = new BaseStateProp<ChannelTicketContentImage[]>();
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

    // ticket PRINTER content images
    private readonly _ticketPrinterContentImages = new BaseStateProp<ChannelTicketContentImage[]>();
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

    // ticket Passbook content texts
    private readonly _ticketPassbookContentTexts = new BaseStateProp<ChannelTicketContentText[]>();
    readonly setTicketPassbookContentTexts = this._ticketPassbookContentTexts.setValueFunction();
    readonly getTicketPassbookContentTexts$ = this._ticketPassbookContentTexts.getValueFunction();
    readonly setTicketPassbookContentTextsLoading = this._ticketPassbookContentTexts.setInProgressFunction();
    readonly isTicketPassbookContentTextsLoading$ = this._ticketPassbookContentTexts.getInProgressFunction();
    // ticket Passbook content texts saving
    private readonly _ticketPassbookContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentTextsSaving = this._ticketPassbookContentTextsSaving.setInProgressFunction();
    readonly isTicketPassbookContentTextsSaving$ = this._ticketPassbookContentTextsSaving.getInProgressFunction();

    // ticket Passbook content images
    private readonly _ticketPassbookContentImages = new BaseStateProp<ChannelTicketContentImage[]>();
    readonly setTicketPassbookContentImages = this._ticketPassbookContentImages.setValueFunction();
    readonly getTicketPassbookContentImages$ = this._ticketPassbookContentImages.getValueFunction();
    readonly setTicketPassbookContentImagesLoading = this._ticketPassbookContentImages.setInProgressFunction();
    readonly isTicketPassbookContentImagesLoading$ = this._ticketPassbookContentImages.getInProgressFunction();
    // ticket Passbook content images saving
    private readonly _ticketPassbookContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesSaving = this._ticketPassbookContentImagesSaving.setInProgressFunction();
    readonly isTicketPassbookContentImagesSaving$ = this._ticketPassbookContentImagesSaving.getInProgressFunction();
    // ticket Passbook content images removing
    private readonly _ticketPassbookContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesRemoving = this._ticketPassbookContentImagesRemoving.setInProgressFunction();
    readonly isTicketPassbookContentImagesRemoving$ = this._ticketPassbookContentImagesRemoving.getInProgressFunction();

    // purchase content images
    private readonly _purchaseContentImages = new BaseStateProp<ChannelPurchaseContentImage[]>();
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
    private readonly _purchaseContentTexts = new BaseStateProp<ChannelPurchaseContentText[]>();
    readonly setPurchaseContentTexts = this._purchaseContentTexts.setValueFunction();
    readonly getPurchaseContentTexts$ = this._purchaseContentTexts.getValueFunction();
    readonly setPurchaseContentTextsLoading = this._purchaseContentTexts.setInProgressFunction();
    readonly isPurchaseContentTextsLoading$ = this._purchaseContentTexts.getInProgressFunction();
    // purchase content texts saving
    private readonly _purchaseContentTextsSaving = new BaseStateProp<void>();
    readonly setPurchaseContentTextsSaving = this._purchaseContentTextsSaving.setInProgressFunction();
    readonly isPurchaseContentTextsSaving$ = this._purchaseContentTextsSaving.getInProgressFunction();

    // external tools
    readonly externalTools = new StateProperty<ChannelExternalTool[]>();

    // purchase config
    private readonly _purchaseConfig = new BaseStateProp<ChannelPurchaseConfig>();
    readonly getPurchaseConfig$ = this._purchaseConfig.getValueFunction();
    readonly setPurchaseConfig = this._purchaseConfig.setValueFunction();
    readonly getPurchaseConfigError$ = this._purchaseConfig.getErrorFunction();
    readonly setPurchaseConfigError = this._purchaseConfig.setErrorFunction();
    readonly setPurchaseConfigLoading = this._purchaseConfig.setInProgressFunction();
    readonly isPurchaseConfigLoading$ = this._purchaseConfig.getInProgressFunction();
    // puchase config saving
    private readonly _purchaseConfigSaving = new BaseStateProp<void>();
    readonly setPurchaseConfigSaving = this._purchaseConfigSaving.setInProgressFunction();
    readonly isPurchaseConfigSaving$ = this._purchaseConfigSaving.getInProgressFunction();
    readonly getPurchaseConfigSavingError$ = this._purchaseConfigSaving.getErrorFunction();
    readonly setPurchaseConfigSavingError = this._purchaseConfigSaving.setErrorFunction();

    // notification email server config
    private readonly _emailServerConfig = new BaseStateProp<EmailServerConf>();
    readonly setEmailServerConfig = this._emailServerConfig.setValueFunction();
    readonly getEmailServerConfig = this._emailServerConfig.getValueFunction();
    readonly setEmailServerConfigLoading = this._emailServerConfig.setInProgressFunction();
    readonly isEmailServerConfigLoading = this._emailServerConfig.getInProgressFunction();
    readonly setEmailServerConfigError = this._emailServerConfig.setErrorFunction();
    readonly getEmailServerConfigError = this._emailServerConfig.getErrorFunction();
    // notification email server test
    private readonly _emailServerTest = new BaseStateProp<void>();
    readonly setEmailServerConfigTesting = this._emailServerTest.setInProgressFunction();
    readonly isEmailServerConfigTesting = this._emailServerTest.getInProgressFunction();
    readonly setEmailServerTestError = this._emailServerTest.setErrorFunction();
    readonly getEmailServerTestError = this._emailServerTest.getErrorFunction();
    // notification email server update
    private readonly _emailServerConfigUpdate = new BaseStateProp<void>();
    readonly setEmailServerConfigUpdating = this._emailServerConfigUpdate.setInProgressFunction();
    readonly isEmailServerConfigUpdating = this._emailServerConfigUpdate.getInProgressFunction();
    // notification email server templates
    private readonly _notificationEmailTemplates = new BaseStateProp<NotificationEmailTemplate[]>();
    readonly setNotificationEmailTemplates = this._notificationEmailTemplates.setValueFunction();
    readonly getNotificationEmailTemplates = this._notificationEmailTemplates.getValueFunction();
    readonly setNotificationEmailTemplatesLoading = this._notificationEmailTemplates.setInProgressFunction();
    readonly isNotificationEmailTemplatesLoading = this._notificationEmailTemplates.getInProgressFunction();
    readonly setNotificationEmailTemplatesError = this._notificationEmailTemplates.setErrorFunction();
    readonly getNotificationEmailTemplatesError = this._notificationEmailTemplates.getErrorFunction();
    // notification email server update
    private readonly _emailNotificationTemplatesUpdate = new BaseStateProp<void>();
    readonly setNotificationEmailTemplatesUpdating = this._emailNotificationTemplatesUpdate.setInProgressFunction();
    readonly isNotificationEmailTemplatesUpdating = this._emailNotificationTemplatesUpdate.getInProgressFunction();

    // cancellation services
    private readonly _cancellationServices = new BaseStateProp<ChannelCancellationServices>();
    readonly getCancellationServices$ = this._cancellationServices.getValueFunction();
    readonly setCancellationServices = this._cancellationServices.setValueFunction();
    readonly getCancellationServicesError$ = this._cancellationServices.getErrorFunction();
    readonly setCancellationServicesError = this._cancellationServices.setErrorFunction();
    readonly setCancellationServicesLoading = this._cancellationServices.setInProgressFunction();
    readonly isCancellationServicesLoading$ = this._cancellationServices.getInProgressFunction();

    // channel auth vendors
    private readonly _authVendorsUserData = new BaseStateProp<ChannelAuthVendorsUserData>();
    readonly getAuthVendorUserData$ = this._authVendorsUserData.getValueFunction();
    readonly setAuthVendorUserData = this._authVendorsUserData.setValueFunction();
    readonly getAuthVendorUserDataError$ = this._authVendorsUserData.getErrorFunction();
    readonly setAuthVendorUserDataError = this._authVendorsUserData.setErrorFunction();
    readonly setAuthVendorUserDataLoading = this._authVendorsUserData.setInProgressFunction();
    readonly isAuthVendorUserDataLoading$ = this._authVendorsUserData.getInProgressFunction();

    private readonly _authVendorsSso = new BaseStateProp<ChannelAuthVendorsSso>();
    readonly getAuthVendorSso$ = this._authVendorsSso.getValueFunction();
    readonly setAuthVendorSso = this._authVendorsSso.setValueFunction();
    readonly getAuthVendorSsoError$ = this._authVendorsSso.getErrorFunction();
    readonly setAuthVendorSsoError = this._authVendorsSso.setErrorFunction();
    readonly setAuthVendorSsoLoading = this._authVendorsSso.setInProgressFunction();
    readonly isAuthVendorSsoLoading$ = this._authVendorsSso.getInProgressFunction();

    readonly crossSaleRestrictions = new StateProperty<ChannelCrossSaleRestriction[]>();
    readonly sharingSettings = new StateProperty<ChannelSharingSettings>();
    readonly channelSuggestions = new StateProperty<GetChannelSuggestionsResponse>();
    readonly channelSessions = new StateProperty<GetChannelSessionsResponse>();
}
/* eslint-enable @typescript-eslint/member-ordering, @typescript-eslint/naming-convention*/

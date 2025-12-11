package es.onebox.mgmt.channels.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.CancellationServiceDTO;
import es.onebox.mgmt.channels.dto.ChannelCancellationServicesDTO;
import es.onebox.mgmt.channels.dto.ChannelContactDTO;
import es.onebox.mgmt.channels.dto.ChannelContactEntityDTO;
import es.onebox.mgmt.channels.dto.ChannelCurrencyVoucherGiftCardDTO;
import es.onebox.mgmt.channels.dto.ChannelDTO;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.dto.ChannelEventSaleRestrictionsDTO;
import es.onebox.mgmt.channels.dto.ChannelEventsSaleRestrictionsDTO;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsTicketsDTO;
import es.onebox.mgmt.channels.dto.ChannelSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelSettingsUpdateDTO;
import es.onebox.mgmt.channels.dto.ChannelVoucherGiftCardDTO;
import es.onebox.mgmt.channels.dto.ChannelVouchersDTO;
import es.onebox.mgmt.channels.dto.ChannelsResponseDTO;
import es.onebox.mgmt.channels.dto.CustomerAssignationDTO;
import es.onebox.mgmt.channels.dto.DonationProviderDTO;
import es.onebox.mgmt.channels.dto.DonationSettingsDTO;
import es.onebox.mgmt.channels.dto.DonationsConfigDTO;
import es.onebox.mgmt.channels.dto.InvitationsSettingsDTO;
import es.onebox.mgmt.channels.dto.SupportEmailDTO;
import es.onebox.mgmt.channels.dto.SurchargesSettingsDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelVouchersRequestDTO;
import es.onebox.mgmt.channels.dto.VirtualQueueConfigDTO;
import es.onebox.mgmt.channels.dto.WhatsappConfigDTO;
import es.onebox.mgmt.channels.dto.BookingSettingsDTO;
import es.onebox.mgmt.channels.dto.CampaignDTO;
import es.onebox.mgmt.channels.dto.DestinationChannelDTO;
import es.onebox.mgmt.channels.enums.ChannelPortalBuild;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.Channel;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventSaleRestrictionResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventSaleRestrictions;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventsSaleRestrictions;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelVouchers;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.CreateChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.CurrencyGiftCard;
import es.onebox.mgmt.datasources.ms.channel.dto.CurrencyGiftCardDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.InvitationsSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.Language;
import es.onebox.mgmt.datasources.ms.channel.dto.SupportEmail;
import es.onebox.mgmt.datasources.ms.channel.dto.VirtualQueueConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.WhatsappConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.Campaign;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.DonationProvider;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.DonationSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.DonationsConfig;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBuild;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSurchargesCalculationDTO;
import es.onebox.mgmt.datasources.ms.channel.enums.DonationType;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.insurance.dto.CancellationService;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServices;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServicesUpdate;
import es.onebox.mgmt.events.dto.CreateChannelDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.channels.ChannelEntityDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.updateField;

public class ChannelConverter {

    public static final String PREVIEWTOKEN_PARAM_KEY = "previewtoken";
    private static final Integer DEFAULT_V2_RECEIPT_TEMPLATE = 3;

    private ChannelConverter() {
    }

    public static ChannelsResponseDTO fromMsChannelsResponse(ChannelsResponse msResponse, ChannelsHelper channelsHelper) {
        ChannelsResponseDTO dto = new ChannelsResponseDTO();
        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData()
                .stream()
                .map(channel -> fromMsChannel(channel, channelsHelper))
                .collect(Collectors.toList()));
        return dto;
    }

    public static ChannelDetailDTO fromMsChannelsResponse(ChannelResponse source, Map<Long, String> languagesByIds, String urlChannel, List<CodeNameDTO> currencies, Boolean hasActivePromotion) {
        ChannelDetailDTO target = new ChannelDetailDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setChannelPublic(source.getChannelPublic());
        if (source.getBuild() != null) {
            target.setBuild(ChannelPortalBuild.getByBuild(source.getBuild().getBuild()));
        }
        target.setStatus(ChannelStatus.getById(source.getStatus().getId()));
        target.setEntity(new ChannelEntityDTO());
        target.getEntity().setName(source.getEntityName());
        target.getEntity().setId(source.getEntityId());
        target.getEntity().setLogo(source.getEntityLogo());
        target.setDomain(source.getDomain());
        target.setType(ChannelSubtype.getById(source.getSubtype().getIdSubtipo()));
        target.setLanguages(convertToLanguageDTO(source.getLanguages(), languagesByIds));
        target.setContact(fromMsChannelsContactResponse(source));
        target.setSettings(fillSettings(source, hasActivePromotion));
        target.getSettings().setRobotsNoFollow(source.getRobotsNoFollow());
        target.setLimits(fillLimits(source));
        target.setVirtualQueueConfig(convertToVirtualQueueConfig(source));

        if (source.getWhitelabelType() != null) {
            target.setWhitelabelType(WhitelabelType.valueOf(source.getWhitelabelType().name()));
            if (WhitelabelType.EXTERNAL.equals(target.getWhitelabelType())) {
                target.setWhitelabelPath(source.getWhitelabelPath());
            }
            target.setForceSquarePictures(source.getForceSquarePictures());
        }
        if (BooleanUtils.isTrue(source.getV4Enabled())) {
            target.setUrl(ChannelsUrlUtils.buildUrlByChannels(urlChannel, source.getUrl()));
        } else if (BooleanUtils.isTrue(source.getV4ConfigEnabled()) && source.getV4PreviewToken() != null) {
            target.setUrl(ChannelsUrlUtils.buildUrlByChannelsPreview(urlChannel, source.getUrl(), PREVIEWTOKEN_PARAM_KEY, source.getV4PreviewToken()));
        } else {
            target.setUrl(source.getUrl());
        }

        if (CollectionUtils.isNotEmpty(currencies)) {
            target.setCurrencies(currencies);
        }
        return target;
    }

    private static VirtualQueueConfigDTO convertToVirtualQueueConfig(ChannelResponse source) {
        if (source.getVirtualQueueConfig() != null) {
            VirtualQueueConfigDTO virtualQueueConfigDTO = new VirtualQueueConfigDTO();
            virtualQueueConfigDTO.setAlias(source.getVirtualQueueConfig().getAlias());
            virtualQueueConfigDTO.setActive(source.getVirtualQueueConfig().isActive());
            return virtualQueueConfigDTO;
        }
        return null;
    }

    private static ChannelContactDTO fromMsChannelsContactResponse(ChannelResponse source) {
        if (source.getContactName() == null && source.getContactSurname() == null && source.getContactEmail() == null
                && source.getContactPhone() == null && source.getContactWeb() == null && source.getContactJobPosition() == null
                && fromMsChannelsContactEntityResponse(source) == null) {
            return null;
        }
        ChannelContactDTO target = new ChannelContactDTO();
        target.setContactName(source.getContactName());
        target.setContactSurname(source.getContactSurname());
        target.setContactEmail(source.getContactEmail());
        target.setContactPhone(source.getContactPhone());
        target.setContactWeb(source.getContactWeb());
        target.setEntity(fromMsChannelsContactEntityResponse(source));
        target.setContactJobPosition(source.getContactJobPosition());
        return target;
    }

    private static ChannelContactEntityDTO fromMsChannelsContactEntityResponse(ChannelResponse source) {
        if (source.getContactEntityOwner() == null && source.getContactEntityManager() == null) {
            return null;
        }
        ChannelContactEntityDTO target = new ChannelContactEntityDTO();
        target.setContactEntityOwner(source.getContactEntityOwner());
        target.setContactEntityManager(source.getContactEntityManager());
        return target;
    }

    private static ChannelDTO fromMsChannel(Channel channel, ChannelsHelper channelsHelper) {
        ChannelDTO dto = new ChannelDTO();

        ChannelEntityDTO entity = new ChannelEntityDTO();
        entity.setId(channel.getEntityId());
        entity.setName(channel.getEntityName());
        entity.setLogo(channel.getEntityLogo());
        dto.setEntity(entity);

        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setStatus(ChannelStatus.getById(channel.getStatus().getId()));

        if (channel.getSubtype() != null) {
            dto.setType(ChannelSubtype.getById(channel.getSubtype().getIdSubtipo()));
        }

        dto.setOperator(new IdNameDTO(channel.getOperatorId(), channel.getOperatorName()));
        dto.setUrl(channelsHelper.buildChannelUrl(channel.getUrl()));

        return dto;
    }

    public static CreateChannel fromCreateChannelDTO(CreateChannelDTO dto) {
        CreateChannel createChannel = new CreateChannel();
        createChannel.setEntityId(dto.getEntityId());
        createChannel.setUrl(dto.getUrl());
        createChannel.setType(es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype.getById(dto.getType().getId()));
        createChannel.setName(dto.getName());
        createChannel.setCollectiveId(dto.getCollectiveId());
        return createChannel;
    }

    public static ChannelUpdateRequest fromUpdateChannelRequestDTO(UpdateChannelRequestDTO source,
                                                                   Map<String, Long> masterLanguages, List<Long> currencies) {
        ChannelUpdateRequest target = new ChannelUpdateRequest();

        updateField(target::setName, source.getName());
        updateField(target::setChannelPublic, source.getChannelPublic());
        updateField(target::setDomain, source.getDomain());
        updateField(target::setWhitelabelPath, source.getWhitelabelPath());
        updateField(target::setForceSquarePictures, source.getForceSquarePictures());
        fromUpdateChannelContactRequestDTO(target, source.getContact());

        if (source.getBuild() != null) {
            target.setBuild(ChannelBuild.getByBuild(source.getBuild().getBuild()));
        }

        if (source.getStatus() != null) {
            target.setStatus(es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus.getById(source.getStatus().getId()));
        }

        if (source.getLanguages() != null) {
            target.setLanguages(convertToMsChannelDTO(source.getLanguages(), masterLanguages));
        }
        if (CollectionUtils.isNotEmpty(source.getCurrencies())) {
            target.setCurrencies(currencies);
        }

        if (source.getSettings() != null) {
            fillSettings(source, target);
        }

        if (source.getLimits() != null) {
            fillLimits(source.getLimits(), target);
        }

        if (source.getVirtualQueueConfig() != null) {
            target.setVirtualQueueConfig(convertToVirtualQueueConfigUpdate(source.getVirtualQueueConfig()));
        }

        return target;
    }

    private static VirtualQueueConfig convertToVirtualQueueConfigUpdate(VirtualQueueConfigDTO virtualQueueConfigSource) {
        if (virtualQueueConfigSource != null) {
            VirtualQueueConfig virtualQueueConfig = new VirtualQueueConfig();
            virtualQueueConfig.setActive(virtualQueueConfigSource.isActive());
            virtualQueueConfig.setAlias(virtualQueueConfigSource.getAlias());
            return virtualQueueConfig;
        }
        return null;
    }

    private static void fromUpdateChannelContactRequestDTO(ChannelUpdateRequest target, ChannelContactDTO source) {
        if (source == null) {
            return;
        }
        updateField(target::setContactEmail, source.getContactEmail());
        updateField(target::setContactJobPosition, source.getContactJobPosition());
        updateField(target::setContactName, source.getContactName());
        updateField(target::setContactPhone, source.getContactPhone());
        updateField(target::setContactSurname, source.getContactSurname());
        updateField(target::setContactWeb, source.getContactWeb());
        fromUpdateChannelContactEntityRequestDTO(target, source.getEntity());
    }

    private static void fromUpdateChannelContactEntityRequestDTO(ChannelUpdateRequest target, ChannelContactEntityDTO source) {
        if (source == null) {
            return;
        }
        updateField(target::setContactEntityManager, source.getContactEntityManager());
        updateField(target::setContactEntityOwner, source.getContactEntityOwner());
    }

    public static BookingSettings fromDTO(BookingSettingsDTO source) {
        if (source == null) {
            return null;
        }
        BookingSettings target = new BookingSettings();
        target.setAllowBooking(source.getAllowBooking());
        target.setAllowBookingCheckout(source.getAllowBookingCheckout());
        target.setBookingCheckoutPaymentMethods(source.getBookingCheckoutPaymentMethods());
        target.setBookingCheckoutDomain(source.getBookingCheckoutDomain());
        target.setAllowCustomerAssignation(source.getAllowCustomerAssignation());
        target.setAllowPresaleRestrictions(source.getAllowPresaleRestrictions());
        return target;
    }

    public static BookingSettingsDTO toDTO(BookingSettings source) {
        if (source == null) {
            return null;
        }
        BookingSettingsDTO target = new BookingSettingsDTO();
        target.setAllowBooking(source.getAllowBooking());
        target.setAllowBookingCheckout(source.getAllowBookingCheckout());
        target.setBookingCheckoutPaymentMethods(source.getBookingCheckoutPaymentMethods());
        target.setBookingCheckoutDomain(source.getBookingCheckoutDomain());
        target.setAllowCustomerAssignation(source.getAllowCustomerAssignation());
        target.setAllowPresaleRestrictions(source.getAllowPresaleRestrictions());
        return target;
    }

    public static ChannelCancellationServicesUpdate fromDTO(ChannelCancellationServicesDTO source, Long operatorId) {
        if (source == null) {
            return null;
        }
        ChannelCancellationServicesUpdate target = new ChannelCancellationServicesUpdate();
        target.setCancellationServices(source.getCancellationServices().stream().map(
                ChannelConverter::updateToMs).toList());
        target.setOperatorId(operatorId);
        return target;
    }

    public static CancellationService updateToMs(CancellationServiceDTO source) {
        if (source == null) {
            return null;
        }
        CancellationService target = new CancellationService();
        target.setId(source.getId());
        target.setEnabled(source.getEnabled());
        target.setDefaultSelected(source.getDefaultSelected());

        return target;
    }

    public static ChannelCancellationServicesDTO toDTO(ChannelCancellationServices source) {
        if (source == null) {
            return null;
        }
        ChannelCancellationServicesDTO target = new ChannelCancellationServicesDTO();
        target.setCancellationServices(source.getCancellationServices().stream().map(
                ChannelConverter::toDTO).toList());
        return target;
    }

    public static CancellationServiceDTO toDTO(CancellationService in) {
        if (in == null) return null;

        CancellationServiceDTO out = new CancellationServiceDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setEnabled(in.getEnabled());
        out.setDefaultAllowed(in.getDefaultAllowed());
        out.setDefaultSelected(in.getDefaultSelected());
        return out;
    }

    public static ChannelLanguagesDTO convertToLanguageDTO(Language source, Map<Long, String> masterLanguages) {
        if (source == null) {
            return null;
        }
        ChannelLanguagesDTO target = new ChannelLanguagesDTO();
        if (source.getDefaultLanguageId() != null) {
            String locale = ConverterUtils.toLanguageTag(masterLanguages.get(source.getDefaultLanguageId()));
            target.setDefaultLanguageCode(locale);
        }
        if (CollectionUtils.isNotEmpty(source.getSelectedLanguages())) {
            target.setSelectedLanguageCode(new ArrayList<>());
            source.getSelectedLanguages().stream().filter(masterLanguages::containsKey).map(masterLanguages::get)
                    .forEach(languageCode -> target.getSelectedLanguageCode().add(ConverterUtils.toLanguageTag(languageCode)));
        }
        return target;
    }

    public static Set<String> fromLanguageIdToLanguageCode(List<Long> languageIds, Map<Long, String> masterLanguages) {
        return languageIds.stream().filter(masterLanguages::containsKey).map(masterLanguages::get).map(ConverterUtils::toLanguageTag)
                .collect(Collectors.toSet());
    }

    public static ChannelVouchersDTO toDTO(ChannelVouchers from, List<Currency> currencies) {
        ChannelVouchersDTO target = new ChannelVouchersDTO();
        target.setAllowRedeemVouchers(from.getAllowRedeemVouchers());
        target.setAllowRefundToVouchers(from.getAllowRefundToVouchers());
        if (from.getGiftCardId() != null) {
            ChannelVoucherGiftCardDTO voucherGiftCardDTO = new ChannelVoucherGiftCardDTO();
            voucherGiftCardDTO.setAllowPurchaseGiftCard(from.getAllowPurchaseGiftCard());
            voucherGiftCardDTO.setGiftCardId(from.getGiftCardId());
            target.setChannelVoucherGiftCardDTO(voucherGiftCardDTO);
        } else if (CollectionUtils.isNotEmpty(from.getGiftCardsIds())) {
            ChannelCurrencyVoucherGiftCardDTO currencyVoucherGiftCardDTO = new ChannelCurrencyVoucherGiftCardDTO();
            currencyVoucherGiftCardDTO.setAllowPurchaseGiftCard(from.getAllowPurchaseGiftCard());
            currencyVoucherGiftCardDTO.setGiftCardsIds(toIdValueDTO(from.getGiftCardsIds(), currencies));
            target.setChannelCurrencyVoucherGiftCardDTO(currencyVoucherGiftCardDTO);
        }
        return target;
    }

    private static List<CurrencyGiftCardDTO> toIdValueDTO(List<CurrencyGiftCard> currencyGiftCards, List<Currency> currencies) {
        return currencyGiftCards.stream()
                .map(g -> toDTO(g, currencies))
                .toList();
    }

    private static CurrencyGiftCardDTO toDTO(CurrencyGiftCard currencyGiftCard, List<Currency> currencies) {
        CurrencyGiftCardDTO currencyGiftCardDTO = new CurrencyGiftCardDTO();
        currencyGiftCardDTO.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, currencyGiftCard.getCurrencyId()));
        currencyGiftCardDTO.setGiftCardId(currencyGiftCard.getGiftCardId());
        return currencyGiftCardDTO;
    }

    public static ChannelVouchers fromDTO(UpdateChannelVouchersRequestDTO from, List<Currency> currencies) {
        ChannelVouchers target = new ChannelVouchers();
        target.setAllowRedeemVouchers(from.getAllowRedeemVouchers());
        target.setAllowRefundToVouchers(from.getAllowRefundToVouchers());
        if (from.getChannelVoucherGiftCardDTO() != null) {
            target.setAllowPurchaseGiftCard(from.getChannelVoucherGiftCardDTO().getAllowPurchaseGiftCard());
            target.setGiftCardId(from.getChannelVoucherGiftCardDTO().getGiftCardId());
        }
        if (from.getChannelCurrencyVoucherGiftCardDTO() != null) {
            target.setAllowPurchaseGiftCard(from.getChannelCurrencyVoucherGiftCardDTO().getAllowPurchaseGiftCard());
            if (CollectionUtils.isNotEmpty(from.getChannelCurrencyVoucherGiftCardDTO().getGiftCardsIds())) {
                target.setGiftCardsIds(fromDTO(from.getChannelCurrencyVoucherGiftCardDTO().getGiftCardsIds(), currencies));
            }
        }
        return target;
    }

    private static List<CurrencyGiftCard> fromDTO(List<CurrencyGiftCardDTO> currencyGiftCards, List<Currency> currencies) {
        return currencyGiftCards.stream()
                .map(g -> fromDTO(g, currencies))
                .toList();
    }

    private static CurrencyGiftCard fromDTO(CurrencyGiftCardDTO currencyGiftCardDTO, List<Currency> currencies) {
        CurrencyGiftCard currencyGiftCard = new CurrencyGiftCard();
        currencyGiftCard.setGiftCardId(currencyGiftCardDTO.getGiftCardId());
        currencyGiftCard.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, currencyGiftCardDTO.getCurrencyCode()));
        return currencyGiftCard;
    }

    public static ChannelEventsSaleRestrictionsDTO toChannelEventsSaleRestrictionsDTO(ChannelEventSaleRestrictionResponse from) {
        ChannelEventsSaleRestrictionsDTO target = new ChannelEventsSaleRestrictionsDTO();
        if (from != null && from.getEventSaleRestrictions() != null) {
            target.addAll(from.getEventSaleRestrictions().entrySet().stream()
                    .map(esr -> new ChannelEventSaleRestrictionsDTO(esr.getKey().longValue(),
                            esr.getValue().stream().map(Integer::longValue).collect(Collectors.toList())))
                    .toList());
        }

        return target;
    }

    public static ChannelEventsSaleRestrictions toChannelEventsSaleRestrictions(ChannelEventsSaleRestrictionsDTO from) {
        ChannelEventsSaleRestrictions target = new ChannelEventsSaleRestrictions();
        if (from == null) {
            return target;
        }

        target.addAll(from.stream()
                .map(ChannelConverter::toChannelEventSaleRestrictions)
                .filter(Objects::nonNull)
                .toList());

        return target;
    }

    private static ChannelEventSaleRestrictions toChannelEventSaleRestrictions(ChannelEventSaleRestrictionsDTO from) {
        if (from == null) {
            return null;
        }
        ChannelEventSaleRestrictions result = new ChannelEventSaleRestrictions();
        result.setRequiredEventId(from.getRequiredEventId());
        result.setCartEventIds(from.getCartEventIds());
        return result;
    }

    private static ChannelSettingsDTO fillSettings(ChannelResponse channel, Boolean hasActivePromotion) {
        ChannelSettingsDTO settings = new ChannelSettingsDTO();

        if (SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)) {
            settings.setAutomaticSeatSelection(channel.getAutomaticSeatSelection());
            settings.setAutomaticSeatSelectionByPriceZone(channel.getAutomaticSeatSelectionByPriceZone());
        }
        settings.setEnableB2B(channel.getEnableB2B());
        settings.setUseMultiEvent(channel.getUseMultiEvent());
        settings.setAllowDataProtectionFields(channel.getAllowDataProtectionFields());
        settings.setAllowLinkedCustomers(channel.getAllowLinkedCustomers());
        settings.setUseRobotIndexation(channel.getUseRobotIndexation());
        settings.setV4Enabled(channel.getV4Enabled());
        settings.setV4ConfigEnabled(channel.getV4ConfigEnabled());
        settings.setV2ReceiptTemplateEnabled(DEFAULT_V2_RECEIPT_TEMPLATE <= channel.getIdReceiptTemplate());
        settings.setAllowDownloadPassbook(channel.getAllowDownloadPassbook());
        settings.setAllowB2BPublishing(channel.getAllowB2BPublishing());
        settings.setEnableB2BEventCategoryFilter(channel.getEnableB2BEventCategoryFilter());
        settings.setInvitationsSettings(toDTO(channel.getInvitationsSettings()));
        settings.setWhatsappConfig(toDTO(channel.getWhatsappConfig()));
        settings.setUseCurrencyExchange(channel.getUseCurrencyExchange());
        settings.setCustomerAssignation(toDTO(channel));
        settings.setEnablePacksAndEventsCatalog(channel.getEnablePacksAndEventsCatalog());
        if (Boolean.TRUE.equals(channel.getUseCurrencyExchange())) {
            settings.setCurrencyDefaultExchange(channel.getCurrencyDefaultExchange());
        } else {
            settings.setCurrencyDefaultExchange(null);
        }

        if (hasActivePromotion != null) {
            settings.setActivePromotion(hasActivePromotion);
        }

        if (channel.getSurchargesSettings() != null) {
            SurchargesSettingsDTO surchargesSettings = new SurchargesSettingsDTO();
            surchargesSettings.setCalculation(ChannelSurchargesCalculationDTO.fromMs(channel.getSurchargesSettings().getCalculation()));
            settings.setSurchargesSettings(surchargesSettings);
        }

        if (channel.getSupportEmail() != null) {
            SupportEmailDTO supportEmailDTO = new SupportEmailDTO();
            supportEmailDTO.setEnabled(channel.getSupportEmail().getEnabled());
            supportEmailDTO.setAddress(channel.getSupportEmail().getAddress());
            settings.setSupportEmailDTO(supportEmailDTO);
        }

        if (!CommonUtils.isNull(channel.getDonationsConfig())) {
            settings.setDonationsConfig(buildDonationsConfigDTO(channel.getDonationsConfig()));
        }

        if (channel.hasDestinationChannel()) {
            DestinationChannelDTO destinationChannelDTO = new DestinationChannelDTO(
                channel.getDestinationChannelType(),
                channel.getDestinationChannel()
            );
            settings.setDestinationChannel(destinationChannelDTO);
        }

        return settings;
    }

    private static InvitationsSettingsDTO toDTO(InvitationsSettings invitationsSettings) {
        if (invitationsSettings == null) {
            return null;
        }

        InvitationsSettingsDTO dto = new InvitationsSettingsDTO();
        dto.setEnabled(invitationsSettings.getEnabled());
        dto.setSelectionMode(invitationsSettings.getSelectionMode());

        return dto;
    }

    private static ChannelLimitsDTO fillLimits(ChannelResponse channel) {
        ChannelLimitsTicketsDTO ticketsLimit = fillLimitTickets(channel);
        if (ticketsLimit == null) {
            return null;
        }
        ChannelLimitsDTO out = new ChannelLimitsDTO();
        out.setTickets(ticketsLimit);
        return out;
    }

    private static ChannelLimitsTicketsDTO fillLimitTickets(ChannelResponse channel) {
        if (channel.getTicketBookingMax() == null && channel.getTicketIssueMax() == null && channel.getTicketPurchaseMax() == null) {
            return null;
        }
        ChannelLimitsTicketsDTO out = new ChannelLimitsTicketsDTO();
        out.setBookingMax(channel.getTicketBookingMax());
        out.setIssueMax(channel.getTicketIssueMax());
        out.setPurchaseMax(channel.getTicketPurchaseMax());
        return out;
    }

    private static void fillLimits(ChannelLimitsDTO limits, ChannelUpdateRequest target) {
        if (limits.getTickets() != null) {
            target.setTicketBookingMax(limits.getTickets().getBookingMax());
            target.setTicketIssueMax(limits.getTickets().getIssueMax());
            target.setTicketPurchaseMax(limits.getTickets().getPurchaseMax());
        }
    }

    private static void fillSettings(UpdateChannelRequestDTO source, ChannelUpdateRequest target) {
        ChannelSettingsUpdateDTO settings = source.getSettings();

        target.setAutomaticSeatSelection(settings.getAutomaticSeatSelection());
        target.setAutomaticSeatSelectionByPriceZone(settings.getAutomaticSeatSelectionByPriceZone());
        target.setEnableB2B(settings.getEnableB2B());
        target.setUseMultiEvent(settings.getUseMultiEvent());
        target.setAllowDataProtectionFields(settings.getAllowDataProtectionFields());
        target.setAllowLinkedCustomers(settings.getAllowLinkedCustomers());
        target.setUseRobotIndexation(settings.getUseRobotIndexation());
        target.setRobotsNoFollow(settings.getRobotsNoFollow());
        target.setAllowB2BPublishing(settings.getAllowB2BPublishing());
        target.setEnableB2BEventCategoryFilter(settings.getEnableB2BEventCategoryFilter());
        target.setInvitationsSettings(toMS(settings.getInvitationsSettings()));
        target.setWhatsappConfig(toMS(settings.getWhatsappConfig()));
        target.setUseCurrencyExchange(settings.getUseCurrencyExchange());
        target.setCurrencyDefaultExchange(settings.getCurrencyDefaultExchange());
        target.setEnablePacksAndEventsCatalog(settings.getEnablePacksAndEventsCatalog());

        if (settings.getCustomerAssignation() != null) {
            target.setShowCustomerAssignation(settings.getCustomerAssignation().getEnabled());
            target.setCustomerAssignationMode(settings.getCustomerAssignation().getMode());
        }
        if (settings.getSurchargesSettings() != null) {
            SurchargesSettingsDTO surchargesSettingsDTO = new SurchargesSettingsDTO();
            surchargesSettingsDTO.setCalculation(source.getSettings().getSurchargesSettings().getCalculation());
            target.setSurchargesSettings(surchargesSettingsDTO);
        }
        if (settings.getSupportEmailDTO() != null) {
            SupportEmail supportEmail = new SupportEmail();
            supportEmail.setEnabled(settings.getSupportEmailDTO().getEnabled());
            supportEmail.setAddress(settings.getSupportEmailDTO().getAddress());
            target.setSupportEmail(supportEmail);
        }
        if (!CommonUtils.isNull(settings.getDonationsConfig())) {
            target.setDonationsConfig(buildDonationsConfig(settings.getDonationsConfig()));
        }
        if(settings.getDestinationChannel() != null) {
            target.setHasDestinationChannel(true);
            target.setDestinationChannel(settings.getDestinationChannel().getDestinationChannelId());
            target.setDestinationChannelType(settings.getDestinationChannel().getDestinationChannelType());
        }
    }

    private static InvitationsSettings toMS(InvitationsSettingsDTO dto) {
        if (dto == null) {
            return null;
        }

        InvitationsSettings invitationsSettings = new InvitationsSettings();
        invitationsSettings.setEnabled(dto.getEnabled());
        invitationsSettings.setSelectionMode(dto.getSelectionMode());

        return invitationsSettings;
    }

    private static Language convertToMsChannelDTO(ChannelLanguagesDTO sourceLanguageDTO, Map<String, Long> masterLanguages) {
        Language target = new Language();

        if (sourceLanguageDTO != null) {
            if (sourceLanguageDTO.getDefaultLanguageCode() != null && !sourceLanguageDTO.getDefaultLanguageCode().isEmpty()) {
                String locale = ConverterUtils.checkLanguage(sourceLanguageDTO.getDefaultLanguageCode(), masterLanguages);
                target.setDefaultLanguageId(masterLanguages.get(locale));
            }
            if (!CommonUtils.isEmpty(sourceLanguageDTO.getSelectedLanguageCode())) {
                target.setSelectedLanguages(new ArrayList<>());
                for (String selectedLang : sourceLanguageDTO.getSelectedLanguageCode()) {
                    if (!StringUtils.isEmpty(selectedLang)) {
                        String locale = ConverterUtils.checkLanguage(selectedLang, masterLanguages);
                        target.getSelectedLanguages().add(masterLanguages.get(locale));
                    }
                }
            }
        }

        return target;
    }

    private static DonationsConfigDTO buildDonationsConfigDTO(DonationsConfig donationsConfig) {
        if (CommonUtils.isNull(donationsConfig)) {
            return null;
        }

        DonationsConfigDTO donationsConfigDTO = new DonationsConfigDTO();
        donationsConfigDTO.setEnabled(donationsConfig.getEnabled());
        donationsConfigDTO.setProvider(buildDonationProviderDTO(donationsConfig.getProvider()));
        donationsConfigDTO.setSettings(buildDonationSettingsDTO(donationsConfig.getSettings()));
        donationsConfigDTO.setCampaign(buildCampaignDTO(donationsConfig.getCampaign()));

        return donationsConfigDTO;
    }

    private static DonationProviderDTO buildDonationProviderDTO(DonationProvider provider) {
        if (CommonUtils.isNull(provider)) {
            return null;
        }

        DonationProviderDTO dto = new DonationProviderDTO();
        dto.setId(provider.getId());
        dto.setTargetId(provider.getTargetId());
        dto.setAdditionalProperties(provider.getAdditionalProperties());

        return dto;
    }

    private static DonationSettingsDTO buildDonationSettingsDTO(DonationSettings settings) {
        if (CommonUtils.isNull(settings)) {
            return null;
        }

        DonationSettingsDTO dto = new DonationSettingsDTO();
        dto.setOptions(settings.getOptions());
        dto.setType(settings.getType());

        return dto;
    }

    private static CampaignDTO buildCampaignDTO(Campaign campaign) {
        if (CommonUtils.isNull(campaign)) {
            return null;
        }

        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        dto.setName(campaign.getName());
        dto.setFoundation(campaign.getFoundation());
        dto.setCurrencyCode(campaign.getCurrencyCode());
        dto.setWebsite(campaign.getWebsite());

        return dto;
    }

    private static DonationsConfig buildDonationsConfig(DonationsConfigDTO donationsConfigDTO) {
        if (CommonUtils.isNull(donationsConfigDTO)) {
            return null;
        }

        DonationsConfig donationsConfig = new DonationsConfig();
        donationsConfig.setEnabled(donationsConfigDTO.getEnabled());
        donationsConfig.setProvider(buildDonationProvider(donationsConfigDTO.getProvider()));
        donationsConfig.setSettings(buildDonationSettings(donationsConfigDTO.getSettings()));
        donationsConfig.setCampaign(buildCampaign(donationsConfigDTO.getCampaign()));

        return donationsConfig;
    }

    private static DonationProvider buildDonationProvider(DonationProviderDTO providerDTO) {
        if (CommonUtils.isNull(providerDTO)) {
            return null;
        }

        DonationProvider provider = new DonationProvider();
        provider.setId(providerDTO.getId());
        provider.setTargetId(providerDTO.getTargetId());
        provider.setAdditionalProperties(providerDTO.getAdditionalProperties());

        return provider;
    }

    private static DonationSettings buildDonationSettings(DonationSettingsDTO settingsDTO) {
        if (CommonUtils.isNull(settingsDTO)) {
            return null;
        }

        if (DonationType.CUSTOM.equals(settingsDTO.getType())) {
            Set<Double> validOptions = getValidOptions(settingsDTO.getOptions());
            settingsDTO.setOptions(validOptions);
        }

        DonationSettings settings = new DonationSettings();
        settings.setOptions(settingsDTO.getOptions());
        settings.setType(settingsDTO.getType());

        return settings;
    }

    private static Set<Double> getValidOptions(Set<Double> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }

        Set<Double> validOptions = options.stream()
                .map(NumberUtils::scale)
                .map(Number::doubleValue)
                .filter(option -> option > 0)
                .collect(Collectors.toSet());

        if (validOptions.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_OPTION_VALUE);
        }

        return validOptions;
    }

    private static Campaign buildCampaign(CampaignDTO campaignDTO) {
        if (CommonUtils.isNull(campaignDTO)) {
            return null;
        }

        Campaign campaign = new Campaign();
        campaign.setId(campaignDTO.getId());
        campaign.setName(campaignDTO.getName());
        campaign.setFoundation(campaignDTO.getFoundation());
        campaign.setCurrencyCode(campaignDTO.getCurrencyCode());
        campaign.setWebsite(campaignDTO.getWebsite());

        return campaign;
    }

    private static WhatsappConfig toMS(WhatsappConfigDTO whatsAppConfigDTO) {
        if (whatsAppConfigDTO == null) {
            return null;
        }

        WhatsappConfig whatsAppConfig = new WhatsappConfig();
        whatsAppConfig.setOverrideEntityConfig(whatsAppConfigDTO.getOverrideEntityConfig());
        whatsAppConfig.setWhatsappTemplate(whatsAppConfigDTO.getWhatsappTemplate());

        return whatsAppConfig;
    }

    private static WhatsappConfigDTO toDTO(WhatsappConfig whatsAppConfig) {
        if (whatsAppConfig == null) {
            return null;
        }

        WhatsappConfigDTO dto = new WhatsappConfigDTO();
        dto.setOverrideEntityConfig(whatsAppConfig.getOverrideEntityConfig());
        dto.setWhatsappTemplate(whatsAppConfig.getWhatsappTemplate());

        return dto;
    }

    private static CustomerAssignationDTO toDTO(ChannelResponse channelResponse) {
        if (channelResponse.getShowCustomerAssignation() == null && channelResponse.getCustomerAssignationMode() == null) {
            return null;
        }
        CustomerAssignationDTO dto = new CustomerAssignationDTO();
        dto.setEnabled(channelResponse.getShowCustomerAssignation());
        dto.setMode(channelResponse.getCustomerAssignationMode());
        return dto;

    }
}

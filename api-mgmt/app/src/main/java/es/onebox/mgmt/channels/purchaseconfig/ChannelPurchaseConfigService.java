package es.onebox.mgmt.channels.purchaseconfig;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.purchaseconfig.converter.ChannelPurchaseConfigConverter;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPriceDisplayConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigInvoiceDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigVenueDTO;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelLinkDestinationMode;
import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceRequestType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.enums.PriceDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.enums.TaxesDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.UrlFormatValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.security.SecurityManager.MSG_ENTITY_MISSMATCH;

@Service
public class ChannelPurchaseConfigService {

    private final ChannelsRepository channelsRepository;
    private final EntitiesRepository entitiesRepository;
    private final ChannelsHelper channelsHelper;
    private final UrlFormatValidator urlFormatValidator;

    @Autowired
    public ChannelPurchaseConfigService(ChannelsRepository channelsRepository, ChannelsHelper channelsHelper, EntitiesRepository entitiesRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
        this.entitiesRepository = entitiesRepository;
        this.urlFormatValidator = new UrlFormatValidator();
    }

    public ChannelPurchaseConfigDTO getPurchaseConfig(final Long channelId) {
        validateChannel(channelId);
        ChannelConfig response = channelsRepository.getChannelConfig(channelId);
        return ChannelPurchaseConfigConverter.toDTO(response);
    }

    public void updatePurchaseConfig(final Long channelId, final ChannelPurchaseConfigDTO body) {
        ChannelConfig originalChannelConfig = channelsRepository.getChannelConfig(channelId);
        validateUpdate(channelId, body, originalChannelConfig.getDefaultLanguageCode());
        ChannelConfig relatedChannelConfig = validateRelatedChannel(body.getRelatedChannel());
        ChannelConfig channelConfig = ChannelPurchaseConfigConverter.updateChannelConfig(originalChannelConfig, body, relatedChannelConfig);
        channelsRepository.updateChannelConfig(channelId, channelConfig);
    }

    private ChannelResponse validateChannel(final Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        if (ChannelType.EXTERNAL.equals(channelResponse.getType()) || ChannelType.OB_BOX_OFFICE.equals(channelResponse.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
        return channelResponse;
    }

    private ChannelConfig validateRelatedChannel(final Long relatedChannelId) {
        if (relatedChannelId == null) {
            return null;
        }
        ChannelConfig relatedChannelConfig = channelsRepository.getChannelConfig(relatedChannelId);
        ChannelResponse relatedChannelResponse = validateChannel(relatedChannelConfig.getId());
        boolean isObPortal = ChannelType.OB_PORTAL.equals(relatedChannelResponse.getType());
        if (!isObPortal) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }

        return relatedChannelConfig;
    }

    private void validateUpdate(final Long channelId, final ChannelPurchaseConfigDTO body, final String defaultLang) {

        ChannelResponse channelResponse = validateChannel(channelId);
        boolean isObPortal = ChannelType.OB_PORTAL.equals(channelResponse.getType());
        boolean isObMember = ChannelType.MEMBER.equals(channelResponse.getType());
        boolean isB2B = ChannelSubtype.PORTAL_B2B.equals(channelResponse.getSubtype());
        if (body.getChannelBuyerRegistration() != null && !isObPortal && !isObMember) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }

        if (body.getChannelCommercialInformationConsent() != null && !isObPortal && !isObMember) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }

        if (BooleanUtils.isTrue(body.getAllowPriceTypeTagFilter()) && !isB2B) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }

        ChannelPurchaseConfigVenueDTO venue = body.getVenue();

        if (venue != null && (venue.getInteractiveVenueTypes() != null ||
                venue.getAllowSeat3dView() != null || venue.getAllowSector3dView() != null)) {

            if (BooleanUtils.isTrue(venue.getAllowInteractiveVenue()) && venue.getInteractiveVenueTypes().isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG);
            }

            Entity entity = entitiesRepository.getEntity(channelResponse.getEntityId());

            if (entity.getInteractiveVenue() == null || CommonUtils.isFalse(entity.getInteractiveVenue().getEnabled())) {
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_CHANNEL_INTERACTIVE_VENUE_UPDATE);
            }

            List<InteractiveVenueType> allowedVenues = body.getVenue().getInteractiveVenueTypes();
            if (allowedVenues != null) {
                for (InteractiveVenueType venueType : allowedVenues) {
                    if (entity.getInteractiveVenue().getAllowedVenues().stream().noneMatch(vt -> vt.name().equals(venueType.name()))) {
                        throw ExceptionBuilder.build(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY, venueType.name());
                    }
                }
            }
        }

        if (venue != null && (BooleanUtils.isTrue(venue.getAllowInteractiveVenue()) && venue.getInteractiveVenueTypes() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG);
        }

        ChannelPurchaseConfigInvoiceDTO channelPurchaseConfigInvoiceDTO = body.getChannelPurchaseInvoice();

        if (channelPurchaseConfigInvoiceDTO != null && BooleanUtils.isTrue(channelPurchaseConfigInvoiceDTO.getEnabled())) {
            if (InvoiceRequestType.BY_AMOUNT.equals(channelPurchaseConfigInvoiceDTO.getInvoiceRequestType()) && CollectionUtils.isEmpty(channelPurchaseConfigInvoiceDTO.getMandatoryThresholds())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.INVALID_INVOICE_CONFIGURATION);
            }
        }

        if (body.getRedirectionPolicy() != null) {
            if (body.getRedirectionPolicy().stream()
                    .filter(elem -> ChannelLinkDestinationMode.ORIGIN.equals(elem.getMode()))
                    .anyMatch(elem -> elem.getValue() != null)) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_REDIRECTION_POLICY_ORIGIN_TYPE_NOT_NEED_VALUE);
            }

            if (body.getRedirectionPolicy().stream()
                    .filter(elem -> ChannelLinkDestinationMode.CUSTOM.equals(elem.getMode()))
                    .anyMatch(elem -> MapUtils.isEmpty(elem.getValue()) || !elem.getValue().containsKey(ConverterUtils.toLanguageTag(defaultLang)))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_REDIRECTION_POLICY_CUSTOM_TYPE_MISSING_DEFAULT_VALUE);
            }

            if (body.getRedirectionPolicy().stream()
                    .filter(elem -> ChannelLinkDestinationMode.CUSTOM.equals(elem.getMode()))
                    .anyMatch(elem -> elem.getValue().values().stream().anyMatch(url -> !urlFormatValidator.isValid(url, null)))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_REDIRECTION_POLICY_CUSTOM_TYPE_INVALID_URL);
            }
        }

        ChannelPriceDisplayConfigDTO priceDisplayConfig = body.getPriceDisplayConfig();
        if (priceDisplayConfig != null) {
            PriceDisplayMode prices = priceDisplayConfig.getPrices();
            TaxesDisplayMode taxes = priceDisplayConfig.getTaxes();
            if ((prices != null && taxes == null) || (taxes != null && prices == null)) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_TAX_DISPLAY_MODE_MISSING);
            }
            if ((PriceDisplayMode.NET.equals(prices) && !TaxesDisplayMode.ON_TOP.equals(taxes)) || (TaxesDisplayMode.ON_TOP.equals(taxes) && !PriceDisplayMode.NET.equals(prices))) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PRICE_DISPLAY);
            }
        }
    }
}

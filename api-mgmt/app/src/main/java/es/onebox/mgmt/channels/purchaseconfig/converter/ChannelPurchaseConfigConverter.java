package es.onebox.mgmt.channels.purchaseconfig.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPriceDisplayConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigHeaderTextsDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigInvoiceDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigLinkDestinationDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigPromotionsDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigRedirectionPolicyDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigSessionsDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigVenueDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigVisualizationDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.LoyaltyProgramDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.LoyaltyReceptionDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.MandatoryThresholdDTO;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelBuyerRegistration;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelCommercialInformationConsent;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelHeaderText;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelLinkDestinationMode;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelLinkDestinationType;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelPromotionCodePersistence;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelSessionVisualizationFormat;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelVenueContentLayout;
import es.onebox.mgmt.channels.purchaseconfig.enums.LoyaltyReceptionTypeDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelInvoiceSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelPurchaseConfigLinkDestination;
import es.onebox.mgmt.datasources.ms.channel.dto.LoyaltyProgram;
import es.onebox.mgmt.datasources.ms.channel.dto.LoyaltyReception;
import es.onebox.mgmt.datasources.ms.channel.dto.MandatoryThreshold;
import es.onebox.mgmt.datasources.ms.channel.dto.PriceDisplay;
import es.onebox.mgmt.datasources.ms.channel.dto.RelatedChannelInfo;
import es.onebox.mgmt.datasources.ms.channel.enums.LoyaltyReceptionType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelPurchaseConfigConverter {

    private ChannelPurchaseConfigConverter() {
    }

    public static ChannelPurchaseConfigDTO toDTO(ChannelConfig source) {
        ChannelPurchaseConfigDTO target = new ChannelPurchaseConfigDTO();
        target.setIncludeTaxesSeparately(source.getIncludeTaxes());
        if (source.getUseUserLogin() != null && source.getForceLogin() != null) {
            target.setChannelBuyerRegistration(ChannelBuyerRegistration.get(source.getUseUserLogin(), source.getForceLogin()));
        }

        if (source.getAllowCommercialMailing() != null && source.getCommercialMailingNegativeAuth() != null) {
            target.setChannelCommercialInformationConsent(
                    ChannelCommercialInformationConsent.get(source.getAllowCommercialMailing(), source.getCommercialMailingNegativeAuth()));
        }

        target.setSessions(toSessionsDTO(source));
        target.setVenue(toVenueDTO(source));
        target.setHeaderTexts(toHeaderTextsDTO(source));
        target.setRedirectionPolicy(toRedirectionPolicy(source));
        target.setShowAcceptAllOption(source.getShowAcceptAllOption());

        RelatedChannelInfo relatedChannelInfo = source.getRelatedChannelInfo();
        if (relatedChannelInfo != null) {
            target.setAddRelatedChannel(relatedChannelInfo.getUseRelatedChannel());
            target.setRelatedChannel(relatedChannelInfo.getId());
        }

        if (source.getInvoiceSettings() != null) {
            target.setChannelPurchaseInvoice(toInvoiceDTO(source.getInvoiceSettings()));
        }

        if (source.getLoyaltyProgram() != null) {
            target.setLoyaltyProgram(toDTO(source.getLoyaltyProgram()));
        }

        if (source.getPriceDisplaySettings() != null) {
            target.setPriceDisplayConfig(toDTO(source.getPriceDisplaySettings()));
        }

        if (source.getAllowPriceTypeTagFilter() != null) {
            target.setAllowPriceTypeTagFilter(source.getAllowPriceTypeTagFilter());
        }

        return target;
    }

    private static ChannelPriceDisplayConfigDTO toDTO(PriceDisplay in) {
        if (in == null) {
            return null;
        }
        return new ChannelPriceDisplayConfigDTO(in.getPriceDisplayMode(), in.getTaxesDisplayMode());
    }

    private static ChannelPurchaseConfigSessionsDTO toSessionsDTO(ChannelConfig source) {
        ChannelPurchaseConfigSessionsDTO target = new ChannelPurchaseConfigSessionsDTO();
        target.setPromotions(toSessionsPromotionsDTO(source));
        target.setVisualization(toSessionsVisualizationDTO(source));
        if (target.getPromotions() == null && target.getVisualization() == null) {
            return null;
        }
        return target;
    }

    private static ChannelPurchaseConfigPromotionsDTO toSessionsPromotionsDTO(ChannelConfig source) {
        if (source.getKeepSalesCode() == null) {
            return null;
        }
        ChannelPurchaseConfigPromotionsDTO target = new ChannelPurchaseConfigPromotionsDTO();
        target.setCodePersistence(ChannelPromotionCodePersistence.fromBoolean(source.getKeepSalesCode()));
        return target;
    }

    private static ChannelPurchaseConfigVisualizationDTO toSessionsVisualizationDTO(ChannelConfig source) {
        ChannelPurchaseConfigVisualizationDTO target = new ChannelPurchaseConfigVisualizationDTO();
        if (source.getShowSessionsList() != null) {
            target.setFormat(ChannelSessionVisualizationFormat.fromBoolean(source.getShowSessionsList()));
        }
        target.setMaxListed(source.getMaxSessionsInList());
        return target;
    }

    private static ChannelPurchaseConfigVenueDTO toVenueDTO(ChannelConfig source) {
        if (source.getUseSeat3dView() == null && source.getUseSector3dView() == null
                && source.getDefaultView() == null && source.getUse3dVenueModule() && source.getUse3dVenueModuleV2()) {
            return null;
        }
        ChannelPurchaseConfigVenueDTO target = new ChannelPurchaseConfigVenueDTO();
        target.setChannelVenueContentLayout(source.getDefaultView() == null ? null : ChannelVenueContentLayout.fromId(source.getDefaultView()));

        if (CollectionUtils.isNotEmpty(source.getInteractiveVenueTypes())) {
            target.setAllowInteractiveVenue(Boolean.TRUE);
            target.setInteractiveVenueTypes(source.getInteractiveVenueTypes());
            target.setAllowSeat3dView(source.getUseSeat3dView());
            target.setAllowSector3dView(source.getUseSector3dView());
            target.setAllowVenue3dView(CommonUtils.isTrue(source.getUse3dVenueModule()) || CommonUtils.isTrue(source.getUse3dVenueModuleV2())
                    || CommonUtils.isTrue(source.getUseVenue3dView()));
        } else {
            target.setAllowInteractiveVenue(Boolean.FALSE);
            target.setInteractiveVenueTypes(null);
            target.setAllowSeat3dView(Boolean.FALSE);
            target.setAllowSector3dView(Boolean.FALSE);
            target.setAllowVenue3dView(Boolean.FALSE);
        }

        return target;
    }

    private static ChannelPurchaseConfigHeaderTextsDTO toHeaderTextsDTO(ChannelConfig source) {
        return source.getChannelComponentVisibility()
                .entrySet()
                .stream()
                .filter(elem -> BooleanUtils.isTrue(elem.getValue()))
                .map(elem -> ChannelHeaderText.getFromValue(elem.getKey()))
                .collect(Collectors.toCollection(ChannelPurchaseConfigHeaderTextsDTO::new));

    }

    private static ChannelPurchaseConfigRedirectionPolicyDTO toRedirectionPolicy(ChannelConfig source) {
        return source.getChannelRedirectionPolicy().entrySet().stream()
                .map(entry -> toLinkDestination(entry, source.getDefaultLanguageCode()))
                .collect(Collectors.toCollection(ChannelPurchaseConfigRedirectionPolicyDTO::new));
    }

    private static ChannelPurchaseConfigLinkDestinationDTO toLinkDestination(Map.Entry<String, ChannelPurchaseConfigLinkDestination> source, String defaultLang) {
        ChannelPurchaseConfigLinkDestinationDTO target = new ChannelPurchaseConfigLinkDestinationDTO();
        target.setType(ChannelLinkDestinationType.valueOf(source.getKey()));
        target.setMode(ChannelLinkDestinationMode.valueOf(source.getValue().getCode()));
        if (ChannelLinkDestinationMode.CUSTOM.equals(target.getMode())) {
            if (MapUtils.isNotEmpty(source.getValue().getI18nUrl())) {
                target.setValue(ConverterUtils.changeMapLangKeyToKevapCase(source.getValue().getI18nUrl()));
            } else if (source.getValue().getName() != null) {
                target.setValue(Map.of(ConverterUtils.toLanguageTag(defaultLang), source.getValue().getName()));
            }
        }
        return target;
    }

    private static ChannelPurchaseConfigInvoiceDTO toInvoiceDTO(ChannelInvoiceSettings in) {
        if (in == null) {
            return null;
        }
        ChannelPurchaseConfigInvoiceDTO targetInvoiceDTO = new ChannelPurchaseConfigInvoiceDTO();
        if (in.getEnabled() != null) {
            targetInvoiceDTO.setEnabled(in.getEnabled());
        }
        if (in.getMandatoryThresholds() != null) {
            targetInvoiceDTO.setMandatoryThresholds(
                    in.getMandatoryThresholds().stream()
                            .map(thresholdDTO ->
                                    new MandatoryThresholdDTO(thresholdDTO.getCurrency(), thresholdDTO.getAmount()))
                            .toList()
            );
        }
        if (in.getInvoiceGenerationMode() != null) {
            targetInvoiceDTO.setInvoiceGenerationMode(in.getInvoiceGenerationMode());
        }
        if (in.getInvoiceRequestType() != null) {
            targetInvoiceDTO.setInvoiceRequestType(in.getInvoiceRequestType());
        }
        return targetInvoiceDTO;
    }

    public static ChannelConfig updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigDTO body, ChannelConfig relatedChannelConfig) {
        channelConfig.setIncludeTaxes(body.getIncludeTaxesSeparately());
        if (body.getChannelBuyerRegistration() != null) {
            channelConfig.setUseUserLogin(body.getChannelBuyerRegistration().isUseUserLogin());
            channelConfig.setForceLogin(body.getChannelBuyerRegistration().isForceLogin());
        }

        if (body.getChannelCommercialInformationConsent() != null) {
            channelConfig.setAllowCommercialMailing(body.getChannelCommercialInformationConsent().isAllowCommercialMailing());
            channelConfig.setCommercialMailingNegativeAuth(body.getChannelCommercialInformationConsent().isCommercialMailingNegativeAuth());
        }

        if (body.getSessions() != null) {
            updateChannelConfig(channelConfig, body.getSessions());
        }

        if (body.getVenue() != null) {
            updateChannelConfig(channelConfig, body.getVenue());
        }

        if (body.getHeaderTexts() != null) {
            updateChannelConfig(channelConfig, body.getHeaderTexts());
        }

        if (body.getRedirectionPolicy() != null) {
            updateChannelConfig(channelConfig, body.getRedirectionPolicy());
        }
        if (body.getShowAcceptAllOption() != null) {
            channelConfig.setShowAcceptAllOption(body.getShowAcceptAllOption());
        }
        if (body.getAddRelatedChannel() != null) {
            RelatedChannelInfo relatedChannelInfo = new RelatedChannelInfo();
            relatedChannelInfo.setUseRelatedChannel(body.getAddRelatedChannel());
            channelConfig.setRelatedChannelInfo(relatedChannelInfo);

            if (Boolean.FALSE.equals(relatedChannelInfo.getUseRelatedChannel())) {
                channelConfig.setRelatedChannel(null);
            } else {
                if (relatedChannelConfig != null && StringUtils.isNotEmpty(relatedChannelConfig.getUrl())) {
                    channelConfig.setRelatedChannel(relatedChannelConfig.getUrl());
                }
            }
        }

        if (body.getChannelPurchaseInvoice() != null) {
            updateChannelConfig(channelConfig, body.getChannelPurchaseInvoice());
        }

        if (body.getLoyaltyProgram() != null) {
            updateChannelConfig(channelConfig, body.getLoyaltyProgram());
        }

        if (body.getPriceDisplayConfig() != null) {
            updateChannelConfig(channelConfig, body.getPriceDisplayConfig());
        }

        if (body.getAllowPriceTypeTagFilter() != null) {
            channelConfig.setAllowPriceTypeTagFilter(body.getAllowPriceTypeTagFilter());
        }

        return channelConfig;
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigSessionsDTO sessions) {
        if (sessions.getPromotions() != null) {
            updateChannelConfig(channelConfig, sessions.getPromotions());
        }

        if (sessions.getVisualization() != null) {
            updateChannelConfig(channelConfig, sessions.getVisualization());
        }
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigPromotionsDTO promotions) {
        channelConfig.setKeepSalesCode(promotions.getCodePersistence().getKeepSalesCode());
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigVisualizationDTO visualization) {
        channelConfig.setShowSessionsList(visualization.getFormat().isList());
        channelConfig.setMaxSessionsInList(visualization.getMaxListed());
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigVenueDTO venue) {
        if (venue.getChannelVenueContentLayout() != null) {
            channelConfig.setDefaultView(venue.getChannelVenueContentLayout().getId());
        }
        if (BooleanUtils.isTrue(venue.getAllowInteractiveVenue())) {
            if (venue.getInteractiveVenueTypes() != null) {
                channelConfig.setInteractiveVenueTypes(venue.getInteractiveVenueTypes());
                if (venue.getAllowVenue3dView() != null) {
                    channelConfig.setUse3dVenueModule(venue.getInteractiveVenueTypes().contains(InteractiveVenueType.VENUE_3D_MMC_V1)
                            && BooleanUtils.isTrue(venue.getAllowVenue3dView()));
                    channelConfig.setUse3dVenueModuleV2(venue.getInteractiveVenueTypes().contains(InteractiveVenueType.VENUE_3D_MMC_V2)
                            && BooleanUtils.isTrue(venue.getAllowVenue3dView()));
                    channelConfig.setUseVenue3dView(BooleanUtils.isTrue(venue.getAllowVenue3dView()));
                }
            }
            ConverterUtils.updateField(channelConfig::setUseSector3dView, venue.getAllowSector3dView());
            ConverterUtils.updateField(channelConfig::setUseSeat3dView, venue.getAllowSeat3dView());
        } else {
            channelConfig.setInteractiveVenueTypes(Collections.emptyList());
            channelConfig.setUse3dVenueModule(Boolean.FALSE);
            channelConfig.setUse3dVenueModuleV2(Boolean.FALSE);
            channelConfig.setUseVenue3dView(Boolean.FALSE);
            channelConfig.setUseSector3dView(Boolean.FALSE);
            channelConfig.setUseSeat3dView(Boolean.FALSE);
        }
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigRedirectionPolicyDTO redirectionPolicy) {
        redirectionPolicy.forEach(elem ->
                channelConfig.getChannelRedirectionPolicy().put(elem.getType().name(), new ChannelPurchaseConfigLinkDestination(elem.getMode().name(),
                        elem.getValue() != null ? elem.getValue().get(ConverterUtils.toLanguageTag(channelConfig.getDefaultLanguageCode())) : null,
                        ConverterUtils.changeMapLangKeyToUnderScore(elem.getValue()))));
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigHeaderTextsDTO headerTexts) {
        channelConfig.setChannelComponentVisibility(
                Arrays.stream(ChannelHeaderText.values())
                        .collect(Collectors.toMap(
                                ChannelHeaderText::getValue,
                                headerTexts::contains
                        ))
        );
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPurchaseConfigInvoiceDTO invoice) {
        ChannelInvoiceSettings newChannelInvoiceSettings = new ChannelInvoiceSettings();

        if (invoice.getEnabled() != null) {
            newChannelInvoiceSettings.setEnabled(invoice.getEnabled());
        }

        if ((Boolean.TRUE.equals(invoice.getEnabled()) && invoice.getMandatoryThresholds() == null)
                || Boolean.FALSE.equals(invoice.getEnabled())) {
            newChannelInvoiceSettings.setMandatoryThresholds(channelConfig.getInvoiceSettings() != null
                    ? channelConfig.getInvoiceSettings().getMandatoryThresholds()
                    : null);
        } else {
            newChannelInvoiceSettings.setMandatoryThresholds(
                    invoice.getMandatoryThresholds().stream()
                            .map(toUpdate ->
                                    new MandatoryThreshold(toUpdate.getCurrency(), toUpdate.getAmount()))
                            .toList()
            );
        }

        if (invoice.getInvoiceGenerationMode() != null) {
            newChannelInvoiceSettings.setInvoiceGenerationMode(invoice.getInvoiceGenerationMode());
        }

        if (invoice.getInvoiceRequestType() != null) {
            newChannelInvoiceSettings.setInvoiceRequestType(invoice.getInvoiceRequestType());
        }

        channelConfig.setInvoiceSettings(newChannelInvoiceSettings);
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, LoyaltyProgramDTO in) {
        LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
        if (in.getReception() != null) {
            loyaltyProgram.setReception(toUpdate(in.getReception()));
        }
        channelConfig.setLoyaltyProgram(loyaltyProgram);
    }

    private static LoyaltyReception toUpdate(LoyaltyReceptionDTO in) {
        LoyaltyReception out = new LoyaltyReception();
        out.setType(LoyaltyReceptionType.valueOf(in.getType().name()));
        out.setHours(in.getHours());
        return out;
    }

    private static LoyaltyProgramDTO toDTO(LoyaltyProgram in) {
        LoyaltyProgramDTO out = new LoyaltyProgramDTO();
        if (in.getReception() != null) {
            out.setReception(toDTO(in.getReception()));
        }
        return out;
    }

    private static LoyaltyReceptionDTO toDTO(LoyaltyReception in) {
        LoyaltyReceptionDTO out = new LoyaltyReceptionDTO();
        out.setType(LoyaltyReceptionTypeDTO.valueOf(in.getType().name()));
        out.setHours(in.getHours());
        return out;
    }

    private static void updateChannelConfig(ChannelConfig channelConfig, ChannelPriceDisplayConfigDTO in) {
        if (in == null) {
            return;
        }
        PriceDisplay out = new PriceDisplay();
        out.setPriceDisplayMode(in.getPrices());
        out.setTaxesDisplayMode(in.getTaxes());
        channelConfig.setPriceDisplaySettings(out);
    }

}

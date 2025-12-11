package es.onebox.mgmt.channels.converter;

import es.onebox.mgmt.channels.dto.ChannelWhitelabelCartDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelCheckoutAttendeesDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelCheckoutDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelPromotionsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelResendTicketsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelReviewsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelThankYouPageDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelThankYouPageModuleDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelVenuemapDTO;
import es.onebox.mgmt.channels.enums.ThankYouPageModuleTypeDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelCart;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelCheckout;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelCheckoutAttendees;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelPromotions;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelResendTickets;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelReviews;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelThankYouPage;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelThankYouPageModule;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelVenuemap;

import java.util.List;
import java.util.stream.Collectors;

public class WhitelabelSettingsConverter {

    private WhitelabelSettingsConverter() {
    }

    public static ChannelWhitelabelSettingsDTO fromMs(ChannelWhitelabelSettings in) {
        if (in == null) return null;
        ChannelWhitelabelSettingsDTO out = new ChannelWhitelabelSettingsDTO();
        out.setPromotions(WhitelabelSettingsConverter.fromMs(in.getPromotions()));
        out.setVenueMap(WhitelabelSettingsConverter.fromMs(in.getVenueMap()));
        out.setThankYouPage(WhitelabelSettingsConverter.fromMs(in.getThankYouPage()));
        out.setCart(WhitelabelSettingsConverter.fromMs(in.getCart()));
        out.setReviews(WhitelabelSettingsConverter.fromMs(in.getReviews()));
        out.setCheckout(WhitelabelSettingsConverter.fromMs(in.getCheckout()));
        out.setResendTickets(WhitelabelSettingsConverter.fromMs(in.getResendTickets()));
        return out;
    }

    private static ChannelWhitelabelThankYouPageDTO fromMs(ChannelWhitelabelThankYouPage in) {
        if (in == null) return null;
        ChannelWhitelabelThankYouPageDTO out = new ChannelWhitelabelThankYouPageDTO();
        out.setShowPurchaseConditions(in.getShowPurchaseConditions());
        out.setModules(fromMs(in.getModules()));
        return out;
    }

    private static List<ChannelWhitelabelThankYouPageModuleDTO> fromMs(List<ChannelWhitelabelThankYouPageModule> in) {
        if (in == null) return null;
        return in.stream().map(WhitelabelSettingsConverter::fromMs).collect(Collectors.toList());
    }

    private static ChannelWhitelabelThankYouPageModuleDTO fromMs(ChannelWhitelabelThankYouPageModule in) {
        if (in == null) return null;
        ChannelWhitelabelThankYouPageModuleDTO out = new ChannelWhitelabelThankYouPageModuleDTO();
        out.setType(ThankYouPageModuleTypeDTO.valueOf(in.getType().name()));
        out.setTextBlockId(in.getTextBlockId());
        out.setEnabled(in.getEnabled());
        out.setVisible(in.getVisible());
        return out;
    }

    private static ChannelWhitelabelVenuemapDTO fromMs(ChannelWhitelabelVenuemap in) {
        if (in == null) return null;
        ChannelWhitelabelVenuemapDTO out = new ChannelWhitelabelVenuemapDTO();
        out.setNavigationMode(in.getNavigationMode());
        out.setShowAvailableTickets(in.getShowAvailableTickets());
        out.setAllowPriceRangeTickets(in.getAllowPriceRangeTickets());
        out.setShowImagesCarousel(in.getShowImagesCarousel());
        out.setEnabledAutomaticSelection(in.getEnabledAutomaticSelection());
        out.setPreselectedItems(in.getPreselectedItems());
        out.setShowCompactedViewList(in.getShowCompactedViewList());
        out.setForceSidePanel(in.getForceSidePanel());
        return out;
    }

    private static ChannelWhitelabelPromotionsDTO fromMs(ChannelWhitelabelPromotions in) {
        if (in == null) return null;
        ChannelWhitelabelPromotionsDTO out = new ChannelWhitelabelPromotionsDTO();
        out.setLocations(in.getLocations());
        out.setApplicationConfig(in.getApplicationConfig());
        return out;
    }

    private static ChannelWhitelabelCartDTO fromMs(ChannelWhitelabelCart in) {
        if (in == null) return null;
        ChannelWhitelabelCartDTO out = new ChannelWhitelabelCartDTO();
        out.setAllowKeepBuying(in.getAllowKeepBuying());
        return out;
    }

    private static ChannelWhitelabelReviewsDTO fromMs(ChannelWhitelabelReviews in) {
        if (in == null) return null;
        ChannelWhitelabelReviewsDTO out = new ChannelWhitelabelReviewsDTO();
        out.setEnabled(in.getEnabled());
        return out;
    }

    private static ChannelWhitelabelCheckoutDTO fromMs(ChannelWhitelabelCheckout in) {
        if (in == null) return null;
        ChannelWhitelabelCheckoutDTO out = new ChannelWhitelabelCheckoutDTO();
        out.setCheckoutFlow(in.getCheckoutFlow());
        out.setAttendees(WhitelabelSettingsConverter.fromMs(in.getAttendees()));
        return out;
    }

    private static ChannelWhitelabelCheckoutAttendeesDTO fromMs(ChannelWhitelabelCheckoutAttendees in) {
        if (in == null) return null;
        ChannelWhitelabelCheckoutAttendeesDTO out = new ChannelWhitelabelCheckoutAttendeesDTO();
        out.setAutofill(in.getAutofill());
        return out;
    }

    public static ChannelWhitelabelSettings toMs(ChannelWhitelabelSettingsDTO in) {
        if (in == null) return null;
        ChannelWhitelabelSettings out = new ChannelWhitelabelSettings();
        out.setPromotions(WhitelabelSettingsConverter.toMs(in.getPromotions()));
        out.setVenueMap(WhitelabelSettingsConverter.toMs(in.getVenueMap()));
        out.setCart(WhitelabelSettingsConverter.toMs(in.getCart()));
        out.setThankYouPage(WhitelabelSettingsConverter.toMs(in.getThankYouPage()));
        out.setReviews(WhitelabelSettingsConverter.toMs(in.getReviews()));
        out.setCheckout(WhitelabelSettingsConverter.toMs(in.getCheckout()));
        out.setResendTickets(WhitelabelSettingsConverter.toMs(in.getResendTickets()));
        return out;
    }

    private static ChannelWhitelabelThankYouPage toMs(ChannelWhitelabelThankYouPageDTO in) {
        if (in == null) return null;
        ChannelWhitelabelThankYouPage out = new ChannelWhitelabelThankYouPage();
        out.setShowPurchaseConditions(in.getShowPurchaseConditions());
        out.setModules(toMs(in.getModules()));
        return out;
    }

    private static List<ChannelWhitelabelThankYouPageModule> toMs(List<ChannelWhitelabelThankYouPageModuleDTO> in) {
        if (in == null) return null;
        return in.stream().map(WhitelabelSettingsConverter::toMs).collect(Collectors.toList());
    }

    private static ChannelWhitelabelThankYouPageModule toMs(ChannelWhitelabelThankYouPageModuleDTO in) {
        if (in == null) return null;
        ChannelWhitelabelThankYouPageModule out = new ChannelWhitelabelThankYouPageModule();
        out.setTextBlockId(in.getTextBlockId());
        out.setEnabled(in.getEnabled());
        out.setVisible(in.getVisible());
        return out;
    }

    private static ChannelWhitelabelVenuemap toMs(ChannelWhitelabelVenuemapDTO in) {
        if (in == null) return null;
        ChannelWhitelabelVenuemap out = new ChannelWhitelabelVenuemap();
        out.setNavigationMode(in.getNavigationMode());
        out.setShowAvailableTickets(in.getShowAvailableTickets());
        out.setAllowPriceRangeTickets(in.getAllowPriceRangeTickets());
        out.setShowImagesCarousel(in.getShowImagesCarousel());
        out.setEnabledAutomaticSelection(in.getEnabledAutomaticSelection());
        out.setPreselectedItems(in.getPreselectedItems());
        out.setShowCompactedViewList(in.getShowCompactedViewList());
        out.setForceSidePanel(in.getForceSidePanel());
        return out;
    }

    private static ChannelWhitelabelPromotions toMs(ChannelWhitelabelPromotionsDTO in) {
        if (in == null) return null;
        ChannelWhitelabelPromotions out = new ChannelWhitelabelPromotions();
        out.setLocations(in.getLocations());
        out.setApplicationConfig(in.getApplicationConfig());
        return out;
    }

    private static ChannelWhitelabelCart toMs(ChannelWhitelabelCartDTO in) {
        if (in == null) return null;
        ChannelWhitelabelCart out = new ChannelWhitelabelCart();
        out.setAllowKeepBuying(in.getAllowKeepBuying());
        return out;
    }

    private static ChannelWhitelabelReviews toMs(ChannelWhitelabelReviewsDTO in) {
        if (in == null) return null;
        ChannelWhitelabelReviews out = new ChannelWhitelabelReviews();
        out.setEnabled(in.getEnabled());
        return out;
    }

    private static ChannelWhitelabelCheckout toMs(ChannelWhitelabelCheckoutDTO in) {
        if (in == null) return null;
        ChannelWhitelabelCheckout out = new ChannelWhitelabelCheckout();
        out.setCheckoutFlow(in.getCheckoutFlow());
        out.setAttendees(WhitelabelSettingsConverter.toMs(in.getAttendees()));
        return out;
    }

    private static ChannelWhitelabelCheckoutAttendees toMs(ChannelWhitelabelCheckoutAttendeesDTO in) {
        if (in == null) return null;
        ChannelWhitelabelCheckoutAttendees out = new ChannelWhitelabelCheckoutAttendees();
        out.setAutofill(in.getAutofill());
        return out;
    }

    private static ChannelWhitelabelResendTicketsDTO fromMs(ChannelWhitelabelResendTickets in) {
        if (in == null) return null;
        ChannelWhitelabelResendTicketsDTO out = new ChannelWhitelabelResendTicketsDTO();
        out.setEnabled(in.getEnabled());
        return out;
    }


    private static ChannelWhitelabelResendTickets toMs(ChannelWhitelabelResendTicketsDTO in) {
        if (in == null) return null;
        ChannelWhitelabelResendTickets out = new ChannelWhitelabelResendTickets();
        out.setEnabled(in.getEnabled());
        return out;
    }

}

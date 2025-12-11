package es.onebox.mgmt.common.channelcontents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.packs.enums.PackContentImageType;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentImageListDTO;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentImageType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChannelContentsUtils {

    private static final int VALID_HEADER_WIDTH = 1076;
    private static final int VALID_HEADER_HEIGHT = 56;
    private static final int MAX_HEADER_SIZE = 25600;

    private static final int VALID_BODY_WIDTH = 360;
    private static final int VALID_BODY_HEIGHT = 430;
    private static final int MAX_BODY_SIZE = 51200;

    private static final int VALID_EVENT_LOGO_WIDTH = 636;
    private static final int VALID_EVENT_LOGO_HEIGHT = 430;
    private static final int MAX_EVENT_LOGO_SIZE = 38400;

    private static final int VALID_BANNER_MAIN_WIDTH = 520;
    private static final int VALID_BANNER_MAIN_HEIGHT = 856;
    private static final int MAX_BANNER_MAIN_SIZE = 87040;

    private static final int VALID_BANNER_ALT_WIDTH = 520;
    private static final int VALID_BANNER_ALT_HEIGHT = 420;
    private static final int MAX_BANNER_ALT_SIZE = 61440;

    private static final int VALID_PRINTER_MAIN_WIDTH = 624;
    private static final int VALID_PRINTER_MAIN_HEIGHT = 696;

    private static final int VALID_PRINTER_BANNER_MAIN_WIDTH = 456;
    private static final int VALID_PRINTER_BANNER_MAIN_HEIGHT = 112;

    private ChannelContentsUtils() {
        throw new UnsupportedOperationException();
    }

    public static <L extends ChannelContentImageListDTO<E>, E extends Enum<E>> void validateEventContents(L contents, boolean landscape) {
        for (ChannelContentImageDTO<E> image : contents.getImages()) {
            String imgBinary = image.getImageBinary();
            E imageType = image.getType();
            if (imgBinary != null) {
                if (imageType instanceof EventChannelContentImageType) {
                    validateEventChannelImage(landscape, image, imgBinary, imageType);
                } else if (imageType instanceof SessionChannelContentImageType) {
                    FileUtils.checkImage(imgBinary, (SessionChannelContentImageType) imageType, imageType.toString());
                } else if (imageType instanceof TourChannelContentImageType) {
                    FileUtils.checkImage(imgBinary, (TourChannelContentImageType) imageType, imageType.toString());
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Image field is mandatory", null);
            }
        }
    }

    public static <L extends ChannelContentImageListDTO<E>, E extends Enum<E>> void validatePackContents(L contents, boolean landscape) {
        for (ChannelContentImageDTO<E> image : contents.getImages()) {
            String imgBinary = image.getImageBinary();
            E imageType = image.getType();
            if (imgBinary != null) {
                if (imageType instanceof PackContentImageType) {
                    validatePackChannelImage(landscape, image, imgBinary, imageType);
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Image field is mandatory", null);
            }
        }
    }

    public static void validateTicketTemplateContents(ChannelContentImageListDTO<TicketTemplateContentImageType> contents, TicketTemplateFormat format) {
        for (ChannelContentImageDTO<TicketTemplateContentImageType> imageContent : contents.getImages()) {
            String imgBinary = imageContent.getImageBinary();
            TicketTemplateContentImageType imageType = imageContent.getType();
            if (imgBinary != null) {
                validateTicketTemplateContents(imageType, format, imgBinary);
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Image field is mandatory", null);
            }
        }
    }

    public static void validateVoucherContents(VoucherGiftCardContentImageListDTO contents) {
        for (ChannelContentImageDTO<VoucherGiftCardContentImageType> image : contents.getImages()) {
            final String imgBinary = image.getImageBinary();
            if (imgBinary != null) {
                FileUtils.checkImage(image.getImageBinary(), image.getType(), image.getType().name());
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Image field is mandatory", null);
            }
        }
    }

    public static void validatePosition(Integer position) {
        if (position == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Position field is mandatory for LANDSCAPE type", null);
        }
    }

    public static String checkElementLanguageForEvent(Event event, Map<String, Long> languages, String language) {
        return checkElementLanguage(event.getLanguages(), languages, language);
    }

    public static String checkElementLanguageForChannel(ChannelResponse channelResponse, Map<Long, String> languagesByIds, String language) {
        String locale = ConverterUtils.checkLanguageByIds(language, languagesByIds);
        if (channelResponse.getLanguages().getSelectedLanguages().stream().noneMatch(l -> languagesByIds.get(l).equals(locale))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static String checkElementLanguageForSeasonTicket(SeasonTicket seasonTicket, Map<String, Long> languages, String language) {
        return checkElementLanguage(seasonTicket.getLanguages(), languages, language);
    }

    public static String checkElementLanguage(List<EventLanguage> eventLanguageList, Map<String, Long> languages, String language) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        if (eventLanguageList.stream().noneMatch(l -> l.getCode().equals(locale))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static String checkElementLanguageProduct(ProductLanguages productLanguageList, Map<String, Long> languages, String language) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        if (productLanguageList.stream().noneMatch(l -> l.getCode().equals(locale))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static String checkElementLanguageForTicketTemplate(TicketTemplate template, Map<String, Long> languages, String language) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        if (template.getSelectedLanguageIds().stream().noneMatch(l -> l.equals(languages.get(locale)))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static String checkElementLanguageForProductTicketTemplate(ProductTicketTemplateDTO template, Map<String, Long> languages, String language) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        if (template.selectedLanguageIds().stream().noneMatch(l -> l.equals(languages.get(locale)))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static String checkElementLanguageForEntity(Entity entity, Map<String, Long> languages, String language) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        Long langId = languages.get(locale);
        if (entity.getSelectedLanguages().stream().noneMatch(l -> l.getId().equals(langId))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    public static void addEventTagsToFilter(CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> condition) {
        filter = checkFilterTags(filter);
        if (filter.getTags().isEmpty()) {
            filter.getTags().addAll(Stream.of(EventTagType.values()).filter(condition).collect(Collectors.toSet()));
        }
    }

    public static void addPackTagsToFilter(CommunicationElementFilter<PackTagType> filter, Predicate<PackTagType> condition) {
        filter = checkFilterTags(filter);
        if (filter.getTags().isEmpty()) {
            filter.getTags().addAll(Stream.of(PackTagType.values()).filter(condition).collect(Collectors.toSet()));
        }
    }

    public static void addTicketTemplateTagsToFilter(CommunicationElementFilter<TicketTemplateTagType> filter,
                                                     Predicate<TicketTemplateTagType> condition) {
        filter = checkFilterTags(filter);
        if (filter.getTags().isEmpty()) {
            filter.getTags().addAll(Stream.of(TicketTemplateTagType.values()).filter(condition).collect(Collectors.toSet()));
        }
    }

    public static void addPromotionTagsToFilter(CommunicationElementFilter<PromotionTagType> filter) {
        filter = checkFilterTags(filter);
        if (filter.getTags().isEmpty()) {
            filter.getTags().addAll(Stream.of(PromotionTagType.values()).collect(Collectors.toSet()));
        }
    }

    private static <T extends Serializable> void validateEventChannelImage(boolean landscape, ChannelContentImageDTO<T> imageContent, String imgBinary, T imageType) {
        Integer landscapePosition = landscape ? imageContent.getPosition() : null;
        EventChannelContentImageType tag = (EventChannelContentImageType) imageType;
        if (EventChannelContentImageType.LANDSCAPE.equals(tag)) {
            validatePosition(landscapePosition);
        }
        FileUtils.checkImage(imgBinary, tag, tag.toString());
    }

    private static <T extends Serializable> void validatePackChannelImage(boolean landscape, ChannelContentImageDTO<T> imageContent, String imgBinary, T imageType) {
        Integer landscapePosition = landscape ? imageContent.getPosition() : null;
        PackContentImageType tag = (PackContentImageType) imageType;
        if (PackContentImageType.LANDSCAPE.equals(tag)) {
            validatePosition(landscapePosition);
        }
        FileUtils.checkImage(imgBinary, tag, tag.toString());
    }

    private static <E extends Enum<E>> CommunicationElementFilter<E> checkFilterTags(CommunicationElementFilter<E> filter) {
        if (filter == null) {
            filter = new CommunicationElementFilter<>();
        }
        if (filter.getTags() == null) {
            filter.setTags(new HashSet<>());
        }
        return filter;
    }

    private static void validateTicketTemplateContents(TicketTemplateContentImageType imageType, TicketTemplateFormat format, String imgBinary) {
        switch (imageType) {
            case HEADER:
                FileUtils.checkImage(imgBinary, VALID_HEADER_WIDTH, VALID_HEADER_HEIGHT, MAX_HEADER_SIZE, TicketTemplateContentImageType.HEADER.toString());
                break;
            case BODY:
                FileUtils.checkImage(imgBinary, VALID_BODY_WIDTH, VALID_BODY_HEIGHT, MAX_BODY_SIZE, TicketTemplateContentImageType.BODY.toString());
                break;
            case EVENT_LOGO:
                FileUtils.checkImage(imgBinary, VALID_EVENT_LOGO_WIDTH, VALID_EVENT_LOGO_HEIGHT, MAX_EVENT_LOGO_SIZE, TicketTemplateContentImageType.EVENT_LOGO.toString());
                break;
            case BANNER_MAIN:
                if (TicketTemplateFormat.PDF.equals(format)) {
                    FileUtils.checkImage(imgBinary, VALID_BANNER_MAIN_WIDTH, VALID_BANNER_MAIN_HEIGHT, MAX_BANNER_MAIN_SIZE, TicketTemplateContentImageType.BANNER_MAIN.toString());
                } else if (TicketTemplateFormat.PRINTER.equals(format)) {
                    FileUtils.checkImage(imgBinary, VALID_PRINTER_BANNER_MAIN_WIDTH, VALID_PRINTER_BANNER_MAIN_HEIGHT, TicketTemplateContentImageType.BANNER_MAIN.toString());
                }
                break;
            case BANNER_SECONDARY:
                FileUtils.checkImage(imgBinary, VALID_BANNER_ALT_WIDTH, VALID_BANNER_ALT_HEIGHT, MAX_BANNER_ALT_SIZE, TicketTemplateContentImageType.BANNER_SECONDARY.toString());
                break;
            case BANNER_CHANNEL_LOGO:
                FileUtils.checkImage(imgBinary, VALID_BANNER_ALT_WIDTH, VALID_BANNER_ALT_HEIGHT, MAX_BANNER_ALT_SIZE, TicketTemplateContentImageType.BANNER_CHANNEL_LOGO.toString());
                break;
            case MAIN:
                FileUtils.checkImage(imgBinary, VALID_PRINTER_MAIN_WIDTH, VALID_PRINTER_MAIN_HEIGHT, TicketTemplateContentImageType.MAIN.toString());
                break;
            default:
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid image type", null);
        }
    }
}

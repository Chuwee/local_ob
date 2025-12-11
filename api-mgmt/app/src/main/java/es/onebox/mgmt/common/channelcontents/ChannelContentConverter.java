package es.onebox.mgmt.common.channelcontents;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextFilter;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.common.enums.EmailCommunicationElementTagType;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChannelEventImageConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EmailCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.session.EventCommunicationElementBulk;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.dto.channel.EventImageConfigDTO;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
import es.onebox.mgmt.events.enums.ImageOrigin;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.enums.PackContentImageType;
import es.onebox.mgmt.packs.enums.PackContentTextType;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.sessions.dto.SessionChannelContentImageListDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.toLocale;

public class ChannelContentConverter {

    private static final String INVALID_CHANNEL_CONTENT_TYPE = "Invalid channel content type";

    private ChannelContentConverter() {
    }

    public static ChannelContentTextListDTO<EventChannelContentTextType> fromMsEventText(List<EventCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<EventChannelContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(EventChannelContentTextType.getById(e.getTagId()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentTextListDTO<SessionChannelContentTextType> fromMsEventSessionText(List<EventCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<SessionChannelContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(SessionChannelContentTextType.getById(e.getTagId()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentTextListDTO<PackContentTextType> fromMsChannelText(List<PackCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<PackContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(PackContentTextType.getById(e.getTagId()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentTextListDTO<PromotionChannelContentTextType> fromMsPromotionText(List<BaseCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<PromotionChannelContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(PromotionChannelContentTextType.valueOf(e.getTag()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static <T extends BaseCommunicationElement> ChannelContentImageListDTO<EventChannelContentImageType> fromMsEventImage(List<T> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<EventChannelContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setImageUrl(element.getValue());
                    if (element instanceof EventCommunicationElement) {
                        EventCommunicationElement el = (EventCommunicationElement) element;
                        dto.setType(EventChannelContentImageType.getById(el.getTagId()));
                        dto.setPosition(el.getPosition());
                        dto.setAltText(el.getAltText());
                    } else if (element instanceof EmailCommunicationElement) {
                        dto.setType(EventChannelContentImageType.PROMOTER_BANNER);
                    }
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static <T extends BaseCommunicationElement> ChannelContentImageListDTO<PackContentImageType> fromMsChannelImage(List<T> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<PackContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setImageUrl(element.getValue());
                    if (element instanceof PackCommunicationElement) {
                        PackCommunicationElement el = (PackCommunicationElement) element;
                        dto.setType(PackContentImageType.getById(el.getTagId()));
                        dto.setPosition(el.getPosition());
                        dto.setAltText(el.getAltText());
                    }
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentImageListDTO<SessionChannelContentImageType> fromMsEventSessionImage(List<EventCommunicationElement> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<SessionChannelContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setImageUrl(element.getValue());
                    dto.setType(SessionChannelContentImageType.getById(element.getTagId()));
                    dto.setPosition(element.getPosition());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentImageListDTO<TourChannelContentImageType> fromMsTourImage(List<EventCommunicationElement> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<TourChannelContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setType(TourChannelContentImageType.getById(element.getTagId()));
                    dto.setImageUrl(element.getValue());
                    dto.setPosition(element.getPosition());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static ChannelContentImageListDTO<TicketTemplateContentImageType> fromMsTicketTemplateImage(
            List<TicketTemplateCommunicationElement> elements, TicketTemplateFormat templateFormat) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<TicketTemplateContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setType(mapFromTicketTagType(element.getTagType(), templateFormat));
                    dto.setImageUrl(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static <T extends BaseChannelContentTextType> List<EventCommunicationElement> toMsEventText(List<ChannelContentTextDTO<T>> elements) {
        return elements.stream()
                .map(element -> {
                    EventCommunicationElement dto = new EventCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTagId(element.getType().getTagId());
                    dto.setValue(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static <T extends BaseChannelContentTextType> List<PackCommunicationElement> toMsChannelText(List<ChannelContentTextDTO<T>> elements) {
        return elements.stream()
                .map(element -> {
                    PackCommunicationElement dto = new PackCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTagId(element.getType().getTagId());
                    dto.setValue(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static <T extends BaseChannelContentTextType> EventCommunicationElementBulk toMsEventText(List<Long> ids, List<ChannelContentTextDTO<T>> elements) {
        EventCommunicationElementBulk out = new EventCommunicationElementBulk();
        out.setIds(ids);
        out.setValues(ChannelContentConverter.toMsEventText(elements));
        return out;
    }

    public static List<BaseCommunicationElement> toMsPromotionText(List<ChannelContentTextDTO<PromotionChannelContentTextType>> elements) {
        return elements.stream()
                .map(element -> {
                    BaseCommunicationElement dto = new BaseCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTag(element.getType().name());
                    dto.setValue(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static <T extends BaseChannelContentImageType> List<EventCommunicationElement> toMsEventImageList(List<ChannelContentImageDTO<T>> elements) {
        return elements.stream()
                .map(element -> {
                    EventCommunicationElement dto = new EventCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTagId(element.getType().getTagId());
                    dto.setImageBinary(Optional.of(element.getImageBinary()));
                    dto.setPosition(element.getPosition());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static <T extends BaseChannelContentImageType> List<PackCommunicationElement> toMsChannelImageList(List<ChannelContentImageDTO<T>> elements) {
        return elements.stream()
                .map(element -> {
                    PackCommunicationElement dto = new PackCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTagId(element.getType().getTagId());
                    dto.setImageBinary(Optional.of(element.getImageBinary()));
                    dto.setPosition(element.getPosition());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static List<EventCommunicationElement> toMsEventImageList(
            SessionChannelContentImageListDTO elements) {
        return elements.stream()
                .map(element -> {
                    EventCommunicationElement dto = new EventCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTagId(element.getType().getTagId());
                    dto.setImageBinary(Optional.of(element.getImageBinary()));
                    dto.setPosition(element.getPosition());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static EventCommunicationElementBulk toMsEventImageList(List<Long> ids,
                                                                   SessionChannelContentImageListDTO elements) {
        EventCommunicationElementBulk out = new EventCommunicationElementBulk();
        out.setIds(ids);
        out.setValues(ChannelContentConverter.toMsEventImageList(elements));
        return out;
    }

    public static <T extends BaseChannelContentImageType> List<EmailCommunicationElement> toMsEventEmailImageList(List<ChannelContentImageDTO<T>> elements) {
        return elements.stream()
                .map(element -> {
                    EmailCommunicationElement dto = new EmailCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTag(EmailCommunicationElementTagType.PROMOTER_BANNER.toString());
                    dto.setImageBinary(Optional.of(element.getImageBinary()));
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static CommunicationElementFilter<EventTagType> fromEventFilter(ChannelContentTextFilter<?> original, MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<EventTagType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() instanceof BaseChannelContentTextType) {
            commFilter.setTags(mapToTagTextType((BaseChannelContentTextType) original.getType()));
        }
        return commFilter;
    }

    public static CommunicationElementFilter<PackTagType> fromPackFilter(ChannelContentTextFilter<?> original, MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<PackTagType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            String langCode = toLocale(original.getLanguage());
            commFilter.setLanguage(langCode);
            Integer languageId = masterdataService.getLanguageByCode(langCode);
            if (languageId != null) {
                commFilter.setLanguageId(languageId);
            }
        }
        if (original.getType() instanceof PackContentTextType) {
            commFilter.setTags(mapToTagTextType((PackContentTextType) original.getType()));
        }
        return commFilter;
    }

    public static CommunicationElementFilter<PromotionTagType> fromPromotionFilter(PromotionChannelContentTextFilter original,
                                                                                   MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<PromotionTagType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(new HashSet<>(Collections.singletonList(PromotionTagType.valueOf(original.getType().name()))));
        }
        return commFilter;
    }

    public static <T extends BaseChannelContentImageType> CommunicationElementFilter<EventTagType> fromEventFilter(ChannelContentImageFilter<T> original,
                                                                                                                   MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<EventTagType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(mapToEventTagImageType(original.getType()));
        }
        return commFilter;
    }

    public static <T extends BaseChannelContentImageType> CommunicationElementFilter<PackTagType> fromPackFilter(ChannelContentImageFilter<T> original,
                                                                                                                 MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<PackTagType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(mapToPackTagImageType((PackContentImageType) original.getType()));
        }
        return commFilter;
    }

    public static EventCommunicationElement buildEventCommunicationElementToDelete(String language, BaseChannelContentImageType type,
                                                                                   Integer position, Map<String, Long> languages) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        EventCommunicationElement dto = new EventCommunicationElement();
        dto.setTagId(type.getTagId());
        dto.setLanguage(locale);
        dto.setImageBinary(Optional.empty());
        dto.setPosition(position);
        return dto;
    }

    public static PackCommunicationElement buildPackCommunicationElementToDelete(String language, BaseChannelContentImageType type,
                                                                                 Integer position, Map<String, Long> languages) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        PackCommunicationElement dto = new PackCommunicationElement();
        dto.setTagId(type.getTagId());
        dto.setLanguage(locale);
        dto.setImageBinary(Optional.empty());
        dto.setPosition(position);
        return dto;
    }

    public static EventCommunicationElementBulk buildEventCommunicationElementBulkToDelete(String language, BaseChannelContentImageType type,
                                                                                           Integer position, Map<String, Long> languages,
                                                                                           List<Long> sessionIds) {
        EventCommunicationElementBulk out = new EventCommunicationElementBulk();
        out.setIds(sessionIds);
        EventCommunicationElement element =
                ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages);
        out.setValues(Collections.singletonList(element));
        return out;
    }

    public static <T extends BaseCommunicationElement> ChannelContentImageListDTO<ChannelEventContentImageType> fromMsChannelEventImage(List<T> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<ChannelEventContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setImageUrl(element.getValue());
                    if (element instanceof EventCommunicationElement) {
                        EventCommunicationElement el = (EventCommunicationElement) element;
                        dto.setType(ChannelEventContentImageType.getById(el.getTagId()));
                        dto.setPosition(el.getPosition());
                    }
                    return dto;
                })
                .collect(Collectors.toList()));
    }


    private static Set<EventTagType> mapToTagTextType(BaseChannelContentTextType textType) {
        if (textType instanceof EventChannelContentTextType) {
            return mapToTagTextType((EventChannelContentTextType) textType);
        } else if (textType instanceof SessionChannelContentTextType) {
            return mapToTagTextType((SessionChannelContentTextType) textType);
        }
        return Collections.emptySet();
    }


    private static Set<EventTagType> mapToTagTextType(EventChannelContentTextType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case TITLE:
                tagTypes.add(EventTagType.TEXT_TITLE_WEB);
                break;
            case SUBTITLE:
                tagTypes.add(EventTagType.TEXT_SUBTITLE_WEB);
                break;
            case SHORT_DESCRIPTION:
                tagTypes.add(EventTagType.TEXT_SUMMARY_WEB);
                break;
            case LONG_DESCRIPTION:
                tagTypes.add(EventTagType.TEXT_BODY_WEB);
                break;
            case LENGTH_AND_LANGUAGE:
                tagTypes.add(EventTagType.TEXT_LENGTH_WEB);
                break;
            case LOCATION:
                tagTypes.add(EventTagType.TEXT_LOCATION_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<EventTagType> mapToTagTextType(SessionChannelContentTextType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case TITLE:
                tagTypes.add(EventTagType.TEXT_TITLE_WEB);
                break;
            case DESCRIPTION:
                tagTypes.add(EventTagType.TEXT_SUMMARY_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<PackTagType> mapToTagTextType(PackContentTextType type) {
        Set<PackTagType> tagTypes = new HashSet<>();
        switch (type) {
            case TITLE:
                tagTypes.add(PackTagType.TEXT_TITLE_WEB);
                break;
            case SUBTITLE:
                tagTypes.add(PackTagType.TEXT_SUBTITLE_WEB);
                break;
            case SHORT_DESCRIPTION:
                tagTypes.add(PackTagType.TEXT_SUMMARY_WEB);
                break;
            case LONG_DESCRIPTION:
                tagTypes.add(PackTagType.TEXT_BODY_WEB);
                break;
            case LENGTH_AND_LANGUAGE:
                tagTypes.add(PackTagType.TEXT_LENGTH_WEB);
                break;
            case LOCATION:
                tagTypes.add(PackTagType.TEXT_LOCATION_WEB);
                break;
            case INFORMATIVE_MESSAGE:
                tagTypes.add(PackTagType.TEXT_INFORMATION_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }


    private static Set<EventTagType> mapToEventTagImageType(BaseChannelContentImageType type) {
        if (type instanceof EventChannelContentImageType) {
            return mapToEventTagImageType((EventChannelContentImageType) type);
        } else if (type instanceof TourChannelContentImageType) {
            return mapToEventTagImageType((TourChannelContentImageType) type);
        } else if (type instanceof SessionChannelContentImageType) {
            return mapToEventTagImageType((SessionChannelContentImageType) type);
        } else if (type instanceof ChannelEventContentImageType) {
            return mapToEventTagImageType((ChannelEventContentImageType) type);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static Set<EventTagType> mapToEventTagImageType(EventChannelContentImageType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case MAIN:
                tagTypes.add(EventTagType.IMG_BODY_WEB);
                break;
            case SECONDARY:
                tagTypes.add(EventTagType.LOGO_WEB);
                break;
            case LANDSCAPE:
                tagTypes.add(EventTagType.IMG_BANNER_WEB);
                break;
            case CARD:
                tagTypes.add(EventTagType.IMG_CARD_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<PackTagType> mapToPackTagImageType(PackContentImageType type) {
        Set<PackTagType> tagTypes = new HashSet<>();
        switch (type) {
            case MAIN -> tagTypes.add(PackTagType.IMG_BODY_WEB);
            case SECONDARY -> tagTypes.add(PackTagType.LOGO_WEB);
            case LANDSCAPE -> tagTypes.add(PackTagType.IMG_BANNER_WEB);
            case CARD -> tagTypes.add(PackTagType.IMG_CARD_WEB);
            default -> throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<EventTagType> mapToEventTagImageType(TourChannelContentImageType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case MAIN:
                tagTypes.add(EventTagType.IMG_BODY_WEB);
                break;
            case SECONDARY:
                tagTypes.add(EventTagType.LOGO_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<EventTagType> mapToEventTagImageType(SessionChannelContentImageType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case MAIN:
                tagTypes.add(EventTagType.LOGO_WEB);
                break;
            case LANDSCAPE:
                tagTypes.add(EventTagType.IMG_BANNER_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static Set<EventTagType> mapToEventTagImageType(ChannelEventContentImageType type) {
        Set<EventTagType> tagTypes = new HashSet<>();
        switch (type) {
            case SQUARE_LANDSCAPE:
                tagTypes.add(EventTagType.IMG_SQUARE_BANNER_WEB);
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return tagTypes;
    }

    private static TicketTemplateContentImageType mapFromTicketTagType(TicketTemplateTagType tagType,
                                                                       TicketTemplateFormat templateFormat) {
        TicketTemplateContentImageType type;
        switch (tagType) {
            case HEADER:
                type = TicketTemplateContentImageType.HEADER;
                break;
            case BODY:
                type = TicketTemplateFormat.PDF.equals(templateFormat) ?
                        TicketTemplateContentImageType.BODY : TicketTemplateContentImageType.MAIN;
                break;
            case EVENT_LOGO:
                type = TicketTemplateContentImageType.EVENT_LOGO;
                break;
            case BANNER_MAIN:
                type = TicketTemplateContentImageType.BANNER_MAIN;
                break;
            case BANNER_SECONDARY:
                type = TicketTemplateFormat.PDF.equals(templateFormat) ?
                        TicketTemplateContentImageType.BANNER_SECONDARY : TicketTemplateContentImageType.BANNER_MAIN;
                break;
            case BANNER_CHANNEL_LOGO:
                type = TicketTemplateContentImageType.BANNER_CHANNEL_LOGO;
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_CHANNEL_CONTENT_TYPE, null);
        }
        return type;
    }

    public static List<EventImageConfigDTO> fromMSChannelEventImageConfigList(List<ChannelEventImageConfigDTO> channelEventImageConfig) {
        if (CollectionUtils.isNotEmpty(channelEventImageConfig)) {
            return channelEventImageConfig.stream().map(config -> {
                EventImageConfigDTO eventImageConfigDTO = new EventImageConfigDTO();
                eventImageConfigDTO.setSessionId(config.getSessionId());
                eventImageConfigDTO.setImageOrigin(ImageOrigin.valueOf(config.getImageOrigin().name()));
                return eventImageConfigDTO;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

package es.onebox.mgmt.salerequests.communicationcontents.converter;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.UrlBuilder;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.channels.utils.ExternalWhitelabelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTagDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentUrlDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentUrlListDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.ChannelCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.PaymentBenefitCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.PurchaseCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationChannelElementType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationElementType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationPaymentBenefitElementType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationPurchaseElementType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.SessionStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.SessionType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestChannelContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestEventChannelContentPublishedLinksDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestEventChannelContentSessionLinkDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPaymentBenefitContentTagListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentVO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestSessionsLinksResponse;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestChannelTextContent;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPaymentBenefitTagContentType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentRequestType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentResponseType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseTextContent;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseUrlContentType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SaleRequestCommElementsConverter {

    private static final String COMMUNICATION_PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/";
    private static final String SELECT = "select";
    private static final String EVENT = "evento";
    private static final String SESSION = "session";
    private static final String TICKETS = "entradas";

    private SaleRequestCommElementsConverter() {

    }

    public static List<SaleRequestEventChannelContentPublishedLinksDTO> convertToEventChannelContentLinkList(
            String urlChannel, String urlPortal, ChannelResponse channelResponse,
            ChannelLanguagesDTO languages, Event event, Boolean v4Enabled, Boolean externalWhitelabel) {
        return languages.getSelectedLanguageCode().stream()
                .map(l -> convertToEventChannelContentLink(urlChannel, urlPortal, channelResponse,
                        l, event, v4Enabled, externalWhitelabel))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<SaleRequestEventChannelContentSessionLinkDTO> convertToEventChannelContentSessionLinkList(
            MsSessionSaleRequestResponseDTO sessions, String urlPortal, ChannelResponse channelResponse,
            String language, Event event, Boolean v4Enabled, Boolean externalWhitelabel) {
        return convertToEventChannelContentSessionLink(sessions, urlPortal, channelResponse, language,
                event, v4Enabled, externalWhitelabel);
    }

    public static SaleRequestSessionsLinksResponse convertToEventChannelContentSessionLinksResponse(List<SaleRequestEventChannelContentSessionLinkDTO> saleRequestContentSessionLinkDTO, Metadata metadata) {
        if (saleRequestContentSessionLinkDTO == null) {
            return null;
        }
        SaleRequestSessionsLinksResponse dto = new SaleRequestSessionsLinksResponse();
        dto.setData(saleRequestContentSessionLinkDTO);
        dto.setMetadata(metadata);
        return dto;
    }

    public static SaleRequestPurchaseContentVO fromMs(SaleRequestCommunicationElementDTO in, CommunicationElementType type) {
        SaleRequestPurchaseContentVO out = new SaleRequestPurchaseContentVO();
        out.setImages(new ChannelContentImageListDTO<>());
        out.setUrls(new ChannelContentUrlListDTO<>());
        out.setTexts(new SaleRequestPurchaseContentTextListDTO());

        if (in == null || CollectionUtils.isEmpty(in.getCommunicationPurchaseElement())) {
            return out;
        }
        in.getCommunicationPurchaseElement().stream().filter(x -> x.getType().getType().equals(type)).forEach(element -> {
            if (CommunicationElementType.LINK.equals(element.getType().getType())) {
                ChannelContentUrlDTO<SaleRequestPurchaseUrlContentType> dto = new ChannelContentUrlDTO<>();
                dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                dto.setUrl(element.getUrlImage());
                dto.setType(SaleRequestPurchaseUrlContentType.valueOf(element.getType().name()));
                out.getUrls().getLinks().add(dto);
            } else if (CommunicationElementType.IMAGE.equals(element.getType().getType())) {
                ChannelContentImageDTO<SaleRequestPurchaseImageContentResponseType> dto = new ChannelContentImageDTO<>();
                dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                dto.setImageUrl(element.getUrlImage());
                dto.setType(SaleRequestPurchaseImageContentResponseType.valueOf(element.getType().name()));
                dto.setAltText(element.getAltText());
                out.getImages().getImages().add(dto);
            } else {
                ChannelContentTextDTO<SaleRequestPurchaseTextContent> dto = new ChannelContentTextDTO<>();
                dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                dto.setType(SaleRequestPurchaseTextContent.valueOf(element.getType().name()));
                dto.setValue(element.getValue());
                out.getTexts().add(dto);
            }
        });
        return out;
    }

    public static SaleRequestChannelContentTextListDTO fromMsToChannelContentTexts(SaleRequestCommunicationElementDTO response) {
        var out = new SaleRequestChannelContentTextListDTO();
        if (response != null && CollectionUtils.isNotEmpty(response.getCommunicationChannelElement())) {
            response.getCommunicationChannelElement().forEach(el -> {
                var in = new ChannelContentTextDTO<SaleRequestChannelTextContent>();
                in.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                in.setValue(el.getValue());
                in.setType(SaleRequestChannelTextContent.valueOf(el.getType().name()));
                out.add(in);
            });
        }
        return out;
    }

    public static SaleRequestPaymentBenefitContentTagListDTO fromMsToPaymentBenefitTags(SaleRequestCommunicationElementDTO response) {
        SaleRequestPaymentBenefitContentTagListDTO out = new SaleRequestPaymentBenefitContentTagListDTO();

        if (response != null && CollectionUtils.isNotEmpty(response.getCommunicationPaymentBenefitElement())) {
            response.getCommunicationPaymentBenefitElement().forEach(el -> {
                ChannelContentTagDTO<SaleRequestPaymentBenefitTagContentType> in = new ChannelContentTagDTO<>();
                in.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                in.setValue(el.getValue());
                in.setBackgroundColor(el.getBackgroundColor());
                in.setTextColor(el.getTextColor());
                in.setType(SaleRequestPaymentBenefitTagContentType.valueOf(el.getType().name()));
                in.setVisible(el.getVisible());
                out.add(in);
            });
        }

        return out;
    }

    public static SaleRequestCommunicationElementDTO toMS(SaleRequestChannelContentTextListDTO body) {
        SaleRequestCommunicationElementDTO out = new SaleRequestCommunicationElementDTO();
        out.setCommunicationChannelElement(body.stream().map(el -> {
            ChannelCommunicationElementDTO element = new ChannelCommunicationElementDTO();
            element.setLanguage(ConverterUtils.toLocale(el.getLanguage()));
            element.setValue(el.getValue());
            element.setType(CommunicationChannelElementType.valueOf(el.getType().name()));
            return element;
        }).toList());
        return out;
    }

    public static SaleRequestCommunicationElementDTO toMS(SaleRequestPaymentBenefitContentTagListDTO body) {
        SaleRequestCommunicationElementDTO out = new SaleRequestCommunicationElementDTO();

        out.setCommunicationPaymentBenefitElement(body.stream().map(el -> {
            PaymentBenefitCommunicationElementDTO element = new PaymentBenefitCommunicationElementDTO();
            element.setLanguage(ConverterUtils.toLocale(el.getLanguage()));
            element.setValue(el.getValue());
            element.setBackgroundColor(el.getBackgroundColor());
            element.setTextColor(el.getTextColor());
            element.setType(CommunicationPaymentBenefitElementType.valueOf(el.getType().name()));
            element.setVisible(el.getVisible());
            return element;
        }).toList());

        return out;
    }

    public static SaleRequestCommunicationElementDTO convertImagesToMsCommunicationElementResponse(
            ChannelContentImageListDTO<SaleRequestPurchaseImageContentRequestType> elements) {
        SaleRequestCommunicationElementDTO response = new SaleRequestCommunicationElementDTO();
        if (Objects.isNull(elements)) {
            return response;
        }

        response.setCommunicationPurchaseElement(elements.getImages().stream().map(element -> {
            PurchaseCommunicationElementDTO dto = new PurchaseCommunicationElementDTO();
            dto.setLanguage(ConverterUtils.toLocale(element.getLanguage()));
            dto.setUrlImage(element.getImageBinary());
            dto.setType(CommunicationPurchaseElementType.valueOf(element.getType().name()));
            dto.setAltText(element.getAltText());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    public static PurchaseCommunicationElementDTO toDTO(String language, SaleRequestPurchaseImageContentRequestType type) {
        PurchaseCommunicationElementDTO comPurchaseElement = new PurchaseCommunicationElementDTO();
        comPurchaseElement.setType(CommunicationPurchaseElementType.valueOf(type.name()));
        comPurchaseElement.setLanguage(ConverterUtils.toLocale(language));
        comPurchaseElement.setUrlImage(StringUtils.EMPTY);
        return comPurchaseElement;
    }

    public static PaymentBenefitCommunicationElementDTO toDTO(String language, SaleRequestPaymentBenefitTagContentType type) {
        PaymentBenefitCommunicationElementDTO comPurchaseElement = new PaymentBenefitCommunicationElementDTO();
        comPurchaseElement.setType(CommunicationPaymentBenefitElementType.valueOf(type.name()));
        comPurchaseElement.setLanguage(ConverterUtils.toLocale(language));
        comPurchaseElement.setValue(StringUtils.EMPTY);
        comPurchaseElement.setBackgroundColor(StringUtils.EMPTY);
        comPurchaseElement.setTextColor(StringUtils.EMPTY);
        return comPurchaseElement;
    }

    public static SaleRequestCommunicationElementDTO toMS(ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> elements) {
        SaleRequestCommunicationElementDTO response = new SaleRequestCommunicationElementDTO();
        response.setCommunicationPurchaseElement(elements.getLinks().stream().map(element -> {
            PurchaseCommunicationElementDTO dto = new PurchaseCommunicationElementDTO();
            dto.setLanguage(ConverterUtils.toLocale(element.getLanguage()));
            dto.setUrlImage(element.getUrl());
            dto.setType(CommunicationPurchaseElementType.valueOf(element.getType().name()));
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    public static SaleRequestCommunicationElementDTO toMS(SaleRequestPurchaseContentTextListDTO body) {
        SaleRequestCommunicationElementDTO response = new SaleRequestCommunicationElementDTO();
        response.setCommunicationPurchaseElement(body.stream().map(element -> {
            PurchaseCommunicationElementDTO dto = new PurchaseCommunicationElementDTO();
            dto.setLanguage(ConverterUtils.toLocale(element.getLanguage()));
            dto.setValue(element.getValue());
            dto.setType(CommunicationPurchaseElementType.valueOf(element.getType().name()));
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    private static SaleRequestEventChannelContentPublishedLinksDTO convertToEventChannelContentLink(String urlChannel, String urlPortal,
                                                                                                    ChannelResponse channelResponse,
                                                                                                    String language,
                                                                                                    Event event, Boolean v4Enabled,
                                                                                                    Boolean externalWhitelabel) {
        SaleRequestEventChannelContentPublishedLinksDTO result = new SaleRequestEventChannelContentPublishedLinksDTO();
        String finalUrl = ChannelsUrlUtils.selectUrlByChannelConfig(v4Enabled, urlChannel, urlPortal);
        result.setLanguage(language);
        result.setPendingGeneration(Boolean.FALSE);
        if (BooleanUtils.isTrue(externalWhitelabel)) {
            if (BooleanUtils.isTrue(event.getSupraEvent())) {
                return null;
            }
            String link = ExternalWhitelabelUtils.buildEventUrl(channelResponse.getDomain(), channelResponse.getWhitelabelPath(),
                    event.getExternalReference(), language);
            result.setLink(link);
            if (StringUtils.isEmpty(link)) {
                result.setPendingGeneration(Boolean.TRUE);
            }
        } else if (BooleanUtils.isTrue(v4Enabled)) {
            result.setLink(ChannelsUrlUtils.buildUrlByChannels(finalUrl, channelResponse.getUrl(), event.getId(), language, Boolean.TRUE, Boolean.FALSE));
        } else {
            result.setLink(buildEventUrl(finalUrl, channelResponse.getUrl(), language, event.getId()));
        }
        return result;
    }

    private static List<SaleRequestEventChannelContentSessionLinkDTO> convertToEventChannelContentSessionLink(MsSessionSaleRequestResponseDTO sessions,
                                                                                      String urlPortal, ChannelResponse channelResponse, String language, Event event, Boolean v4Enabled, Boolean externalWhitelabel) {
        return sessions.getData().stream().map(s -> SaleRequestCommElementsConverter.convertToEventChannelContentSessionLinkList(s,
                urlPortal, channelResponse, language, event, v4Enabled, externalWhitelabel)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static SaleRequestEventChannelContentSessionLinkDTO convertToEventChannelContentSessionLinkList(MsSessionSaleRequestDTO session,
                                                                                                            String urlPortal, ChannelResponse channel, String language, Event event, Boolean v4Enabled, Boolean externalWhitelabel) {

        SaleRequestEventChannelContentSessionLinkDTO sessionLink = new SaleRequestEventChannelContentSessionLinkDTO();
        sessionLink.setId(session.getId());
        sessionLink.setName(session.getName());
        sessionLink.setStartDate(session.getDate().getStart());
        sessionLink.setPendingGeneration(Boolean.FALSE);
        if (BooleanUtils.isTrue(externalWhitelabel)) {
            String link;
            if (BooleanUtils.isTrue(event.getSupraEvent()) || !SessionType.SESSION.equals(session.getType())) {
                link = ExternalWhitelabelUtils.buildEventUrl(channel.getDomain(), channel.getWhitelabelPath(),
                        session.getExternalReference(), language);
                sessionLink.setLink(link);
            } else {
                link = ExternalWhitelabelUtils.buildSessionUrl(channel.getDomain(), channel.getWhitelabelPath(),
                        event.getExternalReference(), language, session.getDate().getStart(), session.getTimeZone());
                sessionLink.setLink(link);
            }
            if (StringUtils.isBlank(link)) {
                sessionLink.setPendingGeneration(Boolean.TRUE);
            }
        } else if (BooleanUtils.isTrue(v4Enabled)) {
            boolean isPreview = session.getStatus().equals(SessionStatus.PREVIEW);
            sessionLink.setLink(ChannelsUrlUtils.buildUrlByChannels(urlPortal, channel.getUrl(), session.getId(), language, Boolean.FALSE, isPreview));
        } else {
            sessionLink.setLink(buildSessionUrl(urlPortal, channel.getUrl(), language, event.getId(), session.getId()));
        }
        sessionLink.setEnabled(SessionStatus.READY.equals(session.getStatus())
                && session.getDate().getPublication().toInstant().compareTo(Instant.now()) <= 0);
        return sessionLink;
    }

    private static String buildEventUrl(String urlPortal, String urlIntegration, String language, Long eventId) {
        return UrlBuilder.builder().protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, ConverterUtils.toLocale(language), TICKETS, EVENT, eventId)
                .separator(URL_SEPARATOR).build();
    }

    private static String buildSessionUrl(String urlPortal, String urlIntegration, String language, Long eventId, Long sessionId) {
        return UrlBuilder.builder().protocol(COMMUNICATION_PROTOCOL).pathParts(urlPortal, urlIntegration, ConverterUtils.toLocale(language),
                TICKETS, EVENT, eventId, SESSION, sessionId, SELECT).separator(URL_SEPARATOR).build();
    }

}

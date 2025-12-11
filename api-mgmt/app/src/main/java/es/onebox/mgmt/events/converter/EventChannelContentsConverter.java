package es.onebox.mgmt.events.converter;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.UrlBuilder;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.dto.UpdateFavoriteChannelDTO;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.channels.utils.ExternalWhitelabelUtils;
import es.onebox.mgmt.common.BaseLinkDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.HashUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.UpdateFavoriteChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionType;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.events.dto.channel.EventChannelContentLinks;
import es.onebox.mgmt.events.dto.channel.EventChannelContentPublishedLinks;
import es.onebox.mgmt.events.dto.channel.EventChannelContentSessionLink;
import es.onebox.mgmt.events.dto.channel.EventChannelContentSessionLinkQueueIt;
import es.onebox.mgmt.events.dto.channel.EventChannelContentSessionsLinksResponse;
import es.onebox.mgmt.events.dto.channel.SessionLinksFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventChannelContentsConverter {
    private static final String COMMUNICATION_PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/";
    private static final String ENTRADAS = "entradas";
    private static final String EVENTO = "evento";
    private static final String SESSION = "session";
    private static final String SELECT = "select";
    private static final String PREVIEW_TOKEN = "?previewToken=";
    private static final String ATTENDEES = "attendees";

    private EventChannelContentsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventChannelContentSessionsLinksResponse convertToEventChannelContentSessionLinksResponse(EventChannelContentPublishedLinks eventChannelContentSessionLink,
                                                                                                            Metadata metadata) {
        if (eventChannelContentSessionLink == null) {
            return null;
        }

        EventChannelContentSessionsLinksResponse dto = new EventChannelContentSessionsLinksResponse();
        dto.setData(eventChannelContentSessionLink.getSessionsLinks());
        dto.setMetadata(metadata);
        return dto;
    }

    public static List<EventChannelContentLinks> convertToEventChannelContentLinks(String urlChannel, String urlPortal,
                                                                                   ChannelResponse channel,
                                                                                   Event event,
                                                                                   ChannelLanguagesDTO languages,
                                                                                   Boolean v4Enabled,
                                                                                   Boolean externalWhitelabel) {

        return languages.getSelectedLanguageCode().stream()
                .map(language -> convertToEventChannelContentLink(urlChannel, urlPortal, channel, event, language, v4Enabled, externalWhitelabel))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static EventChannelContentPublishedLinks convertToEventChannelContentSessionLinksByLanguage(Sessions sessions,
                                                                                                       String urlPortal,
                                                                                                       ChannelResponse channel,
                                                                                                       String language,
                                                                                                       Event event,
                                                                                                       SessionLinksFilter filter,
                                                                                                       Boolean v4Enabled,
                                                                                                       Boolean externalWhitelabel) {

        return convertToEventChannelContentSessionLink(sessions, urlPortal, channel, language, event, filter, v4Enabled, externalWhitelabel);
    }

    private static EventChannelContentPublishedLinks convertToEventChannelContentSessionLink(Sessions sessions, String urlPortal, ChannelResponse channel,
                                                                                             String language, Event event, SessionLinksFilter filter, Boolean v4Enabled, Boolean externalWhitelabel) {
        EventChannelContentPublishedLinks result = new EventChannelContentPublishedLinks();
        result.setSessionsLinks(new ArrayList<>());

        sessions.getData().stream().forEach(session -> {
            if (SessionStatus.SCHEDULED.equals(session.getStatus()) || SessionStatus.READY.equals(session.getStatus())
                    || SessionStatus.PREVIEW.equals(session.getStatus())) {
                convertToEventChannelContentSessionLinks(session, urlPortal, channel, language, event, result, filter, v4Enabled, externalWhitelabel);
            }
        });

        return result;
    }

    private static EventChannelContentLinks convertToEventChannelContentLink(String urlChannel, String urlPortal,
                                                                             ChannelResponse channel,
                                                                             Event event,
                                                                             String language,
                                                                             Boolean v4Enabled,
                                                                             Boolean externalWhitelabel) {
        EventChannelContentLinks result = new EventChannelContentLinks();
        String finalUrl = ChannelsUrlUtils.selectUrlByChannelConfig(v4Enabled, urlChannel, urlPortal);
        result.setPendingGeneration(Boolean.FALSE);

        result.setLanguage(language);
        if (BooleanUtils.isTrue(externalWhitelabel)) {
            if (BooleanUtils.isTrue(event.getSupraEvent())) {
                return null;
            }
            String link = ExternalWhitelabelUtils.buildEventUrl(channel.getDomain(), channel.getWhitelabelPath(), event.getExternalReference(), language);
            result.setLink(link);
            if (StringUtils.isBlank(link)) {
                result.setPendingGeneration(Boolean.TRUE);
            }
        } else if (BooleanUtils.isTrue(v4Enabled)) {
            result.setLink(ChannelsUrlUtils.buildUrlByChannels(finalUrl, channel.getUrl(), event.getId(), language, Boolean.TRUE, Boolean.FALSE));
        } else {
            result.setLink(buildEventUrl(finalUrl, channel.getUrl(), language, event.getId()));
        }


        return result;
    }

    private static String buildEventUrl(String urlPortal, String urlIntegration, String language, Long eventId) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, ConverterUtils.toLocale(language), ENTRADAS, EVENTO, eventId)
                .separator(URL_SEPARATOR)
                .build();
    }

    private static void convertToEventChannelContentSessionLinks(Session session, String urlPortal,
                                                                 ChannelResponse channel,
                                                                 String language, Event event,
                                                                 EventChannelContentPublishedLinks eventChannelContentAllLinks,
                                                                 SessionLinksFilter filter,
                                                                 Boolean v4Enabled,
                                                                 Boolean externalWhitelabel) {

        EventChannelContentSessionLink sessionLink = new EventChannelContentSessionLink();

        sessionLink.setId(session.getId());
        sessionLink.setName(session.getName());
        sessionLink.setStartDate(session.getDate().getStart());
        sessionLink.setPendingGeneration(Boolean.FALSE);

        if (BooleanUtils.isTrue(externalWhitelabel)) {
            String link;
            if (BooleanUtils.isTrue(event.getSupraEvent()) || !session.getSessionType().equals(SessionType.SESSION)) {
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
            sessionLink.setLink(ChannelsUrlUtils.buildUrlByChannels(urlPortal, channel.getUrl(),
                    session.getId(), language, Boolean.FALSE, isPreview));
        } else {
            sessionLink.setLink(buildSessionUrl(urlPortal, channel.getUrl(), language, event.getId(),
                    session.getId(), session.getStatus()));
        }
        sessionLink.setEnabled((SessionStatus.READY.equals(session.getStatus())
                && session.getDate().getChannelPublication().absolute().toInstant().compareTo(Instant.now()) <= 0)
                || SessionStatus.PREVIEW.equals(session.getStatus()));

        if (BooleanUtils.isTrue(session.getEnableQueue())) {
            sessionLink.setQueueit(new EventChannelContentSessionLinkQueueIt(true));
        }

        if (SessionStatus.PREVIEW.equals(session.getStatus())) {
            if (filter.getSessionStatus().contains(es.onebox.mgmt.sessions.enums.SessionStatus.PREVIEW)) {
                eventChannelContentAllLinks.getSessionsLinks().add(sessionLink);
            }
        } else {
            if (filter.getSessionStatus().contains(es.onebox.mgmt.sessions.enums.SessionStatus.SCHEDULED) || filter.getSessionStatus().contains(es.onebox.mgmt.sessions.enums.SessionStatus.READY)) {
                eventChannelContentAllLinks.getSessionsLinks().add(sessionLink);
            }
        }
    }

    private static String buildSessionUrl(String urlPortal, String urlIntegration, String language, Long eventId, Long sessionId, SessionStatus status) {
        String select = SELECT;
        if (SessionStatus.PREVIEW.equals(status)) {
            String hash = HashUtils.encodeHashIds(sessionId);
            select = SELECT + PREVIEW_TOKEN + hash;
        }
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, ConverterUtils.toLocale(language), ENTRADAS, EVENTO, eventId, SESSION, sessionId, select)
                .separator(URL_SEPARATOR)
                .build();
    }

    public static List<BaseLinkDTO> buildEditAttendantsLinks(String urlPortal, String channelUrl,
                                                             ChannelLanguagesDTO languages, Boolean v4Enabled) {
        return languages.getSelectedLanguageCode().stream()
                .map(lang -> buildEditAttendantsLink(urlPortal, channelUrl, lang, v4Enabled))
                .collect(Collectors.toList());
    }

    private static BaseLinkDTO buildEditAttendantsLink(String urlPortal, String channelUrl, String language, boolean v4Enabled) {
        if (BooleanUtils.isTrue(v4Enabled)) {
            return new BaseLinkDTO(ChannelsUrlUtils.buildUrlAttendeesByChannels(urlPortal, channelUrl, language), language);
        } else {
            String url = UrlBuilder.builder()
                    .protocol(COMMUNICATION_PROTOCOL)
                    .pathParts(urlPortal, channelUrl, ConverterUtils.toLocale(language), ATTENDEES)
                    .separator(URL_SEPARATOR)
                    .build();
            return new BaseLinkDTO(url, language);
        }
    }

    public static UpdateFavoriteChannel fromDTO(UpdateFavoriteChannelDTO from) {
        UpdateFavoriteChannel target = new UpdateFavoriteChannel();
        target.setFavorite(from.getFavorite());
        return target;
    }
}

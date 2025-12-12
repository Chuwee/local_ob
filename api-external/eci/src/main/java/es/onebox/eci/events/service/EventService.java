package es.onebox.eci.events.service;

import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.ChannelSessionResponse;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.eci.events.converter.EventConverter;
import es.onebox.eci.events.converter.SessionConverter;
import es.onebox.eci.events.dto.Event;
import es.onebox.eci.events.dto.Session;
import es.onebox.eci.service.ChannelsHelper;
import es.onebox.eci.utils.AuthenticationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private final CatalogRepository catalogRepository;
    private final TokenRepository tokenRepository;
    private final ChannelsHelper channelsHelper;
    private final ChannelConfigRepository channelConfigRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public EventService(CatalogRepository catalogRepository, TokenRepository tokenRepository,
                        ChannelsHelper channelsHelper, ChannelConfigRepository channelConfigRepository,
                        UsersRepository usersRepository) {
        this.catalogRepository = catalogRepository;
        this.tokenRepository = tokenRepository;
        this.channelsHelper = channelsHelper;
        this.channelConfigRepository = channelConfigRepository;
        this.usersRepository = usersRepository;
    }

    public List<Event> getEvents(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, String channelIdentifier) {
        List<Event> events = new ArrayList<>();
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            events = getEvents(gte, lte, limit, offset, channelDetails);
        }
        return events;
    }

    public Event getEvent(String channelIdentifier, Long eventId) {
        Event event = null;
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            event = getEvent(channelDetails, eventId);
        }
        return event;
    }

    public List<Session> getSessions(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, String channelIdentifier, Long eventId) {
        List<Session> sessions = new ArrayList<>();
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            sessions = getSessions(gte, lte, limit, offset, channelDetails, eventId);
        }
        return sessions;
    }

    public Session getSession(String channelIdentifier, Long eventId, Long sessionId) {
        Session session = null;
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            session = getSession(channelDetails, eventId, sessionId);
        }
        return session;
    }

    private List<Event> getEvents(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, List<ChannelDTO> channelDetails) {
        List<ChannelEvent> channelEvents;
        List<ChannelEventDetail> channelEventDetails = new ArrayList<>();

        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            if (token != null) {
                try {
                    channelEvents = catalogRepository.getEvents(token, gte, lte);
                    channelEventDetails.addAll(channelEvents.stream()
                            .map(channelEvent -> catalogRepository.getEvent(token, channelEvent.getId())).toList());
                } catch (OneboxRestException e) {
                    if (!e.getErrorCode().equals(ApiExternalErrorCode.EVENT_NOT_FOUND.getErrorCode())) {
                        LOGGER.warn("[ECI GET EVENTS] Error code: {}, message: {}", e.getErrorCode(), e.getMessage());
                    }
                }
            }
        }

        return channelEventDetails.stream()
                .map(EventConverter::convert)
                .distinct()
                .sorted(Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Event getEvent(List<ChannelDTO> channelDetails, Long eventId) {
        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            if (token != null) {
                try {
                    ChannelEventDetail eventDetail = catalogRepository.getEvent(token, eventId);
                    return EventConverter.convert(eventDetail);
                } catch (OneboxRestException e) {
                    if (!e.getErrorCode().equals(ApiExternalErrorCode.EVENT_NOT_FOUND.getErrorCode())) {
                        LOGGER.warn("[ECI GET EVENT] Error code: {}, message: {}", e.getErrorCode(), e.getMessage());
                    }
                }

            }
        }
        throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
    }

    private List<Session> getSessions(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, List<ChannelDTO> channelDetails, Long eventId) {
        List<ChannelSession> channelSessionsDetails = new ArrayList<>();
        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            if (token != null) {
                try {
                    ChannelSessionResponse sessionResponse = catalogRepository.getSessions(token, eventId, limit, offset, gte, lte);
                    channelSessionsDetails.addAll(sessionResponse.getData());
                } catch (OneboxRestException e) {
                    if (!e.getErrorCode().equals(ApiExternalErrorCode.EVENT_NOT_FOUND.getErrorCode())) {
                        LOGGER.warn("[ECI GET SESSIONS] Error code: {}, message: {}", e.getErrorCode(), e.getMessage());
                    }
                }
            }
        }
        return channelSessionsDetails.stream()
                .map(SessionConverter::convert)
                .distinct()
                .sorted(Comparator.comparing(Session::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Session getSession(List<ChannelDTO> channelDetails, Long eventId, Long sessionId) {
        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            if (token != null) {
                try {
                    ChannelSession sessionDetail = catalogRepository.getSession(token, eventId, sessionId);
                    return SessionConverter.convert(sessionDetail);
                } catch (OneboxRestException e) {
                    if (!e.getErrorCode().equals(ApiExternalErrorCode.EVENT_NOT_FOUND.getErrorCode())) {
                        LOGGER.warn("[ECI GET SESSION] Error code: {}, message: {}", e.getErrorCode(), e.getMessage());
                    }
                }
            }
        }
        throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
    }
}

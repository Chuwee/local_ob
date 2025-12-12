package es.onebox.channels.catalog;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventsResponse;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.ChannelSessionResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionAvailabilityResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionVenueMapResponse;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTO;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTO;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTOBuilder;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChannelCatalogService {

    private final CatalogRepository catalogRepository;
    private final TokenRepository tokenRepository;
    private final ChannelConfigRepository channelConfigRepository;


    @Autowired
    public ChannelCatalogService(CatalogRepository catalogRepository, TokenRepository tokenRepository, ChannelConfigRepository channelConfigRepository) {
        this.catalogRepository = catalogRepository;
        this.tokenRepository = tokenRepository;
        this.channelConfigRepository = channelConfigRepository;
    }

    @Cached(key = "catalog_events", timeUnit = TimeUnit.MINUTES, expires = 5)
    public ChannelEventsResponse getEvents(@CachedArg Long channelId, @CachedArg String apiKey, @CachedArg EventsRequestDTO request) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getEvents(accessToken, request);
    }

    @Cached(key = "catalog_event")
    public ChannelEventDetail getEvent(@CachedArg Long channelId, @CachedArg String apiKey, @CachedArg Long eventId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getEvent(accessToken, eventId);
    }

    public ChannelSessionResponse getSessions(Long channelId, String apiKey, SessionsRequestDTO request) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getSessions(accessToken, request);
    }

    @Cached(key = "catalog_event_sessions")
    public List<ChannelSession> getAllEventSessions(@CachedArg Long channelId, @CachedArg String apiKey, @CachedArg Long eventId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        var requestBuilder = SessionsRequestDTOBuilder.builder()
                .eventIds(List.of(eventId))
                .offset(0L);
        var sessions = new ArrayList<ChannelSession>();
        var sessionsResponse = catalogRepository.getSessions(accessToken, requestBuilder.build());
        sessions.addAll(sessionsResponse.getData());
        var next = sessionsResponse.getMetadata().getNext();
        do {
            if (next != null) {
                sessionsResponse = catalogRepository.getSessionsByNextPage(accessToken, next);
                next = sessionsResponse.getMetadata().getNext();
                sessions.addAll(sessionsResponse.getData());
            }
        } while (StringUtils.isNotBlank(sessionsResponse.getMetadata().getNext()));

        return sessions;
    }

    @Cached(key = "catalog_session")
    public ChannelSession getSession(@CachedArg Long channelId, @CachedArg String apiKey, @SkippedCachedArg Long eventId,
                                     @CachedArg Long sessionId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getSession(accessToken, eventId, sessionId);
    }

    @Cached(key = "catalog_session_availability")
    public ChannelSessionAvailabilityResponse getSessionAvailability(@CachedArg Long channelId,
                                                                     @CachedArg String apiKey, @CachedArg Long sessionId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getSessionAvailability(accessToken, sessionId);
    }

    @Cached(key = "catalog_session_venue_map")
    public ChannelSessionVenueMapResponse getSessionVenueMap(@CachedArg Long channelId,
                                                             @CachedArg String apiKey,
                                                             @CachedArg Long sessionId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getSessionVenueMap(accessToken, sessionId);
    }

    @Cached(key = "catalog_session_prices")
    public SessionPrices getSessionPrices(@CachedArg Long channelId,
                                          @CachedArg String apiKey,
                                          @CachedArg Long sessionId) {
        var accessToken = this.tokenRepository.getSellerChannelToken(channelId, apiKey);

        return catalogRepository.getSessionPrices(accessToken, sessionId);
    }

    @Cached(key = "catalog_channel_config")
    public ChannelConfigDTO getChannelConfig(@CachedArg Long channelId, @CachedArg String channelPath) {
        try {
            if (channelId != null) {
                return channelConfigRepository.getChannelConfig(channelId);
            } else {
                return channelConfigRepository.getChannelConfigByPath(channelPath);
            }
        } catch (Exception e) {
            throw new OneboxRestException(ApiExternalErrorCode.NOT_FOUND);
        }
    }
}

package es.onebox.common.datasources.catalog.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.catalog.ApiCatalogDatasource;
import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventsResponse;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.ChannelSessionResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionAvailabilityResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionVenueMapResponse;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTO;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogRepository {

    private final ApiCatalogDatasource apiCatalogDatasource;

    private static final Long DEFAULT_LIMIT = 50L;

    @Autowired
    public CatalogRepository(ApiCatalogDatasource apiCatalogDatasource) {
        this.apiCatalogDatasource = apiCatalogDatasource;
    }

    public List<ChannelEvent> getEvents(String token, ZonedDateTime gte, ZonedDateTime lte) {
        return getEvents(token, null, null, gte, lte, null);
    }

    public List<ChannelEvent> getEvents(String token, Long limit, Long offset) {
        return getEvents(token, limit, offset, null, null, null);
    }

    @Cached(key = "getEvents", expires = 60 * 10)
    public List<ChannelEvent> getEvents(@CachedArg String token) {
        return getEvents(token, null, null, null, null, null);
    }

    public List<ChannelEvent> getEvents(String token, Long locationId) {
        return getEvents(token, null, null, null, null, locationId);
    }

    public List<ChannelEvent> getEvents(String token, Long limit, Long offset, ZonedDateTime gte, ZonedDateTime lte, Long locationId) {
        if (limit == null && offset == null) {
            List<ChannelEvent> channelEvents = new ArrayList<>();
            Long internalOffset = 0L;
            while (internalOffset != null) {
                ChannelEventsResponse result = apiCatalogDatasource.getEvents(token, DEFAULT_LIMIT, internalOffset, gte, lte, locationId);
                channelEvents.addAll(result.getData());
                internalOffset = result.getMetadata().nextOffset();
            }
            return channelEvents;
        } else {
            return apiCatalogDatasource.getEvents(token, limit, offset, gte, lte, locationId).getData();
        }
    }

    @Cached(key = "api_catalog_event_details", expires = 3600)
    public ChannelEventDetail getEvent(@CachedArg String token, @CachedArg long eventId) {
        return apiCatalogDatasource.getEvent(token, eventId);
    }

    public ChannelEventDetail getEventOrElseNull(@CachedArg String token, @CachedArg long eventId) {
        try {
            return getEvent(token, eventId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode().equals(ApiExternalErrorCode.EVENT_NOT_FOUND.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Cached(key = "api_catalog_session_details", expires = 3600)
    public ChannelSession getSession(@CachedArg String token, @CachedArg long eventId, @CachedArg long sessionId) {
        return apiCatalogDatasource.getSession(token, eventId, sessionId);
    }

    public ChannelSessionResponse getSessions(String token, Long eventId, Long limit, Long offset) {
        return getSessions(token, eventId, limit, offset, null, null);
    }

    public ChannelSessionResponse getSessions(String token, Long eventId, Long limit, Long offset,
                                              ZonedDateTime gte, ZonedDateTime lte) {
        return apiCatalogDatasource.getSessions(token, eventId, limit, offset, gte, lte);
    }

    public ChannelSessionResponse getSessions(String token, SessionsRequestDTO request) {
        return apiCatalogDatasource.getSessions(token, request);
    }

    public ChannelSessionResponse getSessionsByNextPage(String token, String urlNextPage) {
        return apiCatalogDatasource.getSessionsByNextPage(token, urlNextPage);
    }

    public ChannelEventsResponse getEvents(String token, EventsRequestDTO request) {
        return apiCatalogDatasource.getEvents(token, request);
    }

    public ChannelSessionAvailabilityResponse getSessionAvailability(String token, long sessionId) {
        return apiCatalogDatasource.getSessionAvailability(token, sessionId);
    }

    public ChannelSessionVenueMapResponse getSessionVenueMap(String token, long sessionId) {
        return apiCatalogDatasource.getSessionVenueMap(token, sessionId);
    }

    public SessionPrices getSessionPrices(String token, long sessionId) {
        return apiCatalogDatasource.getSessionPrices(token, sessionId);
    }

}

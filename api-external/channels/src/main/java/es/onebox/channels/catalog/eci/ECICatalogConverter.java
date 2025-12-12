package es.onebox.channels.catalog.eci;


import es.onebox.cache.repository.LocalCacheRepository;
import es.onebox.channels.catalog.CatalogEvent;
import es.onebox.channels.catalog.CatalogEvents;
import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.channels.catalog.ChannelCatalogConverter;
import es.onebox.channels.catalog.ChannelCatalogService;
import es.onebox.channels.catalog.ChannelCatalogUtils;
import es.onebox.channels.catalog.eci.dto.ECICatalogDTO;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventsResponse;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTOBuilder;
import es.onebox.common.datasources.common.dto.Metadata;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("ECICatalogConverter")
public final class ECICatalogConverter implements ChannelCatalogConverter<ECICatalogDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ECICatalogConverter.class);

    private final ChannelCatalogService channelCatalogService;
    private final LocalCacheRepository localCacheRepository;


    @Autowired
    public ECICatalogConverter(ChannelCatalogService channelCatalogService, LocalCacheRepository localCacheRepository) {
        this.channelCatalogService = channelCatalogService;
        this.localCacheRepository = localCacheRepository;
    }

    @Override
    public ECICatalogDTO convert(ChannelCatalogContext context, Long limit, Long offset, Map parameters) {
        var channelId = context.getId();
        var apiKey = context.getApiKey();
        var request = EventsRequestDTOBuilder.builder()
                .eventIds(Arrays.asList(ChannelCatalogUtils.getEventId(parameters)))
                .limit(limit)
                .offset(offset)
                .build();

        ChannelEventsResponse events = channelCatalogService.getEvents(channelId, apiKey, request);

        List<CatalogEvent> collect = events.getData().stream().map(event -> {
            Long eventId = event.getId();
            return localCacheRepository.cached("ECICatalogEvent", 5, TimeUnit.MINUTES, () -> {
                ChannelEventDetail eventDetail = null;
                List<ChannelSession> sessions = new ArrayList<>();
                try {
                    eventDetail = channelCatalogService.getEvent(channelId, apiKey, eventId);
                } catch (Exception e) {
                    LOGGER.warn("Failed to fetch event detail: eventId: {}, error: {}", eventId, e.getMessage());
                }
                try {
                    sessions = channelCatalogService.getAllEventSessions(channelId, apiKey, eventId);
                } catch (Exception e) {
                    LOGGER.warn("Failed to fetch sessions: eventId: {}, error: {}", eventId, e.getMessage());
                }

                if (eventDetail == null || ObjectUtils.isEmpty(sessions)) {
                    LOGGER.warn("Skipping eventId {} due to missing data", eventId);
                    return null;
                }
                sessions.stream().filter(ChannelSession::getSoldOut).forEach(channelSession -> {
                    SessionPrices sessionPriceMatrix = channelCatalogService.getSessionPrices(channelId, apiKey, channelSession.getId());
                    ECIConverterUtils.overrideMinPrice(channelSession, sessionPriceMatrix);
                });

                return new CatalogEvent(eventDetail, sessions);
            }, new Object[]{eventId});
        }).filter(Objects::nonNull).collect(Collectors.toList());
        CatalogEvents catalogEvents = new CatalogEvents(events.getMetadata(), collect);

        return ECIChannelCatalogConverter.convert(catalogEvents, context);
    }

    @Override
    public String getLimitParameter() {
        return "size";
    }

    @Override
    public String getOffsetParameter() {
        return "element";
    }
}

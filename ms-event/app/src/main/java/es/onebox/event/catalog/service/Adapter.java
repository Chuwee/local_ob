package es.onebox.event.catalog.service;

import es.onebox.event.catalog.dto.ChannelCatalogEventDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventSessionsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogEventsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogSessionsResponse;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogTypeFilter;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;

public interface Adapter {

    ChannelCatalogEventsResponse searchEvents(Long channelId, ChannelCatalogEventsFilter filter);
    ChannelCatalogEventSessionsResponse searchSessions(final Long channelId, final Long eventId, ChannelCatalogEventSessionsFilter filter, EventData eventData );
    ChannelCatalogSessionsResponse searchSessions(final Long channelId, ChannelCatalogSessionsFilter filter);
    ChannelCatalogEventDetailDTO getEventDetail(final Long channelId, final Long eventId, final ChannelCatalogTypeFilter filter);

    Event getCatalogEvent(Long eventId);
    Session getCatalogSession(Long sessionId);
    <T extends ChannelEvent> T getCatalogChannelEvent(final Long channelId, final Long eventId, final Long agencyId);
    <T extends ChannelSession> T getCatalogChannelSession(final Long channelId, final Long sessionId, final Long agencyId);
    <T extends ChannelSession> EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig(Long eventId, T channelSession);

    default String getS3Repository(String s3domain , String fileBasePath) {
        return s3domain + fileBasePath;
    }

}

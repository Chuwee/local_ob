package es.onebox.event.catalog.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.converter.ChannelCatalogEventConverter;
import es.onebox.event.catalog.converter.ChannelCatalogSessionConverter;
import es.onebox.event.catalog.dao.CatalogChannelEventAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dto.ChannelCatalogEventDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventSessionsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogEventsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogSessionsResponse;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogTypeFilter;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyWithAll;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ChannelProductsCouchDao;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.secondarymarket.service.SecondaryMarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelCatalogAgencyService extends ChannelCatalogAdapter  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCatalogAgencyService.class);

    private final ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao;
    private final ChannelEventAgencyElasticDao channelEventAgencyElasticDao;
    private final String s3domain;
    private final String fileBasePath;


    public ChannelCatalogAgencyService(
            @Value("${onebox.repository.S3SecureUrl}") String s3domain,
            @Value("${onebox.repository.fileBasePath}") String fileBasePath,
            ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao,
            ChannelEventAgencyElasticDao channelEventAgencyElasticDao,
            CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao,
            CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao,
            CatalogChannelEventCouchDao catalogChannelEventCouchDao,
            CatalogChannelSessionCouchDao catalogChannelSessionCouchDao,
            CatalogEventCouchDao catalogEventCouchDao,
            CatalogSessionCouchDao catalogSessionCouchDao,
            SecondaryMarketService secondaryMarketService,
            EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
            EventPromotionCouchDao eventPromotionCouchDao, ChannelProductsCouchDao channelProductsCouchDao) {
        super(catalogChannelEventCouchDao, catalogChannelEventAgencyCouchDao,
                catalogChannelSessionAgencyCouchDao,
                catalogChannelSessionCouchDao,
                catalogEventCouchDao, catalogSessionCouchDao, secondaryMarketService, eventSecondaryMarketConfigService,
                eventPromotionCouchDao, channelProductsCouchDao);
        this.s3domain = s3domain;
        this.fileBasePath = fileBasePath;
        this.channelSessionAgencyElasticDao = channelSessionAgencyElasticDao;
        this.channelEventAgencyElasticDao = channelEventAgencyElasticDao;
    }


    public ChannelCatalogEventsResponse searchEvents(final Long channelId, ChannelCatalogEventsFilter filter) {
        Page page = preparePage(filter);
        String[] fields = prepareFields(filter);
        Long agencyId = filter.getAgencyId();
        BoolQuery.Builder channelEventQuery = prepareChannelAgencyEventsQuery(channelId, agencyId, filter);
        BoolQuery.Builder eventQuery = prepareEventsQuery(filter);
        BoolQuery.Builder channelSessionQuery = prepareChannelSessionAgencyQuery(filter);
        ElasticSearchResults<ChannelEventAgencyWithParent> result = channelEventAgencyElasticDao.searchChannelEventsWithParent(channelEventQuery,
                eventQuery, channelSessionQuery, page, fields, filter);
        ChannelCatalogEventsResponse response = new ChannelCatalogEventsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogEventConverter.fromAgency(result.getResults(), this.getS3Repository(s3domain, fileBasePath)));
        return response;
    }

    @Override
    public ChannelEventAgency getCatalogChannelEvent(final Long channelId, final Long eventId, final Long agencyId) {
        String channelStr = channelId.toString();
        String eventStr = eventId.toString();
        ChannelEventAgency channelEvent = catalogChannelEventAgencyCouchDao.get(channelStr, eventStr, agencyId.toString());
        if (channelEvent == null || channelEvent.getCatalogInfo() == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_EVENT_NOT_FOUND).
                    setMessage("ChannelEvent not found for channel: " + channelId + " - event: " + eventId).build();
        }
        return channelEvent;
    }

    @Override
    public ChannelCatalogEventSessionsResponse searchSessions(Long channelId, Long eventId, ChannelCatalogEventSessionsFilter filter, EventData eventData) {
        Page page = preparePage(filter);
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelEventAgencySessionsQuery(channelId, eventId, filter.getAgencyId(), filter);
        ElasticSearchResults<ChannelSessionAgencyWithParent> result = channelSessionAgencyElasticDao.searchChannelSessionsWithParent(eventId, channelSessionQuery, page);
        ChannelCatalogEventSessionsResponse response = new ChannelCatalogEventSessionsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogSessionConverter.fromAgency(result.getResults(), eventData, this.getS3Repository(s3domain, fileBasePath)));
        return response;
    }

    @Override
    public ChannelSessionAgency getCatalogChannelSession(final Long channelId, final Long sessionId, final Long agencyId) {
        ChannelSessionAgency channelSession = catalogChannelSessionAgencyCouchDao.get(channelId.toString(), sessionId.toString(), agencyId.toString());
        if (channelSession == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_SESSION_NOT_FOUND).
                    setMessage("ChannelSessionAgency not found for channel: " + channelId + " - session: " + sessionId + " agency: " + agencyId).build();
        }
        return channelSession;
    }

    @Override
    public ChannelCatalogSessionsResponse searchSessions(Long channelId, ChannelCatalogSessionsFilter filter) {
        Page page = preparePage(filter);
        Long agencyId = filter.getAgencyId();
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelAgencySessionsQuery(channelId, agencyId, filter);
        ElasticSearchResults<ChannelSessionAgencyData> result = channelSessionAgencyElasticDao.searchChannelSessions(channelSessionQuery, page);
        List<Long> eventIds = result.getResults().stream().map(s -> s.getChannelSessionAgency().getEventId()).distinct().collect(Collectors.toList());
        List<Long> sessionIds = result.getResults().stream().map(s -> s.getChannelSessionAgency().getSessionId()).distinct().collect(Collectors.toList());
        List<Event> eventList = catalogEventCouchDao.bulkGet(eventIds);
        Map<Long, Event> events = eventList.stream().collect(Collectors.toMap(Event::getEventId, Function.identity()));
        Map<Long, Session> sessions = catalogSessionCouchDao.bulkGet(sessionIds).stream()
                .collect(Collectors.toMap(Session::getSessionId, Function.identity()));

        Map<String, ChannelEventAgency> channelEvents = new HashMap<>();
        List<ChannelSessionAgencyWithAll> list = result.getResults().stream().map(channelSessionData -> {
            Long eventId = channelSessionData.getChannelSessionAgency().getEventId();
            ChannelEventAgencyData channelEventData = new ChannelEventAgencyData();
            if (channelEvents.containsKey(eventId.toString())) {
                channelEventData.setChannelEventAgency(channelEvents.get(eventId.toString()));
            } else {
                ChannelEventAgency channelEvent = getCatalogChannelEvent(channelId, eventId, agencyId);
                channelEvents.put(eventId.toString(), channelEvent);
                channelEventData.setChannelEventAgency(channelEvent);
            }
            EventData eventData = new EventData();
            eventData.setEvent(events.get(eventId));
            SessionData sessionData = new SessionData();
            sessionData.setSession(sessions.get(channelSessionData.getChannelSessionAgency().getSessionId()));
            if (eventData.getEvent() == null) {
                LOGGER.warn("[EVENT2ES] eventId: {} - Event not found in CB - skip from catalog", eventId);
                return null;
            }
            return ChannelCatalogSessionConverter.convert(channelSessionData, eventData, sessionData, channelEventData);
        }).filter(Objects::nonNull).collect(Collectors.toList());

        var secondaryMarketByEntity = getSecondaryMarketStatusByEntity(eventList);
        ChannelCatalogSessionsResponse response = new ChannelCatalogSessionsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogSessionConverter.fromAgency(list, this.getS3Repository(s3domain, fileBasePath), secondaryMarketByEntity));
        return response;
    }

    @Override
    public ChannelCatalogEventDetailDTO getEventDetail(Long channelId, Long eventId, ChannelCatalogTypeFilter filter) {
        Event event = getCatalogEvent(eventId);
        Long agencyId = filter.getAgencyId();
        ChannelEventAgency channelEvent = getCatalogChannelEvent(channelId, eventId, agencyId);

        EventData eventData = new EventData();
        eventData.setEvent(event);
        ChannelEventAgencyWithParent channelEventWithParent = new ChannelEventAgencyWithParent();
        channelEventWithParent.setChannelEventAgency(channelEvent);
        channelEventWithParent.setEventData(eventData);
        return ChannelCatalogEventConverter.convertWithDetailsFromAgency(channelEventWithParent, getS3Repository(s3domain, fileBasePath), eventPromotionCouchDao);
    }
}

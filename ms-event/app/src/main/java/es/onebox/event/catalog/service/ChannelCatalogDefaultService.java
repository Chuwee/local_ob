package es.onebox.event.catalog.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.couchbase.core.Key;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.converter.CatalogProductConverter;
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
import es.onebox.event.catalog.dto.ChannelCatalogProductsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogSessionsResponse;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogProductsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogTypeFilter;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithAll;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ChannelProductsCouchDao;
import es.onebox.event.products.dao.ProductCatalogCouchDao;
import es.onebox.event.products.dao.couch.ProductCatalogChannel;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import es.onebox.event.products.enums.ProductPublicationType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.secondarymarket.service.SecondaryMarketService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelCatalogDefaultService extends ChannelCatalogAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCatalogDefaultService.class);

    private final ChannelEventElasticDao channelEventElasticDao;
    private final ChannelSessionElasticDao channelSessionElasticDao;
    private final ProductCatalogCouchDao productCatalogCouchDao;
    private final CatalogService catalogService;
    private final String s3domain;
    private final String fileBasePath;

    protected ChannelCatalogDefaultService(
            @Value("${onebox.repository.S3SecureUrl}") String s3domain,
            @Value("${onebox.repository.fileBasePath}") String fileBasePath,
            CatalogChannelEventCouchDao catalogChannelEventCouchDao,
            CatalogChannelSessionCouchDao catalogChannelSessionCouchDao,
            CatalogEventCouchDao catalogEventCouchDao,
            CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao,
            CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao,
            CatalogSessionCouchDao catalogSessionCouchDao,
            SecondaryMarketService secondaryMarketService,
            EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
            ChannelEventElasticDao channelEventElasticDao1,
            ChannelSessionElasticDao channelSessionElasticDao1,
            EventPromotionCouchDao eventPromotionCouchDao, ChannelProductsCouchDao channelProductsCouchDao,
            ProductCatalogCouchDao productCatalogCouchDao, CatalogService catalogService) {
        super(catalogChannelEventCouchDao, catalogChannelEventAgencyCouchDao, catalogChannelSessionAgencyCouchDao,
                catalogChannelSessionCouchDao, catalogEventCouchDao, catalogSessionCouchDao, secondaryMarketService,
                eventSecondaryMarketConfigService, eventPromotionCouchDao, channelProductsCouchDao);
        this.s3domain = s3domain;
        this.fileBasePath = fileBasePath;
        this.channelEventElasticDao = channelEventElasticDao1;
        this.channelSessionElasticDao = channelSessionElasticDao1;
        this.productCatalogCouchDao = productCatalogCouchDao;
        this.catalogService = catalogService;
    }


    @Override
    public ChannelCatalogEventsResponse searchEvents(final Long channelId, ChannelCatalogEventsFilter filter) {
        Page page = preparePage(filter);
        String[] fields = prepareFields(filter);
        BoolQuery.Builder channelEventQuery = prepareChannelEventsQuery(channelId, filter);
        BoolQuery.Builder eventQuery = prepareEventsQuery(filter);
        BoolQuery.Builder channelSessionQuery = prepareChannelSessionQuery(filter);
        ElasticSearchResults<ChannelEventWithParent> result = channelEventElasticDao.searchChannelEventsWithParent(channelEventQuery,
                eventQuery, channelSessionQuery, page, fields, filter);
        ChannelCatalogEventsResponse response = new ChannelCatalogEventsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogEventConverter.from(result.getResults(), getS3Repository(s3domain, fileBasePath)));
        return response;
    }

    @Override
    public ChannelEvent getCatalogChannelEvent(final Long channelId, final Long eventId, final Long agencyId) {
        String channelStr = channelId.toString();
        String eventStr = eventId.toString();
        ChannelEvent channelEvent = catalogChannelEventCouchDao.get(channelStr, eventStr);
        if (channelEvent == null || channelEvent.getCatalogInfo() == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_EVENT_NOT_FOUND).
                    setMessage("ChannelEvent not found for channel: " + channelId + " - event: " + eventId).build();
        }
        return channelEvent;
    }

    @Override
    public ChannelSession getCatalogChannelSession(final Long channelId, final Long sessionId, final Long agencyId) {
        ChannelSession channelSession = catalogChannelSessionCouchDao.get(channelId.toString(), sessionId.toString());
        if (channelSession == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_SESSION_NOT_FOUND).
                    setMessage("ChannelSession not found for channel: " + channelId + " - session: " + sessionId).build();
        }
        return channelSession;
    }

    @Override
    public ChannelCatalogEventSessionsResponse searchSessions(Long channelId, Long eventId, ChannelCatalogEventSessionsFilter filter, EventData eventData) {
        Page page = preparePage(filter);
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelEventSessionsQuery(channelId, eventId, filter);
        ElasticSearchResults<ChannelSessionWithParent> result = channelSessionElasticDao.searchChannelSessionsWithParent(eventId, channelSessionQuery, page);
        ChannelCatalogEventSessionsResponse response = new ChannelCatalogEventSessionsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogSessionConverter.convert(result.getResults(), eventData, getS3Repository(s3domain, fileBasePath)));
        return response;
    }

    @Override
    public ChannelCatalogSessionsResponse searchSessions(Long channelId, ChannelCatalogSessionsFilter filter) {
        Page page = preparePage(filter);
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelSessionsQuery(channelId, filter);
        ElasticSearchResults<ChannelSessionData> result = channelSessionElasticDao.searchChannelSessions(channelSessionQuery, page);
        List<Long> eventIds = result.getResults().stream().map(s -> s.getChannelSession().getEventId()).distinct().collect(Collectors.toList());
        List<Long> sessionIds = result.getResults().stream().map(s -> s.getChannelSession().getSessionId()).distinct().collect(Collectors.toList());
        List<Event> eventList = catalogEventCouchDao.bulkGet(eventIds);
        Map<Long, Event> events = eventList.stream().collect(Collectors.toMap(Event::getEventId, Function.identity()));
        Map<Long, Session> sessions = catalogSessionCouchDao.bulkGet(sessionIds).stream()
                .collect(Collectors.toMap(Session::getSessionId, Function.identity()));

        Map<String, ChannelEvent> channelEvents = new HashMap<>();
        List<ChannelSessionWithAll> list = result.getResults().stream().map(channelSessionData -> {
            Long eventId = channelSessionData.getChannelSession().getEventId();
            ChannelEventData channelEventData = new ChannelEventData();
            if (channelEvents.containsKey(eventId.toString())) {
                channelEventData.setChannelEvent(channelEvents.get(eventId.toString()));
            } else {
                ChannelEvent channelEvent = getCatalogChannelEvent(channelId, eventId, null);
                channelEvents.put(eventId.toString(), channelEvent);
                channelEventData.setChannelEvent(channelEvent);
            }
            EventData eventData = new EventData();
            eventData.setEvent(events.get(eventId));
            SessionData sessionData = new SessionData();
            sessionData.setSession(sessions.get(channelSessionData.getChannelSession().getSessionId()));
            if (eventData.getEvent() == null) {
                LOGGER.warn("[EVENT2ES] eventId: {} - Event not found in CB - skip from catalog", eventId);
                return null;
            }
            return ChannelCatalogSessionConverter.convert(channelSessionData, eventData, sessionData, channelEventData);
        }).filter(Objects::nonNull).collect(Collectors.toList());

        var secondaryMarketByEntity = getSecondaryMarketStatusByEntity(eventList);
        ChannelCatalogSessionsResponse response = new ChannelCatalogSessionsResponse();
        response.setMetadata(result.getMetadata());
        response.setData(ChannelCatalogSessionConverter.convert(list, this.getS3Repository(s3domain, fileBasePath), secondaryMarketByEntity));
        return response;
    }

    @Override
    public ChannelCatalogEventDetailDTO getEventDetail(Long channelId, Long eventId, ChannelCatalogTypeFilter filter) {
        Event event = getCatalogEvent(eventId);
        ChannelEvent channelEvent = getCatalogChannelEvent(channelId, eventId, null);

        EventData eventData = new EventData();
        eventData.setEvent(event);
        ChannelEventWithParent channelEventWithParent = new ChannelEventWithParent();
        channelEventWithParent.setChannelEvent(channelEvent);
        channelEventWithParent.setEventData(eventData);
        return ChannelCatalogEventConverter.convertWithDetails(channelEventWithParent, getS3Repository(s3domain, fileBasePath), eventPromotionCouchDao);
    }

    public ChannelCatalogProductsResponse searchProducts(Long channelId, ChannelCatalogProductsFilter filter) {
        Set<Long> filteredProductIds = new HashSet<>();
        ChannelCatalogProductsResponse response = new ChannelCatalogProductsResponse();

        if (filter.getEventId() != null || filter.getSessionId() != null || filter.getId() != null) {
            filteredProductIds = getFilteredProductIds(channelId, filter);
            if (filteredProductIds.isEmpty()) {
                response.setMetadata(MetadataBuilder.build(filter, 0L));
                response.setData(new ArrayList<>());
                return response;
            }
        }

        List<Long> channelProductIds = getChannelProductIds(channelId);
        if (!filteredProductIds.isEmpty()) {
            channelProductIds = channelProductIds.stream().filter(filteredProductIds::contains).collect(Collectors.toList());
        }

        List<ProductCatalogDocument> channelProducts = filterProducts(channelId, getChannelProducts(channelProductIds), filter);
        Map<Long, Map<Long, ProductStockStatus>> productsAvailability = catalogService.getProductsAvailability(channelProducts);


        int to = Math.min((int) (filter.getOffset() + filter.getLimit()), channelProducts.size());

        response.setMetadata(MetadataBuilder.build(filter, (long) channelProducts.size()));
        response.setData(CatalogProductConverter.toChannelCatalogProducts(channelId, getS3Repository(s3domain, fileBasePath), productsAvailability,
                channelProducts.subList(filter.getOffset().intValue(), to)));

        return response;
    }


    private Set<Long> getFilteredProductIds(Long channelId, ChannelCatalogProductsFilter filter) {
        Set<Long> filteredProductIds = new HashSet<>();

        Set<Long> productIds = new HashSet<>();
        if (filter.getEventId() != null || filter.getSessionId() != null) {
            Page page = preparePage(filter);
            BoolQuery.Builder channelProductsQuery = ChannelCatalogESQueryAdapter.prepareChannelProductsQuery(channelId, filter);
            ElasticSearchResults<ChannelSessionData> result = channelSessionElasticDao.searchChannelSessions(channelProductsQuery, page);
            List<ChannelSession> channelSessions = result.getResults().stream().map(ChannelSessionData::getChannelSession).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(channelSessions)) {
                return filteredProductIds;
            }
            channelSessions.forEach(cs -> {
                        if (cs.getProductIds() != null) {
                            productIds.addAll(cs.getProductIds());
                        }
                    }
            );
            filteredProductIds.addAll(productIds);
        }
        if (filter.getId() != null) {
            if (!productIds.isEmpty()) {
                filteredProductIds = filter.getId().stream().filter(productIds::contains).collect(Collectors.toSet());
            } else {
                filteredProductIds.addAll(filter.getId());
            }
        }
        return filteredProductIds;
    }

    private List<ProductCatalogDocument> getChannelProducts(List<Long> poductIds) {
        List<Key> keys = poductIds.stream()
                .distinct()
                .map(sId -> new Key(new String[]{String.valueOf(sId)}))
                .toList();

        return productCatalogCouchDao.bulkGet(keys);
    }

    private List<ProductCatalogDocument> filterProducts(Long channelId, List<ProductCatalogDocument> channelProducts, ChannelCatalogProductsFilter filter) {

        List<ProductCatalogDocument> result = new ArrayList<>(channelProducts.size());
        final ProductType type = filter.getProductType();
        final Long currencyId = filter.getCurrencyId();
        final ProductPublicationType publicationType = filter.getProductPublicationType();
        final var deliverySet = filter.getDeliveryType() == null ? null : new HashSet<>(filter.getDeliveryType());

        for (ProductCatalogDocument p : channelProducts) {
            if (type != null && !type.equals(p.getType())) continue;
            if (currencyId != null && currencyId.intValue() != p.getCurrencyId()) continue;
            if (deliverySet != null && !deliverySet.contains(p.getDeliveryType())) continue;
            if (publicationType != null && !filterByPublicationType(p, channelId, publicationType)) continue;
            result.add(p);
        }

        return result.stream().sorted(Comparator.comparing(ProductCatalogDocument::getId)).collect(Collectors.toList());
    }

    private ProductCatalogChannel getChannelPublicationConfig(List<ProductCatalogChannel> channels, Long channelId) {
        return channels.stream().filter(c -> channelId.equals(c.getId())).findFirst().orElse(new ProductCatalogChannel());
    }

    private boolean filterByPublicationType(ProductCatalogDocument product, Long channelId, ProductPublicationType productPublicationType) {
        ProductCatalogChannel channelpublicationConfig = getChannelPublicationConfig(product.getChannels(), channelId);

        if (ProductPublicationType.DUAL_MODE.equals(productPublicationType)) {
            return Boolean.TRUE.equals(channelpublicationConfig.getStandaloneEnabled()) && Boolean.TRUE.equals(channelpublicationConfig.getCheckoutSuggestionEnabled());
        }
        if (ProductPublicationType.SESSION_RESTRICTED.equals(productPublicationType)) {
            return Boolean.TRUE.equals(channelpublicationConfig.getCheckoutSuggestionEnabled()) && !Boolean.TRUE.equals(channelpublicationConfig.getStandaloneEnabled());
        }
        if (ProductPublicationType.STANDALONE.equals(productPublicationType)) {
            return Boolean.TRUE.equals(channelpublicationConfig.getStandaloneEnabled()) && !Boolean.TRUE.equals(channelpublicationConfig.getCheckoutSuggestionEnabled());
        }
        return Boolean.FALSE;
    }

}

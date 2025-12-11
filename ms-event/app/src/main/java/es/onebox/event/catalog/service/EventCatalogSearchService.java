package es.onebox.event.catalog.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.InnerHits;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.json.JsonData;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.elasticsearch.exceptions.OneboxElasticSearchException;
import es.onebox.event.catalog.converter.utils.ChannelEventConversionUtils;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dto.ChannelEventDTO;
import es.onebox.event.catalog.dto.PriceZoneDTO;
import es.onebox.event.catalog.dto.VenueTemplateDTO;
import es.onebox.event.catalog.dto.filter.EventCatalogFilter;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.PriceZonePrice;
import es.onebox.event.catalog.elasticsearch.dto.Venue;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.EventVenueElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.datasources.ms.entity.dto.EntityState;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_CHANNEL_EVENT;

@Service
public class EventCatalogSearchService {

    private static final int MAX_INNER_HITS_ITEMS = 10000;
    private static final Page DEFAULT_PAGE = new Page(0, 10000);

    private static final String GENERIC_SEPARATOR = "|";
    private static final String GENERIC_ELEMENT = "elemComEvento";

    private final EventElasticDao eventElasticDao;
    private final CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    private final CatalogEventCouchDao catalogEventCouchDao;

    @Autowired
    public EventCatalogSearchService(EventElasticDao eventElasticDao,
                                     CatalogChannelEventCouchDao catalogChannelEventCouchDao,
                                     CatalogEventCouchDao catalogEventCouchDao) {
        this.eventElasticDao = eventElasticDao;
        this.catalogChannelEventCouchDao = catalogChannelEventCouchDao;
        this.catalogEventCouchDao = catalogEventCouchDao;
    }

    public ChannelEventDTO getEventCatalog(Long eventId, Long channelId) {
        ChannelEventData channelEventData = new ChannelEventData();
        channelEventData.setChannelEvent(catalogChannelEventCouchDao.get(channelId.toString(), eventId.toString()));
        if (channelEventData.getChannelEvent() == null) {
            return null;
        }
        EventData eventData = new EventData();
        eventData.setEvent(catalogEventCouchDao.get(eventId.toString()));
        if (eventData.getEvent() == null) {
            return null;
        }

        ChannelEventDTO channelEventDTO = new ChannelEventDTO();
        fillChannelEventInfo(channelEventData, channelEventDTO);
        fillEventInfo(eventData, channelEventDTO);

        return channelEventDTO;
    }

    public List<ChannelEventDTO> searchEventsCatalog(Integer channelId, EventCatalogFilter eventCatalogFilter) {
        BoolQuery.Builder query = buildEventQuery(channelId, eventCatalogFilter);

        SearchResponse<EventData> response = eventElasticDao.query(query.build()._toQuery(), DEFAULT_PAGE);
        return parseEventData(response);
    }

    private List<ChannelEventDTO> parseEventData(SearchResponse<EventData> response) {
        List<ChannelEventDTO> channelEventDTOs = new ArrayList<>();

        for (Hit<EventData> searchHit : response.hits().hits()) {
            try {
                EventData eventData = searchHit.source();

                ChannelEventDTO channelEventDTO = new ChannelEventDTO();
                fillEventInfo(eventData, channelEventDTO);

                InnerHitsResult innerChannelEvent = searchHit.innerHits().getOrDefault(KEY_CHANNEL_EVENT, null);
                for (Hit<JsonData> innerHit : innerChannelEvent.hits().hits()) {
                    ChannelEventData channelEvent = innerHit.source().to(ChannelEventData.class);
                    fillChannelEventInfo(channelEvent, channelEventDTO);
                }

                channelEventDTOs.add(channelEventDTO);
            } catch (Exception e) {
                throw new OneboxElasticSearchException("Error in elasticSearchObjectMapper", e);
            }
        }

        return channelEventDTOs;
    }

    private BoolQuery.Builder buildEventQuery(Integer channelId, EventCatalogFilter eventCatalogFilter) {

        BoolQuery.Builder eventQuery = QueryBuilders.bool();

        ESBuilder.addMustTerms(eventQuery, EventElasticProperty.ID, eventCatalogFilter.getEventIdList());
        ESBuilder.addMustTerm(eventQuery, EventElasticProperty.ENTITY_STATUS, EntityState.ACTIVE.getState());
        ESBuilder.addMustTerm(eventQuery, EventElasticProperty.STATUS, eventCatalogFilter.getEventStatus());
        ESBuilder.addMustTerm(eventQuery, EventElasticProperty.OPERATOR_STATUS, EntityState.ACTIVE.getState());
        ESBuilder.addMustTerms(eventQuery, EventElasticProperty.TYPE, eventCatalogFilter.getEventType());
        ESBuilder.addMustTerm(eventQuery, EventElasticProperty.TOUR_ID, eventCatalogFilter.getTourId());
        ESBuilder.addMustMatch(eventQuery, EventElasticProperty.CUSTOM_TAXONOMY_CODE, eventCatalogFilter.getCustomTaxonomyCode());

        ESBuilder.addNestedFilter(eventQuery, EventVenueElasticProperty.VENUES_PATH, EventVenueElasticProperty.VENUES_ID, eventCatalogFilter.getVenueId());

        if (eventCatalogFilter.getTaxonomyId() != null) {
            eventQuery.must(QueryBuilders.bool()
                    .should(QueryBuilders.term(term -> term.field(EventElasticProperty.TAXONOMY_ID.getProperty()).value(eventCatalogFilter.getTaxonomyId())))
                    .should(QueryBuilders.term(term -> term.field(EventElasticProperty.TAXONOMY_PARENT_ID.getProperty()).value(eventCatalogFilter.getTaxonomyId())))
                    .build()._toQuery());
        }
        ESBuilder.addMustTerms(eventQuery, EventElasticProperty.ATTRIBUTE_VALUE_ID, eventCatalogFilter.getEventAttributesValueIds());

        if (eventCatalogFilter.getBeginEventDate() != null && eventCatalogFilter.getEndEventDate() != null) {
            BoolQuery.Builder dateQuery = QueryBuilders.bool();
            if (eventCatalogFilter.getBeginEventDate() != null) {
                dateQuery.should(should -> should.range(range -> range
                        .field(EventElasticProperty.BEGIN_EVENT_DATE.getProperty())
                        .lte(JsonData.of(eventCatalogFilter.getBeginEventDate().getTime()))));
            }
            if (eventCatalogFilter.getEndEventDate() != null) {
                dateQuery.should(should -> should.range(range -> range
                        .field(EventElasticProperty.BEGIN_EVENT_DATE.getProperty())
                        .lte(JsonData.of(eventCatalogFilter.getEndEventDate().getTime()))));
            }
            eventQuery.must(dateQuery.build()._toQuery());

            dateQuery = QueryBuilders.bool();
            if (eventCatalogFilter.getBeginEventDate() != null) {
                dateQuery.should(should -> should.range(range -> range
                        .field(EventElasticProperty.END_EVENT_DATE.getProperty())
                        .gte(JsonData.of(eventCatalogFilter.getBeginEventDate().getTime()))));
            }
            if (eventCatalogFilter.getEndEventDate() != null) {
                dateQuery.should(should -> should.range(range -> range
                        .field(EventElasticProperty.END_EVENT_DATE.getProperty())
                        .gte(JsonData.of(eventCatalogFilter.getEndEventDate().getTime()))));
            }
            eventQuery.must(dateQuery.build()._toQuery());
        }

        BoolQuery.Builder channelEventQuery = QueryBuilders.bool()
                .must(QueryBuilders.term(term ->
                        term.field(ChannelEventElasticProperty.CHANNEL_ID.getProperty()).value(channelId)));
        if (eventCatalogFilter.getPublishChannelEvent() != null) {
            channelEventQuery.must(QueryBuilders.term(term ->
                    term.field(ChannelEventElasticProperty.PUBLISHED.getProperty()).value(eventCatalogFilter.getPublishChannelEvent())));
        }
        if (eventCatalogFilter.getCustomTaxonomyId() != null) {
            channelEventQuery.must(QueryBuilders.term(term ->
                    term.field(ChannelEventElasticProperty.CUSTOM_TAXONOMY_ID.getProperty()).value(eventCatalogFilter.getCustomTaxonomyId())));
        }
        Query channelEventChild = QueryBuilders.hasChild(child -> child
                .type(KEY_CHANNEL_EVENT)
                .query(channelEventQuery.build()._toQuery())
                .scoreMode(ChildScoreMode.None)
                .innerHits(InnerHits.of(hits -> hits.size(MAX_INNER_HITS_ITEMS))));
        eventQuery.must(channelEventChild);

        return eventQuery;
    }

    private void fillChannelEventInfo(ChannelEventData eventData, ChannelEventDTO channelEventDTO) {
        if (eventData != null) {
            ChannelEvent channelEvent = eventData.getChannelEvent();
            BeanUtils.copyProperties(channelEvent, channelEventDTO);
        }
    }

    private void fillEventInfo(EventData eventData, ChannelEventDTO channelEventDTO) {
        if (eventData != null && eventData.getEvent() != null) {
            Event event = eventData.getEvent();
            BeanUtils.copyProperties(event, channelEventDTO);

            channelEventDTO.setEventType(toInt(event.getEventType()));
            channelEventDTO.setEventSeasonType(toInt(event.getEventSeasonType()));
            channelEventDTO.setCurrency(event.getCurrency());
            channelEventDTO.setTypeExpirationBookingEvent(toInt(event.getTypeExpirationBookingEvent()));
            channelEventDTO.setTypeUnitsExpirationBookingEvent(toInt(event.getTypeUnitsExpirationBookingEvent()));
            channelEventDTO.setTypeLimitDateBookingEvent(toInt(event.getTypeLimitDateBookingEvent()));
            channelEventDTO.setTypeUnitsLimitBookingEvent(toInt(event.getTypeUnitsLimitBookingEvent()));
            channelEventDTO.setTypeLimitBookingEvent(toInt(event.getTypeLimitBookingEvent()));
            channelEventDTO.setEventAttributesId(event.getEventAttributesId());
            channelEventDTO.setEventAttributesValueId(event.getEventAttributesValueId());
            channelEventDTO.setVenueTemplates(buildVenueTemplates(event.getPrices()));

            List<Long> venueIds = ChannelEventConversionUtils.arrayOfUniqueIdsByFieldName(event.getVenues(), Venue::getId);
            List<Long> venueEntityIds = ChannelEventConversionUtils.arrayOfUniqueIdsByFieldName(event.getVenues(), Venue::getEntityId);
            channelEventDTO.setVenueId(venueIds);
            channelEventDTO.setVenueEntityId(venueEntityIds);

            Map<String, String> communicationElements = prepareCommunicationElements(event);
            channelEventDTO.setCommunicationElements(communicationElements);

            channelEventDTO.setMandatoryLogin(event.getMandatoryLogin());
            channelEventDTO.setCustomerMaxSeats(event.getCustomerMaxSeats());
            channelEventDTO.setInvoicePrefixId(event.getInvoicePrefixId());
            channelEventDTO.setInvoicePrefix(event.getInvoicePrefix());
            if (event.getPromoter() != null) {
                channelEventDTO.setPromoterId(event.getPromoter().getId());
            }
        }
    }

    private List<VenueTemplateDTO> buildVenueTemplates(List<VenueTemplatePrice> venueTemplatePrices) {
        if (venueTemplatePrices == null) {
            return new ArrayList<>();
        }
        return venueTemplatePrices.stream().map(entity -> {
            VenueTemplateDTO venueTemplateDTO = new VenueTemplateDTO();
            venueTemplateDTO.setId(entity.getId());
            venueTemplateDTO.setName(entity.getName());
            venueTemplateDTO.setPriceZones(buildPriceZones(entity.getPriceZones()));
            return venueTemplateDTO;
        }).collect(Collectors.toList());
    }

    private List<PriceZoneDTO> buildPriceZones(List<PriceZonePrice> priceZonePrices) {
        List<PriceZoneDTO> result = new ArrayList<>();
        if (priceZonePrices == null) {
            return result;
        }
        for (PriceZonePrice entity : priceZonePrices) {
            PriceZoneDTO priceZoneDTO = new PriceZoneDTO();
            priceZoneDTO.setCode(entity.getCode());
            priceZoneDTO.setColor(entity.getColor());
            priceZoneDTO.setDescription(entity.getDescription());
            priceZoneDTO.setId(entity.getId());
            priceZoneDTO.setPriority(entity.getPriority());
            priceZoneDTO.setTranslatedNames(entity.getTranslatedNames());
            priceZoneDTO.setTranslatedDescriptions(entity.getTranslatedDescriptions());
            result.add(priceZoneDTO);
        }
        return result;
    }

    private Integer toInt(Byte nullableByte) {
        return nullableByte != null ? nullableByte.intValue() : null;
    }

    private Map<String, String> prepareCommunicationElements(Event event) {
        Map<String, String> communicationElements = new HashMap<>();
        if (CollectionUtils.isNotEmpty(event.getCommunicationElements())) {
            for (EventCommunicationElement communicationElement : event.getCommunicationElements()) {
                communicationElements.put(GENERIC_ELEMENT + GENERIC_SEPARATOR
                        + communicationElement.getId() + GENERIC_SEPARATOR
                        + communicationElement.getTag() + GENERIC_SEPARATOR
                        + communicationElement.getLanguageCode() + GENERIC_SEPARATOR
                        + communicationElement.getPosition(), communicationElement.getValue());
            }
        }
        return communicationElements;
    }


}

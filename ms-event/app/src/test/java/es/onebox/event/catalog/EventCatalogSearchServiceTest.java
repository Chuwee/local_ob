package es.onebox.event.catalog;

import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dto.ChannelEventDTO;
import es.onebox.event.catalog.dto.filter.EventCatalogFilter;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.service.EventCatalogSearchService;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class EventCatalogSearchServiceTest {

    private static final Integer EVENT_ID = 89;
    private static final Integer CHANNEL_ID = 5;

    private static final String GENERIC_SEPARATOR = "|";
    private static final String GENERIC_ELEMENT = "elemComEvento";

    @Mock
    private EventElasticDao eventElasticDao;
    @Mock
    private CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    @Mock
    private ChannelEventElasticDao channelEventElasticDao;
    @Mock
    private CatalogEventCouchDao catalogEventCouchDao;

    private EventCatalogSearchService eventCatalogSearchService;
    private ObjectMapper elasticSearchObjectMapper;
    private JsonpMapper mapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        elasticSearchObjectMapper = JsonMapper.jacksonMapper();
        mapper = new JacksonJsonpMapper(elasticSearchObjectMapper);
        eventCatalogSearchService = new EventCatalogSearchService(eventElasticDao,
                catalogChannelEventCouchDao, catalogEventCouchDao);

        Mockito.when(eventElasticDao.getMapper()).thenReturn(elasticSearchObjectMapper);
        Mockito.when(eventElasticDao.getIndexName()).thenReturn("eventdata");
    }

    @Test
    public void getEventWithoutData() {
        SearchResponse response = buildResponse(List.of(), 0);
        Mockito.when(eventElasticDao.query(any(), any())).thenReturn(response);

        EventCatalogFilter filter = new EventCatalogFilter();
        filter.setEventIdList(List.of(EVENT_ID));
        List<ChannelEventDTO> eventCatalog = eventCatalogSearchService.searchEventsCatalog(CHANNEL_ID, filter);

        Assertions.assertEquals(0, eventCatalog.size());
    }

    @NotNull
    private static SearchResponse<EventData> buildResponse(List<Hit<EventData>> hits, int value) {
        HitsMetadata<EventData> hitsMetadata = HitsMetadata.of(hm -> hm
                .hits(hits)
                .total(TotalHits.of(th -> th.value(value).relation(TotalHitsRelation.Eq)))
        );
        return SearchResponse.of(r -> r.hits(hitsMetadata).took(1).timedOut(false)
                .shards(new ShardStatistics.Builder().successful(1).failed(0).total(1).build()));
    }

    @Test
    public void getEventData() {
        Date purchaseEventDate = getCalendar(1);
        Date beginEventDate = getCalendar(2);
        Date endEventDate = getCalendar(3);
        Date createEventDate = getCalendar(4);
        Date publishEventDate = getCalendar(5);
        Date modificationEventDate = getCalendar(6);
        Date statusModificationEventDate = getCalendar(7);
        Date limitBookingEventDate = getCalendar(8);
        Date beginBookingEventDate = getCalendar(9);
        Date endBookingEventDate = getCalendar(10);

        List<EventCommunicationElement> communicationElements = new ArrayList<>();
        communicationElements.add(new EventCommunicationElement());
        communicationElements.add(new EventCommunicationElement());
        communicationElements.add(new EventCommunicationElement());

        Event event = new Event();

        event.setEventName("event test");
        event.setEventDescription("test description");
        event.setEventType(EventType.NORMAL.getId().byteValue());
        event.setEventStatus(EventStatus.READY.getId());
        event.setPurchaseEventDate(purchaseEventDate);
        event.setPurchaseEventDateOlsonId("Europe/Madrid");
        event.setBeginEventDate(beginEventDate);
        event.setBeginEventDateOlsonId("Europe/Catalonia");
        event.setEndEventDate(endEventDate);
        event.setEndEventDateOlsonId("World/Catalonia");
        event.setCreateEventDate(createEventDate);
        event.setPublishEventDate(publishEventDate);
        event.setPublishEventDateOlsonId("US/Miami");
        event.setModificationEventDate(modificationEventDate);
        event.setStatusModificationEventDate(statusModificationEventDate);
        event.setEventDefaultLanguage("ca_ES");
        event.setEventLanguages(Arrays.asList("ca_ES", "es_ES", "en_US"));
        event.setPromoterRef("promoter ref");
        event.setChargePersonName("charge person name");
        event.setChargePersonSurname("charge person surname");
        event.setChargePersonEmail("charge person email");
        event.setChargePersonPhone("charge person phone");
        event.setChargePersonPosition("charge person position");
        event.setEventCapacity(333);
        event.setArchived(true);
        event.setEventSeasonType((byte) 2);
        event.setEnabledBookingEvent(true);
        event.setTypeExpirationBookingEvent((byte) 3);
        event.setUnitsExpirationBookingEvent(4);
        event.setTypeUnitsExpirationBookingEvent((byte) 5);
        event.setTypeLimitDateBookingEvent((byte) 6);
        event.setUnitsLimitBookingEvent(7);
        event.setTypeUnitsLimitBookingEvent((byte) 8);
        event.setTypeLimitBookingEvent((byte) 9);
        event.setLimitBookingEventDate(limitBookingEventDate);
        event.setBeginBookingEventDate(beginBookingEventDate);
        event.setBeginBookingEventDateOlsonId("US/Poma");
        event.setEndBookingEventDate(endBookingEventDate);
        event.setEndBookingEventDateOlsonId("US/Pera");
        event.setUseCommunicationElementsTour(true);
        event.setAdmissionAge("Adult");
        event.setCodeAdmissionAge("AGE");
        event.setSupraEvent(true);
        event.setGiftTicket(true);
        event.setEventAttributesId(Arrays.asList(11, 12, 13, 14));
        event.setEventAttributesValueId(Arrays.asList(15, 16, 17));
        event.setOperatorId(10);
        event.setOperatorStatus(11);
        event.setEntityId(12);
        event.setEntityName("Entity name");
        event.setEntityCorporateName("Corporate name");
        event.setEntityStatus(13);
        event.setEntityUsesExternalManagement(true);
        event.setEntityFiscalCode("fiscal code");
        event.setEntityAddress("address");
        event.setEntityCity("city");
        event.setEntityPostalCode("postal code");
        event.setEntityCountryId(14);
        event.setEntityCountryName("country name");
        event.setEntityCountryCode("country code");
        event.setEntityCountrySubdivisionId(15);
        event.setEntityCountrySubdivisionName("country subdivision name");
        event.setEntityCountrySubdivisionCode("country subdivision code");
        event.setTaxonomyId(16);
        event.setTaxonomyCode("taxonomy code");
        event.setTaxonomyDescription("taxonomy description");
        event.setTaxonomyParentId(17);
        event.setCustomTaxonomyId(18);
        event.setCustomTaxonomyDescription("custom taxonomy description");
        event.setCustomTaxonomyCode("custom taxonomy code");
        event.setOwnerUserId(19);
        event.setOwnerUserName("owner user name");
        event.setModifyUserId(20);
        event.setModifyUserName("modify user name");
        event.setTourId(21);
        event.setTourName("tour name");
        event.setTourPromoterRef("promoter ref");
        event.setTourEntityId(22);
        event.setTourOperatorId(23);
        event.setCommunicationElements(communicationElements);
        event.setPrices(new ArrayList<>());

        prepareMock(event, new ChannelEvent());

        ChannelEventDTO eventCatalog = searchEventCatalog();

        compareEvent(event, eventCatalog);
    }

    @Test
    public void getComElementsData() {
        List<EventCommunicationElement> communicationElements = new ArrayList<>();
        communicationElements.add(new EventCommunicationElement());
        communicationElements.add(new EventCommunicationElement());
        communicationElements.add(new EventCommunicationElement());
        communicationElements.add(new EventCommunicationElement());

        Event event = new Event();

        event.setCommunicationElements(communicationElements);
        event.setPrices(new ArrayList<>());

        prepareMock(event, new ChannelEvent());

        ChannelEventDTO eventCatalog = searchEventCatalog();

        compareEvent(event, eventCatalog);
    }

    @Test
    public void getBlankComElementsData() {
        List<EventCommunicationElement> communicationElements = new ArrayList<>();

        Event event = new Event();
        event.setCommunicationElements(communicationElements);
        event.setPrices(new ArrayList<>());

        prepareMock(event, new ChannelEvent());

        ChannelEventDTO eventCatalog = searchEventCatalog();

        compareEvent(event, eventCatalog);
    }

    @Test
    public void getNullComElementsData() {
        Event event = new Event();
        event.setPrices(new ArrayList<>());

        prepareMock(event, new ChannelEvent());

        ChannelEventDTO eventCatalog = searchEventCatalog();
        compareEvent(event, eventCatalog);
    }

    @Test
    public void getChannelEventData() {
        Date publishChannelEventDate = getCalendar(1);
        Date purchaseChannelEventDate = getCalendar(2);
        Date beginBookingChannelEventDate = getCalendar(3);
        Date endBookingChannelEventDate = getCalendar(4);
        Date endChannelEventDate = getCalendar(5);

        ChannelEvent channelEvent = new ChannelEvent();

        channelEvent.setChannelEntityId(2L);
        channelEvent.setChannelName("channnel name");
        channelEvent.setChannelEventStatus(3);
        channelEvent.setPublishChannelEventDate(publishChannelEventDate);
        channelEvent.setPurchaseChannelEventDate(purchaseChannelEventDate);
        channelEvent.setPublishChannelEvent(true);
        channelEvent.setPurchaseChannelEvent(true);
        channelEvent.setEndChannelEventDate(endChannelEventDate);
        channelEvent.setEventDates(true);
        channelEvent.setBeginBookingChannelEventDate(beginBookingChannelEventDate);
        channelEvent.setEndBookingChannelEventDate(endBookingChannelEventDate);
        channelEvent.setEnabledBookingChannelEvent(true);
        channelEvent.setCustomCategoryId(4);
        channelEvent.setCustomCategoryName("category name");
        channelEvent.setCustomCategoryCode("caregory code");

        Event event = new Event();
        event.setPrices(new ArrayList<>());
        prepareMock(event, channelEvent);

        ChannelEventDTO eventCatalog = searchEventCatalog();

        compareChannelEvent(channelEvent, eventCatalog);
    }

    @Test
    public void getEventCatalogWithCustomerMaxSeats() {
        Event event = new Event();
        event.setEventId(EVENT_ID.longValue());
        event.setCustomerMaxSeats(15);

        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setEventId(EVENT_ID.longValue());
        channelEvent.setChannelId(CHANNEL_ID.longValue());
        Mockito.when(catalogEventCouchDao.get(EVENT_ID.toString())).thenReturn(event);
        Mockito.when(catalogChannelEventCouchDao.get(CHANNEL_ID.toString(), EVENT_ID.toString())).thenReturn(channelEvent);

        ChannelEventDTO result = eventCatalogSearchService.getEventCatalog(EVENT_ID.longValue(), CHANNEL_ID.longValue());

        Assertions.assertNotNull(result);
        assertEquals(event.getEventId(), result.getEventId());
        assertEquals(event.getCustomerMaxSeats(), result.getCustomerMaxSeats());
    }

    private ChannelEventDTO searchEventCatalog() {
        EventCatalogFilter filter = new EventCatalogFilter();
        filter.setEventIdList(List.of(EVENT_ID));
        List<ChannelEventDTO> eventCatalog = eventCatalogSearchService.searchEventsCatalog(CHANNEL_ID, filter);

        Assertions.assertEquals(1, eventCatalog.size());
        return eventCatalog.get(0);
    }

    private Date getCalendar(int daysBefore) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, daysBefore * (-1));
        return date.getTime();
    }

    private void prepareMock(Event event, ChannelEvent channelEvent) {
        EventData eventData = new EventData();
        eventData.setEvent(event);
        event.setEventId(EVENT_ID.longValue());

        ChannelEventData channelEventData = new ChannelEventData();
        channelEventData.setChannelEvent(channelEvent);
        channelEvent.setEventId(EVENT_ID.longValue());
        channelEvent.setChannelId(CHANNEL_ID.longValue());

        Hit<EventData> hit = Hit.of(h -> h
                .source(eventData)
                .index(eventElasticDao.getIndexName())
                .innerHits("channelEvent", InnerHitsResult.of(r -> r
                        .hits(hm -> hm.hits(Hit.of(ih -> ih
                                        .source(JsonData.of(channelEventData))
                                        .index(eventElasticDao.getIndexName())))
                                .total(TotalHits.of(th -> th.value(1L).relation(TotalHitsRelation.Eq)))))
                ));

        SearchResponse<EventData> response = buildResponse(List.of(hit), 1);

        Mockito.when(eventElasticDao.query(any(), any())).thenReturn(response);


    }

    private void compareChannelEvent(ChannelEvent channelEvent, ChannelEventDTO eventCatalog) {
        assertEquals(eventCatalog.getChannelEventId(), channelEvent.getChannelEventId());
        assertEquals(eventCatalog.getChannelId(), channelEvent.getChannelId());
        assertEquals(eventCatalog.getChannelEntityId(), channelEvent.getChannelEntityId());
        assertEquals(eventCatalog.getChannelName(), channelEvent.getChannelName());
        assertEquals(eventCatalog.getChannelEventStatus(), channelEvent.getChannelEventStatus());
        assertEquals(eventCatalog.getPublishChannelEventDate(), channelEvent.getPublishChannelEventDate());
        assertEquals(eventCatalog.getPurchaseChannelEventDate(), channelEvent.getPurchaseChannelEventDate());
        assertEquals(eventCatalog.getPublishChannelEvent(), channelEvent.getPublishChannelEvent());
        assertEquals(eventCatalog.getPurchaseChannelEvent(), channelEvent.getPurchaseChannelEvent());
        assertEquals(eventCatalog.getPurchaseSecondaryMarketChannelEvent(), channelEvent.getPurchaseSecondaryMarketChannelEvent());
        assertEquals(eventCatalog.getEndChannelEventDate(), channelEvent.getEndChannelEventDate());
        assertEquals(eventCatalog.getEventDates(), channelEvent.getEventDates());
        assertEquals(eventCatalog.getBeginBookingChannelEventDate(), channelEvent.getBeginBookingChannelEventDate());
        assertEquals(eventCatalog.getEndBookingChannelEventDate(), channelEvent.getEndBookingChannelEventDate());
        assertEquals(eventCatalog.getEnabledBookingChannelEvent(), channelEvent.getEnabledBookingChannelEvent());
        assertEquals(eventCatalog.getCustomCategoryId(), channelEvent.getCustomCategoryId());
        assertEquals(eventCatalog.getCustomCategoryName(), channelEvent.getCustomCategoryName());
        assertEquals(eventCatalog.getCustomCategoryCode(), channelEvent.getCustomCategoryCode());
        assertEquals(eventCatalog.getAllowChannelPromotions(), channelEvent.getAllowChannelPromotions());
    }

    private Byte integerToByte(Integer value) {
        return value == null ? null : value.byteValue();
    }

    private void compareEvent(Event expected, ChannelEventDTO response) {
        assertEquals(expected.getEventId(), response.getEventId());
        assertEquals(expected.getEventName(), response.getEventName());
        assertEquals(expected.getEventDescription(), response.getEventDescription());
        assertEquals(expected.getEventType(), integerToByte(response.getEventType()));
        assertEquals(expected.getEventStatus(), response.getEventStatus());
        assertEquals(expected.getPurchaseEventDate(), response.getPurchaseEventDate());
        assertEquals(expected.getPurchaseEventDateOlsonId(), response.getPurchaseEventDateOlsonId());
        assertEquals(expected.getBeginEventDate(), response.getBeginEventDate());
        assertEquals(expected.getBeginEventDateOlsonId(), response.getBeginEventDateOlsonId());
        assertEquals(expected.getEndEventDate(), response.getEndEventDate());
        assertEquals(expected.getEndEventDateOlsonId(), response.getEndEventDateOlsonId());
        assertEquals(expected.getCreateEventDate(), response.getCreateEventDate());
        assertEquals(expected.getPublishEventDate(), response.getPublishEventDate());
        assertEquals(expected.getPublishEventDateOlsonId(), response.getPublishEventDateOlsonId());
        assertEquals(expected.getModificationEventDate(), response.getModificationEventDate());
        assertEquals(expected.getStatusModificationEventDate(), response.getStatusModificationEventDate());
        assertEquals(expected.getEventDefaultLanguage(), response.getEventDefaultLanguage());
        assertEquals(expected.getEventLanguages(), response.getEventLanguages());
        assertEquals(expected.getPromoterRef(), response.getPromoterRef());
        assertEquals(expected.getChargePersonName(), response.getChargePersonName());
        assertEquals(expected.getChargePersonSurname(), response.getChargePersonSurname());
        assertEquals(expected.getChargePersonEmail(), response.getChargePersonEmail());
        assertEquals(expected.getChargePersonPhone(), response.getChargePersonPhone());
        assertEquals(expected.getChargePersonPosition(), response.getChargePersonPosition());
        assertEquals(expected.getEventCapacity(), response.getEventCapacity());
        assertEquals(expected.getArchived(), response.getArchived());
        assertEquals(expected.getEventSeasonType(), integerToByte(response.getEventSeasonType()));
        assertEquals(expected.getEnabledBookingEvent(), response.getEnabledBookingEvent());
        assertEquals(expected.getTypeExpirationBookingEvent(), integerToByte(response.getTypeExpirationBookingEvent()));
        assertEquals(expected.getUnitsExpirationBookingEvent(), response.getUnitsExpirationBookingEvent());
        assertEquals(expected.getTypeUnitsExpirationBookingEvent(), integerToByte(response.getTypeUnitsExpirationBookingEvent()));
        assertEquals(expected.getTypeLimitDateBookingEvent(), integerToByte(response.getTypeLimitDateBookingEvent()));
        assertEquals(expected.getUnitsLimitBookingEvent(), response.getUnitsLimitBookingEvent());
        assertEquals(expected.getTypeUnitsLimitBookingEvent(), integerToByte(response.getTypeUnitsLimitBookingEvent()));
        assertEquals(expected.getTypeLimitBookingEvent(), integerToByte(response.getTypeLimitBookingEvent()));
        assertEquals(expected.getLimitBookingEventDate(), response.getLimitBookingEventDate());
        assertEquals(expected.getBeginBookingEventDate(), response.getBeginBookingEventDate());
        assertEquals(expected.getBeginBookingEventDateOlsonId(), response.getBeginBookingEventDateOlsonId());
        assertEquals(expected.getEndBookingEventDate(), response.getEndBookingEventDate());
        assertEquals(expected.getEndBookingEventDateOlsonId(), response.getEndBookingEventDateOlsonId());
        assertEquals(expected.getUseCommunicationElementsTour(), response.getUseCommunicationElementsTour());
        assertEquals(expected.getAdmissionAge(), response.getAdmissionAge());
        assertEquals(expected.getCodeAdmissionAge(), response.getCodeAdmissionAge());
        assertEquals(expected.getSupraEvent(), response.getSupraEvent());
        assertEquals(expected.getGiftTicket(), response.getGiftTicket());
        assertEquals(expected.getMultiVenue(), response.getMultiVenue());
        assertEquals(expected.getMultiLocation(), response.getMultiLocation());
        assertEquals(expected.getEventAttributesId(), response.getEventAttributesId());
        assertEquals(expected.getEventAttributesValueId(), response.getEventAttributesValueId());
        assertEquals(expected.getOperatorId(), response.getOperatorId());
        assertEquals(expected.getOperatorStatus(), response.getOperatorStatus());
        assertEquals(expected.getEntityId(), response.getEntityId());
        assertEquals(expected.getEntityName(), response.getEntityName());
        assertEquals(expected.getEntityCorporateName(), response.getEntityCorporateName());
        assertEquals(expected.getEntityStatus(), response.getEntityStatus());
        assertEquals(expected.getEntityUsesExternalManagement(), response.getEntityUsesExternalManagement());
        assertEquals(expected.getEntityFiscalCode(), response.getEntityFiscalCode());
        assertEquals(expected.getEntityAddress(), response.getEntityAddress());
        assertEquals(expected.getEntityCity(), response.getEntityCity());
        assertEquals(expected.getEntityPostalCode(), response.getEntityPostalCode());
        assertEquals(expected.getEntityCountryId(), response.getEntityCountryId());
        assertEquals(expected.getEntityCountryName(), response.getEntityCountryName());
        assertEquals(expected.getEntityCountryCode(), response.getEntityCountryCode());
        assertEquals(expected.getEntityCountrySubdivisionId(), response.getEntityCountrySubdivisionId());
        assertEquals(expected.getEntityCountrySubdivisionName(), response.getEntityCountrySubdivisionName());
        assertEquals(expected.getEntityCountrySubdivisionCode(), response.getEntityCountrySubdivisionCode());
        assertEquals(expected.getTaxonomyId(), response.getTaxonomyId());
        assertEquals(expected.getTaxonomyCode(), response.getTaxonomyCode());
        assertEquals(expected.getTaxonomyDescription(), response.getTaxonomyDescription());
        assertEquals(expected.getTaxonomyParentId(), response.getTaxonomyParentId());
        assertEquals(expected.getCustomTaxonomyId(), response.getCustomTaxonomyId());
        assertEquals(expected.getCustomTaxonomyDescription(), response.getCustomTaxonomyDescription());
        assertEquals(expected.getCustomTaxonomyCode(), response.getCustomTaxonomyCode());
        assertEquals(expected.getOwnerUserId(), response.getOwnerUserId());
        assertEquals(expected.getOwnerUserName(), response.getOwnerUserName());
        assertEquals(expected.getModifyUserId(), response.getModifyUserId());
        assertEquals(expected.getModifyUserName(), response.getModifyUserName());
        assertEquals(expected.getTourId(), response.getTourId());
        assertEquals(expected.getTourName(), response.getTourName());
        assertEquals(expected.getTourPromoterRef(), response.getTourPromoterRef());
        assertEquals(expected.getTourEntityId(), response.getTourEntityId());
        assertEquals(expected.getTourOperatorId(), response.getTourOperatorId());
        assertEquals(expected.getCustomerMaxSeats(), response.getCustomerMaxSeats());
        assertEquals(expected.getMandatoryLogin(), response.getMandatoryLogin());
        
        compareComElements(expected, response);
    }

    private void compareComElements(Event event, ChannelEventDTO eventCatalog) {
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

        assertEquals(eventCatalog.getCommunicationElements().entrySet(), communicationElements.entrySet());
    }

}

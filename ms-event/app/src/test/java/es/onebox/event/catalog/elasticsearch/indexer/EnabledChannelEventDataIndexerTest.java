package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.amqp.catalogupdate.CatalogUpdateQueueProducer;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class EnabledChannelEventDataIndexerTest {

    private static final Long EVENT_ID = 6874L;
    private static final Long CHANNEL_ID = 325L;
    private static final Long CHANNEL_EVENT_ID = 21L;
    private static final String CHANNEL_NAME = "ABC";
    private static final Long ENTITY_ID = 5L;
    private static final Long SESSION_ID_1 = 324620L;
    private static final Long SESSION_ID_2 = 324621L;
    private static final ChannelEventStatus CHANNEL_EVENT_STATUS = ChannelEventStatus.ACCEPTED;
    private static final Boolean PUBLISH_CHANNEL_EVENT = true;
    private static final Boolean PURCHASE_CHANNEL_EVENT = true;
    private static final Boolean EVENT_DATES = true;
    private static final String PUBLISH_CHANNEL_EVENT_DATE = "2019-05-01T00:00:00Z";
    private static final String PURCHASE_CHANNEL_EVENT_DATE = "2019-05-02T00:00:00Z";
    private static final String END_CHANNEL_EVENT_DATE = "2019-06-01T00:00:00Z";
    private static final String BEGIN_BOOKING_CHANNEL_EVENT_DATE = "2019-05-03T00:00:00Z";
    private static final String END_BOOKING_CHANNEL_EVENT_DATE = "2019-05-15T00:00:00Z";
    private static final Boolean ENABLED_BOOKING_CHANNEL_EVENT = true;
    private static final Integer CUSTOM_CATEGORY_ID = 41;
    private static final String CUSTOM_CATEGORY_NAME = "Category";
    private static final String CUSTOM_CATEGORY_CODE = "CAT-XXX";
    private static final Boolean CHANNEL_SESSION_FOR_SALE = true;
    private static final Boolean CHANNEL_SESSION_SOLD_OUT = false;
    private static final List<Long> CHANNEL_SESSION_PROMOTIONS = Arrays.asList(1L, 2L, 3L);
    private static final String CHANNEL_SESSION_PUBLISH_DATE = "2019-05-01T10:00:00Z";
    private static final String CHANNEL_SESSION_START_DATE = "2019-06-01T17:00:00Z";
    private static final String CHANNEL_SESSION_END_DATE = "2019-06-01T18:00:00Z";
    private static final String CHANNEL_SESSION_SALE_START_DATE = "2019-05-01T12:00:00Z";
    private static final String CHANNEL_SESSION_SALE_END_DATE = "2019-05-15T20:00:00Z";
    private static final String CHANNEL_SESSION_TIME_ZONE = "Pacific/Galapagos";
    private static final ChannelEventSurcharges SURCHARGES = new ChannelEventSurcharges();
    private static final Long VENUE_ID_1 = 663L;
    private static final Long VENUE_ID_2 = 114L;
    private static final String VENUE_MUNICIPALITY_1 = "London";
    private static final String VENUE_MUNICIPALITY_2 = "Tokyo";

    @Mock
    private ChannelEventElasticDao channelEventElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private CustomTaxonomyDao customTaxonomyDao;
    @Mock
    private EmailCommunicationElementDao emailCommunicationElementDao;
    @Mock
    private CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private CatalogUpdateQueueProducer catalogUpdateQueueProducer;
    @Mock
    private ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    @Mock
    private CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;

    @InjectMocks
    private ChannelEventDataIndexer channelEventDataIndexer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(localCacheRepository.cached(anyString(), anyInt(), any(), any(), any())).
                thenAnswer(i -> {
                    return ((Callable) i.getArguments()[3]).call();
                });
    }

    @Test
    void testIndexChannelEventWhenNoChannelEvent() {
        EventIndexationContext ctx = prepareEventContext();
        ctx.setChannelEvents(Collections.emptyList());
        channelEventDataIndexer.indexChannelEvents(ctx);
        validateNoDocumentsIndexed(ctx);
    }

    @Test
    void testIndexChannelEventWhenNoEventChannel() {
        EventIndexationContext ctx = prepareEventContext();
        ctx.setEventChannels(Collections.emptyList());
        channelEventDataIndexer.indexChannelEvents(ctx);
        validateNoDocumentsIndexed(ctx);
    }

    @Test
    void testIndexChannelEvent() {
        EventIndexationContext ctx = prepareEventContext();
        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(ctx.getDocumentsIndexed(ChannelSessionData.class)
                .stream().map(ChannelSessionData::getChannelSession).toList());
        channelEventDataIndexer.indexChannelEvents(ctx);
        validateDocumentsIndexed(ctx, Collections.singletonList(VENUE_ID_1), false, false);
    }

    @Test
    void testIndexChannelEventWhenMultiVenues() {
        EventIndexationContext ctx = prepareEventContext();
        Map<Long, Long> venuesBySession = new HashMap<>();
        venuesBySession.put(SESSION_ID_1, VENUE_ID_1);
        venuesBySession.put(SESSION_ID_2, VENUE_ID_2);
        List<VenueRecord> venues = new ArrayList<>();
        venues.add(prepareVenue(VENUE_ID_1, VENUE_MUNICIPALITY_1));
        venues.add(prepareVenue(VENUE_ID_2, VENUE_MUNICIPALITY_1));
        ctx.setVenues(venues);
        ctx.setVenuesBySession(venuesBySession);
        ctx.addDocumentIndexed(prepareChannelSessionData(SESSION_ID_1));
        ctx.addDocumentIndexed(prepareChannelSessionData(SESSION_ID_2));
        ctx.getAllSessions().addAll(prepareSessions(SESSION_ID_2));
        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(ctx.getDocumentsIndexed(ChannelSessionData.class)
                .stream().map(ChannelSessionData::getChannelSession).toList());

        channelEventDataIndexer.indexChannelEvents(ctx);
        validateDocumentsIndexed(ctx, Arrays.asList(VENUE_ID_1, VENUE_ID_2), true, false);
    }

    @Test
    void testIndexChannelEventWhenMultiLocations() {
        EventIndexationContext ctx = prepareEventContext();
        Map<Long, Long> venuesBySession = new HashMap<>();
        venuesBySession.put(SESSION_ID_1, VENUE_ID_1);
        venuesBySession.put(SESSION_ID_2, VENUE_ID_2);
        List<VenueRecord> venues = new ArrayList<>();
        venues.add(prepareVenue(VENUE_ID_1, VENUE_MUNICIPALITY_1));
        venues.add(prepareVenue(VENUE_ID_2, VENUE_MUNICIPALITY_2));
        ctx.setVenues(venues);
        ctx.setVenuesBySession(venuesBySession);
        ctx.addDocumentIndexed(prepareChannelSessionData(SESSION_ID_2));
        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(ctx.getDocumentsIndexed(ChannelSessionData.class)
                .stream().map(ChannelSessionData::getChannelSession).toList());
        channelEventDataIndexer.indexChannelEvents(ctx);
        validateDocumentsIndexed(ctx, Arrays.asList(VENUE_ID_1, VENUE_ID_2), true, true);
    }

    @Test
    void testIndexOccupationWhenNoSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(false, true);
        channelEventDataIndexer.indexOccupation(ctx);
        assertFalse(ctx.getDocumentsIndexed(ChannelEventData.class).get(0).getChannelEvent().getCatalogInfo().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(true, false);
        channelEventDataIndexer.indexOccupation(ctx);
        assertEquals(2, ctx.getNumDocumentsIndexed());
        assertTrue(ctx.getDocumentsIndexed(ChannelEventData.class).get(0).getChannelEvent().getCatalogInfo().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenSoldWithoutChanges() {
        OccupationIndexationContext ctx = prepareOccupationContext(true, true);
        channelEventDataIndexer.indexOccupation(ctx);
        assertEquals(0, ctx.getDocumentsIndexed(ChannelEventData.class).size());
    }

    private EventIndexationContext prepareEventContext() {
        EventIndexationContext ctx = new EventIndexationContext(prepareEvent());
        ctx.setChannelEvents(Collections.singletonList(prepareChannelEvent()));
        ctx.setEventChannels(Collections.singletonList(prepareEventChannel()));
        ctx.setVenues(Collections.singletonList(prepareVenue(VENUE_ID_1, VENUE_MUNICIPALITY_1)));
        ctx.setVenuesBySession(Collections.singletonMap(SESSION_ID_1, VENUE_ID_1));
        ctx.addDocumentIndexed(prepareChannelSessionData(SESSION_ID_1));

        Map<Long, ChannelEventSurcharges> channelSurcharges = new HashMap<>();
        channelSurcharges.put(CHANNEL_ID, SURCHARGES);
        ctx.setChannelSurcharges(channelSurcharges);
        ctx.setSessions(prepareSessions(SESSION_ID_1));
        ctx.setAllSessions(prepareSessions(SESSION_ID_1));
        ctx.setChannels(Map.of(CHANNEL_ID, prepareChannel()));
        ctx.setEventConfig(new EventConfig());

        when(customTaxonomyDao.getTaxonomyInfo(CUSTOM_CATEGORY_ID)).thenReturn(prepareTaxonomy());
        doNothing().when(catalogUpdateQueueProducer).sendMessage(Collections.singletonList(Mockito.anyLong()), Mockito.anyLong());

        return ctx;
    }

    private List<SessionForCatalogRecord> prepareSessions(Long sessionId) {
        SessionForCatalogRecord session = new SessionForCatalogRecord();
        session.setIdsesion(sessionId.intValue());
        session.setEstado(3);
        session.setPublicado((byte) 1);
        session.setIspreview(false);
        session.setFechainiciosesion(toTimestamp(CHANNEL_SESSION_START_DATE));
        session.setFechaventa(toTimestamp(END_CHANNEL_EVENT_DATE));
        session.setFechapublicacion(toTimestamp(CHANNEL_SESSION_PUBLISH_DATE));
        session.setFecharealfinsesion(toTimestamp(END_CHANNEL_EVENT_DATE));
        return new ArrayList<>(List.of(session));
    }

    private OccupationIndexationContext prepareOccupationContext(boolean sessionSoldOut, boolean eventSoldOut) {
        OccupationIndexationContext ctx = new OccupationIndexationContext(prepareEvent(), null);
        ChannelSessionData channelSessionData = prepareChannelSessionData(SESSION_ID_1);
        channelSessionData.getChannelSession().setSoldOut(sessionSoldOut);
        ChannelEventData eventDataIndexed = prepareChannelEventData();
        eventDataIndexed.getChannelEvent().getCatalogInfo().setSoldOut(eventSoldOut); //Force transition of status
        ctx.setChannelEvents(Collections.singletonList(eventDataIndexed));
        ctx.addDocumentIndexed(channelSessionData);

        when(channelSessionElasticDao.getOccupationFieldsByEventAndChannelId(EVENT_ID, CHANNEL_ID)).thenReturn(Collections.singletonList(channelSessionData));
        return ctx;
    }

    private CpanelEventoRecord prepareEvent() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID.intValue());
        event.setTipoevento(EventType.NORMAL.getId());
        event.setEstado(EventStatus.READY.getId());
        return event;
    }

    private CpanelCanalEventoRecord prepareChannelEvent() {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdevento(EVENT_ID.intValue());
        channelEvent.setIdcanal(CHANNEL_ID.intValue());
        channelEvent.setIdcanaleevento(CHANNEL_EVENT_ID.intValue());
        channelEvent.setEstadorelacion(CHANNEL_EVENT_STATUS.getId());
        channelEvent.setPublicado((byte) (PUBLISH_CHANNEL_EVENT ? 1 : 0));
        channelEvent.setEnventa((byte) (PURCHASE_CHANNEL_EVENT ? 1 : 0));
        channelEvent.setUsafechasevento((byte) (EVENT_DATES ? 1 : 0));
        channelEvent.setReservasactivas((byte) (ENABLED_BOOKING_CHANNEL_EVENT ? 1 : 0));
        channelEvent.setFechapublicacion(toTimestamp(PUBLISH_CHANNEL_EVENT_DATE));
        channelEvent.setFechaventa(toTimestamp(PURCHASE_CHANNEL_EVENT_DATE));
        channelEvent.setFechafin(toTimestamp(END_CHANNEL_EVENT_DATE));
        channelEvent.setFechainicioreserva(toTimestamp(BEGIN_BOOKING_CHANNEL_EVENT_DATE));
        channelEvent.setFechafinreserva(toTimestamp(END_BOOKING_CHANNEL_EVENT_DATE));
        channelEvent.setAllowchannelusealternativecharges(true);
        return channelEvent;
    }

    private EventChannelForCatalogRecord prepareEventChannel() {
        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdevento(EVENT_ID.intValue());
        eventChannel.setIdcanal(CHANNEL_ID.intValue());
        eventChannel.setTaxonomiapropia(CUSTOM_CATEGORY_ID);
        eventChannel.setAplicarrecargoscanalespecificos((byte) 1);
        return eventChannel;
    }

    private ChannelInfo prepareChannel() {
        return new ChannelInfo(CHANNEL_ID, CHANNEL_NAME, ENTITY_ID, ChannelSubtype.PORTAL_WEB.getIdSubtipo(), null);
    }

    private BaseTaxonomyDao.TaxonomyInfo prepareTaxonomy() {
        return new BaseTaxonomyDao.TaxonomyInfo(CUSTOM_CATEGORY_ID, null, CUSTOM_CATEGORY_CODE, CUSTOM_CATEGORY_NAME);
    }

    private ChannelSessionData prepareChannelSessionData(Long sessionId) {
        ChannelSessionData channelSessionData = new ChannelSessionData();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setSessionId(sessionId);
        channelSession.setChannelId(CHANNEL_ID);
        channelSession.setEventId(EVENT_ID);
        channelSession.setForSale(CHANNEL_SESSION_FOR_SALE);
        channelSession.setSoldOut(CHANNEL_SESSION_SOLD_OUT);
        channelSession.setPromotions(CHANNEL_SESSION_PROMOTIONS);
        channelSession.setTimeZone(CHANNEL_SESSION_TIME_ZONE);
        ChannelCatalogDates date = new ChannelCatalogDates();
        date.setStart(toTimestamp(CHANNEL_SESSION_START_DATE));
        date.setEnd(toTimestamp(CHANNEL_SESSION_END_DATE));
        date.setPublish(toTimestamp(CHANNEL_SESSION_PUBLISH_DATE));
        date.setSaleStart(toTimestamp(CHANNEL_SESSION_SALE_START_DATE));
        date.setSaleEnd(toTimestamp(CHANNEL_SESSION_SALE_END_DATE));
        channelSession.setDate(date);
        channelSessionData.setChannelSession(channelSession);
        return channelSessionData;
    }

    private ChannelEventData prepareChannelEventData() {
        ChannelEventData channelEventData = new ChannelEventData();
        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setEventId(EVENT_ID);
        channelEvent.setChannelId(CHANNEL_ID);
        ChannelCatalogEventInfo catalogInfo = new ChannelCatalogEventInfo();
        catalogInfo.setSoldOut(false);
        catalogInfo.setPrices(new PriceMatrix());
        channelEvent.setCatalogInfo(catalogInfo);
        channelEventData.setChannelEvent(channelEvent);
        return channelEventData;
    }

    private VenueRecord prepareVenue(Long id, String municipality) {
        VenueRecord venue = new VenueRecord();
        venue.setId(id);
        venue.setMunicipality(municipality);
        return venue;
    }

    private void validateNoDocumentsIndexed(BaseIndexationContext<?, ?> ctx) {
        List<ChannelEventData> documents = ctx.getDocumentsIndexed(ChannelEventData.class);
        assertNotNull(documents);
        assertTrue(documents.isEmpty());
    }

    private void validateDocumentsIndexed(EventIndexationContext ctx, List<Long> venueIds, boolean multiVenue, boolean multiLocation) {
        List<ChannelEventData> documents = ctx.getDocumentsIndexed(ChannelEventData.class);
        assertNotNull(documents);
        assertEquals(1, documents.size());
        validateDocumentIndexed(documents.get(0), venueIds, multiVenue, multiLocation);
    }

    private void validateDocumentIndexed(ChannelEventData channelEventData, List<Long> venueIds, boolean multiVenue, boolean multiLocation) {
        assertNotNull(channelEventData);
        assertEquals("channelEvent|" + CHANNEL_ID + "|" + EVENT_ID, channelEventData.getId());
        JoinField join = channelEventData.getJoin();
        assertNotNull(join);
        assertEquals("channelEvent", join.getName());
        assertEquals("event|" + EVENT_ID, join.getParent());
        validateChannelEvent(channelEventData.getChannelEvent(), venueIds, multiVenue, multiLocation);
    }

    private void validateChannelEvent(ChannelEvent channelEvent, List<Long> venueIds, boolean multiVenue, boolean multiLocation) {
        assertNotNull(channelEvent);
        assertEquals(EVENT_ID, channelEvent.getEventId());
        assertEquals(CHANNEL_ID, channelEvent.getChannelId());
        assertEquals(CHANNEL_EVENT_ID, channelEvent.getChannelEventId());
        assertEquals(ENTITY_ID, channelEvent.getChannelEntityId());
        assertEquals(CHANNEL_NAME, channelEvent.getChannelName());
        assertEquals(CHANNEL_EVENT_STATUS.getId(), channelEvent.getChannelEventStatus());
        assertEquals(PUBLISH_CHANNEL_EVENT, channelEvent.getPublishChannelEvent());
        assertEquals(PURCHASE_CHANNEL_EVENT, channelEvent.getPurchaseChannelEvent());
        assertEquals(EVENT_DATES, channelEvent.getEventDates());
        assertDate(PUBLISH_CHANNEL_EVENT_DATE, channelEvent.getPublishChannelEventDate());
        assertDate(PURCHASE_CHANNEL_EVENT_DATE, channelEvent.getPurchaseChannelEventDate());
        assertDate(END_CHANNEL_EVENT_DATE, channelEvent.getEndChannelEventDate());
        assertDate(BEGIN_BOOKING_CHANNEL_EVENT_DATE, channelEvent.getBeginBookingChannelEventDate());
        assertDate(END_BOOKING_CHANNEL_EVENT_DATE, channelEvent.getEndBookingChannelEventDate());
        assertEquals(ENABLED_BOOKING_CHANNEL_EVENT, channelEvent.getEnabledBookingChannelEvent());
        assertEquals(CUSTOM_CATEGORY_ID, channelEvent.getCustomCategoryId());
        assertEquals(CUSTOM_CATEGORY_NAME, channelEvent.getCustomCategoryName());
        assertEquals(CUSTOM_CATEGORY_CODE, channelEvent.getCustomCategoryCode());
        assertEquals(multiVenue, channelEvent.getMultiVenue());
        assertEquals(multiLocation, channelEvent.getMultiLocation());
        assertEquals(venueIds, channelEvent.getVenueIds());
        assertSame(SURCHARGES, channelEvent.getSurcharges());
        validateCatalogInfo(channelEvent.getCatalogInfo());
    }

    private void validateCatalogInfo(ChannelCatalogEventInfo catalogInfo) {
        assertNotNull(catalogInfo);
        assertEquals(CHANNEL_SESSION_FOR_SALE, catalogInfo.getForSale());
        assertEquals(CHANNEL_SESSION_SOLD_OUT, catalogInfo.getSoldOut());
        assertEquals(CHANNEL_SESSION_PROMOTIONS, catalogInfo.getPromotions());
        validateCatalogInfoDates(catalogInfo.getDate());
    }

    private void validateCatalogInfoDates(ChannelCatalogDatesWithTimeZones date) {
        assertNotNull(date);
        assertDate(CHANNEL_SESSION_PUBLISH_DATE, date.getPublish());
        assertDate(CHANNEL_SESSION_START_DATE, date.getStart());
        assertDate(CHANNEL_SESSION_END_DATE, date.getEnd());
        assertDate(CHANNEL_SESSION_SALE_START_DATE, date.getSaleStart());
        assertDate(CHANNEL_SESSION_SALE_END_DATE, date.getSaleEnd());
        assertEquals(CHANNEL_SESSION_TIME_ZONE, date.getPublishTimeZone());
        assertEquals(CHANNEL_SESSION_TIME_ZONE, date.getStartTimeZone());
        assertEquals(CHANNEL_SESSION_TIME_ZONE, date.getEndTimeZone());
        assertEquals(CHANNEL_SESSION_TIME_ZONE, date.getSaleStartTimeZone());
        assertEquals(CHANNEL_SESSION_TIME_ZONE, date.getSaleEndTimeZone());
    }

    private void assertDate(String expected, Date actual) {
        assertEquals(ZonedDateTime.parse(expected).toInstant(), actual.toInstant());
    }

    private Timestamp toTimestamp(String dateString) {
        return Timestamp.from(ZonedDateTime.parse(dateString).toInstant());
    }
}

package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dto.ChangeSeatAllowedSessions;
import es.onebox.event.catalog.dto.ChangeSeatAmountType;
import es.onebox.event.catalog.dto.ChangeSeatChangeType;
import es.onebox.event.catalog.dto.ChangeSeatRefundType;
import es.onebox.event.catalog.dto.ChangeSeatTickets;
import es.onebox.event.catalog.dto.ChangeSeatsConfig;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.entity.dto.BasicEntityDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityState;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerStatus;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.CountrySubdivisionDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.TourDao;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatExpiry;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.ChangeSeatNewTicketSelection;
import es.onebox.event.events.domain.eventconfig.ChangeSeatVoucherExpiry;
import es.onebox.event.events.domain.eventconfig.ChangeSeatPrice;
import es.onebox.event.events.domain.eventconfig.ChangeSeatRefund;
import es.onebox.event.events.domain.eventconfig.ReallocationChannel;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.event.user.dao.UserDao;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventDataIndexerTest {

    private static final int EVENT_ID = 877;
    private static final int ENTITY_ID = 811;
    private static final int VENUE_CONFIG_ID = 321;

    @Mock
    private EventElasticDao eventElasticDao;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private UserDao userDao;
    @Mock
    private CatalogEventCouchDao catalogEventCouchDao;
    @Mock
    private TourDao tourDao;
    @Mock
    private CustomTaxonomyDao customTaxonomyDao;
    @Mock
    private EntityDao entityDao;
    @Mock
    private CountrySubdivisionDao countrySubdivisionDao;
    @Mock
    private EventRelatedDataSupplier eventRelatedDataSupplier;
    @Mock
    private StaticDataContainer staticDataContainer;

    private EventDataIndexer eventDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.eventDataIndexer = new EventDataIndexer(
                eventElasticDao,
                catalogEventCouchDao,
                userDao,
                tourDao,
                customTaxonomyDao,
                entityDao,
                countrySubdivisionDao,
                entitiesRepository,
                eventRelatedDataSupplier,
                staticDataContainer,
                localCacheRepository
        );
    }

    @Test
    void testBuildEvent() {
        EventIndexationContext context = buildContext(EventIndexationType.FULL);
        Mockito.when(localCacheRepository.cached(eq(LocalCache.PRODUCER_KEY), anyInt(), any(), any(),
                eq(new Object[]{context.getEvent().getIdpromotor()}))).thenReturn(buildProducer());
        EventData eventData = eventDataIndexer.buildEvent(context);
        Assertions.assertNotNull(eventData);
    }

    @Test
    void testBuildEvent_withChangeSeatConfig() {
        EventIndexationContext context = buildContext(EventIndexationType.FULL);
        context.setEventConfig(buildEventConfig());
        context.getEventConfig().setEventChangeSeatConfig(buildEventChangeConfig());
        Mockito.when(localCacheRepository.cached(eq(LocalCache.PRODUCER_KEY), anyInt(), any(), any(),
                eq(new Object[]{context.getEvent().getIdpromotor()}))).thenReturn(buildProducer());
        EventData eventData = eventDataIndexer.buildEvent(context);

        ChangeSeatsConfig changeSeatConfig = eventData.getEvent().getChangeSeatConfig();
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getAllowChangeSeat(),
                changeSeatConfig.getAllowChangeSeat()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getEventChangeSeatExpiry().getTimeOffsetLimitAmount(),
                changeSeatConfig.getEventChangeSeatExpiry().getTimeOffsetLimitAmount()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getEventChangeSeatExpiry().getTimeOffsetLimitUnit(),
                changeSeatConfig.getEventChangeSeatExpiry().getTimeOffsetLimitUnit()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getChangeType(),
                changeSeatConfig.getChangeType()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getNewTicketSelection().getTickets(),
                changeSeatConfig.getNewTicketSelection().getTickets()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getNewTicketSelection().getAllowedSessions(),
                changeSeatConfig.getNewTicketSelection().getAllowedSessions()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getNewTicketSelection().getPrice().getType(),
                changeSeatConfig.getNewTicketSelection().getPrice().getType()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getNewTicketSelection().getPrice().getRefund().getType(),
                changeSeatConfig.getNewTicketSelection().getPrice().getRefund().getType()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().getEnabled(),
                changeSeatConfig.getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().getEnabled()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getReallocationChannel().getId(),
                changeSeatConfig.getReallocationChannel().getId()
        );
        Assertions.assertEquals(
                context.getEventConfig().getEventChangeSeatConfig().getReallocationChannel().getApplyToAllChannelTypes(),
                changeSeatConfig.getReallocationChannel().getApplyToAllChannelTypes()
        );
    }

    @Test
    void testIndexEvent_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        EventData oldValue = buildEventData();
        when(catalogEventCouchDao.get(anyString())).thenReturn(oldValue.getEvent());

        Assertions.assertEquals(oldValue.getEvent().getEventStatus(), EventStatus.IN_PROGRAMMING.getId());

        eventDataIndexer.indexEvent(context);

        verify(eventElasticDao, times(1)).upsert(
                argThat(argument ->
                {
                    Event newValue = argument.getEvent();
                    return newValue != null &&
                            newValue.getEventStatus().equals(EventStatus.READY.getId());
                }),
                any(), anyBoolean());
    }

    @Test
    void testIndexEvent_whenPartialComElements_shouldUpdateComElementsInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_COM_ELEMENTS);

        EventData oldValue = buildEventData();
        when(catalogEventCouchDao.get(anyString())).thenReturn(oldValue.getEvent());


        CpanelIdiomaComEventoRecord lang = new CpanelIdiomaComEventoRecord();
        lang.setIdidioma(1);
        when(eventRelatedDataSupplier.getCommunicationLanguages(eq(EVENT_ID))).thenReturn(List.of(lang));
        CpanelElementosComEventoRecord elem = new CpanelElementosComEventoRecord();
        elem.setIdelemento(1);
        elem.setIdioma(1);
        elem.setValor("test");
        when(eventRelatedDataSupplier.getCommunicationElements(eq(EVENT_ID))).thenReturn(List.of(elem));

        eventDataIndexer.indexEvent(context);

        verify(eventRelatedDataSupplier, times(0)).getRates(any());

        verify(eventElasticDao, times(1)).upsert(
                argThat(argument ->
                {
                    Event newValue = argument.getEvent();
                    return newValue != null &&
                            newValue.getCommunicationElements().size() == 1 &&
                            newValue.getCommunicationElements().get(0).getValue().equals("test");
                }),
                any(), anyBoolean());
    }

    private EventData buildEventData() {
        EventData eventData = new EventData();
        Event event = new Event();
        event.setEventId((long) EVENT_ID);
        event.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        event.setEntity(entity);
        eventData.setEvent(event);
        return eventData;
    }

    private EventChangeSeatConfig buildEventChangeConfig() {
        EventChangeSeatConfig eventChangeSeatConfig = new EventChangeSeatConfig();
        eventChangeSeatConfig.setAllowChangeSeat(true);
        eventChangeSeatConfig.setChangeType(ChangeSeatChangeType.ALL);
        eventChangeSeatConfig.setEventChangeSeatExpiry(buildEventChangeSeatExpiry());
        eventChangeSeatConfig.setNewTicketSelection(buildEventNewTicketSelection());
        eventChangeSeatConfig.setReallocationChannel(buildReallocationChannel());
        return eventChangeSeatConfig;
    }

    private EventChangeSeatExpiry buildEventChangeSeatExpiry() {
        EventChangeSeatExpiry eventChangeSeatExpiration = new EventChangeSeatExpiry();
        eventChangeSeatExpiration.setTimeOffsetLimitUnit(ObjectRandomizer.random(ChronoUnit.class));
        eventChangeSeatExpiration.setTimeOffsetLimitAmount(ObjectRandomizer.randomInteger());
        return eventChangeSeatExpiration;
    }

    private ChangeSeatNewTicketSelection buildEventNewTicketSelection() {
        ChangeSeatNewTicketSelection newTicketSelection = new ChangeSeatNewTicketSelection();
        newTicketSelection.setTickets(ChangeSeatTickets.ANY);
        newTicketSelection.setAllowedSessions(ChangeSeatAllowedSessions.ANY);
        ChangeSeatPrice price = new ChangeSeatPrice();
        price.setType(ChangeSeatAmountType.ANY);
        ChangeSeatRefund changeSeatRefund = new ChangeSeatRefund();
        changeSeatRefund.setType(ChangeSeatRefundType.VOUCHER);
        ChangeSeatVoucherExpiry changeSeatVoucherExpiry = new ChangeSeatVoucherExpiry();
        changeSeatVoucherExpiry.setEnabled(true);
        changeSeatRefund.setVoucherExpiry(changeSeatVoucherExpiry);
        price.setRefund(changeSeatRefund);
        newTicketSelection.setPrice(price);
        return newTicketSelection;
    }

    private ReallocationChannel buildReallocationChannel() {
        ReallocationChannel reallocationChannel = new ReallocationChannel();
        reallocationChannel.setId(1L);
        reallocationChannel.setApplyToAllChannelTypes(true);

        return reallocationChannel;
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext context = new EventIndexationContext(eventRecord, type);
        context.setEntity(buildEntity());
        context.setPrices(buildEventPrices());
        context.setVenueDescriptor(new HashMap<>());
        context.setVenues(new ArrayList<>());
        return context;
    }

    private EventConfig buildEventConfig() {
        EventConfig eventConfig = new EventConfig();
        eventConfig.setEventId(EVENT_ID);
        return eventConfig;
    }

    private List<EventPriceRecord> buildEventPrices() {
        EventPriceRecord eventPriceRecord = new EventPriceRecord();
        eventPriceRecord.setEventId(EVENT_ID);
        eventPriceRecord.setVenueConfigId(VENUE_CONFIG_ID);
        eventPriceRecord.setRateId(1);
        return List.of(eventPriceRecord);
    }

    private ProducerDTO buildProducer() {
        ProducerDTO producer = new ProducerDTO();
        producer.setId(ENTITY_ID);
        producer.setOperator(buildBasicEntity());
        producer.setStatus(ProducerStatus.ACTIVE);
        return producer;
    }

    private EntityDTO buildEntity() {
        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setState(EntityState.ACTIVE);
        entity.setOperator(entity);
        return entity;
    }

    private BasicEntityDTO buildBasicEntity() {
        BasicEntityDTO entity = new BasicEntityDTO();
        entity.setId(ENTITY_ID);
        entity.setStatus(EntityState.ACTIVE);
        return entity;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        event.setIdpromotor(ENTITY_ID);
        event.setEstado(EventStatus.READY.getId());
        event.setIdentidad(ENTITY_ID);
        return event;
    }
}

package es.onebox.event.events.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpService;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.services.CommonSurchargesService;
import es.onebox.event.common.services.CommonTicketTemplateService;
import es.onebox.event.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import es.onebox.event.datasources.ms.crm.repository.SubscriptionsRepository;
import es.onebox.event.datasources.ms.entity.dto.AccommodationsEntityConfig;
import es.onebox.event.datasources.ms.entity.dto.EntityConfigDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.events.amqp.eventnotification.ExternalEventConsumeNotificationService;
import es.onebox.event.events.amqp.whitelistgeneration.WhitelistGenerationService;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.AccommodationsConfigDTO;
import es.onebox.event.events.dto.AccommodationsVendor;
import es.onebox.event.events.dto.CreateEventRequestDTO;
import es.onebox.event.events.dto.DateDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventsDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.enums.TaxModeDTO;
import es.onebox.event.events.enums.TicketTemplateFormatModel;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.event.exception.MSEventNotFoundException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductSessionDeliveryPointDao;
import es.onebox.event.products.service.ProductService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.event.tickettemplates.dao.TicketTemplateDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private static final String ENTITY_NAME = "entity";

    private EventService eventService;

    @Mock
    private EventDao eventDao;

    @Mock
    private EventLanguageDao eventLanguageDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private RateDao rateDao;

    @Mock
    private WhitelistGenerationService whitelistGenerationService;

    @Mock
    private ExternalEventConsumeNotificationService externalEventConsumeNotificationService;

    @Mock
    private EventExternalService eventExternalService;

    @Mock
    private RefreshDataService refreshDataService;

    @Mock
    private SurchargeRangeDao surchargeRangeDao;

    @Mock
    private TicketTemplateDao ticketTemplateDao;

    @Mock
    private VenueTemplateDao venueTemplateDao;

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @Mock
    private TierDao tierDao;

    @Mock
    private TaxDao taxDao;

    @Mock
    private CommonSurchargesService commonSurchargesService;

    @Mock
    private EventConfigService eventConfigService;

    @Mock
    private MsTicketDatasource msTicketDatasource;

    @Mock
    private SubscriptionsRepository subscriptionsRepository;

    @Mock
    private EventChannelService eventChannelService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private CommonCommunicationElementService commonCommunicationElementService;

    @Mock
    private WebhookService webhookService;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private EventRateGroupService eventRateGroupService;

    @Mock
    private CommonRatesService commonRatesService;

    @Mock
    private ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService;

    @Mock
    ProductEventDao productEventDao;

    @Mock
    ProductSessionDao productSessionDao;

    @Mock
    ProductService productService;

    @Mock
    ProductEventDeliveryPointDao productEventDeliveryPointDao;

    @Mock
    ProductSessionDeliveryPointDao productSessionDeliveryPointDao;

    @Mock
    ChannelEventDao channelEventDao;

    @Mock
    AccessControlSystemsRepository accessControlSystemsRepository;

    @Mock
    IntDispatcherRepository intDispatcherRepository;

    @Captor
    private ArgumentCaptor<CpanelEventoRecord> captureCpanelEventoRecord;

    @Captor
    private ArgumentCaptor<List<Integer>> captureSessionIds;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        CommonTicketTemplateService commonTicketTemplateService = new CommonTicketTemplateService(ticketTemplateDao, msTicketDatasource);
        eventService = new EventService(eventDao,
                sessionDao,
                taxDao,
                rateDao,
                eventExternalService,
                eventLanguageDao,
                venueTemplateDao,
                eventConfigCouchDao,
                null,
                null,
                refreshDataService,
                whitelistGenerationService,
                externalEventConsumeNotificationService,
                null,
                tierDao,
                ordersRepository,
                commonTicketTemplateService,
                commonSurchargesService,
                eventConfigService,
                subscriptionsRepository,
                eventChannelService,
                commonCommunicationElementService,
                webhookService,
                null,
                entitiesRepository,
                eventRateGroupService,
                commonRatesService,
                channelSuggestionsCleanUpService,
                productEventDao,
                productSessionDao,
                productService,
                productEventDeliveryPointDao,
                productSessionDeliveryPointDao,
                channelEventDao,
                accessControlSystemsRepository,
                intDispatcherRepository
        );
    }

    @Test
    void findEventsTestOK() {

        Map<EventRecord, List<VenueRecord>> eventRecords = createEventArray();
        EventRecord eventRecord = eventRecords.keySet().iterator().next();


        when(eventDao.countByFilter(any(EventSearchFilter.class))).thenReturn(1L);
        when(eventDao.findEvents(any(EventSearchFilter.class))).thenReturn(eventRecords);

        EventsDTO dto = eventService.searchEvents(createEventFilter());

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getMetadata());
        Assertions.assertNotNull(dto.getData());
        Assertions.assertEquals(1, dto.getData().size());
        EventDTO eventDTO = dto.getData().get(0);
        Assertions.assertNotNull(eventDTO);
        Assertions.assertEquals(eventRecord.getIdevento().longValue(), eventDTO.getId().longValue());
        Assertions.assertNotNull(eventDTO.getType());
        Assertions.assertNotNull(eventDTO.getStatus());
        Assertions.assertNotNull(eventDTO.getName());
        Assertions.assertEquals(eventRecord.getIdentidad().longValue(), eventDTO.getEntityId().longValue());
        Assertions.assertEquals(eventRecord.getReferenciapromotor(), eventDTO.getPromoterReference());
        Assertions.assertNotNull(eventDTO.getDate());
        assertDate(eventDTO.getDate().getStart());
        assertDate(eventDTO.getDate().getEnd());
        Assertions.assertEquals(eventRecord.getEmailresponsable(), eventDTO.getContactPersonEmail());
        Assertions.assertEquals(eventRecord.getNombreresponsable(), eventDTO.getContactPersonName());
        Assertions.assertEquals(eventRecord.getApellidosresponsable(), eventDTO.getContactPersonSurname());
        Assertions.assertEquals(eventRecord.getTelefonoresponsable(), eventDTO.getContactPersonPhone());
        Assertions.assertEquals(eventRecord.getObjetivosobreentradas(), eventDTO.getSalesGoalTickets());
        Assertions.assertEquals(eventRecord.getObjetivosobreventas(), eventDTO.getSalesGoalRevenue());
        Assertions.assertNotNull(eventDTO.getTour());
        Assertions.assertEquals(eventRecord.getIdgira().longValue(), eventDTO.getTour().getId().longValue());
        Assertions.assertEquals(eventRecord.getTourName(), eventDTO.getTour().getName());
        Assertions.assertNotNull(eventDTO.getCategory());
        Assertions.assertEquals(eventRecord.getIdtaxonomia(), eventDTO.getCategory().getId());
        Assertions.assertEquals(eventRecord.getCategoryCode(), eventDTO.getCategory().getCode());
        Assertions.assertEquals(eventRecord.getCategoryDescription(), eventDTO.getCategory().getDescription());
        Assertions.assertNotNull(eventDTO.getCustomCategory());
        Assertions.assertEquals(eventRecord.getIdtaxonomiapropia(), eventDTO.getCustomCategory().getId());
        Assertions.assertEquals(eventRecord.getCustomCategoryRef(), eventDTO.getCustomCategory().getCode());
        Assertions.assertEquals(eventRecord.getCustomCategoryDescription(), eventDTO.getCustomCategory().getDescription());
        Assertions.assertNotNull(eventDTO.getProducer());
        Assertions.assertEquals(eventRecord.getIdpromotor().longValue(), eventDTO.getProducer().getId().longValue());
        Assertions.assertEquals(eventRecord.getPromoterName(), eventDTO.getProducer().getName());
    }


    @Test
    void findEventByEventIdTestOK() {
        when(eventDao.findEvent(anyLong())).thenReturn(createEventArray().entrySet().iterator().next());

        List<EventLanguageRecord> languages = getEventLanguages();
        when(eventLanguageDao.findByEventId(anyLong())).thenReturn(languages);

        EventDTO dto = eventService.getEvent(52L);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(52L, (long) dto.getId());
        Assertions.assertNotNull(dto.getType());
        Assertions.assertNotNull(dto.getStatus());
        Assertions.assertNotNull(dto.getName());
        Assertions.assertEquals(1L, (long) dto.getEntityId());
        Assertions.assertEquals(ENTITY_NAME, dto.getEntityName());
        Assertions.assertNotNull(dto.getDate());
        Assertions.assertEquals(languages.size(), dto.getLanguages().size());
        Assertions.assertNotNull(dto.getLanguages().get(1));
        Assertions.assertEquals(languages.get(0).getId(), dto.getLanguages().get(0).getId());
        Assertions.assertEquals(languages.get(0).getDefault(), dto.getLanguages().get(0).getDefault());
        Assertions.assertEquals(languages.get(1).getDefault(), dto.getLanguages().get(1).getDefault());
        Assertions.assertEquals(languages.get(2).getDefault(), dto.getLanguages().get(2).getDefault());
    }

    @Test
    void createEvent() {
        OneboxRestException e = new OneboxRestException(CoreErrorCode.NOT_FOUND, null, null);

        CpanelRangoRecord cpanelRangoRecord = new CpanelRangoRecord();
        cpanelRangoRecord.setNombrerango("A");
        cpanelRangoRecord.setValor(0d);
        cpanelRangoRecord.setRangomaximo(0d);
        cpanelRangoRecord.setRangominimo(0d);
        when(surchargeRangeDao.insert(any())).thenReturn(cpanelRangoRecord);

        CreateEventRequestDTO newEvent = new CreateEventRequestDTO();
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("event name is mandatory", e.getMessage(),
                "On null name an exception is thrown");

        newEvent = new CreateEventRequestDTO();
        newEvent.setName("");
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("event name is mandatory", e.getMessage(),
                "On empty name an exception is thrown");

        newEvent.setName("012345678901234567890123456789012345678901234567890");
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("event name length cannot be above 50 characters", e.getMessage(),
                "On long name an exception is thrown");

        newEvent.setName("a valid name");
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("valid entityId is mandatory", e.getMessage(),
                "On null categoryId an exception is thrown");

        newEvent.setEntityId(-1L);
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("valid entityId is mandatory", e.getMessage(),
                "On negative entityId an exception is thrown");

        newEvent.setEntityId(1L);
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }

        newEvent.setProducerId(-1L);
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("valid producerId is mandatory", e.getMessage(),
                "On negative entityId an exception is thrown");

        newEvent.setProducerId(1L);
        newEvent.setCategoryId(0);
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("valid categoryId is mandatory", e.getMessage(),
                "On zero categoryId an exception is thrown");

        newEvent.setCategoryId(1);
        newEvent.setType(EventType.NORMAL);
        when(eventDao.countByFilter(any())).thenReturn(1L);
        try {
            eventService.createEvent(newEvent);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("event name already used for entity", e.getMessage(),
                "On repeated name an exception is thrown");

        when(eventDao.countByFilter(any())).thenReturn(0L);

        int createdEventId = 1;
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setIdevento(createdEventId);

        when(eventDao.insert(any())).thenReturn(cpanelEventoRecord);

        List<TicketTemplateRecord> ticketTemplateRecords = new ArrayList<>();

        TicketTemplateRecord ticketTemplateRecord = new TicketTemplateRecord();
        ticketTemplateRecord.setIdplantilla(1);
        ticketTemplateRecord.setModelFormat(TicketTemplateFormatModel.STANDARD.getId());
        ticketTemplateRecords.add(ticketTemplateRecord);

        TicketTemplateRecord ticketTemplateRecordTicket = new TicketTemplateRecord();
        ticketTemplateRecordTicket.setIdplantilla(2);
        ticketTemplateRecordTicket.setModelFormat(TicketTemplateFormatModel.TICKET.getId());
        ticketTemplateRecords.add(ticketTemplateRecordTicket);

        when(ticketTemplateDao.getDefaultTemplates(any())).thenReturn(ticketTemplateRecords);

        Long returnedEventId = eventService.createEvent(newEvent);

        verify(eventDao).insert(captureCpanelEventoRecord.capture());

        assertEquals(createdEventId, returnedEventId.intValue(), "Returned eventId matches created eventId");

        assertEquals(ticketTemplateRecord.getIdplantilla(), captureCpanelEventoRecord.getValue().getIdplantillaticket(), "Assign default template error");
        assertEquals(ticketTemplateRecordTicket.getIdplantilla(), captureCpanelEventoRecord.getValue().getIdplantillatickettaquilla(), "Assign default template error");
    }

    private void assertDate(DateDTO date) {
        Assertions.assertNotNull(date);
        Assertions.assertNotNull(date.getTimeZone());
        Assertions.assertNotNull(date.getTimeZone().getOlsonId());
        Assertions.assertNotNull(date.getTimeZone().getName());
        Assertions.assertNotNull(date.getTimeZone().getOffset());
    }

    private Map<EventRecord, List<VenueRecord>> createEventArray() {
        Map<EventRecord, List<VenueRecord>> events = new HashMap<>();
        events.put(createEventMapper(), null);
        return events;
    }

    private EventRecord createEventMapper() {
        EventRecord event = new EventRecord();

        event.setIdevento(52);
        event.setTipoevento(EventType.NORMAL.getId());
        event.setEstado(EventStatus.READY.getId());
        event.setNombre("Event");
        event.setIdentidad(1);
        event.setOperatorId(1);
        event.setEntityName(ENTITY_NAME);
        event.setReferenciapromotor(random(String.class));
        event.setFechainicio(Timestamp.valueOf("2018-10-01 10:00:00"));
        event.setFechafin(Timestamp.valueOf("2018-10-01 11:00:00"));
        event.setFechainiciotz(1);
        event.setFechafintz(1);
        event.setStartDateTZ("Europe/Berlin");
        event.setEndDateTZ("Europe/Berlin");
        event.setStartDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        event.setEndDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        event.setStartDateTZOffset(60);
        event.setEndDateTZOffset(60);
        event.setEmailresponsable(random(String.class));
        event.setNombreresponsable(random(String.class));
        event.setApellidosresponsable(random(String.class));
        event.setTelefonoresponsable(random(String.class));
        event.setObjetivosobreentradas(random(Integer.class));
        event.setObjetivosobreventas(random(Double.class));
        event.setIdpromotor(random(Integer.class));
        event.setPromoterName(random(String.class));
        event.setIdtaxonomia(Math.abs(random(Integer.class)));
        event.setCategoryCode(random(String.class));
        event.setCategoryDescription(random(String.class));
        event.setIdtaxonomiapropia(Math.abs(random(Integer.class)));
        event.setCustomCategoryRef(random(String.class));
        event.setCustomCategoryDescription(random(String.class));
        event.setIdgira(random(Integer.class));
        event.setTourName(random(String.class));
        event.setTipoabono(SessionPackType.DISABLED.getId());
        return event;
    }

    private List<EventLanguageRecord> getEventLanguages() {
        List<EventLanguageRecord> result = new ArrayList<>();
        result.add(getEventLanguageRecord(1L, "es_ES", false));
        result.add(getEventLanguageRecord(2L, "ca_ES", true));
        result.add(getEventLanguageRecord(3L, "en_US", false));
        return result;
    }

    private EventLanguageRecord getEventLanguageRecord(Long id, String code, Boolean isDefault) {
        EventLanguageRecord resultItem = new EventLanguageRecord();
        resultItem.setId(id);
        resultItem.setCode(code);
        resultItem.setDefault(isDefault);
        return resultItem;
    }

    private EventSearchFilter createEventFilter() {
        EventSearchFilter filter = new EventSearchFilter();

        filter.setVenueConfigId(110L);
        filter.setVenueId(Arrays.asList(10L));
        filter.setEntityId(10L);
        filter.setStatus(createStatusArray());
        filter.setType(createTypeArray());

        FilterWithOperator<ZonedDateTime> startFilter = new FilterWithOperator<>();
        startFilter.setValue(ZonedDateTime.of(2018, 10, 1, 10, 0, 0, 0, ZoneOffset.UTC));

        FilterWithOperator<ZonedDateTime> endFilter = new FilterWithOperator<>();
        endFilter.setOperator(Operator.LESS_THAN_OR_EQUALS);
        endFilter.setValue(ZonedDateTime.of(2018, 10, 1, 11, 0, 0, 0, ZoneOffset.UTC));

        filter.setStartDate(Collections.singletonList(startFilter));
        filter.setEndDate(Collections.singletonList(endFilter));
        filter.setLimit(100L);
        filter.setOffset(0L);

        return filter;
    }

    private List<EventStatus> createStatusArray() {
        List<EventStatus> statusList = new ArrayList<>();
        statusList.add(EventStatus.READY);
        return statusList;
    }

    private List<EventType> createTypeArray() {
        List<EventType> typeList = new ArrayList<>();
        typeList.add(EventType.ACTIVITY);
        return typeList;
    }

    @Test
    void updateEvent() {

        when(eventDao.findEvent(0L)).thenThrow(new EntityNotFoundException(""));

        Long eventId;
        OneboxRestException e = new OneboxRestException(CoreErrorCode.NOT_FOUND, null, null);
        UpdateEventRequestDTO event = new UpdateEventRequestDTO();
        event.setId(0L);
        try {
            eventService.updateEvent(event);

        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("Event not found for id 0", e.getMessage(),
                "On not found an exception is thrown");

        EventRecord eventRecord1 = createEventMapper();
        eventRecord1.setUsetieredpricing((byte) 1);
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigId(2L);
        Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord1, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);

        eventId = 1L;
        String eventName = "012345678901234567890123456789012345678901234567890"; // len 51
        try {
            event = new UpdateEventRequestDTO();
            event.setId(eventId);
            event.setName(eventName);

            eventService.updateEvent(event);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("event name length cannot be above 50 characters", e.getMessage(),
                "On long name an exception is thrown");
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(Math.toIntExact(eventId));
        eventRecord.setIdentidad(1);

        doReturn(eventVenueRecord).when(eventDao).findEvent(any());
        doReturn(eventRecord).when(eventDao).update(any());
        doReturn(1L).when(tierDao).countByEventId(anyInt(), isNull());
        event.setName("VALID NAME");
        eventRecord.setUsetieredpricing((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        event.setUseTieredPricing(false);
        eventRecord.setFechaventa(Timestamp.from(Instant.EPOCH));
        eventRecord.setFechafin(Timestamp.from(new Date().toInstant()));
        try {
            eventService.updateEvent(event);
            fail("Try to deactivate useTieredPricing should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("Once active, use_tiered_pricing cannot be deactivated", ex.getMessage());
        }

        when(rateDao.countByEventId(anyInt())).thenReturn(2L);
        doReturn(0L).when(tierDao).countByEventId(anyInt(), anyInt());
        eventRecord1.setUsetieredpricing((byte) 0);
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord1, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);
        event.setUseTieredPricing(true);
        try {
            eventService.updateEvent(event);
            fail("Try to set useTieredPricing with more than one rate should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("Event must have exactly one rate to perform this action", ex.getMessage());
        }
        when(rateDao.countByEventId(eventId.intValue())).thenReturn(1L);
        when(sessionDao.countByFilter(any())).thenReturn(1L);
        try {
            eventService.updateEvent(event);
            fail("Try to change useTieredPricing with sessions already created should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("Event can't have any session to perform this action", ex.getMessage());
        }
        when(sessionDao.countByFilter(any())).thenReturn(0L);
        when(venueTemplateDao.countActiveGraphicalVenueTemplates(eventId.intValue())).thenReturn(1L);
        try {
            eventService.updateEvent(event);
            fail("Try to change useTieredPricing with graphical venue should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("Cannot use tiers with a graphical venue", ex.getMessage());
        }
        when(venueTemplateDao.countActiveGraphicalVenueTemplates(eventId.intValue())).thenReturn(0L);
        event.setStatus(EventStatus.READY);
        eventRecord1.setUsetieredpricing((byte) 1);
        eventRecord1.setEstado(EventStatus.IN_PROGRESS.getId());
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord1, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);

        TierRecord tierRecord = new TierRecord();
        tierRecord.setVenueTemplateId(1);
        tierRecord.setIdzona(1);
        tierRecord.setVenta((byte) 1);
        tierRecord.setFechaInicio(new Timestamp(10000));
        tierRecord.setCondicion(0);
        TierRecord anotherTierRecord = new TierRecord();
        anotherTierRecord.setVenueTemplateId(1);
        anotherTierRecord.setIdzona(1);
        anotherTierRecord.setVenta((byte) 1);
        anotherTierRecord.setFechaInicio(new Timestamp(20000));
        anotherTierRecord.setCondicion(0);
        List<TierRecord> tiers = Arrays.asList(tierRecord, anotherTierRecord);
        when(tierDao.findByEventId(anyInt(), isNull(), isNull(), isNull())).thenReturn(tiers);

        try {
            eventService.updateEvent(event);
            fail("Set event to ready without tax mode should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("Invalid event tax mode", ex.getMessage());
        }

        eventRecord1.setTaxmode(TaxModeDTO.INCLUDED.getId());

        try {
            eventService.updateEvent(event);
            fail("Set event to ready without event dates should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("In order to set to READY a tiered event, event dates are needed", ex.getMessage());
        }
        anotherTierRecord.setFechaInicio(new Timestamp(20000));
        eventRecord1.setFechaventa(new Timestamp(15000));
        eventRecord1.setFechafin(new Timestamp(17000));
        eventRecord1.setEstado(EventStatus.IN_PROGRESS.getId());
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord1, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);
        eventRecord1.setFechafin(new Timestamp(30000));
        eventRecord1.setFechafin(anotherTierRecord.getFechaInicio());
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord1, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);
        tierRecord.setVenta((byte) 0);
        try {
            eventService.updateEvent(event);
            fail("Set event to ready when there is a gap with no on sale tiers should throw exception");
        } catch (OneboxRestException ex) {
            assertEquals("At least one price type must have a tier on sale at any point in the event lifespan", ex.getMessage());
        }

        event.setTaxMode(TaxModeDTO.ON_TOP);
        eventRecord1.setTaxmode(null);
        eventRecord1.setEstado(EventStatus.READY.getId());

        try {
            eventService.updateEvent(event);
            fail("Tax mode cannot be updated when an event is ready");
        } catch (OneboxRestException ex) {
            assertEquals("Tax mode cannot be updated when event is ready", ex.getMessage());
        }

        event.setTaxMode(null);

        tierRecord.setVenta((byte) 1);
        eventService.updateEvent(event);
    }

    @Test
    void updateEventInvalidSessionPack() {
        UpdateEventRequestDTO request = new UpdateEventRequestDTO();
        request.setId(1L);
        request.setSessionPackType(SessionPackType.RESTRICTED);

        EventRecord eventRecord = createEventMapper();
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigId(2L);
        Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);
        CpanelSesionRecord session = new CpanelSesionRecord();
        session.setEsabono((byte) 1);
        when(sessionDao.countByFilter(any())).thenReturn(1L);

        try {
            eventService.updateEvent(request);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_ALREADY_HAS_SESSION_PACKS.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void updateEventAccommodationsConfig_validationFails() {
        UpdateEventRequestDTO request = new UpdateEventRequestDTO();
        request.setId(1L);
        request.setAccommodationsConfig(new AccommodationsConfigDTO());
        request.getAccommodationsConfig().setEnabled(true);
        EventRecord eventRecord = createEventMapper();
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setVenueConfigId(2L);
        Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Arrays.asList(venueRecord));
        when(eventDao.findEvent(1L)).thenReturn(eventVenueRecord);

        try {
            eventService.updateEvent(request);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED.getErrorCode(), e.getErrorCode());
        }

        request.getAccommodationsConfig().setVendor(AccommodationsVendor.CLOSER_2_EVENT);
        request.getAccommodationsConfig().setValue("https://vendorUrl.cat/widgetId=mywidgetid");

        EntityConfigDTO entityConfig = new EntityConfigDTO();
        entityConfig.setAccommodationsConfig(new AccommodationsEntityConfig());
        entityConfig.getAccommodationsConfig().setEnabled(false);

        when(eventDao.getById(anyInt())).thenReturn(eventRecord);
        when(entitiesRepository.getEntityConfig(anyInt())).thenReturn(entityConfig);

        try {
            eventService.updateEvent(request);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.ACCOMMODATIONS_CONFIG_NOT_ENABLED_BY_ENTITY.getErrorCode(), e.getErrorCode());
        }

        entityConfig.getAccommodationsConfig().setEnabled(true);
        entityConfig.getAccommodationsConfig().setAllowedVendors(
                List.of(es.onebox.event.datasources.ms.entity.dto.AccommodationsVendor.CUSTOM)
        );
        try {
            eventService.updateEvent(request);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.ACCOMMODATIONS_VENDOR_NOT_ALLOWED_BY_ENTITY.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void postUpdateEventChangeToReady() {
        Long eventId = random(Long.class);

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.PLANNED);

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.READY);

        Instant yesterday = Instant.now().plus(-2, ChronoUnit.DAYS);
        Instant today = Instant.now();
        when(sessionDao.findFlatSessions(any()))
                .thenReturn(Arrays.asList(
                        getCpanelSesionRecord(1, today, SessionStatus.READY),
                        getCpanelSesionRecord(2, yesterday, SessionStatus.READY),
                        getCpanelSesionRecord(3, today, SessionStatus.PLANNED)));

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);

        verify(whitelistGenerationService).generateWhiteList(captureSessionIds.capture());
        verify(webhookService, times(1)).sendEventNotification(anyLong(), any());

        List<Integer> sessionsIds = captureSessionIds.getValue();

        Assertions.assertEquals(sessionsIds.size(), 1, "We expect only one session");
        Assertions.assertTrue(sessionsIds.contains(1), "We expect our session in the list to process");
    }

    @Test
    void postUpdateEventChangeToReady_shouldPublishEventToExternalProviders() {
        Long eventId = 12345L;
        Long entityId = 678L;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.IN_PROGRAMMING);
        oldEvent.setEntityId(entityId);
        oldEvent.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.READY);

        Instant today = Instant.now();
        when(sessionDao.findFlatSessions(any()))
                .thenReturn(Collections.singletonList(
                        getCpanelSesionRecord(1, today, SessionStatus.READY)));

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);

        verify(intDispatcherRepository, times(1)).publishEvent(entityId, eventId);
    }

    @Test
    void postUpdateEventChangeToReady_shouldNotPublishIfStatusNotChangingToReady() {
        Long eventId = 12345L;
        Long entityId = 678L;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.IN_PROGRAMMING);
        oldEvent.setEntityId(entityId);

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.IN_PROGRAMMING);

        when(sessionDao.findFlatSessions(any())).thenReturn(Collections.emptyList());

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);

        verify(intDispatcherRepository, times(0)).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateEventChangeToReady_shouldHandlePublishEventError() {
        Long eventId = 12345L;
        Long entityId = 678L;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.PLANNED);
        oldEvent.setEntityId(entityId);
        oldEvent.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.READY);

        Instant today = Instant.now();
        when(sessionDao.findFlatSessions(any()))
                .thenReturn(Collections.singletonList(
                        getCpanelSesionRecord(1, today, SessionStatus.READY)));

        doThrow(new RuntimeException("External service unavailable"))
                .when(intDispatcherRepository).publishEvent(anyLong(), anyLong());

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);
        
        // Verify that whitelist generation still happened
        verify(whitelistGenerationService).generateWhiteList(any());
    }

    @Test
    void postUpdateEventChangeToReady_shouldNotPublishIfNoInventoryProvider() {
        Long eventId = 12345L;
        Long entityId = 678L;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.IN_PROGRAMMING);
        oldEvent.setEntityId(entityId);
        // No inventory provider set

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.READY);

        Instant today = Instant.now();
        when(sessionDao.findFlatSessions(any()))
                .thenReturn(Collections.singletonList(
                        getCpanelSesionRecord(1, today, SessionStatus.READY)));

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);

        verify(intDispatcherRepository, times(0)).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateEventChangeToReady_shouldNotPublishIfDifferentProvider() {
        Long eventId = 12345L;
        Long entityId = 678L;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setStatus(EventStatus.IN_PROGRAMMING);
        oldEvent.setEntityId(entityId);
        oldEvent.setInventoryProvider(Provider.SGA); // Different provider

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setStatus(EventStatus.READY);

        Instant today = Instant.now();
        when(sessionDao.findFlatSessions(any()))
                .thenReturn(Collections.singletonList(
                        getCpanelSesionRecord(1, today, SessionStatus.READY)));

        eventService.postUpdateEvent(eventId, oldEvent, newEvent);

        verify(intDispatcherRepository, times(0)).publishEvent(anyLong(), anyLong());
    }

    @Test
    void getTaxesBySession() {
        CpanelImpuestoRecord tax = new CpanelImpuestoRecord();
        tax.setIdimpuesto(4);
        tax.setIdoperadora(1);
        tax.setNombre("Sin Impuesto");
        tax.setDescripcion("Sin Impuesto");
        tax.setValor(0.0);
        tax.setDefecto(null);

        when(taxDao.getTicketTaxBySession(anyLong(), anyLong())).thenReturn(tax);
        when(taxDao.getChargesTaxBySession(anyLong(), anyLong())).thenReturn(tax);

        List<SessionTaxDTO> taxes = eventService.getTaxesBySession(3066L, 182160L);

        Assert.notNull(taxes, "Null result");
        Assert.notEmpty(taxes, "Empty result");
        assertEquals(taxes.get(0).getId(), tax.getIdimpuesto().longValue());

        assertEquals(taxes.get(0).getType(), SessionTaxDTO.SessionTaxType.TICKET_TAX.name());
        assertEquals(taxes.get(1).getType(), SessionTaxDTO.SessionTaxType.CHARGES_TAX.name());
    }

    @Test
    void getTaxesBySessionNotFound() {
        when(taxDao.getTicketTaxBySession(anyLong(), anyLong())).thenThrow(new EmptyResultDataAccessException(1));
        when(taxDao.getChargesTaxBySession(anyLong(), anyLong())).thenThrow(new EmptyResultDataAccessException(1));

        Assertions.assertThrows(MSEventNotFoundException.class, () ->
                eventService.getTaxesBySession(3066L, 182160L));
    }

    private CpanelSesionRecord getCpanelSesionRecord(int sessionId, Instant instant, SessionStatus sessionStatus) {
        CpanelSesionRecord cpanelSesionRecord = new CpanelSesionRecord();
        cpanelSesionRecord.setIdsesion(sessionId);
        cpanelSesionRecord.setFechainiciosesion(Timestamp.from(instant));
        cpanelSesionRecord.setEstado(sessionStatus.getId());
        return cpanelSesionRecord;
    }

}

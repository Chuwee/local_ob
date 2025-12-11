package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.services.CommonTicketTemplateService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.eventremove.EventRemoveService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.DateDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.VenueStatusDTO;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.events.service.EventExternalService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.seasontickets.dao.record.SessionCapacityGenerationStatusRecord;
import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.event.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.MaxBuyingLimitDTO;
import es.onebox.event.seasontickets.dto.SearchSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketsDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketStatusRequestDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class SeasonTicketServiceTest {
    private static final String ENTITY_NAME = "entity";

    @InjectMocks
    private SeasonTicketService seasonTicketService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private SeasonTicketEventDao seasonTicketEventDao;

    @Mock
    private EventLanguageDao eventLanguageDao;

    @Mock
    private SeasonTicketSessionDao seasonTicketSessionDao;

    @Mock
    private SeasonTicketSurchargesService seasonTicketSurchargesService;

    @Mock
    private CommonRatesService commonRatesService;

    @Mock
    private StaticDataContainer staticDataContainer;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private SeasonTicketServiceHelper helper;

    @Mock
    private VenueConfigDao venueConfigDao;

    @Mock
    private VenueTemplateDao venueTemplateDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private EventRemoveService eventRemoveService;

    @Mock
    private EventSecondaryMarketConfigService eventSecondaryMarketConfigService;

    @Mock
    private CommonTicketTemplateService commonTicketTemplateService;

    @Mock
    private RefreshDataService refreshDataService;

    @Mock
    private SeasonSessionDao seasonSessionDao;

    @Mock
    private SeasonTicketDao seasonTicketDao;

    @Mock
    private SeasonTicketRenewalsService seasonTicketRenewalsService;

    @Mock
    private EventConfigService eventConfigService;

    @Mock
    private EventDao eventDao;

    @Mock
    private SeasonTicketHelper seasonTicketHelper;

    @Mock
    private SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao;

    @Mock
    private EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;

    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;

    @Mock
    private SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;

    @Mock
    private es.onebox.event.seasontickets.dao.couch.SeasonTicketTransferConfigCouchDao seasonTicketTransferConfigCouchDao;

    @Mock
    private EventExternalService eventExternalService;

    @Mock
    private AccessControlSystemsRepository accessControlSystemsRepository;

    @Captor
    private ArgumentCaptor<List<Integer>> captureSessionIds;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createSeasonTicket() {
        OneboxRestException e = new OneboxRestException(CoreErrorCode.NOT_FOUND, null, null);
        CreateSeasonTicketRequestDTO newSeasonTicket = new CreateSeasonTicketRequestDTO();

        newSeasonTicket.setName("a valid name");

        newSeasonTicket.setEntityId(1L);

        newSeasonTicket.setProducerId(1L);

        newSeasonTicket.setCategoryId(1);

        int createdEventId = 1;
        CpanelEventoRecord createdEvent = new CpanelEventoRecord();
        createdEvent.setIdevento(createdEventId);
        when(seasonTicketEventDao.insert(any())).thenReturn(createdEvent);

        Long returnedEventId = seasonTicketService.createSeasonTicket(newSeasonTicket);

        assertEquals(createdEventId, returnedEventId.intValue());

        Mockito.verify(seasonTicketDao, times(1)).insert(any());
        Mockito.verify(seasonTicketSurchargesService, times(1)).initSeasonTicketSurcharges(any(CpanelEventoRecord.class));
    }

    @Test
    public void updateSeasonTicket() {
        Long seasonTicketId = 1L;
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenThrow(new EntityNotFoundException(""));

        OneboxRestException e = null;
        UpdateSeasonTicketRequestDTO st = new UpdateSeasonTicketRequestDTO();
        st.setId(seasonTicketId);
        try {
            seasonTicketService.updateSeasonTicket(st);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assertions.assertNotNull(e);
        assertEquals("Season ticket not found for id " + seasonTicketId, e.getMessage());

        EventRecord stRecord = new EventRecord();
        seasonTicketId = 2L;
        stRecord.setIdevento(seasonTicketId.intValue());

        SessionRecord sRecord = generateRandomSessionRecord();
        sRecord.setEstado(SeasonTicketStatusDTO.DELETED.getId());

        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(createEntry(stRecord));
        when(seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketId)).thenReturn(Collections.singletonList(sRecord));
        st.setId(seasonTicketId);

        try {
            seasonTicketService.updateSeasonTicket(st);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("season ticket is already deleted. It cant be updated!",e.getMessage(),"Cannot update a deleted season ticket");

        seasonTicketId = 3L;
        Long entityId = 10L;
        //TODO: Fix status to SeasonticketStatus
        sRecord.setEstado(SeasonTicketStatusDTO.SET_UP.getId());
        stRecord.setIdentidad(entityId.intValue());
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(createEntry(stRecord));

        st.setName("an existing name");
        SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();
        filter.setEntityId(entityId);
        filter.setName(st.getName());
        when(seasonTicketEventDao.countByFilter(filter)).thenReturn(1L);
        try {
            seasonTicketService.updateSeasonTicket(st);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("season ticket name already used for entity",e.getMessage(),"cannot repeat the season ticket name");

        String anExistingButNotChangedName = "an existing but not changed name";
        st.setName(anExistingButNotChangedName);
        stRecord.setNombre(anExistingButNotChangedName);
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(createEntry(stRecord));

        filter = new SeasonTicketSearchFilter();
        filter.setEntityId(entityId);
        filter.setName(st.getName());
        when(seasonTicketEventDao.countByFilter(filter)).thenReturn(1L);
        boolean allOk = true;

        try {
            seasonTicketService.updateSeasonTicket(st);
        } catch (OneboxRestException ore) {
            e = ore;
            allOk = false;
        }
        Assertions.assertTrue(allOk, "repeated season ticket name check is not performed when name is not changed");

        st.setName("an non existing name");

        stRecord.setNombre("just a common name");
        when(seasonTicketEventDao.findSeasonTicket(seasonTicketId)).thenReturn(createEntry(stRecord));

        filter = new SeasonTicketSearchFilter();
        filter.setEntityId(entityId);
        filter.setName(st.getName());
        when(seasonTicketEventDao.countByFilter(filter)).thenReturn(0L);
        st.setLanguages(new ArrayList<>());
        EventLanguageDTO eventLanguage = new EventLanguageDTO();
        eventLanguage.setId(1L);
        eventLanguage.setCode("es_ES");
        st.getLanguages().add(eventLanguage);

        try {
            seasonTicketService.updateSeasonTicket(st);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("season ticket languages must define exactly 1 by default",e.getMessage(),"season ticket must have a default language");

        eventLanguage.setDefault(true);
        st.setMemberMandatory(Boolean.TRUE);
        st.setAllowRenewal(Boolean.TRUE);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        Mockito.when(seasonTicketDao.getById(ArgumentMatchers.anyInt())).thenReturn(cpanelSeasonTicketRecord);

        seasonTicketService.updateSeasonTicket(st);

        Mockito.verify(seasonTicketDao, times(1)).update(any());
    }

    @Test
    public void searchSeasonTicketsTestOK() {

        Map<EventRecord, List<VenueRecord>> ticketsMap = createSeasonTicketsMap();
        EventRecord seasonTicketRecord = ticketsMap.keySet().iterator().next();
        SessionRecord sRecord = generateRandomSessionRecord();
        sRecord.setEstadogeneracionaforo(SessionGenerationStatus.ACTIVE.getId());

        CpanelConfigRecintoRecord venueRecord = new CpanelConfigRecintoRecord();
        venueRecord.setIdevento(52);
        venueRecord.setEstado(VenueStatusDTO.ACTIVE.getId());

        when(seasonTicketEventDao.countByFilter(any(SeasonTicketSearchFilter.class))).thenReturn(1L);
        when(seasonTicketEventDao.findSeasonTickets(any(SeasonTicketSearchFilter.class))).thenReturn(ticketsMap);
        when(seasonTicketSessionDao.searchSessionInfoByEventIds(anyList())).thenReturn(Collections.singletonList(sRecord));
        when(venueConfigDao.getVenueConfigListBySeasonTicketIdList(anyList())).thenReturn(Collections.singletonList(venueRecord));

        SeasonTicketsDTO seasonTicketsDTO = seasonTicketService.searchSeasonTickets(createFilter());

        Assertions.assertNotNull(seasonTicketsDTO);
        Assertions.assertNotNull(seasonTicketsDTO.getMetadata());
        Assertions.assertNotNull(seasonTicketsDTO.getData());
        Assertions.assertEquals(1, seasonTicketsDTO.getData().size());
        SearchSeasonTicketDTO dto = seasonTicketsDTO.getData().get(0);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(seasonTicketRecord.getIdevento().longValue(), dto.getId());
        Assertions.assertNotNull(dto.getStatus());
        Assertions.assertNotNull(dto.getName());
        Assertions.assertEquals(seasonTicketRecord.getIdentidad().longValue(), dto.getEntityId());
        Assertions.assertEquals(seasonTicketRecord.getReferenciapromotor(), dto.getPromoterReference());
        Assertions.assertNotNull(dto.getDate());
        assertDate(dto.getDate().getStart());
        assertDate(dto.getDate().getEnd());
        Assertions.assertNotNull(dto.getProducer());
        Assertions.assertEquals(seasonTicketRecord.getIdpromotor().longValue(), dto.getProducer().getId());
        Assertions.assertEquals(seasonTicketRecord.getPromoterName(), dto.getProducer().getName());
        Assertions.assertEquals(dto.getSalesStartingDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechaventa()));
        Assertions.assertEquals(dto.getSalesEndDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechafinsesion()));
        Assertions.assertEquals(dto.getChannelPublishingDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechapublicacion()));
        Assertions.assertEquals(dto.getEnableChannels(), CommonUtils.isTrue(sRecord.getPublicado()));
        Assertions.assertEquals(dto.getEnableSales(), CommonUtils.isTrue(sRecord.getEnventa()));
        Assertions.assertNotNull(dto.getGenerationStatus());
        Assertions.assertEquals(SeasonTicketInternalGenerationStatus.READY, dto.getGenerationStatus());
    }

    @Test
    public void searchSeasonTicketsTestOK_noSessionCreated() {

        Map<EventRecord, List<VenueRecord>> ticketsMap = createSeasonTicketsMap();
        EventRecord seasonTicketRecord = ticketsMap.keySet().iterator().next();

        when(seasonTicketEventDao.countByFilter(any(SeasonTicketSearchFilter.class))).thenReturn(1L);
        when(seasonTicketEventDao.findSeasonTickets(any(SeasonTicketSearchFilter.class))).thenReturn(ticketsMap);
        when(seasonTicketSessionDao.searchSessionInfoByEventId(anyLong())).thenReturn(null);

        SeasonTicketsDTO seasonTicketsDTO = seasonTicketService.searchSeasonTickets(createFilter());

        Assertions.assertNotNull(seasonTicketsDTO);
        Assertions.assertNotNull(seasonTicketsDTO.getMetadata());
        Assertions.assertNotNull(seasonTicketsDTO.getData());
        Assertions.assertEquals(1, seasonTicketsDTO.getData().size());
        SearchSeasonTicketDTO dto = seasonTicketsDTO.getData().get(0);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(seasonTicketRecord.getIdevento().longValue(), dto.getId());
        Assertions.assertNull(dto.getStatus());
        Assertions.assertNotNull(dto.getName());
        Assertions.assertEquals(seasonTicketRecord.getIdentidad().longValue(), dto.getEntityId());
        Assertions.assertEquals(seasonTicketRecord.getReferenciapromotor(), dto.getPromoterReference());
        Assertions.assertNotNull(dto.getDate());
        assertDate(dto.getDate().getStart());
        assertDate(dto.getDate().getEnd());
        Assertions.assertNotNull(dto.getProducer());
        Assertions.assertEquals(seasonTicketRecord.getIdpromotor().longValue(), dto.getProducer().getId());
        Assertions.assertEquals(seasonTicketRecord.getPromoterName(), dto.getProducer().getName());
        Assertions.assertNull(dto.getSalesStartingDate());
        Assertions.assertNull(dto.getSalesEndDate());
        Assertions.assertNull(dto.getChannelPublishingDate());
        Assertions.assertNull(dto.getEnableChannels());
        Assertions.assertNull(dto.getEnableSales());
    }

    @Test
    public void getSeasonTicket_ok() {
        Map.Entry<EventRecord, List<VenueRecord>> stRecord = createSeasonTicketsMap().entrySet().iterator().next();
        SessionRecord sRecord = generateRandomSessionRecord();
        when(seasonTicketEventDao.findSeasonTicket(anyLong())).thenReturn(stRecord);
        when(seasonTicketSessionDao.searchSessionInfoByEventId(anyLong())).thenReturn(Collections.singletonList(sRecord));

        List<EventLanguageRecord> languages = createLanguages();
        when(eventLanguageDao.findByEventId(anyLong())).thenReturn(languages);

        SeasonTicketDTO dto = seasonTicketService.getSeasonTicket(52L);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(52L, (long) dto.getId());
        Assertions.assertNotNull(dto.getStatus());
        Assertions.assertNotNull(dto.getName());
        Assertions.assertEquals(1L, (long) dto.getEntityId());
        Assertions.assertEquals(ENTITY_NAME, dto.getEntityName());
        Assertions.assertNotNull(dto.getDate());
        Assertions.assertEquals(dto.getSalesStartingDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechaventa()));
        Assertions.assertEquals(dto.getSalesEndDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechafinsesion()));
        Assertions.assertEquals(dto.getChannelPublishingDate(), CommonUtils.timestampToZonedDateTime(sRecord.getFechapublicacion()));
        Assertions.assertEquals(dto.getEnableChannels(), CommonUtils.isTrue(sRecord.getPublicado()));
        Assertions.assertEquals(dto.getEnableSales(), CommonUtils.isTrue(sRecord.getEnventa()));
        Assertions.assertTrue(dto.getMemberMandatory());
    }

    @Test
    public void getSeasonTicket_ok_noSessionCreated() {
        Map.Entry<EventRecord, List<VenueRecord>> stRecord = createSeasonTicketsMap().entrySet().iterator().next();
        SessionRecord sRecord = null;
        when(seasonTicketEventDao.findSeasonTicket(anyLong())).thenReturn(stRecord);
        when(seasonTicketSessionDao.searchSessionInfoByEventId(anyLong())).thenReturn(null);

        List<EventLanguageRecord> languages = createLanguages();
        when(eventLanguageDao.findByEventId(anyLong())).thenReturn(languages);

        SeasonTicketDTO dto = seasonTicketService.getSeasonTicket(52L);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(52L, (long) dto.getId());
        Assertions.assertNull(dto.getStatus());
        Assertions.assertNotNull(dto.getName());
        Assertions.assertEquals(1L, (long) dto.getEntityId());
        Assertions.assertEquals(ENTITY_NAME, dto.getEntityName());
        Assertions.assertNotNull(dto.getDate());
        Assertions.assertNull(dto.getSalesStartingDate());
        Assertions.assertNull(dto.getSalesEndDate());
        Assertions.assertNull(dto.getChannelPublishingDate());
        Assertions.assertNull(dto.getEnableChannels());
        Assertions.assertNull(dto.getEnableSales());
        Assertions.assertTrue(dto.getMemberMandatory());
    }

    private Map<EventRecord, List<VenueRecord>> createSeasonTicketsMap() {
        Map<EventRecord, List<VenueRecord>> seasonTickets = new HashMap<>();
        seasonTickets.put(createEventMapper(), null);
        return seasonTickets;
    }

    private EventRecord createEventMapper() {
        EventRecord seasonTicketRecord = new EventRecord();

        seasonTicketRecord.setIdevento(52);
        seasonTicketRecord.setTipoevento(EventType.NORMAL.getId());
        seasonTicketRecord.setEstado(EventStatus.READY.getId());
        seasonTicketRecord.setNombre("Event");
        seasonTicketRecord.setIdentidad(1);
        seasonTicketRecord.setEntityName(ENTITY_NAME);
        seasonTicketRecord.setReferenciapromotor(random(String.class));
        seasonTicketRecord.setFechainicio(Timestamp.valueOf("2018-10-01 10:00:00"));
        seasonTicketRecord.setFechafin(Timestamp.valueOf("2018-10-01 11:00:00"));
        seasonTicketRecord.setFechainiciotz(1);
        seasonTicketRecord.setFechafintz(1);
        seasonTicketRecord.setStartDateTZ("Europe/Berlin");
        seasonTicketRecord.setEndDateTZ("Europe/Berlin");
        seasonTicketRecord.setStartDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        seasonTicketRecord.setEndDateTZDesc("(GMT +01:00) Brussels, Copenhagen, Madrid, Paris");
        seasonTicketRecord.setStartDateTZOffset(60);
        seasonTicketRecord.setEndDateTZOffset(60);
        seasonTicketRecord.setEmailresponsable(random(String.class));
        seasonTicketRecord.setNombreresponsable(random(String.class));
        seasonTicketRecord.setApellidosresponsable(random(String.class));
        seasonTicketRecord.setTelefonoresponsable(random(String.class));
        seasonTicketRecord.setObjetivosobreentradas(random(Integer.class));
        seasonTicketRecord.setObjetivosobreventas(random(Double.class));
        seasonTicketRecord.setIdpromotor(random(Integer.class));
        seasonTicketRecord.setPromoterName(random(String.class));
        seasonTicketRecord.setIdtaxonomia(Math.abs(random(Integer.class)));
        seasonTicketRecord.setCategoryCode(random(String.class));
        seasonTicketRecord.setCategoryDescription(random(String.class));
        seasonTicketRecord.setIdtaxonomiapropia(Math.abs(random(Integer.class)));
        seasonTicketRecord.setCustomCategoryRef(random(String.class));
        seasonTicketRecord.setCustomCategoryDescription(random(String.class));
        seasonTicketRecord.setIdgira(random(Integer.class));
        seasonTicketRecord.setTourName(random(String.class));
        seasonTicketRecord.setMemberMandatory(Boolean.TRUE);
        seasonTicketRecord.setAllowRenewal(Boolean.TRUE);
        return seasonTicketRecord;
    }

    private SessionRecord generateRandomSessionRecord() {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdevento(52);
        sessionRecord.setEstado(SessionStatus.READY.getId());
        sessionRecord.setIspreview(false);
        sessionRecord.setFechapublicacion(random(Timestamp.class));
        sessionRecord.setFechaventa(random(Timestamp.class));
        sessionRecord.setFechafinsesion(random(Timestamp.class));
        sessionRecord.setEnventa(random(Byte.class));
        sessionRecord.setPublicado(random(Byte.class));
        return sessionRecord;
    }

    private List<EventLanguageRecord> createLanguages() {
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

    private void assertDate(DateDTO date) {
        Assertions.assertNotNull(date);
        Assertions.assertNotNull(date.getTimeZone());
        Assertions.assertNotNull(date.getTimeZone().getOlsonId());
        Assertions.assertNotNull(date.getTimeZone().getName());
        Assertions.assertNotNull(date.getTimeZone().getOffset());
    }

    private SeasonTicketSearchFilter createFilter() {
        SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();

        filter.setVenueConfigId(110L);
        filter.setVenueId(10L);
        filter.setEntityId(10L);
        filter.setStatus(createStatusArray());

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

    private List<SeasonTicketStatusDTO> createStatusArray() {
        List<SeasonTicketStatusDTO> statusList = new ArrayList<>();
        statusList.add(SeasonTicketStatusDTO.READY);
        return statusList;
    }

    private Map.Entry<EventRecord, List<VenueRecord>> createEntry(EventRecord record) {
        return new Map.Entry<>() {
            @Override
            public EventRecord getKey() {
                return record;
            }

            @Override
            public List<VenueRecord> getValue() {
                return new ArrayList<>();
            }

            @Override
            public List<VenueRecord> setValue(List<VenueRecord> value) {
                return new ArrayList<>();
            }
        };
    }


    @Test
    public void checkOperativeDates() {

        SessionRecord record = new SessionRecord();
        UpdateSeasonTicketRequestDTO request = new UpdateSeasonTicketRequestDTO();
        OneboxRestException e = new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES, null, null);
        addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "11/02/2020 10:10:10", "10/02/2020 10:10:10");
        SeasonTicketService.checkOperativeDates(request, record);

        addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "11/02/2020 10:10:10", "10/02/2020 10:10:10");
        request.setSalesStartingDate(null);
        SeasonTicketService.checkOperativeDates(request, record);

        addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "11/02/2020 10:10:10", "10/02/2020 10:10:10");
        request.setChannelPublishingDate(null);
        SeasonTicketService.checkOperativeDates(request, record);

        addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "11/02/2020 10:10:10", "10/02/2020 10:10:10");
        request.setSalesEndDate(null);
        SeasonTicketService.checkOperativeDates(request, record);

        try {
            addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "11/02/2020 10:10:10", "12/02/2020 10:10:10");
            SeasonTicketService.checkOperativeDates(request, record);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("Publication date must be before the ending sales date", e.getMessage());

        try {
            addDatesToRecordAndRequest(record, request, "12/02/2020 10:10:10", "11/02/2020 10:10:10", "10/02/2020 10:10:10");
            SeasonTicketService.checkOperativeDates(request, record);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("Sales starting date must be before the ending sales date", e.getMessage());

        try {
            addDatesToRecordAndRequest(record, request, "10/02/2020 10:10:10", "12/02/2020 10:10:10", "11/02/2020 10:10:10");
            SeasonTicketService.checkOperativeDates(request, record);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("Publication date must be before the starting sales date", e.getMessage());
    }

    @Test
    public void checkOperativeStates() {

        SessionRecord record = new SessionRecord();
        UpdateSeasonTicketRequestDTO request = new UpdateSeasonTicketRequestDTO();
        OneboxRestException e = new OneboxRestException(MsEventErrorCode.INCONSISTENT_DATES, null, null);
        addOperativeStatesToRecordAndRequest(record, request, true, true);
        SeasonTicketService.checkOperativeStates(request, record);

        addOperativeStatesToRecordAndRequest(record, request, true, true);
        request.setEnableSales(null);
        SeasonTicketService.checkOperativeStates(request, record);

        addOperativeStatesToRecordAndRequest(record, request, true, true);
        request.setEnableChannels(null);
        SeasonTicketService.checkOperativeStates(request, record);

        try {
            addOperativeStatesToRecordAndRequest(record, request, false, true);
            SeasonTicketService.checkOperativeStates(request, record);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertEquals("Sales cannot be enabled while the season ticket is not published", e.getMessage());
    }

    private void addDatesToRecordAndRequest(SessionRecord record, UpdateSeasonTicketRequestDTO request, String salesStartingDateS, String salesEndDateS, String publicationDateS) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            Timestamp salesStartingDate = new Timestamp(formatter.parse(salesStartingDateS).getTime());
            Timestamp salesEndDate = new Timestamp(formatter.parse(salesEndDateS).getTime());
            Timestamp publicationDate = new Timestamp(formatter.parse(publicationDateS).getTime());

            record.setFechaventa(salesStartingDate);
            record.setFechafinsesion(salesEndDate);
            record.setFechapublicacion(publicationDate);
            request.setSalesStartingDate(CommonUtils.timestampToZonedDateTime(salesStartingDate));
            request.setSalesEndDate(CommonUtils.timestampToZonedDateTime(salesEndDate));
            request.setChannelPublishingDate(CommonUtils.timestampToZonedDateTime(publicationDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void addOperativeStatesToRecordAndRequest(SessionRecord record, UpdateSeasonTicketRequestDTO request, Boolean channelPublished, Boolean enableSales) {
        request.setEnableChannels(channelPublished);
        request.setEnableSales(enableSales);
        record.setPublicado(ConverterUtils.isTrueAsByte(channelPublished));
        record.setEnventa(ConverterUtils.isTrueAsByte(enableSales));
    }

    @Test
    public void getGenerationStatusTest() {
        VenueConfigStatusRecord venueConfigStatusRecord = new VenueConfigStatusRecord();

        SessionCapacityGenerationStatusRecord sessionCapacityGenerationStatusRecord = new SessionCapacityGenerationStatusRecord();
        when(seasonTicketSessionDao.getCapacityGenerationStatusBySeasonTicketId(any())).thenReturn(sessionCapacityGenerationStatusRecord);

        when(venueConfigDao.getVenueConfigStatusBySeasonTicketId(any())).thenReturn(null);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 1, 0, SeasonTicketInternalGenerationStatus.CREATED);

        when(venueConfigDao.getVenueConfigStatusBySeasonTicketId(any())).thenReturn(venueConfigStatusRecord);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 2, 0, SeasonTicketInternalGenerationStatus.VENUE_GENERATION_IN_PROGRESS);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 3, 0, SeasonTicketInternalGenerationStatus.VENUE_ERROR);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 1, 0, SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 1, 1, SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 1, 3, SeasonTicketInternalGenerationStatus.SESSION_ERROR);
        checkGenerationStatus(venueConfigStatusRecord, sessionCapacityGenerationStatusRecord, 1, 2, SeasonTicketInternalGenerationStatus.READY);
    }

    private void checkGenerationStatus(VenueConfigStatusRecord venueConfigStatusRecord,
                                       SessionCapacityGenerationStatusRecord sessionCapacityGenerationStatusRecord,
                                       int venueConfigStatusRecordEstado,
                                       int sessionCapacityGenerationStatusRecordEstado,
                                       SeasonTicketInternalGenerationStatus expectedStatus) {
        venueConfigStatusRecord.setEstado(venueConfigStatusRecordEstado);
        sessionCapacityGenerationStatusRecord.setEstadoGeneracionAforo(sessionCapacityGenerationStatusRecordEstado);

        SeasonTicketStatusResponseDTO responseDTO = seasonTicketService.getStatus(1L);

        assertNotNull(responseDTO);
        assertEquals(expectedStatus, responseDTO.getGenerationStatus());
    }

    @Test
    public void updateStatusTest_notChanged() {

        Long seasonTicketId = 1L;
        UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO = new UpdateSeasonTicketStatusRequestDTO();
        updateSeasonTicketStatusRequestDTO.setStatus(SeasonTicketStatusDTO.SET_UP);

        EventRecord seasonTicketRecord = new EventRecord();
        seasonTicketRecord.setIdevento(1);
        VenueRecord seasonTicketVenueRecord = new VenueRecord();
        Map<EventRecord, List<VenueRecord>> mapEventVenueRecords = new HashMap<>();
        List<VenueRecord> seasonTicketVenueRecordList = new ArrayList<>();
        seasonTicketVenueRecordList.add(seasonTicketVenueRecord);
        mapEventVenueRecords.put(seasonTicketRecord, seasonTicketVenueRecordList);
        when(seasonTicketEventDao.findSeasonTicket(any())).thenReturn(mapEventVenueRecords.entrySet().iterator().next());

        SessionRecord seasonTicketSessionRecord = new SessionRecord();
        seasonTicketSessionRecord.setEstado(SessionStatus.PLANNED.getId());
        List<SessionRecord> seasonTicketSessionRecordList = new ArrayList<>();
        seasonTicketSessionRecordList.add(seasonTicketSessionRecord);
        when(seasonTicketSessionDao.searchSessionInfoByEventId(any())).thenReturn(seasonTicketSessionRecordList);

        // Generation status READY
        VenueConfigStatusRecord statusRecord = new VenueConfigStatusRecord(VenueStatusDTO.ACTIVE.getId());
        when(venueConfigDao.getVenueConfigStatusBySeasonTicketId(any())).thenReturn(statusRecord);
        SessionCapacityGenerationStatusRecord generationStatus = new SessionCapacityGenerationStatusRecord(SessionGenerationStatus.ACTIVE.getId());
        when(seasonTicketSessionDao.getCapacityGenerationStatusBySeasonTicketId(any())).thenReturn(generationStatus);

        seasonTicketService.updateStatus(seasonTicketId, updateSeasonTicketStatusRequestDTO);

        Mockito.verify(seasonTicketEventDao, never()).update(any());
    }

    @Test
    public void checkOperativeMaxBuyingLimitNullTests() {
        UpdateSeasonTicketRequestDTO updateSeasonTicketRequestDTO = null;
        OneboxRestException capturedException = null;

        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNull(capturedException);

        updateSeasonTicketRequestDTO = new UpdateSeasonTicketRequestDTO();
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNull(capturedException);

        MaxBuyingLimitDTO maxBuyingLimitDTO = new MaxBuyingLimitDTO();
        updateSeasonTicketRequestDTO.setMaxBuyingLimit(maxBuyingLimitDTO);
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNull(capturedException);
    }

    @Test
    public void checkOperativeMaxBuyingLimitValueLimitTest() {
        UpdateSeasonTicketRequestDTO updateSeasonTicketRequestDTO = new UpdateSeasonTicketRequestDTO();

        MaxBuyingLimitDTO maxBuyingLimitDTO = new MaxBuyingLimitDTO();
        updateSeasonTicketRequestDTO.setMaxBuyingLimit(maxBuyingLimitDTO);

        // -1
        OneboxRestException capturedException = null;
        maxBuyingLimitDTO.setValue(-1);
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNotNull(capturedException);
        assertEquals(capturedException.getErrorCode(), MsEventSeasonTicketErrorCode.SEASON_TICKET_MAX_BUYING_LIMIT_RANGE.getErrorCode());

        // 0
        capturedException = null;
        maxBuyingLimitDTO.setValue(0);
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNotNull(capturedException);
        assertEquals(capturedException.getErrorCode(), MsEventSeasonTicketErrorCode.SEASON_TICKET_MAX_BUYING_LIMIT_RANGE.getErrorCode());

        // 11
        capturedException = null;
        maxBuyingLimitDTO.setValue(0);
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNotNull(capturedException);
        assertEquals(capturedException.getErrorCode(), MsEventSeasonTicketErrorCode.SEASON_TICKET_MAX_BUYING_LIMIT_RANGE.getErrorCode());
    }

    @Test
    public void checkOperativeMaxBuyingLimitValueOKTest() {
        UpdateSeasonTicketRequestDTO updateSeasonTicketRequestDTO = new UpdateSeasonTicketRequestDTO();

        MaxBuyingLimitDTO maxBuyingLimitDTO = new MaxBuyingLimitDTO();
        updateSeasonTicketRequestDTO.setMaxBuyingLimit(maxBuyingLimitDTO);

        // 5
        OneboxRestException capturedException = null;
        maxBuyingLimitDTO.setValue(5);
        try {
            SeasonTicketService.checkOperativeMaxBuyingLimit(updateSeasonTicketRequestDTO);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        assertNull(capturedException);
    }

    @Test
    public void updateStatusTest_changed() {

        Long seasonTicketId = 1L;
        UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO = new UpdateSeasonTicketStatusRequestDTO();
        updateSeasonTicketStatusRequestDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);

        EventRecord seasonTicketRecord = new EventRecord();
        seasonTicketRecord.setIdevento(1);
        VenueRecord seasonTicketVenueRecord = new VenueRecord();
        Map<EventRecord, List<VenueRecord>> mapEventVenueRecords = new HashMap<>();
        List<VenueRecord> seasonTicketVenueRecordList = new ArrayList<>();
        seasonTicketVenueRecordList.add(seasonTicketVenueRecord);
        mapEventVenueRecords.put(seasonTicketRecord, seasonTicketVenueRecordList);
        when(seasonTicketEventDao.findSeasonTicket(any())).thenReturn(mapEventVenueRecords.entrySet().iterator().next());

        SessionRecord seasonTicketSessionRecord = new SessionRecord();
        seasonTicketSessionRecord.setEstado(SessionStatus.PLANNED.getId());
        List<SessionRecord> seasonTicketSessionRecordList = new ArrayList<>();
        seasonTicketSessionRecordList.add(seasonTicketSessionRecord);
        when(seasonTicketSessionDao.searchSessionInfoByEventId(any())).thenReturn(seasonTicketSessionRecordList);

        // Generation status READY
        VenueConfigStatusRecord statusRecord = new VenueConfigStatusRecord(VenueStatusDTO.ACTIVE.getId());
        when(venueConfigDao.getVenueConfigStatusBySeasonTicketId(any())).thenReturn(statusRecord);
        SessionCapacityGenerationStatusRecord generationStatus = new SessionCapacityGenerationStatusRecord(SessionGenerationStatus.ACTIVE.getId());
        when(seasonTicketSessionDao.getCapacityGenerationStatusBySeasonTicketId(any())).thenReturn(generationStatus);

        seasonTicketService.updateStatus(seasonTicketId, updateSeasonTicketStatusRequestDTO);

        Mockito.verify(seasonTicketEventDao, times(1)).update(any());
    }

    @Test
    public void deleteSeasonTicket_ok_noSession() {

        SeasonTicketService spyService = Mockito.spy(new SeasonTicketService(seasonTicketEventDao, seasonTicketSessionDao,
                venueTemplateDao, sessionDao, eventRemoveService, eventSecondaryMarketConfigService,
                eventLanguageDao, commonRatesService, entitiesRepository, helper, venueConfigDao,
                ordersRepository, null, seasonSessionDao, null,
                null, null, commonTicketTemplateService,
                null, refreshDataService, seasonTicketRenewalsService, eventConfigService, eventDao,
                seasonTicketSurchargesService, seasonTicketHelper, seasonTicketReleaseSeatCouchDao,
                seasonTicketRenewalConfigCouchDao, eventExternalService, accessControlSystemsRepository, seasonTicketTransferConfigCouchDao));

        Long seasonTicketId = 1L;
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setIdevento(seasonTicketId.intValue());
        cpanelEventoRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        when(seasonTicketEventDao.getById(any())).thenReturn(cpanelEventoRecord);
        Mockito.doReturn(0L).when(ordersRepository).countByEventAndChannel(anyLong(), any());
        Mockito.doReturn(SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS).when(spyService).getGenerationStatus(any());

        spyService.deleteSeasonTicket(seasonTicketId);
        Mockito.verify(eventRemoveService, times(1)).removeSeats(any());
        Mockito.verify(eventSecondaryMarketConfigService, times(1)).deleteEventSecondaryMarketConfig(any());
        Mockito.verify(seasonTicketEventDao, times(1)).update(any());
        Mockito.verify(seasonTicketSessionDao, times(0)).update(any());
        Mockito.verify(seasonTicketReleaseSeatCouchDao, times(1)).remove(seasonTicketId.toString());
    }

    @Test
    public void deleteSeasonTicket_ok_session() {

        SeasonTicketService spyService = Mockito.spy(new SeasonTicketService(seasonTicketEventDao, seasonTicketSessionDao,
                venueTemplateDao, sessionDao, eventRemoveService, eventSecondaryMarketConfigService,
                eventLanguageDao, commonRatesService, entitiesRepository, helper, venueConfigDao,
                ordersRepository, null, seasonSessionDao, null,
                null, null, commonTicketTemplateService,
                null, refreshDataService, seasonTicketRenewalsService, eventConfigService, eventDao,
                seasonTicketSurchargesService, seasonTicketHelper, seasonTicketReleaseSeatCouchDao, seasonTicketRenewalConfigCouchDao,
                eventExternalService, accessControlSystemsRepository, seasonTicketTransferConfigCouchDao));

        Long seasonTicketId = 1L;
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setIdevento(seasonTicketId.intValue());
        cpanelEventoRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(11);
        List<SessionRecord> sessionRecords = new ArrayList<>();
        sessionRecords.add(sessionRecord);


        when(seasonTicketEventDao.getById(any())).thenReturn(cpanelEventoRecord);
        Mockito.doReturn(0L).when(ordersRepository).countByEventAndChannel(anyLong(), any());
        Mockito.doReturn(SeasonTicketInternalGenerationStatus.READY).when(spyService).getGenerationStatus(any());
        when(seasonTicketSessionDao.searchSessionInfoByEventId(any())).thenReturn(sessionRecords);

        Mockito.doNothing().when(spyService).prepareDeleteSession(any(), any());

        spyService.deleteSeasonTicket(seasonTicketId);
        Mockito.verify(eventRemoveService, times(1)).removeSeats(any());
        Mockito.verify(eventSecondaryMarketConfigService, times(1)).deleteEventSecondaryMarketConfig(any());
        Mockito.verify(seasonTicketEventDao, times(1)).update(any());
        Mockito.verify(seasonTicketSessionDao, times(1)).update(any());
    }

    @Test
    public void deleteSeasonTicket_event_not_removable() {

        SeasonTicketService spyService = Mockito.spy(new SeasonTicketService(seasonTicketEventDao, seasonTicketSessionDao,
                venueTemplateDao, sessionDao, eventRemoveService, eventSecondaryMarketConfigService,
                eventLanguageDao, commonRatesService, entitiesRepository, helper, venueConfigDao,
                ordersRepository, null, seasonSessionDao, null,
                null, null, commonTicketTemplateService,
                null, refreshDataService, seasonTicketRenewalsService, eventConfigService, eventDao,
                seasonTicketSurchargesService, seasonTicketHelper, seasonTicketReleaseSeatCouchDao,
                seasonTicketRenewalConfigCouchDao, eventExternalService, accessControlSystemsRepository, seasonTicketTransferConfigCouchDao));

        Long seasonTicketId = 1L;
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setIdevento(seasonTicketId.intValue());
        cpanelEventoRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        when(seasonTicketEventDao.getById(any())).thenReturn(cpanelEventoRecord);
        Mockito.doReturn(1L).when(ordersRepository).countByEventAndChannel(anyLong(), any());

        ProductSearchResponse productSearchResponse = new ProductSearchResponse();
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        List<OrderProductDTO> data = Collections.singletonList(orderProductDTO);
        productSearchResponse.setData(data);
        when(ordersRepository.getAlmostOneActiveProduct(anyList())).thenReturn(productSearchResponse);

        Assertions.assertThrows(OneboxRestException.class, () ->
                spyService.deleteSeasonTicket(seasonTicketId));
    }

    @Test
    public void prepareDeleteSession_session_with_orders() {
        Long seasonTicketId = 1L;
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(123);
        when(ordersRepository.sessionOperations(any())).thenReturn(Map.of(1L, 1L));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketService.prepareDeleteSession(seasonTicketId, sessionRecord));
    }

    @Test
    public void updateSeasonTicket_validateCustomData() {
        // Only member mandatory to true when member mandatory was false
        UpdateSeasonTicketRequestDTO updateSeasonTicketRequestDTO = new UpdateSeasonTicketRequestDTO();
        updateSeasonTicketRequestDTO.setId(1L);
        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.TRUE);
        updateSeasonTicketRequestDTO.setAllowRenewal(null);

        EventRecord stRecord = new EventRecord();
        stRecord.setIdevento(1);
        when(seasonTicketEventDao.findSeasonTicket(ArgumentMatchers.eq(1L))).thenReturn(createEntry(stRecord));

        SessionRecord sRecord = generateRandomSessionRecord();
        when(seasonTicketSessionDao.searchSessionInfoByEventId(ArgumentMatchers.eq(1L))).thenReturn(Collections.singletonList(sRecord));

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);
        Mockito.when(seasonTicketDao.getById(ArgumentMatchers.anyInt())).thenReturn(cpanelSeasonTicketRecord);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);

        ArgumentCaptor<CpanelSeasonTicketRecord> recordCaptor = ArgumentCaptor.forClass(CpanelSeasonTicketRecord.class);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        CpanelSeasonTicketRecord record = recordCaptor.getValue();
        Assertions.assertTrue(record.getIsmembermandatory());
        Assertions.assertFalse(record.getAllowrenewal());

        // Only member mandatory to true when member mandatory was true
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.TRUE);
        updateSeasonTicketRequestDTO.setAllowRenewal(null);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.TRUE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, never()).update(recordCaptor.capture());

        // Only member mandatory to false when member mandatory was false
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.FALSE);
        updateSeasonTicketRequestDTO.setAllowRenewal(null);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, never()).update(recordCaptor.capture());

        // Only member mandatory to false when member mandatory was true
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.FALSE);
        updateSeasonTicketRequestDTO.setAllowRenewal(null);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.TRUE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertFalse(record.getIsmembermandatory());
        Assertions.assertFalse(record.getAllowrenewal());

        // Only allow renewal to true when allow renewal was false
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(null);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.TRUE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertFalse(record.getIsmembermandatory());
        Assertions.assertTrue(record.getAllowrenewal());

        // Only allow renewal to true when allow renewal was true
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(null);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.TRUE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, never()).update(recordCaptor.capture());

        // Only allow renewal to false when allow renewal was false
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(null);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.FALSE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, never()).update(recordCaptor.capture());

        // Only allow renewal to false when allow renewal was true
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(null);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.FALSE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertFalse(record.getIsmembermandatory());
        Assertions.assertFalse(record.getAllowrenewal());

        // Both to true when both were false
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.TRUE);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.TRUE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertTrue(record.getIsmembermandatory());
        Assertions.assertTrue(record.getAllowrenewal());

        // Both to false when both were true
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.FALSE);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.FALSE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.TRUE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertFalse(record.getIsmembermandatory());
        Assertions.assertFalse(record.getAllowrenewal());

        // Mixed modifying
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.TRUE);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.FALSE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, times(1)).update(recordCaptor.capture());

        record = recordCaptor.getValue();
        Assertions.assertTrue(record.getIsmembermandatory());
        Assertions.assertFalse(record.getAllowrenewal());

        // Mixed not modifying
        Mockito.clearInvocations(seasonTicketDao);

        updateSeasonTicketRequestDTO.setMemberMandatory(Boolean.FALSE);
        updateSeasonTicketRequestDTO.setAllowRenewal(Boolean.TRUE);

        cpanelSeasonTicketRecord.setIsmembermandatory(Boolean.FALSE);
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        seasonTicketService.updateSeasonTicket(updateSeasonTicketRequestDTO);
        Mockito.verify(seasonTicketDao, never()).update(recordCaptor.capture());
    }
}
